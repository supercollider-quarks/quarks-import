//redFrik


PLine : Pattern {
	var <>start, <>end, <>dur, <>mul, <>add, <>length;
	*new {|start= 0, end= 1, dur= 1, mul= 1, add= 0, length= inf|
		^super.newCopyArgs(start, end, dur, mul, add, length);
	}
	storeArgs {^[start, end, dur, mul, add, length]}
	embedInStream {|inval|
		var mulStr= mul.asStream;
		var addStr= add.asStream;
		var mulVal, addVal;
		var counter= 0;
		length.value.do{
			addVal= addStr.next(inval);
			mulVal= mulStr.next(inval);
			if(addVal.isNil or:{mulVal.isNil}, {^inval});
			inval= (counter.linlin(0, 1, start, end)*mulVal+addVal).yield;
			counter= (counter+dur.reciprocal).min(1);
		};
		^inval;
	}
}

PXLine : PLine {
	*new {|start= 1, end= 2, dur= 1, mul= 1, add= 0, length= inf|
		^super.newCopyArgs(start, end, dur, mul, add, length);
	}
	embedInStream {|inval|
		var mulStr= mul.asStream;
		var addStr= add.asStream;
		var mulVal, addVal;
		var counter= 0;
		length.value.do{
			addVal= addStr.next(inval);
			mulVal= mulStr.next(inval);
			if(addVal.isNil or:{mulVal.isNil}, {^inval});
			inval= (counter.linexp(0, 1, start, end)*mulVal+addVal).yield;
			counter= (counter+dur.reciprocal).min(1);
		};
		^inval;
	}
}
