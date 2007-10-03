
+ Patch {
	playToMixer { arg m, atTime = nil, callback;
		this.play(m.synthgroup, atTime, m.inbus, callback);
	}
}

+ Nil {
	draggedIntoMixerGUI { |gui|
		gui.mixer = nil;
	}
	
	asMixerChannelGUI { |board|
		^MixerChannelGUI(nil, board)
	}
}

// needed to allow collections as mixer arguments in MixingBoard-new
+ Collection {
	asMixerChannelGUI { |board|
		^this.collect({ |mixer| MixerChannelGUI(mixer, board) })
	}
}

+ MixerChannel {
	draggedIntoMixerGUI { |gui|
		gui.mixer = this;
	}
}

+ Voicer {
		// if the voicer's target is a MC, assign it to the gui
	draggedIntoMixerGUI { |gui|
		var	mc;
		(mc = bus.tryPerform(\asMixer)).notNil.if({
			gui.mixer_(mc);
			gui.refresh;
		});
	}
}

+ VoicerProxy {
		// change the voicer's target to the mixer in this gui
		// does not affect currently playing notes
		// if the gui is empty, the drag-n-drop will be ignored
	draggedIntoMixerGUI { |gui|
		voicer.notNil.if({
			voicer.draggedIntoMixerGUI(gui)
		});
	}
}

// I have to override this core method. Sorry.
// If I don't, MixerChannelReconstructor doesn't work.
+ Server {
	initTree {
//		nodeAllocator = NodeIDAllocator(clientID);	
		this.sendMsg("/g_new", 1);
		tree.value(this);
	}
}
