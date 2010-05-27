SWMiniHiveConfig{

	classvar <>folder;

	var <>hive;

	var <>hiveMap;
	var <configLib;
	var <configLabels; // to derive configIDs from
	//	var <hiveConfigMap;
	//	var <hiveIdMap;
	//	var <hiveConfed;
	//	var <hiveStatus;

	var idAllocator;

	var <>gui;

	*new{
		^super.new.init;
	}

	makeGui{
		gui = SWMiniHiveConfigGui.new( this );
		^gui;
	}

	init{
		configLib = IdentityDictionary.new;     // labels -> SWMiniBeeConfigs
		configLabels = List.new; // empty list with config labels

		hiveMap = IdentityDictionary.new; // serial IDs -> SWMiniBeeID's

		/*
		hiveConfigMap = IdentityDictionary.new; // serial IDs -> config label
		hiveIdMap = IdentityDictionary.new;     // serial IDs -> node IDs
		hiveConfed = IdentityDictionary.new;    // serial IDs -> configured?
		hiveStatus = IdentityDictionary.new; // serial IDs -> status
		*/

		idAllocator = SWMBNumberAllocator.new(1,255);
	}

	// create a new bee
	newBee{ |serial|
		var mb;
		mb = SWMiniBeeID.new( serial );
		hiveMap.put( serial, mb );
		^mb
	}

	// get the node id for a serial number. Assigns a new number if necessary
	getNodeID{ |serial|
		var mb,id;
		mb = hiveMap.at( serial );
		if ( mb.isNil ){
			id = this.assignNodeID( serial );
		}{
			id = mb.nodeID;
			if ( id.isNil ){
				id = this.assignNodeID( serial, mb );
			};
		};
		^id;
	}

	assignNodeID{ |serial, mb|
		var id = idAllocator.alloc;
		if ( mb.isNil ){
			mb = this.newBee(serial);
		};
		mb.nodeID = id;
		^id;
	}

	// finds a serial number based on a certain property (must be instance method of SWMiniBeeID)
	findSerialForProperty { arg prop,val;
		hiveMap.keysValuesDo {|key, mb|
			if(mb.perform(prop) == val) {^key }
		};
		^nil
	}

	// get the serial number for a node id.
	getSerial{ |id|
		var serial;
		serial = this.findSerialForProperty( \nodeID, id );
		//	serial = hiveIdMap.findKeyForValue(id);
		^serial;
	}

	// assign a certain defined configuration to a serial number
	setConfig{ |configLabel,serial|
		hiveMap.at( serial.asSymbol ).configLabel_( configLabel ).configured_( 1 );
		hive.changeBee( this.getNodeID( serial ) );
		/*
		hiveConfigMap.put( serial.asSymbol, configLabel );
		hiveConfed.put( serial.asSymbol, 1 );
		*/
	}

	// set configuration status to a serial number
	setConfigured{ |serial,value=0|
		hiveMap.at( serial.asSymbol ).configured_( value );
		//	hiveConfed.put( serial.asSymbol, value );
	}

	// set status of a specific bee 
	setVersion{ |serial,rev,libv,caps|
		// TODO: if other revision or libversion than before, config has to be adapted!
		hiveMap.at( serial.asSymbol ).revision_( rev ).libversion_( libv ).libcaps_( caps );
		//		hiveStatus.put( serial.asSymbol, value );
	}

	// set status of a specific bee 
	setStatus{ |serial,value=0|
		hiveMap.at( serial.asSymbol ).status_( value );
		//		hiveStatus.put( serial.asSymbol, value );
	}

	getStatus{ |serial|
		// three states : 
		// - sent serial (0), 
		// - is waiting for config(1),
		// - is configured(2),
		// - is sending data(3),
		// - stopped sending data(4)
		// - inactive (not yet started) (5)
		^hiveMap.at( serial ).status;
	}

	// retrieve the configuration number for this serial number
	getConfigID{ |serial|
		var config,cid;
		config = hiveMap.at( serial ).configLabel;
		if ( config.isNil ){
			("no config known for this device!" + serial).postln;
		}{
			cid = this.getConfigIDLabel( config );
		}
		^cid;
	}

	// get the configuration message
	getConfigMsg{ |cid|
		var config;
		config = this.getConfig( cid );
		^([cid] ++ config.getConfigMsg);
	}

	// get the configuration itself
	getConfig{ |cid|
		var label,config;
		label = configLabels.at( cid - 1 );
		config = configLib.at( label );
		^config;
	}

	/// get the id of a certain configuration from its label
	getConfigIDLabel{ |label|
		^(configLabels.indexOf( label ) + 1); // offset of one
	}

	isConfigured{ |serial|
		// three states : 
		// - send new config(1), 
		// - do not send config(0),
		// - must define config(2)
		var config = hiveMap.at( serial ).configured;
		if ( config.isNil ){
			^2;
		};
		^config;
	}

	// add a new configuration to the library
	addConfig{ |config|
		config.parseConfig;
		if ( configLabels.select{ |it| it == config.label.asSymbol }.size == 0 ){
			configLabels.add( config.label.asSymbol );
		};
		configLib.put( config.label.asSymbol, config );
		if ( gui.notNil ){
			gui.updateMenu;
		}
	}

	save{ |name|
		var file;
		var thisf = folder ? thisProcess.platform.userAppSupportDir;
		name = name ? "SWMiniHiveConfig";
		file = File.open( thisf +/+ name, "w" );
		file.write( hiveMap.asCompileString );
		file.write( "\n");
		//		file.write( hiveConfed.asCompileString );
		//		file.write( "\n");
		//		file.write( hiveIdMap.asCompileString );
		//		file.write( "\n");
		file.write( configLabels.asCompileString );
		file.write( "\n");
		file.write( configLib.asCompileString );
		file.write( "\n");
		//	this.writeArchive( thisf +/+ name );
		file.close;
	}

	load{ |name|
		var file;
		var thisf = folder ? thisProcess.platform.userAppSupportDir;
		name = name ? "SWMiniHiveConfig";
		file = File.open( thisf +/+ name, "r" );
		hiveMap = file.getLine(4196*16).interpret;
		//		hiveConfed = file.getLine(4196).interpret;
		//		hiveIdMap = file.getLine(4196).interpret;
		configLabels = file.getLine(1024).interpret;
		configLib = file.getLine(4196*16).interpret;
		file.close;

		configLib.do{ |it| it.hive = this; it.parseConfig; };
		hiveMap.keysValuesDo{ |key,it|
			this.setStatus( key, 5 );
			idAllocator.allocID( it.nodeID );
		};
		//	^Object.readArchive( thisf +/+ name );
	}
}

