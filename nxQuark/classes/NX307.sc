NX307 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var alpha,beta,gamma;
		"NX307:: Hi ha un natura inherentment positiva dins nostre.".postln;
"~nx307 = { var alpha,beta,gamma;
	beta = rrand(5,10);
	alpha = TRand.kr(100,300,Impulse.kr((1/(beta*2))/8));
	gamma = TRand.kr(100,300,Impulse.kr((1/((beta/2)*2))/8));
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					GVerb.ar(Mix.new([
					EnvGen.kr(Env.new([0,1,0],[beta,beta],'sine'), Impulse.kr(1/(beta*2)))*
					HPF.ar(
					Gendy1.ar([1,2,4].choose,
						TRand.kr(1,4,Impulse.kr((1/(beta*2))/8))
						,1,1,alpha,alpha,0.5,0.5,rrand(7,12))
						,TRand.kr(500,5000,Impulse.kr((1/(beta*2))/4))),
					EnvGen.kr(Env.new([0,1,0],[beta/2,beta/2],'sine'), Impulse.kr(1/((beta/2)*2)))*
					HPF.ar(
					Gendy1.ar([1,2,4].choose,
						TRand.kr(1,4,Impulse.kr((1/((beta/2)*2))/8))
						,1,1,gamma,gamma,0.5,0.5,rrand(7,12))
						,TRand.kr(500,5000,Impulse.kr((1/((beta/2)*2))/4)))
					]),rrand(1,100),rrand(10,100),0.5,0.5,15,1,0.7,0.5,300) * (0.1/0.999)*1.0
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ),0)
};".postln;
		beta = rrand(5,10);
		alpha = TRand.kr(100,300,Impulse.kr((1/(beta*2))/8));
		gamma = TRand.kr(100,300,Impulse.kr((1/((beta/2)*2))/8));
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					GVerb.ar(Mix.new([
					EnvGen.kr(Env.new([0,1,0],[beta,beta],'sine'), Impulse.kr(1/(beta*2)))*
					HPF.ar(
					Gendy1.ar([1,2,4].choose,
						TRand.kr(1,4,Impulse.kr((1/(beta*2))/8))
						,1,1,alpha,alpha,0.5,0.5,rrand(7,12))
						,TRand.kr(500,5000,Impulse.kr((1/(beta*2))/4))),
					EnvGen.kr(Env.new([0,1,0],[beta/2,beta/2],'sine'), Impulse.kr(1/((beta/2)*2)))*
					HPF.ar(
					Gendy1.ar([1,2,4].choose,
						TRand.kr(1,4,Impulse.kr((1/((beta/2)*2))/8))
						,1,1,gamma,gamma,0.5,0.5,rrand(7,12))
						,TRand.kr(500,5000,Impulse.kr((1/((beta/2)*2))/4)))
					]),rrand(1,100),rrand(10,100),0.5,0.5,15,1,0.7,0.5,300) * (0.1/0.999)*vol
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ),pan)
	}
}