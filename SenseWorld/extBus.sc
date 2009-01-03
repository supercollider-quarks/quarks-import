+ Bus{
	// gets a one channel bus from a multichannel bus
	getChannel{ |chan|
		^this.subBus( chan );
	}
}

