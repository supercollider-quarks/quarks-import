
TestPatch : UnitTest {
	var p;

	setUp {
		var i;
		// patch
		i = Instr("help-Patch",{ arg freq=100,amp=1.0;
				SinOsc.ar([freq,freq + 30],0,amp)
			});
		p = Patch(i,[ 500,	0.3 ]);
		
		AbstractPlayer.bundleClass = MixedBundleTester;
		MixedBundleTester.reset;
		InstrSynthDef.clearCache(Server.default);
	}

	test_play {
		p.play;
		this.wait( {p.isPlaying},"wait for patch to play");
		
		p.stop;
		this.wait( {p.isPlaying.not},"waiting for patch to stop playing");
		
		// no longer true
		//this.assert( p.readyForPlay,"patch should still be ready for play");
		
		p.free;
		this.wait( {p.isPrepared.not},"after free, patch should not be ready for play");
		
		this.assertEquals(MixedBundleTester.bundlesSent.size,3,"should be only three bundles sent: prepare, play and stop");
	}
	
	test_prepare {
		p.prepareForPlay;
		
		this.wait( {p.isPrepared},"wait for patch to be ready");
		
		p.play;
		this.wait( {p.isPlaying},"wait for patch to play");

		p.free;
		this.wait({ p.isPrepared.not},"wait for patch to be un-ready after free");

		//p.stop;
		//this.wait( {p.isPlaying.not},"waiting for patch to stop playing");
		
		// no longer true
		//this.assert( p.readyForPlay,"patch should still be ready for play");
		
		//p.free;
		//this.wait( {p.readyForPlay.not},"after free, patch should not be ready for play");
	}
	test_gui {
		var s;
		Instr.clearAll;
		Instr("sin",{SinOsc.ar});
		{
			s = Sheet({ arg f;
				Patch("sin").gui(f);
				Patch("sin").gui(f);
			});
			s.close;
		}.defer;
		this.wait( { s.isClosed },"waiting for window to close");
		this.assertEquals( Instr.leaves.size,1,"should only be one instr in the lib");
	}
}

