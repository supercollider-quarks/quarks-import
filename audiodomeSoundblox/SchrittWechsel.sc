// Schrittwechsel, a performative installation for Audiodome Soundblox 
// These are the needed classfiles.
// Please see accompanying Schrittwechsel.html for the controller/main patch.
// Implemented in 2010 by Till Bovermann
// See http://tangibleauditoryinterfaces.de/index.php/tai-applications/audiodome-soundblox/ for further details


// everything "postln" is debug output, remove if neccesary
// everything "info" is regular output

BlockPerson {
	classvar <all;
	
	var <>server;	
	var <synth, <>synthName, <synthParams;
	var <stepBuffers;
	var <>homeBlock, <>currentBlock;

	*new{|server, stepBuffers, homeBlock, currentBlock|
		^super.new.initHome(server, stepBuffers, homeBlock, currentBlock)
	}
	
	*initClass {
		all = IdentitySet[];
	}
	
	initHome {|aServer, aStepBuffers, aHomeBlock, aCurrentBlock|
		server = aServer;

		stepBuffers = aStepBuffers;
		
		homeBlock    =    aHomeBlock;
		currentBlock = aCurrentBlock;
		
		synthName = \BlockPerson;
		synthParams = (
			masterAmp: [0.1],
			amp: [0.1],
			interpolation: [2],
			masterMute: [0],
			mute: [0]
		);

		// add person to current block 
		this.currentBlock.addPerson(this);
		all.add(this);
	}

	remove{
		all.remove(this);	
	}
	
	inTransit {
		^(synth.notNil.if({
			synth.isPlaying;	
		}, {
			false		
		}));
	}
	

	// one shot synth
	// FIXME: add different door bufnums according to current side
	transite {|to, dur = 5, dt = 1| // a Block
		
		
		// one cannot go to where he already is
		(to == this.currentBlock).if{"BlockPerson:transite :illegal move".warn; ^this};
		
		
		this.inTransit.not.if({

			// remove person from current block, and 
			currentBlock.removePerson(this, dt);

			// wait for dt seconds (and take care of server's latency)
			server.makeBundle((server.latency ? 0) + dt, {
				synth = Synth(synthName, target: server)
					.set(
						\openBufnum, this.currentBlock.doorOpenBufnum,
						\closeBufnum, to.doorCloseBufnum,
						\stepBufnum, stepBuffers.first.bufnum,
						\dur, dur,
						\startChan, this.currentBlock.out,
						\finishChan, to.out,
						\rate, 1
				);

				// Watch Synth and trigger followup actions
				synth.register(assumePlaying: true)
					.addDependant({|obj, what|
						(what == \n_end).if({
							// if synth ended, put person in block
							"add person to %\n".format(this.currentBlock).postln;
							this.currentBlock.addPerson(this);
							// release dependants for garbage collection
							obj.releaseDependants;
						});	
					});
				synthParams.keysValuesDo{|key, value|
					synth.setn(key, value)
				};
			});

			// set current block to the one she's heading to
			currentBlock = to;
			
		}, {
			"in transit (%)".format(this).inform;	
		})
	}

	transiteImmediate{
		synth.free;
	}
	
	goHome {|dur = 5|
		this.transite(homeBlock, dur)
	}
	
	others {
		^HomeBlock.all difference: [this];
	}
	
	*sendSynth {
		SynthDef(\BlockPerson, { 
			arg	masterAmp = 0.1, amp = 1, 
				masterMute = 1, mute = 1, 
				stepBufnum = 0, openBufnum, closeBufnum,
				startChan = 0, finishChan = 1, 
				dur = 5, rate=1, interpolation=4;

			var steps, pannedSteps, open, close;
			var openLength, closeLength, minDur;
			

			openLength = BufSampleRate.kr(openBufnum).reciprocal * BufFrames.ir(openBufnum);
			closeLength = BufSampleRate.kr(closeBufnum).reciprocal * BufFrames.ir(closeBufnum);
		
			// minimal operation time (open and close doors)
			minDur = openLength + closeLength + dur;
			//minDur.poll;
			open = BufRd.ar(
				1,
				openBufnum, 
				EnvGen.ar(Env([0, BufFrames.ir(openBufnum), 0], [openLength, 0]), gate: Impulse.ar(0)), 
				0, // no loop 
				interpolation
			);
			
			steps = BufRd.ar(
				1,
				stepBufnum, 
				EnvGen.ar(Env([0, 0, BufFrames.ir(stepBufnum), 0], [openLength * 0.9, BufSampleRate.kr(stepBufnum).reciprocal * BufFrames.ir(stepBufnum), 0]), gate: Impulse.ar(0)), 
				0, // no loop 
				interpolation
			) * EnvGen.ar(Env.linen(0, minDur - closeLength, 0));
			
			pannedSteps = Pan2.ar(steps, 
				EnvGen.ar(Env([-1, -1, 1], [openLength * 0.9, minDur - closeLength + (openLength * 0.1)]))
			);
		
			close = BufRd.ar(
				1,
				closeBufnum, 
				EnvGen.ar(Env([0, 0, BufFrames.ir(closeBufnum), 0], [minDur - closeLength, closeLength, 0]), gate: Impulse.ar(0), doneAction: 2), 
				0, // no loop 
				interpolation
			);
		
			Out.ar(startChan,  (open  + pannedSteps[0]) * amp * masterAmp * (1 - masterMute) * (1-mute));
			Out.ar(finishChan, (close + pannedSteps[1]) * amp * masterAmp * (1 - masterMute) * (1-mute));
		}).memStore;
	}
	
}

