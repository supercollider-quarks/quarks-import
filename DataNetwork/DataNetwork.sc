// abstracting classes from the XBeeNetwork to a general DataNetwork
// part of SenseWorld (prefix SW)
// developed for the SenseStage project

SWDataNetwork{
	var <spec;
	var <nodes;
	var <>verbose=0; // 1 is warning, 2 is all;

	var <>osc;

	var <>expectedNodes;
	//	var <>expectedSize;
	var <watcher;
	var <worrytime = 60; // time after which to worry whether node is still active
	var <>gui;
	var <>recTask;
	var <logfile;

	*new{ 
		^super.new.init;
	}

	init{
		expectedNodes = Array.new;
		//		expectedSize = IdentityDictionary.new;
		nodes = IdentityDictionary.new;
		spec = SWDataNetworkSpec.new( this );
		watcher = SkipJack.new(
			{
				var now = Process.elapsedTime;
				nodes.do{ |it,i| 
					if ( it.elapsed > worrytime,
						{
							if ( verbose > 0, { "restarting network".postln; });
							it.restartAction.value;
						});
				};
			}, worrytime/10, name: "DataNetwork-watcher", autostart: false );
		recTask = Task.new( {} );
		this.watch( false );
	}

	removeNode{ |name|
		nodes.removeAt( name );
		if ( osc.notNil, {
			osc.nodeRemoved( name );
		});
	}

	isExpected{ |id|
		^expectedNodes.indexOf( id ).notNil;
	}

	addExpected{ |id,label,size=nil|
		if ( this.isExpected( id ).not, {
			expectedNodes = expectedNodes.add( id );
		});
		if ( label.notNil, {
			this.add( label, id );
		},{
			// maybe the label is already in the spec
			label = spec.findNode( id );
		});
		if ( osc.notNil, {
			osc.newExpected( id, label );
		});
		if ( size.notNil, {
			this.setData( id, Array.fill( size, 0 ) );
		})
	}

	registerNode{ |id,sz|
		var ret,key;
		ret = (sz > 0) and: (expectedNodes.indexOf( id ).notNil);
		if ( ret ) {
			nodes.put( id, SWDataNode.new( id,sz ) );
			if ( osc.notNil, {
				osc.newNode( nodes[id] );
			});
			if ( gui.notNil, {
				gui.addNode( nodes[id] );
			});
			key = spec.findNode( id );
			if ( key.notNil, {this.at( key ).key = key; });
			sz.do{ |it| 
				key = spec.findSlot( id, it );
				if ( key.notNil, {this.at( key ).key = key; });
			};
		}{
			if ( verbose > 0 , {("node with id"+id+"and size"+sz+"is not expected to be part of the network" ).postln;});
		};
		^ret;
	}

	worrytime_{ |wt|
		worrytime = wt;
		watcher.dt = wt/10;
	}

	watch{ |onoff=true|
		if ( onoff, { watcher.start }, { watcher.stop; } );
	}

	createAllNodeBuses{
		nodes.do{ |it| it.createBus };
	}

	debug_{ |onoff|
		nodes.do{ |sl|
			sl.do{ |slt| slt.debug_( onoff ) } };
	}

	setData{ |id,data|
		var ret = true;
		if ( verbose > 1, { [id,data].postln; } );
		if ( nodes[id].isNil, {
			ret = this.registerNode( id, data.size );
			if ( verbose > 0 ) { ("registering node"+id+ret).postln; };
		});
		if ( ret ) { 
			nodes[id].data = data;
			if ( osc.notNil, {
				osc.sendData( id, data );
			});
		};
	}

	/* This should not be necessary:
	setDataSlot{ |id,id2,value|
		var ret = true;
		if ( verbose > 1, { [id,id2,value].postln; } );
		if ( nodes[id].isNil, {
			ret = false;
			if ( verbose > 0 ) { ("node"+id+"does not exist").postln; };
		});
		if ( ret ) { 
			nodes[id].dataSlot_( id2, value );
			if ( osc.notNil, {
				osc.sendData( id, nodes[id].data );
			});
		};
	}
	*/

	// direct access to spec:

	add{ |key, slot|
		var ns;
		spec.add( key, slot );
		if ( osc.notNil, {
			ns = this.at( key );
			//	ns.postln;
			if ( ns.isKindOf( SWDataNode ),{
				osc.newNode( ns );
			});
			if ( ns.isKindOf( SWDataSlot ),{
				osc.newSlot( ns );
			});
		});
	}

	at{ |key|
		^spec.at( key );
	}

	value{ |key|
		^spec.value( key );
	}

	bus{ |key|
		^spec.bus( key );
	}

	value_{ |key,value|
		spec.value_( key, value );
	}

	action_{ |key,action|
		spec.action_( key, action );
	}

	createBus{ |key,server|
		spec.createBus( key, server );
	}

	freeBus{ |key|
		spec.freeBus( key );
	}

	createAllBuses{ |server|
		spec.createAllBuses( server );
	}

	freeAllBuses{
		spec.freeAllBuses;
	}

	setSpec{ |name|
		spec.fromFile( name );
	}

	// recording
	initRecord{ |fn,dt=0.005|
		var recordnodes;
		fn = fn ? "SWDataNetworkLog";
		logfile =  File(fn++"_"++Date.localtime.stamp++".txt", "w");
		
		recordnodes = this.writeHeader;

		recTask = Task.new( {
			loop {
				this.writeLine( dt, recordnodes );
				dt.wait;
			}
		});
	}

	record{ |onoff|
		if ( onoff ) {
			recTask.reset.play;
		}{
			recTask.stop;
		};
	}

	closeRecord{
		this.record( false );
		logfile.close;
		recTask = Task.new( {} );
	}

	writeHeader{
		var recordnodes;
		// this tells the spec used for the recording:
		logfile.write( spec.name.asString );
		logfile.write( "\n" );

		logfile.write( "time\t" );
		// this creates a header with the ids of the node slots
		recordnodes = nodes.collect{ |node|
			node.slots.do{ |it| logfile.write( it.id.asCompileString ); logfile.write( "\t" ); };
			node.id;
		};
		logfile.write( "\n" );
		^recordnodes;
	}

	writeLine{ |dt,recordnodes|
		logfile.write( dt.asString );
		logfile.write( "\t" );
		recordnodes.do{ |it|
			nodes[it].slots.collect{ |slot| slot.value }.do{ |dat|
				logfile.write( dat.asCompileString );
				logfile.write( "\t" );
			}; 
		};
		logfile.write( "\n" );
	}

	makeGui{
		^SWDataNetworkGui.new( this );
	}

	addOSCInterface{
		^SWDataNetworkOSC.new( this );
	}

}


