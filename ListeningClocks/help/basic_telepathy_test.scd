/*
The plan is to reimplement the not-quite-correct TelepathicClock
as to match a future time sharing protocol for laptop ensembles.
TelepathicClock may change implementation.
ListeningClock and SoftClock will stay the same.
*/

(
// basic test:
a = ReferenceClock.new(TempoClock.default.tempo, TempoClock.default.elapsedBeats);
b = ListeningClock.new(TempoClock.default.tempo, TempoClock.default.elapsedBeats);
b.permanent_(true);
b.addClock(a, 1);
b.startListen;
b.verbose = true;
);

// push reference into the future
a.adjust(1, b.elapsedBeats + 3);
// push reference into the past
a.adjust(1, b.elapsedBeats - 3);

// regular updates
fork { loop { 1.wait; a.adjust(1, b.elapsedBeats + 0.1.rand2) } }


(
SynthDef(\Pdefhelp, { arg out, freq, sustain=1, amp=1, pan;
    var env = EnvGen.ar(Env.perc(0.01, sustain), 1, doneAction:2);
    Out.ar(out, Pan2.ar(SinOsc.ar(freq, 0.5pi, amp * env), pan));
}).add;
)

(
Pbind(\instrument, \Pdefhelp, \dur, 0.5, \note, Pseq([0, 3, 6, 7], inf)).play(b, quant: 1);
Pbind(\instrument, \Pdefhelp, \dur, 0.5, \note, Pseq([0, 3, 6, 7] + 5, inf)).play(TempoClock.default, quant: 1);
fork { loop { 1.wait; a.adjust(TempoClock.default.tempo, TempoClock.default.elapsedBeats) } }
);

TempoClock.default.tempo = 1.4;
TempoClock.default.tempo = 0.4;
TempoClock.default.tempo = 1.0;
TempoClock.default.tempo = rrand(0.7, 1.2);


b.verbose = false;

Tdef(\x, { loop { 1.4.wait; TempoClock.default.tempo = TempoClock.default.tempo * rrand(0.94, 1.05) } }).play;
b.empathy = 1;
b.confidence = 0.2;

Tdef(\x, { loop { 1.4.wait; TempoClock.default.tempo = TempoClock.default.tempo * rrand(0.8, 1.2) } }).play;
Tdef(\x, { loop { 1.4.wait; TempoClock.default.tempo = TempoClock.default.tempo  } }).play;
TempoClock.default.tempo = 2;


b.stop;


