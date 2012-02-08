//redFrik

//resettable phasor with a per period rate control

RedRamp {
	*ar {|freq= 1, reset= 0, mul= 1, add= 0|
		var dur= Duty.ar(freq, reset, freq);
		var trg= HPZ1.ar(dur).abs>0;
		^MulAdd(Sweep.ar(trg, 1/dur), mul, add);
	}
	*kr {|freq= 1, reset= 0, mul= 1, add= 0|
		var dur= Duty.kr(freq, reset, freq);
		var trg= HPZ1.kr(dur).abs>0;
		^MulAdd(Sweep.kr(trg, 1/dur), mul, add);
	}
}
