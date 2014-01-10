/* reworking CtLoop, as used in GamePad quark.

* records control events in time, as they come from interfaces.

SynthDef(\toc, {
Out.ar(0, XLine.ar(1, 0.01, 0.02, doneAction: 2)
* SinOsc.ar([600, 1300, 4500], 0, [0.3, 0.2, 0.1]));
}).add;

(instrument: \toc).play;

z = TimeLoop(\time, {|ev| (instrument: \toc).play; });
z.verbosity = 2;
z.startRec;
z.recordEvent; (instrument: \toc).play;
z.stopRec;
z.listInfo;
z.printLists;

z.listDur;
z.quantizeTo(0.25, 8);
z.printLists;

z.play;

z.toggleRec;
z.recordEvent;
z.toggleRec;
z.printLists;


z.loop = true;
z.play;
z.tempo = 0.3;
z.tempo = 1;
z.loop = false;
z.reverse;

z.verbosity = 2;
z.loop = true;
z.play;
z.resetLoop;

z.jitter = 1.0;

KeyPlayer

/////////////
k = KeyLoop(\a);
k.keyDict = ($a: { "aaa".speak; }, $b: { "beh".speak }, $c: { "CCC".speak });

k.startRec;
k.verbosity = 2;
k.recordEvent("abc".choose, [\down, \up].choose);
k.stopRec;
k.play;
k.list;

TimeLoop -
- list of event times only
- times are scalable,
- quantizable to tempoclock,
- segments selectable
- step, jitter

- function what to do on event time

- multiple recorded lists can be kept
- switch lists while playing


KeyLoop
- event has time and single key as ID for events,
e.g. char of keystroke on a computer keyboard
-   lookup in dict of functions what to do for each key

KeyLoop2
- can have more args after key that go into the function(s) as args.

KtlLoop - list of key/value pairs
- single func, all set e.g. a specific proxy to new settings
- can rescale parameter values
shift, scale, invert;

KtlLoop2 - list of key/value pairs
- single func, all set e.g. a specific proxy to new settings

AutoLoop - ??

*/

