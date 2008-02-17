

TestAbstractPlayerProxy : UnitTest {
	var s,b,p,g,b,n;
	
	setUp {
		s = Server.default;
		MixedBundleTester.reset;
		InstrSynthDef.clearCache(Server.default);
		//AbstractPlayer.bundleClass = MixedBundleTester;

		p = Patch("oscillOrc.saw",[
				440,
				0.1
			]);
	
		g = Group.basicNew(s);
		b = Bus.audio(s,1);
		n = MixedBundleTester.new;
	}
	
// loadDefFileToBundle
// makePatchOut
// makeResourcesToBundle
// prepareChildrenToBundle
// loadBuffersToBundle

	test_loadDefFileToBundle {
		var app;
		app = AbstractPlayerProxy.new;
		app.source = p;
		
		app.loadDefFileToBundle(n,s);
		// should have one bundle, the def file of p
		this.assertEquals( n.preparationMessages.size,1,"should be one bundle, the def file");
		this.assert( p.synthDef.notNil,"patch should have its synthDef there and built");
	}
	test_makePatchOut {
		var app,po;
		app = AbstractPlayerProxy.new;
		app.source = p;
		
		// makePatchOut { arg agroup,private = false,bus,bundle;
		app.makePatchOut(g,true,b,n);
		
		//app.insp;
		po = app.patchOut;
		// you should now have a 1 channel audio patch out
		this.assert( po.notNil,"should have a patchOut");
		this.assert( po.rate === \audio,"audio rate patchOut");
		this.assert( po.group === g,"group g");
		this.assert( po.bus === b,"bus b");
		
		this.assert( app.group === g,"AbstractPlayerProxy should have group g");
		this.assert( app.bus === b,"AbstractPlayerProxy should have group g");
		
		// the source is not yet assigned, and should not be until we make resources
		//this.assert( p.group === g,"AbstractPlayerProxy should have group g");
		//this.assert( p.bus === b,"AbstractPlayerProxy should have group g");
		
		// these in makeResources
		// no socketGroup in this class
		// sharedBus not yet created
	}
	test_makeResourcesToBundle {
		var app,po;
		app = AbstractPlayerProxy.new;
		app.source = p;
		
		// makePatchOut { arg agroup,private = false,bus,bundle;
		app.makePatchOut(g,true,b,n);
		
		app.makeResourcesToBundle(n);
		// make the bus into a  sharedBus
		
		this.assert( app.bus.isKindOf(SharedBus), "bus is now a shared bus");
		this.assert( app.bus.index == b.index, "bus is now a shared bus");

	}
	test_prepareChildrenToBundle {
		var app,po;
		app = AbstractPlayerProxy.new;
		app.source = p;
		
		// makePatchOut { arg agroup,private = false,bus,bundle;
		app.makePatchOut(g,true,b,n);
		app.makeResourcesToBundle(n);
		
		app.prepareChildrenToBundle(n);
		
		this.assert( p.group === g,"patch should have group g");
		this.assert( p.bus === app.bus,"patch should have shared bus");
		this.assert( p.bus.isKindOf(SharedBus),"patch should have shared bus");
	}
	test_loadBuffersToBundle {
		var app,po;
		app = AbstractPlayerProxy.new;
		app.source = p;
		// nothing happens
	}

	test_prepareToBundle {
		var app,po;
		app = AbstractPlayerProxy.new;
		app.source = p;

		// prepareToBundle { arg agroup,bundle,private = false, bus, defWasLoaded = false;
		app.prepareToBundle(g,n,true,b,false);

		this.assert( p.synthDef.notNil,"patch should have its synthDef there and built");

		po = app.patchOut;
		this.assert( po.notNil,"should have a patchOut");
		this.assert( po.rate === \audio,"audio rate patchOut");
		this.assert( po.group === g,"group g");
		this.assert( po.bus === b,"bus b");
		this.assert( app.group === g,"AbstractPlayerProxy should have group g");
		this.assert( app.bus === b,"AbstractPlayerProxy should have group g");
		this.assert( app.bus.isKindOf(SharedBus), "bus is now a shared bus");
		this.assert( app.bus.index == b.index, "bus is now a shared bus");
		this.assert( p.group === g,"patch should have group g");
		this.assert( p.bus === app.bus,"patch should have shared bus");
		this.assert( p.bus.isKindOf(SharedBus),"patch should have shared bus");
	}


	test_play {
		var a;
	
		this.bootServer;
	
		a = AbstractPlayerProxy.new;
		a.source = Patch({ Saw.ar });

		a.play;
		this.wait({a.isPlaying},"a failed to play");
		
		this.assert(a.socketStatus == \isPlaying,"socketStatus should be isPlaying");
	
		this.assert(a.bus.numChannels == 1, "should have a bus with 1 channel");
		this.assert(a.bus.index == 0,"should be playing on Bus index 0");

		a.stop;
		this.wait({a.isPlaying.not},"a failed to stop");
		
		a.free;
		// bus should be freed
		
		// a is nil ????
		// something in the language is fucked
		//a.bus.debug("a bus");
		
		0.4.wait;
		a.play;
		this.wait({a.isPlaying},"a failed to play a second time after being freed");
		this.assert(a.socketStatus == \isPlaying,"socketStatus should be isPlaying");
	
		this.assert(a.bus.numChannels == 1, "should have a bus with 1 channel");
		this.assert(a.bus.index == 0,"should be playing on Bus index 0");

		a.free;

	}
	test_prepare {
		var a;
	
		this.bootServer;
	
		a = AbstractPlayerProxy.new;
		a.source = Patch({ Saw.ar });

		a.prepareForPlay;
		this.wait({a.readyForPlay},"wait for a to be ready for play");
		

		a.free;
		this.wait({a.readyForPlay.not},"wait for a to be un-ready");
	}




}

