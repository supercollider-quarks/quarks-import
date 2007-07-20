

TestAbstractPlayerProxy : UnitTest {
	
	test_play {
		var a;
	
		this.bootServer;
	
		a = AbstractPlayerProxy.new;
		a.source = Patch({ Saw.ar });

		a.play;
		this.wait({a.isPlaying},"a failed to play");
		
		this.assert(a.socketStatus == \isPlaying,"socketStatus should be isPlaying");
	
		this.assert(a.bus.numChannels == 1, "should have a bus with 1 channel");
		this.assert(a.bus.index == 0,"should be playing on Bus index 0");

		a.stop;
		this.wait({a.isPlaying.not},"a failed to stop");
		
		a.free;
		// bus should be freed
		
		// a is nil ????
		// something in the language is fucked
		//a.bus.debug("a bus");
		
		0.4.wait;
		a.play;
		this.wait({a.isPlaying},"a failed to play a second time after being freed");
		this.assert(a.socketStatus == \isPlaying,"socketStatus should be isPlaying");
	
		this.assert(a.bus.numChannels == 1, "should have a bus with 1 channel");
		this.assert(a.bus.index == 0,"should be playing on Bus index 0");

		a.free;

	}


}

