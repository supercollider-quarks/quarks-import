CtLoopGui { 
	var <w, currLoop;
	
	*new { |nLoops| ^super.new.init(nLoops); }
	
	init { |n| 
	
		var k;
		Spec.add(\lSpeed, [0.1, 10, \exp, 0.01, 1]);
		Spec.add(\lStart, [0, 1]);
		Spec.add(\length, [0.0, 1]);
		Spec.add(\jitter, [0.0, 1]);
		Spec.add(\lScale, [0.0, 4, \amp]);
		Spec.add(\lShift, [0.0, 4, \amp]);
		
		k = (0..7).collect(_.asSymbol);
		w = GUI.window.new("CtLoops", Rect(0,0,210, 400)).front;
		w.view.decorator = FlowLayout(w.bounds.moveTo(0,0));
		
		GUI.popUpMenu.new(w, Rect(0,0,30,20))
			.items_(k)
			.action_({ |pop| pop.items[pop.value].postln; });
		
		k.collect { |key| GUI.button.new(w, Rect(0,0, 20, 20))
			.states_([[key]])
			.action_({ |btn| key.postln });
		}; 
		
		w.view.decorator.nextLine;
		[\lSpeed, \lStart, \length, \jitter, \lScale, \lShift ]
		.collect({ |lbl|
			EZSlider.new(w, 200@20, lbl, lbl.asSpec, 
				{ |ez| ez.value.postln }, 0.5, false, 40, 60);
		});
	}
}