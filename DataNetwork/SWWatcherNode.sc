SWWatcherNode{
	var <watcher;
	var <network;
	var <>id;
	var <>function;

	*new{ |id,network,func,dt=0.05|
		^super.new.init(id,network,func,dt);
	}

	init{ |ky,netw,func,dt|
		id = ky;
		network = netw;
		function = func;
		watcher = SkipJack.new(
			{ network.setData( id, function.value ) },
			dt ).stop;
	}

	start{
		watcher.start;
	}

	stop{
		watcher.stop;
	}

	node{
		^network.nodes[id];
	}

}

SWBusWatcherNode{
	var <watcher;
	var <network;
	var <>id;
	var <>bus;

	*new{ |id,network,bus,dt=0.05|
		^super.new.init(id,network,bus,dt);
	}

	init{ |ky,netw,b,dt|
		id = ky;
		bus = b;
		network = netw;
		watcher = SkipJack.new(
			{ bus.getn( bus.numChannels, { |v| network.setData( id, v ) } ) },
			dt ).stop;
		network.setData( id, Array.fill( bus.numChannels, 0 ) );
		this.node.bus_( bus );
	}

	start{
		watcher.start;
	}

	stop{
		watcher.stop;
	}

	node{
		^network.nodes[id];
	}

}

SWCombineNode{
	var <network;
	var <>id;
	var <data;

	*new{ |id,network,size|
		^super.new.init(id,network,size);
	}

	init{ |ky,netw,size|
		id = ky;
		network = netw;
		data = Array.fill( size, 0 );
	}

	set{ |index,newdata|
		data.putEach( (index..(index+newdata.size-1)), newdata );
		network.setData( id, data );
	}

	node{
		^network.nodes[id];
	}
}