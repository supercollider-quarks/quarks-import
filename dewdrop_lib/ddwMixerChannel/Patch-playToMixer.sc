
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

// I have to override this core method. Sorry.
// If I don't, MixerChannelReconstructor doesn't work.
+ Server {
	initTree {
//		nodeAllocator = NodeIDAllocator(clientID);	
		this.sendMsg("/g_new", 1);
		tree.value(this);
	}
}
