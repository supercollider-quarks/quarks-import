NX017 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
		var alpha;
		"NX017:: Tot és impersonal.".postln;
"~nx017 = {
	var alpha;
	alpha = rrand(60,200);
	Pan2.ar(LeakDC.ar(Normalizer.ar(
		BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
			LPF.ar(
				RHPF.ar(
					SinOsc.ar(TRand.kr(alpha,alpha+10,Impulse.kr(Rand(33,100)))),
					Rand(1000,10000),Rand(0.0001,0.01)),Rand(500,3000))
			,0.1,0.1,0.1)
			,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		,(1.33/0.999)*1.0,0.1)),0)
};".postln;
		alpha = rrand(60,200);
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
			LPF.ar(
				RHPF.ar(
					SinOsc.ar(TRand.kr(alpha,alpha+10,Impulse.kr(Rand(33,100)))),
					Rand(1000,10000),Rand(0.0001,0.01)),Rand(500,3000))
					,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.33/0.999)*vol,0.1)),pan)
	}
}