
(
	a = [
			EZKnobEditor("knob 1", 0.25),
			EZKnobEditor("knob 2", 110.0, \freq),
			EZKnobEditor("knob 3", 0.0, \pan),
			EZKnobEditor("knob 4", 0.7, \rq),
			EZKnobEditor("knob 5", 0.0, \boostcut)
			
		];

	Sheet({ arg layout;
		g = a.collect({ arg k; k.gui(layout) });
	}, "EZKnobEditor Cluster")
)

a.collect( _.value );
g.at(2).centered_(true)

