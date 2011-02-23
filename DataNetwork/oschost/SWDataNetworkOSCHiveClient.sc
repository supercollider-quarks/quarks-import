
// minimal class holding info on a minibee
SWMiniBeeOSC{
	var <>id, <>inputs, <>outputs;

	*new{ arg ...args;
		^super.newCopyArgs( *args );
	}
}

// a hive client is like an OSC client, but with some extra methods:
SWDataNetworkOSCHiveClient : SWDataNetworkOSCClient {

	var <>nodeRange;
	var <activeBees;

	*new{ |addr,name,minNode,maxNode|
		^super.new( addr, name ).myInit( minNode, maxNode );
	}

	*newFrom{ |client,minNode,maxNode|
		^this.new(client.addr, client.key, minNode, maxNode ).initFrom( client );	
	}

	setRange{ |min,max|
		nodeRange = [min, max];
	}

	initFrom{ |client|
		active = client.active;
		subscriptions = client.subscriptions;
		setters = client.setters;
		nodeSubs = client.nodeSubs;
		slotSubs = client.slotSubs;
		slotNodesSubs = client.slotNodesSubs;
	}

	myInit{ |minNode,maxNode|
		nodeRange = [minNode, maxNode];
		activeBees = IdentityDictionary.new;
	}

	sendRegistered{
		addr.sendMsg( '/registered/hive', addr.port.asInteger, key.asString, nodeRange[0], nodeRange[1] );
	}

	sendHiveInfo{ |address|
		address.sendMsg( '/info/hive', addr.ip, addr.port.asInteger, key.asString, nodeRange[0], nodeRange[1] );
	}

	addBee{ |id,inputs,outputs|
		if ( nodeRange[0] <= id and: ( nodeRange[1] >= id ) ){
			activeBees.put( id, SWMiniBeeOSC.new( id, inputs, outputs ) );
			^true;
		}{
			"Bee ID not within range of HiveClient".warn;
			^false;
		}
	}

	// currently we have no message to remove a bee from the network; maybe needed?
	removeBee{ |id|
		activeBees.removeAt( id );
	}

	mapHiveOutput{ |nodeid, beeid|
		if ( activeBees.at( beeid ).notNil ){
			addr.sendMsg( '/map/minibee/output', nodeid, beeid );
		};
		// hive client should subscribe to nodeid, and map it to the output of the beeid
	}

	mapHiveCustom{ |nodeid, beeid|
		if ( activeBees.at( beeid ).notNil ){
			addr.sendMsg( '/map/minibee/custom', nodeid, beeid );
		};
		// hive client should subscribe to nodeid, and map it to the custom output of the beeid
	}

	unmapHiveOutput{ |nodeid, beeid|
		if ( activeBees.at( beeid ).notNil ){
			addr.sendMsg( '/unmap/minibee/output', nodeid, beeid );
		};
		// hive client should unsubscribe from nodeid, and unmap it from the output of the beeid
	}

	unmapHiveCustom{ |nodeid, beeid|
		if ( activeBees.at( beeid ).notNil ){
			addr.sendMsg( '/unmap/minibee/custom', nodeid, beeid );
		};
		// hive client should unsubscribe from nodeid, and unmap it from the custom output of the beeid
	}

}