// a DataNode are a collection of slots which are physically connected to each other, e.g. data gathered from the same device.
SWDataNode{
	var <slots;
	var <>databus;
	var <id;
	var <lasttime;
	var <>restartAction;
	var <>key;

	var <>action;

	//	var <>trigger;

	var <>scale = 1;

	var <data;

	var >bus;

	// monitoring support
	var <busmonitor;

	*new{ |id,maxs=4|
		^super.new.init(id,maxs);
	}
	
	init{ |ident,maxs|
		id = ident;
		lasttime = 0;
		slots = Array.fill( maxs, 0 );
		data = Array.fill( maxs, 0 );
		slots.do{ |it,i| slots.put( i, SWDataSlot.new([id,i]) ); };
		// the restart action should contain what should be done if the node does not provide data anymore
		restartAction = {};
		action = {};
		lasttime = Process.elapsedTime;
		//		trigger = {};
	}

	elapsed{
		^(Process.elapsedTime - lasttime );
	}

	createBus{ |s|
		if ( bus.isNil, {
			s = s ? Server.default;
			databus = DataBus.new( { slots.collect{ |it| it.value } }, slots.size, s )
		});
	}

	bus{
		if ( bus.notNil, { ^bus } );
		if ( databus.isNil, { ^nil } );
		^databus.bus;
	}

	freeBus{
		if ( bus.notNil, { bus.free; },
			{
			if ( databus.notNil, { databus.free;});
			});
	}

	debug_{ |onoff|
		slots.do{ |sl|
			sl.do{ |slt| slt.debug_( onoff ) } };
	}

	value{
		^data;
	}

	data_{ |indata|
		if ( indata.size == slots.size , {
			data = indata * scale;
			data.do{ |it,i| slots[i].value = it };
			lasttime = Process.elapsedTime;
			action.value( data );
			//	trigger.value;
		});
		//		indata.copyRange(0,data.size-1).do{ |it,i| data[i].value = it };
	}

	/*
	dataSlot_{ |id,indata|
		if( id < slots.size ){
			data[id] = indata * scale;
			slots[id].value = data[id];
			lasttime = Process.elapsedTime;
			action.value( data );
		};
	}
	*/

	monitor{ |onoff=true|
		if ( onoff, {
			if ( busmonitor.isNil, { 
				if ( bus.isNil, { this.createBus } );
				busmonitor = BusMonitor.new( this.bus );
			});
			busmonitor.start;
		}, { busmonitor.stop; });
	}
}

