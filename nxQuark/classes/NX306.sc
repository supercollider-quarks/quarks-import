NX306 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		"NX306:: Multiplica les teves opinions fins que no en puguis tenri més.".postln;
"~nx306 = {
	Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					( 
						RHPF.ar(
					DynKlank.ar(`[
						[Rand(10,100),Rand(10,100),Rand(10,100),Rand(10,100)],
						[Rand(0.25,1),Rand(0.25,1),Rand(0.25,1),Rand(0.25,1)],
						[Rand(0.25,1),Rand(0.25,1),Rand(0.25,1),Rand(0.25,1)]
					],
						EnvGen.ar(Env.new([0,1,0],[Rand(0.01,0.1),Rand(0.01,0.1)],'sine'), Impulse.kr(
							TRand.kr(1,100),Impulse.kr(Rand(1,100)))), TRand.kr(1.0,10.0,Impulse.kr(Rand(0.1,1)))), Rand(10000,12000), 0.001)
					)  * (1.00/0.999)*1.0
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
	),0)
};".postln;
		^Pan2.ar(LeakDC.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(
						RHPF.ar(
					DynKlank.ar(`[
						[Rand(10,100),Rand(10,100),Rand(10,100),Rand(10,100)],
						[Rand(0.25,1),Rand(0.25,1),Rand(0.25,1),Rand(0.25,1)],
						[Rand(0.25,1),Rand(0.25,1),Rand(0.25,1),Rand(0.25,1)]
					],
						EnvGen.ar(Env.new([0,1,0],[Rand(0.01,0.1),Rand(0.01,0.1)],'sine'), Impulse.kr(
							TRand.kr(1,100),Impulse.kr(Rand(1,100)))), TRand.kr(1.0,10.0,Impulse.kr(Rand(0.1,1)))), Rand(10000,12000), 0.001)
					)  * (1.00/0.999)* vol 
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4))
		,pan)
	}
}