SWMiniBeeID{

	var <>serial;
	var <>configLabel;
	var <>nodeID;
	var <>configured = 2;
	var <>status;
	var <>libversion;
	var <>libcaps;
	var <>revision;

	*new{ |ser,cl,nid,conf=2,st,libv,rev|
		^super.newCopyArgs( ser, cl, nid, conf, st, libv, rev );
	}

	storeOn { arg stream;
		stream << this.class.name << ".new(" << serial.asCompileString << "," << configLabel.asCompileString << "," << nodeID << "," << configured << "," << status << "," << libversion << "," << revision.asCompileString << "," << libcaps << ")"
	}

}

SWMiniBeeConfig{

	classvar <pinTypes;

	var <label;
	var <>msgInterval;
	var <>samplesPerMsg;
	var <>pinConfig;

	var <>hive;

	var <noInputs,<noOutputs;
	var <inputSize;
	//	var <noCustomOut;
	var <customSizes;


	*initClass{
		pinTypes = [
			\unconfigured,
			\digitalIn, \digitalOut, 
			\analogIn, \analogOut, \analogIn10bit,
			\SHTClock, \SHTData,
			\TWIClock, \TWIData,
			\Ping,
			\CustomIn, \CustomOut
		];
	}

	*getPinCID{ |name|
		^pinTypes.indexOf( name );
	}

	*getPinCaps{ |label|
		var caps;
		caps = switch( label,
			\SDA_A4, { this.filterPinTypes( [\TWIClock, \analogOut ]); },
			\SCL_A5, { this.filterPinTypes( [\TWIData, \analogOut ]); },
			\A0, { this.filterPinTypes( [\TWIData, \TWIClock, \analogOut ]); },
			\A1, { this.filterPinTypes( [\TWIData, \TWIClock, \analogOut ]); },
			\A2, { this.filterPinTypes( [\TWIData, \TWIClock, \analogOut ]); },
			\A3, { this.filterPinTypes( [\TWIData, \TWIClock, \analogOut ]); },
			\A6, { this.filterPinTypes( [\TWIData, \TWIClock, \analogOut ]); },
			\A7, { this.filterPinTypes( [\TWIData, \TWIClock, \analogOut ]); },
			\D3, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit ]); },
			\D4, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit, \analogOut ]); },
			\D5, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit ]); },
			\D6, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit ]); },
			\D7, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit, \analogOut ]); },
			\D8, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit, \analogOut ]); },
			\D9, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit ]); },
			\D10, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit ]); },
			\D11, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit ]); },
			\D12, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit, \analogOut ]); },
			\D13, { this.filterPinTypes( [\TWIData, \TWIClock, \analogIn, \analogIn10bit, \analogOut ]); }
			);
		^caps;
	}

	*filterPinTypes{ |filters|
		var caps = pinTypes;
		filters.do{ |it|
			caps = caps.reject{ |jt| jt == it };
		};
		//	caps.postln;
		^caps;
	}
	
	*new{
		^super.new.init;
	}

	*newFrom{ |label,msgInt, smp, pinC|
		^super.newCopyArgs( label, msgInt, smp, pinC );
	}

	from{ |conf|
		label = conf.label;
		msgInterval = conf.msgInterval;
		samplesPerMsg = conf.samplesPerMsg;
		pinConfig = conf.pinConfig;

		noInputs = conf.noInputs;
		noOutputs = conf.noOutputs;
		inputSize = conf.inputSize;
		this.parseConfig;
	}

	addCustom{ |custom|
		var customPins;
		//	customSizes = [];
		customSizes = Array.fill( custom[0], custom[1] );
		customPins = custom.clump(2).copyToEnd(1);
		customPins.do{ |it,i|
			if ( it[1] > 0 ){
				pinConfig[ it[0] ] = \CustomIn;
				noInputs = noInputs + 1;
				customSizes = customSizes ++ it[1];
			}{
				pinConfig[ it[0] ] = \CustomOut;
			};
		}
	}

	storeOn { arg stream;
		stream << this.class.name << ".newFrom(" << label.asCompileString << "," << msgInterval << "," << samplesPerMsg << "," << pinConfig.asCompileString << ")"
	}

	init{
		pinConfig = Array.fill( 19, { \unconfigured } );
		customSizes = [];
	}

	label_{ |lb|
		label = lb.asSymbol;
		if ( hive.notNil){
			hive.addConfig( this.deepCopy; );
		};
	}

	getDataFunc{
		var analog,digital;
		var dataFunc;
		// order of data is: custom, analog in (8 or 10 bit), digital, twi, sht, ping
		var sizes = customSizes;
		var scales = Array.fill( customSizes.size, 1 );

		analog = pinConfig.select{ |it| (it == \analogIn) or: (it == \analogIn10bit) };
		analog.do{ |it|
			if ( it == \analogIn ){
				sizes = sizes.add( 1 );
				scales = scales.add( 255 );
			}{
				sizes = sizes.add( 2 );
				scales = scales.add( 1023 );
			}
		};

		digital = pinConfig.select{ |it| (it == \digitalIn) };
		digital.do{ |it|
				sizes = sizes.add( 1 );
				scales = scales.add( 1 );
		};

		if ( pinConfig.includes( \TWIData ) ){
			sizes = sizes ++ [1,1,1];
			scales = scales ++ [255,255,255];
		};

		if ( pinConfig.includes( \SHTData ) ){
			sizes = sizes ++ [2,2];
			scales = scales ++ [1,1];
		};

		if ( pinConfig.includes( \Ping ) ){
			sizes = sizes.add( 2 );
			scales = scales ++ [20000];
		};

		dataFunc = { |indata|
			indata = indata.clumps( sizes );
			indata.collect{ |it,i|
				if ( it.size == 2 ){
					(it * [256,1]).sum / scales[i]; // 10 bit; may not be correct for ping
				}{
					it/ scales[i];
				};
			}.flatten;
		}
		^dataFunc;
	}

	parseConfig{
		noInputs = 0;
		noOutputs = 0;
		inputSize = 0;
		pinConfig.do{ |it|
			switch( it,
				\analogIn, { noInputs = noInputs + 1; inputSize = inputSize + 1; },
				\analogIn10bit, { noInputs = noInputs + 1; inputSize = inputSize + 2; },
				\digitalIn, { noInputs = noInputs + 1; inputSize = inputSize + 1; },
				\analogOut, { noOutputs = noOutputs + 1; },
				\digitalOut, { noOutputs = noOutputs + 1; },
				\SHTData, { noInputs = noInputs + 2; inputSize = inputSize + 4; },
				\TWIData, { noInputs = noInputs + 3; inputSize = inputSize + 3; },
				\Ping, { noInputs = noInputs + 1; }
			)
		}
	}

	getConfigMsg{
		var pins,mint;
		// config has things like:
		// noInputs
		// samplesPerMsg
		// msgInterval
		// scale
		// pins
		pins = pinConfig.collect{ |it| SWMiniBeeConfig.getPinCID( it ) }.replace( 0, 200 );
		mint = [ (msgInterval / 256).floor.asInteger, (msgInterval%256).asInteger ];
		^( mint ++ samplesPerMsg ++ pins );
	}

	makeGui{
		^SWMiniBeeConfigGui.new( this );
	}

	checkConfig{
		// check if SHTData is matched by SHTClock, and vice versa
		// check if TWIData is matched by TWIClock, and vice versa
		var hasSHTData, hasSHTClock, shtOk = true;
		var hasTWIData, hasTWIClock, twiOk = true;
		var configStatus = "";

		hasSHTClock = pinConfig.select{ |it| it == \SHTClock };
		if ( hasSHTClock.size == 1 ){
			shtOk = false;
			hasSHTData = pinConfig.select{ |it| it == \SHTData };
			if ( hasSHTData.size == 1 ){
				shtOk = true;
			}{
				if ( hasSHTData.size > 1 ){
					configStatus = configStatus ++ "Err: More than one SHTData pin defined!"

				}{
					configStatus = configStatus ++ "Err: No SHTData pin defined!"
				}
			}
		}{
			if ( hasSHTClock.size == 0 ){
				// check for data pin
				hasSHTData = pinConfig.select{ |it| it == \SHTData };
				if ( hasSHTData.size > 0 ){
					shtOk = false;
					configStatus = configStatus ++ "Err: No SHTClock pin defined!"
				}
			}{
				// more than one!
				shtOk = false;
				configStatus = configStatus ++ "Err: more than one SHTClock pin defined!"
			};
		};


		hasTWIClock = pinConfig.select{ |it| it == \TWIClock };
		if ( hasTWIClock.size == 1 ){
			twiOk = false;
			hasTWIData = pinConfig.select{ |it| it == \TWIData };
			if ( hasTWIData.size == 1 ){
				twiOk = true;
			}{
				if ( hasTWIData.size > 1 ){
					configStatus = configStatus ++ "Err: More than one TWIData pin defined!"

				}{
					configStatus = configStatus ++ "Err: No TWIData pin defined!"
				}
			}
		}{
			if ( hasTWIClock.size == 0 ){
				// check for data pin
				hasTWIData = pinConfig.select{ |it| it == \TWIData };
				if ( hasTWIData.size > 0 ){
					twiOk = false;
					configStatus = configStatus ++ "Err: No TWIClock pin defined!"
				}
			}{
				// more than one!
				twiOk = false;
				configStatus = configStatus ++ "Err: more than one TWIClock pin defined!"
			};
		};
		configStatus.postln;
		^[ twiOk, shtOk, configStatus ];
	}


}

