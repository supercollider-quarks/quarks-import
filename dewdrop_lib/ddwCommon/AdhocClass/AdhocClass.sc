
// AdhocClass: a flexible environment that can be evaluated like a stream to return SequenceNotes
// all object properties and functions (evaluation, new object generation, self-modification)
// must be stored in environment variables

// certain variables must have specific names to be found easily from outside:
// ~next = func to generate next value

AdhocClass {
	var	<>env;	// I'm providing a setter for env, but it's really for internal use only
				// be careful if you muck around with it!
	var	<>putAction,
		>isPrototype;	// safety check for chucklib
	
	*new { arg initFunc, env, parentKeys;
			// the init func is evaluated in Environment.make
			// and should include setting of all env variables
			// env allows passing of a default environment
			// parentKeys says which data keys should be moved into the parent for inheritance
		^super.new.init(initFunc, env, parentKeys);
	}
	
		// call on an existing instance: like creating an instance of a class
		// initialize() is passed thru to the environment
	new { |...args| ^this.copy.initialize(*args) }
	
		// init clears the environment
	init { arg func, initEnv, parentKeys;
		var new;
//		initEnv = AdhocClassEnvir.newFrom(initEnv);
		initEnv.isNil.if({
				// the initfunc needs to establish all the envir vars
			env = Environment.new;
		}, {
				// this should create nested parents
			env = initEnv.shallowCopyItems
				.parent_(Environment.new(8, parent: initEnv.parent, know: true));
		});
		this.make(func).moveFunctionsToParent(parentKeys);
		env.know = true;
//		env.parent.know = true;
	}
	
		// myAdhoc.import((myOtherAdhoc: #[key1, key2]))
	import { |objectKeyDict, parentKeys|
		var obj2;
		objectKeyDict.keysValuesDo({ |obj, keysToImport|
			(obj2 = obj.asAdhocClassImportable).notNil.if({
				keysToImport.do({ |key|
					this.put(key, obj2[key]);
				});
			}, {
				"% is not valid for import, was ignored.".format(obj).warn;
			});
		});
		env.moveFunctionsToParent(parentKeys);
	}
	
	asAdhocClassImportable {}
	
	moveFunctionsToParent { |keysToMove|
		env.moveFunctionsToParent(keysToMove)
	}
	
	storeArgs { ^[env] }
	
	at { |key| ^env[key] }
	put { |key, value|
		env.put(key = key.asSymbol, value);		// keys MUST be symbols
		putAction.value(key, value, this);
	}
	putAll { |... dictionaries| env.putAll(*dictionaries) }
	parent { ^env.parent }
	isPrototype { ^isPrototype == true }
	
	next { arg ... args;
		var result;
		this.use({ result = ~next.valueArray([this] ++ args); });
		^result
	}
	
	value { arg ... args;
		var result;
		this.use({ result = ~next.valueArray([this] ++ args); });
		^result
	}
	
	reset { arg ... args;
		var result;
		this.use({ result = ~reset.valueArray([this] ++ args); });
		^result
	}
	
	update { arg ... args;
		var result;
		this.use({ result = ~update.valueArray([this] ++ args); });
		^result
	}
	
	asStream { arg ... args;
		var result;
		this.use({ result = ~asStream.valueArray([this] ++ args); });
		^result
	}
	
	asPattern { arg ... args;
		var result;
		this.use({ result = ~asPattern.valueArray([this] ++ args); });
		^result
	}
	
//	use { arg func;
//		var result;
//		env.use({ result = func.value; });
//		^result
//	}

	use { arg func;
		var result, saveEnvir;
		protect {
			saveEnvir = currentEnvironment;
			currentEnvironment = this;
			result = func.value;
			currentEnvironment = saveEnvir;
		} {
			currentEnvironment = saveEnvir;
		};
		^result
	}

	make { arg func;
		var saveEnvir;
		protect {
			saveEnvir = currentEnvironment;
			currentEnvironment = this;
			func.value;
			currentEnvironment = saveEnvir;
		} {
			currentEnvironment = saveEnvir;
		};
		^this
	}
	
	free { arg ... args;
//		env.put(\me, nil);		// may be necessary for gc to explicitly kill the circular ref.
		this.use({ ~free.valueArray([this] ++ args) });
		env = nil;
	}
	
		// make the AdhocClass act like an object
		// selector must be implemented as a func assigned to an environment var
		// named for the selector
		// if that key returns nil, object does not understand!
		
		// messages not understood by AdhocClass should be passed to the environment
		// so: node.message == node.perform(\message) == node.use({ ~message.value })
	doesNotUnderstand { arg selector ... args;
		var result, item;
		(item = env.at(selector)).isFunction.if({
			this.use({ result = item.valueArray([this] ++ args) });
		}, {
				// if setter is defined as a function in the environment, it will be
				// handled by the above branch -- this needs only to do a simple put
			selector.isSetter.if({
				this.put(selector.asGetter, args[0]);
				result = this
			}, {
				result = item
			});
		});
		^result
	}
	
	perform { arg selector ... args;
//		super.respondsTo(selector).if({
//			^super.performList(selector, args)
//		}, {
			^this.performList(\doesNotUnderstand, [selector] ++ args);
//		});
	}

	tryPerform { arg selector ... args;	// for sth like draggedInto...GUI
		^this.perform(selector, *args);
	}
	
	respondsTo { arg selector;
		^super.respondsTo(selector) or: { env.keys.includes(selector) }
	}
	
		// make a copy of this node, and run this func to change some props
	clone { arg modFunc, parentKeys;
		modFunc.isNil.if({
				// no modification, just return a straight copy
			^this.copy
		}, {
				// create a new AdhocClass, applying the modFunc to the existing environment
			^this.class.new(modFunc, env, parentKeys)
		});
	}

	copy { ^this.shallowCopyItems }
	shallowCopyItems { 
		^this.class.new
			.env_(env.shallowCopyItems.parent_(env.parent))
			.putAction_(putAction)
	}
	
	listVars { this.help(\var) }
	listMethods { this.help(\method) }
	
		// return a flat dictionary with all methods
	allMethods {
		var	getter = { |level, list|
					// methods must be added from top (so children override)
					// thus, recurse all the way up before adding anything
				level.tryPerform(\parent).notNil.if({
					getter.value(level.tryPerform(\parent), list);
				});
				list.putAll(level.select(_.isFunction));
			},
			methods = IdentityDictionary.new;
		getter.value(this.env, methods);
		^methods
	}
	
	help { |what = \all|
		(what == \var or: { what == \all }).if({
			"\nVariables:".postln;
			env.parent.reject(_.isFunction)
				.putAll(env.reject(_.isFunction))
				.asSortedArray.do(_.postln);
		});
		(what == \method or: { what == \all }).if({
			"\nMethods:".postln;
			this.allMethods
				.asSortedArray.do({ |pair|
					Post << pair[0] << "(";
					pair[1].listArgs;
					Post << ")\n";
				});
		});
	}

}
