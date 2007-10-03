
TempoClick {
	// takes a tempoclock, plays impulses on a kr bus based on the clock
	
	// this latency adjustment works on my iBook 700mHz
	// if your routines don't play in sync with BufferSeq's,
	// adjust this number. Smaller values mean the BufferSeq's
	// will play slightly later.
	// not yet determined if the correct latencyFudge depends on desired tempo

	// 29 nov 06: latencyFudge is deprecated; variable kept for backward compatibility
	classvar	<>latencyFudge = 0;
//			nodeIDStart = 500, lastIDUsed;	// for bouncing between 2 reserved node IDs
	
	var	<server, <clock, <bus, <subdiv, <nodeID, nodeIDBounce;
	var	aliveThread;
	
	*initClass {
		StartUp.add({
			SynthDef.writeOnce("TempoClick", { arg tempo = 1, subd = 1, bus = 0;
					// kill the synth every beat - this helps the client enforce sync
				Line.kr(0, 1, tempo.reciprocal, doneAction:2);
				Out.kr(bus, Impulse.kr(tempo * subd));
			});
		});
	}
	
	*new { arg server, clock, bus, subdiv = 1;
		^super.newCopyArgs(server ?? { Server.default }, clock ? TempoClock.default,
			bus, subdiv).init;
	}
	
	init {
		var	id2;
		clock.isKindOf(TempoClock).not.if({
			("Clock must be a TempoClock. This is " ++ clock.asString).die;
		});
		clock.addDependant(this);
		nodeID = server.nodeAllocator.allocPerm;
		id2 = server.nodeAllocator.allocPerm;
//		nodeID = nodeID.isNil.if({ nodeIDStart }, { lastIDUsed = lastIDUsed + 2 });
		nodeIDBounce = nodeID + id2;
		this.play;
	}
	
	play {
		server.waitForBoot({	// when server is booted
			bus.isNil.if({ bus = Bus.control(server, 1) });
//			synth.notNil.if({ synth.free });
			this.startAliveThread;
		});
	}
	
	startAliveThread {
		var	time;
		aliveThread = Routine({
			{	nodeID = nodeIDBounce - nodeID;
					// schedule the onset using latency to coincide with the clock
					// so server-side and client-side sequencers are in sync
				server.sendBundle(0.2 / clock.tempo, [\s_new, \TempoClick, nodeID,
					0, 0,	// at head of group 0
					\tempo, clock.tempo, \subd, subdiv, \bus, bus.index]);
				1.0.wait;
			}.loop;
		});
		clock.schedAbs(
			((time = clock.elapsedBeats.roundUp - 0.2) < clock.elapsedBeats).if(
				{ time + 1 }, { time }),
			aliveThread);
	}
	
	stop {
		aliveThread.stop; aliveThread = nil;
	}
	
	remove { this.free }
	
	free {
		this.stop;
		bus.free;
		clock.removeDependant(this);
		server.nodeAllocator.freePerm(nodeID);
		server.nodeAllocator.freePerm(nodeIDBounce - nodeID);
//		server.nodeAllocator.freePerm((nodeIDBounce - 1) >> 1);
	}
	
	update {
		aliveThread.notNil.if({ server.sendMsg(\n_set, nodeID, \tempo, clock.tempo) });
	}
	
	tempo_ { arg tempo;
		clock.tempo = tempo;	// TempoClock calls changed, which calls update
	}
	
	subdiv_ { arg s;
		subdiv = s;
		aliveThread.notNil.if({ server.sendMsg(\n_set, nodeID, \subd, subdiv) });
	}
	
	index { ^bus.index }
	asMapArg { ^"c" ++ bus.index }	
	asMap { ^this.asMapArg }
}
