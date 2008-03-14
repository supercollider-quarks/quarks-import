

TestPlayerAmp : TestAbstractPlayer {

	makePlayer {
		^PlayerAmp(PlayerInputProxy(AudioSpec(2)),KrNumberEditor(1.0,\amp));
	}
	makeBus {
		^Bus.audio(s,2)
	}
	test_play {
		this.startPlayer;
		0.5.wait;
		this.stopPlayer;
	}
	
	test_prepareToBundle {
		player.prepareToBundle(group,bundle,bus: bus);
		this.assert(bundle.preparationMessages.notNil,"should have synth defs of player amp");
	}

}

