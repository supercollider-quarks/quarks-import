
// extensions required for flexible scheduling

+SimpleNumber {
	asTimeSpec {
			// why NilTimeSpec if this is 0? NilTimeSpec includes an offset to be sure
			// the schedTime is always (just slightly) in the future
		(this == 0).if({ ^NilTimeSpec.new }, {^BasicTimeSpec(this) });
	}
}

+ArrayedCollection {
	asTimeSpec { ^BasicTimeSpec(*this) }
//	asTimeSpec { 
//		(this[0] == 0).if({
//			^NilTimeSpec.new
//		}, {
//			(this[2].isNil or: { this[2] == 0 }).if({
//				^QuantOffsetTimeSpec(*this)
//			}, {
//				^QuantOffsetLatencyTimeSpec(*this)
//			});
//		});
//	}			
}

+Nil {
	asTimeSpec { ^NilTimeSpec.new }
}

//+ TempoClock {
//	play { arg task, when;
////["TempoClock-play", this.elapsedBeats, when.asTimeSpec.schedTime(this)].postln;
//		this.schedAbs(when.asTimeSpec.schedTime(this), task)
//	}
//}
