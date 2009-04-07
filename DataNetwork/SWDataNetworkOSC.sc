// this is a class to manage outgoing osc streams to other applications.
// other applications can register to the network, request info, and subscribe to slots or nodes.

SWDataNetworkOSC{

	classvar <>httppath = "/var/www/";

	var <clients;
	var <network;
	var responders;
	var <watcher;

	var <setters;

	var <>gui;

	var <>verbose = 0;

	*new{ |netw|
		^super.new.init( netw );
	}

	init{ |netw|
		network = netw;
		network.osc = this;
		clients = Array.new;
		setters = IdentityDictionary.new;
		responders = [
			OSCresponderNode( nil, '/register', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.addClient( addr );
			}),
			OSCresponderNode( nil, '/unregister', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.removeClient( addr );
			}),
			OSCresponderNode( nil, '/pong', { |t,r,msg,addr|
				var client;
				if ( verbose > 3, { msg.postln; });
				addr.port = msg[1];
				client = this.findClient( addr );
				if ( client.notNil, { client.pong } ) ;
			}),
			OSCresponderNode( nil, '/query/expected', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.expectedQuery( addr );
			}),
			OSCresponderNode( nil, '/query/nodes', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.nodeQuery( addr );
			}),
			OSCresponderNode( nil, '/query/slots', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.slotQuery( addr );
			}),
			OSCresponderNode( nil, '/query/clients', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.clientQuery( addr );
			}),
			OSCresponderNode( nil, '/query/subscriptions', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.subscriptionQuery( addr );
			}),
			OSCresponderNode( nil, '/query/setters', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.setterQuery( addr );
			}),
			OSCresponderNode( nil, '/subscribe/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.nodeSubscribe( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/subscribe/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.slotSubscribe( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/unsubscribe/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.nodeUnsubscribe( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/unsubscribe/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.slotUnsubscribe( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/set/data', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.setData( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/label/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.labelSlot( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/label/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.labelNode( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/get/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.getNode( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/get/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.getSlot( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/remove/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.removeNode( addr, msg.copyToEnd( 2 ) );
			}),
			OSCresponderNode( nil, '/add/expected', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				addr.port = msg[1]; this.addExpected( addr, msg.copyToEnd( 2 ) );
			})
		];

		responders.do{ |it| it.add };

		watcher = SkipJack.new( { this.sendPings }, 1, name: "SWDataNetworkOSC" , autostart: true );

		this.announce;

		ShutDown.add( { this.stop });
	}

	makeGui{
		^SWDataNetworkOSCGui.new( this );
	}

	announce{ |ports|
		var b,broadcastip,myip;
		var prefix;
		var cips;

		// write to http accessible file:
		("echo" + NetAddr.langPort + "> " ++ httppath +/+ "SenseWorldDataNetwork").unixCmd;

		if ( ports.isNil, {
			ports = (6000..6009) ++ (57120..57129);
		});


		NetAddr.broadcastFlag_( true );

		if ( thisProcess.platform.name == \linux, {
			prefix = "/sbin/";
		},{ prefix = "" });

		broadcastip = NetAddr.broadcastIP( prefix );
		myip = NetAddr.myIP( prefix );

		ports.do{ |it|
			NetAddr.new( broadcastip, it ).sendMsg( 
				"/datanetwork/announce", myip, NetAddr.langPort );
		};

		cips = this.restoreClientsIPs;
		if ( cips.notNil ){
			cips.do{ |jt|
				ports.do{ |it|
					NetAddr.new( jt, it ).sendMsg( 
						"/datanetwork/announce", myip, NetAddr.langPort );
				};
			};
		};

		if ( gui.notNil ){
			gui.addLogMsg( "network announced" );
		};

	}

	expectedQuery{ |addr|
		if ( network.expectedNodes.size == 0, {
			addr.sendMsg( '/warn', "There are no expected nodes in the network");
		});
		network.expectedNodes.do{ |key|
			addr.sendMsg( '/info/expected', key );
		};
	}

	nodeQuery{ |addr|
		if ( network.nodes.size == 0, {
			addr.sendMsg( '/warn', "There are no nodes in the network");
		});
		network.nodes.keysValuesDo{ |key,node|
			addr.sendMsg( '/info/node', key, node.key, node.slots.size );
		};
	}

	slotQuery{ |addr|
		if ( network.nodes.size == 0, {
			addr.sendMsg( '/warn', "There are no nodes in the network");
		});
		network.nodes.keysValuesDo{ |key,node|
			node.slots.do{ |it,i|
				addr.sendMsg( '/info/slot', key, i, it.key );
			};
		};
	}

	clientQuery{ |addr|
		if ( clients.size == 0, {
			addr.sendMsg( '/warn', "There are no clients in the network");
		});
		clients.do{ |it|
			addr.sendMsg( '/info/client', it.addr.ip, it.addr.port, it.addr.hostname );
		};
	}

	subscriptionQuery{ |addr|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.subscriptionQuery;
		});
	}

	newExpected{ |id,label|
		clients.do{ |it|
			it.newExpected( id, label );
		}
	}
	newNode{ |node|
		clients.do{ |it|
			it.newNode( node );
		}
	}

	newSlot{ |slot|
		clients.do{ |it|
			it.newSlot( slot );
		}
	}

	nodeSubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.subscribeNode( msg[0] );
			this.getNode( addr, msg );
		});

		if ( gui.notNil ){
			gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to node:"+msg[0] );
		};

	}

	slotSubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.subscribeSlot( msg[0], msg[1].asInteger );
			this.getSlot( addr, msg );
		});

		if ( gui.notNil ){
			gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to slot:"+msg.copyRange(0,1) );
		};
	}

	nodeUnsubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.unsubscribeNode( msg[0] );
		});

		if ( gui.notNil ){
			gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from node:"+msg[0] );
		};

	}

	slotUnsubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.unsubscribeSlot( msg[0], msg[1].asInteger );
		});

		if ( gui.notNil ){
			gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from slot:"+msg.copyRange(0,1) );
		};

	}

	getNode{ |addr,msg|
		var client,data;
		//[addr,msg].postln;
		client = this.findClient( addr );
		//client.postln;
		if ( client.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			if ( network.nodes.at( msg[0] ).isNil, {
				addr.sendMsg( '/warn', "Node"+msg[0]+"does not exist yet");
			},{
				data = [ '/data/node', msg[0], network.nodes.at( msg[0] ).data ].flatten;
			//			data.postln;
			addr.sendMsg( *data );
			//	addr.sendMsg( '/data/node', msg[1], *data );
			});
		});
	}

	getSlot{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			if ( network.nodes.at( msg[0] ).isNil, {
				addr.sendMsg( '/warn', "Node"+msg[0]+"does not exist yet");
			},{
				addr.sendMsg( '/data/slot', msg[0], msg[1], network.nodes.at( msg[0] ).slots.at( msg[1].asInteger ).value );
			});
		});
	}

	findClient{ |addr|
		^clients.select( { |it| it.addr == addr } ).first;
	}

	addClient{ |addr|
		var there;
		there = this.findClient( addr );
		//		there = clients.find( { |it| it.addr == addr } );
		[addr,there].postln;
		if ( there.isNil, {
			clients = clients.add( SWDataNetworkOSCClient.new( addr ); );
			watcher.start;
			if ( gui.notNil ){ 
				gui.addClient( clients.last );
				gui.addLogMsg( "client registered:"+(addr.asString.replace( "a NetAddr","")) );
			};
		},{
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is already registered. Please unregister first");
		});
	}

	removeClient{ |addr|
		var there,there2;
		there = this.findClient( addr );
		[addr,there].postln;
		if ( there.notNil, { clients.remove( there ) } );
		there2 = setters.findKeyForValue( addr );
		if ( there2.notNil, { setters.removeAt( there ) } );
		addr.sendMsg( '/unregistered', addr.port.asInteger );

		if ( gui.notNil ){
			gui.removeClient( there );
			gui.addLogMsg( "client unregistered:"+(addr.asString.replace( "a NetAddr","")) );
		};
	}

	backupClientsIPs{ |name|
		var file;
		if ( name.isNil ){
			name = "SWDataNetworOSC_clientIPs";
		}{
			name = (name++"IPS");
		};
		file = File.open( Platform.userAppSupportDir +/+ name, "w" );
		file.write( clients.collect{ |it| it.addr.addr.asIPString }.asCompileString );
		file.close;
	}

	restoreClientsIPs{ |name|
		var file,res;
		name = name ? "SWDataNetworOSC_clientIPs";
		file = File.open( Platform.userAppSupportDir +/+ name, "r" );
		if ( file.isOpen ){
			res = file.readAllString.interpret;
			file.close;
			^res;
		};
		file.close;
		^nil;
		//	file.write( clients.collect{ |it| it.addr.hostname }.asCompileString );
	}

	backupClients{ |name|
		name = name ? "SWDataNetworOSC_clients";
		this.backupClientIPs( name );
		clients.collect{ |it| [ it.addr, it.subscriptions.asArray, it.setters.collect{ |it| [it.id,it.data.size] }.asArray ] }.writeArchive( Platform.userAppSupportDir +/+ name  );
	}

	restoreClients{ |name|
		var tcl,tc;
		name = name ? "SWDataNetworOSC_clients";
		tcl = Object.readArchive(  Platform.userAppSupportDir +/+ name );
		tcl.postcs;
		tcl.do{ |it|
			this.addClient( it[0] );
			tc = this.findClient( it[0] );

			// subscriptions
			it[1].do{ |jt|
				if ( jt.isKindOf( Array ), {
					tc.subscribeSlot( *jt );
				},{
					network.addExpected( jt );
					tc.subscribeNode( jt );
				});
			};

			// setters
			it[2].do{ |jt|
				network.addExpected( jt[0] );
				network.setData( jt[0], Array.fill( jt[1], 0 ) );
				network.nodes.postcs;
				setters.put( jt[0], tc.addr );
				tc.addSetter( network.nodes.at( jt[0] ).postcs; );
			};
		};
	}

	setterQuery{ |addr|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.setterQuery;
		});
	}

	removeNode{ |addr,msg|
		var there;
		there = this.findClient( addr );
		if ( there.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first");
		},{
			if ( network.nodes.at( msg[0] ).notNil,
				{
					if ( setters.at( msg[0] ) == addr, {
						network.removeNode( msg[0] );
						addr.sendMsg( '/removed/node', msg[0] );
					},{
						addr.sendMsg( '/error', ("you are not the setter of node with id"+msg[0]+", so you cannot remove it" ) ); 
					});
				},{
					addr.sendMsg( '/error', ("node with id"+msg[0]+"is not part of the network" ) ); 
				});
		});
	}

	setData{ |addr,msg|
		var there, addsetter;
		addsetter = false;
		there = this.findClient( addr );
		if ( there.isNil, {
			addr.sendMsg( '/error', "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first");
		},{
			if ( network.nodes.at( msg[0] ).isNil,
				{
					if ( setters.at( msg[0] ).isNil, {
						setters.put( msg[0], addr );
					});
					addsetter = true;
				});
			if ( network.expectedNodes.indexOf( msg[0] ).isNil,
				{
					addr.sendMsg( '/error', ("node with id"+msg[0]+"and size"+(msg.size - 1 )+"is not expected to be part of the network" ) ); 
				}, {
					if ( setters.at( msg[0] ) == addr, {
						network.setData( msg[0], msg.copyToEnd( 1 ) );
						if ( addsetter, {
							there.addSetter( network.nodes.at( msg[0] ) );
							// only send data back to sender first time the node is set, for confirmation:
							this.getNode( addr, msg );
							if ( gui.notNil ){
								gui.addLogMsg( "client:"+(there.addr.asString.replace( "a NetAddr",""))+"became setter of node:"+msg[0] );
							};

						});
					});
				});
		});
	}

	labelSlot{ |addr,msg|
		if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
			{
				setters.put( msg[0], addr );
			});
		if ( network.expectedNodes.indexOf( msg[0] ).isNil, {
			addr.sendMsg( '/error', ("node with id"+msg[0]+"and size"+(msg.size - 1 )+"is not expected to be part of the network" ) ); }, {
				if ( setters.at( msg[0] ) == addr, {
					network.add( msg[2], [msg[0], msg[1].asInteger] );
				},{
					addr.sendMsg( '/warn', ("you are not the setter of node with id"+msg[0]++", so you cannot label it" ) );
				});
			});
	}

	labelNode{ |addr,msg|
		if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
			{
				setters.put( msg[0], addr );
			});
		if ( network.expectedNodes.indexOf( msg[0] ).isNil, {
			addr.sendMsg( '/error', ("node with id"+msg[0]+"and size"+(msg.size - 1 )+"is not expected to be part of the network" ) ); }, {
				if ( setters.at( msg[0] ) == addr, {
					network.add( msg[1], msg[0] );
				},{
					addr.sendMsg( '/warn', ("you are not the setter of node with id"+msg[0]++", so you cannot label it" ) );
				});
			});
	}

	addExpected{ |addr,msg|
		if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
			{
				setters.put( msg[0], addr );
			});
		if ( setters.at( msg[0] ) == addr, {
			network.addExpected( msg[0], msg[1], msg[2] );
		});
	}

	sendData{ |id,data|
		if ( verbose > 1, { ["sendData", id,data].postln; } );
		clients.do{ |it|
			it.sendData( id, data );
		};
	}

	nodeRemoved{ |id|
		clients.do{ |it| it.nodeRemoved( id ) };
	}
	
	sendPings{
		clients.do{ |it| it.ping };
	}

	stop{
		clients.do{ |it| 
			it.addr.sendMsg( '/unregistered', it.addr.port.asInteger );
			it.addr.sendMsg( '/datanetwork/quit');
		}
	}

}

