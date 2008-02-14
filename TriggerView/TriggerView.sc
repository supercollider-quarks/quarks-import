
// 08/2006 - blackrain at realizedsound dot net

TriggerView : SCUserView {
	var <value, <>hitColor, <>fillColor, <>caption, <>font, <>fontColor, area;
	*viewClass { ^SCUserView }
	
	*paletteExample { arg parent, bounds;
		^TriggerView(parent, bounds)
	}

	init { arg parent, bounds;
		super.init(parent, bounds);
		this.relativeOrigin_(false);
		hitColor = Color.blue(0.7, 0.2);
		fillColor = Color.blue(0.0, 0.0);
		fontColor = Color.black;
	//	this.canFocus_(false);
		this.value_(false);
	}
	
	draw {
		area = this.bounds.insetBy(2,2);
		Color.black.set;
		Pen.strokeRect(this.bounds);
		if (value, {
			hitColor.set;
		},{
			fillColor.set;
		});
		Pen.fillRect(area);
		caption.notNil.if({
			caption.drawInRect(area, font, fontColor)
		})
	}

	mouseDown { arg x, y, modifiers, buttonNumber, clickCount;
		this.value_(true);
		action.value(this, x, y, modifiers, buttonNumber, clickCount);
	}
	mouseUp{arg x, y, modifiers;
		this.value_(false);
		action.value(this, x, y, modifiers);
	}
	value_ { arg val;
		value = val;
		this.refresh;
	}
	valueAction_ { arg val;
		value = val;
		action.value(this);
		this.refresh;
	}
}

ToggleView : SCUserView {
	var <value, <>fillColor, <>hitColor, <>caption, <>font, <>fontColor, area;
	*viewClass { ^SCUserView }
	
	*paletteExample { arg parent, bounds;
		^ToggleView(parent, bounds)
	}

	init { arg parent, bounds;
		super.init(parent, bounds);
		this.relativeOrigin_(false);
		hitColor = Color.blue(0.7, 0.2);
		fillColor = Color.blue(0, 0);
		fontColor = Color.black;
	//	this.canFocus_(false);
		value = false;
	}
	
	draw {
		area = this.bounds.insetBy(2,2);
		Color.black.set;
		Pen.strokeRect(this.bounds);
		if (value, {
			hitColor.set;
		},{
			fillColor.set;
		});
		Pen.fillRect(area);
		caption.notNil.if({
			caption.drawInRect(area, font, fontColor)
		})
	}

	mouseDown { arg x, y, modifiers, buttonNumber, clickCount;
		this.value_(this.value.not);
		this.refresh;
		action.value(this, x, y, modifiers, buttonNumber, clickCount);
	}
	
	value_ { arg val;
		value = val;
		this.refresh;
	}
	valueAction_ { arg val;
		value = val;
		this.refresh;
		action.value(this);
	}
}
