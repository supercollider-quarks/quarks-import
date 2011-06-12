
// implementation and concept by Alberto de Campo, Rainer Schütz, Julian Rohrhuber
// developed for the virtual gamelan graz 2007



// abstact superclock //

SoftClock : TempoClock {

	var <>dt = 0.1, <>verbose = false;
	var fadeTask, fading=false, isPlaying=true;
	var <rateOfChange = 1.0;
	var <>beatWrap;
	
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
	
	prWrapPhase { arg beats;
		^if(beatWrap.isNil) {
			beats
		} {
			beats.wrap2(beatWrap * 0.5)
		}
	}
	
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
		var phaseDiff = this.prWrapPhase(deltaBeats);
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
		listener = this.makeTask({ others !? { this.adjust } });
		listener.play;
	}
	
	stopListen {
		listener.stop;
	}
	
	isListening {
		^listener.isPlaying
	}
	
	allPermanent { ^others.every(_.permanent) && this.permanent }
	
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
	
	othersMeanBeat {
		if(others.isNil) { ^nil };
		^if(weights.isNil) {
			others.collect(_.elapsedBeats).mean
		} {
			others.collect(_.elapsedBeats).mean {|x, i| x * weights[i] } * weights.size
		}
	}
	
	

}


PseudoClock {
	var <tempo, lastBeats, <lastUpdate;
	
	update { arg argTempo, argBeats;
		tempo = argTempo;
		lastBeats = argBeats;
		lastUpdate = Main.elapsedTime;
	}
	
	elapsedBeats {
		^lastBeats + (tempo * 	(Main.elapsedTime - lastUpdate))
	}
	
	permanent { ^true }
}


PseudoNetClock : PseudoClock {
	var <>id = 0, <channel;
	var <responder;
	var <>channel;
	
	classvar <cmd = \netClockSet;
	
	*new { arg id, channel = \telepathicClock;
		^super.newCopyArgs(id, channel)	
	}
	
	startListen {
		// cmd, channel, id, tempo, beats
		responder = OSCresponderNode(nil, cmd, { |t, r, msg|
			if(msg[1] == id) {
				if(msg[2] == channel) {
					this.update(msg[3], msg[4])
				}
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
	classvar <addParticipantCmd = \addParticipant;
	
	
	addParticipant { |id|
		var clock;
		if(others.any { |clock| try { clock.id == id } ? false }.not) { // allow other (local) clocks
				clock = PseudoNetClock(id, channel).startListen;
				this.addClock(clock);
				weights = nil; // for now.
		}
	}
	
	removeParticipant { |id|
		var index = others.detectIndex { |clock| try { clock.id == id } ? false };
		var clock;
		if(index.notNil) {
			clock = others.removeAt(index);
			// todo: weights
			clock.stopListen;
		};
	}
	
	startListen {
		// cmd, channel, flag (1= add, 0 = remove), id
		responder = OSCresponderNode(nil, addParticipantCmd, { |t, r, msg, replyAddr|
			if(msg[1] == channel) {
				if(msg[2] > 0) {
					this.addParticipant(msg[3])
				} {
					this.removeParticipant(msg[3])
				}
			}
		});
		others.do { |clock| try { clock.startListen } };
		responder.add;
	}
	
	stopListen {
		others.do { |clock| try { clock.stopListen } };
		responder.remove;
	}
	
		
}


+ TempoClock {
	
	initTeleport { |addr, id, channel = \telepathicClock|
		addr.sendMsg(TelepathicClock.addParticipantCmd, channel, 1, id);
	}
	
	endTeleport { |addr, id, channel = \telepathicClock|
		addr.sendMsg(TelepathicClock.addParticipantCmd, channel, 0, id);
	}
	
	teleport { |addr, id, channel = \telepathicClock|
		addr.sendMsg(PseudoNetClock.cmd, channel, id, this.tempo, this.elapsedBeats)
	}
}


/*

// usage:

// local clock:
t = TelepathicClock.new.startListen;
n = NetAddr("255.255.255.255", 57120);
t.initTeleport(n, 7);
t.teleport(n, 7); // put in regular task or so...
t.endTeleport(n, 7); // end
t.stopListen;



*/

