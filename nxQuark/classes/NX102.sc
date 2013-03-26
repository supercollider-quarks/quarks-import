NX102 {
	*ar {
		arg sample=1,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
		"NX102:: La Ment no té Límits.".postln;
"
~nx102 = {
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					RHPF.ar(RLPF.ar(
					Pulse.ar(
						(PlayBuf.kr(1,1,Rand(50,100),1,0,1)*Rand(100,1000)).clip(
							Rand(5,12),Rand(12,20))) 
						,Rand(100,5000),Rand(0.005,0.1))
						,Rand(5000,20000),Rand(0.005,0.1))
					,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(1.25/0.999)*1.0,0.1)),0)
};
".postln;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					RHPF.ar(RLPF.ar(
					Pulse.ar(
						(PlayBuf.kr(1,1,Rand(50,100),1,0,1)*Rand(100,1000)).clip(
							Rand(5,12),Rand(12,20))) 
						,Rand(100,5000),Rand(0.005,0.1))
						,Rand(5000,20000),Rand(0.005,0.1))
					,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.25/0.999)*vol,0.1)),pan)
	}
}