NX303 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var delay,time,alpha,beta,gamma,delta,gamma2,delta2;
		"NX303:: No hi haurà aquí, ni allà, ni el que hi ha entremig; aquest és el final.".postln;
"~nx303 = { var delay,time,alpha,beta,gamma,delta,gamma2,delta2;
	time = TRand.kr(0.01,10.0,Impulse.kr(rrand(0.001,0.01)));
	alpha = TRand.kr(0.001,9.01,Impulse.kr(time));
	beta = TRand.kr(0.001,9.01,Impulse.kr(time));
	gamma = TRand.kr(1,100,Impulse.kr(time));
    delta = TRand.kr(8000,18000,Impulse.kr(time));
	gamma2 = TRand.kr(1,100,Impulse.kr(time));
    delta2 = TRand.kr(8000,18000,Impulse.kr(time));
    delay = TRand.kr(0.01,1,Impulse.kr(time));
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					CombC.ar(
					EnvGen.kr(Env.new([0,(0.4/0.999)*1.0,0],[alpha/2,alpha/2]), 
						Impulse.kr(1/(alpha+beta))) *
					Mix.new([
					RHPF.ar( Pulse.ar(gamma), delta, 0.001),
						RHPF.ar( Pulse.ar(gamma2), delta2, 0.001)
					])
						,0.5,0.5,delay)
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ),0)
};".postln;
		time = TRand.kr(0.01,10.0,Impulse.kr(rrand(0.001,0.01)));
		alpha = TRand.kr(0.001,9.01,Impulse.kr(time));
		beta = TRand.kr(0.001,9.01,Impulse.kr(time));
		gamma = TRand.kr(1,100,Impulse.kr(time));
		delta = TRand.kr(8000,18000,Impulse.kr(time));
		gamma2 = TRand.kr(1,100,Impulse.kr(time));
		delta2 = TRand.kr(8000,18000,Impulse.kr(time));
		delay = TRand.kr(0.01,1,Impulse.kr(time));
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					CombC.ar(
					EnvGen.kr(Env.new([0,(0.4/0.999)*vol,0],[alpha/2,alpha/2]), 
						Impulse.kr(1/(alpha+beta))) *
					Mix.new([
					RHPF.ar( Pulse.ar(gamma), delta, 0.001),
						RHPF.ar( Pulse.ar(gamma2), delta2, 0.001)
					])
						,0.5,0.5,delay)
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ),pan)
	}
}
