//bugfix for redUniverse
//my species bugfix in extCollection breaks plot.  this is a fix to get that back

+FloatArray {
	unlace {|clumpSize= 2, numChan= 1, clip= false|
		^this.as(Array).unlace(clumpSize, numChan, clip)
	}
}