//redFrik

//--todo:
//gui class
//make left/right speaking voice.  formant synthesis?


RedTest {
	var buf, syn, <sfGrp;
	
	//--pseudo ugen
	*ar {|amp= 1, pan= 0|
		^Pan2.ar(Mix(SinOsc.ar([400, 404], 0, LFNoise0.kr(5).max(0)*amp*0.5)), pan)
	}
	
	//--soundfile
	*sf {|out= 0, group|
		^super.new.initRedTestSF(out, group, 1);
	}
	*sf2 {|out= 0, group|
		^super.new.initRedTestSF(out, group, 2);
	}
	initRedTestSF {|out, group, channels|
		sfGrp= group ? Server.default.defaultGroup;
		Routine.run{
			sfGrp.server.bootSync;
			buf= Buffer.read(sfGrp.server, "sounds/a11wlk01.wav");
			sfGrp.server.sync;
			if(channels==1, {
				SynthDef(\RedTestSF, {|out, buf|
					Out.ar(out, PlayBuf.ar(1, buf, 1, 1, 0, 1));
				}).send(sfGrp.server);
				sfGrp.server.sync;
				syn= Synth.head(sfGrp, \RedTestSF, [\out, out, \buf, buf]);
			}, {
				SynthDef(\RedTestSF2, {|out, buf|
					Out.ar(out, Pan2.ar(PlayBuf.ar(1, buf, 1, 1, 0, 1), 0));
				}).send(sfGrp.server);
				sfGrp.server.sync;
				syn= Synth.head(sfGrp, \RedTestSF2, [\out, out, \buf, buf]);
			});
		}
	}
	sfBus_ {|out|
		syn.set(\out, out);
	}
	sfFree {
		syn.free;
		buf.free;
	}
	
	//--speaker tests
	*speaker {|channels|
		channels= channels ? [0, 1];
		SynthDef(\redTestPink, {|out= 0, gate= 1|
			var e= EnvGen.kr(Env.perc, gate, doneAction:2);
			var z= PinkNoise.ar(e);
			Out.ar(out, z);
		}).add;
		^Pbind(\instrument, \redTestPink, \out, Pseq(channels, inf)).play
	}
	*speaker2 {|channels|
		channels= channels ? [0, 1];
		SynthDef(\redTestPing, {|out= 0, gate= 1, freq= 400|
			var e= EnvGen.kr(Env.perc, gate, doneAction:2);
			var z= SinOsc.ar(freq, 0, e);
			Out.ar(out, z);
		}).add;
		^Pbind(\instrument, \redTestPing, \out, Pseq(channels, inf), \degree, Pseq(channels, inf)).play
	}
}
