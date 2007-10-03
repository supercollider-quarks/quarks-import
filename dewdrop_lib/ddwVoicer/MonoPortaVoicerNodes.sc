
MonoPortaSynthVoicerNode : SynthVoicerNode {

	trigger { arg freq, gate = 1, args, latency;
		var bundle;
		this.shouldSteal.if({
			bundle = this.setMsg([\freqlag, voicer.portaTime, \freq, freq,
				\gate, gate, \t_gate, gate] ++ args);
		}, {
			isReleasing.if({
				bundle = this.releaseMsg(-1.02);		// quick release
			});
				// triggerMsg() sets the synth instance var
			bundle = bundle ++ this.triggerMsg(freq, gate, [\freqlag, voicer.portaTime] ++ args);
			NodeWatcher.register(synth);
				// when the synth node dies, I need to set my flags
			Updater(synth, { |syn, msg|
				(msg == \n_end).if({
						// synth may have changed
					(syn == synth).if({
						isPlaying = isReleasing = false;
					});
					Object.dependantsDictionary.removeAt(syn);
//					syn.releaseDependants;	// remove node and Updater from dependants dictionary
				});
			});
		});
//bundle.asCompileString.postln;
		target.server.listSendBundle(myLastLatency = latency, bundle);
		frequency = freq;	// save frequency for Voicer.release
		voicer.lastFreqs.add(freq);
		lastTrigger = Main.elapsedTime;	// save time
		isPlaying = true;
		isReleasing = false;
	}
	
	release { arg gate = 0, latency;	// release using Env's releaseNode
		this.isPlaying.if({ 
			synth.server.listSendBundle(latency, this.releaseMsg(gate));
			isPlaying = false;
			isReleasing = true;
		});
	}
	
	shouldSteal {
		^super.shouldSteal and: { isReleasing.not }
	}
	
}


// method defs are repeated between these 2 classes because of no multiple inheritance

MonoPortaInstrVoicerNode : InstrVoicerNode {

	trigger { arg freq, gate = 1, args, latency;
		var bundle;

//"MonoPortaInstrVoicerNode-trigger - ".post;
//[freq, gate, args, noLatency].asCompileString.postln;

		this.shouldSteal.if({
			bundle = this.setMsg([\freqlag, voicer.portaTime, \freq, freq,
				\gate, gate, \t_gate, gate] ++ args);
		}, {
			isReleasing.if({
				bundle = this.releaseMsg(-1.02);		// quick release
			});
			bundle = bundle ++ this.triggerMsg(freq, gate, [\freqlag, voicer.portaTime] ++ args);
			NodeWatcher.register(synth);
				// when the synth node dies, I need to set my flags
			Updater(synth, { |syn, msg|
				(msg == \n_end).if({
						// synth may have changed
					(syn == synth).if({
						isPlaying = isReleasing = false;
					});
					Object.dependantsDictionary.removeAt(syn);
	//				syn.releaseDependants;	// remove node and Updater from dependants dictionary
				});
			});
		});
		
//bundle.asCompileString.postln;
		target.server.listSendBundle(myLastLatency = latency, bundle);
		
		frequency = freq;
		voicer.lastFreqs.add(freq);
		lastTrigger = Main.elapsedTime;
		isPlaying = true;
		isReleasing = false;
//["MonoPortaInstrVoicerNode-trigger", freq, voicer.lastFreqs].asCompileString.postln;
	}

	release { arg gate = 0, latency;
//"MonoPortaInstrVoicerNode-release - ".post;
		this.isPlaying.if({
//			voicer.lastFreqs.remove(frequency).postln;
//this.setMsg([\gate, gate]).postln;
			this.set([\gate, gate], latency);
			isPlaying = false;
			isReleasing = true;
		});
	}

	shouldSteal {
		^super.shouldSteal and: { isReleasing.not }
	}

}
