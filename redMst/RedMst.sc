//redFrik - released under gnu gpl license

//--changes 080807:
//made into a quark
//now works with more items (NodeProxy, BBCut2, RedMOD, RedXM, Function, SynthDef)
//quite a few internal changes - no big changes to the interface
//--071102:
//first release

//--todo:
//empty section example (index 0, 1, 2, 5)
//post comment /function asRoutine example
//hack for bbcut2 with cutbuf?
//RedSeq: gui class with esc key for next
//special redtrk subclass that can force stop for long duration events

//--notes:
//redxm and redmod can change redmst clock tempo!
//events with long duration does not get cut off
//some times 

RedMst {
	classvar	<tracks, <>clock, <>quant= 4,
			<section= 0, <maxSection= 0;
	*initClass {
		tracks= ();
		clock= TempoClock.default;
	}
	*at {|key|
		^tracks[key];
	}
	*add {|trk|
		tracks.put(trk.key, trk);
		if(trk.sections.maxItem>maxSection, {
			maxSection= trk.sections.maxItem;
		});
	}
	*remove {|trk|
		trk.stop;
		tracks.put(trk.key, nil);
	}
	*clear {
		tracks.do{|x| x.clear};
		tracks= ();
		section= 0;
		maxSection= 0;
		if(clock!=TempoClock.default, {
			clock.stop;
			clock.clear;
			clock= TempoClock.default;
		});
	}
	*stop {
		if(clock.notNil, {
			clock.schedAbs(clock.nextTimeOnGrid(quant), {
				tracks.do{|x| x.stop};
				nil;
			});
		}, {
			"RedMst: clock is nil - stopping now".warn;
			tracks.do{|x| x.stop};
		});
	}
	*play {|startSection= 0|
		this.goto(startSection);
	}
	*goto {|jumpSection|
		if(clock.isNil, {
			clock= TempoClock.default;
			"RedMst: clock is nil - using TempoClock.default".warn;
		});
		clock.schedAbs(clock.nextTimeOnGrid(quant), {
			section= jumpSection;
			if(section>maxSection, {
				"RedMst: reached the end".postln;
			}, {
				("RedMst: new section:"+section+"of"+maxSection).postln;
			});
			tracks.do{|x|
				if(x.sections.includes(section), {
					if(x.isPlaying.not, {
						x.play;
					});
				}, {
					if(x.isPlaying, {
						x.stop;
					});
				});
			};
			nil;
		});
	}
	*next {
		this.goto(section+1);
	}
	*prev {
		this.goto(section-1);
	}
}
