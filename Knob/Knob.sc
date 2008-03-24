
// blackrain at realizedsound dot net - 05/2006
//	fix key modidiers bug by Stephan Wittwer 08/2006 - thanks!
//	Knob updates only on value changes - 10/2006
//	GUI.cocoa changes - 04/2007
//	
//	03.10.2008 - new implementation:
//		- Knob now is a subclass of SCViewHolder
//		- Relative origin

Knob : SCViewHolder {
	classvar <useRelativeOrigin=false, <>defaultMode;
	var <>color, <>action, <value, last, <>step, hit, <>keystep, <>mode, isCentered = false;
	var <skin;
	var <>mouseOverAction;

	*initClass {
		var version;
		
		defaultMode='round';

		StartUp.add({ var kit;
			
			version = Main.tryPerform(\version);
			if ( version.notNil and: { version >= "3.2" } ) {
				useRelativeOrigin = true;
			};
				
			kit = GUI.schemes[ \cocoa ];
			if( kit.notNil, { kit.knob = Knob });
			kit = GUI.schemes[ \swing ];
			if( kit.notNil, { kit.knob = Knob });

			GUI.skins.isNil({ GUI.put(\skins) });
			GUI.skins.put('knob', (
				default: (
					scale:	Color.black.alpha_(0.3),
					center:	Color.blue(0.7, 0.5),
					level:	Color.green(0.8, 0.8),
					dial:	Color.black.alpha_(0.7),
					defaultMode: 'round'
				)
			));
			
		});
	}
	*new { arg parent, bounds;
		^super.new.init(parent, bounds);
	}
	init { arg parent, bounds;
		var size, widthDiv2, center;
		bounds = bounds.asRect.bounds.height_( bounds.asRect.bounds.width);
		this.view_(GUI.userView.new(parent, bounds));
		this.view.tryPerform(\relativeOrigin_, useRelativeOrigin);
		
		value = 0.0;
		mode = defaultMode;
		keystep = 0.01;
		step = 0.01;
		
		skin = GUI.skins.knob.default;

		this.oldMethodsCompat(skin);
		
		size = this.view.bounds.width;
		widthDiv2 = size * 0.5;
		center = Point(widthDiv2, widthDiv2);
		
		if (useRelativeOrigin.not) {
			center = center + (this.view.bounds.left @ this.view.bounds.top);
		};
		
		this.view.drawFunc_({
			var startAngle, arcAngle, aw;

			color[2].set;
			GUI.pen.addAnnularWedge(
				center,
				widthDiv2 - (0.08 * size), 
				widthDiv2, 	
				0.25pi, 
				-1.5pi
			);
			GUI.pen.perform(\fill);
	
			if (isCentered.not, {
				startAngle = 0.75pi; 
				arcAngle = 1.5pi * value;
			}, {
				startAngle = -0.5pi; 
				arcAngle = 1.5pi * (value - 0.5);
			});
	
			color[1].set;
			GUI.pen.addAnnularWedge(
				center, 
				widthDiv2 - (0.12 * size), 
				widthDiv2, 	
				startAngle, 
				arcAngle
			);
			GUI.pen.perform(\fill);
	
			color[0].set;
			aw = widthDiv2 - (0.14 * size);
			GUI.pen.addWedge(center, aw, 0, 2pi);
			GUI.pen.perform(\fill);
	
			color[3].set;
			GUI.pen.width = (0.08 * size);
			GUI.pen.moveTo(center);
			GUI.pen.lineTo(Polar.new(aw, 0.75pi + (1.5pi * value)).asPoint + center);
			GUI.pen.stroke;
		});

		this.view.mouseDownAction_({ arg view, x, y, modifiers, buttonNumber, clickCount;
			if (useRelativeOrigin.not) {
				hit = x @ y;
			}{
				hit = view.mousePosition;
			};
			view.mouseMoveAction.value(view, x, y, modifiers);
		});
		
		this.view.mouseMoveAction_({ arg view, x, y, modifiers;
			var mp, pt, angle, inc = 0;
			
			if (useRelativeOrigin) {
				mp = view.mousePosition;
				x = mp.x; y = mp.y;
			};
			
			if (modifiers & 1048576 != 1048576) { // we are not dragging out - apple key
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
						pt = center - Point(x,y);
						angle = Point(pt.y, pt.x.neg).theta;
						if ((angle >= -0.80pi) and: { angle <= 0.80pi} , {
							value = [-0.75pi, 0.75pi].asSpec.unmap(angle);
							if (last != value) {
								action.value(this, x, y, modifiers);
								last = value;
								this.refresh;
							}
						});
	
					}
			}
		});
		
		this.view.mouseOverAction_({ arg v, x, y;
			this.mouseOverAction.value(this, x, y);
		});

		this.view.keyDownAction_({ arg view, char, modifiers, unicode, keycode;
			// standard keydown
			if (char == $r, { this.valueAction = 1.0.rand; });
			if (char == $n, { this.valueAction = 0.0; });
			if (char == $x, { this.valueAction = 1.0; });
			if (char == $c, { this.valueAction = 0.5; });
			if (char == $[, { this.decrement; this});
			if (char == $], { this.increment; this });
			if (unicode == 16rF700, { this.increment; this });
			if (unicode == 16rF703, { this.increment; this });
			if (unicode == 16rF701, { this.decrement; this });
			if (unicode == 16rF702, { this.decrement; this });
		});

		this.view.receiveDragHandler = { this.valueAction_(SCView.currentDrag); };
		this.view.beginDragAction = { value.asFloat; };
		this.view.canReceiveDragHandler = { SCView.currentDrag.isNumber };
	}

	increment { ^this.valueAction = (this.value + keystep).min(1) }

	decrement { ^this.valueAction = (this.value - keystep).max(0) }

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
	
	skin_ { arg newskin;
		if ( newskin.notNil ) {
			skin = newskin;
			newskin.proto_( GUI.skins.knob.default );
			this.oldMethodsCompat;
			this.refresh;
		}{
			format("%: skin not found.", this.class).inform;
		};
	}
	oldMethodsCompat {
		color = [
			skin.center,
			skin.level,
			skin.scale,
			skin.dial
		];
		defaultMode = skin.defaultMode;
	}
}
