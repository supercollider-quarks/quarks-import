
// blackrain at realizedsound dot net - 0106

KnobEditorGui : EditorGui {
	var <>knob, <>numv, <>roundVal = 0.001, size, <enabled=true;

	guiBody { arg layout, size=28, centered=false, hasBox=true;
		this.kn(layout, size, centered);
		if(hasBox,{
			layout.startRow;
			this.box(layout, size);
		});
	}
	
	kn { arg layout, size, centered=false;
		knob = GUI.knob.new(layout, size @ size);
		knob.centered = centered;
		knob.color[0] = this.knobColor;
		knob.action_({arg v; 
			model.activeValue_(model.spec.map(v.value)).changed(this);
		});
		knob.receiveDragHandler = { arg kn;
			kn.valueAction = model.spec.unmap(SCView.currentDrag);
		};
		knob.beginDragAction = { arg kn;
			model.spec.map(kn.value)
		};
		knob.value = model.spec.unmap(model.poll);
		if(consumeKeyDowns,{ knob.keyDownAction = {nil}; });
	}

	box { arg layout, x, y=17;
		var r;
		x = x ? 40;
		numv = GUI.numberBox.new(layout, x.max(40) @ y)
			.object_(model.poll)
			.action_({ arg nb;
				model.activeValue_(nb.value).changed(numv);
			})
			.canReceiveDragHandler_({ SCView.currentDrag.isFloat })
			.beginDragAction_({|v|
				model.value
			})
			.receiveDragHandler_({|v|
				v.value = SCView.currentDrag.round(roundVal);
				model.activeValue_(SCView.currentDrag).changed(numv);
			})
			.value_(model.value.round(roundVal));
		numv.step = roundVal;
		if(consumeKeyDowns,{ numv.keyDownAction = {nil}; });
	}
	
	centered_ { arg mode;
		knob.centered = mode;
	}
	
	enabled_ { arg state=true;
		enabled = state;
		[knob,numv].do(_.enabled_(enabled));
	}
	
	update {arg changed,changer;
		{
			var val;
			if(changer !== numv and: {numv.notNil},{
				val = model.poll;
				if (val <= -10, { roundVal = 0.01; });
				numv.value_(val.round(roundVal));
			});
			if(changer !== knob,{
				knob.value_(model.spec.unmap(model.poll));
			});
			nil
		}.defer;
	}

	background { ^Color.blue(0.2, 0.1) }
	knobColor { ^Color.blue(0.6, 0.3) }
}

KrKnobEditorGui : KnobEditorGui {
	knobColor { ^Color.blue(0.8, 0.4) }
}

IrKnobEditorGui : KnobEditorGui {
	knobColor { ^Color.black.alpha_(0.3) }
}
