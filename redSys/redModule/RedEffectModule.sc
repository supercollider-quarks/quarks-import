//redFrik

//--related:
//RedEffectsRack RedAbstractMix RedEfxRing
//RedEffectModule.subclasses
//RedInstrumentModule.subclasses

RedEffectModule : RedAbstractModule {					//abstract class
	var <synth, internalGroup;
	
	prepareForPlay {|server|
		if(group.isNil, {
			internalGroup= Group.after(server.defaultGroup);
			CmdPeriod.doOnce({internalGroup.free});
			group= internalGroup;
		});
		synth= Synth.controls(this.def.name, args, group, defaultAddAction);
	}
	free {
		RedAbstractModule.all.remove(this);
		synth.free;
		internalGroup.free;
	}
	gui {|parent, position|
		^RedEffectModuleGUI(this, parent, position);
	}
	
	//--for subclasses
	*def {^this.subclassResponsibility(thisMethod)}
}
