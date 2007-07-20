
TestBeatSched : UnitTest {
	
	test_beat {
				var b,t;
		//Tempo.bpm_(120);
		b = BeatSched.beat;
		t = BeatSched.time;
		//[b,t,Tempo.secs2beats(t) ].postln;
		this.assertFloatEquals( Tempo.secs2beats( t ) ,   b,
		 	"BeatSched .beat should be convertible to .time using Tempo");

	}
	test_deltaTillNext {
				var db,b,ds;
		//Tempo.bpm_(120);
		b = BeatSched.beat;
		db = BeatSched.deltaTillNext(4.0);
		// ds = Tempo.beats2secs(db);
		//[b,db,b + db,b + db % 4.0].postln;
		this.assertFloatEquals( (b + db) % 4.0 , 4.0, 
			"with a fresh BeatSched (at beat 0), delta till next beat with quanitzation of 4.0 should be 4.0 beats");
	}
}