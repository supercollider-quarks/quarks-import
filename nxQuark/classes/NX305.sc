NX305 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var alpha, beta, gamma, alphaB, betaB, gammaB, amp;
		"NX305:: Allargo la mà cap a la meva pròpia dissolució".postln;
"~nx305 = { var alpha, beta, gamma, alphaB, betaB, gammaB, amp;
	alpha = [[0,20000,300,0.01],[rrand(100,400),rrand(400,1000),rrand(10,100),10]];
	beta = rrand(0,1);
	gamma = Array.series(rrand(1000,5000), rrand(5000,15000), 5);
	gamma = gamma ++ gamma.reverse;
	alphaB = [[0,20000,300,0.01],[rrand(100,400),rrand(400,1000),rrand(10,100),10]];
	betaB = rrand(0,1);
	gammaB = Array.series(rrand(1000,5000), rrand(5000,15000), 5);
	gammaB = gammaB ++ gammaB.reverse;
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
					RHPF.ar(
					CombC.ar(
					SinOsc.ar(
						TRand.kr(alpha[beta][0],alpha[beta][1], Impulse.kr(alpha[beta][2])),
						0.0,mul:alpha[beta][3]),0.5,0.5,rrand(0.1,10))
						,Demand.kr(Impulse.kr(rrand(33,99)),0,Dseq(gamma,inf)), 0.001)					+
					RHPF.ar(
					CombC.ar(
					SinOsc.ar(
						TRand.kr(alphaB[betaB][0],alphaB[betaB][1], Impulse.kr(alphaB[betaB][2])),
						0.0,mul:alphaB[betaB][3]),0.5,0.5,rrand(0.1,10))
						,Demand.kr(Impulse.kr(rrand(33,99)),0,Dseq(gammaB,inf)), 0.001)
				    ) * (0.25/0.999)*1.0
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
	),0)
};".postln;
		alpha = [[0,20000,300,0.01],[rrand(100,400),rrand(400,1000),rrand(10,100),10]];
		beta = rrand(0,1);
		gamma = Array.series(rrand(1000,5000), rrand(5000,15000), 5);
		gamma = gamma ++ gamma.reverse;
		alphaB = [[0,20000,300,0.01],[rrand(100,400),rrand(400,1000),rrand(10,100),10]];
		betaB = rrand(0,1);
		gammaB = Array.series(rrand(1000,5000), rrand(5000,15000), 5);
		gammaB = gammaB ++ gammaB.reverse;
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
					RHPF.ar(
					CombC.ar(
					SinOsc.ar(
						TRand.kr(alpha[beta][0],alpha[beta][1], Impulse.kr(alpha[beta][2])),
						0.0,mul:alpha[beta][3]),0.5,0.5,rrand(0.1,10))
						,Demand.kr(Impulse.kr(rrand(33,99)),0,Dseq(gamma,inf)), 0.001)					+
					RHPF.ar(
					CombC.ar(
					SinOsc.ar(
						TRand.kr(alphaB[betaB][0],alphaB[betaB][1], Impulse.kr(alphaB[betaB][2])),
						0.0,mul:alphaB[betaB][3]),0.5,0.5,rrand(0.1,10))
						,Demand.kr(Impulse.kr(rrand(33,99)),0,Dseq(gammaB,inf)), 0.001)
				) * (0.25/0.999)*1.0
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		),pan)
	}
}