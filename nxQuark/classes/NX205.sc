NX205 {
	*ar {
	arg inlet,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
	"NX205:: La dinàmica del verd sobre la ment.".postln;
"
~nx205 = { 
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
						DelayN.ar(
							RLPF.ar(
							EnvGen.kr(
								Env.perc(Rand(0.001,0.01),Rand(0.001,0.01)),
								Impulse.kr(Rand(10,50)))
							* InFeedback.ar(~a.index), Rand(800,1200),0.01)
							,Rand(0.1,1.0),Rand(0.1,1.0))
					) * (0.5/0.999) * 1
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ),0)
};".postln;
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
						DelayN.ar(
							RLPF.ar(
							EnvGen.kr(
								Env.perc(Rand(0.001,0.01),Rand(0.001,0.01)),
								Impulse.kr(Rand(10,50)))
							* InFeedback.ar(inlet), Rand(800,1200),0.01)
							,Rand(0.1,1.0),Rand(0.1,1.0))
					) * (0.5/0.999) * vol ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		),pan)
	}		
}