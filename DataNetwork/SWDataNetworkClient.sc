SWDataNetworkClient : SWDataNetwork{

	var <host;
	var <myaddr;
	var responders;
	var <lasttime;

	var <subscriptions,<setters;
	
	var <>autoregister = true;
	// do not set unless you are the class itself
	var <>registered = false;

	*new{ |myip,hostip,reg=true|
		^super.new.init.myInit( myip,hostip, reg );
	}

	myInit{ |ip,hst,reg=true|
		lasttime = Process.elapsedTime;
		host = NetAddr( hst, NetAddr.langPort);
		this.findHost;
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
				this.removeNode( msg.copyToEnd( 1 ), true );
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
			}
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

	worryAboutTime{
		if ( Process.elapsedTime - lasttime > worrytime,
			{
				this.tryReconnect;
			});
	}

	/// OSC interface

	register{
		host.sendMsg( '/register', NetAddr.langPort );
	}

	unregister{
		host.sendMsg( '/unregister', NetAddr.langPort );
	}

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

	queryClients{
		host.sendMsg( '/query/clients', NetAddr.langPort );
	}

	querySubscriptions{
		host.sendMsg( '/query/subscriptions', NetAddr.langPort );
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

	labelNode{ |node|
		host.sendMsg( '/label/node', NetAddr.langPort, node.id, node.key );
	}

	labelSlot{ |slot|
		host.sendMsg( '/label/slot', NetAddr.langPort, slot.id[0], slot.id[1], slot.key );
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

	// overloaded from base class
	addExpected{ |id,label,size=nil,fromnw=false|
		if ( fromnw.not, {
			host.sendMsg( '/add/expected', NetAddr.langPort, id );
		});
		if ( this.isExpected( id ).not, {
			expectedNodes = expectedNodes.add( id );
		});
		if ( label.notNil, {
			this.add( label, id, fromnw );
		},{
			// maybe the label is already in the spec
			label = spec.findNode( id );
		});
		if ( size.notNil, {
			this.setData( id, Array.fill( size, 0 ), fromnw );
		});
	}

	sendPong{
		host.sendMsg( '/pong', NetAddr.langPort );
		lasttime = Process.elapsedTime;
	}

	nodeInfo{ |msg|
		this.addExpected( msg[0], msg[1], fromnw: true );
		this.registerNode( msg[0], msg[2] );
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
		/*		if ( gui.notNil ){

			};*/
	}

	// overloaded from base class
	removeNode{ |id,fromnw=false|
		if ( verbose > 1, { ("remove" + id).postln; });
		if ( fromnw.not )
		{ host.sendMsg( '/remove/node', NetAddr.langPort, id );}
		{
			nodes.removeAt( id );			
		};	
	}

	// overloaded from base class
	setData{ |id,data,fromnw=false|
		var ret = true;
		if ( verbose > 1, { [id,data].postln; } );
		if ( nodes[id].isNil, {
			ret = this.registerNode( id, data.size );
			if ( verbose > 0 ) { ("registering node"+id+ret).postln; };
		});
		if ( ret ) { 
			nodes[id].data = data;
			if ( fromnw.not, {
				this.sendData( id, data );
			});
		};
	}

	sendData{ |id, data|
		host.sendMsg( '/set/data', NetAddr.langPort, id, *data );
	}

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

	makeGui{
		^SWDataNetworkClientGui.new( this );
	}

}