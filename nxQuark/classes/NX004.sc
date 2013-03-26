NX004 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
		var alpha;
		"NX004:: El suicidi dels dimonis a la teva tomba.".postln;
"~nx004 = { var alpha;
	alpha = rrand(50,250);
	Pan2.ar(LeakDC.ar(Normalizer.ar(
	BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
			 HPF.ar(
				 Formant.ar(TRand.kr(alpha,alpha+rrand(5,10),Impulse.kr(rrand(5,10))))
				 ,rrand(3000,15000))+
		     HPF.ar(
		     Formant.ar(alpha+rrand(10,100)),rrand(3000,15000))
			 ,rrand(0.0,1.0),rrand(0.0,1.0),rrand(0.0,1.0))
			,100,6.0,rrand(-12,12)),1000,6.0,rrand(-12,12)),5000,6.0,rrand(-12,12)),10000,6.0,rrand(-12,12))
			,(1.33/0.999)*1,0.1)),0)
    };".postln;
		alpha = rrand(50,250);
        ^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
			 HPF.ar(
				 Formant.ar(TRand.kr(alpha,alpha+rrand(5,10),Impulse.kr(rrand(5,10))))
				 ,rrand(3000,15000))+
		     HPF.ar(
		     Formant.ar(alpha+rrand(10,100)),rrand(3000,15000))
			 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.33/0.999)*vol,0.1)),pan)
}
}