
// implementation and concept by Alberto de Campo, Rainer Schütz, Julian Rohrhuber
// developed for the virtual gamelan graz 2007



// abstact superclock //

SoftClock : TempoClock {

	var <>dt = 0.1, <>verbose = false;
	var fadeTask, fading=false, isPlaying=true;
	var <rateOfChange = 1.0;
	
	classvar <>all;
	
	add {
		this.class.all = this.class.all.add(this);
	}
	
	*stopAll {
		this.all.do { |clock|
			clock.permanent = false;
			if(clock.isPlaying) { clock.stop };
		};
		this.all = nil;
	}
	
	stop {
		this.stopListen;
		isPlaying = false;
		super.stop;
		if(this.class.all.notNil) { this.class.all.take(this) };
		if(verbose) { ("stopped clock:" + this).postln };
	}
	
	tempo_ { arg newTempo;
		newTempo = max(newTempo, 1e-256); // zero not allowed.
		rateOfChange = newTempo / this.tempo;
		super.tempo = newTempo;
	}
	
	pause { arg dur = 0;
		this.fadeTempo(0.0, dur)
	}
	
	
	fadeTempo { arg newTempo, dur = 1.0, warp = \cos, clock;
		var start = this.tempo, interpol;
		warp = warp.asWarp;
		if (warp.isKindOf(ExponentialWarp)) { warp.spec.minval_(0.01) };
		if (fading) { fadeTask.stop }; 
		fadeTask = Task { 
			fading = true;
			"fadeTempo starts. going from: % to: %\n".postf(
				start.round(0.001), newTempo.round(0.001));
			(1 .. (dur / dt + 1).asInteger).normalize.do { |val|
				interpol = blend(start, newTempo, warp.map(val));
				this.tempo = interpol;
				if (verbose) { "fadeTempo index: % tempo: %\n".postf(
					val.round(0.001), interpol.round(0.001)) };
				dt.value.wait;
			};
			fading = false;
			"fadeTempo done. tempo was: % new tempo is: %\n".postf(
				start.round(0.001), interpol.round(0.001));
		}; 
		clock = clock ? SystemClock;
		fadeTask.play(clock);
	}
	
	warpTempo { arg frac, beats = 1.0, warp = \cos;
		this.fadeTempo(frac * this.tempo, beats, warp, this)
	}
	
	
	// subclass interface //
		
	allPermanent { ^permanent }
	
	stopListen { }
	
	makeTask { arg func;
		^if(this.allPermanent) {
			SkipJack({ if(isPlaying, func) }, { dt })
		} {
			Task { loop { dt.wait; if(isPlaying, func) } }
		}
	}
	
	prAdjust { |deltaBeats, argTempo|
		var phaseDiff = deltaBeats;
		var myTempo = this.tempo;
		var timeComp = (phaseDiff * this.empathy); 
		var newTempo = (blend(argTempo, myTempo, this.confidence) + timeComp).max(0.01);
		
		if (verbose 
			and: { (phaseDiff.abs > 0.001)  
			or: { (newTempo - myTempo).abs > 0.001 } }) 
		{ 
			"Clock - adjust - avgDeltaBeats: % 	avgTempo: % timeComp: % newTempo: %"
			.format(*[deltaBeats, argTempo, timeComp, newTempo].round(0.0001)).postln;
		};
		this.tempo_(newTempo);
	}

}


ListeningClock : SoftClock {
	
	var <listener;
	var <>empathy = 0.5, <>confidence=0.5;
	var <>others, <>weights;
	
	classvar <>all;
	
	adjust {
		var tempo = this.othersMeanTempo;
		var beats = this.othersMeanBeats;

		if(tempo.notNil) {
			this.prAdjust(beats - this.elapsedBeats, tempo)
		}
	}
	
	startListen {
		listener.stop;
		listener = this.makeTask({ others !? { this.adjust }; });
		listener.play;
	}
	
	stopListen {
		listener.stop;
	}
	
	isListening {
		^listener.isPlaying
	}
	
	allPermanent { ^if(others.isNil) { true } { others.every(_.permanent) && this.permanent } }
	
	setClocks { arg clocks, argWeights, start=true;
		var listening = start or: { this.isListening };
		this.stopListen;
		if(clocks.isNil or: { clocks.isEmpty }) {
			^this
		};
		others = clocks;
		argWeights !? { weights = argWeights.normalizeSum };
		if(listening) { this.startListen };
	}
	
	addClock { arg clock, weight;
		others = others.add(clock);
		if(weight.notNil) {
			weights = weights.add(weight);
			// todo: make weights un-normalized!
		};
	}
	
	othersMeanTempo {
		if(others.isNil) { ^nil };
		^if(weights.isNil) {
			others.collect(_.tempo).mean
		} {
			others.collect(_.tempo).mean {|x, i| x * weights[i] } * weights.size
		}
	}
	
	othersMeanBeats {
		if(others.isNil) { ^nil };
		^if(weights.isNil) {
			others.collect(_.elapsedBeats).mean
		} {
			others.collect(_.elapsedBeats).mean {|x, i| x * weights[i] } * weights.size
		}
	}
	
	

}


