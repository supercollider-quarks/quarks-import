



Instr([\deltaStream,\geomHesitate],{ arg min = -4, size = 9,numEvents=0,slideLen=3,slideBy=2;
	Pslide(
			Array.geom(size,2 ** min,2),CXPatterns.inferCycleLength(numEvents), slideLen, slideBy
		)
},[
	StaticIntegerSpec(-9,2),
	StaticIntegerSpec(3,12),
	StaticIntegerSpec(0,1024),
	StaticIntegerSpec(2,11),
	StaticIntegerSpec(3,11)
],\deltaStream);




Instr([\deltaStream,\geomHesitateAndWander],{ arg min = -4, size = 9,numEvents=0,slideLen=3,slideBy=2;
	var list;
	list = Array.geom(size,2 ** min,2);
	Prand([
		Pslide(
				list,CXPatterns.inferCycleLength(numEvents),slideLen,slideBy
			),
		Pswitch1( list, Pbrown(0,size - 1,1) )
	],inf)
		
},[
	StaticIntegerSpec(-9,2),
	StaticIntegerSpec(3,12),
	StaticIntegerSpec(0,1024),
	StaticIntegerSpec(2,11),
	StaticIntegerSpec(3,11)
],\deltaStream);




/* 
	4 bars of random , but add up even
*/
Instr([\deltaStream,\resetingFixRand],{ arg minDiv = -4, maxDiv = -1, cycleLength = 4,cycles=16,offset=0;
	var cyclesDone = 0,sum=0,list,item;
	while({
		cyclesDone < cycles
	},{
		item = 2 ** rrand(minDiv,maxDiv);
		if(sum + item >= cycleLength,{
			//[sum + item, ">",cycleLength,":",sum,cycleLength - sum].debug;
			item = cycleLength - sum;
			cyclesDone = cyclesDone + 1;
			sum = 0;
		},{
			sum = sum + item;
		});
		list = list.add(item);
	});
	
	Pseq(list,inf,offset)
},[
	StaticIntegerSpec(-9,2),
	StaticIntegerSpec(-9,2),
	StaticIntegerSpec(1,16),
	StaticIntegerSpec(1,16),
	StaticIntegerSpec(0,16)
],\deltaStream);

//PlayerBuilder(\deltaStream,[\deltaStream,\resetingFixRand]).gui



Instr([\deltaStream,\resetingFixLooseRand],{ arg minDiv = -4, maxDiv = -1, cycleLength = 4,cycles=16,offset=0;
	var cyclesDone = 0,sum=0,list,item;
	while({
		cyclesDone < cycles
	},{
		item = 2 ** rrand(minDiv.asFloat,maxDiv.asFloat);
		if(sum + item >= cycleLength,{
			//[sum + item, ">",cycleLength,":",sum,cycleLength - sum].debug;
			item = cycleLength - sum;
			cyclesDone = cyclesDone + 1;
			sum = 0;
		},{
			sum = sum + item;
		});
		list = list.add(item);
	});
	
	Pseq(list,inf,offset)
},[
	StaticSpec(-9,2,\linear,0),
	StaticSpec(-9,2,\linear,0),
	StaticIntegerSpec(1,16),
	StaticIntegerSpec(1,16),
	StaticIntegerSpec(0,16)
],\deltaStream);


Instr("deltaStream.swung",{ arg swing;
	Pseq([swing,1 - swing] ,inf)
},[
	StaticSpec(0,1)
],
	\deltaStream
);


/*
Instr([\deltaStream,\XOX],{ arg cycleLength=16;
	XOX(cycleLength)
},[
	StaticIntegerSpec(4,64)
],\deltaStream);

Instr([\deltaStream,\XOXfuzzy],{ arg cycleLength=16;
	XOXfuzzy(cycleLength)
},[	
	StaticIntegerSpec(4,64)
],\deltaStream);




Instr([\deltaStream,\simpleStutterXOX],{ arg cycleLength=16,stutters=2;
	Pstutter(stutters,XOX(cycleLength))
},[\cycleLength,StaticIntegerSpec(2,16)],\deltaStream);

Instr([\deltaStream,\simpleStutterXOXfuzzy],{ arg cycleLength=16,stutters=2;
	Pstutter(stutters,XOXfuzzy(cycleLength))
},[\cycleLength,StaticIntegerSpec(2,16)],\deltaStream);



Instr([\deltaStream,\StutterXOX],{ arg cycleLength=16,stutterCycleLength=16;

	PdurStutter(
		PseqLive(Array.fill(stutterCycleLength,1),0,5,1),
		XOX(cycleLength)
	)
},[
	\cycleLength,
	\cycleLength
],\deltaStream);

Instr([\deltaStream,\StutterXOXfuzzy],{ arg cycleLength=16,stutterCycleLength=16;

	PdurStutter(
		PseqLive(Array.fill(stutterCycleLength,1),0,5,1),
		XOXfuzzy(cycleLength)
	)
},[
	\cycleLength,
	\cycleLength
],\deltaStream);



Instr([\deltaStream,\RandStutterXOX],{ arg cycleLength=16,minStutter=0,maxStutter=5;

	PdurStutter(
		Prand(Array.series((maxStutter - minStutter).max(2),minStutter),inf),
		XOX(cycleLength)
	)
},[
	\cycleLength,
	StaticIntegerSpec(1,5),
	StaticIntegerSpec(1,5)
],\deltaStream);

Instr([\deltaStream,\RandStutterXOXfuzzy],{ arg cycleLength=16,minStutter=0,maxStutter=5;

	PdurStutter(
		Prand(Array.series((maxStutter - minStutter).max(2),minStutter),inf),
		XOXfuzzy(cycleLength)
	)
},[
	\cycleLength,
	StaticIntegerSpec(1,5),
	StaticIntegerSpec(1,5)
],\deltaStream);


Instr([\deltaStream,\NumberEditor],{
	NumberEditor(0.25,[0.125,32.0,\linear,0.125])
},nil,\deltaStream);

*/



/*

PlayerMixer([

	Patch({ arg trig;
		SinOsc.ar * EnvGen.kr(Env.perc(0.01,0.1),trig)
	},[
	Stream2Trig( 1,
		PlayerBuilder(\deltaStream,[\deltaStream,\RandStutterXOXfuzzy])
	)
]),
	Patch({ arg tempo;
		Impulse.ar( tempo )
	},[
		TempoPlayer.new
	])
]).gui

*/


