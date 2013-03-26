NX012 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
		var alpha;
		"NX012:: El meu cos és igual que el putrefacte.".postln;
"~nx012 = { 
	var alpha;
	alpha = rrand(3,30);
	Pan2.ar(LeakDC.ar(Normalizer.ar(
	BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
		EnvGen.kr(Env.adsr(0.1,3,1,rrand(0.1,1.0)), 
			Impulse.kr(TRand.kr(66,133.33,Impulse.kr(rrand(0.1,0.1))))) * 
		HPF.ar(Pulse.ar(TRand.kr(alpha,alpha+10, Impulse.kr(rrand(33,100)))),
			rrand(100,15000).postln)	
	,0.1,0.1,0.1)
	,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
	,(1.33/0.999)*1,0.1)),0)
};".postln;
		alpha = rrand(3,30);
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
		EnvGen.kr(Env.adsr(0.1,3,1,rrand(0.1,1.0)), 
			Impulse.kr(TRand.kr(66,133.33,Impulse.kr(rrand(0.1,0.1))))) * 
		HPF.ar(Pulse.ar(TRand.kr(alpha,alpha+10, Impulse.kr(rrand(33,100)))),
			rrand(100,15000).postln)
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.33/0.999)*vol,0.1)),pan)
	}
}
