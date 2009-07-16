/*
s.boot;
TestMcldUGens.run
*/
TestMcldUGens : UnitTest {
	test_avgtrig_1noop{
		var testsIncomplete = 0;
	  	this.bootServer;

		// Test sanity of MedianTriggered / MeanTriggered when span is 1 - should be an identity (no-op) operation
		
		[MeanTriggered, MedianTriggered].do{|avgunit|
			[
			{
				var trig = Impulse.kr(100);
				var son = TRand.kr(-1, 1, trig);
				var mash = avgunit.kr(son, trig, 1);
				[son, mash]
			},
			{
				var trig = Impulse.ar(100);
				var son = TRand.ar(-1, 1, trig);
				var mash = avgunit.ar(son, trig, 1);
				[son, mash]
			}
			].do{|synthfunc, whichfunc|
				testsIncomplete = testsIncomplete + 1;
				synthfunc.loadToFloatArray(1, action: { |array|
					// no-op, the two channels should be equal
					array = array.clump(2).flop;
					this.assert( (array[0] - array[1]).abs.every(_<0.0001) ,"%.%(length:1) should not alter the signal".format(avgunit, #[\kr, \ar][whichfunc]), true, {array.flop.flat.plot(numChannels:2)} );
					testsIncomplete = testsIncomplete - 1;
				});
			};
			
		};
		
		// Wait for async tests
		this.wait{testsIncomplete==0};
	}

	test_avgtrig_sameaslangcalc{
		var testsIncomplete = 0;
	  	this.bootServer;

		// Test that MedianTriggered / MeanTriggered give the same results as calculating the same thing lang-side
		
		[[MedianTriggered, \median], [MeanTriggered, \mean]].do{|avgstuff|
			var avgunit = avgstuff[0], avgcalc = avgstuff[1], rnddata, rndbuf;
			testsIncomplete = testsIncomplete + 2;
			
			rnddata = {exprand(0.1, 10.0)}.dup(1000);
//			rnddata = {1.0.rand}.dup(100).postcs;
//			rnddata = 100.collect{|v| (v*0.01).sin};
			rndbuf = Buffer.loadCollection(Server.default, rnddata);
			0.5.wait;
			Server.default.sync;
//			rndbuf.plot;
			[
				[{
					avgunit.kr(PlayBuf.kr(1, rndbuf), 1, rnddata.size);
				}, \kr, Server.default.options.blockSize]
				,
				[{
					avgunit.ar(PlayBuf.ar(1, rndbuf), 1, rnddata.size);
				}, \ar, 1]
			].do{|stuff|
				stuff[0].loadToFloatArray(rndbuf.duration * stuff[2], action: { |array|
					this.assertFloatEquals(array.last, rnddata.perform(avgcalc), 
						"average from %.% should match val calculated in language".format(avgunit, stuff[1]));
					testsIncomplete = testsIncomplete - 1;
				});
			};
		};
		
		// Wait for async tests
		this.wait{testsIncomplete==0};
	}

} // end class
