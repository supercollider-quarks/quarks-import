NX001 {
	*ar {
		arg vol=0.0,pan=0,q1=6,q2=0,q3=0,q4=6,v1=0.0,v2=0.0,v3=0.0;
		var scale,t,fund,freq,vibdepth,ffreq,cutoff;
		"NX001:: Cada acumulació representa una mica més de tristesa.".postln;
		"Original code: http://sccode.org/1-4R2 «ambient scifi pads» by xffff".postln;
"
~nx001 = { var scale,t,fund,freq,vibdepth,ffreq,cutoff;
	scale = [0,3,4,5];
    t = [16,32].choose;
	fund = 16+[0,3,4,5,6,7].choose;
	freq = (scale.choose+fund+(12*(0..3).choose)).midicps;
    vibdepth = (90..500).choose.reciprocal;
    cutoff = freq * (1.1,1.2..4).choose;
    ffreq = max( EnvGen.kr(Env.linen(300.0.rand/t,300.0.rand/t,300.0.rand/t))*freq*12,cutoff)+100;
	Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
	RLPF.ar(
		MoogFF.ar(
			Pulse.ar( [
				(SinOsc.kr(t/(1..10).choose).range(vibdepth.neg,vibdepth)+1) * Line.kr(freq,freq*(0.99,0.991..1.01).choose,t)
				,freq*(1+LFNoise0.kr(t,0.02,1))
	])
		,ffreq,LFNoise2.kr(1/t,1.4,1.5)).tanh
		,1e4,0.9).tanh
		 ,0.8,0.8,0.8)
		 ,100,6.0,6),1000,6.0,0),5000,6.0,0),10000,6.0,6)
		 ,(0.5/0.999)*1.0,0.1)),0)
};
".postln;
		scale = [0,3,4,5];
		t = [16,32].choose;
		fund = 16+[0,3,4,5,6,7].choose;
		freq = (scale.choose+fund+(12*(0..3).choose)).midicps;
		vibdepth = (90..500).choose.reciprocal;
		cutoff = freq * (1.1,1.2..4).choose;
		ffreq = max( EnvGen.kr(Env.linen(300.0.rand/t,300.0.rand/t,300.0.rand/t))*freq*12,cutoff)+100;
		^Pan2.ar(LeakDC.ar(Normalizer.ar(
				BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(BPeakEQ.ar(FreeVerb.ar(
					RLPF.ar(
						MoogFF.ar(
							Pulse.ar( [
								(SinOsc.kr(t/(1..10).choose).range(vibdepth.neg,vibdepth)+1) * Line.kr(freq,freq*(0.99,0.991..1.01).choose,t)
								,freq*(1+LFNoise0.kr(t,0.02,1))
							])
							,ffreq,LFNoise2.kr(1/t,1.4,1.5)).tanh
						,1e4,0.9).tanh
					,v1,v2,v3)
		 ,100,6.0,q1),1000,6.0,q2),5000,6.0,q3),10000,6.0,q4)
		 ,(0.5/0.999)*vol,0.1)),pan)
	}
}