
TestTempo : UnitTest {
	
	test_secs2beats {
		Tempo.bpm = 60;
		this.assertFloatEquals( Tempo.secs2beats( 1 ) , 1.0,
			"with a bpm of 60, 1 beat should equal 1 second");
	}

	test_asCompileString {
		var t;
		t = TempoPlayer.new;
		// and not the class
		this.assert( t.asCompileString.compile.value.isKindOf(TempoPlayer),"should recompile as a TempoPlayer");
	}

}

