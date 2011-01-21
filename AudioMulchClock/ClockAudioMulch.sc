//f.olofsson 2010

//todo: use SkipJack for permanent clocks or just recreate the routine at cmdperiod?

ClockAudioMulch : TempoClock {
	var <>addr, <>tick= 0, <>shift= 0, task;
	*new {|tempo, beats, seconds, queueSize= 256, addr|
		^super.new.initClockAudioMulch(tempo, beats, seconds, queueSize, addr);
	}
	initClockAudioMulch {|tempo, beats, seconds, queueSize, argAddr|
		addr= argAddr ?? {NetAddr("127.0.0.1", 7000)};
		task= Routine({
			addr.sendMsg(\t_start, (tick+shift).asInteger, 0.0, this.beatDur/24.0);
			inf.do{
				var delta= this.beatDur/24.0;
				addr.sendMsg(\t_pulse, (tick+shift).asInteger, 0.0, delta);
				delta.wait;
				tick= tick+1;
			};
		}).play(this);
		^super.init(tempo, beats, seconds, queueSize);
	}
	stop {
		addr.sendMsg(\t_stop, (tick+shift).asInteger, 0.0, 0.0);
		task.stop;
		super.stop;
	}
}
