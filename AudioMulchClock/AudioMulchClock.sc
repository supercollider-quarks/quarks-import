//f.olofsson 2010

//todo:	legato in pbind not working properly - why?

AudioMulchClock {
	var	<running= false, <synced= false, <tick= 0, <>shift= 0,
		<tempo= 0, avg, lastTime= 0,
		queue, cmdPeriod, start, stop, pulse;
	*new {|waitForStart= false|
		^super.new.initAudioMulchClock(waitForStart);
	}
	initAudioMulchClock {|waitForStart|
		queue= PriorityQueue.new;
		cmdPeriod= {this.clear};
		avg= FloatArray.newClear(10);
		start= OSCresponderNode(nil, \t_start, {|t, r, m|
			if(running.not, {
				(this.class.name++": start").postln;
				synced= false;
				running= true;
				pulse.add;
				if(CmdPeriod.objects.includes(cmdPeriod).not, {
					CmdPeriod.doOnce(cmdPeriod);
				});
			});
		}).add;
		stop= OSCresponderNode(nil, \t_stop, {|t, r, m|
			if(running, {
				(this.class.name++": stop").postln;
				synced= false;
				running= false;
				pulse.remove;
				CmdPeriod.remove(cmdPeriod);
			});
		}).add;
		pulse= OSCresponderNode(nil, \t_pulse, {|t, r, m|
			var time, item, delta;
			tick= m[1]-shift;
			avg.put(tick%10, Main.elapsedTime-lastTime);
			lastTime= Main.elapsedTime;
			tempo= 1/(avg.sum*0.1*24);
			if(synced.not and:{tick%96==0}, {
				synced= true;
				(this.class.name++": synced").postln;
			});
			if(synced, {
				while({time= queue.topPriority; time.notNil and:{time.floor<=tick}}, {
					item= queue.pop;
					SystemClock.sched(avg[tick%10]*time.frac, {
						delta= item.awake(tick, Main.elapsedTime, this);
						if(delta.isNumber, {
							this.sched(delta, item);
						});
						nil;
					});
				});
			});
		});
		if(waitForStart.not, {
			running= true;
			pulse.add;
			if(CmdPeriod.objects.includes(cmdPeriod).not, {
				CmdPeriod.doOnce(cmdPeriod);
			});
		});
	}
	play {|task, quant= 1|
		this.schedAbs(this.nextTimeOnGrid(quant), task);
	}
	beatDur {
		^1/tempo;
	}
	beats {
		^tick/24;
	}
	sched {|delta, item|
		queue.put(tick+(delta*24), item);
	}
	schedAbs {|tick, item|
		queue.put(tick, item);
	}
	nextTimeOnGrid {|quant= 1, phase= 0|
		if(quant.isNumber.not, {
			quant= quant.quant;
		});
		if(quant==0, {^tick+(phase*24)});
		^tick+((24*quant)-(tick%(24*quant)))+(phase%quant*24);
	}
	clear {
		(this.class.name++": clear").postln;
		queue.array.pairsDo{|time, item| item.removedFromScheduler};
		queue.clear;
		start.remove;
		stop.remove;
		pulse.remove;
	}
}
