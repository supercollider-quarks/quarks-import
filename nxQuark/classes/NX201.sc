NX201 {
	*ar {
		arg inlet,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		"NX201:: Havent vist tot el que hi ha al món.".postln;
		"
~nx201 = {
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(EnvGen.kr(Env.perc(0.001,0.001),
						Impulse.kr(Rand(10,50)+
							TRand.kr(0,10,Impulse.kr(Rand(10,100)))))*
					(
					RHPF.ar(
			 		(RHPF.ar(
					InFeedback.ar(~a.index) * LFTri.kr(Rand(5,100))
						,Rand(1000,10000),0.001))
						,Rand(1000,10000),0.001))) * (0.15/0.999)*1
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ),0)
};
".postln;
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(EnvGen.kr(Env.perc(0.001,0.001),
						Impulse.kr(Rand(10,50)+
							TRand.kr(0,10,Impulse.kr(Rand(10,100)))))*
					(
					RHPF.ar(
			 		(RHPF.ar(
					InFeedback.ar(inlet) * LFTri.kr(Rand(5,100))
						,Rand(1000,10000),0.001))
						,Rand(1000,10000),0.001)))
		 * (0.15/0.999)*vol ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ),pan)
	}
}		