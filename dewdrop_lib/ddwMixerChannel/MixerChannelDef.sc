
// this class stores parameters needed to define a mixer channel
// mixers should be customizable for any number of channels, any panning methodology
// define all that here
// h. james harkins -- jamshark70@dewdrop-world.net

MixerChannelDef {

		// the setters are there for the sake of inheritance
		// to make a similar mcdef, .copy an existing one,
		// then replace the instance variables you need to change
		
		// outChannels, basicFader and postSendReadyFader automatically cause synthdefs
		// to be resent so that running servers are in sync with the definition

	var	<>name, <>inChannels, <outChannels,
		<basicFader, <postSendReadyFader,
		<>controls, <>guidef;

	*initClass {
		Class.initClassTree(Collection);
		Library.global.put(\mixerdefs, IdentityDictionary.new);
		MixerChannelDef(\mix1x1, 1, 1,
			basicFader: SynthDef("mixers/Mix1x1", {	// mono-to-stereo mixer channel
						arg busin, busout, level;
						Out.ar(busout, In.ar(busin, 1) * level);
					}), 
			postSendReadyFader: SynthDef("mixers/Mxb1x1", {						arg busin, busout, level;
						var in;
						in = In.ar(busin, 1) * level;
							// so that mixerchan bus can be used as postsendbus
						ReplaceOut.ar(busin, in);
						Out.ar(busout, in);
					}), 
			controls: (level: { |name| MixerControl(name, nil, 0.75, \amp) })
		);
		
		MixerChannelDef(\mix1x2, 1, 2,
			basicFader: SynthDef("mixers/Mix1x2", {	// mono-to-stereo mixer channel
						arg busin, busout, level, pan;
						Out.ar(busout, Pan2.ar(In.ar(busin, 1), pan, level));
					}), 
			postSendReadyFader: SynthDef("mixers/Mxb1x2", {						arg busin, busout, level, pan;
						var in, out;
						in = In.ar(busin, 1);
						out = Pan2.ar(in, pan, level);
							// so that mixerchan bus can be used as postsendbus
						ReplaceOut.ar(busin, out);
						Out.ar(busout, out);
					}), 
			controls: (level: { |name| MixerControl(name, nil, 0.75, \amp) },
						pan: { |name| MixerControl(name, nil, 0, \bipolar) }
			)
		);
		
		MixerChannelDef(\mix2x2, 2, 2,
			basicFader: SynthDef("mixers/Mix2x2", {	// stereo-to-stereo mixer channel
						arg busin, busout, level, pan;
						var l, r;
						#l, r = In.ar(busin, 2);
						Out.ar(busout, Balance2.ar(l, r, pan, level));
					}), 
			postSendReadyFader: SynthDef("mixers/Mxb2x2", {						arg busin, busout, level, pan;
						var l, r, out;
						#l, r = In.ar(busin, 2);
						out = Balance2.ar(l, r, pan, level);
						ReplaceOut.ar(busin, out);
						Out.ar(busout, out);
					}), 
			controls: (level: { |name| MixerControl(name, nil, 0.75, \amp) },
						pan: { |name| MixerControl(name, nil, 0, \bipolar) }
			)
		);

	}

	*new { |name, inChannels, outChannels, basicFader, postSendReadyFader, controls, guidef|
		inChannels.isNil.if({
			^this.at(name)
		}, {
			^super.newCopyArgs(name, inChannels, outChannels, basicFader, postSendReadyFader,
				controls, guidef).init
		});
	}
	
		// validate inputs and store in Library
	init {
		name = name.asSymbol;
		basicFader = basicFader.asSynthDef;
		postSendReadyFader = postSendReadyFader.asSynthDef;
		controls.respondsTo(\keys).not.if({
			MethodError("Error constructing MixerChannelDef - controls must be a Dictionary",
				this).throw
		});
		
		Library.global.put(\mixerdefs, name, this);
	}
	
	*at { |name|
		^Library.global[\mixerdefs, name]
	}
	
	*all { ^Library.global[\mixerdefs] }

		// instance methods

	synthdef { |postSendReady = false|
		^postSendReady.if({ postSendReadyFader }, { basicFader })
	}
	
	defName { |postSendReady = false|
		^this.synthdef(postSendReady).name
	}

	makeControlArray { |mixerName|
		var out = IdentityDictionary.new;
		controls.keysValuesDo({ |key, gcfunc|
			out.put(key, gcfunc.value("% %".format(mixerName, key)));
		});
		^out
	}

	outChannels_ { |outCh|
		(outCh.notNil and: { outCh != outChannels }).if({
			outChannels = outCh;
			this.sendDefsToRunningServers;	// update send, xfer and recorder synth
		});
	}
	
	basicFader_ { |synthdef|
		synthdef.notNil.if({
			basicFader = synthdef;
			this.sendDefsToRunningServers;
		});
	}
	
	postSendReadyFader_ { |synthdef|
		synthdef.notNil.if({
			postSendReadyFader = synthdef;
			this.sendDefsToRunningServers;
		});
	}
	
	sendDefsToRunningServers {
		Server.named.do({ |server|
			this.sendSynthDefs(server);
		});
	}
	
		// send the mixer, xfer, send and record synths for this MCDef
	sendSynthDefs { |server|
		server = server ?? Server.default;
		server.serverRunning.if({
			#[true, false].do({ |bool|
				this.synthdef(bool).send(server);
			});
		
			SynthDef("mixers/xfer" ++ outChannels, {
				arg busin, busout;
				Out.ar(busout, In.ar(busin, outChannels));
			}).send(server);
			
				// this doesn't cover all cases
				// if you have defs only for stereo outs, there would be no send def for mono in
				// but why would you route a stereo signal into a mono mixer?
				// (send is not meant to reduce the number of channels)
				// so, you can break it only by doing something stupid to begin with
			SynthDef("mixers/Send" ++ outChannels, {
				arg busin, busout, level;
				Out.ar(busout, In.ar(busin, outChannels) * level);
			}).send(server);

			SynthDef("mixers/Rec" ++ outChannels, { arg i_in, i_bufNum = 0;
				DiskOut.ar(i_bufNum, In.ar(i_in, outChannels));
			}).send(server);

		});
	}
	
	*sendSynthDefs { |server|
		this.all.do({ |def|
			def.sendSynthDefs(server);
		});
	}

}

