+ Bus{
	// gets a one channel bus from a multichannel bus
	getChannel{ |chan|
		^this.subBus( chan );
	}

	*newFrom { |bus, offset, numChannels=1|
		if ( offset > bus.numChannels or: {numChannels + offset >
			bus.numChannels} )
		{
			Error( "Bus:newFrom tried to reach outside the channel range of  
%".format( bus )).throw
		};
		^Bus.new( bus.rate, bus.index + offset, numChannels);
	}

	subBus{|offset, numChannels=1|
		^Bus.newFrom( this, offset, numChannels );
	}
}

