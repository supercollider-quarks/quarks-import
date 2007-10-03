
// allows special handling of certain .put operations

//AdhocClassEnvir : EnvironmentRedirect {
//	var	<>putAction;
//
///*	*new { arg n = 4, proto, parent, know = false;
//		^super.newCopyArgs(Environment(n, proto, parent, know))
//	}
//*/
//	*newFrom { |envir|
//		(envir.class == this).if({ ^envir }, {
//			envir.respondsTo(\keysValuesDo).if({
//				^this.new.putAll(envir).parent_(envir.parent).proto_(envir.proto)
//			}, { ^nil });
//		});
//	}
//
//		// also of importance is the envirPut override in Chuck ext.sc
//	put { |key, value|
//		super.put(key, value);
//		this.use({ putAction.value(key, value, this); });
//	}
//	
//	parent { ^envir.parent }
//	
//	shallowCopyItems { ^this.class.new.envir_(envir.shallowCopyItems).putAction_(putAction) }
//	
//	moveFunctionsToParent { |keysToMove|
//		envir.moveFunctionsToParent(keysToMove)
//	}
//	
//	keys { ^envir.keys }
//	
//	select { |func| ^envir.select(func) }
//	reject { |func| ^envir.reject(func) }
//	asSortedArray { ^envir.asSortedArray }
//}


// ugly version, known to work

//AdhocClassEnvir : Environment {
//	var	<>putAction;
//
//	*newFrom { |envir|
//		(envir.class == this).if({ ^envir }, {
//			envir.respondsTo(\keysValuesDo).if({
//				^this.new.putAll(envir).parent_(envir.parent).proto_(envir.proto)
//			}, { ^nil });
//		});
//	}
//
//		// also of importance is the envirPut override in Chuck ext.sc
//	put { |key, value|
//		super.put(key, value);
//		this.use({ putAction.value(key, value, this); });
//	}
//}
