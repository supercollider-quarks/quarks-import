
TestSample : UnitTest {
	
	test_standardizePath {
		//this.assertEquals( Sample.standardizePath("a11wlk01.wav"), "a11wlk01.wav" )
		this.assert(
				Sample.standardizePath("a11wlk01.wav").find("//").isNil,
				"should not have two // anywhere in the path");
	}
	
}

