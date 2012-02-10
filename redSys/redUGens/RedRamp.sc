//redFrik

//resettable phasor with a per period rate control

RedRamp {
	*ar {|dur= 1, reset= 0, mul= 1, add= 0|
		var timer= Duty.ar(dur, reset, dur);
		var trg= HPZ1.ar(timer).abs>0;
		^MulAdd(Sweep.ar(trg, 1/dur), mul, add);
	}
	*kr {|dur= 1, reset= 0, mul= 1, add= 0|
		var timer= Duty.kr(dur, reset, dur);
		var trg= HPZ1.kr(timer).abs>0;
		^MulAdd(Sweep.kr(trg, 1/dur), mul, add);
	}
}
