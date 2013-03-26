NX202 {
	*ar {
		arg inlet,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		"NX202:: Només és una expressió convencional..".postln;
		"
~nx202 = { 
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					CombC.ar(
					RHPF.ar(
					Mix.new([
						EnvGen.kr(Env.perc(
							Rand(0.0001,0.001),Rand(0.0001,0.001)),Impulse.kr(rand(50,100)))
						*InFeedback.ar(~a.index),
						EnvGen.kr(Env.perc(
							Rand(0.0001,0.001),Rand(0.0001,0.001)),Impulse.kr(rand(50,100)))
						*InFeedback.ar(~a.index) ,
						EnvGen.kr(Env.perc(
							Rand(0.0001,0.001),Rand(0.0001,0.001)),Impulse.kr(rand(50,100)))
						*InFeedback.ar(~a.index) 
					]),Rand(8000,12000),0.01),0.2,0.2,Rand(1,10))
					* (1.0/0.999) * 1
		,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ),0)
};
".postln;
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					CombC.ar(
					RHPF.ar(
					Mix.new([
						EnvGen.kr(Env.perc(
							Rand(0.0001,0.001),Rand(0.0001,0.001)),Impulse.kr(rand(50,100)))
						*InFeedback.ar(inlet),
						EnvGen.kr(Env.perc(
							Rand(0.0001,0.001),Rand(0.0001,0.001)),Impulse.kr(rand(50,100)))
						*InFeedback.ar(inlet) ,
						EnvGen.kr(Env.perc(
							Rand(0.0001,0.001),Rand(0.0001,0.001)),Impulse.kr(rand(50,100)))
						*InFeedback.ar(inlet) 
					]),Rand(8000,12000),0.01),0.2,0.2,Rand(1,10))
		 * (1.0/0.999)*vol ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ),pan)
	}
}		