
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
	var <others, <>weights;
	classvar <>all;
	
	adjust {
		this.prAdjust(this.othersMeanBeat - this.elapsedBeats, this.othersMeanTempo)
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
	
	othersMeanTempo {
		^if(weights.isNil) {
			others.collect(_.tempo).mean
		} {
			others.collect(_.tempo).mean {|x, i| x * weights[i] } * weights.size
		}
	}
	
	othersMeanBeat {
		^if(weights.isNil) {
			others.collect(_.elapsedBeats).mean
		} {
			others.collect(_.elapsedBeats).mean {|x, i| x * weights[i] } * weights.size
		}
	}
	
	

}


TelepathicClock : SoftClock {
	
	
	var <>empathy = 0.5, <>confidence=0.5;
	
	var <othersMeanTempo, <othersMeanBeat;
	var <othersTempo, <othersBeat;
	
	var <channel, <>addr, <responder;
	var <listener, token;
	var <manual = false;
	
	classvar <>all;
	
	*new { arg tempo, beats, seconds, queueSize=256, permanent=false, addr, channel, start=true;
		^super.new.initNetwork(tempo, beats, seconds, queueSize, permanent, 
			addr, channel, start)
	}
	
	adjust { |argBeats, argTempo|
		this.prAdjust(argBeats - this.elapsedBeats, argTempo)
	}

	
	// listens initially by default
	
	startListen {
		responder = OSCresponderNode(nil, channel, { |t, r, msg|
			if(msg[3] != token and: { manual.not }) {
				this.adjust(msg[1], msg[2])
			}
		});
		responder.add;
		listener = this.makeTask({ this.share });
		listener.play;
	}
	
	stopListen {
		listener.stop;
		responder.remove;
	}
	
	getBroadcastAddr {
		var ip;
		try { ip = NetAddr.broadcastIP };
		^if(ip.notNil) { NetAddr(ip, 57120) }
	}
	
	initNetwork { arg tempo, beats, seconds, queueSize, argPermanent, 
		argAddr, argChannel, start;
		
		permanent = argPermanent;
		channel = argChannel ? '/teleclock';
		this.init(tempo, beats, seconds, queueSize);
		
		if(NetAddr.broadcastFlag.not) { "broadcast was switched on.".postln };
		NetAddr.broadcastFlag = true;
		
		addr = argAddr ?? { this.getBroadcastAddr };
		token = Date.seed; // send a token: do not listen to myself
		
		othersTempo = IdentityDictionary.new;
		othersBeat = IdentityDictionary.new;
		
		if(start) { this.startListen };
	}
	
	share {
		if(addr.isNil) { 
			"TeleClock: No broadcast address found. Please contact your system".warn;
			addr = this.getBroadcastAddr; // try again.
		} {
			addr.sendMsg(channel, this.beats, this.tempo, token) 
		}
	}
	
	fadeTempo { arg newTempo, dur = 1.0, warp = \cos, clock;
		manual = true;
		super.fadeTempo(newTempo, dur, warp, clock);
		(clock ? SystemClock).sched(dur, { manual = false; nil; });
	}
		
}
