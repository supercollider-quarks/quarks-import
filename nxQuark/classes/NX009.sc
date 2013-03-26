NX009 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
		var alpha,beta;
		"NX009:: La contemplació del cos l'esborra.".postln;
"~nx009 = { var alpha, beta;
	alpha = rrand(10,20);
	beta = rrand(0.00001,0.0001);
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					FreeVerb.ar(
					HPF.ar(SinOsc.ar(Rand(25,125),Rand(0.0,1.0),
					EnvGen.kr(Env.new([1.0,0.0,1.0],[alpha,alpha],'sine'),Impulse.kr(1/(alpha*2)))
					).distort * Rand(0.2,1.0),Rand(1000,10000)) *
					EnvGen.kr(Env.new([1.0,0.0,1.0],[alpha*100,alpha*100],'sine'),Impulse.kr(1/((alpha*2)*100)))
 +
					HPF.ar(SinOsc.ar(Rand(25,125),Rand(0.0,1.0),
					EnvGen.kr(Env.new([0.0,1.0,0.0],[beta,beta],'sine'),Impulse.kr(1/(beta*2)))
					).distort * Rand(0.2,1.0),Rand(1000,10000)) *
					EnvGen.kr(Env.new([0.0,1.0,0.0],[beta*100,beta*100],'sine'),Impulse.kr(1/((beta*2)*100))),
						1.0,1.0,1.0)
				,0.1,0.1,0.1)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(1.33/0.999)*1,0.1)),0)
};
".postln;
		alpha = rrand(10,20);
		beta = rrand(0.00001,0.0001);
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					FreeVerb.ar(
					HPF.ar(SinOsc.ar(Rand(25,125),Rand(0.0,1.0),
					EnvGen.kr(Env.new([1.0,0.0,1.0],[alpha,alpha],'sine'),Impulse.kr(1/(alpha*2)))
					).distort * Rand(0.2,1.0),Rand(1000,10000)) *
					EnvGen.kr(Env.new([1.0,0.0,1.0],[alpha*100,alpha*100],'sine'),Impulse.kr(1/((alpha*2)*100)))
 +
					HPF.ar(SinOsc.ar(Rand(25,125),Rand(0.0,1.0),
					EnvGen.kr(Env.new([0.0,1.0,0.0],[beta,beta],'sine'),Impulse.kr(1/(beta*2)))
					).distort * Rand(0.2,1.0),Rand(1000,10000)) *
					EnvGen.kr(Env.new([0.0,1.0,0.0],[beta*100,beta*100],'sine'),Impulse.kr(1/((beta*2)*100))),
						1.0,1.0,1.0)
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.33/0.999)*vol,0.1)),pan)
	}
}