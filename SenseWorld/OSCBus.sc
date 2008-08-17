OSCBus{
	var <bus;
	var <server;
	var <responder;
	var <nc;
	var <>scale=1;

	*new{ |addr, cmdName, nchan, server|
		^super.new.init(addr, cmdName, nchan, server);
	}

	init{ |addr, cmdName, nchan, s|
		server = s ? Server.local;
		nc = nchan ? 1;
		responder = OSCresponderNode.new( addr, cmdName, { |t,r,msg|
			bus.setn( msg.copyRange( 1, nc ) * scale );
		});
		this.renew;
	}

	renew{
		bus = Bus.control( server, nc );
		responder.add;
	}
	
	free{
		bus.free;
		responder.remove;
	}

}

DataBus{
	var <bus;
	var <server;
	var <func;
	var <nc;
	var <updater;
	var <>dT = 0.05;
	var <>scale=1;

	*new{ |function, nchan, server|
		^super.new.init(function, nchan, server);
	}

	init{ |function, nchan, s|
		server = s ? Server.local;
		nc = nchan ? 1;
		func = function;
		this.renew;
	}

	renew{
		bus = Bus.control( server, nc );
		updater = SkipJack( { bus.setn( func.value * scale ) }, dT );
		updater.start;
	}
	
	free{
		bus.free;
		updater.stop;
	}

}