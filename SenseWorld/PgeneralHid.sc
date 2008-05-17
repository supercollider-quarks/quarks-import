
//human device interface pattern. pulls values from devices like gamepads etc.
// based on GeneralHID

PgeneralHid : Pattern {
	var <>slot,<>type,<>device;
	var <>repeats;
	var <>pSlot;

	// the Device list must have been built.
	*new { arg slot, type, device, repeats=inf;
		^super.newCopyArgs(slot, type, device, repeats)
	}
	storeArgs { ^[slot, type, device, repeats] }
	embedInStream { arg event;
		// var all, spec, elements, deviceName, min, max;
		// device.postln;
		if ( device.isKindOf( GeneralHIDDevice ).not,
			{
				try { device = GeneralHID.open( device ); }{ "device argument is not a GeneralHIDDevice".error; ^nil }
			});
		if ( device.slots[type].isNil,
			{ "slot type not found".error; ^nil });
		if ( device.slots[type][slot].isNil,
			{ "slot not found".error; ^nil });
		pSlot = device.slots[type][slot];
		/*		min = elements.at(element).min;
				max = elements.at(element).max;
				spec = ControlSpec.new(min, max, 'lin', 1)
			^if((min === 0) and: {max === 1}, {
					repeats.value.do({
						event = device.value(element).yield
					});
					^event
			},
			{
		*/
		repeats.value.do({
			event = pSlot.value.yield;
		});
		^event;
		//	})	
	}
}

