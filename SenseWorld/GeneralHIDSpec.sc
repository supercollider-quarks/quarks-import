GeneralHIDSpec{
	classvar <>all;

	var <map, <device;

	*initClass { 
		// not yet used
		//all	= IdentityDictionary.new;
	}

	*new { |dev|
		^super.new.init(dev);
	}

	init{ |dev|
		device = dev;
		map = IdentityDictionary.new;
		
	}

	add{ |key, slot|
		map.put( key, slot );
	}
	
	// returns the slot
	at{ |key|
		var id1,id2;
		id1 = map.at(key)[0];
		id2 = map.at(key)[1];
		^device.slots[id1][id2];
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

	createBus{ |name,server|
		this.at( name ).createBus( server );
	}

	freeBus{ |name|
		this.at( name ).freeBus;
	}

	createAllBuses{ |server|
		this.freeAllBuses;
		map.do{ |it|
			device.slots.at( it[0] ).at( it[1] ).createBus( server );
		};
	}

	freeAllBuses{
		map.do{ |it|
			device.slots.at( it[0] ).at( it[1] ).freeBus;
		};
	}
}
