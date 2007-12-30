
// plug-n-play quantizing algorithms for process onsets

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
	adjustTimeForLatency { |time| ^time }
		// for compatibility with normal TempoClock-play
	nextTimeOnGrid { |clock| ^this.schedTime(clock) }
	quant { ^0 }
	bindClassName { ^\NilTimeSpec }
	adjustQuantForPhaseAndOffset {}
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

// schedule for a specific beat number
AbsoluteTimeSpec : NilTimeSpec {
	var	<>quant, <>clock;
	*new { arg quant; ^super.prNew.quant_(quant ? 1).clock_(TempoClock.default) }
	applyLatency { ^this }
	schedTime { |argClock|
		(quant >= (argClock ? clock).beats).if({
			^quant
		}, {
				// invalid after given time has passed
			MethodError("AbsoluteTimeSpec(%) has expired at % beats."
				.format(quant, (argClock ? clock).beats), this).throw;
		});
	}
}

BasicTimeSpec : AbsoluteTimeSpec {   // quant only
	*new { arg quant; ^super.prNew.quant_(quant ? 1).clock_(TempoClock.default) }
	applyLatency { |latency| ^QuantOffsetLatencyWrapTimeSpec(quant, 0, latency ? 0) }
	schedTime { arg argClock, argQuant;
		^(argClock ? clock).elapsedBeats.roundUp(argQuant ? quant);
	}
	adjustTimeForLatency { |time, latency, argClock|
		((time = time - (latency ? 0)) < (argClock ? clock).elapsedBeats).if({
			^time + quant
		}, {
			^time
		});
	}
	adjustQuantForPhaseAndOffset { |phase = 0, offset = 0|
		^QuantOffsetLatencyWrapTimeSpec(quant, phase, offset)
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
	adjustQuantForPhaseAndOffset { |phase = 0, offset = 0|
		^QuantOffsetLatencyWrapTimeSpec(quant, this.offset + phase, offset)
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
		var time;
		time = (argClock ? clock).elapsedBeats.roundUp(argQuant ? quant)
			+ (argOffset ? offset) - (argLatency ? latency);
		^time
	}
	adjustTimeForLatency { |time, argLatency| ^(time - (argLatency ? latency)) }
	adjustQuantForPhaseAndOffset { |phase = 0, offset = 0|
		^this.copy.offset_(phase + this.offset, latency + offset)
	}
}

	// scheduling will always succeed
	// provided that quant > latency
QuantOffsetLatencyWrapTimeSpec : QuantOffsetLatencyTimeSpec {
	schedTime { arg argClock, argQuant, argOffset, argLatency;
		var time, tempClock = argClock ? clock;
		time = super.schedTime(tempClock, argQuant, argOffset, argLatency);
		(time < tempClock.elapsedBeats).if({
			^time + quant
		}, {
			^time
		});
	}
	adjustTimeForLatency { |time, argLatency|
		((time = time - (argLatency ? latency)) < clock.elapsedBeats).if({
			^time + quant
		}, {
			^time
		});
	}
}
