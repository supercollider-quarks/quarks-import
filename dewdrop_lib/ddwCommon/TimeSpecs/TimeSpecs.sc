
// plug-n-play quantizing algorithms for process onsets

// for help -- see [TimeSpec] help file

NilTimeSpec {   // always schedules for now
	classvar	<default;
		// use default var to avoid creating and destroying objects
		// no parameters so this should be OK
	*initClass { default = this.prNew; }
	*new { ^default }
	*prNew { ^super.new }
	asTimeSpec { ^this }
	asQuant { ^this }	// for compatibility with pattern playing
	applyLatency { ^this }	// can't schedule earlier than now!
		// override schedTime in subclasses for different scheduling results
	nextTimeOnGrid { arg clock; 
		^clock.tryPerform(\beats) ? 0
	}
		// this is used in too many places, can't delete it outright yet
	schedTime { |clock|
		this.deprecated(thisMethod, this.class.findRespondingMethodFor(\nextTimeOnGrid));
		^this.nextTimeOnGrid(clock)
	}
		// for chucklib, normally you shouldn't use this
		// same as nextTimeOnGrid here but others have to take bp's leadTime into account
	bpSchedTime { |bp|
		^this.nextTimeOnGrid(bp.clock)
	}
	quant { ^0 }
	phase { ^0 }
	offset { ^0 }
	offset_ {}
	bindClassName { ^\NilTimeSpec }
}

DelayTimeSpec : NilTimeSpec {
	var	<delay, dStream, <>clock;
	*new { |delay| ^super.prNew.delay_(delay ? 1) }
	delay_ { |dly|
		delay = dly;
		dStream = dly.asPattern.asStream;
	}
	nextTimeOnGrid { arg clock;
		var beats = clock.tryPerform(\beats);
		^beats + dStream.next(beats)
	}
}

DelayTimeSpecLeadTime : DelayTimeSpec {
	bpSchedTime { |bp|
		^this.nextTimeOnGrid(bp.clock) - (bp.leadTime ? 0)
	}
}

// schedule for a specific beat number
AbsoluteTimeSpec : NilTimeSpec {
	var	<>quant, <>clock;
	*new { arg quant; ^super.prNew.quant_(quant ? 1).clock_(TempoClock.default) }
	applyLatency { ^this }
	nextTimeOnGrid { |argClock|
		(quant >= (argClock ? clock).beats).if({
			^quant
		}, {
				// invalid after given time has passed
			MethodError("AbsoluteTimeSpec(%) has expired at % beats."
				.format(quant, (argClock ? clock).beats), this).throw;
		});
	}
}

AbsoluteTimeSpecLeadTime : AbsoluteTimeSpec {
	bpSchedTime { |bp|
		^this.nextTimeOnGrid(bp.clock) - (bp.leadTime ? 0)
	}
}


// BasicTimeSpec is standard scheduling model: quant, phase, offset

BasicTimeSpec : AbsoluteTimeSpecLeadTime {
	var	<>phase, <>offset;
	*new { arg quant, phase, offset;
		^super.new(quant).phase_(phase).offset_(offset).clock_(TempoClock.default)
	}
	applyLatency { |latency| ^this.copy.offset_(latency) }
	nextTimeOnGrid { arg argClock;
		^(argClock ? clock).nextTimeOnGrid(quant ? 0, phase ? 0) - (offset ? 0);
	}
		// BP's leadTime overrides BasicTimeSpec's offset
	bpSchedTime { |bp|
		^this.nextTimeOnGrid(bp.clock) + (offset ? 0) - (bp.leadTime ? 0)
	}
}

QuantOffsetTimeSpec : BasicTimeSpec {
//	var <>offset;
	*new { arg quant, offset;
		this.deprecated(thisMethod, Meta_BasicTimeSpec.findRespondingMethodFor(\new));
		^super.new(quant, offset)
	}
//	applyLatency { |latency| ^QuantOffsetLatencyWrapTimeSpec(quant, offset, latency ? 0) }
//	nextTimeOnGrid { arg argClock, argQuant, argOffset;
//		var time, tempClock;
//		time = (tempClock = argClock ? clock).elapsedBeats.roundUp(argQuant ? quant)
//			+ (argOffset ? offset);
//		(time < tempClock.elapsedBeats).if({
//			^time + quant
//		}, {
//			^time
//		});
//	}
////	adjustQuantForPhaseAndOffset { |phase = 0, offset = 0|
////		^QuantOffsetLatencyWrapTimeSpec(quant, this.offset + phase, offset)
////	}
}

	// scheduling will fail if latency window is passed
QuantOffsetLatencyTimeSpec : QuantOffsetTimeSpec {
//	var <>latency;
	*new { arg quant, offset, latency;
		this.deprecated(thisMethod, Meta_BasicTimeSpec.findRespondingMethodFor(\new));
		^super.new(quant, offset, latency)
	}
//	applyLatency { |argLatency|
//		(latency == argLatency).if({
//			^this
//		}, {
//			^this.copy.latency_(argLatency ? 0)
//		});
//	}
//	nextTimeOnGrid { arg argClock, argQuant, argOffset, argLatency;
//		var time;
//		time = (argClock ? clock).elapsedBeats.roundUp(argQuant ? quant)
//			+ (argOffset ? offset) - (argLatency ? latency);
//		^time
//	}
//	adjustTimeForLatency { |time, argLatency| ^(time - (argLatency ? latency)) }
////	adjustQuantForPhaseAndOffset { |phase = 0, offset = 0|
////		^this.copy.offset_(phase + this.offset, latency + offset)
////	}
}

	// scheduling will always succeed
	// provided that quant > latency
QuantOffsetLatencyWrapTimeSpec : QuantOffsetLatencyTimeSpec {
//	nextTimeOnGrid { arg argClock, argQuant, argOffset, argLatency;
//		var time, tempClock = argClock ? clock;
//		time = super.nextTimeOnGrid(tempClock, argQuant, argOffset, argLatency);
//		(time < tempClock.elapsedBeats).if({
//			^time + quant
//		}, {
//			^time
//		});
//	}
//	adjustTimeForLatency { |time, argLatency|
//		((time = time - (argLatency ? latency)) < clock.elapsedBeats).if({
//			^time + quant
//		}, {
//			^time
//		});
//	}
}
