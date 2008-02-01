
PeakMonitor {
		// back end for graphic peak follower

	classvar	<all;	// kr bus # -> monitor object

	var	<target,	// may be bus or mixer
		<synth,	// writes peak level(s) onto control bus(es)
		<bus,
		<synthTarget,
		<freq,
		<>peaks,
		resp,	// OSCresponderNode
		<updater,
		<mixer;	// used only when PM is hitting a mixerchannel
		
	*initClass {
		8.do({ arg i;
			SynthDef.writeOnce("sys-PeakMon" ++ (i+1), {
				arg bus, kbus, t_trig;  // t_trig lets client control timing/reset of Peak
				var sig;
				sig = Peak.ar(In.ar(bus, i+1), t_trig);
				Out.kr(kbus, sig);
			});
		});
		all = IdentityDictionary.new;
		CmdPeriod.add(this);
	}

	*new { arg target = 0, freq = 6, layout;
		^super.new.init(target, freq, layout)
	}
	
	init { arg t, f = 2, layout;
		var	groupbus;		// to determine if the target is a mixerchannel
		freq = f ? 2;
		(groupbus = t.tryPerform(\groupBusInfo, \fader)).notNil.if({
			mixer = t;	// save for free
			target = groupbus[1];	// if it's postsendready, fader's output is on the mc's bus
			synthTarget = groupbus[0];	// place at tail of fadergroup
		}, {
				// convert other types of args
			target = t.asBus(\audio, 2, t.tryPerform(\server) ? Server.default);
				// go to rootnode -- need to consider this an fx synth, so group 0
			synthTarget = Group.basicNew(target.server, 0);
		});
		
		this.prInit;
		
		bus = Bus.control(target.server, target.numChannels);
		peaks = Array.fill(target.numChannels, 0);
		synth = Synth.tail(synthTarget, "sys-PeakMon" ++ (target.numChannels),
			[\bus, target.index, \kbus, bus.index, \freq, freq]);
		
		all.put(bus.index, this);	// so OSCresponder can find me
		updater = Routine({
					// first get the results from the last period
					// then reset the Peak ugen immediately after -- bundling ensures timing
			{	target.server.sendBundle(nil, [\c_getn, bus.index, bus.numChannels], 
					[\n_set, synth.nodeID, \t_trig, 1]);
				freq.reciprocal.wait;
			}.loop
		}).play(SystemClock);
		
		this.gui(layout);

	}
	
	freq_ { arg f;
		freq = f;
	}
	
	free {
		updater.stop;
			// if there's a gui, drop it
		this.dependants.do({ arg d; d.free });
		resp.remove;
		synth !? { synth.free };
		bus !? {
			all.removeAt(bus.index);
			bus.free;
		};
		synth = bus = peaks = target = synthTarget = updater = mixer = nil;
	}
	
	*freeAll {
		all.do({ arg p; p.free });
	}
	
	*cmdPeriod {
		this.freeAll;
	}
	
	numChannels { ^target.numChannels }
	
	guiClass { ^PeakMonitorGui }

	asString { ^"PeakMonitor(" ++ target.asString ++ ", " ++ freq ++ ")" }


/// private	
	prInit {	// called first time
			// target should already be set

				// make my responder
		resp = OSCresponderNode(synthTarget.server.addr, "/c_setn", { arg t, r, m;
			PeakMonitor.all.at(m.at(1)).tryPerform(\peaks_, m.copyRange(3, m.size-1)).changed;
		}).add;
	}
}
