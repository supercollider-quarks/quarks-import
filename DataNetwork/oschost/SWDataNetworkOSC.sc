// this is a class to manage outgoing osc streams to other applications.
// other applications can register to the network, request info, and subscribe to slots or nodes.

SWDataNetworkOSC{

	classvar <>httppath;

	var <>verbose = 0;

	var <>maxMissedPongs = 10;


	var <clientDictionary;

	var <clients;
	var <setters;
	var <network;
	var <watcher;

	var <clientPorts;
	var responders;


	var <>gui;

	var <logfile;
	var <logging = false;

	var myhost;

	*initClass{
		Platform.case(
			\osx, { this.httppath = "/Library/WebServer/Documents/"; },
			\linux, { this.httppath = "/var/www/" },
			\windows, { "Please set SWDataNetworkOSC.httppath to an appropriate path!".warn }
		);
	}

	*new{ |netw|
		^super.new.init( netw );
	}

	init{ |netw|
		network = netw;
		network.osc = this;
		clientDictionary = IdentityDictionary.new;
		clients = Array.new;
		clientPorts = List.new;
		setters = IdentityDictionary.new;

		this.createResponders;

		watcher = SkipJack.new( { 
			this.sendPings;
		}, 1, name: "SWDataNetworkOSC" , autostart: true );

		this.announce;

		ShutDown.add( { this.stop });
	}

	makeGui{
		^SWDataNetworkOSCGui.new( this );
	}

	stop{
		clients.do{ |it| 
			it.addr.sendMsg( '/unregistered', it.addr.port.asInteger, it.key.asString );
			it.addr.sendMsg( '/datanetwork/quit', myhost.hostname, myhost.port.asInteger );
		};
		this.logMsg("datanetwork stopped" );
		this.removeResponders;
	}

	sendPings{
		clients.do{ |it| it.ping };
		clients.do{ |it| if ( it.missedPongs > maxMissedPongs ){ this.removeClient( it.addr, it.key ) } };
	}

	removeResponders{
		responders.do{ |it| it.remove };
	}

	createResponders{
		responders = [
			/// REGISTRATION
			OSCresponderNode( nil, '/register', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.addClient( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unregister', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.removeClient( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),

			OSCresponderNode( nil, '/pong', { |t,r,msg,addr|
				var client;
				if ( verbose > 3, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1];
					client = this.findClient( addr, msg[2] );
					if ( client.notNil, { client.pong } ) ;
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),

			/// QUERIES

			OSCresponderNode( nil, '/query/all', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.allQuery( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/expected', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.expectedQuery( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/nodes', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.nodeQuery( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/slots', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.slotQuery( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/clients', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.clientQuery( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/subscriptions', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.subscriptionQuery( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/setters', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.setterQuery( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),

			/// SUBSCRIPTIONS

			OSCresponderNode( nil, '/subscribe/all', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.allNodeSubscribe( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unsubscribe/all', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.allNodeUnsubscribe( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/subscribe/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.nodeSubscribe( addr, msg[2],  msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unsubscribe/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.nodeUnsubscribe( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/subscribe/slot', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.slotSubscribe( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unsubscribe/slot', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.slotUnsubscribe( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),

			// SETTING, LABELING

			OSCresponderNode( nil, '/add/expected', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.addExpected( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/set/data', { |t,r,msg,addr|
				if ( verbose > 2, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.setData( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/label/slot', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.labelSlot( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/label/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.labelNode( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),

			/// GETTING
			OSCresponderNode( nil, '/get/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.getNode( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/get/slot', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.getSlot( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),

			/// REMOVING

			OSCresponderNode( nil, '/remove/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.removeNode( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/remove/all', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.removeAll( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),

			/// MAPPING TO MINIBEE OUTPUT

			OSCresponderNode( nil, '/query/minibees', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.minibeeQuery( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),

			OSCresponderNode( nil, '/map/minibee/output', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.mapHiveOutput( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/map/minibee/custom', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.mapHiveCustom( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/map/minibee/pwm', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.mapHivePWM( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/map/minibee/digital', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.mapHiveDig( addr, msg[2], msg.copyToEnd( 3 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			})

		];

		responders.do{ |it| it.add };
	}


	// ---------- autoconnection and recovery support -------

	announce{ |ports|
		var b,broadcastip;
		var prefix;
		var cips;
		var file;

		// write the file:
		file = File.open( httppath +/+ "SenseWorldDataNetwork", "w");
		if ( file.isOpen.not ){
			"Could not write file with port information, clients may not be able to find the host. See [SW_Apache_setup] for instructions to make this work properly".warn;
			this.logMsg("WARNING: could not write apache file");
		}{ // file is open:
			file.write( NetAddr.langPort.asString);
			file.close;
		};

		if ( ports.isNil, {
			ports = (6000..6009) ++ (57120..57129);
		});

		ports = ports ++ clientPorts;

		NetAddr.broadcastFlag_( true );

		if ( thisProcess.platform.name == \linux, {
			prefix = "/sbin/";
		},{ prefix = "" });

		broadcastip = NetAddr.broadcastIP( prefix );
		myhost = NetAddr.atMyIP( NetAddr.langPort, prefix );

		ports.do{ |it|
			NetAddr.new( broadcastip, it ).sendMsg( 
				"/datanetwork/announce", myhost.hostname, myhost.port.asInteger );
		};

		cips = this.restoreClientsIPs;
		if ( cips.notNil ){
			cips.do{ |jt|
				ports.do{ |it|
					NetAddr.new( jt, it ).sendMsg( 
						"/datanetwork/announce", myhost.hostname, myhost.port.asInteger );
				};
			};
		};
		
		// localhost
		ports.do{ |it|
			NetAddr.new( "127.0.0.1", it ).sendMsg( 
				"/datanetwork/announce", myhost.hostname, myhost.port.asInteger );
			};

		this.logMsg( "network announced" );
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
		this.backupClientsIPs( name );
		clients.collect{ |it| 
			[ it.addr, it.subscriptions.asArray,
				it.setters.collect{ |it| [it.id,it.data.size] }.asArray
			] }.writeArchive( Platform.userAppSupportDir +/+ name  );
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
				setters.put( jt[0], tc.key );
				tc.addSetter( network.nodes.at( jt[0] ).postcs; );
			};
		};
	}

	// -------- 
	

	//------- Methods called by the network

	newExpected{ |id,label|
		clients.do{ |it|
			it.newExpected( id, label );
		}
	}
	newNode{ |node|
		//	node.postcs;
		clients.do{ |it|
			it.newNode( node );
		}
	}

	newSlot{ |slot|
		clients.do{ |it|
			it.newSlot( slot );
		}
	}

	newBee{ |bee|
		clients.do{ |it|
			it.newBee( bee );
		}
	}

	sendData{ |id,data|
		if ( verbose > 2, { ["sendData", id,data].postln; } );
		clients.do{ |it|
			it.sendData( id, data );
		};
	}

	sendDataNode{ |node|
		if ( verbose > 2, { ["sendDataNode", node.id, node.data].postln; } );
		clients.do{ |it|
			it.sendDataNode( node );
		};
	}

	nodeRemoved{ |id|
		clients.do{ |it| it.nodeRemoved( id ) };
	}

	///--------- subscriptions and data retrieval

	allNodeSubscribe{ |addr,name|
		var client;
		client = this.findClient( addr, name.asSymbol );
		if ( client.isNil, {
			this.errorMsg( addr, "/subscribe/all", 15, [name] );
		},{
			network.nodes.do{ |it| 
				client.subscribeNode( it.id );
				this.getNode( addr, client.key, [it.id] );
				this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to node:"+it.id );
			};
			this.logMsg( "/subscribe/all from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	nodeSubscribe{ |addr,name,msg|
		var client;
		client = this.findClient( addr, name.asSymbol );
		if ( client.isNil, {
			this.errorMsg( addr, "/subscribe/node", 15, [name] );
		},{
			client.subscribeNode( msg[0].asInteger );
			this.getNode( addr, client.key,  msg );
			this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to node:"+msg[0] );
		});
	}

	slotSubscribe{ |addr,name,msg|
		var client;
		client = this.findClient( addr, name.asSymbol );
		if ( client.isNil, {
			this.errorMsg( addr, "/subscribe/slot", 15, [name] );
		},{
			client.subscribeSlot( msg[0].asInteger, msg[1].asInteger );
			this.getSlot( addr, client.key, msg );
			this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to slot:"+msg.copyRange(0,1) );
		});
	}


	allNodeUnsubscribe{ |addr,name|
		var client;
		client = this.findClient( addr, name.asSymbol );
		if ( client.isNil, {
			this.errorMsg( addr, "/unsubscribe/all", 15, [name]);
		},{
			client.unsubscribeAll;
			this.logMsg( "/unsubscribe/all from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	nodeUnsubscribe{ |addr,name,msg|
		var client;
		client = this.findClient( addr, name.asSymbol );
		if ( client.isNil, {
			this.errorMsg( addr, "/unsubscribe/node", 15, [name] );
		},{
			client.unsubscribeNode( msg[0].asInteger );
			this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from node:"+msg[0] );
		});
	}

	slotUnsubscribe{ |addr,name,msg|
		var client;
		client = this.findClient( addr, name.asSymbol );
		if ( client.isNil, {
			this.errorMsg( addr, "/unsubscribe/slot", 15, name );
		},{
			client.unsubscribeSlot( msg[0].asInteger, msg[1].asInteger );
			this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from slot:"+msg.copyRange(0,1) );
		});
	}


	getNode{ |addr,name,msg|
		var client,data,node;
		//[addr,msg].postln;
		client = this.findClient( addr, name.asSymbol );
		//client.postln;
		if ( client.isNil, {
			this.errorMsg( addr, "/get/node", 15, [name] );
		},{
			msg[0] = msg[0].asInteger;
			node = network.nodes.at( msg[0] );
			if ( node.isNil, {
				this.warnMsg( addr, "/get/node", 5, msg );
			},{
				data = [ '/data/node', msg[0], node.data ].flatten;
				addr.sendMsg( *data );
			});
			this.logMsg( "/get/node:" + msg[0] + "from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	getSlot{ |addr,name,msg|
		var client,node;
		client = this.findClient( addr, name.asSymbol );
		if ( client.isNil, {
			this.errorMsg( addr, "/get/slot", 15, [name]);
		},{
			msg[0] = msg[0].asInteger;
			node = network.nodes.at( msg[0] );
			if ( node.isNil, {
				this.warnMsg( addr, "/get/slot", 5, msg );
			},{
				addr.sendMsg( '/data/slot', msg[0], msg[1], node.slots.at( msg[1].asInteger ).value );
			});
			this.logMsg( "/get/slot:" + msg.copyRange(0,1) + " from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	// --------- client management -----------

	findClient{ |addr,name|
		if ( name.notNil ){
			^clients.select( { |it| (it.addr == addr) and: (it.key == name.asSymbol) } ).first;
		}{ // don't check for name
			^clients.select( { |it| it.addr == addr } ).first;
		}
	}

	welcomeClientBack{ |client|
		//	("welcoming client back" + client.addr + client.key ).postln;
		client.welcomeBack;
		client.nodeSubs.do{ |it|
			if ( network.nodes[it].notNil){
				client.newNode( network.nodes[it] );
			}
		};
		/*
		client.setters.do{ |it|
			// "it" is an instance of SWDataNode
			//		("setter"+it.id + client.addr).postln;
			setters.put( it.id, client.key );
		};
		*/
	}

	addClient{ |addr,name|
		var there,newclient;
		there = this.findClient( addr );
		//		there = clients.find( { |it| it.addr == addr } );
		//	[addr,there,name].postln;

		if ( there.isNil, { 
			// no client had that IP and port before
			// see if the client exists by name in the library
			there = clientDictionary.at( name.asSymbol );
			if ( there.notNil){
				// address may have changed, so we reset it:
				there.addr = addr;
				clients = clients.add( there );
				this.welcomeClientBack( there );
				if ( gui.notNil ){ 
					gui.addClient( there );
				};
				this.logMsg( "client reregistered:"+(addr.asString.replace( "a NetAddr",""))+name );

			}{
				if ( addr.port > 0){
					//		"new client".postln;
					clientPorts.add( addr.port );
					newclient = SWDataNetworkOSCClient.new( addr, name.asSymbol );
					clients = clients.add( newclient );
					//	watcher.start;
					clientDictionary.put( name.asSymbol, newclient );
					if ( gui.notNil ){ 
						gui.addClient( newclient );
					};
					this.logMsg( "client registered:"+(addr.asString.replace( "a NetAddr",""))+name );
				};
			};
		},{
			//	"client had same ip and port, welcome back".postln;
			there = clientDictionary.at( name.asSymbol );
			//	there.postln;
			if ( there.notNil ){
				there.addr = addr;
				this.welcomeClientBack( there );				
				this.logMsg( "client reregistered:"+(addr.asString.replace( "a NetAddr",""))+name );				
			}{
				this.errorMsg( addr, "/register", 2);
			};
		});
	}

	removeClient{ |addr,name|
		var there,there2;
		there = this.findClient( addr );
		//		[addr,there].postln;

		if ( there.notNil, { 
			if ( there.key == name.asSymbol ){
				/// removed, as we keep the client in the dictionary
				//	there.setters.do{ |node| setters.removeAt( node.id ) };
				there.active = false;
				clients.remove( there );
				addr.sendMsg( '/unregistered', addr.port.asInteger, there.key );
				if ( gui.notNil ){
					gui.removeClient( there );
				};
				this.logMsg( "client unregistered:"+(addr.asString.replace( "a NetAddr","")) );
			}{
				this.errorMsg( addr, "/unregister", 14, [name] );
				this.logMsg( "client tried to unregister:"+(addr.asString.replace( "a NetAddr",""))+"but was not succesful" );

			};
		},{
			this.errorMsg( addr, "/unregister", 3 );
				this.logMsg( "client tried to unregister:"+(addr.asString.replace( "a NetAddr",""))+"but was not succesful" );
		} );

		//		there2 = setters.findKeyForValue( addr );
		//		if ( there2.notNil, { setters.removeAt( there2 ) } );
	}

	//------- Queries -------

	allQuery{ |addr,name|
		this.logMsg( "/query/all from client with IP"+addr.ip+"and port"+addr.port );
		this.expectedQuery( addr, name );
		this.nodeQuery( addr, name );
		this.slotQuery( addr, name );
		this.clientQuery( addr, name );
		this.subscriptionQuery( addr, name );
		this.setterQuery( addr, name );
		this.minibeeQuery( addr, name );
	}

	expectedQuery{ |addr,name|
		var client;
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/query/expected", 15, [name] );
		},{
			if ( network.expectedNodes.size == 0, {
				this.warnMsg( addr, "/query/expected", 7 );
			});
			network.expectedNodes.do{ |key|
				addr.sendMsg( '/info/expected', key );
			};
		});
		this.logMsg( "/query/expected from client with IP"+addr.ip+"and port"+addr.port );
	}

	nodeQuery{ |addr,name|
		var client;
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/query/nodes", 15, [name] );
		},{
			if ( network.nodes.size == 0, {
				this.warnMsg( addr, "/query/nodes", 8 );
			});
			network.nodes.keysValuesDo{ |key,node|
				addr.sendMsg( '/info/node', key, node.key.asString, node.slots.size, node.type );
			};
		});
		this.logMsg( "/query/nodes from client with IP"+addr.ip+"and port"+addr.port );
	}

	slotQuery{ |addr,name|
		var client;
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/query/slots", 15, [name] );
		},{
			if ( network.nodes.size == 0, {
				this.warnMsg( addr, "/query/slots", 8 );
			});
			network.nodes.keysValuesDo{ |key,node|
				node.slots.do{ |it,i|
					addr.sendMsg( '/info/slot', key, i, it.key.asString, it.type );
				};
			};
		});
		this.logMsg( "/query/slots from client with IP"+addr.ip+"and port"+addr.port );
	}

	clientQuery{ |addr,name|
		var client;
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/query/clients", 15, [name] );
		},{
			if ( clients.size == 0, {
				this.warnMsg( addr, "/query/clients", 9 );
			});
			clients.do{ |it|
				addr.sendMsg( '/info/client', it.addr.ip, it.addr.port, it.key );
			};
		});
		this.logMsg( "/query/clients from client with IP"+addr.ip+"and port"+addr.port );
	}

	subscriptionQuery{ |addr,name|
		var client;
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/query/subscriptions", 1 );
		},{
			if( client.subscriptionQuery ){
				this.logMsg( "/query/subscriptions from client with IP"+addr.ip+"and port"+addr.port );
			}{
				this.warnMsg( addr, "/query/subscriptions", 11 );
			};

		});
		this.logMsg( "/query/subscriptions from client with IP"+addr.ip+"and port"+addr.port );
	}

	setterQuery{ |addr,name|
		var client;
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/query/setters", 1);
		},{
			if( client.setterQuery ){
				this.logMsg( "/query/setters from client with IP"+addr.ip+"and port"+addr.port );
			}{
				this.warnMsg( addr, "/query/setters", 10 );
			};
		});
	}

	// -----------------------

	/// -------- node control by clients --------

	setData{ |addr,name,msg|
		var there, addsetter, node, code;
		addsetter = false;
		there = this.findClient( addr, name.asSymbol );
		if ( there.isNil, {
			this.errorMsg( addr, "/set/data", 15, [name] );
		},{
			// node id:
			msg[0] = msg[0].asInteger;
			node = network.nodes.at( msg[0] );
			//	node.postln;
			if ( node.isNil,
				{ // it's a new node for the network:
					if ( setters.at( msg[0] ).isNil, {
						// and there was no setter yet, so client becomes the setter
						setters.put( msg[0], name.asSymbol );
					});
					// if the client is the setter:
					if ( setters.at( msg[0] ) == name.asSymbol ){
						if ( network.expectedNodes.indexOf( msg[0] ).isNil,
							{
								this.errorMsg( addr, "/set/data", 6, msg );
							}, {
								network.setData( msg[0], msg.copyToEnd( 1 ) );
								// .asFloat ); // no conversion, since it can be a string node now.
								// has to look up the newly created node:
								there.addSetter( network.nodes.at( msg[0] ) );
								// only send data back to sender first time the node is set, for confirmation:
								this.getNode( addr, there.key, msg );
								this.logMsg( "client:"+(there.addr.asString.replace( "a NetAddr",""))+"became setter of node:"+msg[0] );
							});
					}{
						// but someone else claimed it already!
						// client isn't the setter
						this.errorMsg( addr, "/set/data", 4, msg );
					};
				},{
					// it's an already existing node (less checks, more efficient!)
					if ( there.checkForSetter( node ) ){
						// client is the setter:
						code = network.setData( msg[0], msg.copyToEnd( 1 ) );
						//.asFloat ); // can be a string node now.
						if ( code != 0 ){ // error occured:
							if ( code == 1 ){
								this.errorMsg( addr, "/set/data", 6, msg );
							};
							if ( code == 2 ){
								this.errorMsg( addr, "/set/data", 12, msg );
							};
						};
					}{
						// client isn't the setter
						this.errorMsg( addr, "/set/data", 4, msg );
					};
				});
		});
	}

	removeAll{ |addr,name|
		var there;
		there = this.findClient( addr, name.asSymbol );
		if ( there.isNil, {
			this.errorMsg( addr, "/remove/all", 15, [name]);
		},{
			there.setters.do{ |nd|
				if ( network.nodes.at( nd.id ).notNil,
					{
						network.removeNode( nd.id );
						addr.sendMsg( '/removed/node', nd.id );
					},{
						this.errorMsg( addr, "/remove/all", 5, [nd.id]);
				});
			};
			this.logMsg( "/remove/all: from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	removeNode{ |addr,name,msg|
		var there,node;
		there = this.findClient( addr, name.asSymbol );
		if ( there.isNil, {
			this.errorMsg( addr, "/remove/node", 15, [name] );
		},{
			msg[0] = msg[0].asInteger;
			node = network.nodes.at( msg[0] );
			if ( node.notNil,
				{
					if ( there.checkForSetter(node), {
						// old version:
						//	if ( setters.at( msg[0] ) == addr, {
						network.removeNode( msg[0] );
						addr.sendMsg( '/removed/node', msg[0] );
					},{
						this.errorMsg( addr, "/remove/node", 4, msg );
					});
				},{
					this.errorMsg( addr, "/remove/node", 5, msg );
				});
			this.logMsg( "/remove/node:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
		});
	}



	minibeeQuery{ |addr,name|
		var client;
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/query/minibees", 15, [name] );
		},{
			network.getBeeInfo( addr );
		});
		this.logMsg( "/query/minibees from client with IP"+addr.ip+"and port"+addr.port );
	}

	sendBeeNoInfo{ |addr|
		this.warnMsg( addr, "/query/minibees", 8 );
	}

	sendBeeInfo{ |addr, id,insize,outsize|
		addr.sendMsg( '/info/minibee', id, insize, outsize );
	}

	mapHiveOutput{ |addr,name,msg|
		var there,node;
		there = this.findClient( addr, name.asSymbol );
		if ( there.isNil, {
			this.errorMsg( addr, "/map/minibee/output", 15, [name] );
		},{
			msg[0] = msg[0].asInteger;
			node = network.nodes.at( msg[0] );
			if ( node.notNil,
				{
					if ( there.checkForSetter(node), {
						// old version:
						//	if ( setters.at( msg[0] ) == addr, {
						network.mapHiveOutput( msg[0], msg[1] );
						addr.sendMsg( '/mapped/minibee/output', msg[0], msg[1] );
					},{
						this.errorMsg( addr, "/map/minibee/output", 4, msg );
					});
				},{
					this.errorMsg( addr, "/map/minibee/output", 5, msg );
				});
			this.logMsg( "/map/minibee/output:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	mapHiveCustom{ |addr,name,msg|
		var there,node;
		there = this.findClient( addr, name.asSymbol );
		if ( there.isNil, {
			this.errorMsg( addr, "/map/minibee/custom", 15, [name] );
		},{
			msg[0] = msg[0].asInteger;
			node = network.nodes.at( msg[0] );
			if ( node.notNil,
				{
					if ( there.checkForSetter(node), {
						// old version:
						//	if ( setters.at( msg[0] ) == addr, {
						network.mapHiveCustom( msg[0], msg[1] );
						addr.sendMsg( '/mapped/minibee/custom', msg[0], msg[1] );
					},{
						this.errorMsg( addr, "/map/minibee/custom", 4, msg );
					});
				},{
					this.errorMsg( addr, "/map/minibee/custom", 5, msg );
				});
			this.logMsg( "/map/minibee/custom:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	mapHivePWM{ |addr,name,msg|
		var there,node;
		there = this.findClient( addr, name.asSymbol );
		if ( there.isNil, {
			this.errorMsg( addr, "/map/minibee/pwm", 15, [name] );
		},{
			msg[0] = msg[0].asInteger;
			node = network.nodes.at( msg[0] );
			if ( node.notNil,
				{
					if ( there.checkForSetter(node), {
						// old version:
						//	if ( setters.at( msg[0] ) == addr, {
						network.mapHivePWM( msg[0], msg[1] );
						addr.sendMsg( '/mapped/minibee', msg[0], msg[1] );
					},{
						this.errorMsg( addr, "/map/minibee/pwm", 4, msg );
					});
				},{
					this.errorMsg( addr, "/map/minibee/pwm", 5, msg );
				});
			this.logMsg( "/map/minibee/pwm:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	mapHiveDig{ |addr,name,msg|
		var there,node;
		there = this.findClient( addr, name.asSymbol );
		if ( there.isNil, {
			this.errorMsg( addr, "/map/minibee/digital", 15, [name] );
		},{
			msg[0] = msg[0].asInteger;
			node = network.nodes.at( msg[0] );
			if ( node.notNil,
				{
					if ( there.checkForSetter(node), {
						// old version:
						//	if ( setters.at( msg[0] ) == addr, {
						network.mapHiveDig( msg[0], msg[1] );
						addr.sendMsg( '/mapped/minibee', msg[0], msg[1] );
					},{
						this.errorMsg( addr, "/map/minibee/digital", 4, msg );
					});
				},{
					this.errorMsg( addr, "/map/minibee/digital", 5, msg );
				});
			this.logMsg( "/map/minibee/digital:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
		});
	}


	/// TODO: why am I using a different tactic here??? Fix check for name!

	labelNode{ |addr,name,msg|
		var client;
		// a client should be registered before being able to add expected nodes
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/label/node", 15, [name] );
		},{
			msg[0] = msg[0].asInteger;
			if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
				{
					setters.put( msg[0], name.asSymbol );
				});
			if ( network.expectedNodes.indexOf( msg[0] ).isNil, {
				this.warnMsg( addr, "/label/node", 6, msg );
				if ( setters.at( msg[0] ) == name.asSymbol, {
					network.add( msg[1], msg[0] );
				},{
					this.warnMsg( addr, "/label/node", 4, msg );
				});
			});
		});
		this.logMsg(  "/label/node:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
	}

	labelSlot{ |addr,name,msg|
		var client;
		// a client should be registered before being able to add expected nodes
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/label/slot", 15, [name] );
		},{
			msg[0] = msg[0].asInteger;
			if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
				{
					setters.put( msg[0], name.asSymbol );
				});
			if ( network.expectedNodes.indexOf( msg[0] ).isNil, {
				this.errorMsg( addr, "/label/slot", 6, msg );
			}, {
				if ( setters.at( msg[0] ) == name.asSymbol, {
					network.add( msg[2], [msg[0], msg[1].asInteger] );
				},{
					this.warnMsg( addr, "/label/slot", 4, msg );
				});
			});
		});
		this.logMsg( "/label/slot:" + msg.copyRange(0,1) + " from client with IP"+addr.ip+"and port"+addr.port );
	}


	addExpected{ |addr,name,msg|
		var client;
		// a client should be registered before being able to add expected nodes
		client = this.findClient( addr, name );
		if ( client.isNil, {
			this.errorMsg( addr, "/add/expected", 15, [name] );
		},{
			msg[0] = msg[0].asInteger;
			if ( msg[1].notNil ){
				msg[1] = msg[1].asInteger;
			};
			if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
				{
					setters.put( msg[0], name.asSymbol );
				});
			if ( setters.at( msg[0] ) == name.asSymbol, {
				switch( msg.size,
					4, { network.addExpected( msg[0], msg[2], msg[1], msg[3] ); },
					3, { network.addExpected( msg[0], msg[2], msg[1] ); },
					2, { network.addExpected( msg[0], size: msg[1] ); },
					1, { network.addExpected( msg[0] ); }
				);
				if ( msg[1].notNil, {
					if ( network.nodes.at( msg[0] ).notNil){
						client.addSetter( network.nodes.at( msg[0] ) );
					};
				})
			},{
				this.warnMsg( addr, "/add/expected", 4, msg );
			});
		});
		this.logMsg("/add/expected:" + msg + " from client with IP"+addr.ip+"and port"+addr.port );
	}

	// ---------------	

	getErrorString{ |addr,id,msg|
		var string;
		switch( id,
			1, { string = "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first." },
			2, { string = "Client with IP"+addr.ip+"and port"+addr.port+"is already registered. Please unregister first" },
			3, { string = "Client with IP"+addr.ip+"and port"+addr.port+"was not registered."},
			4, { string = "Client with IP"+addr.ip+"and port"+addr.port+"is not the setter of node with id"+msg[0] },
			5, { string = "Node with id"+msg[0]+"is not part of the network" },
			6, { string = "Node with id"+msg[0]+"is not expected to be part of the network" },
			7, { string = "There are no expected nodes in the network" },
			8, { string = "There are no nodes in the network" },
			9, { string = "There are no clients in the network" },
			10, { string = "Client with IP"+addr.ip+"and port"+addr.port+"has no setters"},
			11, { string = "Client with IP"+addr.ip+"and port"+addr.port+"has no subscriptions" },
			12, { string = "Node with id"+msg[0]+"does not have"+(msg.size-1)+"slots" },
			13, { string = "Node with id"+msg[0]+"has wrong type"+msg[3] },
			14, { string = "Client with IP"+addr.ip+"and port"+addr.port+"was not registered under name" + msg[0] },
			15, { string = "Client with IP"+addr.ip+"and port"+addr.port+"and name" + msg[0] + "is not registered. Please register first" };
		);	
		^string;
	}

	errorMsg{ |addr,request,id,msg|
		var string = this.getErrorString( addr, id, msg );
		addr.sendMsg( '/error', request, string, id );
		this.logMsg( "/error" + request + string );
	}

	warnMsg{ |addr,request,id,msg|
		var string = this.getErrorString( addr, id, msg );
		addr.sendMsg( '/warn', request, string, id );
		this.logMsg( "/warn" + request + string );
	}

	logMsg{ |string|
		if ( gui.notNil ){
			gui.addLogMsg( string );
		};
		if ( logging ){
			this.writeLogLine( string );
		};
	}

	// recording
	initLog{ |fn|
		fn = fn ? "SWDataNetworkOSCLog";
		logfile =  File(fn++"_"++Date.localtime.stamp++".txt", "w");
		logging = true;
		ShutDown.add( { this.closeLog; });
	}

	writeLogLine{ |line|
		logfile.write( Date.localtime.asString );
		logfile.write( "\t" );
		logfile.write( line.asString );
		logfile.write( "\n" );
	}

	closeLog{
		logging = false;
		logfile.close;
	}
}

SWDataNetworkOSCClient{

	var <>key;
	var <addr;

	var <>active = false;

	var <missedPongs = 0;
	var <subscriptions;
	var <setters;

	var <nodeSubs;
	var <slotSubs;
	var <slotNodesSubs;


	*new{ |addr,name|
		^super.new.init( addr,name );
	}

	init{ |address,name|
		addr = address;
		key = name;

		subscriptions = Set.new;

		slotNodesSubs = Set.new;
		nodeSubs = Set.new;
		slotSubs = IdentityDictionary.new;

		setters = Set.new;
		this.sendRegistered;
	}

	sendRegistered{
		addr.sendMsg( '/registered', addr.port.asInteger, key.asString );
	}

	addr_{ |newaddr|
		addr = newaddr;
		//	addr.sendMsg( '/registered', addr.port.asInteger );
	}

	ping{
		addr.sendMsg( '/ping', addr.port.asInteger, key.asString );
		missedPongs = missedPongs + 1;
	}

	pong{
		active = true;
		missedPongs = 0;
		//		missedPongs = missedPongs - 1;
	}


	addSetter{ |node|
		var existing;
		// check for nodes with same id:
		existing = setters.select( { |it| it.id == node.id });
		if ( existing.notNil ){
			existing.do{ |it| setters.remove( it ) };
		};
		setters.add( node );
		addr.sendMsg( '/info/setter', node.id, node.key.asString, node.slots.size, node.type );
	}

	setterQuery{
		if ( setters.size == 0, {
			^false;
		});
		setters.do{ |it|
			addr.sendMsg( '/info/setter', it.id, it.key.asString, it.slots.size, it.type );
		};
		^true;
	}

	welcomeBack{
		//	this.dump;
		this.sendRegistered;
		this.pong;
		this.setterQuery;		
		this.subscriptionQuery;
		setters.do{ |it|
			this.newNode( it );
		};
	}


	checkForSetter{ |node|
		^setters.includes(node);
	}

	subscriptionQuery{
		if ( subscriptions.size == 0, {
			^false;
		});

		nodeSubs.do{ |it|
			addr.sendMsg( '/subscribed/node', addr.port, key.asString, it );
		};
		slotNodesSubs.do{ |it|
			slotSubs[it].do{ |jt|
				addr.sendMsg( '/subscribed/slot', addr.port, key.asString, it, jt );
			}
		};
		/*
		subscriptions.do{ |it|
			//			it.postln;
			if ( it.isKindOf( Array ),
				{
					addr.sendMsg( '/subscribed/slot', addr.port, it[0], it[1] );
				},{
					addr.sendMsg( '/subscribed/node', addr.port, it );
				})
		};
		*/
		^true;
	}


	unsubscribeAll{
		nodeSubs.copy.do{ |it|
			this.unsubscribeNode( it );
		};
		slotNodesSubs.copy.do{ |it|
			slotSubs[it].copy.do{ |jt|
				this.unsubscribeSlot( it, jt );
			}
		};
	}

	subscribeNode{ |id|
		subscriptions.add( id );

		nodeSubs.add( id );

		addr.sendMsg( '/subscribed/node', addr.port, key.asString, id );
	}

	subscribeSlot{ |id1,id2|
		subscriptions.add( [id1, id2] );

		slotNodesSubs.add( id1 );
		if ( slotSubs.at(id1).isNil ){
			slotSubs.put( id1, Set.new );
		};
		slotSubs[id1].add( id2 );

		addr.sendMsg( '/subscribed/slot', addr.port, key.asString, id1, id2 );
	}

	unsubscribeNode{ |id|
		subscriptions.remove( id );
		nodeSubs.remove( id );
		addr.sendMsg( '/unsubscribed/node', addr.port, key.asString, id );
	}

	unsubscribeSlot{ |id1,id2|
		subscriptions.remove( [id1, id2] );

		slotSubs[id1].remove( id2 );

		if ( slotSubs[id1].size == 0 ){
			slotSubs.removeAt(id1);
			slotNodesSubs.remove( id1 );
		};

		addr.sendMsg( '/unsubscribed/slot', addr.port, key.asString, id1, id2 );
	}

	newExpected{ |id,label|
		addr.sendMsg( '/info/expected', id, label.asString );
	}

	newNode{ |node|
		//	node.dump;
		addr.sendMsg( '/info/node', node.id, node.key.asString, node.slots.size, node.type );
		node.slots.do{ |it,i|
			this.newSlot( it );
		};
	}

	newSlot{ |slot|
		addr.sendMsg( '/info/slot', slot.id[0], slot.id[1], slot.key.asString, slot.type );
	}

	newBee{ |bee|
		addr.sendMsg( '/info/minibee', bee.id, bee.noInputs, bee.noOutputs );
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

	sendDataNode{ |node|
		var msg;
		//		if ( verbose, { 
		//		["sendData", id,data].postln;// } );
		// check node subscriptions:
		if ( nodeSubs.includes(node.id),
			//		if ( subscriptions.includes( node.id ),
			{
				msg = ['/data/node', node.id] ++ node.data;
				//	"node subscribed".postln;
				//	msg.postln;
				addr.sendMsg( *msg );
			});
		if ( slotNodesSubs.includes( node.id ),{
			slotSubs[node.id].do{ |it|
				//	"slot subscribed".postln;
				addr.sendMsg( '/data/slot', node.id, it, node.slots[it].value );
			};
		});
	}
}