+ Bus{
	// gets a one channel bus from a multichannel bus
	getChannel{ |chan|
		^Bus.new( this.rate, this.index + chan, 1 );
	}
}