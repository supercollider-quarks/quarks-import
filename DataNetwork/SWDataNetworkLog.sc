// reads a data network log and plays it

SWDataNetworkLog{
	//	var <>recTask;

	var <network;

	var <reader;

	var playnodes;
	var playTask;

	var <timeMap;
	var <curTime;
	var <deltaT=0;

	*new{ |fn,network|
		^super.new.init( fn,network );
	}

	init{ |fn,netw|
		network = netw ? SWDataNetwork.new;
		this.open( fn );
	}

	open{ |fn|
		if ( playTask.notNil ){ playTask.stop; };
		if ( reader.notNil ){ reader.close; };

		reader = TabFilePlayer.new( fn );

		this.readHeader;

		playTask = Task{
			var dt = 0;
			while( { dt.notNil }, {
				dt = this.readLine;
				dt.wait;
			});
		};
		// timeMap maps the time elapsed to the line number in the file
		//		timeMap = Order.new;
		//		timeMap.put( 0, 1 );

	}

	goToTime{ |newtime|
		var line;
		if ( deltaT == 0 ){
			deltaT = this.readLine;
		};
		line = floor( newtime / deltaT );
		curTime = line * deltaT;
		// assuming dt is constant.
		// header is line 1
		reader.goToLine( line.asInteger + 1 )
	}

	play{
		playTask.start;
	}

	pause{
		playTask.pause;
	}

	resume{
		playTask.resume;
	}

	stop{
		playTask.stop;
		this.reset;
	}

	reset{
		curTime = 0;
		reader.reset;
		this.readHeader;
		playTask.reset;
	}

	close{
		playTask.stop;
		reader.close;
	}


	readHeader{
		var spec,playset,playids;
		var playslots;
		spec = reader.next.first;
		if ( spec != "nil", { 
			network.setSpec( spec );
		});
		playnodes = Dictionary.new;
		playslots = reader.next.drop(1).drop( -1 ).collect{ |it| it.interpret };
		playset = Set.new;
		playids = playslots.collect{ |it| it.first }.do{ |it,i| playset.add( it ); } ;
		playset.do{ |it| network.addExpected( it ); playnodes.put( it, Array.new ) };
		playids.do{ |it,i| playnodes.put( it, playnodes[it].add( i ) ) };
	}

	readLine{ |update=true|
		var dt,line,data;
		line = reader.next.drop(-1).collect{ |it| it.interpret };
		dt = line.first;
		if ( update ){
			data = line.drop( 1 );
			playnodes.keysValuesDo{ |key,it|
				network.setData( key, data.at( it ) );
			};
		};
		if( dt.notNil ){ 
			deltaT = dt;
			curTime = curTime + dt;
			//	timeMap.put( curTime, reader.currentLine );
		};
		^dt;
	}

}