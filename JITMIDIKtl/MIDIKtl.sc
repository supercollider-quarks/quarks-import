MIDIKtl { 

	classvar <defaults; 
	
	var <srcID, <ccDict, <ccresp; 
	var <ctlNames;

	*initClass { 
		defaults = ();
		this.subclasses.do(_.makeDefaults); 
	}

	*makeDefaults { 
		// subclasses override this method. 
		// they put their controller keys and chan/ccnum combinations into 
		// defaults[class]
			// MIDIKtl is empty by default
		defaults.put(this.class, ());
	}
	
	*new { |srcID, ccDict| 
		^super.newCopyArgs(srcID, ccDict).init;
	}
	
	init { 
		ctlNames = defaults[this.class];

		ccDict = ccDict ?? ();
		
		ccresp.remove; 
		ccresp = CCResponder({ |src, chan, ccn, val| 
			var lookie = this.makeCCKey(chan, ccn);
			if (this.class.verbose, { ['cc', src, chan, ccn, val].postcs });
			
			ccDict[lookie].value(chan, ccn, val);
		}, srcID);
	}

		// use when ctlNames is one flat dict
	mapCC { |ctl= \sl1, action| 
		var ccDictKey = ctlNames[ctl]; // '0_42'
		if (ccDictKey.isNil) { 
			warn("key % : no chan_ccnum found!\n".format(ctl));
			^this
		}; 
		ccDict.put(ccDictKey, action);
	}
	
		// use when ctlNames are scene-based dicts (NanoKtl, PDKtl)
	mapCCS { |scene=2, ctl= \sl1, action| 
		var ccScene, ccDictKey; 
		
		ccScene = ctlNames[scene];
		if (ccScene.isNil) { 
			warn("% : mapCCS: scene % : not found!\n".format(this, scene));
			^this
		};
		ccDictKey = ccScene[ctl]; // '0_42'
		if (ccDictKey.isNil) { 
			warn("key % : no chan_ccnum found!\n".format(ctl));
			^nil			
		};	
		ccDict.put(ccDictKey, action);
	}

	free { 
		ccresp.remove;
		ccDict.clear;
		// redraw pxmix and pxedit with clear colors...?
	}

	makeCCKey { |chan, cc| ^(chan.asString ++ "_" ++ cc).asSymbol }
	
	ccKeyToChanCtl { |ccKey| ^ccKey.asString.split($_).asInteger }

	makeNoteKey { |chan, note| 
		var key = chan.asString; 
		if (note.notNil) { key = key ++ "_" ++ note };
		^key.asSymbol 
	}

	noteKeyToChanNote { |noteKey| ^noteKey.asString.split($_).asInteger }
	
	findKey { |val| ^ctlNames.findKeyForValue(val); } 
	
	findSceneKey { |val|
		var res;
		ctlNames.keysValuesDo { |scene, dict| 
			res = dict.findKeyForValue(val); 
			if (res.notNil) { ^[scene, res] }; 
		}; 
		^res;
	}
}

MIDINKtl : MIDIKtl { 
	var <noteOnDict, <noteOffDict, <noteOnResp, <noteOffResp;
	
	*new { |srcID, ccDict, noteOnDict, noteOffDict| 
		^super.newCopyArgs(srcID, ccDict).init(noteOnDict, noteOffDict);
	}

	init { |noteOnD, noteOffD|
		super.init.initNote(noteOnD, noteOffD)
	}
	
	initNote { |noteOnD, noteOffD|
		noteOnDict = noteOnD ?? {()};
		noteOffDict = noteOffD ?? {()};

		noteOnResp.remove; 
		noteOnResp = CCResponder({ |src, chan, ccn, val| 
			var lookie = this.makeCCKey(chan, ccn);
			if (this.class.verbose, { ['cc', src, chan, ccn, val].postcs });
			
			ccDict[lookie].value(chan, ccn, val);
		}, srcID);

		noteOffResp.remove; 
		noteOffResp = CCResponder({ |src, chan, ccn, val| 
			var lookie = this.makeCCKey(chan, ccn);
			if (this.class.verbose, { ['cc', src, chan, ccn, val].postcs });
			
			ccDict[lookie].value(chan, ccn, val);
		}, srcID);
	}

		// only for single keys from that source, 
		// ignore midi channels for now. 
		// maybe fix later if needed? 
	mapNoteOn { |note, action| 
		// assume channels
	}
	
	mapNoteOff { 
		
	}

	mapNoteOnS { 
		
	}
	
	mapNoteOffS { 
		
	}
	

	free { 
		super.free;
		
		noteOnResp.remove; 
		noteOffResp.remove; 
		noteOnDict.clear;
		noteOffDict.clear;
	}
}