SWMiniBeeConfigGui{
	var <config;

	var w;
	var left,right,top; //,bottom;

	var <menu;
	var label,store,send;
	var <leftpins, <rightpins;
	var <status,<check;
	var noInputs;

	var msgInt, smpMsg;

	*new{ |config|
		^super.new.init( config );
	}

	init{ |conf|
		w = Window.new("MiniBee Configuration", Rect( 0, 0, 430, 350 ));
		
		top = CompositeView.new( w, Rect( 0,0, 430, 90 ));
		top.addFlowLayout;


		menu = PopUpMenu.new( top, 150@20 );
		label = TextField.new( top, 205@25 ).focusLostAction_( { arg field; config.label = field.value } ).action_( { arg field; config.label = field.value } );
		store = Button.new( top, 50@25 ).states_( [[ "store"]]).action_({ 
			this.storeConfig;
		});
		//		send = Button.new( top, 50@25 ).states_( [[ "send"]]).action_({ "sending config".postln; });

		msgInt = EZNumber.new( top, 160@20, "delta T (ms)", [5,100000,\exponential,1].asSpec, { |g| config.msgInterval = g.value; }, 50, labelWidth: 80 );
		smpMsg = EZNumber.new( top, 130@20, "samples/msg", [1,20,\linear,1].asSpec, { |g| config.samplesPerMsg = g.value; }, 1, labelWidth: 90 );
		//		noInputs = EZNumber.new( top, 80@20, "#in", labelWidth:40 );

		//		bottom = CompositeView( w, Rect( 0, 320, 430, 30 ) );

		top.decorator.nextLine;

		check = Button.new( top, 50@25 ).states_( [["check"]]).action_({ this.checkConfig; });
		status = StaticText.new( top, 366@25 );

		
		left = CompositeView( w,  Rect(0,  90, 215, 260) );
		right = CompositeView( w, Rect(215,90, 215, 260) );


		left.addFlowLayout(2@2,2@2);
		right.addFlowLayout(2@2,2@2);
		//		bottom.addFlowLayout(2@2,2@2);

		StaticText.new( left, 180@20 ); // spacer

		leftpins = [ \SDA_A4, \SCL_A5, \A0, \A1, \A2, \A3, \A6, \A7 ].collect{ |it|
			this.createPin( it, left );
		};

		leftpins[0][1].action = { |b| 
			if ( b.items.at( b.value ) == \TWIData ) { 
				leftpins[1][1].value = SWMiniBeeConfig.getPinCaps( \SCL_A5 ).indexOf( \TWIClock );
			};
			status.string_( "" );
		};
		leftpins[1][1].action = { |b| 
			if ( b.items.at( b.value ) == \TWIClock ) { 
				leftpins[0][1].value = SWMiniBeeConfig.getPinCaps( \SDA_A4 ).indexOf( \TWIData );
			};
			status.string_( "" );
		};

		rightpins = (13..3).collect{ |it|
			this.createPin( ("D"++it).asSymbol, right );
		};

		w.front;

		if ( conf.notNil ){
			this.config = conf;
		}{
			this.config = SWMiniBeeConfig.new;
		};
		this.updateMenu;
	}

	config_{ |conf,hconf|
		// set all values to given config
		config = conf;
		if ( hconf.notNil ){
			config.hive = hconf;
		};
		this.updateGui;
	}

	updateGui{
		var rpins,lpins;
		label.string_( config.label.asString );

		rpins = config.pinConfig.copyRange( 0, 10 ).reverse;
		lpins = config.pinConfig.copyRange( 11, 18 ).at( [4,5, 0,1,2,3, 6,7]);
		rightpins.do{ |it,i|
			it[1].value_( it[1].items.indexOf( rpins[i] ) );
		};
		leftpins.do{ |it,i|
			it[1].value_( it[1].items.indexOf( lpins[i] ) );
		};
	}

	checkConfig{
		var res;
		// checks validity of config and indicates errors if any
		this.getConfig;
		res = config.checkConfig;
		res.postln;
		status.string_( res[2] );
		w.refresh;
		// make wrong ones red
		if ( res[0] && res[1] ){
			status.string_( "configuration valid" );
		}
	}

	storeConfig{
		// check the label, and put config under label in hiveconfig
		//	config.label = label.string;
		this.checkConfig;
		this.updateMenu;
	}

	updateMenu{
		if ( config.hive.notNil ){
			menu.items_(  
				[ "*new*" ] ++ config.hive.configLabels).action_({ |men|
					if ( men.value > 0 ){
						this.config_( 
							config.hive.getConfig( men.value ).deepCopy,
							config.hive );
					}
				}); // choice of configs
		};
	}

	getConfig{
		// reads the gui status for the config
		config.msgInterval = msgInt.value.asInteger;
		config.samplesPerMsg = smpMsg.value.asInteger;
		config.pinConfig = this.getPinVals;
	}

	createPin{ |label,parent|
		^[
			StaticText.new( parent, 50@20 ).string_( label ).align_( \right ),
			PopUpMenu.new( parent, 150@20 ).items_( 
				SWMiniBeeConfig.getPinCaps( label );
			).action_( { status.string_( "" ); });
		]
	}

	getPinVals{
		^(
			rightpins.collect{ |it| it[1].items.at( it[1].value) }.reverse ++
			leftpins.collect{ |it| it[1].items.at( it[1].value) }.at( [2,3,4,5, 0,1, 6,7])
		)
	}

}

