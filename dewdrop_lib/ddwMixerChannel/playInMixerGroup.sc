
// extensions for playing things onto mixers

+ Symbol {
		// maybe refactor this later
	playInMixerGroup { |mixer, target, patchType, args|
		var result;
		(args.notNil and: { args.includes(\outbus) }).not.if({ args = args
			++ [\outbus, mixer.inbus.index, \out, mixer.inbus.index,
				\i_out, mixer.inbus.index] });
		mixer.queueBundle({ result = Synth.tail(target, this, args) });
		^result
	}

//	playInMixerGroupMsg { |mixer, target, patchType, args|
//		args.includes(\outbus).not.if({ args = args
//			++ [\outbus, mixer.inbus.index, \out, mixer.inbus.index] });
//		^[\s_new, this, mixer.server.nextNodeID, 1, target.nodeID] ++ args
//	}
//	
//	playInMixerGroupToBundle { |bundle, mixer, target, patchType, args|
//		bundle.add(this.playInMixerGroupMsg(mixer, target, patchType, args)
//	}
//	
	playOnGlobalControl { |gc, args, target, addAction = \addToTail|
		(args.notNil and: { args.includes(\outbus) }).not.if({ args = args 
			++ [\outbus, gc.bus.index, \out, gc.bus.index, \i_out, gc.bus.index] });
		^Synth(this, args, (target ?? { gc.server }).asTarget, addAction);
//		^Synth.tail((target ?? { gc.server }).asTarget, this, args)
	}
}

+ String {
	playInMixerGroup { |mixer, target, patchType, args|
		^this.asSymbol.playInMixerGroup(mixer, target, patchType, args)
	}

	playOnGlobalControl { |gc, args, target, addAction = \addToTail|
		^this.asSymbol.playOnGlobalControl(gc, args, target, addAction)
	}
}

+ SynthDef {
	playInMixerGroup { |mixer, target, patchType, args|
		var result;
		(args.notNil and: { args.includes(\outbus) }).not.if({ args = args
			++ [\outbus, mixer.inbus.index, \out, mixer.inbus.index,
				\i_out, mixer.inbus.index] });
		mixer.queueBundle({ result = this.play(target, args, \addToTail) });
		^result
	}
	
	playOnGlobalControl { |gc, args, target, addAction = \addToTail|
		(args.notNil and: { args.includes(\outbus) }).not.if({
			args = args ++ [\outbus, gc.index, \out, gc.index, \i_out, gc.index]
		});
		^this.play(target, args, \addToTail)
	}
}

+ Instr {
	playInMixerGroup { |mixer, target, patchType, args|
		var	newPatch;
		mixer.addPatch(newPatch = patchType.new(this, args));
		^newPatch.playToMixer(mixer)
	}
	
//	// no ...Msg method b/c it doesn't make sense for Instr/Patch
//	
//	// expects CXBundle
//	playInMixerGroupToBundle { |mixer, target, patchType, args|
//		var	newPatch;
//		mixer.addPatch(newPatch = patchType.new(this, args));
//		
//		^newPatch.playToMixer(mixer)		
//	}
	
	playOnGlobalControl { |gc, args, target|
		^Patch(this, args).playOnGlobalControl(gc, target)
	}
}

+ AbstractPlayer {
	playInMixerGroup { |mixer, target, patchType, args|
		mixer.addPatch(this);
//		this.respondsTo(\playToMixer).if({
//			this.playToMixer(mixer)
//		}, {
			this.play(target, nil, mixer.inbus.index);
//		});
	}
	playOnGlobalControl { |gc, target|
		this.play((target ?? { gc.server }).asTarget, nil, gc.bus)
	}
}

+ Function {
	playInMixerGroup { |mixer, target, patchType, args|
		var result;
		mixer.queueBundle({
			result = this.asSynthDef(
				outClass: (target == mixer.effectgroup).if({ \ReplaceOut }, { \Out }))
				.play(target, args ++ [\i_out, mixer.inbus.index, \out, mixer.inbus.index,
					\outbus, mixer.inbus.index], \addToTail);
		});
		^result
	}
	
	playOnGlobalControl { |gc, args, target, addAction = \addToTail|
		^this.asSynthDef.play((target ?? { gc.server }).asTarget,
			args ++ [\i_out, gc.bus.index, \out, gc.bus.index, \outbus, gc.bus.index],
			addAction);
//		^this.play((target ?? { gc.server }).asTarget, gc.bus.index)
	}
}


// needed for type tests

+ Object {
	isMixerChannel { ^false }
}

// other misc extensions

+ Bus {
	asMixer {		// returns the mixer if "this" corresponds to a MixerChannel inbus,
				// nil otherwise
		^MixerChannel.servers.at(server).tryPerform(\at, index);
	}
}

+ Nil {
	asMixer {}
}

// felix won't like this b/c it "should" be private; but w/ busdict, shared buses have to
// be double-freed which breaks the bus allocator, so this is necessary for MixerChannel
+ SharedBus {
	released_ { arg bool; released = bool }
}