NX304 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var alpha;
		"NX304:: El temps és mort".postln;
"~nx304 = { var alpha,beta;
	alpha = TRand.kr(0.1,10,Impulse.kr(Rand(0.01,0.1)));
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					RHPF.ar(
					Pulse.ar(Demand.kr(Impulse.kr(alpha),0,Drand([
						0,
						TRand.kr(2,10,Impulse.kr(alpha/Rand(1,4)))
						,0,
					    TRand.kr(2,10,Impulse.kr(alpha))
					    ,0,
					    TRand.kr(2,
							TRand.kr(10,5000),Impulse.kr(Rand(0.001,0.01))
							,Impulse.kr(alpha/Rand(1,4)))],inf)))
						,TRand.kr(10000,20000,Impulse.kr(alpha)),
						TRand.kr(0.0001,0.001,Impulse.kr(alpha)))
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(2.0/0.999)*1.0,0.1)),0)
};".postln;
		alpha = TRand.kr(0.1,10,Impulse.kr(Rand(0.01,0.1)));
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					RHPF.ar(
					Pulse.ar(Demand.kr(Impulse.kr(alpha),0,Drand([
						0,
						TRand.kr(2,10,Impulse.kr(alpha/Rand(1,4)))
						,0,
					    TRand.kr(2,10,Impulse.kr(alpha))
					    ,0,
					    TRand.kr(2,
							TRand.kr(10,5000),Impulse.kr(Rand(0.001,0.01))
							,Impulse.kr(alpha/Rand(1,4)))],inf)))
						,TRand.kr(10000,20000,Impulse.kr(alpha)),
						TRand.kr(0.0001,0.001,Impulse.kr(alpha)))
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(2.00/0.999)*vol,0.1)),pan)
	}
}
