// blackrain at realizedsound dot net - 0106

EZKnob	{
	var <>labelView, <>knobView, <>numberView, <value, <>round = 0.0001, <>action, <>controlSpec;
	var layout, <enabled;

	*new { arg window, dimensions, label, controlSpec, action, initVal, 
			initAction=false, labelWidth=40, numberWidth = 40, centered=false, back;
			
		^super.new.init(window, dimensions, label, controlSpec, action, initVal, 
			initAction, labelWidth, numberWidth, centered, back);
	}

	init { arg window, dimensions, label, argControlSpec, argAction, initVal, 
			initAction, labelWidth, numberWidth, centered, b;
		var width;
		var decorator = window.tryPerform(\decorator), gap = decorator.tryPerform(\gap);

		dimensions = dimensions ?? ( 32 @ 16 );
	
		b = b ? Color.blue(0.2, alpha:0.1);
	
		enabled = true;
		controlSpec = argControlSpec.asSpec;
		initVal = initVal ? controlSpec.default;
		action = argAction;

		width = labelWidth.max(dimensions.x).max(numberWidth);
		layout = FlowView.new(window, (width + 4) @
			(dimensions.y + dimensions.x + dimensions.y + 11));
		layout.do({ arg lay;
			labelView = SCStaticText(lay, width @ dimensions.y);
			labelView.string = label;
			labelView.align = \center;
	
			knobView = Knob(lay, dimensions.x @ dimensions.x);
			knobView.action = {
				value = controlSpec.map(knobView.value);
				numberView.value = value.round(round);
				action.value(this);
			};
			if (controlSpec.step != 0) {
				knobView.step = (controlSpec.step / (controlSpec.maxval - controlSpec.minval));
			};
			
			knobView.centered = centered;
			
			knobView.receiveDragHandler = { arg kn;
				kn.valueAction = controlSpec.unmap(SCView.currentDrag);
			};
			
			knobView.beginDragAction = { arg kn;
				controlSpec.map(kn.value)
			};

			numberView = SCNumberBox(lay, numberWidth @ dimensions.y);
			numberView.action = {
				numberView.value = value = controlSpec.constrain(numberView.value);
				knobView.value = controlSpec.unmap(value);
				action.value(this);
			};
			
			if (initAction, {
				this.value = initVal;
			}, {
				value = initVal;
				knobView.value = controlSpec.unmap(value);
				numberView.value = value.round(round);
			});
		}).background_(b);

	}
	value_ { arg value; if( knobView.enabled, { numberView.valueAction = value }) }
	set { arg label, spec, argAction, initVal, initAction=false;
		labelView.string = label;
		controlSpec = spec.asSpec;
		action = argAction;
		initVal = initVal ? controlSpec.default;
		if( knobView.enabled, { 
			if (initAction) {
				this.value = initVal;
			}{
				value = initVal;
				knobView.value = controlSpec.unmap(value);
				numberView.value = value.round(round);
			};
		});
	}
	centered_ { arg bool;
		knobView.centered_(bool)
	}
	visible_ { arg bool;
		layout.visible_(bool)
	}
	enabled_ { arg bool;
		enabled = bool;
		[knobView, numberView].do(_.enabled_(bool))
	}
}
