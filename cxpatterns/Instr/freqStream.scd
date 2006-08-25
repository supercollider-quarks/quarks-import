
/*
	taking degrees, frequencies, durations, scales in various combinations
	and returning objects that supply frequency
*/


Spec.specs.addAll([
	\freqStream -> StreamSpec(\freq),

	\scale -> ArraySpec.new(StaticSpec(-100,100,\linear)),
	\scaleStream -> StreamSpec(\scale),

	\cycleLength -> StaticIntegerSpec(2,1024,default:16)
	]);



// given degrees and scales, return frequencies
Instr([\freqStream,\streamByDegree],{ arg degreeStream,scaleStream;

	degreeStream.degreeToKey(scaleStream).midicps

},nil,
	\freqStream
);


Instr([\freqStream,\divStutter],{ arg root=36,scale,cycleLength=16;

	var list;
	(cycleLength).do({ arg i;
		i = i % 4;
		(cycleLength div: 4).do({
			list = list.add( i + 1 );
		})
	});
	PdurStutter(
		PseqLive(list.debug.copyRange(0,cycleLength-1),0,5,1),
		Pseq((root + scale).midicps ,inf)
	)
},[
	StaticSpec(-100,100,\linear,1),// could even be a stream
	\scale,
	\cycleLength
], \freqStream );


