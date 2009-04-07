// reads a data network log and plays it

SWDataNetworkLog{
	//	var <>recTask;

	var <network;

	var logfile;
	var reader;
	var playnodes;
	var playTask;

	*new{ |fn,network|
		^super.new.init( fn,network );
	}

	init{ |fn,netw|
		network = netw ? SWDataNetwork.new;
		logfile =  File(fn, "r");
		reader = TabFileReader.new( logfile );
		this.readHeader;
		playTask = Task{
			var dt = 0;
			while( { dt.notNil }, {
				dt = this.readLine;
				dt.wait;
			});
		};
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

	readLine{
		var dt,line,data;
		line = reader.next.drop(-1).collect{ |it| it.interpret };
		dt = line.first;
		data = line.drop( 1 );
		playnodes.keysValuesDo{ |key,it|
			network.setData( key, data.at( it ) );
		};
		^dt;
	}

	play{
		playTask.play;
	}

	stop{
		playTask.stop;
	}

	reset{
		reader.reset;
		this.readHeader;
		playTask.reset;
	}

	close{
		reader.close;
		logfile.close;
	}

}