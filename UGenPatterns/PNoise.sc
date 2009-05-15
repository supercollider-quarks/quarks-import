//redFrik


PWhiteNoise : Pattern {
	var <>mul, <>add, <>length;
	*new {|mul= 1, add= 0, length= inf|
		^super.newCopyArgs(mul, add, length);
	}
	storeArgs {^[mul, add, length]}
	embedInStream {|inval|
		var mulStr= mul.asStream;
		var addStr= add.asStream;
		var mulVal, addVal;
		length.value.do{
			addVal= addStr.next(inval);
			mulVal= mulStr.next(inval);
			if(addVal.isNil or:{mulVal.isNil}, {^inval});
			inval= (mulVal.rand+addVal).yield;
		};
		^inval;
	}
}

PClipNoise : PWhiteNoise {
	embedInStream {|inval|
		var mulStr= mul.asStream;
		var addStr= add.asStream;
		var mulVal, addVal;
		length.value.do{
			addVal= addStr.next(inval);
			mulVal= mulStr.next(inval);
			if(addVal.isNil or:{mulVal.isNil}, {^inval});
			inval= ((2.rand*2*mulVal-mulVal)+addVal).yield;
		};
		^inval;
	}
}
