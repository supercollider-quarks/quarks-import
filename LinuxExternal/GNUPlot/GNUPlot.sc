GNUPlot { 

	// original class by Ben Moran (2006)
	// mods from John Yates (2006)
	// seriously expanded by Marije Baalman (2006-9)
	// additions by Oswald Berthold (2009)

	classvar id, <>folder = "SC_to_GNUPlot/", <>initCode = "set data style lines\n", <>gnuplotpath="gnuplot";

	var pipe,gid,<hisdata,monrout,updateFunc,<lastdata;

	// histogram:
	var <>histoMin, <>histoMax, <>histoSteps = 100;

        // This function takes an arbitrary array and plots the values
	*initClass{
		StartUp.add{
			id = 0;
			Platform.case(\osx, {
				// on OSX, common "x11" term doesn't work by default (since SC not under X11)
				initCode = initCode ++ "set term aqua\n";
				gnuplotpath = "/opt/local/bin/gnuplot";
			});
			this.folder = PathName.tmp +/+ "SC_to_GNUPlot/";
			this.makeBackupFolder;
		}
	}

	*makeBackupFolder { 
		var testfile, testname = "zzz_gnuplot_test_delete_me.txt"; 
		testfile = File(folder ++ testname, "w");
		if (testfile.isOpen.not) 
			{ unixCmd("mkdir" + folder) }
			{ testfile.close;  unixCmd("rm" + folder ++ "*.*"); }
	}

    *plot {
		|data|
		var fh, tmpname;
		tmpname = folder++"scdata"++id++".tmp"; // Todo: proper temp name!
		id = id + 1;
		// And add exception handling.
		fh = File.new(tmpname,"w");
		data.do({|val|
			fh.putString(val.asString ++ "\n");
		});
		fh.close;
		fh = Pipe.new(gnuplotpath + "-persist", "w");
		fh.putString(initCode);
		fh.putString("plot \"" ++ tmpname ++ "\" title \"\" \n");
		fh.close;
	}
	
	// Helper function to convert an envelope into a suitable array
    *envarray { |env|
		var times, step, slices;
		times = env.times;
		step = times.minItem;
		slices = Array.fill(times.sum/step,
			{|ind| env.asArray.envAt(step * ind); });
		^ slices
        }
	
	*plotenv {
		|env|
		this.plot(this.envarray(env));
		//plot(envarray(env));
	}

	*new { 
		^super.new.init;
	}
	
	init{
		gid = id;
		id = id+1;
		this.start;
		//		pipe.putString("plot \"" ++ tmpname ++ "\"\n");
	}

	start{
		pipe = Pipe.new(gnuplotpath + "-persist", "w");
		pipe.putString(this.class.initCode);
	}

	createTempFile{ |data,ns=1|
		var fh, tmpname,unlaced;
		tmpname = folder+/+"scdata"++gid++".tmp"; // Todo: proper temp name!
		//	id = id + 1;
		// And add exception handling.
		fh = File.new(tmpname,"w");
		if ( ns == 1,
			{
				data.do{|val|
					fh.putString(val.asString ++ "\n");
				};
			},
			{
				//unlaced = data.flat.unlace( ns );
				//unlaced.postln;
				ns.do{ |i| 
					data[i].do{ |val|
						fh.putString(val.asString ++ "\n");
					};
					fh.putString("\n\n");};
			});
		fh.close;
		^tmpname;
	}

	putCommand{ |command,tmpname,label=""|
		pipe.putString(command+ "\"" ++ tmpname ++ "\" title \""++label++"\"\n");
		pipe.flush;
	}

	putTestCommand{ |command|
		pipe.putString(command ++ "\n");
		pipe.flush;
	}

	plotd{ |data,ns=1,label=""|
		defer{
			pipe.putString("plot ");
			(ns-1).do{ |i|
				pipe.putString("'-' title \""++label++(i+1)++"\",");
			};
			pipe.putString("'-' title \""++label++ns++"\"\n");
			if ( ns > 1,
				{
					ns.do{ |id|
						data.at(id).do{ |it,i| pipe.putString( ""++ it ++ "\n" ); };
						pipe.putString("e\n");
					};
				},
				{
					data.do{ |it,i| pipe.putString( ""++ it ++ "\n" ); };
					pipe.putString("e\n");
				});
			pipe.flush;
		};
	}

	plot{ |data,ns=1,label="", title|
		var tmpname = this.createTempFile( data, ns );
		title !? {pipe.putString("set title %\n".format(title.quote))};
		if ( ns == 1,
			{
				pipe.putString("plot \""++tmpname++"\" title \""++label++"\"\n");
				lastdata = [ data ];
			},
			{
				pipe.putString("plot \""++tmpname++"\" index 0 title \""++label+"0\"");
				(ns-1).do{ |i|
					pipe.putString(", \""++tmpname++"\" index "++ (i+1) ++" title \""++label+(i+1)++"\""); };
				pipe.putString( "\n" );
				lastdata = data;
			});
		pipe.flush;
		//		this.putCommand( "plot", tmpname, label );
		
	}

	replot{ |data,ns=1,label=""|
		var lastns;
		if ( ns == 1,
			{
				lastdata = lastdata.add( data );
			},
			{
				ns.do{ |i| lastdata = lastdata.add( data[i] ) };
			});
		lastns = lastdata.size;
		this.plot( lastdata, lastns, label );
	}

	autoscaleX{
		pipe.putString( "set autoscale x\n" );
		pipe.flush;
	}

	autoscaleY{
		pipe.putString( "set autoscale y\n" );
		pipe.flush;
	}

	setXrange{ |min,max|
		pipe.putString( "unset autoscale x\n" );
		pipe.putString( "set xrange ["++min++":"++max++"]\n" );
		pipe.flush;
	}

	setYrange{ |min,max|
		pipe.putString( "unset autoscale y\n" );
		pipe.putString( "set yrange ["++min++":"++max++"]\n" );
		pipe.flush;
	}

	stop {
		pipe.putString( "quit\n" );
		pipe.flush;
		pipe.close;
	}

	monitorReset{
		var length = hisdata.size;
		hisdata = Array.fill( length, 0 );
	}

	monitor{ |updateF,dt,length,ns=1,skip=1| // id: id of data to monitor, dt: time step, skip: stepsize
		updateFunc = updateF;
		hisdata = Array.fill( length, 0 );
		monrout = Task{ 
			var cnt = 0;
			inf.do{ 
				hisdata.pop;
				hisdata = hisdata.addFirst( updateFunc.value );
				cnt = cnt + 1;
				if ( cnt == skip,
					{
						if ( ns > 1, {
							this.plotd( hisdata.flop, ns );
						},{
							this.plotd( hisdata.flatten, ns );
						});
						cnt = 0;
					});
				dt.wait;
			}
		};
	}

	startMonitor{
		monrout.stop.play;
	}

	stopMonitor{ 
		monrout.stop;
	}


	plotdHisto{ |data,ns=1,label="",verb=true|
		var histodata, xtics;
		var step,start,end,xticsArr;
		defer{
			if ( ns > 1, {
				histodata = data.collect{ |it| it.histo(histoSteps,histoMin,histoMax, verb) };
				xticsArr = data.first.histoBands(histoSteps,histoMin,histoMax);
				//	histodata = histodata.flop;
			},{
				histodata = data.histo(histoSteps,histoMin,histoMax, verb);
				xticsArr = data.histoBands(histoSteps,histoMin,histoMax);
				//	histodata = histodata.first;
			});


			// create the tics axis:
			step = xticsArr.size / 10;
			start = (step / 2 ).floor;
			end = xticsArr.size - start;
			
			xticsArr = xticsArr.at( ( start,(step+start)..end) );
			if ( xticsArr.last.isNil, { xticsArr = xticsArr.drop(-1); });
			
			xtics = "set xtics ( ";
			xticsArr.do{ |it,i| 
				xtics = xtics ++ "\"" ++ it.round(0.1) ++ "\"" + ( i*step+(step/2) );
				if ( i < (xticsArr.size - 1) ){ xtics = xtics ++ ", " };
			};
			xtics = xtics ++ ")";
			
			pipe.putString( xtics ++ "\n" );
			
			this.plotd( histodata, ns, label );
		};
	}

	// as Monitor, but plots a Histogram of the data
	monitorHisto{ |updateF,dt,length,ns=1,skip=1| // id: id of data to monitor, dt: time step, skip: stepsize
		updateFunc = updateF;
		hisdata = Array.fill( length, 0 );
		monrout = Task{ 
			var cnt = 0;
			var histodata;
			inf.do{ 
				hisdata.pop;
				hisdata = hisdata.addFirst( updateFunc.value );
				cnt = cnt + 1;
				if ( cnt == skip,
					{
						if ( ns > 1, {
							this.plotdHisto( hisdata.flop, ns );
						},{
							this.plotdHisto( hisdata.flatten, ns );
						});
						cnt = 0;
					});
				dt.wait;
			}
		};
	}


	// Oswalds' additions:
	plot3 {|data, label, title|
		var fh, tmpname; // = this.createTempFile3( data, ns );
		defer {
			tmpname = folder+/+"scdata"++gid++".tmp"; // Todo: proper temp name!
			// And add exception handling.
			fh = File.new(tmpname,"w");
			data.do{|sub|
				sub.do {|val|
					fh.putString(val.asString ++ " ");
				};
				fh.putString("\n");
			};
			fh.close;
			
			["GNUPlot.plot3 data size: ", data.size].postln;
			title !? {pipe.putString("set title %\n".format(title.quote))};
			pipe.putString("splot \""++tmpname++"\" with lines title \""++label++"\"\n");
			lastdata = [ data ];
			pipe.flush;
		}
	}
	
	// the data for this should be an array-of-arrays-of-arrays, eg:
	// [[p01, p02, p03], [p10, p11, p12], [p20, p21, p22]] where each "pXX" is an array of 3D co-ords.
	/*
	g = GNUPlot.new;
	// a nice deckchair:
	g.surf3([[[0,0.5,0], [ 0.5,0,0], [ 1,-0.5,0]],   [[ 0.5,1,0], [ 1,0.5,0], [ 1.5,0,0]], [[ 1,1.5,0.5], [ 1.5,1,0.5], [ 2,0.5,0.5]]])	*/
	surf3 {|data, label, hidden3d=true, pm3d=false, title|
		var fh, tmpname; // = this.createTempFile3( data, ns );
		defer {
			tmpname = folder+/+"scdata"++gid++".tmp"; // Todo: proper temp name!
			// And add exception handling.
			fh = File.new(tmpname,"w");
			data.do{	|col|
				col.do{|sub|
					sub.do {|val|
						fh.putString(val.asString ++ " ");
					};
					fh.putString("\n");
				};
				fh.putString("\n");
			};
			fh.close;
			
			["GNUPlot.plot3 data size: ", data.size].postln;
			pipe.putString("%set hidden3d\n".format(if(hidden3d, "", "un")));
			pipe.putString("%set pm3d\n".format(if(pm3d, "", "un")));
			title !? {pipe.putString("set title %\n".format(title.quote))};
			pipe.putString("set dummy u,v\n"); // This dummy tells gnuplot it's doing a surface not a curve
			pipe.putString("splot \""++tmpname++"\" with lines title \""++label++"\"\n");
			lastdata = [ data ];
			pipe.flush;
		}
	}

	scatter {|data, label, title|
		var fh, tmpname; // = this.createTempFile3( data, ns );
		defer {
			tmpname = folder+/+"scdata"++gid++".tmp"; // Todo: proper temp name!
			// And add exception handling.
			fh = File.new(tmpname,"w");
			data.do{|sub|
				sub.do {|val|
					fh.putString(val.asString ++ " ");
				};
				fh.putString("\n");
			};
			fh.close;
			
			["GNUPlot.scatter data size: ", data.size].postln;
			title !? {pipe.putString("set title %\n".format(title.quote))};
			pipe.putString(  if(data[0].size==3, "splot", "plot") 
					++ "\""++tmpname++"\" with points title \""++label++"\"\n");
			lastdata = [ data ];
			pipe.flush;     
		}
	}
	
	// http://gnuplot.info/docs/node281.html
	setView{ |...vals|
		pipe.putString("set view %\n".format(vals.join($,)).postln);
		pipe.putString("replot\n");
		pipe.flush;
	}

}
