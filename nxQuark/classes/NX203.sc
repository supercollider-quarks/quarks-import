NX203 {
	*ar {
	arg inlet,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
	var alpha, beta, gamma, delta;
	"NX203:: Tots tenim el mateix estany sota la ment.".postln;
	"
~nx203 = { var alpha, beta, gamma,delta;
	delta = rrand(0,1000);
	alpha = Array.fill(delta, rrand(5000.0,15000.1), 1);
	alpha = alpha ++ alpha.reverse;
	beta = Array.fill(delta, rrand(0.001,0.01), 0.001);
	beta = beta ++ beta.reverse;
	gamma = rrand(0.01,0.001);
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
						RHPF.ar(
							EnvGen.kr(Env.new([0,1,0],[gamma/2,gamma/2],'step'),Impulse.kr(1/gamma))*
							InFeedback.ar(~a.index)
							, Demand.kr(Impulse.kr(Rand(0.5,10)), 0, Dseq(alpha,inf))
							, Demand.kr(Impulse.kr(Rand(0.5,10)), 0, Dseq(beta,inf)))
					) * (0.8/0.999) * 1
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ),0)
};".postln;
		delta = rrand(0,1000);
		alpha = Array.fill(delta, rrand(5000.0,15000.1), 1);
		alpha = alpha ++ alpha.reverse;
		beta = Array.fill(delta, rrand(0.001,0.01), 0.001);
		beta = beta ++ beta.reverse;
		gamma = rrand(0.01,0.001);
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
						RHPF.ar(
							EnvGen.kr(Env.new([0,1,0],[gamma/2,gamma/2],'step'),Impulse.kr(1/gamma))*
							InFeedback.ar(inlet)
							, Demand.kr(Impulse.kr(Rand(0.5,10)), 0, Dseq(alpha,inf))
							, Demand.kr(Impulse.kr(Rand(0.5,10)), 0, Dseq(beta,inf)))
					) * (0.8/0.999) * vol ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		),pan)
	}		
}
