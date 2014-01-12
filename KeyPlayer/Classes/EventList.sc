/* EventList is a list of recorded events.
   It follows a few conventions:
*  recorded events always have an abstime,
*  plus any other key/value combinations that help
     to store the events in semantically rich form.
TimeLoop
* reserved keys - overwrite at your own risk:
   delta: is used for storing delta-time between events,
   dur: is used to calculate actual logical duration,
        e.g. when soft-quantizing an EventList to a time grid.

* recording a List is terminated by .finish(absTime),
  which puts an end event at the end of the list.

EventLoop
*

a = EventList[];
a.addEvent((absTime: 0));// events should begin with time 0;
a.addEvent((absTime: 0.3));
a.addEvent((absTime: 0.52));
a.addEvent((absTime: 0.72));
a.addEvent((absTime: 0.93));
a.finish(1.88);

a.print;
a.print([\dur]);
a.print([\dur], false);
a.print([\absTime, \dur], false);

a.quantizeDurs(0.25, 2).printAll;"";
a.totalDur;
a.playingDur;


a.collect(_.absTime);
a.collect(_.type);

? also put a startEvent before all others?
    esp. if one wants to record silence first?

*/

EventList : List {
	var <totalDur = 0, <playingDur = 0;

	print { |keys, postRest = true|
		var postKeys;
		if (postRest.not) {
			postKeys = keys;
		} {
			postKeys = this[1].keys.asArray.sort;
			if (keys.notNil) {
				postKeys = (keys ++ postKeys.removeAll(keys));
			};
		};
		this.do { |ev|
			var ev2 = ev.copy;
			postKeys.do { |key|
				"%: %, ".postf(key, ev2.removeAt(key));
			};
			if (ev2.size > 0) {
				".. %\n".postf(ev2);
			} {
				"".postln;
			};
		}
	}

	start { |absTime = 0|
		this.add((absTime: absTime, type: \start, delta: 0));
	}

	addEvent { |ev|
		if (array.size == 0) { this.start(ev[\absTime]) };
		super.add(ev);
		this.setDeltaInPrev(ev, this.lastIndex);
	}

	calcDeltas {
		this.doAdjacentPairs { |prev, next|
			var newDelta = next[\absTime] - prev[\absTime];
			prev.put(\delta, newDelta);
			prev.put(\dur, newDelta);
		};
		this.last.put(\delta, 0).put(\dur, 0);
	}

	finish { |absTime|
		this.addEvent((absTime: absTime, type: \end, delta: 0));
		totalDur = absTime - this.first[\absTime];
		playingDur = totalDur;
		this.setDursToDelta;
	}

	setDeltaInPrev { |newEvent, newIndex|
		var prevEvent;
		newIndex = newIndex ?? { array.indexOf(newEvent) };
		prevEvent = array[newIndex - 1];

		if (prevEvent.notNil) {
			prevEvent[\delta] = newEvent[\absTime] - prevEvent[\absTime];
		};
	}

	setDurs { |func| this.do { |ev| ev.put(\dur, func.value(ev)) } }

	setDursToDelta { this.setDurs({ |ev| ev[\delta] }); }

	quantizeDurs { |quant = 0.25, fullDur|
		var durScaler = 1;
		fullDur !? {
			playingDur = fullDur;
			durScaler = fullDur / totalDur;
		};

		this.doAdjacentPairs({ |ev1, ev2|
			var absNow = (ev2[\absTime] * durScaler).round(quant);
			var absPrev = (ev1[\absTime] * durScaler).round(quant);
			ev1.put(\dur, (absNow - absPrev));
		});
		// leaves end event untouched.
	}
	restoreDurs {
		this.setDursToDelta;
	}
}