ReferenceClock {
	var clock;
	
	*new { arg tempo, beats;
		^super.new.update(tempo, beats)	
	}
	
	update { arg tempo, beats, beatWrap = (0);
		var clockBeats, beatDifference;
		clock !? {
			clockBeats = clock.elapsedBeats;
			if(beatWrap > 0 and: beats.notNil) {
				beatDifference =  beats - clockBeats;
				beats = beatDifference % beatWrap + clockBeats;
				// [\old, clockBeats, \new, beats, \diff, beatDifference].postln;
			} {
				beats = beats ? clockBeats;
			};
			
			tempo = tempo ? clock.tempo;
			clock.stop;
		};
		clock = TempoClock.new(tempo, beats).permanent_(true)
	}
	
	elapsedBeats {
		^clock.elapsedBeats
	}
	
	tempo {
		^clock.tempo	
	}
	
	permanent { ^true }
}


TelepathicReferenceClock : ReferenceClock {

	var <>id = 0, <>channel;
	var <responder;
	var <>channel;
	
	classvar <cmd = \netClockSet;
	
	*new { arg tempo, beats, id, channel;
		^super.new(tempo, beats).initID(id, channel ? \telepathicClock)
	}
	
	initID { arg argID, argChannel;
		channel = argChannel.postln;
		id = argID;	
	}
	
	startListen {
		// cmd, channel, id, tempo, beats, beatWrap
		responder = OSCresponderNode(nil, cmd, { |t, r, msg|
			if(msg[2] == id and: { msg[1] == channel }) {
					this.update(msg[3], msg[4], msg[5]);
			}
		});
		responder.add;
	}
	
	stopListen {
		responder.remove;
	}
}


TelepathicClock : ListeningClock {
	
	var <>channel = \telepathicClock, <responder;
	
	classvar <addClockSourceCmd = \addClockSource;

	
	addClockSource { |id|
		var clock;
		if(others.isNil or: { others.any { |clock| try { clock.id == id } ? false }.not }) { // allow other (local) clocks
				clock = TelepathicReferenceClock(this.tempo, this.elapsedBeats, id, channel);
				clock.startListen;
				this.addClock(clock);
				weights = nil; // for now.
		}
	}
	
	removeClockSource { |id|
		var index, clock;
		if(others.isNil) { ^this };
		index = others.detectIndex { |clock| try { clock.id == id } ? false };
		if(index.notNil) {
			clock = others.removeAt(index);
			// todo: weights
			clock.stopListen;
		};
	}
	
	startListen {
		super.startListen;
		// cmd, channel, flag (1= add, 0 = remove), id
		responder = OSCresponderNode(nil, addClockSourceCmd, { |t, r, msg, replyAddr|
			if(msg[1] == channel) {
				if(msg[2] > 0) {
					this.addClockSource(msg[3])
				} {
					this.removeClockSource(msg[3])
				}
			}
		});
		others.do { |clock| try { clock.startListen } };
		responder.add;
	}
	
	stopListen {
		super.stopListen;
		others.do { |clock| try { clock.stopListen } };
		responder.remove;
	}
	
	
	
		
}


+ TempoClock {
	
	initTeleport { |addr, id, channel = \telepathicClock|
		addr.sendMsg(TelepathicClock.addClockSourceCmd, channel, 1, id);
	}
	
	endTeleport { |addr, id, channel = \telepathicClock|
		addr.sendMsg(TelepathicClock.addClockSourceCmd, channel, 0, id);
	}
	
	teleport { |addr, id, channel = \telepathicClock, beatWrap|
		addr.sendMsg(TelepathicReferenceClock.cmd, channel, id, this.tempo, this.elapsedBeats, beatWrap)
	}
	
	clearQueue {
		queue.removeAllSuchThat(true);	
	}
}


/*

// usage:

// local clock:
(
t.stop;
t = TelepathicClock.new.permanent_(true);
t.empathy = 0.9;
t.confidence = 0.5;
t.addClockSource(0);

t.verbose = true;
t.startListen;
n = NetAddr("127.0.0.1", 57120);
// TempoClock.default.initTeleport(n, 7);
);

t.others.first.dump




(
SynthDef(\x, { |freq, sustain| Out.ar(0, XLine.kr(0.1, 0.0001, sustain, doneAction: 2) * SinOsc.ar(freq, 0.5pi)) }).add;
Pbind(\freq, 810, \sustain, 0.5, \dur, 1, \instrument, \x).play(t, quant:1);
Pbind(\freq, 2500, \sustain, 0.1, \dur, 1, \instrument, \x).play(TempoClock.default, quant:1);
);

(
TempoClock.default.tempo = rrand(1.0, 3.0);
TempoClock.default.teleport(n, 0, beatWrap: 4);
)


(
Tdef(\x, {
	loop {
		1.wait;
		TempoClock.default.teleport(n, 7);
	};
}).play;

t.others;
t.othersMeanTempo;
t.othersMeanBeats;

TempoClock.default.tempo = 0.125;
TempoClock.default.tempo = 1.9;

t.tempo
t.othersMeanTempo;
t.startListen;

// end
t.endTeleport(n, 7);
t.stopListen;

t.stop;

*/

