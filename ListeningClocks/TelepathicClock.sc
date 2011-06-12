
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

	var <id = 0, <channel;
	var responder;
	
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
		// allow other (local) clocks
		if(others.isNil or: { others.any { |clock| try { clock.id == id } ? false }.not }) { 
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
t.stop; x.stop;
t = TelepathicClock.new.permanent_(true);
t.empathy = 0.9;
t.confidence = 0.5;
t.addClockSource(0);

x = TelepathicClock.new.permanent_(true);
x.addClockSource(0);
x.startListen;

t.verbose = true;
t.startListen;
n = NetAddr("127.0.0.1", 57120);
// TempoClock.default.initTeleport(n, 7);
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