SWDataNetworkOSCClient{
	var <addr;
	var <missedPongs = 0;
	var <subscriptions;
	var <setters;
	var <>maxMissedPongs = 60;
	var <>key;

	*new{ |addr|
		^super.new.init( addr );
	}

	init{ |address|
		addr = address;
		subscriptions = Set.new;
		setters = Set.new;
		addr.sendMsg( '/registered', addr.port.asInteger );
	}

	ping{
		addr.sendMsg( '/ping', addr.port.asInteger );
		missedPongs = missedPongs + 1;
	}

	pong{
		missedPongs = 0;
		//		missedPongs = missedPongs - 1;
	}

	setterQuery{
		if ( setters.size == 0, {
			addr.sendMsg( '/warn', "Client has no setters");
		});
		setters.do{ |it|
			addr.sendMsg( '/info/setter', it.id, it.key, it.slots.size );
		};
	}

	addSetter{ |node|
		setters.add( node );
		addr.sendMsg( '/info/setter', node.id, node.key, node.slots.size );
	}

	subscriptionQuery{
		if ( subscriptions.size == 0, {
			addr.sendMsg( '/warn', "Client has no subscriptions");
		});
		subscriptions.do{ |it|
			//			it.postln;
			if ( it.isKindOf( Array ),
				{
					addr.sendMsg( '/subscribed/slot', addr.port, it[0], it[1] );
				},{
					addr.sendMsg( '/subscribed/node', addr.port, it );
				})
		};
	}

	subscribeNode{ |id|
		subscriptions.add( id );
		addr.sendMsg( '/subscribed/node', addr.port, id );
	}

	subscribeSlot{ |id1,id2|
		subscriptions.add( [id1, id2] );
		addr.sendMsg( '/subscribed/slot', addr.port, id1, id2 );
	}

	unsubscribeNode{ |id|
		subscriptions.remove( id );
		addr.sendMsg( '/unsubscribed/node', addr.port, id );
	}

	unsubscribeSlot{ |id1,id2|
		subscriptions.remove( [id1, id2] );
		addr.sendMsg( '/unsubscribed/slot', addr.port, id1, id2 );
	}

	newExpected{ |id,label|
		addr.sendMsg( '/info/expected', id, label );
	}

	newNode{ |node|
		addr.sendMsg( '/info/node', node.id, node.key, node.slots.size );
		node.slots.do{ |it,i|
			this.newSlot( it );
		};
	}

	newSlot{ |slot|
		addr.sendMsg( '/info/slot', slot.id[0], slot.id[1], slot.key );
	}

	nodeRemoved{ |id|
		if ( subscriptions.includes( id ),{
			addr.sendMsg( '/removed/node', id );
		});
	}

	sendData{ |id,data|
		var msg;
		//		if ( verbose, { 
		//		["sendData", id,data].postln;// } );
		if ( subscriptions.includes( id ),
			{
				msg = ['/data/node', id] ++ data;
				//	"node subscribed".postln;
				//	msg.postln;
				addr.sendMsg( *msg );
			});
		data.do{ |it,i|
			if ( subscriptions.includes( [id,i] ),
				{
					//	"slot subscribed".postln;
					addr.sendMsg( '/data/slot', id, i, it );
				})
		};
	}
}