SWDataSlot{
	var <value;
	var <bus;
	var <>action;
	var <>key;
	var debugAction;
	var <>id;
	var <>scale=1;
	var <map;
	var <range;

	// monitoring support
	var <busmonitor;

	*new{ |id|
		^super.new.init(id);
	}

	init{ |ident|
		id = ident;
		action = {};
		debugAction = {};
		value = 0;
	}

	map_{ |mp|
		if( range.isNil){
			// input range after scaling:
			range = [0,1].asSpec;
		};
		map = mp;
	}

	range_{ |mp|
		if( map.isNil){
			// input range after scaling:
			map = [0,1].asSpec;
		};
		range = mp;
	}

	createBus{ |s|
		s = s ? Server.default;
		if ( bus.isNil, {
			bus = Bus.control( s, 1 );
		},{
			if ( bus.index.isNil, {
				bus = Bus.control( s, 1 );
			});
		});
	}

	freeBus{
		bus.free;
		bus = nil;
	}

// JITLib support
	kr{
		this.createBus;
		^In.kr( bus );
	}

	value_{ |val|
		value = val * scale;
		// map to control spec from input range after scaling
		if ( map.notNil, { value = map.map( range.unmap( value ) ) } );
		action.value( value );
		debugAction.value( value );
		if ( bus.notNil, { bus.set( value ) } );
	}

	debug_{ |onoff|
		if ( onoff, {
			debugAction = { |val| [ id, value, key ].postln; };
		},{
			debugAction = {};
		});
	}

	// currently only does minimum:
	calibrate{ |steps=100| // about two seconds currently
		var calib,values;
		values = Array.new( steps );
		range = [0,1].asSpec;
		calib = Routine{ 
			var mean;
			steps.do{ |it,i| values.add( this.value ); it.yield; };
			mean = values.sum / values.size;
			range.minval = mean;
			this.debug_( false );
			"calibration done".postln;
		};
		debugAction = { calib.next };
	}

	monitor{ |onoff=true|
		if ( onoff, {
			if ( busmonitor.isNil, { 
				this.createBus;
				busmonitor = BusMonitor.new( this.bus );
			});
			busmonitor.start;
		}, { busmonitor.stop; });
	}
}



SWDataNetworkSpec{
	classvar <>all,<folder;

	var <>name;
	var <map, network;

	*initClass { 
		// not yet used
		this.makeSaveFolder;
		this.loadSavedInfo;
		all	= all ? Set.new;
	}

	*loadSavedInfo{
		all = (folder+/+"allspecs.info").load;
	}
	
	*makeSaveFolder { 
		var testfile, testname = "zzz_datanetwork_test_delete_me.txt"; 
		folder = (Platform.userAppSupportDir +/+ "DataNetworkSpecs").standardizePath;
		testfile = File(folder +/+ testname, "w");

		if (testfile.isOpen.not) 
			{ unixCmd("mkdir" + folder.escapeChar($ )) }
			{ testfile.close;  unixCmd("rm" + folder.escapeChar($ ) +/+ testname) }
	}

	*new { |netw|
		^super.new.init(netw);
	}

	init{ |netw|
		network = netw;
		map = IdentityDictionary.new;
	}

	add{ |key, slot|
		map.put( key, slot );
		if ( this.at( key ).notNil, {
			this.at( key ).key = key;
		});
	}

	findNode{ |id|
		^map.findKeyForValue( id );
	}

	findSlot{ |id1,id2|
		var keySlot = nil;
		map.keysValuesDo{ |key,val| if ( val == [id1,id2] ) { keySlot = key } };
		^keySlot;
		//		^map.findKeyForValue( [id1,id2] );
	}

	// returns the slot or node
	at{ |key|
		var id1,id2;
		var item;
		item = map.at( key );
		if ( item.isKindOf( Array ), {
			id1 = map.at(key)[0];
			id2 = map.at(key)[1];
			if ( network.nodes[id1].isNil, { ^nil } );
			^network.nodes[id1].slots[id2];
		},{
			^network.nodes[item]
		});
		// map.at(key)
	}

	value{ |key|
		^this.at(key).value;
	}

	value_{ |key,value|
		var slot;
		slot = this.at(key);
		slot.value_(value);
		^slot;
	}

	action_{ |key,action|
		var slot;
		slot = this.at(key);
		slot.action_(action);
		^slot;		
	}

	bus{ |key|
		^this.at(key).bus;
	}

	createBus{ |key,server|
		this.at( key ).createBus( server );
	}

	freeBus{ |key|
		this.at( key ).freeBus;
	}

	/*	setAllActions{ |action|
		map.do{ |it|
			device.slots.at( it[0] ).at( it[1] ).action_( action );
		};
		}*/

	createAllBuses{ |server|
		map.do{ |it|
			network.nodes.at( it[0] ).slots.at( it[1] ).createBus( server );
		};
	}

	freeAllBuses{
		map.do{ |it|
			network.nodes.at( it[0] ).slots.at( it[1] ).freeBus;
		};
	}

	save{ |name|
		var file, res = false;
		var filename;
		all.add( name.asSymbol );
		this.name = name;
		filename = folder +/+ name ++ ".spec";
		file = File(filename, "w"); 
		if (file.isOpen) { 
			res = file.write(map.asCompileString);
			file.close;
		};
		this.class.saveAll;
		^res;
	}

	*saveAll{
		var file, res = false;
		var filename;
		filename = folder +/+ "allspecs.info";
		file = File(filename, "w"); 
		if (file.isOpen) { 
			res = file.write(all.asCompileString);
			file.close;
		};
		^res;
	}

	fromFile { |name| 
		var slot;
		this.name = name;
		map = (folder +/+ name++".spec").load;
		map.keysValuesDo{ |key,it|
			slot = this.at( key );
			if ( slot.notNil, { slot.key = key; } );
		}
	} 

}
