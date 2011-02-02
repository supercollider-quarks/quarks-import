Ktl { 

	// in this variable all subclasses register what they can handle
	classvar <defaults; 
	
	var <srcID; // (port in MIDIKtl, for HID can be the findBy parameters)
	var <destID; //  (port in MIDIKtl )
	var <ktlDict; // (was ccDict in MIDIKtl) - this contains all the mapped controls
	var <resp; // (was ccresp in MIDIKtl) responder that listens to incoming messages
	var <ktlNames, <orderedKtlNames; // (names as in MIDIKtl) - the defined ctlNames for the device; defined in subclass
			
	*initClass { 
		defaults = ();
		this.allSubclasses.do(_.makeDefaults); 
	}

	*makeDefaults { 
		// subclasses override this method. 
		// they put their controller keys and chan/ccnum combinations into 
		// defaults[class]
			// MIDIKtl is empty by default
		defaults.put(this.class, ());
	}
	
	*new { |srcID, destID, ccDict| 
		^super.newCopyArgs(srcID, destID, ccDict).init;
	}
	
	hasScenes { 
		^ktlNames.every(_.isKindOf(Dictionary))
	}
	
	init { 
		ktlNames = defaults[this.class];

		ktlDict = ktlDict ?? ();
		
		this.makeResp;
	}

	free { 
		this.removeResp;
		ktlDict.clear;
		// redraw pxmix and pxedit with clear colors...?
	}

	removeResp{
		// subclass responsibility:
	}

	makeResp{
		// subclass responsibility
	}

	addAction{ |ctlKey, action, actKey=\user|
		if ( ktlDict.at( ctlKey).isNil ){
			ktlDict.put( ctlKey, FunctionDict.new );
		};
		ktlDict.at( ctlKey ).put( actKey, action );
	}

	removeAction{ |ctlKey, actKey = \user|
		ktlDict.at( ctlKey ).removeAt( actKey );
		if ( ktlDict.at( ctlKey ).isEmpty ){
			ktlDict.removeAt( ctlKey );
		};
	}

	// use when ktlNames is one flat dict
	map { |ctl= \sl1, action, actKey = \user| 
		var ktlDictKey = ktlNames[ctl]; // '0_42'
		if (ktlDictKey.isNil) { 
			warn("key % : no control found!\n".format(ctl));
			^this
		};
		this.addAction( ktlDictKey, action, actKey );
		//		ktlDict.put(ktlDictKey, action);
	}
	
		// use when ktlNames are scene-based dicts (NanoKtl, PDKtl)
	mapS { |scene=2, ctl= \sl1, action,actKey = \user| 
		var mapScene, ktlDictKey; 
		
		mapScene = ktlNames[scene];
		if (mapScene.isNil) { 
			warn("% : mapCCS: scene % : not found!\n".format(this, scene));
			^this
		};
		ktlDictKey = mapScene[ctl]; // '0_42'
		if (ktlDictKey.isNil) { 
			warn("key % : no control found in scene: % !\n".format(ctl,scene));
			^nil			
		};	
		this.addAction( ktlDictKey, action, actKey );
		//		ktlDict.put(ktlDictKey, action);
	}

	findKey { |val| ^ktlNames.findKeyForValue(val); } 
	
	findSceneKey { |val|
		var res;
		ktlNames.keysValuesDo { |scene, dict| 
			res = dict.findKeyForValue(val); 
			if (res.notNil) { ^[scene, res] }; 
		}; 
		^res;
	}
}
