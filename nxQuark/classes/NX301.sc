NX301 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var alpha,beta,gamma,t1,t2,t3;
		"NX301:: Si això, i allò... no hi ha temps per a tant".postln;
"
~nx301 = {  var alpha,beta,gamma,t1,t2,t3,vol;
	alpha = rrand(1,10);
    beta = rrand(1,10);
	gamma = rrand(1,10);
	t1 = rrand(20,200);
	t2 = rrand(20,200);
	t3 = rrand(20,200);
    vol = 1;
	Pan2.ar(LeakDC.ar(		
		BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
			EnvGen.kr(Env.new([0,(0.8/0.999)*vol,0],[t1/2,t1/2],'sine'),Impulse.kr(1/t1)) *
			RHPF.ar(
			Pulse.ar(TRand.kr(alpha,alpha+10,Impulse.kr(Rand(50,100))))
				,Rand(6666,15000),Rand(0.001,0.1)) +
			EnvGen.kr(Env.new([0,(0.8/0.999)*vol,0],[t2/2,t2/2],'sine'),Impulse.kr(1/t2)) *
			RHPF.ar(
			Pulse.ar(TRand.kr(beta,beta+10,Impulse.kr(Rand(50,100))))
				,Rand(6666,15000),Rand(0.001,0.1)) +
			EnvGen.kr(Env.new([0,(0.8/0.999)*vol,0],[t3/2,t3/2],'sine'),Impulse.kr(1/t3)) *
			RHPF.ar(
			Pulse.ar(TRand.kr(gamma,gamma+10,Impulse.kr(Rand(50,100))))
				,Rand(6666,15000),Rand(0.001,0.1))
		,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ),0)
};
".postln;
		alpha = rrand(1,10);
		beta = rrand(1,10);
		gamma = rrand(1,10);
		t1 = rrand(20,200);
		t2 = rrand(20,200);
		t3 = rrand(20,200);
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
			EnvGen.kr(Env.new([0,(0.8/0.999)*vol,0],[t1/2,t1/2],'sine'),Impulse.kr(1/t1)) *
			RHPF.ar(
			Pulse.ar(TRand.kr(alpha,alpha+10,Impulse.kr(Rand(50,100))))
				,Rand(6666,15000),Rand(0.001,0.1)) +
			EnvGen.kr(Env.new([0,(0.8/0.999)*vol,0],[t2/2,t2/2],'sine'),Impulse.kr(1/t2)) *
			RHPF.ar(
			Pulse.ar(TRand.kr(beta,beta+10,Impulse.kr(Rand(50,100))))
				,Rand(6666,15000),Rand(0.001,0.1)) +
			EnvGen.kr(Env.new([0,(0.8/0.999)*vol,0],[t3/2,t3/2],'sine'),Impulse.kr(1/t3)) *
			RHPF.ar(
			Pulse.ar(TRand.kr(gamma,gamma+10,Impulse.kr(Rand(50,100))))
				,Rand(6666,15000),Rand(0.001,0.1))
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		),pan)
	}
}
