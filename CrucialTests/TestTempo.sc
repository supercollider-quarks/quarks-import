
TestTempo : UnitTest {
	
	test_secs2beats {
		Tempo.bpm = 60;
		this.assertFloatEquals( Tempo.secs2beats( 1 ) , 1.0,
			"with a bpm of 60, 1 beat should equal 1 second");
	}

}

