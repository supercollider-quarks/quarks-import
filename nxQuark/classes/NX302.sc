NX302 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var alpha,beta;
		"NX302:: Quan això sorgeix, sorgeix allò".postln;
"~nx302 = { var alpha,beta,vol;
	vol = 1;
	alpha = Demand.kr(Impulse.kr(Rand(0.1,1)),0,Dseq(Rand(1,50),Rand(1,10)));
	beta = TRand.kr(3,22,Impulse.kr(rrand(0.01,0.5)));
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					EnvGen.kr(Env.new(
						[0,(0.8/0.999)*vol,0,(0.8/0.999)*vol]
						,[beta/3,beta/3,beta/3],'step'),Impulse.kr(1/beta))*
					CombC.ar(
						RHPF.ar(
					Pulse.ar(alpha)
						,TRand.kr(10000,15000,Impulse.kr(Rand(5,20))),
						TRand.kr(0.0001,0.1,Impulse.kr(Rand(0.1,1))))
						,0.2,0.2,0.001)
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ),0)
};
".postln;
		alpha = Demand.kr(Impulse.kr(Rand(0.1,1)),0,Dseq(Rand(1,50),Rand(1,10)));
		beta = TRand.kr(3,22,Impulse.kr(rrand(0.01,0.5)));
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					EnvGen.kr(Env.new(
						[0,(0.8/0.999)*vol,0,(0.8/0.999)*vol]
						,[beta/3,beta/3,beta/3],'step'),Impulse.kr(1/beta))*
					CombC.ar(
						RHPF.ar(
					Pulse.ar(alpha)
						,TRand.kr(10000,15000,Impulse.kr(Rand(5,20))),
						TRand.kr(0.0001,0.1,Impulse.kr(Rand(0.1,1))))
						,0.2,0.2,0.001)
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		),pan)
	}
}
