NX005 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var arrayA, velA, modulatorA, mod1A, mod2A, mod3A,
		arrayB, velB, modulatorB, mod1B, mod2B, mod3B,
		arrayC, velC, modulatorC, mod1C, mod2C, mod3C,
		arrayD, velD, modulatorD, mod1D, mod2D, mod3D;
		arrayA = Array.series(rrand(1200,2500),rrand(2.5,5.0),0.001);
		arrayA = arrayA ++ arrayA.reverse;
		velA = Demand.kr(Impulse.kr(rrand(0.5,1)),0,Dseq(arrayA,inf));
		modulatorA = LFSaw.ar(velA, 1, 0.5, 0.5);
		mod2A = (modulatorA * 40.6 * 2pi).cos.squared;
		mod3A = modulatorA * 3147;
		mod3A = (mod3A * 2pi).cos + ((mod3A * 2 * 2pi).cos * 0.3);
		mod1A = ((Wrap.ar(modulatorA.min(0.1714) * 5.84) - 0.5).squared * (-4) + 1) * (mod2A * mod3A);
		mod1A = (mod1A * 0.1)!2;
		arrayB = Array.series(rrand(1200,2500),rrand(2.5,5.0),0.001);
		arrayB = arrayB ++ arrayB.reverse;
		velB = Demand.kr(Impulse.kr(rrand(0.5,1)),0,Dseq(arrayB,inf));
		modulatorB = LFSaw.ar(velB, 1, 0.5, 0.5);
		mod2B = (modulatorB * 40.6 * 2pi).cos.squared;
		mod3B = modulatorB * 3147;
		mod3B = (mod3B * 2pi).cos + ((mod3B * 2 * 2pi).cos * 0.3);
		mod1B = ((Wrap.ar(modulatorB.min(0.1714) * 5.84) - 0.5).squared * (-4) + 1) * (mod2B * mod3B);
		mod1B = (mod1B * 0.1)!2;
		arrayC = Array.series(rrand(1200,2500),rrand(2.5,5.0),0.001);
		arrayC = arrayC ++ arrayC.reverse;
		velC = Demand.kr(Impulse.kr(rrand(0.5,1)),0,Dseq(arrayC,inf));
		modulatorC = LFSaw.ar(velC, 1, 0.5, 0.5);
		mod2C = (modulatorC * 40.6 * 2pi).cos.squared;
		mod3C = modulatorC * 3147;
		mod3C = (mod3C * 2pi).cos + ((mod3C * 2 * 2pi).cos * 0.3);
		mod1C = ((Wrap.ar(modulatorC.min(0.1714) * 5.84) - 0.5).squared * (-4) + 1) * (mod2C * mod3C);
		mod1C = (mod1C * 0.1)!2;
		arrayD = Array.series(rrand(1200,2500),rrand(2.5,5.0),0.001);
		arrayD = arrayD ++ arrayD.reverse;
		velD = Demand.kr(Impulse.kr(rrand(0.5,1)),0,Dseq(arrayD,inf));
		modulatorD = LFSaw.ar(velD, 1, 0.5, 0.5);
		mod2D = (modulatorD * 40.6 * 2pi).cos.squared;
		mod3D = modulatorD * 3147;
		mod3D = (mod3D * 2pi).cos + ((mod3D * 2 * 2pi).cos * 0.3);
		mod1D = ((Wrap.ar(modulatorD.min(0.1714) * 5.84) - 0.5).squared * (-4) + 1) * (mod2D * mod3D);
		mod1D = (mod1D * 0.1)!2;
		"NX005:: Ràpid! renuncia a les vanes esperances, acudeix en la teva pròpia ajuda.".postln;
		"Original code:  http://sccode.org/1-4QB «Insects» by DSastre".postln;
"~nx005 = {	var arrayA, velA, modulatorA, mod1A, mod2A, mod3A,
	arrayB, velB, modulatorB, mod1B, mod2B, mod3B,
	arrayC, velC, modulatorC, mod1C, mod2C, mod3C,
	arrayD, velD, modulatorD, mod1D, mod2D, mod3D;
	arrayA = Array.series(rrand(1200,2500),rrand(2.5,5.0),0.001);
	arrayA = arrayA ++ arrayA.reverse;
	velA = Demand.kr(Impulse.kr(rrand(0.5,1)),0,Dseq(arrayA,inf));
	modulatorA = LFSaw.ar(velA, 1, 0.5, 0.5);
	mod2A = (modulatorA * 40.6 * 2pi).cos.squared;
	mod3A = modulatorA * 3147;
	mod3A = (mod3A * 2pi).cos + ((mod3A * 2 * 2pi).cos * 0.3);
	mod1A = ((Wrap.ar(modulatorA.min(0.1714) * 5.84) - 0.5).squared * (-4) + 1) * (mod2A * mod3A);
	mod1A = (mod1A * 0.1)!2;
	arrayB = Array.series(rrand(1200,2500),rrand(2.5,5.0),0.001);
	arrayB = arrayB ++ arrayB.reverse;
	velB = Demand.kr(Impulse.kr(rrand(0.5,1)),0,Dseq(arrayB,inf));
	modulatorB = LFSaw.ar(velB, 1, 0.5, 0.5);
	mod2B = (modulatorB * 40.6 * 2pi).cos.squared;
	mod3B = modulatorB * 3147;
	mod3B = (mod3B * 2pi).cos + ((mod3B * 2 * 2pi).cos * 0.3);
	mod1B = ((Wrap.ar(modulatorB.min(0.1714) * 5.84) - 0.5).squared * (-4) + 1) * (mod2B * mod3B);
	mod1B = (mod1B * 0.1)!2;
	arrayC = Array.series(rrand(1200,2500),rrand(2.5,5.0),0.001);
	arrayC = arrayC ++ arrayC.reverse;
	velC = Demand.kr(Impulse.kr(rrand(0.5,1)),0,Dseq(arrayC,inf));
	modulatorC = LFSaw.ar(velC, 1, 0.5, 0.5);
	mod2C = (modulatorC * 40.6 * 2pi).cos.squared;
	mod3C = modulatorC * 3147;
	mod3C = (mod3C * 2pi).cos + ((mod3C * 2 * 2pi).cos * 0.3);
	mod1C = ((Wrap.ar(modulatorC.min(0.1714) * 5.84) - 0.5).squared * (-4) + 1) * (mod2C * mod3C);
	mod1C = (mod1C * 0.1)!2;
	arrayD = Array.series(rrand(1200,2500),rrand(2.5,5.0),0.001);
	arrayD = arrayD ++ arrayD.reverse;
	velD = Demand.kr(Impulse.kr(rrand(0.5,1)),0,Dseq(arrayD,inf));
	modulatorD = LFSaw.ar(velD, 1, 0.5, 0.5);
	mod2D = (modulatorD * 40.6 * 2pi).cos.squared;
	mod3D = modulatorD * 3147;
	mod3D = (mod3D * 2pi).cos + ((mod3D * 2 * 2pi).cos * 0.3);
	mod1D = ((Wrap.ar(modulatorD.min(0.1714) * 5.84) - 0.5).squared * (-4) + 1) * (mod2D * mod3D);
	mod1D = (mod1D * 0.1)!2;
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					Mix.new([mod1A,mod1B,mod1C,mod1D]!2)
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(0.05/0.999)*1.0,0.1)),0)
};".postln;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					Mix.new([mod1A,mod1B,mod1C,mod1D]!2)
					,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(0.05/0.999)*vol,0.1)),pan)
	}
}