SWMiniHiveConfigGui{

	var w,view,hview;
	var <confs;
	var <header;
	var <save;
	var <hiveConf;

	var <configEdit;

	*new{ |hc|
		^super.new.init(hc);
	}

	init{ |hc|
		hiveConf = hc;
		
		w = Window.new("MiniHive Configuration", Rect( 0, 400, 500, 375 ));
		//	
		
		hview = CompositeView.new(w, Rect( 0,0, 500, 50));
		hview.addFlowLayout(2@2);

		save = [
			TextField.new( hview, 325@20 ),
			Button.new( hview, 60@20 ).states_( [ ["save"] ]).action_( { |b|
				hiveConf.save( this.getSaveString );
			}),
			Button.new( hview, 60@20 ).states_( [ ["load"] ]).action_( { |b|
				hiveConf.load( this.getSaveString );
				this.updateGui;
			})
		];

		header = [
			StaticText.new( hview, 130@20 ).string_("serial number").align_( \center ), // serial number
			StaticText.new( hview, 60@20 ).string_( "node ID" ).align_( \center ), // node ID
			StaticText.new( hview, 150@20 ).string_( "configuration" ).align_( \center ), // choice of configs
			StaticText.new( hview, 40@20 ).string_( "status" ).align_( \center ), // active/not active
			StaticText.new( hview, 80@20 ).string_( "send config").align_( \center ), // send config
		];

		view = ScrollView.new( w, Rect( 0,50, 500, 310 ));
		view.addFlowLayout(2@2);

		confs = List.new;

		configEdit = SWMiniBeeConfigGui.new;
		configEdit.config.hive = hiveConf;

		this.updateGui;
		w.front;
	}

	getSaveString{
		var str = save[0].string;
		if ( str.isNil ){ ^str };
		if ( str.size == 0 ){ ^nil }{ ^str };
	}

	addLine{ |key|
				
		confs.add([
			StaticText.new( view, 130@20 ), // serial number
			StaticText.new( view, 60@20 ).align_('center'), // node ID
			PopUpMenu.new( view, 150@20 ).items_(  [ "*new*" ] ++ hiveConf.configLabels).action_({ |men|
				//	men.value.postln;
				if ( men.value > 0 ){
					configEdit.config_( 
						hiveConf.getConfig( men.value ).deepCopy,
						hiveConf );
					hiveConf.setConfig( men.items[men.value].asSymbol, key );
				}
			}), // choice of configs
			Button.new( view, 40@20 ).states_( [
				['s',Color.black, Color.blue], // (0) has sent serial
				['w',Color.black, Color.yellow], // (1) is waiting for config
				['c',Color.black, Color.green], // (2) has confirmed config
				['d',Color.black, Color.green], // (3) is sending data
				['x', Color.black, Color.red ], // (4) stopped sending data
				['i', Color.black, Color.white ] // (5) inactive
			]), // active/not active
			//			Button.new( view, 60@20 ).states_( [['known'],['send'],['define']]), // send config
			Button.new( view, 60@20 ).states_( [['send']]).action_({
				hiveConf.hive.sendID( key );
			}), // send config
		]);
	}

	updateLine{ |key|
		var configID,status;
		confs.last[0].string_( key );
		confs.last[1].string_( hiveConf.getNodeID( key ) );

		configID = hiveConf.getConfigID( key );
		if ( configID.notNil ){
			confs.last[2].value_( configID );
		}{
			confs.last[2].value_( 0 );
		};

		// set status
		confs.last[3].value_( hiveConf.getStatus( key ) );
	}

	updateMenu{
		confs.do{ |it|
			it[2].items_( [ "*new*" ] ++ hiveConf.configLabels);
		};
	}

	updateGui{
		defer{
			view.removeAll;
			view.decorator.reset;
			confs = List.new;
		
			hiveConf.hiveMap.keys.do{ |key,i|
				this.addLine(key);
				this.updateLine( key );
			}
		}
	}
}