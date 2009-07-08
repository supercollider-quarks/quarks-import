SWDataNetworkClient : SWDataNetwork{

	var <host;
	var <myaddr;
	var responders;
	var <lasttime;

	var <>name;

	var <subscriptions,<setters;
	
	var <>autoregister = true;
	// do not set unless you are the class itself
	var <>registered = false;

	*new{ |hostip,name="",reg=true|
		^super.new.init.myInit( hostip,name, reg );
	}

	myInit{ |hst,nm="",reg=true|
		var ip,prefix;
		name = nm;
		lasttime = Process.elapsedTime;
		host = NetAddr( hst, NetAddr.langPort);

		this.findHost;

		if ( thisProcess.platform.name == \linux, {
			prefix = "/sbin/";
		},{ prefix = "" });

		ip = NetAddr.myIP(prefix);
		//		network = SWDataNetwork.new;
		myaddr = NetAddr( ip, NetAddr.langPort );

		subscriptions = Set.new;
		setters = Set.new;

		this.addResponders;

		if ( reg ){
			this.register;
		};

		watcher = SkipJack.new( { this.worryAboutTime }, 1, name: "SWDataNetworkClient", autostart: true );			

		ShutDown.add(
			{ 
				if ( this.registered ){ this.unregister; };
			}
		)

	}

	addResponders{
		responders = [
			OSCresponderNode( nil, '/datanetwork/announce', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.setHost( *(msg.copyToEnd( 1 )) );
			}),
			OSCresponderNode( host, '/datanetwork/quit', { |t,r,msg,addr|
				if ( verbose > 0, { msg.postln; });
				this.lostHost;
			}),
			OSCresponderNode( host, '/error', { |t,r,msg,addr|
				"Error: ".post; msg.postln;
				if ( gui.notNil ){ gui.setInfo( msg )};
			}),
			OSCresponderNode( host, '/warn', { |t,r,msg,addr|
				"Warning: ".post; msg.postln;
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
				this.addExpected( msg[1], msg[2], nil, true );				
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
			
			subscriptions.do{ |it|
				if ( it.isArray ){
					this.subscribeSlot( it );
				}{
					this.subscribeNode( it );
				}
			};
			setters.do{ |it|
				this.addExpected( it, spec.findNode( it ) );
			};
		});
	}

	// used to find the port on which the host is listening
	findHost{ |ip|
		var port;
		if ( ip.isNil, { ip = host.hostname });
		port = ("curl -s http://" ++ ip ++ "/SenseWorldDataNetwork").unixCmdGetStdOut.asInteger;
		host.port = port;
	}

	lostHost{
		"DataNetwork host has quit".postln;
		if ( gui.notNil ){ gui.setInfo( "DataNetwork host has quit" )};
	}

	tryReconnect{
		this.findHost;
		this.resetHost;
	}

	// ------------

	worryAboutTime{
		if ( Process.elapsedTime - lasttime > worrytime,
			{
				this.tryReconnect;
			});
	}

	// overloaded from base class
	addExpected{ |id,label,size=nil,fromnw=false|
		if ( fromnw.not, {
			host.sendMsg( '/add/expected', NetAddr.langPort, id, size, label );
		},{
			if ( this.isExpected( id ).not, {
				expectedNodes = expectedNodes.add( id );
			});
			if ( label.notNil and: (label.asSymbol != \0 ), {
				this.add( label, id, fromnw );
			},{
				// maybe the label is already in the spec
				label = spec.findNode( id );
			});
			if ( size.notNil, {
				this.setData( id, Array.fill( size, 0 ), fromnw );
			});
		});
	}

	// overloaded from base class
	setData{ |id,data,fromnw=false|
		var type;
		var ret = true;
		if ( verbose > 1, { [id,data].postln; } );
		if ( nodes[id].isNil, {
			type = this.checkDataType( data );
			ret = this.registerNode( id, data.size, type );
			if ( verbose > 0 ) { ("registering node"+id+ret).postln; };
		});
		if ( ret ) { 
			nodes[id].data = data;
			if ( fromnw.not, {
				this.sendData( id, data );
			});
		};
	}

	// overloaded from base class
	add{ |key, slot,fromnw=false|
		var ns;
		spec.add( key, slot );
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
		if ( fromnw.not )
		{ 
			host.sendMsg( '/remove/node', NetAddr.langPort, id.asInteger );}
		{
			nodes.removeAt( id.asInteger );			
		};	
	}


	/// OSC interface

	register{
		host.sendMsg( '/register', NetAddr.langPort, name );
	}

	unregister{
		host.sendMsg( '/unregister', NetAddr.langPort );
	}

	// Querying ---

	queryExpected{
		host.sendMsg( '/query/expected', NetAddr.langPort );
	}

	queryNodes{
		host.sendMsg( '/query/nodes', NetAddr.langPort );
	}

	querySlots{
		host.sendMsg( '/query/slots', NetAddr.langPort );
	}

	querySetters{
		host.sendMsg( '/query/setters', NetAddr.langPort );
	}

	querySubscriptions{
		host.sendMsg( '/query/subscriptions', NetAddr.langPort );
	}

	queryClients{
		host.sendMsg( '/query/clients', NetAddr.langPort );
	}


	// -- Subscribing --

	subscribeAll{ 
			host.sendMsg( '/subscribe/all', NetAddr.langPort );
	}

	unsubscribeAll{ 
			host.sendMsg( '/unsubscribe/all', NetAddr.langPort );
	}

	removeAll{ 
			host.sendMsg( '/remove/all', NetAddr.langPort );
	}

	subscribeNode{ |node|
		if ( node.isKindOf( SWDataNode ) ){
			host.sendMsg( '/subscribe/node', NetAddr.langPort, node.id );
		}{
			host.sendMsg( '/subscribe/node', NetAddr.langPort, node );
		}
	}

	unsubscribeNode{ |node|
		if ( node.isKindOf( SWDataNode ) ){
			host.sendMsg( '/unsubscribe/node', NetAddr.langPort, node.id );
		}{
			host.sendMsg( '/unsubscribe/node', NetAddr.langPort, node );
		}
	}

	subscribeSlot{ |slot|
		if ( slot.isKindOf( SWDataSlot ) ){
			host.sendMsg( '/subscribe/slot', NetAddr.langPort, slot.id[0], slot.id[1] );
		}{
			host.sendMsg( '/subscribe/slot', NetAddr.langPort, slot[0], slot[1] );
		}
	}

	unsubscribeSlot{ |slot|
		if ( slot.isKindOf( SWDataSlot ) ){
			host.sendMsg( '/unsubscribe/slot', NetAddr.langPort, slot.id[0], slot.id[1] );
		}{
			host.sendMsg( '/unsubscribe/slot', NetAddr.langPort, slot[0], slot[1] );
		}
	}

	getNode{ |node|
		if ( node.isKindOf( SWDataNode ) ){
			host.sendMsg( '/get/node', NetAddr.langPort, node.id );
		}{
			host.sendMsg( '/get/node', NetAddr.langPort, node );
		}
	}

	getSlot{ |slot|
		if ( slot.isKindOf( SWDataSlot ) ){
			host.sendMsg( '/get/slot', NetAddr.langPort, slot.id[0], slot.id[1] );
		}{
			host.sendMsg( '/get/slot', NetAddr.langPort, slot[0], slot[1] );
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
	}

	slotInfo{ |msg|
		//	"slot info ".post; msg.postln;
		if ( msg[2] !=  0,{
			this.add( msg[2], [msg[0],msg[1].asInteger], true );
		});
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
		subscriptions.remove( msg[1]);
		if ( gui.notNil ){ gui.setNodeSub( msg[1], 0 )};
	}

	unsubscribeSlotInfo{ |msg|
		("unsubscribed slot"+msg).postln;
		subscriptions.remove( [msg[1],msg[2]] );
		if ( gui.notNil ){ gui.setSlotSub( [msg[1],msg[2]], 0 )};
	}

	subscribeNodeInfo{ |msg|
		("subscribed node"+msg).postln;
		subscriptions.add( msg[1]);
		if ( gui.notNil ){ gui.setNodeSub( msg[1], 1 )};
	}

	subscribeSlotInfo{ |msg|
		("subscribed slot"+msg).postln;
		subscriptions.add( [msg[1],msg[2]] );
		if ( gui.notNil ){ gui.setSlotSub( [msg[1],msg[2]], 1 )};
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
			gui.setSetter( msg[0] );
		};
	}

	// ---

	labelNode{ |node|
		host.sendMsg( '/label/node', NetAddr.langPort, node.id, node.key );
	}

	labelSlot{ |slot|
		host.sendMsg( '/label/slot', NetAddr.langPort, slot.id[0], slot.id[1], slot.key );
	}

	//-------------

	sendData{ |id, data|
		host.sendMsg( '/set/data', NetAddr.langPort, id, *data );
	}


	sendPong{
		host.sendMsg( '/pong', NetAddr.langPort );
		lasttime = Process.elapsedTime;
	}

	// -------

	makeGui{
		^SWDataNetworkClientGui.new( this );
	}

}