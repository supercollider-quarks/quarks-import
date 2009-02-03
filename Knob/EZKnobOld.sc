// blackrain at realizedsound dot net - 0106
//	03.10.2008 - Relative origin mods. Knob is a subclass of SCViewHolder now.

// This version of EZKnob is deprecated.
// The SuperCollider 3.3 alpha distribution includes EZKnob now.

EZKnobOld	{
	var <>labelView, <>knobView, <>numberView, <value, <>round = 0.0001, <>action, <>controlSpec;
	var <enabled, <cv;
	
	*new { arg window, dimensions, label, controlSpec, action, initVal, 
			initAction=false, labelWidth=40, numberWidth = 40, centered=false, back;
			
		^super.new.init(window, dimensions, label, controlSpec, action, initVal, 
			initAction, labelWidth, numberWidth, centered, back);
	}

	init { arg window, dimensions, label, argControlSpec, argAction, initVal, 
			initAction, labelWidth, numberWidth, centered, b;
		var left, top, width, height, bounds, flowBounds;

		dimensions = dimensions ?? ( 32 @ 16 );
	
		b = b ? Color.blue(0.2, alpha:0.1);
		width = labelWidth = numberWidth = max(dimensions.x, labelWidth).max(numberWidth) - 2;
		height = (dimensions.y * 2) + dimensions.x + 6;

		bounds = Point.new(width + 4, height + 4);

		cv = GUI.compositeView.new(window, bounds)
			.background_(b);

		flowBounds = cv.bounds;
		cv.decorator = FlowLayout.new(flowBounds, 2@0, 0@4);
	
		enabled = true;
		controlSpec = argControlSpec.asSpec;
		initVal = initVal ? controlSpec.default;
		action = argAction;

		labelView = GUI.staticText.new(cv, Rect(0, 0, labelWidth, dimensions.y));
		labelView.string = label;
		labelView.align = \center;

//		cv.decorator.nextLine;
//		cv.decorator.shift(((flowBounds.width - dimensions.x) / 2), 0);
		knobView = GUI.knob.new(cv, Rect(0, 0, dimensions.x, dimensions.x));
//		cv.decorator.shift(((flowBounds.width - dimensions.x) / 2).neg, 0);

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
		
		knobView.beginDragAction = { value };

		numberView = GUI.numberBox.new(cv, Rect(0, 0, numberWidth, dimensions.y));
		numberView.action = {
			numberView.value = value = controlSpec.constrain(numberView.value);
			knobView.value = controlSpec.unmap(value);
			action.value(this);
		};
		numberView.beginDragAction = { value };
		
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
		labelView.string = label ? labelView.string;
		controlSpec = (spec ? controlSpec).asSpec;
		action = argAction ? action;
		initVal = initVal ? value; //controlSpec.default;
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
