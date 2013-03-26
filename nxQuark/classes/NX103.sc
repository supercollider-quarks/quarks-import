NX103 {
	*ar {
		arg sample=1,vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var temp,droneevent,droneevent_select;
		"NX103:: Estira les teves cames i dorm.".postln;
"
~nx103 = {
	var temp,droneevent,droneevent_select;
	droneevent = [
		[rrand(0.001,10.0),rrand(0.001,10.0)], 
		[0.009,0.009] 
	];
	droneevent_select = rrand(1,1);
	temp = TRand.kr(0.003,0.05,Impulse.kr(1/(
		droneevent[droneevent_select][0]
		+droneevent[droneevent_select][1])));
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(RHPF.ar(RLPF.ar(
					  PlayBuf.ar(1,1,0.5,Impulse.kr(Rand(10,100)),BufRateScale.kr(1)/2,1)
					* EnvGen.kr(Env.new([0,1,0],[temp/2,temp/2]),
						Impulse.kr(1/temp))
						,Rand(5000,15000),Rand(0.001,1.0))
						,Rand(8000,15000),0.001))
					* EnvGen.kr(Env.new([0,0.5,1,0.5,0],[
						droneevent[droneevent_select][0]/4,
						droneevent[droneevent_select][0]/4,
						droneevent[droneevent_select][0]/4,
						droneevent[droneevent_select][0]/4],'sine'),
						Impulse.kr(1/(droneevent[droneevent_select][0]
							+droneevent[droneevent_select][1])))
					,0.0,0.0,0.0)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(1.25/0.999)*1.0,0.1)),0)
};
".postln;
	droneevent = [
		[rrand(0.001,10.0),rrand(0.001,10.0)], 
		[0.009,0.009] 
	];
	droneevent_select = rrand(1,1);
	temp = TRand.kr(0.003,0.05,Impulse.kr(1/(
		droneevent[droneevent_select][0]
		+droneevent[droneevent_select][1])));
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					(RHPF.ar(RLPF.ar(
					  PlayBuf.ar(1,1,0.5,Impulse.kr(Rand(10,100)),BufRateScale.kr(1)/2,1)
					* EnvGen.kr(Env.new([0,1,0],[temp/2,temp/2]),
						Impulse.kr(1/temp))
						,Rand(5000,15000),Rand(0.001,1.0))
						,Rand(8000,15000),0.001))
					* EnvGen.kr(Env.new([0,0.5,1,0.5,0],[
						droneevent[droneevent_select][0]/4,
						droneevent[droneevent_select][0]/4,
						droneevent[droneevent_select][0]/4,
						droneevent[droneevent_select][0]/4],'sine'),
						Impulse.kr(1/(droneevent[droneevent_select][0]
							+droneevent[droneevent_select][1])))
					,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(1.25/0.999)*vol,0.1)),pan)
	}
}

