
TestAbstractPlayer : UnitTest {
	
	var s,player,group,bus,bundle;
	
	makePlayer {
		^SynthlessPlayer.new
	}
	makeBus {
		//^nil //Bus.audio(s,1);
		^this.subclassResponsibility
	}
	setUp {
		Instr.clearAll;
		InstrSynthDef.clearCache(s);
		s = Server.default;

		MixedBundleTester.reset;
		AbstractPlayer.bundleClass = MixedBundleTester;

		player = this.makePlayer.checkKind(AbstractPlayer);

		bundle = MixedBundleTester.new;
		group = bus = nil; // you must make these yourself
	}
	startPlayer {
		this.bootServer;
		0.1.wait;
		group = Group.basicNew(s);
		bus = this.makeBus;
		AbstractPlayer.annotate(bus,"test bus");
		
		NodeWatcher.register(group);
		s.sendBundle(nil, group.newMsg);
		this.wait({ group.isPlaying },"waiting for group to play");
		
		player.play(group,nil,bus);
		this.wait({ player.isPlaying },"waiting for "+player+"to .isPlaying");
	}
	stopPlayer {

		player.free;
		this.wait({ player.isPlaying.not },"waiting for "+player+"to .isPlaying.not");
		// player may be stopped but notifications to its node are still going to come
		this.wait({ player.synth.isPlaying.not },"waiting for the player's synth to get notififed stopped");
		1.0.wait;
		// and a group.free will get there before the bundle already on its way !

		group.free;
		this.wait({ group.isPlaying.not },"waiting for group to free");

		if(bus.index.notNil,{
			// that's fine, I gave it to you
			// ideally should know if it was given to you
			// but ususually uses a SharedBus in that case
			bus.free;
			AbstractPlayer.removeAnnotation(bus);
		});
		
		this.assertEquals( s.audioBusAllocator.blocks.size,0,"should be no busses allocated now");
	}
	startStopStart {
		this.startPlayer;
		1.0.wait;
		this.stopPlayer;
		1.0.wait;
		this.startPlayer;
		1.0.wait;
		this.stopPlayer;
		1.0.wait;
	}
	
	test_play {
		this.startPlayer;
		this.wait({ player.isPlaying },"waiting for "+player+"to play");
		0.5.wait;
		this.stopPlayer;
		0.5.wait;
	}
	test_prepareForPlay {
		player.prepareForPlay(group,true,bus);
		this.wait({ player.readyForPlay },"waiting for "+player+"to prepare");
		0.5.wait;
		player.free;
		0.5.wait;
	}
	test_prepareToBundle {
		bundle = MixedBundleTester.new;
		player.prepareToBundle(group,bundle,true, bus, false);
		this.assertEquals(player.status,\isPreparing);
		this.assertEquals(player.group, group, "group is set");
		this.assertEquals(player.server, s, "server is set");

		// patchOut
		this.assert(player.patchOut.notNil,"should make patch out");
		if(bus.notNil,{
			this.assertEquals( player.patchOut.bus, bus,"bus is set from passed in");
			this.assertEquals( player.bus, bus,"bus is set from passed in");
		});
		
	}
	test_makeResourcesToBundle {
		player.makeResourcesToBundle(bundle)
	}
	test_prepareChildrenToBundle {
		player.prepareChildrenToBundle(bundle)
	}
	test_loadDefFileToBundle { 
		player.loadDefFileToBundle( bundle,s);
	}



}


