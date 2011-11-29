
// minimal class holding info on a minibee
SWMiniBeeOSC{
	var <>id, <>inputs, <>outputs, <>configID, <>serialNumber, <>status;

	*new{ arg ...args;
		^super.newCopyArgs( *args );
	}
}

// a hive client is like an OSC client, but with some extra methods:
SWDataNetworkOSCHiveClient : SWDataNetworkOSCClient {

	var <>nodeRange;
	var <activeBees;
	var <configs;
	var <configQueries;

	//	var <hookSet;

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
		configs = IdentityDictionary.new;
		configQueries = Set.new;
		//		hookSet = SWHookSet.new;
	}

	sendRegistered{
		addr.sendMsg( '/registered/hive', addr.port.asInteger, key.asString, nodeRange[0], nodeRange[1] );
	}

	sendHiveInfo{ |address|
		address.sendMsg( '/info/hive', addr.ip, addr.port.asInteger, key.asString, nodeRange[0], nodeRange[1] );
	}

	queryConfigInfo{ |address|
		("query config info" + address ).postln;
		// add the address to interessees of config info
		configQueries.add( address );
		// send a query to the hive client for configurations
		addr.sendMsg( '/query/configurations' );
	}

	sendConfigInfo{ |config|
		configs.put( config[0], config );
		configQueries.do{ |address|
			address.sendMsg( *(['/info/configuration'] ++ config ++ [ addr.ip, addr.port.asInteger, key.asString] ) );			
		}
		// how do I keep track of which clients already received the configurations?
	}

	
	configureBee{ |beeid,cid|
		//		if ( activeBees.at( beeid ).notNil ){
		addr.sendMsg( '/configure/minibee', beeid, cid );
		//		};
	}

	createConfig{ |id,config|
		addr.sendMsg( *( ['/minihive/configuration/create', id ] ++ config ) );
	}

	deleteConfig{ |id|
		addr.sendMsg( '/minihive/configuration/delete', id );
	}

	saveConfig{ |filename|
		addr.sendMsg( '/minihive/configuration/save', filename );
	}

	loadConfig{ |filename|
		addr.sendMsg( '/minihive/configuration/load', filename );
	}

	addBee{ |id,inputs,outputs,config,serial|
		if ( nodeRange[0] <= id and: ( nodeRange[1] >= id ) ){
			activeBees.put( id, SWMiniBeeOSC.new( id, inputs, outputs, config, serial ) );
			^true;
		}{
			"Bee ID not within range of HiveClient".warn;
			^false;
		}
	}

	statusBee{ |id, status|
		if ( activeBees.at( id ).isNil ){
			^false;
		};
		activeBees.at( id ).status_( status );
		^true;
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


	mapHiveAllOutput{ |nodeid|
		addr.sendMsg( '/map/minihive/output', nodeid );
		// hive client should subscribe to nodeid, and map it to the output of the broadcast bee
	}

	mapHiveAllCustom{ |nodeid|
		addr.sendMsg( '/map/minihive/custom', nodeid );
		// hive client should subscribe to nodeid, and map it to the output of the broadcast bee
	}

	unmapHiveAllOutput{ |nodeid|
		addr.sendMsg( '/unmap/minihive/output', nodeid );
		// hive client should subscribe to nodeid, and map it to the output of the broadcast bee
	}

	unmapHiveAllCustom{ |nodeid|
		addr.sendMsg( '/unmap/minihive/custom', nodeid );
		// hive client should subscribe to nodeid, and map it to the output of the broadcast bee
	}

}