/////////////////////////////////////////////////

HomeBlock : SoundBlock {
	var <>server;
	var <out, <activitySynth, <>activitySynthName, <activitySynthParams;
	var <>persons, <>maxAllowedPersons = 1;
	
	var <>activityBuffers, <doorOpenBuffers, <doorCloseBuffers;
	
	// overfullAction is evaluated, if the cube reaches a state in which it contains more persons than allowed. (Only once!)
	// If the state is below, fittingAction is evaluated. 
	var <>overfullAction, <>fittingAction;
	var <isOverfull;
	
	
	*new{|color=\red, number=0, server, activityBuffers, doorOpenBuffers, doorCloseBuffers, outChannel = 0|
		^super.new(color, number).initHome(server, activityBuffers, doorOpenBuffers, doorCloseBuffers, outChannel)
	}
	
	initHome {|aServer, aBuffers, aDoorOpenBuffers, aDoorCloseBuffers, outChannel|
		out = outChannel;

		server = aServer;

		activityBuffers = aBuffers;
		doorOpenBuffers = aDoorOpenBuffers;
		doorCloseBuffers = aDoorCloseBuffers;
		
		activitySynthName = \HomeBlock;
		activitySynthParams = (
			masterAmp: [0.1],
			rates: 1!6,
			amp: [0.1],
			interpolation: [2],
			masterMute: [0],
			mute: [0]
		);
		
		persons = IdentitySet[];
	}

	out_{|val|
		out = val;
		activitySynth.notNil.if{
			activitySynth.set(\out, val);	
		}	
	}
	
	isActive {
		^activitySynth.notNil
	}
	
	// FIXME: set bufnum according to upfacing side of cube
	getActive {
		this.isActive.not.if({
			server.bind{
				activitySynth = Synth(activitySynthName, [\out, out], target: server).setn(\bufnum, this.activeBufNum);
				activitySynthParams.keysValuesDo{|key, value|
					activitySynth.setn(key, value)
				}
			}
		})
	}

	getInactive{|dt = 1|
		activitySynth.release(dt);
		activitySynth = nil;	
	}

	// only stop in emergency, use getInactive instead.
	getInactiveImmediatly {
		activitySynth.free;
		activitySynth = nil;	
	}
	

	addPerson{|person, dt = 1|
		persons.add(person);
		
		// no good, want to tweek this lateron
		// this.getActive(dt);
		this.act;
	}

	removePerson{|person, dt = 0|
		persons.remove(person);
		(persons.size <= maxAllowedPersons).if{
			isOverfull = false;
		};
		persons.isEmpty.if{ // if there's no one in the cube, it cannot be active.
			this.getInactive(dt);
		};
	}

	act{
		var personsSize = persons.size;
		// "HomeBlock:act : persons.size == %".format(persons.size).postln;
		(personsSize > maxAllowedPersons).if({
			"HomeBlock:act : more then % person in %.".format(maxAllowedPersons, this).postln;
			isOverfull.not.if{
				overfullAction.value(this, personsSize, personsSize - maxAllowedPersons);
				isOverfull = true;
			};
		}, {
			isOverfull = false;
			fittingAction.value(this, personsSize)
		});
	}

	others {
		^HomeBlock.all difference: [this];
	}

	pr_computeBufnum {|buffers, upface = 0| 
		^buffers[((buffers.size / 6) * upface).asInteger].bufnum;
	}
	doorOpenBufnum {
		^this.pr_computeBufnum(doorOpenBuffers,  upFace);
	}
	doorCloseBufnum {
		^this.pr_computeBufnum(doorCloseBuffers, upFace);
	}
	activeBufNum {
		^this.pr_computeBufnum(activityBuffers, upFace);
	}
	
	*sendSynth {
		SynthDef(\HomeBlock, {
			arg	out = 0, 
				amp = 0.1, mute = 0, masterAmp = 1, masterMute = 0, 
				bufnum = 0, rate = 1,//startpos = 0,
				interpolation = 2, gate= 1;
				
			var env = EnvGen.kr(Env.asr(0.1, 1, 10), gate: gate, doneAction: 2);
			Out.ar(out, 
				BufRd.ar(
					1, 
					bufnum, 
					Phasor.ar(
						0, BufRateScale.kr(bufnum) * rate, 
						0, 
						BufFrames.kr(bufnum)
					), 
					1, 
					interpolation
				) * amp * masterAmp * (1 - masterMute) * (1-mute) * env
			);
		}).memStore;
	}

	
}