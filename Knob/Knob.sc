// blackrain at realizedsound dot net - 05/2006
//	fix key modidiers bug by Stephan Wittwer 08/2006 - thanks!
//	Knob updates only on value changes - 10/2006
//	GUI.cocoa changes - 04/2007

Knob : SCUserView {
	var <>color, <value, last, <>step, hit, <>keystep, <>mode, isCentered = false;
	
	*viewClass { ^GUI.userView }

	*initClass {
		if (\JSCWindow.asClass.notNil) {
			GUI.schemes.at(\swing).put(\knob, JKnob)
		}
	}

	*paletteExample { arg parent, bounds;
		^GUI.knob.new(parent,bounds)
	}
	
	init { arg parent, bounds;
		super.init(parent, bounds);
		mode = \round;
		keystep = 0.01;
		step = 0.01;
		value = 0.0;
		
		color = [Color.blue(0.7, 0.5), Color.green(0.8, 0.8), Color.black.alpha_(0.3),
			Color.black.alpha_(0.7)];
	}
	
	draw {
		var startAngle, arcAngle, size, widthDiv2, aw;
		size = this.bounds.width;
		widthDiv2 = this.bounds.width * 0.5;
		
		color[2].set;
		Pen.addAnnularWedge(
			this.bounds.center, 
			widthDiv2 - (0.08 * size), 
			this.bounds.width * 0.5, 	
			0.25pi, 
			-1.5pi
		);
		Pen.perform(\fill);

		if (isCentered.not, {
			startAngle = 0.75pi; 
			arcAngle = 1.5pi * value;
		}, {
			startAngle = -0.5pi; 
			arcAngle = 1.5pi * (value - 0.5);
		});

		color[1].set;
		Pen.addAnnularWedge(
			this.bounds.center, 
			widthDiv2 - (0.12 * size), 
			widthDiv2, 	
			startAngle, 
			arcAngle
		);
		Pen.perform(\fill);

		color[0].set;
		aw = widthDiv2 - (0.14 * size);
		Pen.addWedge(this.bounds.center, aw, 0, 2pi);
		Pen.perform(\fill);

		color[3].set;
		Pen.width = (0.08 * size);
		Pen.moveTo(this.bounds.center);
		Pen.lineTo(Polar.new(aw, 0.75pi + (1.5pi * value)).asPoint + this.bounds.center);
		Pen.stroke;
	}

	mouseDown { arg x, y, modifiers, buttonNumber, clickCount;
		hit = Point(x,y);
		this.mouseMove(x, y, modifiers);
	}
	
	mouseMove { arg x, y, modifiers;
		var pt, angle, inc = 0;

		if (modifiers & 1048576 != 1048576, { // we are not dragging out - apple key
			case
				{ (mode == \vert) || (modifiers & 262144 == 262144) } { // Control
					if ( hit.y > y, {
						inc = step;
					}, {
						if ( hit.y < y, {
							inc = step.neg;
						});
					});
					value = (value + inc).clip(0.0, 1.0);
					hit = Point(x,y);
					if (last != value) {
						action.value(this, x, y, modifiers);
						last = value;
						this.refresh;
					}
				}
				{ (mode == \horiz) || (modifiers & 524288 == 524288) } { // Option
					if ( hit.x > x, {
						inc = step.neg;
					}, {
						if ( hit.x < x, {
							inc = step;
						});
					});
					value = (value + inc).clip(0.0, 1.0);
					hit = Point(x,y);
					if (last != value) {
						action.value(this, x, y, modifiers);
						last = value;
						this.refresh;
					}
				}
				{ mode == \round } {
					pt = this.bounds.center - Point(x,y);
					angle = Point(pt.y, pt.x.neg).theta;
					if ((angle >= -0.80pi) && (angle <= 0.80pi), {
						value = [-0.75pi, 0.75pi].asSpec.unmap(angle);
						if (last != value) {
							action.value(this, x, y, modifiers);
							last = value;
							this.refresh;
						}
					});

				}
		});
	}
	
	value_ { arg val;
		value = val.clip(0.0, 1.0);
		this.refresh;
	}

	valueAction_ { arg val;
		value = val.clip(0.0, 1.0);
		action.value(this);
		this.refresh;
	}

	centered_ { arg bool;
		isCentered = bool;
		this.refresh;
	}
	
	centered {
		^isCentered
	}

	increment { ^this.valueAction = (this.value + keystep).min(1) }
	decrement { ^this.valueAction = (this.value - keystep).max(0) }

	keyDown { arg char, modifiers, unicode,keycode;
		// standard keydown
		if (char == $r, { this.valueAction = 1.0.rand; });
		if (char == $n, { this.valueAction = 0.0; });
		if (char == $x, { this.valueAction = 1.0; });
		if (char == $c, { this.valueAction = 0.5; });
		if (char == $], { this.increment; ^this });
		if (char == $[, { this.decrement; ^this });
		if (unicode == 16rF700, { this.increment; ^this });
		if (unicode == 16rF703, { this.increment; ^this });
		if (unicode == 16rF701, { this.decrement; ^this });
		if (unicode == 16rF702, { this.decrement; ^this });
	}

	defaultReceiveDrag {
		this.valueAction_(SCView.currentDrag);
	}
	defaultGetDrag { 
		^value
	}
	defaultCanReceiveDrag {
		^currentDrag.isFloat;
	}
}

