SWDataNetworkClient : SWDataNetwork{

	var <host;
	var <myaddr;
	var responders;
	var <lasttime;

	var <>name;

	var <subscriptions,<setters;
	
	var <>autoregister = true;
	// do not set unless you are the class itself
	var <registered = false;

	*new{ |hostip,name="",reg=true|
		^super.new.init.myInit( hostip,name, reg );
	}

	myInit{ |hst,nm="",reg=true|
		var ip,prefix,foundHost;
		name = nm;
		lasttime = Process.elapsedTime;
		host = NetAddr( hst, NetAddr.langPort);

		foundHost = this.findHost;

		if ( thisProcess.platform.name == \linux, {
			prefix = "/sbin/";
		},{ prefix = "" });

		ip = NetAddr.myIP(prefix);
		//		network = SWDataNetwork.new;
		myaddr = NetAddr( ip, NetAddr.langPort );

		subscriptions = Set.new;
		setters = Set.new;

		this.addResponders;

		if ( reg and: foundHost ){
			this.register;
		}{
			if ( reg ){ // make sure that we start worrying right away, so we keep looking for the host
				lasttime = lasttime - worrytime;
			}
		};

		watcher = SkipJack.new( { this.worryAboutTime }, 1, name: "SWDataNetworkClient", autostart: true );			

		ShutDown.add(
			{ 
				if ( this.registered ){ this.unregister; };
			}
		)

	}

	registered_{ |reg|
		registered = reg;
		if ( registered ){
			"Registered as client at DataNetwork".postln;
		}{
			"Unregistered as client at DataNetwork".postln;
		};

		subscriptions.do{ |it|
			if ( it.isArray ){
				this.subscribeSlot( it );
			}{
				this.subscribeNode( it );
			}
		};
		setters.do{ |it|
			if ( nodes[it].notNil ){
				this.addExpected( it, spec.findNode( it ), nodes[it].size, nodes[it].type );
			}{
				this.addExpected( it, spec.findNode( it ) );
			};
		};
	}

	addResponders{
		responders = [
			OSCresponderNode( nil, '/datanetwork/announce', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.setHost( *(msg.copyToEnd( 1 )) );
			}),
			OSCresponderNode( host, '/datanetwork/quit', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				// could do checking of hostname and port!
				this.lostHost( *(msg.copyToEnd( 1 )) );
			}),
			OSCresponderNode( host, '/error', { |t,r,msg,addr|
				"DataNetwork Error: ".post; msg.postln;
				if ( gui.notNil ){ gui.setInfo( msg )};
			}),
			OSCresponderNode( host, '/warn', { |t,r,msg,addr|
				"DataNetwork Warning: ".post; msg.postln;
				if ( gui.notNil ){ gui.setInfo( msg )};
			}),
			OSCresponderNode( host, '/ping', { |t,r,msg,addr|
				if ( verbose > 1, { msg.postln; });
				this.sendPong;
			}),
			OSCresponderNode( host, '/registered', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.registered_( true );				
			}),
			OSCresponderNode( host, '/unregistered', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.registered_( false );				
			}),
			OSCresponderNode( host, '/info/expected', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.addExpected( msg[1], msg[2], fromnw: true );				
			}),
			OSCresponderNode( host, '/info/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.nodeInfo( msg.copyToEnd( 1 ) );
			}),
			OSCresponderNode( host, '/info/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.slotInfo( msg.copyToEnd( 1 ) );
			}),
			OSCresponderNode( host, '/info/client', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.clientInfo( msg.copyToEnd( 1 ) );
			}),
			OSCresponderNode( host, '/info/setter', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.setterInfo( msg.copyToEnd( 1 ) );
			}),
			OSCresponderNode( host, '/subscribed/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.subscribeNodeInfo( msg.copyToEnd( 1 ) );
			}),
			OSCresponderNode( host, '/subscribed/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.subscribeSlotInfo( msg.copyToEnd( 1 ) );
			}),
			OSCresponderNode( host, '/unsubscribed/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.unsubscribeNodeInfo( msg.copyToEnd( 1 ) );
			}),
			OSCresponderNode( host, '/unsubscribed/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.unsubscribeSlotInfo( msg.copyToEnd( 1 ) );
			}),
			OSCresponderNode( host, '/removed/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.removeNode( msg[1], true );
			}),
			OSCresponderNode( host, '/data/node', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.nodeData( msg.copyToEnd( 1 ) );
			}),
			OSCresponderNode( host, '/data/slot', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.slotData( msg.copyToEnd( 1 ) );
			})
		];

		responders.do{ |it| it.add };
	}

	removeResponders{
		responders.do{ |it| it.remove };
	}

	/// ---- host interaction ---

	setHost{ |ip,port|
		host = NetAddr( ip.asString, port );
		this.resetHost;
	}

	resetHost{ |addr|
		this.removeResponders;
		this.addResponders;
		if ( autoregister, {
			this.unregister;
			this.register;
		});
	}

	// used to find the port on which the host is listening
	findHost{ |ip|
		var port;
		if ( ip.isNil, { ip = host.hostname });
		port = ("curl -s http://" ++ ip ++ "/SenseWorldDataNetwork").unixCmdGetStdOut.asInteger;
		if ( port == 0 ){
			"The port I found is 0. This may be an indication that the http access to the host machine is not set up correctly. Try and visit: http://" ++ ip ++ "/SenseWorldDataNetwork to verify this. If you cannot reach that page, then check the helpfile [SW_Apache_setup] for instructions to make the http setup on the host work properly".warn;
			^false;
		}{
			host.port = port;
			^true;
		}
	}

	lostHost{ |ip,port|
		"DataNetwork host has quit".postln;
		if ( gui.notNil ){ gui.setInfo( "DataNetwork host has quit" )};
	}

	tryReconnect{
		if ( this.findHost ){
			this.resetHost;
		};
	}

	osc_{
		"cannot create a osc-datanetwork-host from a client".warn;
	}

	// ------------

	worryAboutTime{
		if ( Process.elapsedTime - lasttime > worrytime,
			{
				this.tryReconnect;
			});
	}

	// overloaded from base class
	addExpected{ |id,label,size=nil,type=0,fromnw=false|
		if ( fromnw.not, {
			if ( size.isNil ){
				this.sendMsgWithArgs( '/add/expected', [ id ] );
			}{
				if ( label.isNil ){
					this.sendMsgWithArgs( '/add/expected', [ id, size ] );
				}{
					this.sendMsgWithArgs( '/add/expected', [ id, size, label, type] );
				}
			};
		},{
			("DataNetwork: expected node %, with label % and % slots".format( id, label, size )).postln;
			// use the method from the super-class
			super.addExpected( id, label, size, type );
		});
	}

	// overloaded from base class
	setData{ |id,data,fromnw=false|
		//	var type;
		var ret;
		if ( verbose > 1, { [id,data].postln; } );
		
		ret = super.setData( id, data );
		
		/*
		if ( nodes[id].isNil, {
			type = this.checkDataType( data );
			ret = this.registerNode( id, data.size, type );
			if ( verbose > 0 ) { ("registering node"+id+ret).postln; };
		});
		*/

		if ( ret == 0 ) { 
			//	nodes[id].data = data;
			if ( fromnw.not, {
				this.sendData( id, data );
			});
		};
	}

	// overloaded from base class
	add{ |key, slot,fromnw=false|
		var ns;
		super.add( key, slot );
		//	spec.add( key, slot );
		if ( fromnw.not, {
			ns = this.at( key );
			if ( ns.isKindOf( SWDataNode ),{
				this.labelNode( ns );
			});
			if ( ns.isKindOf( SWDataSlot ),{
				this.labelSlot( ns );
			});
		});
	}

	// overloaded from base class
	removeNode{ |id,fromnw=false|
		if ( verbose > 0, { ("remove" + id).postln; });
		if ( fromnw.not ){
			this.sendMsgWithArgs( '/remove/node', [ id.asInteger] );
		}{
			super.removeNode( id.asInteger );
			//		nodes.removeAt( id.asInteger );			
		};	
	}


	/// OSC interface

	sendSimpleMsg{ |msg|
		host.sendMsg( msg, NetAddr.langPort, name.asString );
	}

	sendMsgWithArgs{ |msg,args|
		var fullMsg = [ msg, NetAddr.langPort, name.asString ]++args;
		host.sendMsg( *fullMsg );
	}

	register{
		this.sendSimpleMsg( '/register');
	}

	unregister{
		this.sendSimpleMsg( '/unregister');
	}

	// Querying ---

	queryAll{
		this.sendSimpleMsg( '/query/all' );
	}

	queryExpected{
		this.sendSimpleMsg( '/query/expected' );
	}

	queryNodes{
		this.sendSimpleMsg( '/query/nodes' );
	}

	querySlots{
		this.sendSimpleMsg( '/query/slots' );
	}

	querySetters{
		this.sendSimpleMsg( '/query/setters' );
	}

	querySubscriptions{
		this.sendSimpleMsg( '/query/subscriptions' );
	}

	queryClients{
		this.sendSimpleMsg( '/query/clients' );
	}


	// -- Subscribing --

	subscribeAll{ 
		this.sendSimpleMsg( '/subscribe/all' );
	}

	unsubscribeAll{ 
		this.sendSimpleMsg( '/unsubscribe/all' );
	}

	removeAll{ 
		this.sendSimpleMsg( '/remove/all' );
	}

	subscribeNode{ |node|
		if ( node.isKindOf( SWDataNode ) ){
			this.sendMsgWithArgs( '/subscribe/node', node.id );
		}{
			this.sendMsgWithArgs( '/subscribe/node', node );
		}
	}

	unsubscribeNode{ |node|
		if ( node.isKindOf( SWDataNode ) ){
			this.sendMsgWithArgs( '/unsubscribe/node', node.id );
		}{
			this.sendMsgWithArgs( '/unsubscribe/node', node );
		}
	}

	subscribeSlot{ |slot|
		if ( slot.isKindOf( SWDataSlot ) ){
			this.sendMsgWithArgs( '/subscribe/slot', slot.id );
		}{
			this.sendMsgWithArgs( '/subscribe/slot', slot );
		}
	}

	unsubscribeSlot{ |slot|
		if ( slot.isKindOf( SWDataSlot ) ){
			this.sendMsgWithArgs( '/unsubscribe/slot', slot.id );
		}{
			this.sendMsgWithArgs( '/unsubscribe/slot', slot );
		}
	}

	getNode{ |node|
		if ( node.isKindOf( SWDataNode ) ){
			this.sendMsgWithArgs( '/get/node', node.id );
		}{
			this.sendMsgWithArgs( '/get/node', node );
		}
	}

	getSlot{ |slot|
		if ( slot.isKindOf( SWDataSlot ) ){
			this.sendMsgWithArgs( '/get/slot', slot.id );
		}{
			this.sendMsgWithArgs( '/get/node', slot );
		}
	}

	// ----------

	nodeInfo{ |msg|
		this.addExpected( msg[0], msg[1], fromnw: true );
		this.registerNode( msg[0], msg[2], msg[3] );
		//	"node info ".post; msg.postln;
		if ( msg[1] != 0,{
			this.add( msg[1], msg[0], true );
		});
		("DataNetwork: info node %, label %, % slots and type %".format( msg[0], msg[1], msg[2], msg[3] )).postln;
	}

	slotInfo{ |msg|
		//	"slot info ".post; msg.postln;
		if ( msg[2] !=  0,{
			this.add( msg[2], [msg[0],msg[1].asInteger], true );
		});
		("DataNetwork: info slot [%, %], label %".format( msg[0], msg[1], msg[2] )).postln;
	}

	nodeData{ |msg|
		this.setData( msg[0], msg.copyToEnd( 1 ), true );
	}

	slotData{ |msg|
		nodes.at( msg[0] ).slots.at( msg[1].asInteger ).value = msg[2];
		nodes.at( msg[0] ).setLastTime;
	}

	// ----

	unsubscribeNodeInfo{ |msg|
		("unsubscribed node"+msg).postln;
		subscriptions.remove( msg[2] );
		//		if ( gui.notNil ){ gui.setNodeSub( msg[1], 0 )};
		if ( gui.notNil ){ gui.subsetChanged = true };
	}

	unsubscribeSlotInfo{ |msg|
		("unsubscribed slot"+msg).postln;
		subscriptions.remove( [msg[2],msg[3]] );
		//		if ( gui.notNil ){ gui.setSlotSub( [msg[1],msg[2]], 0 )};
		if ( gui.notNil ){ gui.subsetChanged = true };
	}

	subscribeNodeInfo{ |msg|
		("subscribed node"+msg).postln;
		subscriptions.add( msg[2]);
		//		if ( gui.notNil ){ gui.setNodeSub( msg[1], 1 )};
		if ( gui.notNil ){ gui.subsetChanged = true };
	}

	subscribeSlotInfo{ |msg|
		("subscribed slot"+msg).postln;
		subscriptions.add( [msg[2],msg[3]] );
		//		if ( gui.notNil ){ gui.setSlotSub( [msg[1],msg[2]], 1 )};
		if ( gui.notNil ){ gui.subsetChanged = true };
	}

	clientInfo{ |msg|
		("client:"+msg).postln;
		if ( gui.notNil ){ gui.setInfo( "client:" + msg )};
	}

	setterInfo{ |msg|
		("setter of node: "+msg).postln;
		setters.add( msg[0]);
		if ( gui.notNil ){ 
			gui.setInfo( "setter of node:" + msg );
			gui.subsetChanged = true;
			//			gui.setSetter( msg[0] );
		};
	}

	// ---

	labelNode{ |node|
		this.sendMsgWithArgs( '/label/node', [node.id, node.key] );
	}

	labelSlot{ |slot|
		this.sendMsgWithArgs( '/label/slot', slot.id ++ slot.key );
	}

	//-------------

	sendData{ |id, data|
		this.sendMsgWithArgs( '/set/data', [id] ++ data );
		//	host.sendMsg( '/set/data', NetAddr.langPort, name.asString, id, *data );
	}


	sendPong{
		this.sendSimpleMsg( '/pong' );
		lasttime = Process.elapsedTime;
	}

	// -------

	makeGui{
		^SWDataNetworkClientGui.new( this );
	}

}