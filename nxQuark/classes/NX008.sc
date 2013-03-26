NX008 {
	*ar {
		arg  
		vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.1,v2=0.1,v3=0.1;
        "NX008:: Hi ha un refugi d'alliberament.".postln;
"~nx008 = { 
	Pan2.ar(LeakDC.ar(Normalizer.ar(
	BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
		SinOsc.ar(rrand(100,300),0.1)+Saw.ar(rrand(10,99),0.1)
			 ,rrand(0.0,1.0),rrand(0.0,1.0),rrand(0.0,1.0))
			,100,6.0,rrand(-12,12)),1000,6.0,rrand(-12,12)),5000,6.0,rrand(-12,12)),10000,6.0,rrand(-12,12))
			,(1.33/0.999)*1,0.1)),0)
    };".postln;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
	     BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
			 SinOsc.ar(rrand(100,300),0.1)+Saw.ar(rrand(10,99),0.1)
		 ,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.33/0.999)*vol,0.1)),pan) }
}	
