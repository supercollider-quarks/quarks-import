// blackrain at realizedsound dot net - 0106

EZKnob	{
	var <>labelView, <>knobView, <>numberView, <value, <>round = 0.0001, <>action, <>controlSpec;
	var <enabled, <cv;

	*new { arg window, dimensions, label, controlSpec, action, initVal, 
			initAction=false, labelWidth=40, numberWidth = 40, centered=false, back;
			
		^super.new.init(window, dimensions, label, controlSpec, action, initVal, 
			initAction, labelWidth, numberWidth, centered, back);
	}

	init { arg window, dimensions, label, argControlSpec, argAction, initVal, 
			initAction, labelWidth, numberWidth, centered, b;
		var width, height, bounds;

		dimensions = dimensions ?? ( 32 @ 16 );
	
		b = b ? Color.blue(0.2, alpha:0.1);
		width = max(numberWidth, labelWidth) + 8;
		height = (dimensions.y * 2 + 8) + dimensions.x + 8;

		bounds = Point.new(width, height);
		
		cv = SCCompositeView.new(window, bounds)
			.background_(b);
			
		cv.decorator = FlowLayout.new(cv.bounds);
	
		enabled = true;
		controlSpec = argControlSpec.asSpec;
		initVal = initVal ? controlSpec.default;
		action = argAction;

		labelView = SCStaticText(cv, labelWidth @ dimensions.y);
		labelView.string = label;
		labelView.align = \center;

		knobView = Knob(cv, dimensions.x @ dimensions.x);
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

		numberView = SCNumberBox(cv, numberWidth @ dimensions.y);
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
		cv.visible_(bool)
	}
	enabled_ { arg bool;
		enabled = bool;
		[knobView, numberView].do(_.enabled_(bool))
	}
}
