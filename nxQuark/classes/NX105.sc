NX105 {
	*ar {
		arg sample=1,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		"NX105:: Tots els actes donen fruits.".postln;
"~nx105 = {
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					Pan2.ar(MoogFF.ar(Osc.ar(1,rrand(10,200),rrand(0.0,1.0))
						,rrand(300,600),rrand(0.1,1.0)),-1)+
			 MoogFF.ar(Osc.ar(1,rrand(10,200),rrand(0.0,1.0)),rrand(300,600),rrand(0.1,1.0))+
			 Pan2.ar(MoogFF.ar(Osc.ar(1,rrand(10,200),rrand(0.0,1.0)),rrand(300,600),rrand(0.1,1.0)),1)
		 ,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(1.00/0.999)*1.0,0.1)),0)
};".postln;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					Pan2.ar(MoogFF.ar(Osc.ar(1,rrand(10,200),rrand(0.0,1.0))
						,rrand(300,600),rrand(0.1,1.0)),-1)+
			 MoogFF.ar(Osc.ar(1,rrand(10,200),rrand(0.0,1.0)),rrand(300,600),rrand(0.1,1.0))+
			 Pan2.ar(MoogFF.ar(Osc.ar(1,rrand(10,200),rrand(0.0,1.0)),rrand(300,600),rrand(0.1,1.0)),1)
					,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.00/0.999)*vol,0.1)),pan)
	}
}