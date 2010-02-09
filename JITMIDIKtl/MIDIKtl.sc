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
	
	*new { |srcID, ccDict, noteDict| 
		^super.newCopyArgs(srcID, ccDict).init;
	}
	
	free { 
		ccresp.remove;
		ccDict.clear;
		// redraw pxmix and pxedit with clear colors...?
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
	
	makeCCKey { |chan, cc| ^(chan.asString ++ "_" ++ cc).asSymbol }
	
	ccKeyToChanCtl { |ccKey| ^ccKey.asString.split($_).asInteger }

	makeNoteKey { |chan, note| 
		var key = chan.asString; 
		if (note.notNil) { key = key ++ "_" ++ note };
		^key.asSymbol 
	}

	noteKeyToChanNote { |noteKey| ^noteKey.asString.split($_).asInteger }

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
	
}

MIDINKtl : MIDIKtl { 
	var <noteOnDict, <noteOffDict, <noteOnResp, <noteOffResp;
	
	init { 
		super.init.initNote
	}
	
	initNote { 
		noteOnDict = noteOnDict ?? ();
		noteOffDict = noteOffDict ?? ();

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

	mapNoteOn { 
		
	}
	
	mapNoteOff { 
		
	}
	

	free { 
		super.free;
		
		noteOnResp.remove; 
		noteOffResp.remove; 
		noteOnDict.clear;
		noteOffDict.clear;
	}
}
