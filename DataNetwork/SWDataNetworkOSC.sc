// this is a class to manage outgoing osc streams to other applications.
// other applications can register to the network, request info, and subscribe to slots or nodes.

SWDataNetworkOSC{

	classvar <>httppath = "/var/www/";

	var <>verbose = 0;

	var <>maxMissedPongs = 60;

	var <clients;
	var <setters;
	var <network;
	var <watcher;

	var <clientPorts;
	var responders;


	var <>gui;

	var <logfile;
	var <logging = false;


	*new{ |netw|
		^super.new.init( netw );
	}

	init{ |netw|
		network = netw;
		network.osc = this;
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
			it.addr.sendMsg( '/unregistered', it.addr.port.asInteger );
			it.addr.sendMsg( '/datanetwork/quit');
		};
		this.logMsg("datanetwork stopped" );
		this.removeResponders;
	}

	sendPings{
		clients.do{ |it| it.ping };
		clients.do{ |it| if ( it.missedPongs > maxMissedPongs ){ this.removeClient( it.addr ) } };
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
					addr.port = msg[1]; this.removeClient( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),

			OSCresponderNode( nil, '/pong', { |t,r,msg,addr|
				var client;
				if ( verbose > 3, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1];
					client = this.findClient( addr );
					if ( client.notNil, { client.pong } ) ;
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),

			/// QUERIES

			OSCresponderNode( nil, '/query/expected', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.expectedQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/nodes', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.nodeQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/slots', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.slotQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/clients', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.clientQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/subscriptions', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.subscriptionQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/setters', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.setterQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),

			/// SUBSCRIPTIONS

			OSCresponderNode( nil, '/subscribe/all', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.allNodeSubscribe( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unsubscribe/all', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.allNodeUnsubscribe( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/subscribe/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.nodeSubscribe( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unsubscribe/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.nodeUnsubscribe( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/subscribe/slot', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.slotSubscribe( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unsubscribe/slot', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.slotUnsubscribe( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),

			// SETTING, LABELING

			OSCresponderNode( nil, '/add/expected', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.addExpected( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/set/data', { |t,r,msg,addr|
				if ( verbose > 2, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.setData( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/label/slot', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.labelSlot( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/label/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.labelNode( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),

			/// GETTING
			OSCresponderNode( nil, '/get/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.getNode( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/get/slot', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.getSlot( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),

			/// REMOVING

			OSCresponderNode( nil, '/remove/node', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.removeNode( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/remove/all', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.removeAll( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			})
		];

		responders.do{ |it| it.add };
	}


	// ---------- autoconnection and recovery support -------

	announce{ |ports|
		var b,broadcastip,myip;
		var prefix;
		var cips;
		var file;

		// write the file:
		file = File.open( httppath +/+ "SenseWorldDataNetwork", "w");
		file.write( NetAddr.langPort.asString);
		file.close;

		if ( ports.isNil, {
			ports = (6000..6009) ++ (57120..57129);
		});

		ports = ports ++ clientPorts;

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

	// -------- 
	

	//------- Methods called by the network

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

	allNodeSubscribe{ |addr|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			this.errorMsg( addr, "/subscribe/all", 1 );
		},{
			network.nodes.do{ |it| 
				client.subscribeNode( it.id );
				this.getNode( addr, [it.id] );
				this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to node:"+it.id );
			};
			this.logMsg( "/subscribe/all from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	nodeSubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			this.errorMsg( addr, "/subscribe/node", 1 );
		},{
			client.subscribeNode( msg[0].asInteger );
			this.getNode( addr, msg );
			this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to node:"+msg[0] );
		});
	}

	slotSubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			this.errorMsg( addr, "/subscribe/slot", 1);
		},{
			client.subscribeSlot( msg[0].asInteger, msg[1].asInteger );
			this.getSlot( addr, msg );
			this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to slot:"+msg.copyRange(0,1) );
		});
	}


	allNodeUnsubscribe{ |addr|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			this.errorMsg( addr, "/unsubscribe/all", 1);
		},{
			client.subscriptions.do{ |it| 
				client.unsubscribeNode( it );
				this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from node:"+it );
			};
			this.logMsg( "/unsubscribe/all from client with IP"+addr.ip+"and port"+addr.port );
		});
	}

	nodeUnsubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			this.errorMsg( addr, "/unsubscribe/node", 1);
		},{
			client.unsubscribeNode( msg[0].asInteger );
			this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from node:"+msg[0] );
		});
	}

	slotUnsubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			this.errorMsg( addr, "/unsubscribe/slot", 1);
		},{
			client.unsubscribeSlot( msg[0].asInteger, msg[1].asInteger );
			this.logMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from slot:"+msg.copyRange(0,1) );
		});
	}


	getNode{ |addr,msg|
		var client,data,node;
		//[addr,msg].postln;
		client = this.findClient( addr );
		//client.postln;
		if ( client.isNil, {
			this.errorMsg( addr, "/get/node", 1);
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

	getSlot{ |addr,msg|
		var client,node;
		client = this.findClient( addr );
		if ( client.isNil, {
			this.errorMsg( addr, "/get/slot", 1);
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

	findClient{ |addr|
		^clients.select( { |it| it.addr == addr } ).first;
	}

	addClient{ |addr,name|
		var there;
		there = this.findClient( addr );
		//		there = clients.find( { |it| it.addr == addr } );
		//	[addr,there].postln;
		if ( there.isNil, {
			if ( addr.port > 0){
				clientPorts.add( addr.port );
				clients = clients.add( SWDataNetworkOSCClient.new( addr ); );
				//	watcher.start;
				clients.last.key = name;
				if ( gui.notNil ){ 
					gui.addClient( clients.last );
				};
				this.logMsg( "client registered:"+(addr.asString.replace( "a NetAddr",""))+name );
			};
		},{
			this.errorMsg( addr, "/register", 2);
		});
	}

	removeClient{ |addr|
		var there,there2;
		there = this.findClient( addr );
		//		[addr,there].postln;

		if ( there.notNil, { 
			there.setters.do{ |node| setters.removeAt( node.id ) };
			clients.remove( there );
			addr.sendMsg( '/unregistered', addr.port.asInteger );
		},{
			this.errorMsg( addr, "/unregister", 3 );
		} );

		//		there2 = setters.findKeyForValue( addr );
		//		if ( there2.notNil, { setters.removeAt( there2 ) } );


		if ( gui.notNil ){
			gui.removeClient( there );
		};
		this.logMsg( "client unregistered:"+(addr.asString.replace( "a NetAddr","")) );
	}

	//------- Queries -------

	expectedQuery{ |addr|
		if ( network.expectedNodes.size == 0, {
			this.warnMsg( addr, "/query/expected", 7 );
		});
		network.expectedNodes.do{ |key|
			addr.sendMsg( '/info/expected', key );
		};
		this.logMsg( "/query/expected from client with IP"+addr.ip+"and port"+addr.port );
	}

	nodeQuery{ |addr|
		if ( network.nodes.size == 0, {
			this.warnMsg( addr, "/query/nodes", 8 );
		});
		network.nodes.keysValuesDo{ |key,node|
			addr.sendMsg( '/info/node', key, node.key, node.slots.size );
		};

		this.logMsg( "/query/nodes from client with IP"+addr.ip+"and port"+addr.port );
	}

	slotQuery{ |addr|
		if ( network.nodes.size == 0, {
			this.warnMsg( addr, "/query/slots", 8 );
		});
		network.nodes.keysValuesDo{ |key,node|
			node.slots.do{ |it,i|
				addr.sendMsg( '/info/slot', key, i, it.key );
			};
		};

		this.logMsg( "/query/slots from client with IP"+addr.ip+"and port"+addr.port );
	}

	clientQuery{ |addr|
		if ( clients.size == 0, {
			this.warnMsg( addr, "/query/clients", 9 );
		});
		clients.do{ |it|
			addr.sendMsg( '/info/client', it.addr.ip, it.addr.port, it.key );
		};

		this.logMsg( "/query/clients from client with IP"+addr.ip+"and port"+addr.port );
	}

	subscriptionQuery{ |addr|
		var client;
		client = this.findClient( addr );
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

	setterQuery{ |addr|
		var client;
		client = this.findClient( addr );
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

	setData{ |addr,msg|
		var there, addsetter, node, code;
		addsetter = false;
		there = this.findClient( addr );
		if ( there.isNil, {
			this.errorMsg( addr, "/set/data", 1);
		},{
			msg[0] = msg[0].asInteger;
			node = network.nodes.at( msg[0] );
			//	node.postln;
			if ( node.isNil,
				{ // it's a new node for the network:
					if ( setters.at( msg[0] ).isNil, {
						// and there was no setter yet, so client becomes the setter
						setters.put( msg[0], addr );
					});
					// if the client is the setter:
					if ( setters.at( msg[0] ) == addr ){
						if ( network.expectedNodes.indexOf( msg[0] ).isNil,
							{
								this.errorMsg( addr, "/set/data", 6, msg );
							}, {
								network.setData( msg[0], msg.copyToEnd( 1 ).asFloat );
								// has to look up the newly created node:
								there.addSetter( network.nodes.at( msg[0] ) );
								// only send data back to sender first time the node is set, for confirmation:
								this.getNode( addr, msg );
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
						code = network.setData( msg[0], msg.copyToEnd( 1 ).asFloat );
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

	removeAll{ |addr|
		var there;
		there = this.findClient( addr );
		if ( there.isNil, {
			this.errorMsg( addr, "/remove/all", 1);
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

	removeNode{ |addr,msg|
		var there,node;
		there = this.findClient( addr );
		if ( there.isNil, {
			this.errorMsg( addr, "/remove/node", 1);
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


	labelNode{ |addr,msg|
		msg[0] = msg[0].asInteger;
		if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
			{
				setters.put( msg[0], addr );
			});
		if ( network.expectedNodes.indexOf( msg[0] ).isNil, {
			this.warnMsg( addr, "/label/node", 6, msg );
			if ( setters.at( msg[0] ) == addr, {
				network.add( msg[1], msg[0] );
			},{
				this.warnMsg( addr, "/label/node", 4, msg );
			});
		});
		this.logMsg(  "/label/node:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
	}

	labelSlot{ |addr,msg|
		msg[0] = msg[0].asInteger;
		if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
			{
				setters.put( msg[0], addr );
			});
		if ( network.expectedNodes.indexOf( msg[0] ).isNil, {
			this.errorMsg( addr, "/label/slot", 6, msg );
		}, {
			if ( setters.at( msg[0] ) == addr, {
				network.add( msg[2], [msg[0], msg[1].asInteger] );
			},{
				this.warnMsg( addr, "/label/slot", 4, msg );
			});
		});
		this.logMsg( "/label/slot:" + msg.copyRange(0,1) + " from client with IP"+addr.ip+"and port"+addr.port );
	}


	addExpected{ |addr,msg|
		var client;
		msg[0] = msg[0].asInteger;
		if ( msg[1].notNil ){
			msg[1] = msg[1].asInteger;
		};
		if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
			{
				setters.put( msg[0], addr );
			});
		if ( setters.at( msg[0] ) == addr, {
			network.addExpected( msg[0], msg[2], msg[1] );
			if ( msg[1].notNil, {
				client = this.findClient( addr );
				if ( client.notNil ){
					if ( network.nodes.at( msg[0] ).notNil){
						client.addSetter( network.nodes.at( msg[0] ) );
					};
				};
			})
		},{
			this.warnMsg( addr, "/add/expected", 4, msg );
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
			12, { string = "Node with id"+msg[0]+"does not have"+(msg.size-1)+"slots" }
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
	var <missedPongs = 0;
	var <subscriptions;
	var <setters;

	var <nodeSubs;
	var <slotSubs;
	var <slotNodesSubs;


	*new{ |addr|
		^super.new.init( addr );
	}

	init{ |address|
		addr = address;

		subscriptions = Set.new;

		slotNodesSubs = Set.new;
		nodeSubs = Set.new;
		slotSubs = IdentityDictionary.new;

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


	addSetter{ |node|
		var existing;
		// check for nodes with same id:
		existing = setters.select( { |it| it.id == node.id });
		if ( existing.notNil ){
			existing.do{ |it| setters.remove( it ) };
		};
		setters.add( node );
		addr.sendMsg( '/info/setter', node.id, node.key, node.slots.size );
	}

	setterQuery{
		if ( setters.size == 0, {
			^false;
		});
		setters.do{ |it|
			addr.sendMsg( '/info/setter', it.id, it.key, it.slots.size );
		};
		^true;
	}

	checkForSetter{ |node|
		^setters.includes(node);
	}

	subscriptionQuery{
		if ( subscriptions.size == 0, {
			^false;
		});

		nodeSubs.do{ |it|
			addr.sendMsg( '/subscribed/node', addr.port, it );
		};
		slotNodesSubs.do{ |it|
			slotSubs[it].do{ |jt|
				addr.sendMsg( '/subscribed/slot', addr.port, it, jt );
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

	subscribeNode{ |id|
		subscriptions.add( id );

		nodeSubs.add( id );

		addr.sendMsg( '/subscribed/node', addr.port, id );
	}

	subscribeSlot{ |id1,id2|
		subscriptions.add( [id1, id2] );

		slotNodesSubs.add( id1 );
		if ( slotSubs.at(id1).isNil ){
			slotSubs.put( id1, Set.new );
		};
		slotSubs[id1].add( id2 );

		addr.sendMsg( '/subscribed/slot', addr.port, id1, id2 );
	}

	unsubscribeNode{ |id|
		subscriptions.remove( id );
		nodeSubs.remove( id );
		addr.sendMsg( '/unsubscribed/node', addr.port, id );
	}

	unsubscribeSlot{ |id1,id2|
		subscriptions.remove( [id1, id2] );

		slotSubs[id1].remove( id2 );

		if ( slotSubs[id1].size == 0 ){
			slotSubs.removeAt(id1);
			slotNodesSubs.remove( id1 );
		};

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