TimeLoop {

	var <key, <>func, <>loop = false;
	var <list, <task, <isRecording = false;
	var recStartTime, then;

	var <>tempo=1, <step=1, <>jitter=0.0;
	var <>lpStart = 0, <>length = 1;

	var <>verbosity = 1;

	var <lists, <currIndex = 0, <maxIndex, <numLists = 0;

	*new { arg key, func;
		^super.newCopyArgs(key, func).init;
	}

	// backwards compat
	isOn { ^isRecording }

	storeArgs { ^[key] }

	printOn { |stream| ^this.storeOn(stream) }

	init {
		func = func ?? { this.defaultFunc };
		lists = List[];
		list = List[];

		this.initTask;
	}

	defaultFunc { ^{ |ev| ev.round(0.001).postln } }

	initTask {

		task = TaskProxy({ |ev|
			var abstime, delta, index, indexOffset, indexPlusOff, event;

			maxIndex = list.size - 1;
			index = (lpStart * maxIndex).round.asInteger;

			if (verbosity > 0) { (this.asString + "task starts.").postln; };

			while { loop or: (index <= maxIndex) } {

				indexOffset = ((jitter.asFloat.squared).bilinrand * maxIndex)
				.round.asInteger;

				indexPlusOff = index + indexOffset;

				event = list.wrapAt(indexPlusOff);
				if (verbosity > 1) {
					String.fill(indexPlusOff, $-).post;
					"i: % - ev: %".format(indexPlusOff, event).postln;
				};
				#abstime, delta = event;

				func.value(event);

				// quantized playback done in delta times
				// how to quant to current TempoClock 16ths?
				(delta / tempo).wait;

				index = (index + step);
				if (loop) {
					index = index.wrap(
						(lpStart * maxIndex).round,
						((lpStart + length.abs).max(1) * maxIndex + 1).round
					).asInteger;
				};
			};
		});
		task.clock_(SystemClock).quant_(0);
	}

	// recording events:

	startRec { |instant = false|
		isRecording = true;
		this.clear;
		task.stop;
		if (verbosity > 0) { "  %.startRec;\n".postf(this) };
		if (instant) {
			recStartTime = then = thisThread.seconds;
		};
	}

	recordEvent { |...args|
		var event;
		if (isRecording) {
			event = this.getTimes ++ args;
			if (verbosity > 1) { (this.asString + "rec: ").post; event.postcs; };
			this.putDeltaInPrev(event[1]);
			list.add(event);
		}
	}

	putDeltaInPrev { |delta|
		if (list.last.notNil) { list.last.put(1,delta) };
	}

	stopRec {
		isRecording = false;
			this.putDeltaInPrev(this.getTimes[1]);
		this.addList;
		if (verbosity > 0) { "  %.stopRec;\n".postf(this) };
	}

	toggleRec { |instant=false|
		if (isRecording, { this.stopRec }, { this.startRec(instant) });
	}

	getTimes {
		var abstime, delta;
		var now = thisThread.seconds;
		if (then.isNil) {
			then = now;
			recStartTime = now;
		};
		delta = now - then;
		abstime = now - recStartTime;
		then = now;
		^[ abstime, delta];
	}

	postRecFix {
		var now, delta;
		now = thisThread.seconds;
		delta = now - then;
		try { list.first.put(1, list.first[1] + delta) };
	}

	clear {
		this.addList;
		list = List[];
		then = recStartTime = nil;
		this.resetLoop;
	}

	// taskproxy for playback interface

	play {
		if (verbosity > 0) { "  %.play;\n".postf(this) };
		isRecording = false;
		task.stop.play;
	}

	togglePlay { if (task.isPlaying, { this.stop }, { this.play }); }

	stop {
		if (verbosity > 0) { "  %.stop;\n".postf(this) };
		task.stop;
	}

	pause { task.pause; }
	resume { task.resume; }
	isPlaying { ^task.isPlaying; }

	resetLoop { lpStart = 0; length = 1; step = 1; tempo = 1; jitter = 0 }

	isReversed { ^step == -1 }
	reverse { step = -1 }
	forward { step = 1 }
	flip { step = step.neg }


	addList {
		if (list.notEmpty and: { lists.first !== list }) {
			lists.addFirst(list);
			numLists = lists.size;
		}
	}

	listInfo { ^lists.collect { |l, i| [i, l.size] } }

	printLists {|round = 0.01|
		this.post; " - lists: ".postln;
		this.listInfo.postln;
		lists.do { |li, i|
			("\nlist" + i).postln;
			li.do { |el, j|
				("\t" + j + el.round(round)).postln;
			}
		};
	}

	setList { |index = 0|
		var newList = lists[index];
		if (newList.isNil) {
			this.post; ": no list at index %.".postf(index);
			^this
		};
		list = newList;
		currIndex = index;
		maxIndex = list.maxIndex;
	}

	listDur { ^list.last.keep(2).sum; }

	quantizeTo { |quant = 0.25, totalDur|
		    // last abstime + last delta, which loops to one
		var realTotalDur = this.listDur;
		var durScaler, quantizedDeltas;
		totalDur = totalDur ? realTotalDur;
		durScaler = totalDur / realTotalDur;

		(list.collect(_[0]).add(realTotalDur) * durScaler)
		    .round(quant)
		    .doAdjacentPairs { |a, b, i|
			    list[i].put(1, b - a);
		    };
	}

	unquantize {
		list.doAdjacentPairs { |a, b|
			a.put(1, b[0] - a[0]);
		};
	}

}

KeyLoop : TimeLoop {
	var <>keyDict;

	// assume single-depth key dict by default:
	defaultFunc { ^{ |ev| keyDict[ev[2]].value.postln }; }

	// or prep for lookup in an existing player, both down and up
	*keyPlayerFunc { |player|
		^{ |ev| player.keyAction(ev[2], which: ev[3]) };
	}

	recordEvent { |key, type|
		super.recordEvent(key, type);
	}
}
