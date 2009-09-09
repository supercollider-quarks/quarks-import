//redFrik

//--related:
//RedMixerGUI RedMixerChannel RedEffectsRack

//--todo:
//solution to redefx/redmixer order
//mono?, swap RedMixerChannels on the fly possible?, or stereo/mono switch for all channels?, quad?
//multichannel, generalise - now RedMixerChannel only stereo, remake as mono-quad channels

RedMixer {
	var <group, <cvs, <isReady= false, groupPassedIn,
		<channels, <mixers,
		internalSynths;
	*new {|inputChannels= #[[2, 3], [4, 5], [6, 7], [8, 9]], outputChannels= #[[0, 1]], group, lag= 0.05|
		^super.new.initRedMixer(inputChannels, outputChannels, group, lag);
	}
	initRedMixer {|argInputChannels, argOutputChannels, argGroup, lag|
		var server;
		if(argGroup.notNil, {
			server= argGroup.server;
			groupPassedIn= true;
		}, {
			server= Server.default;
			groupPassedIn= false;
		});
		
		Routine.run{
			if(groupPassedIn.not, {
				server.bootSync;
				group= Group.after(server.defaultGroup);
				server.sync;
				CmdPeriod.doOnce({group.free});
			}, {
				group= argGroup;
			});
			
			//--create cvs
			cvs= (
				\lag: CV.new.spec_(ControlSpec(0, 99, 'lin', 0, lag))
			);
			cvs.lag.action= {|v|
				mixers.do{|x| x.cvs.lag.value= v.value};
				channels.do{|x| x.cvs.lag.value= v.value};
			};
			
			//--create mixers
			mixers= argOutputChannels.collect{|x, i|
				RedMixerChannel(x, group, cvs.lag.value);
			};
			
			//--internal synth for routing from channels to mixers
			this.def(argInputChannels).send(server);
			server.sync;
			internalSynths= argOutputChannels.collect{|x|
				Synth(\redMixerInternalRouting, [\out, x[0]], group);
			};
			
			//--create channels
			channels= argInputChannels.collect{|x, i|
				RedMixerChannel(x, group, cvs.lag.value);
			};
//			while({channels.any{|x| x.isReady.not}}, {0.02.wait});
			isReady= true;
		};
	}
	mute {|channel|
		if(channel.isKindOf(Boolean), {
			channels.do{|x| x.mute(channel)};
		}, {
			channel.asArray.do{|x|
				channels[x].mute(true);
			};
		});
	}
	solo {|channel|	
		if(channel==false, {
			this.mute(false);
		}, {
			channels.do{|x, i|
				x.mute(i!=channel);
			};
		});
	}
	mixer {
		^mixers[0];
	}
	free {
		if(groupPassedIn.not, {group.free});
		mixers.do{|x| x.free};
		channels.do{|x| x.free};
		internalSynths.do{|x| x.free};
	}
	defaults {
		channels.do{|x| x.defaults};
		mixers.do{|x| x.defaults};
		cvs.do{|cv| cv.value= cv.spec.default};
	}
	gui {|position|
		^RedMixerGUI(this, position);
	}
	
	inputChannels {
		^channels.collect{|x| x.cvs.out.value};
	}
	inputChannels_ {|arr|
		if(channels.size!=arr.size, {
			(this.class.name++": array must match number of channels").error;
		}, {
			channels.do{|x, i|
				x.cvs.out.value= arr[i][0];
			};
			internalSynths.do{|x|
				x.set(\inputs, arr.collect{|y| y[0]});
			};
		});
	}
	outputChannels {
		^mixers.collect{|x| x.cvs.out.value};
	}
	outputChannels_ {|arr|
		if(internalSynths.size!=arr.size, {
			(this.class.name++": array must match outputChannels argument").error;
		}, {
			arr.do{|x, i|
				mixers[i].cvs.out.value= x[0];
				internalSynths[i].set(\out, x[0]);
			};
		});
	}
	def {|inputChannels= #[[2, 3], [4, 5], [6, 7], [8, 9]]|
		^SynthDef(\redMixerInternalRouting, {|out= 0|
			var c= Control.names(\inputs).kr(inputChannels.collect{|x| x[0]});
			var z= inputChannels.collect{|x, i| In.ar(c[i], x.size)};
			Out.ar(out, Mix(z));
		});
	}
}
