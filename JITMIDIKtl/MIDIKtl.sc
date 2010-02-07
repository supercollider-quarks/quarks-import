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

	free { 
		super.free;
		
		noteOnResp.remove; 
		noteOffResp.remove; 
		noteOnDict.clear;
		noteOffDict.clear;
	}
}
