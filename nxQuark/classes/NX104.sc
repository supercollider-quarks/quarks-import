NX104 {
	*ar {
		arg sample=1,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		"NX104:: Una paraula de pau.".postln;
"
~nx104 = {
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					Warp1.ar(1,1,Rand(0.0,1.0),Rand(0.5,2.0),
						Rand(0.01,0.05),-1,Rand(0.05,2.0),Rand(0.00,1.0),Rand(1,2))
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(1.25/0.999)*1.0,0.1)),0)
};
".postln;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					Warp1.ar(1,1,Rand(0.0,1.0),Rand(0.5,2.0),
						Rand(0.01,0.05),-1,Rand(0.05,2.0),Rand(0.00,1.0),Rand(1,2))
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.0/0.999)*vol,0.1)),pan)
	}
}