NX007 {
	*ar {
		arg  
		vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
        "NX007:: El jo és ignorancia.".postln;
"~nx007 = { 
	Pan2.ar(LeakDC.ar(Normalizer.ar(
	BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
		HPF.ar(HPF.ar(
		Pulse.ar(rrand(50,5500),rrand(0.1,1.0),0.1) +
		Pulse.ar(rrand(5,300),rrand(0.1,1.0),0.1),
			10000,0.01),20000,1)
			 ,rrand(0.0,1.0),rrand(0.0,1.0),rrand(0.0,1.0))
			,100,6.0,rrand(-12,12)),1000,6.0,rrand(-12,12)),5000,6.0,rrand(-12,12)),10000,6.0,rrand(-12,12))
			,(1.33/0.999)*1,0.1)),0)
    };".postln;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
	     BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
		 HPF.ar(HPF.ar(
		Pulse.ar(rrand(50,5500),rrand(0.1,1.0),0.1) +
		Pulse.ar(rrand(5,300),rrand(0.1,1.0),0.1),
			10000,0.01),20000,1)
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.33/0.999)*vol,0.1)),pan) }
}	
