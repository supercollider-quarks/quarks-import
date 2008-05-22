
// 08/2006 - blackrain at realizedsound dot net
// 03.10.2008:
//	- relative origin
//	- A subclass of SCViewHolder
// 04.2008:
//	- build a class hierarchy
/	- FlatButtonView addition 

ToggleBaseView : SCViewHolder {
	var <value, <string, <hasBorder, <colorOn, <colorOff, <fontColorOn, <fontColorOff, font,
		fontColor, color, <>action;

	*new { arg parent, bounds;
		^super.new.init(parent, bounds);
	}
	init { arg parent, bounds;
		var area;
		
		this.view_(GUI.userView.new(parent, bounds))
			.relativeOrigin_(true);
		
		colorOn = Color.blue(0.7, 0.2);
		colorOff = Color.blue(0.1, 0.1);
		fontColorOn  = Color.black;
		fontColorOff = Color.black;
		hasBorder = true;
		this.canFocus_(true);
		this.value_(false);

		this.view.drawFunc_({
			var bounds;
			bounds = Rect(0,0,this.view.bounds.width, this.view.bounds.height);
			color = [colorOff, colorOn].at(this.value.binaryValue);
			area = bounds.insetBy(2,2);
			if (hasBorder) {
				Color.black.set;
				GUI.pen.strokeRect(bounds);
			};
			color.set;
			GUI.pen.fillRect(area);
			string.notNil.if({
				string.drawInRect(area, font, fontColor)
			})
		});
		this.view.mouseDownAction_({ arg view, x, y, modifiers, buttonNumber, clickCount;
			action.value(this, x, y, modifiers, buttonNumber, clickCount);
		});
	}
	
	value_ { arg val;
		if (val != value) {
			value = val;
			fontColor = [fontColorOff, fontColorOn].at(this.value.binaryValue);
			this.refresh;
		}
	}
	valueAction_ { arg val;
		if (val != value) {
			value = val;
			fontColor = [fontColorOff, fontColorOn].at(this.value.binaryValue);
			this.refresh;
			action.value(this);
		}
	}
	string_ { arg text; string = text; this.refresh }
	font_ { arg newfont; font = newfont; this.refresh }
	colorOn_  { arg color; colorOn  = color; this.refresh }
	colorOff_ { arg color; colorOff = color; this.refresh }
	fontColorOn_ { arg color; fontColorOn = color; this.refresh }
	fontColorOff_ { arg color; fontColorOff = color; this.refresh }
	hasBorder_ { arg bool; hasBorder = bool; this.refresh }
}

ToggleView : ToggleBaseView {
	init { arg parent, bounds;
		super.init(parent,bounds);

		this.view.mouseDownAction_({ arg view, x, y, modifiers, buttonNumber, clickCount;
			this.value_(this.value.not);
			action.value(this, x, y, modifiers, buttonNumber, clickCount);
		});
	}
}

TriggerView : ToggleBaseView {
	init { arg parent, bounds;
		super.init(parent,bounds);

		this.view.mouseDownAction_({ arg view, x, y, modifiers, buttonNumber, clickCount;
			this.value_(true);
			action.value(this, x, y, modifiers, buttonNumber, clickCount);
		});

		this.view.mouseUpAction_({ arg view, x, y, modifiers;
			this.value_(false);
			action.value(this, x, y, modifiers);
		});
	}
}

FlatButtonView : ToggleBaseView {
	init { arg parent, bounds;
		super.init(parent,bounds);

		this.view.mouseDownAction_({ arg view, x, y, modifiers, buttonNumber, clickCount;
			value = true;
			fontColor = fontColorOn;
			this.refresh;
		});
		this.view.mouseUpAction_({ arg view, x, y, modifiers;
			this.value_(false);
			if (this.view.bounds.containsPoint( Point(x,y) )) {
				action.value(this, x, y, modifiers);
			}
		});
		this.mouseMoveAction_({ arg view, x, y, modifiers;
			if ( this.view.bounds.containsPoint( Point(x,y) ) ) {
				this.value_(true)
			}{
				this.value_(false)
			}
		});
	}
}


/*
ToggleBaseView : SCUserView {
	var <value, <string, <hasBorder, <colorOn, <colorOff, <fontColorOn, <fontColorOff, font,
		fontColor, color;

	*viewClass { ^SCUserView }
	init { arg parent, bounds;
		super.init(parent, bounds);
		this.relativeOrigin_(false);
		colorOn = Color.blue(0.7, 0.2);
		colorOff = Color.blue(0.1, 0.1);
		fontColorOn  = Color.black;
		fontColorOff = Color.black;
		hasBorder = true;
		this.canFocus_(false);
		this.value_(false);
	}
	draw {
		var area;
		if (hasBorder) {
			Color.black.set;
			GUI.pen.strokeRect(this.bounds);
		};
		area = this.bounds.insetBy(2,2);
		color.set;
		GUI.pen.fillRect(area);
		string.notNil.if({
			string.drawInRect(area, font, fontColor)
		})
	}
	value_ { arg val;
		value = val;
		fontColor = [fontColorOff, fontColorOn].at(this.value.binaryValue);
		color = [colorOff, colorOn].at(this.value.binaryValue);
		this.refresh;
	}
	valueAction_ { arg val;
		value = val;
		fontColor = [fontColorOff, fontColorOn].at(this.value.binaryValue);
		color = [colorOff, colorOn].at(this.value.binaryValue);
		this.refresh;
		action.value(this);
	}
	string_ { arg text; string = text; this.refresh }
	font_ { arg newfont; font = newfont; this.refresh }
	colorOn_  { arg color; colorOn  = color; this.refresh }
	colorOff_ { arg color; colorOff = color; this.refresh }
	fontColorOn_ { arg color; fontColorOn = color; this.refresh }
	fontColorOff_ { arg color; fontColorOff = color; this.refresh }
	hasBorder_ { arg bool; hasBorder = bool; this.refresh }
	mouseDown { arg x, y, modifiers, buttonNumber, clickCount;
		action.value(this, x, y, modifiers, buttonNumber, clickCount);
	}
}

FlashingView : ToggleBaseView {
	classvar <all;
	var saveVal, r, <flashing=false, <rate=1.0, <ratio=1.0;
	
	*initClass {
		all = Set.new;
		StartUp.add {
			CmdPeriod.add(this);
		}
	}
	init { arg parent, bounds;
		super.init(parent, bounds);
		saveVal = this.value;
		flashing=false;
		all.add(this);
		this.onClose = { all.remove(this); r.stop };
	}
	*cmdPeriod {
		all.do({ arg v;
			if (v.flashing) {
				v.installFlashTask
			}
		});
	}
	start { arg arate=1.0, aratio=1.0;
		saveVal = this.value;
		flashing=true;
		this.installFlashTask;
	}
	stop {
		r.stop;
		flashing=false;
		this.value_(saveVal);
	}
	*stopAll {
		all.do(_.stop)
	}
	installFlashTask {
		var w1,w2;
		r = Routine({
			loop {
				w1 = 0.5;
				w2 = 0.5;
				this.value_(true);
				w1.wait;
				this.value_(false);
				w2.wait;
			}
		}).play(AppClock);
	}
	rate_ { arg argRate;
		rate = argRate.max(0.1);
	}
	ratio_ { arg argRatio;
		ratio = argRatio.max(0.1);
	}
}
*/

