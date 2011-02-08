HIDResponder {

	classvar <openHIDDevices;

	classvar <allSlotResponders;
	classvar <all;

	var <>action,<>device,<>vendor,<>slot,<hwdev;

	*initClass{
		openHIDDevices = IdentityDictionary.new;
		all = List.new;
		allSlotResponders = IdentityDictionary.new;
	}

	*getOpenDevice{ |device,vendor|
		//	var dev;
		var devSym = this.makeDevVendorSymbol( device, vendor );
		if ( openHIDDevices[ devSym ].isNil ){
			this.openDevice( device, vendor );
		};
		^openHIDDevices[ devSym ];
		//^dev;
	}

	*makeDevVendorSymbol{ |device,vendor|
		^("dev" ++ device.asString ++ "_" ++ (vendor.asString) ).asSymbol
	}

	*getSlotArr{ |slot,device,vendor,dev|
		var slotarr;
		if ( slot.isKindOf( Symbol ) ){
			if ( dev.isNil ){
				dev = this.getOpenDevice( device, vendor );
			};
			slotarr = [ dev.at( slot ).type, dev.at( slot ).id ];
		}{
			slotarr = slot;
		};
		^slotarr;
	}

	*makeDVSlotSymbol{ |slot,device,vendor,dev|
		var slotarr = this.getSlotArr( slot,device,vendor,dev);
		^(device.asString ++ "_" ++ (vendor.asString) ++ "_" ++ slotarr[0] ++ "_" ++ slotarr[1] ).asSymbol;
	}

	*openDevice{ |device,vendor|
		var dev,spec,mydev;
		GeneralHID.buildDeviceList;
		mydev = GeneralHID.findBy( vendor, device );
		if ( mydev.notNil ){
			dev = GeneralHID.open( mydev );
			if ( dev.notNil ){
				// set spec if there is one:
				spec = dev.findSpec;
				if ( spec.notNil ){
				dev.setSpec( spec.first );
				};
				openHIDDevices.put( this.makeDevVendorSymbol( device, vendor ), dev );
			};
		};
		//	dev.device.closeAction_( { openHIDDevices.removeAt( this.makeDevVendorSymbol( device, vendor ) ); ("DEVICE WAS CLOSED" + dev.info ).warn; } );
	}

	*createNewSlotResponder{ |slot,device,vendor|
		var dev = this.getOpenDevice( device, vendor );
		var sdv = this.makeDVSlotSymbol( slot,device,vendor, dev );
		var slotarr = this.getSlotArr( slot,device,vendor,dev);
		if ( allSlotResponders[ sdv ].isNil ){
			allSlotResponders[ sdv ] = List.new;
			dev.slots[ slotarr[0] ][ slotarr[1]].action = { |myslot|
				allSlotResponders[ sdv ].do{ |it|
					it.value( myslot.value );
				};
			};
		};

	}
	
	*new{ |function,vendor,device,slot|
		^super.newCopyArgs(function,device,vendor,slot).init;
	}

	*addResponder{ |resp|
		var sdv = this.makeDVSlotSymbol( resp.slot, resp.device, resp.vendor, resp.hwdev );
		allSlotResponders[sdv].add( resp );
		all.add( resp );
	}

	*removeResponder{ |resp|
		var sdv = this.makeDVSlotSymbol( resp.slot, resp.device, resp.vendor, resp.hwdev );
		allSlotResponders[sdv].remove( resp );
	}

	init{ //|function,device,vendor,slot|
		hwdev = this.class.getOpenDevice( device, vendor );
		if ( hwdev.notNil ){
			this.class.createNewSlotResponder( slot, device, vendor );
			//	action = function;
			this.class.addResponder( this );
		}{
			("Could not open device" + device + vendor ).warn;
		}
	}

	value{ |v|
		action.value( v );
	}

	remove{
		this.class.removeResponder( this );
	}

	add{
		this.class.addResponder( this );
	}
	
}