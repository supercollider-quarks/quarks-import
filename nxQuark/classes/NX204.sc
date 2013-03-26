NX204 {
	*ar {
	arg inlet,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
	var alpha;
	"NX204:: Quan el moment següent s'intercepta i s'interromp, una vida completa s'acaba".postln;
"~nx204 = { var alpha;
	alpha = Array.series(50,rrand(0.1,2),0.1);
	alpha = alpha ++ alpha.reverse;
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
						RHPF.ar(
						Pan2.ar(InFeedback.ar(~a.index), Demand.kr(
							Impulse.kr(
								Demand.kr(Impulse.kr(Rand(0.1,3)),0,Dseq(alpha,inf))
							),0,Dseq([-1,1],inf)))
							,Rand(100,1000),Rand(0.5,1))
					) * (0.8/0.999) * 1
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ),0)
};".postln;
		alpha = Array.series(50,rrand(0.1,2),0.1);
		alpha = alpha ++ alpha.reverse;
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
						RHPF.ar(
						Pan2.ar(InFeedback.ar(inlet), Demand.kr(
							Impulse.kr(
								Demand.kr(Impulse.kr(Rand(0.1,3)),0,Dseq(alpha,inf))
							),0,Dseq([-1,1],inf)))
							,Rand(100,1000),Rand(0.5,1))
					) * (0.8/0.999) * vol,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		),pan)
	}		
}