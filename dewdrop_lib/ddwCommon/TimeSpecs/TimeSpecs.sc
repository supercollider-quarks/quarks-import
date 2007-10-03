
// plug-n-play quantizing algorithms for process onsets
// borrowed from Julian Rohrhuber on sc-dev, 15 jan 04

// for help -- see [TimeSpec.help.rtf]

NilTimeSpec {   // always schedules for now
	classvar	<default;
		// use default var to avoid creating and destroying objects
		// no parameters so this should be OK
	*initClass { default = this.prNew; }
	*new { ^default }
	*prNew { ^super.new }
	asTimeSpec { ^this }
	applyLatency { ^this }	// can't schedule earlier than now!
		// override schedTime in subclasses for different scheduling results
	schedTime { arg clock; 
		^clock.tryPerform(\beats) ? 0
	}
//	adjustTimeForLatency { |time, clock| ^time }
		// for compatibility with normal TempoClock-play
	nextTimeOnGrid { |clock| ^this.schedTime(clock) }
	quant { ^0 }
	bindClassName { ^\NilTimeSpec }
}

DelayTimeSpec : NilTimeSpec {
	var	<delay, dStream, <>clock;
	*new { |delay| ^super.prNew.delay_(delay ? 1) }
	delay_ { |dly|
		delay = dly;
		dStream = dly.asPattern.asStream;
	}
	schedTime { arg clock;
		var beats = clock.tryPerform(\beats);
		^beats + dStream.next(beats)
	}
}

BasicTimeSpec : NilTimeSpec {   // quant only
	var <>quant, <>clock;
	*new { arg quant; ^super.prNew.quant_(quant ? 1) }
	applyLatency { |latency| ^QuantOffsetLatencyWrapTimeSpec(quant, 0, latency ? 0) }
//	applyLatency { |lat| latency = lat ? 0 };
	schedTime { arg argClock, argQuant;
		^(argClock ? clock).elapsedBeats.roundUp(argQuant ? quant);
	}
//		// this will most likely be shared for all subclasses
//		// could be simpler but if I change the implementation later, easier to update
//	adjustTimeForLatency { |time, clock|
//		^time - latency
//	}
}

// should be used only once then thrown away
AbsoluteTimeSpec : BasicTimeSpec {
	*new { arg quant; ^super.prNew.quant_(quant ? 1) }
	applyLatency { ^this }
	schedTime {
		var	saveQuant = quant;
		quant.notNil.if({
			quant = nil;
			^saveQuant
		}, {
			MethodError("AbsoluteTimeSpec may not be reused.", this);
		});
	}
}

QuantOffsetTimeSpec : BasicTimeSpec {
	var <>offset;
	*new { arg quant, offset;
		^super.new(quant).offset_(offset ? 0)
	}
	applyLatency { |latency| ^QuantOffsetLatencyWrapTimeSpec(quant, offset, latency ? 0) }
	schedTime { arg argClock, argQuant, argOffset;
		var time, tempClock;
		time = (tempClock = argClock ? clock).elapsedBeats.roundUp(argQuant ? quant)
			+ (argOffset ? offset);
		(time < tempClock.elapsedBeats).if({
			^time + quant
		}, {
			^time
		});
	}
}

	// scheduling will fail if latency window is passed
QuantOffsetLatencyTimeSpec : QuantOffsetTimeSpec {
	var <>latency;
	*new { arg quant, offset, latency;
		^super.new(quant, offset).latency_(latency ? 0)
	}
	applyLatency { |argLatency|
		(latency == argLatency).if({
			^this
		}, {
			^this.copy.latency_(argLatency ? 0)
		});
	}
	schedTime { arg argClock, argQuant, argOffset, argLatency;
		var time, tempClock;
		time = (tempClock = argClock ? clock).elapsedBeats.roundUp(argQuant ? quant)
			+ (argOffset ? offset) - ((argLatency ? latency)/* * tempClock.tempo*/);
//		(time < tempClock.elapsedBeats).if({
//			^time + quant
//		}, {
			^time
//		});
	}
}

	// scheduling will always succeed
	// provided that quant > latency
QuantOffsetLatencyWrapTimeSpec : QuantOffsetLatencyTimeSpec {
	schedTime { arg argClock, argQuant, argOffset, argLatency;
		var time, tempClock = argClock ? clock;
		time = super.schedTime(tempClock, argQuant, argOffset, argLatency);
//		time = (tempClock = argClock ? clock).elapsedBeats.roundUp(argQuant ? quant)
//			+ (argOffset ? offset) - (argLatency ? latency);
		(time < tempClock.elapsedBeats).if({
			^time + quant
		}, {
			^time
		});
	}
}
