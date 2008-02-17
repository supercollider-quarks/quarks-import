
CXPatterns {

	*initClass {

		Class.initClassTree(Spec);

		Spec.specs.addAll([

			\scale -> ArraySpec.new(StaticSpec(-100,100,\linear),12),

			\freqStream -> StreamSpec(\freq.asSpec.as(StaticIntegerSpec)),

			\cycleLength -> StaticIntegerSpec(2,1024,default:16),

			\degreeStream -> StreamSpec.new(\degree.asSpec.as(StaticIntegerSpec)),
			
			\octaveStream -> StreamSpec.new(\octave.asSpec.as(StaticIntegerSpec)),

			// could use dur but that goes to 0
			\deltaStream -> StreamSpec.new(StaticSpec(2 ** -6, 2 ** 8)),

			\playerFreq -> PlayerSpec(\freq),

			\scaleStream -> StreamSpec(\scale),

			// chord changes are an array of float arrays
			// TwoDArraySpec works
			// but it has to accept patterns
			// or maybe always doing chord changes as a stream is best.
			// you can always return a Pseq that returns each chord array.
			//\chordChanges -> ArraySpec( ArraySpec( \degree ) )

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

