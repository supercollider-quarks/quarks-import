/**
	2006  Till Bovermann (IEM)

	base class for Just InTerface 
*/
JInT {
	var <controllers; // a dict of controller representations (JInTController)
	var cMap;
	
	*new {
		^super.new.initJInT;
	}
	initJInT {
		cMap = TwoWayIdentityDictionary.new;
		controllers = [];
	}
	startCustom {"JInT-start: abstract method".warn}
	stopCustom {"JInT-stop: abstract method".warn}
	start {
		// setup controller-map 
		controllers.do{|cont, i| cMap[cont.short] = i};
		// setup JIT control
		controllers.do(_.initNodeProxy);
		// run subclass start method
		this.startCustom;
	}
	stop {
		// run subclass stop method
		this.stopCustom;
	}
	at {|keys = 0|
		try {
			keys.isKindOf(Collection).not.if({
				^this.basicAt(keys);
			}, {
				^keys.collect{|key| this.basicAt(key)};
			});
		} {
			(format("JInT-at: malformed argument (%). has to be either a valid key, an index, or a collection of these.\n", keys)).warn;
		}
	}
	basicAt {|key|
		key.isKindOf(Symbol).if({
			^controllers[cMap[key]];
		}, {
			^controllers[key];
		});
	}
	info {
		postf("%:\n", this.class);
		controllers.do{|cont, i|
			postf("\t% (%):\t%\n", cont.short, i, cont.description);
		}
	}
}
