
/*
	pchRatio patterns
	
	this is a gateway instrument, allowing the entire degree and scale library to
	be used with samplers
	
	Pbinds and InstrGateSpawner can find uses for pchRatio streams
	
*/

Instr([\pchRatioStream,\degree],{ arg degreeStream,scaleStream;

	(degreeStream - 48).degreeToKey(scaleStream).midiratio
	
},
	[
		StreamSpec.new(StaticIntegerSpec(-100,100,\linear,1)),
		\scaleStream
	],
	StreamSpec(\pchRatio)
);



