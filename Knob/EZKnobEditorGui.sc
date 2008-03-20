
EZKnobEditorGui : KrKnobEditorGui {
	var <>knob, <>numv, <>roundVal = 0.0001, size, <enabled=true, backColor;
	guiBody { arg layout, knobSize=36, numWidth=40, numHeight=14, background, centered=false;
		
		knob = EZKnob.new(layout, knobSize@numHeight, model.name, model.spec.asSpec, { arg ctrl;
			model.set(ctrl.value).changed(this);
		}, model.value, false, numWidth, numWidth, centered, background);
	
	}
	centered_ { arg mode;
		if ( knob.notNil ) {
			knob.knobView.centered = mode;
		}
	}
	enabled_ { arg state=true;
		enabled = state;
		numv.enabled_(enabled);
		if ( knob.notNil ) {
			knob.enabled_(enabled);
		}
	}
	update { arg changed, changer;
		if ( changer !== this) {
			{ knob.value = model.value }.defer;
		}
	}
}
