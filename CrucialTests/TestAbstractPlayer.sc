/*
TestAbstractPlayer : UnitTest {
	
	var s,p,k,g,b;
	
	setUp {
		s = Server.default;
		MixedBundleTester.reset;
		AbstractPlayer.bundleClass = MixedBundleTester;

		p = Patch("oscillOrc.saw",[
				440,
				0.1
			]);
	
		k = SwappableAudio.new(p);
		g = Group.basicNew(s);
		b = Bus.audio(s,1);
*/