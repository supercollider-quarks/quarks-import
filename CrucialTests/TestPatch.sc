
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
	}

	test_play {

		p.play;
		this.wait( {p.isPlaying},"wait for patch to play");
		
		p.stop;
		this.wait( {p.isPlaying.not},"waiting for patch to stop playing");
		
		// no longer true
		//this.assert( p.readyForPlay,"patch should still be ready for play");
		
		p.free;
		this.wait( {p.readyForPlay.not},"after free, patch should not be ready for play");
	}
	
	test_prepare {
		p.prepareForPlay;
		
		this.wait( {p.readyForPlay},"wait for patch to be ready");
		
		p.play;
		this.wait( {p.isPlaying},"wait for patch to play");

		p.free;
		this.wait({ p.readyForPlay.not},"wait for patch to be un-ready after free");

		//p.stop;
		//this.wait( {p.isPlaying.not},"waiting for patch to stop playing");
		
		// no longer true
		//this.assert( p.readyForPlay,"patch should still be ready for play");
		
		//p.free;
		//this.wait( {p.readyForPlay.not},"after free, patch should not be ready for play");
	}
}

