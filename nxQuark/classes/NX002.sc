NX002 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
		var alpha,beta,gamma;
		alpha = rrand(1000,3000);
		beta = rrand(100,440);
		gamma = rrand(100,440);
		"NX002:: Els ulls no poden veure-la; no té veu per sentir-la.".postln;
		"~nx002 = {      var alpha,beta,gamma;
			alpha = rrand(1000,3000);
			beta = rrand(100,440);
			gamma = rrand(100,440);
			Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					HPF.ar(LPF.ar(
					RHPF.ar(
                    Saw.ar(TRand.kr(alpha,alpha+10, Impulse.kr(rrand(10,100))))+
					CombC.ar(Saw.ar(TRand.kr(beta,beta+1000, Impulse.kr(rrand(10,100))))
                    ,1,1,0.1),17000,0.01)+
					Saw.ar(TRand.kr(alpha,alpha+100, Impulse.kr(rrand(10,100))))+
					Formant.ar(TRand.kr(gamma,gamma+
						TRand.kr(1,100, Impulse.kr(rrand(0.1,1)))
						, Impulse.kr(rrand(10,100))))
						,rrand(100,1000),0.1),rrand(1000,10000),0.1)
			 ,rrand(0.1,0.7),rrand(0.1,0.7),rrand(0.1,0.7))
			,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
			,(1.25/0.999)*1,0.1)),0)
         };".postln;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					HPF.ar(LPF.ar(
					RHPF.ar(
                    Saw.ar(TRand.kr(alpha,alpha+10, Impulse.kr(rrand(10,100))))+
					CombC.ar(Saw.ar(TRand.kr(beta,beta+1000, Impulse.kr(rrand(10,100))))
                    ,1,1,0.1),17000,0.01)+
					Saw.ar(TRand.kr(alpha,alpha+100, Impulse.kr(rrand(10,100))))+
					Formant.ar(TRand.kr(gamma,gamma+
						TRand.kr(1,100, Impulse.kr(rrand(0.1,1)))
						, Impulse.kr(rrand(10,100))))
						,rrand(100,1000),0.1),rrand(1000,10000),0.1)
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.27/0.999)*vol,0.1)),pan)
	}
}
