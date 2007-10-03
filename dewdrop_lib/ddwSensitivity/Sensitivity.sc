
Sensitivity {		// a macro ugen - jamshark70@yahoo.com

	// if value ranges from 0..1:
	//		sense == 0: function always returns 1
	//		sense == 0.5: function ranges from 0.5..1
	//		sense == 1: function ranges from 0..1 (unchanged)
	// this mimics the sensitivity parameter on hardware synths

	*kr { arg scaler, value, sense;
		(scaler == 0).if({
			^0
		}, {
			(scaler == 1).if({
				^(value-1) * sense + 1
			}, {
				^scaler * ((value - 1) * sense + 1)
			})
		});
	}

	*ar { arg scaler, value, sense;
		(scaler == 0).if({
			^0
		}, {
			(scaler == 1).if({
				^(value-1) * sense + 1
			}, {
				^scaler * ((value - 1) * sense + 1)
			})
		});
	}	
	
}



NumericRange {
	var	<>lo, <>hi, <>spec;
	
	*new { arg lo, hi, spec;
		^super.newCopyArgs(lo, hi, spec.asSpec)
	}
	
	rrand { ^rrand(lo, hi) }
	inrange { |num| ^num.inclusivelyBetween(lo, hi) }
	
	range01_ { arg l, h;
		lo = spec.map(l);
		hi = spec.map(h);
	}

	range_ { arg l, h;
		lo = l;
		hi = h;
	}
	
	lo01 { ^spec.unmap(lo) }
	hi01 { ^spec.unmap(hi) }
	
	guiClass { ^NumericRangeGui }
}
