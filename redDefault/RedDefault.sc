//redFrik
/*
	s.boot; Pbind(\dur, 2).play;
	now run the examples in [Streams-Patterns-Events5] and [Streams-Patterns-Events6]
*/

RedDefault {
	*initClass {
		Class.initClassTree(Event);
		StartUp.add{
		
			//overwrites the def that *makeDefaultSynthDef created in Event.sc
			SynthDef(\default, {|out= 9, freq= 9, amp= 9, pan= 9, gate= 9, dur= 9|
				var d, f, e, a, z;
				d= dur*LinRand(0.9, 9);
				f= freq.cpsmidi.round(6-(dur*3).round(3)).midicps*0.5;
				e= EnvGen.kr(Env.perc(0.009, d-amp), gate);
				Linen.kr(gate, 0.009, 1, dur.max(d-amp), 2);
				a= FSinOsc.ar(LinRand(9, 99*freq).round(9), 0, LinRand(0.09, 0.999-dur), amp);
				z= Pulse.ar(f, PinkNoise.kr((ExpRand(0.009, dur*0.9)*amp).min(0.45), 0.45), a);
				z= RLPF.ar(z, (f*9*dur).max(f*4.5), LinRand(0.09, 4.5)).softclip;
				OffsetOut.ar(out, Pan2.ar(z*e, pan));
			}, #[\ir]).store;
		"".postln;
		" redDefault hack just replaced the file default.scsyndef in your synthdefs directory.\n to get back to boring normal, delete this file (ie default.scsyndef), uninstall redDefault\n with the command Quarks.uninstall(\"redDefault\"), and recompile.\n thank you for choosing red inc systems.".warn;
		}
	}
}

//RedEvent hack might come later
