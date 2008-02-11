
TestStreamKrDur : UnitTest {
	
	test_pseqSpec {
		var s,k;
		
		k = StreamKrDur( Pseq([0,1,2,3,4,5],inf), 0.25 );
		s = k.spec;
		this.assertEquals( s.minval, 0, "minval 0");
		this.assertEquals( s.maxval, 5, "minval 5");
	
	}
	
	test_defaultSpec {
		this.assert( StreamKrDur.new.spec.isKindOf(ControlSpec),"should be a control spec by default");
	}

}

