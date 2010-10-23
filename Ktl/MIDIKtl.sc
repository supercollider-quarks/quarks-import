MIDIKtl : Ktl { 
	
	removeResp{
		resp.remove;
	}

	makeResp{
		resp.remove; 
		resp = CCResponder({ |src, chan, ccn, ccval| 
			var lookie = this.makeCCKey(chan, ccn);
			if (this.class.verbose, { ['cc', src, chan, ccn, ccval].postcs });
			
			ktlDict[lookie].valueAll(ccval);
		}, srcID);
	}

	/* unique to MIDIKtl */
	makeCCKey { |chan, cc| ^(chan.asString ++ "_" ++ cc).asSymbol }
	
	ccKeyToChanCtl { |ccKey| ^ccKey.asString.split($_).asInteger }

	makeNoteKey { |chan, note| 
		var key = chan.asString; 
		if (note.notNil) { key = key ++ "_" ++ note };
		^key.asSymbol 
	}

	noteKeyToChanNote { |noteKey| ^noteKey.asString.split($_).asInteger }
	
	/* end unique to MIDIKtl */
}
	
/*
	// add responders for noteOn, noteOff: 
	
MIDINKtl : MIDIKtl { 
	var <chan, <noteOnDict, <noteOffDict, <noteOnResp, <noteOffResp;
	var <>globalNoteOnFunc, <>globalNoteOffFunc, noGlobal;
	
	*new { |srcID, chan = 0, ccDict, noteOnDict, noteOffDict| 
		^super.newCopyArgs(srcID, ccDict).init(chan, noteOnDict, noteOffDict);
	}

	init { |chan, noteOnD, noteOffD|
		super.init.initNote(chan, noteOnD, noteOffD)
	}
	
	initNote { |inChan, noteOnD, noteOffD|
		chan = inChan ? 0; 
		noteOnDict = noteOnD ?? {()};
		noteOffDict = noteOffD ?? {()};

		noteOnResp.remove; 
		noteOnResp = NoteOnResponder({ |src, chan, note, vel| 
		//	var lookie = this.makeNoteKey(chan, note);
			var specialFunc = noteOnDict[note];
			
			if (this.class.verbose, { ['cc', src, chan, note, vel].postcs });
			
			if (specialFunc.notNil) { 
				specialFunc.value(note, vel / 127) 
			} { 
				globalNoteOnFunc.value(note, vel / 127);
			};
		}, srcID);

		noteOffResp.remove; 
		noteOffResp = NoteOffResponder({ |src, chan, note, vel| 
		//	var lookie = this.makeNoteKey(chan, note);
			var specialFunc = noteOffDict[note];
			
			if (this.class.verbose, { ['cc', src, chan, note, vel].postcs });
			
			if (specialFunc.notNil) { 
				specialFunc.value(note, vel / 127) 
			} { 
				globalNoteOffFunc.value(note, vel / 127);
			};
		}, srcID);
	}
	
	on_ { |func| globalNoteOnFunc = func }
	off_ { |func| globalNoteOffFunc = func }
	
	mapNoteOn { |note, action| noteOnDict.put(note, action) }
	
	mapNoteOff { |note, action| noteOffDict.put(note, action) }	

	free { 
		super.free;
		
		noteOnResp.remove; 
		noteOffResp.remove; 
		noteOnDict.clear;
		noteOffDict.clear;
	}
}
*/