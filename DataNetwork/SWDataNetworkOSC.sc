// this is a class to manage outgoing osc streams to other applications.
// other applications can register to the network, request info, and subscribe to slots or nodes.

SWDataNetworkOSC{

	classvar <>httppath = "/var/www/";

	var <>maxMissedPongs = 60;

	var <clientPorts;
	var <clients;
	var <network;
	var responders;
	var <watcher;

	var <setters;

	var <>gui;

	var <logfile;
	var <logging = false;

	var <>verbose = 0;

	*new{ |netw|
		^super.new.init( netw );
	}

	init{ |netw|
		network = netw;
		network.osc = this;
		clients = Array.new;
		clientPorts = List.new;
		setters = IdentityDictionary.new;
		responders = [
			OSCresponderNode( nil, '/register', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.addClient( addr, msg[2] );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unregister', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
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
			OSCresponderNode( nil, '/query/expected', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.expectedQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/nodes', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.nodeQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/slots', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.slotQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/clients', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.clientQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/subscriptions', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.subscriptionQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/query/setters', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.setterQuery( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/subscribe/all', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.allNodeSubscribe( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unsubscribe/all', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.allNodeUnsubscribe( addr );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/subscribe/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.nodeSubscribe( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/subscribe/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.slotSubscribe( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unsubscribe/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.nodeUnsubscribe( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/unsubscribe/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.slotUnsubscribe( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/set/data', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.setData( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/label/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.labelSlot( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };
			}),
			OSCresponderNode( nil, '/label/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.labelNode( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/get/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.getNode( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/get/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.getSlot( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/remove/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.removeNode( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			}),
			OSCresponderNode( nil, '/add/expected', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				if ( msg.size > 1 ){
					addr.port = msg[1]; this.addExpected( addr, msg.copyToEnd( 2 ) );
				}{ if ( verbose > 0, { "missing port in message".postln; }); };			})
		];

		responders.do{ |it| it.add };

		watcher = SkipJack.new( { 
			this.sendPings;
		}, 1, name: "SWDataNetworkOSC" , autostart: true );

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
		var file;

		// write to http accessible file:
		//		("echo" + NetAddr.langPort + "> " ++ httppath +/+ "SenseWorldDataNetwork").unixCmd;
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

		if ( gui.notNil ){
			gui.addLogMsg( "network announced" );
		};

		if ( logging ){
			this.writeLogLine( "network announced");
		};
	}

	expectedQuery{ |addr|
		if ( network.expectedNodes.size == 0, {
			addr.sendMsg( '/warn', "There are no expected nodes in the network");
		});
		network.expectedNodes.do{ |key|
			addr.sendMsg( '/info/expected', key );
		};

		if ( logging ){
			this.writeLogLine( "/query/expected from client with IP"+addr.ip+"and port"+addr.port );
		};
	}

	nodeQuery{ |addr|
		if ( network.nodes.size == 0, {
			addr.sendMsg( '/warn', "There are no nodes in the network");
		});
		network.nodes.keysValuesDo{ |key,node|
			addr.sendMsg( '/info/node', key, node.key, node.slots.size );
		};

		if ( logging ){
			this.writeLogLine( "/query/nodes from client with IP"+addr.ip+"and port"+addr.port );
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
		if ( logging ){
			this.writeLogLine( "/query/slots from client with IP"+addr.ip+"and port"+addr.port );
		};

	}

	clientQuery{ |addr|
		if ( clients.size == 0, {
			addr.sendMsg( '/warn', "There are no clients in the network");
		});
		clients.do{ |it|
			addr.sendMsg( '/info/client', it.addr.ip, it.addr.port, it.addr.hostname );
		};
		if ( logging ){
			this.writeLogLine( "/query/clients from client with IP"+addr.ip+"and port"+addr.port );
		};

	}

	subscriptionQuery{ |addr|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "/query/subscriptions", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.subscriptionQuery;
		});
		if ( logging ){
			this.writeLogLine( "/query/subscriptions from client with IP"+addr.ip+"and port"+addr.port );
		};

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

	allNodeSubscribe{ |addr|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "/subscribe/all" "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			network.nodes.do{ |it| 
				client.subscribeNode( it.id );
				this.getNode( addr, [it.id] );
				if ( gui.notNil ){
					gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to node:"+it.id );
				};
				if ( logging ){
					this.writeLogLine( "/subscribe/all from client with IP"+addr.ip+"and port"+addr.port );
				};
			};
		});
	}

	nodeSubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "/subscribe/node", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.subscribeNode( msg[0].asInteger );
			this.getNode( addr, msg );

			if ( gui.notNil ){
				gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to node:"+msg[0] );
			};
			if ( logging ){
				this.writeLogLine( "/subscribe/node:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
			};
		});
	}

	slotSubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "/subscribe/slot", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.subscribeSlot( msg[0].asInteger, msg[1].asInteger );
			this.getSlot( addr, msg );
			if ( gui.notNil ){
				gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"subscribed to slot:"+msg.copyRange(0,1) );
			};
			if ( logging ){
				this.writeLogLine( "/subscribe/slot:" + msg.copyRange(0,1) + " from client with IP"+addr.ip+"and port"+addr.port );
			};
		});
	}

	allNodeUnsubscribe{ |addr|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "/unsubscribe/all", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.subscriptions.do{ |it| 
				client.unsubscribeNode( it );
				if ( gui.notNil ){
					gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from node:"+it );
				};
				if ( logging ){
					this.writeLogLine( "/unsubscribe/all from client with IP"+addr.ip+"and port"+addr.port );
				};

			};
		});
	}

	nodeUnsubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "/unsubscribe/node", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.unsubscribeNode( msg[0].asInteger );
			if ( gui.notNil ){
				gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from node:"+msg[0] );
			};
			if ( logging ){
				this.writeLogLine( "/unsubscribe/node:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
			};

		});
	}

	slotUnsubscribe{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "/unsubscribe/slot", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.unsubscribeSlot( msg[0].asInteger, msg[1].asInteger );
			if ( gui.notNil ){
				gui.addLogMsg( "client:"+(client.addr.asString.replace( "a NetAddr",""))+"unsubscribed from slot:"+msg.copyRange(0,1) );
			};
			if ( logging ){
				this.writeLogLine( "/unsubscribe/slot:" + msg.copyRange(0,1) + " from client with IP"+addr.ip+"and port"+addr.port );
			};

		});
	}

	getNode{ |addr,msg|
		var client,data;
		//[addr,msg].postln;
		client = this.findClient( addr );
		//client.postln;
		if ( client.isNil, {
			addr.sendMsg( '/error', "/get/node", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			msg[0] = msg[0].asInteger;
			if ( network.nodes.at( msg[0] ).isNil, {
				addr.sendMsg( '/warn', "Node"+msg[0]+"does not exist yet");
			},{
				data = [ '/data/node', msg[0], network.nodes.at( msg[0] ).data ].flatten;
			//			data.postln;
			addr.sendMsg( *data );
			//	addr.sendMsg( '/data/node', msg[1], *data );
			});
			if ( logging ){
				this.writeLogLine( "/get/node:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
			};
		});
	}

	getSlot{ |addr,msg|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "/get/slot", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			msg[0] = msg[0].asInteger;
			if ( network.nodes.at( msg[0] ).isNil, {
				addr.sendMsg( '/warn', "Node"+msg[0]+"does not exist yet");
			},{
				addr.sendMsg( '/data/slot', msg[0], msg[1], network.nodes.at( msg[0] ).slots.at( msg[1].asInteger ).value );
			});
			if ( logging ){
				this.writeLogLine( "/get/slot:" + msg.copyRange(0,1) + " from client with IP"+addr.ip+"and port"+addr.port );
			};
		});
	}

	findClient{ |addr|
		^clients.select( { |it| it.addr == addr } ).first;
	}

	addClient{ |addr,name|
		var there;
		there = this.findClient( addr );
		//		there = clients.find( { |it| it.addr == addr } );
		[addr,there].postln;
		if ( there.isNil, {
			if ( addr.port > 0){
				clientPorts.add( addr.port );
				clients = clients.add( SWDataNetworkOSCClient.new( addr ); );
				watcher.start;
				clients.last.key = name;
				if ( gui.notNil ){ 
					gui.addClient( clients.last );
					gui.addLogMsg( "client registered:"+(addr.asString.replace( "a NetAddr",""))+name );
				};
				if ( logging ){
					this.writeLogLine( "client registered:"+(addr.asString.replace( "a NetAddr"))+name );
				};
			};
		},{
			addr.sendMsg( '/error', "/register", "Client with IP"+addr.ip+"and port"+addr.port+"is already registered. Please unregister first");
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
		if ( logging ){
			this.writeLogLine( "client unregistered:"+(addr.asString.replace( "a NetAddr")) );
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

	setterQuery{ |addr|
		var client;
		client = this.findClient( addr );
		if ( client.isNil, {
			addr.sendMsg( '/error', "/query/setters", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first.");			
		},{
			client.setterQuery;
		});
	}

	removeNode{ |addr,msg|
		var there;
		there = this.findClient( addr );
		if ( there.isNil, {
			addr.sendMsg( '/error', "/remove/node", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first");
		},{
			msg[0] = msg[0].asInteger;
			if ( network.nodes.at( msg[0] ).notNil,
				{
					if ( setters.at( msg[0] ) == addr, {
						network.removeNode( msg[0] );
						addr.sendMsg( '/removed/node', msg[0] );
					},{
						addr.sendMsg( '/error', "/remove/node", ("you are not the setter of node with id"+msg[0]+", so you cannot remove it" ) ); 
					});
				},{
					addr.sendMsg( '/error', "/remove/node", ("node with id"+msg[0]+"is not part of the network" ) ); 
				});
			if ( logging ){
				this.writeLogLine( "/remove/node:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
			};
		});
	}

	setData{ |addr,msg|
		var there, addsetter;
		addsetter = false;
		there = this.findClient( addr );
		if ( there.isNil, {
			addr.sendMsg( '/error', "/set/data", "Client with IP"+addr.ip+"and port"+addr.port+"is not registered. Please register first");
		},{
			msg[0] = msg[0].asInteger;
			if ( network.nodes.at( msg[0] ).isNil,
				{
					if ( setters.at( msg[0] ).isNil, {
						setters.put( msg[0], addr );
					});
					addsetter = true;
				});
			if ( network.expectedNodes.indexOf( msg[0] ).isNil,
				{
					addr.sendMsg( '/error', "/set/data", ("node with id"+msg[0]+"and size"+(msg.size - 1 )+"is not expected to be part of the network" ) ); 
				}, {
					if ( setters.at( msg[0] ) == addr, {
						network.setData( msg[0], msg.copyToEnd( 1 ).asFloat );
						if ( addsetter, {
							there.addSetter( network.nodes.at( msg[0] ) );
							// only send data back to sender first time the node is set, for confirmation:
							this.getNode( addr, msg );
							if ( gui.notNil ){
								gui.addLogMsg( "client:"+(there.addr.asString.replace( "a NetAddr",""))+"became setter of node:"+msg[0] );
							};
							if ( logging ){
								this.writeLogLine( "client:"+(there.addr.asString.replace( "a NetAddr",""))+"became setter of node:"+msg[0] );
							};
						});
					});
				});
		});
	}

	labelSlot{ |addr,msg|
		msg[0] = msg[0].asInteger;
		if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
			{
				setters.put( msg[0], addr );
			});
		if ( network.expectedNodes.indexOf( msg[0] ).isNil, {
			addr.sendMsg( '/error', "/label/slot", ("node with id"+msg[0]+"and size"+(msg.size - 1 )+"is not expected to be part of the network" ) ); }, {
				if ( setters.at( msg[0] ) == addr, {
					network.add( msg[2], [msg[0], msg[1].asInteger] );
				},{
					addr.sendMsg( '/warn', ("you are not the setter of node with id"+msg[0]++", so you cannot label it" ) );
				});
			});
		if ( logging ){
			this.writeLogLine( "/label/slot:" + msg.copyRange(0,1) + " from client with IP"+addr.ip+"and port"+addr.port );
		};
	}

	labelNode{ |addr,msg|
		msg[0] = msg[0].asInteger;
		if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
			{
				setters.put( msg[0], addr );
			});
		if ( network.expectedNodes.indexOf( msg[0] ).isNil, {
			addr.sendMsg( '/error', "/label/node", ("node with id"+msg[0]+"and size"+(msg.size - 1 )+"is not expected to be part of the network" ) ); }, {
				if ( setters.at( msg[0] ) == addr, {
					network.add( msg[1], msg[0] );
				},{
					addr.sendMsg( '/warn', ("you are not the setter of node with id"+msg[0]++", so you cannot label it" ) );
				});
			});
		if ( logging ){
			this.writeLogLine( "/label/node:" + msg[0] + " from client with IP"+addr.ip+"and port"+addr.port );
		};
	}

	addExpected{ |addr,msg|
		msg[0] = msg[0].asInteger;
		if ( network.nodes.at( msg[0] ).isNil and: setters.at( msg[0] ).isNil,
			{
				setters.put( msg[0], addr );
			});
		if ( setters.at( msg[0] ) == addr, {
			network.addExpected( msg[0], msg[2], msg[1] );
		});
		if ( logging ){
			this.writeLogLine( "/add/expected:" + msg + " from client with IP"+addr.ip+"and port"+addr.port );
		};
	}

	sendData{ |id,data|
		if ( verbose > 1, { ["sendData", id,data].postln; } );
		clients.do{ |it|
			it.sendData( id, data );
		};
	}

	sendDataNode{ |node|
		if ( verbose > 1, { ["sendDataNode", node.id, node.data].postln; } );
		clients.do{ |it|
			it.sendDataNode( node );
		};
	}

	nodeRemoved{ |id|
		clients.do{ |it| it.nodeRemoved( id ) };
	}
	
	sendPings{
		clients.do{ |it| it.ping };
		clients.do{ |it| if ( it.missedPongs > maxMissedPongs ){ this.removeClient( it.addr ) } };
	}

	stop{
		clients.do{ |it| 
			it.addr.sendMsg( '/unregistered', it.addr.port.asInteger );
			it.addr.sendMsg( '/datanetwork/quit');
		};
		if ( logging ){
			this.writeLogLine( "datanetwork stopped" );
		};
	}

	// recording
	initLog{ |fn|
		fn = fn ? "SWDataNetworkOSCLog";
		logfile =  File(fn++"_"++Date.localtime.stamp++".txt", "w");
		logging = true;
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
	var <addr;
	var <missedPongs = 0;
	var <subscriptions;
	var <nodeSubs;
	var <slotSubs;
	var <slotNodesSubs;
	var <setters;
	var <>key;

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