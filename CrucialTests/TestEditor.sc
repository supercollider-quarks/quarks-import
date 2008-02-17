
TestKrNumberEditor : UnitTest {
	
	
	test_canAccept {
		var k,c,s;
		k = KrNumberEditor(6.0,ControlSpec(0, 11, 'linear', 1, 6, ""));

		c = ControlSpec(0, 11, 'linear', 1, 6, "");

		this.assert( c.canAccept(k), "ControlSpec should canAccept KrNumberEditor");
		
		s = StreamSpec(ControlSpec(0, 11, 'linear', 1, 6, ""));

		this.assert( s.canAccept(k), "StreamSpec should canAccept KrNumberEditor");
		// and please let us know about the cheeseburger...
	}
	
}


