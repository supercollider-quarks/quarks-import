
CXPatterns {
	
	*initClass {
		
		Class.initClassTree(Spec);
		Spec.specs.addAll([
			\scale -> ArraySpec.new(StaticSpec(-100,100,\linear))
		]);
				
		Spec.specs.addAll([
			\freqStream -> StreamSpec(\freq),

			\scaleStream -> StreamSpec(\scale),

			\cycleLength -> StaticIntegerSpec(2,1024,default:16),
			
			\degreeStream -> StreamSpec.new(\degree),
			
			\deltaStream -> StreamSpec.new(StaticSpec(2 ** -6, 2 ** 8)),

			\playerFreq -> PlayerSpec(\freq),
		
			\chordChanges -> ArraySpec( ArraySpec( \degree ) )
	
		
		]);
	}
	
	// this allows you to use 0 to mean infinity
	// when specifying for Patterns

	// main benefit being that you can use a NumberEditor to edit on the fly
	// and easily express infinity by setting it to zero

	*inferCycleLength { arg int;
		if(int == 0,{ ^inf });
		if(int == 1,{ ^2 });
		^int
	}

}

