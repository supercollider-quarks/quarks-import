+ GeneralHIDSlot{

	// should put s = s ? Server.default in createBus

	kr{
		var s;
		s = s ? Server.default;
		if ( bus.isNil, { this.createBus(s) } );
		^In.kr( bus );
	}

}