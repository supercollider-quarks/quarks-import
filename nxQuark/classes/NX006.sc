NX006 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
		var number1, array1, array2;
		"NX006:: La detenció només pot anar cap endavant.".postln;
"~nx006 = { var number1, array1, array2;
	number1 = rrand(3,12);
	array1 = Array.series(number1, rrand(0.1,5), rrand(0.1,0.5)); 
	array2 = Array.series(number1, rrand(0.0,0.5), rrand(0.01,0.1)); 
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
						(
						EnvGen.kr(Env.new([0,1,0],[0.01,0.09]), Impulse.kr(
						Demand.kr(Impulse.kr(TRand.kr(1,5,Impulse.kr(rrand(0.01,0.1)))),0,
						Drand(array1,inf)))) *
						RLPF.ar(SinOsc.ar(Rand(200,300)),500,0.01) + 
						RLPF.ar(SinOsc.ar(Rand(100,444)),500,0.1)
					)* (0.25/0.999) * Demand.kr(Impulse.kr(TRand.kr(1,5,
						Impulse.kr(rrand(0.01,0.1)))),0, Drand(array2,inf)) +
					(
						EnvGen.kr(Env.new([0,1,0],[0.001,0.001]), Impulse.kr(
						Demand.kr(Impulse.kr(TRand.kr(1,5,Impulse.kr(rrand(0.01,0.1)))),0,
						Drand(array1,inf)))) *
						RHPF.ar(LFPulse.ar(Rand(20,222)),Rand(10,5000),0.01) + 
						RHPF.ar(LFPulse.ar(Rand(20,222)),Rand(10,5000),0.1)
					)* (0.1/0.999) * Demand.kr(Impulse.kr(TRand.kr(1,5,
						Impulse.kr(rrand(0.01,0.1)))),0, Drand(array2,inf)) +
					(
						Pan2.ar(EnvGen.kr(Env.new([0,1,0],[0.001,0.1]), Impulse.kr(
						Demand.kr(Impulse.kr(TRand.kr(1,5,Impulse.kr(rrand(0.01,0.1)))),0,
						Drand(array1,inf)))) *
						BPF.ar(Pulse.ar(
							TRand.kr(0.1,10,Impulse.kr(rrand(0.01,0.1)))),Rand(500,5000),0.01) + 
						BPF.ar(Pulse.ar(
							TRand.kr(0.1,10,Impulse.kr(rrand(0.01,0.1)))
						),Rand(500,5000),0.01), 
							TRand.kr(-1,1,Impulse.kr(Rand(0.01,0.1))))
					)* (2/0.999) * Demand.kr(Impulse.kr(TRand.kr(1,5,
						Impulse.kr(rrand(0.01,0.1)))),0, Drand(array2,inf))							
					)* (2.00/0.999)*1.0
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
	),0)
};".postln;		
		number1 = rrand(3,12);
		array1 = Array.series(number1, rrand(0.1,5), rrand(0.1,0.5)); 
		array2 = Array.series(number1, rrand(0.0,0.5), rrand(0.01,0.1)); 
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
						(
						EnvGen.kr(Env.new([0,1,0],[0.01,0.09]), Impulse.kr(
						Demand.kr(Impulse.kr(TRand.kr(1,5,Impulse.kr(rrand(0.01,0.1)))),0,
						Drand(array1,inf)))) *
						RLPF.ar(SinOsc.ar(Rand(200,300)),500,0.01) + 
						RLPF.ar(SinOsc.ar(Rand(100,444)),500,0.1)
					)* (0.25/0.999) * Demand.kr(Impulse.kr(TRand.kr(1,5,
						Impulse.kr(rrand(0.01,0.1)))),0, Drand(array2,inf)) +
					(
						EnvGen.kr(Env.new([0,1,0],[0.001,0.001]), Impulse.kr(
						Demand.kr(Impulse.kr(TRand.kr(1,5,Impulse.kr(rrand(0.01,0.1)))),0,
						Drand(array1,inf)))) *
						RHPF.ar(LFPulse.ar(Rand(20,222)),Rand(10,5000),0.01) + 
						RHPF.ar(LFPulse.ar(Rand(20,222)),Rand(10,5000),0.1)
					)* (0.1/0.999) * Demand.kr(Impulse.kr(TRand.kr(1,5,
						Impulse.kr(rrand(0.01,0.1)))),0, Drand(array2,inf)) +
					(
						Pan2.ar(EnvGen.kr(Env.new([0,1,0],[0.001,0.1]), Impulse.kr(
						Demand.kr(Impulse.kr(TRand.kr(1,5,Impulse.kr(rrand(0.01,0.1)))),0,
						Drand(array1,inf)))) *
						BPF.ar(Pulse.ar(
							TRand.kr(0.1,10,Impulse.kr(rrand(0.01,0.1)))),Rand(500,5000),0.01) + 
						BPF.ar(Pulse.ar(
							TRand.kr(0.1,10,Impulse.kr(rrand(0.01,0.1)))
						),Rand(500,5000),0.01), 
							TRand.kr(-1,1,Impulse.kr(Rand(0.01,0.1))))
					)* (2/0.999) * Demand.kr(Impulse.kr(TRand.kr(1,5,
						Impulse.kr(rrand(0.01,0.1)))),0, Drand(array2,inf))							
					)* (2.00/0.999)*vol
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		),pan)
	}
}