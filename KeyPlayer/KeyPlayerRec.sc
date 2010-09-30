 /*** could be unified with CtLoop, maybe superclass EventLoop. ****/ 

KeyPlayerRec { 

	var <>isOn=false, <>list, <>player;
	var <then, <task, <>speed = 1, <>loop = true;
	
	*new { |player| 
		^super.new.player_(player);
	}
	
	startRec { 
		("recording keys: kp" + player.key).postln;
		isOn = true; 
		then = nil;
		list = List.new; 
		task = TaskProxy({ 
			this.playFunc;
			if (loop) { task.play } { 
				("done: kp" + player.key).postln; 
			}
		});
	}
		
	stopRec { 
		("stopRec: kp" + player.key).postln; 
		isOn = false;
	}
		
	recordEvent { |char, type=\down|
		var now = thisThread.seconds;
		if (isOn) {Ê
			then = then ? now;
			list.add([now - then, char, type]);
			then = now;
		//	list.printAll;
		}
	}
	playFunc {  
		"KeyPlayRec starts".postln; 
		list.do { |trip| 
			var time, char, type, unicode; 
			#time, char, type = trip.postln;
			unicode = char.asUnicode; 
			(time / speed.value).wait;
			player.keyAction(char, which: type);	
		};
		"KeyPlayRec done.".postln; 
	}
	
		// play should play with a task proxy?
		// how to do looping? 
	playOnce { Task({ this.playFunc }).play; }
		
	toggleRec { if (isOn) { this.stopRec } { this.startRec } }
	play { if (isOn) { this.stopRec }; this.playOnce; }
	togglePlay { 
		if (isOn) { this.stopRec }; 
		if (task.isActive) { task.stop } { task.play };
	}
	changeSpeed { |factor=1| speed = speed * factor }
}
