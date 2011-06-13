
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

	var <>name;
	var responder;
	
	classvar <cmd = \refClockSet;
	
	*new { arg tempo, beats, name;
		^super.new(tempo, beats).name_(name ? \telepathicClock)
	}
	
	startListen {
		// cmd, name, tempo, beats, beatWrap
		responder = OSCresponderNode(nil, cmd, { |t, r, msg|
			if(msg[1] == name) {
					this.update(msg[2], msg[3], msg[4]);
			}
		});
		responder.add;
	}
	
	stopListen {
		responder.remove;
	}
}


TelepathicClock : ListeningClock {
	
	var <>name = \telepathicClock, <responder;
	
	classvar <addClockSourceCmd = \addClockSource;

	
	addClockSource { |name|
		var clock;
		// allow other (local) clocks
		if(others.isNil or: { others.any { |clock| try { clock.name == name } ? false }.not }) { 
			clock = TelepathicReferenceClock(this.tempo, this.elapsedBeats, name);
			clock.startListen;
			this.addClock(clock);
			weights = nil; // for now.
		}
	}
	
	removeClockSource { |name|
		var index, clock;
		if(others.isNil) { ^this };
		index = others.detectIndex { |clock| try { clock.name == name } ? false };
		if(index.notNil) {
			clock = others.removeAt(index);
			// todo: weights
			clock.stopListen;
		};
	}
	
	startListen {
		super.startListen;
		// cmd, name, flag (1= add, 0 = remove)
		responder = OSCresponderNode(nil, addClockSourceCmd, { |t, r, msg, replyAddr|
			if(msg[1] == name) {
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
	
	initTeleport { |addr, name = \telepathicClock|
		addr.sendMsg(TelepathicClock.addClockSourceCmd, name, 1);
	}
	
	endTeleport { |addr, name = \telepathicClock|
		addr.sendMsg(TelepathicClock.addClockSourceCmd, name, 0);
	}
	
	teleport { |addr, name = \telepathicClock, beatWrap|
		addr.sendMsg(TelepathicReferenceClock.cmd, name, this.tempo, this.elapsedBeats, beatWrap)
	}
	
	clearQueue {
		queue.removeAllSuchThat(true);	
	}
}


/*

// usage:

// local clock:
(
t.stop; x.stop;
t = TelepathicClock.new.permanent_(true);
t.empathy = 0.9;
t.confidence = 0.5;
t.addClockSource(\test);

x = TelepathicClock.new.permanent_(true);
x.addClockSource(\test);
x.startListen;

t.verbose = true;
t.startListen;
n = NetAddr("127.0.0.1", 57120);
);

t.others.first.dump




(
SynthDef(\x, { |freq, sustain| Out.ar(0, XLine.kr(0.1, 0.0001, sustain, doneAction: 2) * SinOsc.ar(freq, 0.5pi)) }).add;
Pbind(\freq, 810, \sustain, 0.5, \dur, 1, \instrument, \x).play(t, quant:1);
Pbind(\freq, 8110, \sustain, 0.5, \dur, 1, \instrument, \x).play(x, quant:1);
Pbind(\freq, 2500, \sustain, 0.1, \dur, 1, \instrument, \x).play(TempoClock.default, quant:1);
);

(
TempoClock.default.tempo = rrand(1.0, 3.0);
TempoClock.default.teleport(n, \test, 4);
)


// end
t.endTeleport(n, 0);
t.stopListen;

t.stop;

*/

