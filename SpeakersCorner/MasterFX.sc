MasterFX { 

	classvar <all;
	var <group, <numChannels, <busIndex, <server, <pxChain; 
	
	*initClass { 
		all = IdentityDictionary.new;
	}
	
	*new { |server, numChannels, slotNames, busIndex=0| 
		// only one masterfx per server ATM.
		// could be changed if different MasterFX 
		// for different outchannel groups are to be used.
		var fx = all[server.name];
		if (fx.notNil) { 
			"// MasterFX for server % exists - use \nMasterFX.clear(%) \n// to make a new one.\n"
				.postf(server.name, server.name.asCompileString);
			^fx
		} { 
			^this.make(server, numChannels, slotNames, busIndex) 
		}
	}
	
	*make { |server, numChannels, slotNames| 
		^super.new.init(server, numChannels, slotNames);
	}
	
	bus { 
		^Bus.new(\audio, busIndex, numChannels, server);
	}

					// evil just to wait? hmmm. 
	cmdPeriod { defer({ this.wakeUp }, 0.2) }
	
	init { |inServer, inNumChannels, inSlotNames, inBusIndex| 
		var proxy;
		server = inServer ? Server.default;
		numChannels = inNumChannels ? server.options.numOutputBusChannels;
		busIndex = inBusIndex ? 0; 

		proxy = NodeProxy.audio(server, numChannels).bus_(this.bus);
		proxy.vol_(0);	
		pxChain = ProxyChain.from(proxy, inSlotNames ? []);
		
		all.put(server.name, this);
		
		this.makeGroup; 
		CmdPeriod.add(this);
	}
	
	makeGroup { 
		group = Group.new(1.asGroup, \addAfter).isPlaying_(true);
		pxChain.proxy.parentGroup_(group);
		
	}
	
	wakeUp { 
		"\nMasterFX for server % waking up.\n\n".postf(server.name); 
			this.makeGroup; 
			pxChain.proxy.wakeUp; 			
	}
	
	clear { 
		CmdPeriod.remove(this);
		pxChain.proxy.clear;
		all.removeAt(pxChain.proxy.server.name);
	}
	
	*clear { |name| 
		(name ?? { all.keys }).do { |name| 
			all.removeAt(name).clear;
		};
	}
	
	makeName { 
		^(this.class.name ++ "_" ++ server.name 
		++ "_" ++ pxChain.proxy.numChannels).asSymbol 
	}
	
	gui { |name, buttonList, nSliders, win|
			// the effects are all on by default: 
		buttonList = buttonList ?? { pxChain.slotNames.collect ([_, \slotCtl, 1]) };
		name = name ?? { this.makeName };
		nSliders = nSliders ? 16; 
		^pxChain.gui(name, buttonList, nSliders, win);
	}
	
	*default { ^all[Server.default.name] }
}