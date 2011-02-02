MIDIKtl : Ktl {
	
	var <midiOut;
	
	init {
		super.init;
		
		if( destID.notNil ) {
			midiOut = MIDIOut(destID)
		}
	}
	
	removeResp {
		resp.remove;
	}

	makeResp {
		resp.remove; 
		resp = CCResponder({ |src, chan, ccn, ccval| 
			var lookie = this.makeCCKey(chan, ccn);
			if (this.class.verbose, { ['cc', src, chan, ccn, ccval].postcs });
			
			if( ktlDict[lookie].notNil ) {
				ktlDict[lookie].valueAll(ccval);
			}
		}, srcID);
	}

	/* unique to MIDIKtl */
	makeCCKey { |chan, cc| ^(chan.asString ++ "_" ++ cc).asSymbol }
	
	ccKeyToChanCtl { |ccKey| ^ccKey.asString.split($_).asInteger }
	
	keyToChanCtl { |key| ^this.ccKeyToChanCtl(this.ktlNames[key]) }
	
	sendCtl { |key, value|
		var chanCtl = this.keyToChanCtl(key);
		midiOut.control(chanCtl[0], chanCtl[1], value)	
	}
	
	sendCtlCcKey{ |ccKey,value|
		var chanCtl = this.ccKeyToChanCtl(ccKey).postln;
		midiOut.control(chanCtl[0], chanCtl[1], value)	
	}

	makeNoteKey { |chan, note| 
		var key = chan.asString; 
		if (note.notNil) { key = key ++ "_" ++ note };
		^key.asSymbol 
	}

	noteKeyToChanNote { |noteKey| ^noteKey.asString.split($_).asInteger }
	
	/* end unique to MIDIKtl */
}

//Creates a virtual scene system with unlimited scenes.
//For use with controllers that accept midi In, and that can reposition the controls on receiving a 
//midi message (e.g. encoders, motorized faders, etc)

MIDIPagedKtl : MIDIKtl {
	classvar <numOfscenes = 32;
	var <currentScene = 0, <valueArray;
	
	init { 
		super.init;
		
		ktlDict = numOfscenes.collect{ () };
		valueArray = numOfscenes.collect{ 
			ktlNames.invert.collect{ |thing, key| 0 }
		}
	}
	
	makeResp {
		resp.remove; 
		resp = CCResponder({ |src, chan, ccn, ccval| 
			var lookie = this.makeCCKey(chan, ccn);
			if (this.class.verbose, { ['cc', src, chan, ccn, ccval].postcs });
			
			valueArray[currentScene].put(lookie,ccval);
			
			if( ktlDict[currentScene][lookie].notNil ) {
				ktlDict[currentScene][lookie].valueAll(ccval);
			}
		}, srcID);
	}
	
	addAction{ |scene = 0, ctlKey, action, actKey=\user|
		if ( ktlDict.at(scene).at( ctlKey).isNil ){
			ktlDict.at(scene).put( ctlKey, FunctionDict.new );
		};
		ktlDict.at(scene).at( ctlKey ).put( actKey, action );
	}

	removeAction{ |scene = 0, ctlKey, actKey = \user|
		ktlDict.at(scene).at( ctlKey ).removeAt( actKey );
		if ( ktlDict.at(scene).at( ctlKey ).isEmpty ){
			ktlDict.at(scene).removeAt( ctlKey );
		};
	}

	map { |scene = 0, ctl= \sl1, action, actKey = \user| 
		var ktlDictKey = ktlNames[ctl]; // '0_42'
		if (ktlDictKey.isNil) { 
			warn("key % : no control found!\n".format(ctl));
			^this
		};
		this.addAction( scene, ktlDictKey, action, actKey );
	}
	
	mapAll{ |ctl= \sl1, action, actKey = \user|
		numOfscenes.do{ |i|
			this.map(i,ctl, action, actKey)
		}
	}
	
	changeScene{ |scene|
		if( scene < numOfscenes ) {
			currentScene = scene;
			valueArray[currentScene].keysValuesDo{ |key,value|
				[key,value].postln;
				this.sendCtlCcKey(key,value)
			}
		}
	}
	
	nextScene {
		var nextScene = (currentScene + 1).clip(0,numOfscenes-1);
		if( nextScene != currentScene ) {
			this.changeScene(nextScene)
		}
	}
	
	previousScene {
		var previousScene = (currentScene - 1).clip(0,numOfscenes-1);
		if( previousScene != currentScene ) {
			this.changeScene(previousScene)
		}
	}
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