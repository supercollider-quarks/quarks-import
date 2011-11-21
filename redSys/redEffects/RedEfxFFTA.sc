//redFrik

//phase problem when xfading? - keep either full on or full off

RedEfxFFTA : RedEffectModule {
	*def {
		^SynthDef(\redEfxFFTA, {|out= 0, mix= -1, gain= 1, min= 10, max= 30, smooth= 0|
			var dry, wet, chain;
			dry= In.ar(out, 2);
			chain= FFT(LocalBuf(2048), dry);
			chain= PV_MagAbove(chain, min);
			chain= PV_MagBelow(chain, max);
			chain= PV_MagSmooth(chain, smooth);
			wet= IFFT(chain);
			ReplaceOut.ar(out, XFade2.ar(dry, wet, mix, gain));
		}, metadata: (
			specs: (
				\out: \audiobus.asSpec,
				\mix: ControlSpec(-1, 1, 'lin', 0, -1),
				\gain: ControlSpec(0, 4, 'lin', 0, 1),
				\min: ControlSpec(0, 50, 'lin', 0, 10),
				\max: ControlSpec(0, 50, 'lin', 0, 30),
				\smooth: ControlSpec(0, 1, 'lin', 0, 0)
			),
			order: [
				\out -> \fftaOut,
				\mix -> \fftaMix,
				\gain -> \fftaGain,
				\min -> \fftaMin,
				\max -> \fftaMax,
				\smooth -> \fftaSmooth
			]
		));
	}
}
