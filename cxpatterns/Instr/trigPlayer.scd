
Instr([\trigPlayer,\Stream2Trig],{ arg deltaStream, amplitudeStream=1.0;

	Stream2Trig(amplitudeStream, deltaStream)
	
},nil,\trigPlayer);


//Instr([\trigPlayer,\XOX],{ arg size=16,stepsize=0.25;
//
//	XOX(size,stepsize)
//	
//},[
//	StaticIntegerSpec(2,128),
//	StaticSpec(0.125 / 2,4)
//],\trigPlayer);
//
//Instr([\trigPlayer,\XOXlevels],{ arg size=16,stepsize=0.25;
//
//	XOXlevels(size,stepsize)
//	
//},[
//	StaticIntegerSpec(2,128),
//	StaticSpec(0.125 / 2,4)
//],\trigPlayer);
//
//
//Instr([\trigPlayer,\XOXfuzzy],{ arg size=16,stepsize=0.25;
//
//	XOXfuzzy(size,stepsize)
//	
//},[
//	StaticIntegerSpec(2,128),
//	StaticSpec(0.125 / 2,4)
//],\trigPlayer);


Instr([\trigPlayer,\BeatClockPlayer],{ arg tempoFactor=2.0;

	BeatClockPlayer(tempoFactor)

},[
	StaticSpec(1.0,8.0,\linear,1)
],\trigPlayer);





