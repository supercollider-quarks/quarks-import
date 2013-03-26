NX101 {
	*ar {
		arg sample=1,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
		"NX101:: Totes les morts perden el present.".postln;
"
~nx101 = {
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
			    BHiPass4.ar(Mix.new([
     PlayBuf.ar(1,1,BufRateScale.kr(1)+rrand(0.1,1.0),
	 Impulse.kr(rrand(25.0,100.0)),
	 BufFrames.kr(1)/rrand(1,10),1)
     * EnvGen.kr(Env([0,rrand(0.1,1),rrand(0.1,1),0],[0.01,0.01,0.01],-4),
     Impulse.kr(rrand(25.0,100.0))),
     PlayBuf.ar(1,1,BufRateScale.kr(1)+rrand(0.1,1.0),
	 Impulse.kr(rrand(25.0,100.0)),
	 BufFrames.kr(1)/rrand(1,10),1)
     * EnvGen.kr(Env([0,rrand(0.1,1),rrand(0.1,1),0],[0.01,0.01,0.01],-4),
     Impulse.kr(rrand(25.0,100.0)))
				]),Rand(1000,10000),rrand(0.05,0.9))
	,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(1.25/0.999)*1.0,0.1)),0)
};
".postln;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(
	FreeVerb.ar(
			    BHiPass4.ar(Mix.new([
     PlayBuf.ar(1,sample,BufRateScale.kr(sample)+rrand(0.1,1.0),
	 Impulse.kr(rrand(25.0,100.0)),
	 BufFrames.kr(sample)/rrand(1,10),1)
     * EnvGen.kr(Env([0,rrand(0.1,1),rrand(0.1,1),0],[0.01,0.01,0.01],-4),
     Impulse.kr(rrand(25.0,100.0))),
     PlayBuf.ar(1,sample,BufRateScale.kr(sample)+rrand(0.1,1.0),
	 Impulse.kr(rrand(25.0,100.0)),
	 BufFrames.kr(sample)/rrand(1,10),1)
     * EnvGen.kr(Env([0,rrand(0.1,1),rrand(0.1,1),0],[0.01,0.01,0.01],-4),
     Impulse.kr(rrand(25.0,100.0)))
				]),Rand(1000,10000),rrand(0.05,0.9))
			,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.25/0.999)*vol,0.1)),pan)
	}
}
