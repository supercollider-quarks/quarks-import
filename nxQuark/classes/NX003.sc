NX003 {
	*ar{
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var z,alpha,beta,gamma;
	    "NX003:: Si no ho entens, menjat la roba.".postln;
        "Based on cxaudio quark metal by felix".postln;
"~nx003 = { var z,alpha,beta,gamma;
			z=
	[ `(#[ [ 859, 18973, 17077, 17189, 1427, 2441, 6481, 11688, 16411, 1950, 
	  6793, 3088, 8861, 2795, 14281, 11383, 16313, 16301, 4340, 4101 ], nil, [ 0.127808, 0.0160289, 0.0222603, 0.0156637, 0.309441, 0.044308, 0.274721, 0.0520477, 0.0197506, 0.188093 ] ]), `(#[ [ 859, 18973, 17077, 17189, 1427, 2441, 6481, 11688, 16411, 1950, 
	  6793, 3088, 8861, 2795, 14281, 11383, 16313, 16301, 4340, 4101 ], nil, [ 0.287593, 0.0334687, 0.11472, 0.0231302, 0.0367275, 0.0292539, 0.0267082, 0.0609702, 0.0164628, 0.0448704 ] ]) ];
	beta = [[30,100],[700,3000]];
	gamma = beta[rrand(0,1)].postln;
	alpha = rrand(gamma[0],gamma[1]).postln;
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					DynKlank.ar(z, Decay.ar(Impulse.ar(alpha), 0.04, WhiteNoise.ar(0.03)),
						TRand.kr(0.0,
							Demand.kr(Impulse.kr(alpha/100),0,Drand([1,100],inf))
							,Impulse.kr(alpha))
						,0.0,1.0)
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(0.25/0.999)*1.0,0.1)),0)
};".postln;
			z=
	[ `(#[ [ 859, 18973, 17077, 17189, 1427, 2441, 6481, 11688, 16411, 1950, 
	  6793, 3088, 8861, 2795, 14281, 11383, 16313, 16301, 4340, 4101 ], nil, [ 0.127808, 0.0160289, 0.0222603, 0.0156637, 0.309441, 0.044308, 0.274721, 0.0520477, 0.0197506, 0.188093 ] ]), `(#[ [ 859, 18973, 17077, 17189, 1427, 2441, 6481, 11688, 16411, 1950, 
	  6793, 3088, 8861, 2795, 14281, 11383, 16313, 16301, 4340, 4101 ], nil, [ 0.287593, 0.0334687, 0.11472, 0.0231302, 0.0367275, 0.0292539, 0.0267082, 0.0609702, 0.0164628, 0.0448704 ] ]) ];
	beta = [[30,100],[700,3000]];
	gamma = beta[rrand(0,1)].postln;
	alpha = rrand(gamma[0],gamma[1]).postln;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
										DynKlank.ar(z, Decay.ar(Impulse.ar(alpha), 0.04, WhiteNoise.ar(0.03)),
						TRand.kr(0.0,
							Demand.kr(Impulse.kr(alpha/100),0,Drand([1,100],inf))
							,Impulse.kr(alpha))
						,0.0,1.0)
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(0.25/0.999)*vol,0.1)),pan)
	}
}
