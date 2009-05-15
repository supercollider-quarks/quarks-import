//redFrik

//todo:
//

PTrig1 : FilterPattern {
	var <>dur;
	*new {|pattern, dur= 1|
		^super.newCopyArgs(pattern, dur);
	}
	storeArgs {^[pattern, dur]}
	embedInStream {|inval|
		var evtStr= pattern.asStream;
		var durStr= dur.asStream;
		var outVal, durVal;
		var counter= 0, prev= 0;
		loop{
			outVal= evtStr.next(inval);
			if(outVal.isNil, {^inval});
			durVal= durStr.next(outVal);
			if(durVal.isNil, {^inval});
			
			if(prev<=0 and:{outVal>0 and:{counter==0}}, {
				counter= durVal;
			});
			if(counter>0, {
				inval= 1.yield;
			}, {
				inval= 0.yield;
			});
			counter= (counter-1).max(0);
			prev= outVal;
		};
	}
}

PTrig : PTrig1 {
	embedInStream {|inval|
		var evtStr= pattern.asStream;
		var durStr= dur.asStream;
		var outVal, durVal;
		var counter= 0, prev= 0, trig= 0;
		loop{
			outVal= evtStr.next(inval);
			if(outVal.isNil, {^inval});
			durVal= durStr.next(outVal);
			if(durVal.isNil, {^inval});
			
			if(prev<=0 and:{outVal>0 and:{counter==0}}, {
				counter= durVal;
				trig= outVal;
			});
			if(counter>0, {
				inval= trig.yield;
			}, {
				inval= 0.yield;
			});
			counter= (counter-1).max(0);
			prev= outVal;
		};
	}
}
