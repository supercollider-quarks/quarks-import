/*
	
*/

/*
w = Window.new("test", Rect(0,0, 800, 600)).front;
w.view.decorator = FlowLayout.new(w.view.bounds);
b = {
	var spec = Spec.specs.select({|x| x.isKindOf(ControlSpec)}).keys.choose;
	MinBox.new(w, (0@0)@([rrand(20,100)@rrand(20,100)].choose))
		.mode_(MinBox.modes.choose)
		.textMode_(MinBox.textModes.choose)
		.label_(spec.asString)
		.align_(MinBox.aligns.choose)
		.spec_(Spec.specs[spec])
		.input_(1.0.rand)

}!24;


w = Window.new.front;
w.view.decorator = FlowLayout.new(w.view.bounds);
//b = MinBox.new(w, Rect(20,20,88,30)).resize_(8);
b = {MinBox.new(w, Rect(0,0,80,40))}!24;
b.collect {|x| x.mode = \round }

b.collect {|x| x.step = 0.001 };
b.collect {|x| x.spec = \freq.asSpec };
b.collect {|x| x.round = 1 };
(
~spec = \freq.asSpec;
~step = 0.0001;
~hitSpec = [0.00001, 0.25,2].asSpec;
b.collect {|er| 
	er.mouseDownAction = {|view, x, y, mod|
		~hit = view.mousePosition;
		~hit.postln;
		~prev = ~hit.y;
		~val = ~spec.default;
	};
	
	er.mouseMoveAction = {|view, x, y, mod|
		~diff = (~prev - view.mousePosition.y);
		~run = ~diff.sign*~hitSpec.map((~prev - view.mousePosition.y).abs/200);
		//~run.postln;
		~val = ~spec.map(~spec.unmap(~val) + (~run));
		~val.postln;
		~prev = view.mousePosition.y;
		
	};
};
)
*/
MinBox : ViewRedirect { 

	*key { ^\minBox } 

	*initClass {
		Class.initClassTree(GUI);	

		StartUp.add({
			var scheme;
			
			scheme = GUI.get( \cocoa );
			if( scheme.notNil, {scheme.put( \minBox, SCMinBox )});
			scheme = GUI.get( \swing );
			if( scheme.notNil, {scheme.put( \minBox, JSCMinBox )});
			
			GUI.skins.default.put('minbox', (
				default: (
					hi:	Color(0.6, 0.2, 0.0, 1.0),
					lo:	Color(0.2, 0.4, 0.6, 1.0),
					text:	Color.gray(0.9,0.8),
					type:	Color(1.0, 0.8, 0.2, 1.0),
					line:	Color.gray(0.1,0.8),
					font:	Font("Monaco", 12.0),
					defaultMode: 'vert'
				)
			));
			
		});

	}
}

SCMinBox : SCUserView {
	classvar <>defaultMode;
	classvar <modes, <textModes, <aligns;

	var center, or, ir, radius, hit, typing=false, string, stringColor;
	var showLabel, showValue, labelr,valuer;
	var <value, prevValue, <mode, <textMode;
	var <>loColor, <>hiColor, <>normalColor, <>typingColor, <>lineColor, <>font, <skin;
	var <>step, <round, <>align, <spec;
	var keyString, <>editable;
	var <label;

	*viewClass { ^SCUserView }
	
	*initClass {

		defaultMode='vert';
		modes = [\vert, \horiz, \round, \blend,\clear, \button];
		textModes = [\both,\switch,\label,\value];
		aligns = [\left, \right, \center, \full];
		
		Class.initClassTree(GUI);	
	}

	init { arg argParent, argBounds;
	

		argBounds = this.calcConsts(argBounds.asRect);
		
		super.init(argParent, argBounds);
		value = 0;
		round = 0.01;
		this.spec = \unipolar; // also sets value
		
		editable = true;

		string = value.round(round).asString;
		step = 0.001;
		mode = defaultMode;
		
		// NOTE not efficient, calls calcConstants again
		this.textMode_(\value);

		align = \center;
		label = "";
		
		
		skin = GUI.skins.default.minbox.default;

		this.oldMethodsCompat(skin);

		this.receiveDragHandler = { this.valueAction_(SCView.currentDrag); };
		this.beginDragAction = { value.asFloat; };
		this.canReceiveDragHandler = { SCView.currentDrag.isNumber };
		
		^this;
	}

	calcConsts { arg rect;
		or = ((0.5@0.5)@(rect.extent-(0.5@0.5)));
		ir = Rect.fromRect(or);
		
		if([\both,\show].includes(textMode)) {
			labelr = or.insetBy(2).height_((or.height*0.5).max(16));
			valuer = Rect.fromRect(labelr).bottom_(or.bottom-2);
		} {
			labelr = or.insetBy(2);
			valuer = labelr;
		};

		radius = (or.width + or.height)*0.5;
		center = or.center;
				
		^rect
	}

	bounds_ { arg rect;
		rect = this.calcConsts(rect);
		super.bounds_(rect);
	}

	mode_ { arg argMode;
		if(argMode==\default) 
		{ mode = defaultMode }
		{ mode = argMode };
		this.calcConsts(this.bounds);
	}

	textMode_ { arg argTextMode=\value;
		textMode = argTextMode;
		showLabel = [\label,\both,\show,\switch].includes(textMode);
		showValue = [\value,\both,\show,\default].includes(textMode);

		this.calcConsts(this.bounds);
	}

	draw {
		var in = this.input;
		
		SCPen.width =1;
		
		
		
		switch(mode)
			{ \round } {
				SCPen.fillColor = lineColor;
				SCPen.fillRect(or);
				SCPen.fillColor = loColor;
				SCPen.strokeColor_(lineColor);
				SCPen.addAnnularWedge(or.center, 0, radius, -1.25pi, 1.5pi);
				SCPen.tryPerform(\fillStroke);
				SCPen.fillColor = hiColor;
				SCPen.addAnnularWedge(or.center, 0, radius, -1.25pi, in*1.5pi);
				SCPen.tryPerform(\fillStroke);
			}
			{\vert } {
				ir.top_(or.height*(1-in));
				SCPen.color = loColor;
				SCPen.fillRect(or);
				SCPen.color = hiColor;
				SCPen.fillRect(ir);
			
				SCPen.strokeColor_(lineColor);
				SCPen.addRect(or);
				SCPen.line((ir.leftTop),(ir.rightTop));
				SCPen.perform(\stroke);			
			}
			{\horiz } {
				ir.right_(or.width*in);
				SCPen.color = loColor;
				SCPen.fillRect(or);
				SCPen.color = hiColor;
				SCPen.fillRect(ir);
			
				SCPen.strokeColor_(lineColor);
				SCPen.addRect(or);
				SCPen.line((ir.rightTop),(ir.rightBottom));
				SCPen.perform(\stroke);	
			}
			{\blend} {
				SCPen.fillColor = loColor.blend(hiColor, in);
				SCPen.strokeColor_(lineColor);
				SCPen.addRect(or);
				SCPen.tryPerform(\fillStroke);
				
			}
			{\button} {
				SCPen.fillColor = loColor.blend(hiColor, in);
				SCPen.strokeColor_(lineColor);
				SCPen.addRect(or);
				SCPen.tryPerform(\fillStroke);
				
			}
			{\clear} {
				SCPen.fillColor = lineColor;
				SCPen.fillRect(or);
			};

/* NOTE commented out for 3.2 compatibility - the shadow looks nicer */
//		SCPen.setShadow(3@3, 1, Color.gray(0,0.75));
		SCPen.color = stringColor;
		SCPen.font = font;

		if(showLabel) {
			if(align==\full) { SCPen.stringLeftJustIn(label, labelr) } // if full, draw left
			{ SCPen.stringCenteredIn(label, labelr) }; // else centered
		};

		if(showValue) {
			switch(align)
			{ \left } { SCPen.stringLeftJustIn(string, valuer) }
			{ \right } { SCPen.stringRightJustIn(string, valuer) }
			{ \center } { SCPen.stringCenteredIn(string, valuer) }
			{ \full } { SCPen.stringRightJustIn(string, valuer) }
			{ SCPen.stringCenteredIn(string, valuer) };
		};
		
	}

	mouseDown { arg x, y, modifiers, buttonNumber, clickCount;
		var in = this.input;
		var inc;
		
		hit = x @ y;
		
		mouseDownAction.value(this, x, y, modifiers, buttonNumber, clickCount);
		
		this.mouseMove(x, y, modifiers);
		
		if(textMode==\switch) {
			showLabel = false;
			showValue = true;
		};
		
		if(mode==\button) {
			if(this.editable) {
//				inc = case
//					{ (modifiers & 262144 == 262144) } { if(in>=0.5) { 0.0 } { 1.0 } }
//					{ (modifiers & 524288 == 524288) } { (in-step).wrap(0.0, 1.0) }
//					{ (in+step).wrap(0.0, 1.0001) };
				inc = if(in>=0.5) { 0.0 } { 1.0 };

				this.valueAction = spec.map( inc ).round(round);
			}
		};

	}
		
	mouseUp { arg x, y, modifiers, buttonNumber, clickCount;
		if(textMode==\switch) {
			showLabel = true;
			showValue = false;
		};
		
		this.refresh;
		mouseUpAction.value(this, x, y, modifiers, buttonNumber, clickCount);
	}
	
	mouseMove { arg x, y, modifiers;
		var mp, pt, angle, inc = 0;
		var in = this.input;
		
		if(this.editable) {
			inc = case
				{ (modifiers & 262144 == 262144) } { 10 }
				{ (modifiers & 524288 == 524288) } { 0.1 }
				{ 1 };
							
			if (modifiers & 1048576 != 1048576) { // we are not dragging out - apple key
				case
					{ [\vert, \blend, \clear].includes(mode) } { 
						
						inc = inc*step*(hit.y - y);
						this.valueAction = spec.map(in + inc);
						hit = Point(x,y);
					}
					{ (mode == \horiz) } { 
						inc = inc*step.neg*(hit.x - x);
						this.valueAction = spec.map(in + inc).round(round);
						hit = Point(x,y);
					}
					{ mode == \round } {
						pt = center - Point(x,y);
						angle = Point(pt.y, pt.x.neg).theta;
						if ((angle >= -0.80pi) and: { angle <= 0.80pi} , {
							this.valueAction = spec.map([-0.75pi, 0.75pi].asSpec
								.unmap(angle)).round(round);
						});
					}
			};
		};

		mouseMoveAction.value(this, x, y, modifiers);	
	}	

	increment { this.valueAction = spec.map(this.input + step); }
	decrement { this.valueAction = spec.map(this.input - step); }
	
	defaultKeyDownAction { arg char, modifiers, unicode;
	
		if(this.editable) {
			
			// standard chardown
			if (unicode == 16rF700, { this.increment; ^this });
			if (unicode == 16rF703, { this.increment; ^this });
			if (unicode == 16rF701, { this.decrement; ^this });
			if (unicode == 16rF702, { this.decrement; ^this });
			if ((char == 3.asAscii) || (char == $\r) || (char == $\n), { // enter key
				if (keyString.notNil,{ // no error on repeated enter
				
					this.textMode_(textMode);

					this.valueAction_(keyString.asFloat);
				});
				^this
			});
			if (char == 127.asAscii, { // delete key
				keyString = nil;
				string = (value.round(round)).asString;
				stringColor = normalColor;

				this.textMode_(textMode);

				this.refresh;
				^this
			});
			if (char.isDecDigit || "+-.eE".includes(char), {
				if (keyString.isNil, { 
					keyString = String.new;
					stringColor = typingColor;
					
					if([\label,\switch,\none,\hide].includes(textMode)) { 
						showLabel = false;
						showValue = true 
					};
				});
				keyString = keyString.add(char);
				string = keyString;
				this.refresh;
				^this
			});
		};
		
		^nil		// bubble if it's an invalid key
	}
	
	spec_ {arg argSpec;
		spec = argSpec.asSpec;
		this.value = spec.default ?? { spec.constrain(value) };
		this.refresh;
		this.changed(\spec);
	}
	
	round_{arg argRound; round = argRound; }

	input_ { arg in; ^this.value_(spec.map(in)) }
	input { ^spec.unmap(value) }

	value_ { arg val;
		keyString = nil;
		stringColor = normalColor;
		value = spec.constrain(val);
//		string = value.asString;
		string = value.round(round).asString;
		this.refresh;
		this.changed(\synch, this);		
	}

	valueAction_ { arg val;
		var prev;
		prev = value;
		this.value = val !? { spec.constrain(val) };
		if (value != prev) { this.doAction };
		
		this.refresh;
	}
	
	label_ { arg l; label = l.asString }

	skin_ { arg newskin;
		if ( newskin.notNil ) {
			skin = newskin;
			newskin.proto_( GUI.skins.default.minbox.default );
			this.oldMethodsCompat;
			this.refresh;
		}{
			format("%: skin not found.", this.class).inform;
		};
	}
	
	oldMethodsCompat {
		loColor = skin.lo;
		hiColor = skin.hi;
		typingColor = skin.type;
		normalColor = skin.text;
		lineColor = skin.line;
		font = skin.font;
		defaultMode = skin.defaultMode;
		stringColor = normalColor;
	}

	*paletteExample{arg parent, bounds;
		^this.new(parent, bounds.asRect.height@bounds.asRect.height);	
	}
	
}

JSCMinBox : JSCUserView {
	classvar <>defaultMode;
	var center, or, ir, radius, hit, typing=false, string, stringColor;
	var showLabel, showValue, labelr,valuer;
	var <value, prevValue, <mode, <textMode;
	var <>loColor, <>hiColor, <>normalColor, <>typingColor, <>lineColor, <>font, <skin;
	var <>step, <round, <>align, <spec;
	var keyString, <>editable;
	var <label;

	*viewClass { ^JSCUserView }
	
	*initClass {
		defaultMode='vert';

	}

	init { arg argParent, argBounds;
	

		argBounds = this.calcConsts(argBounds.asRect);
		
		super.init(argParent, argBounds);
		value = 0;
		round = 0.01;
		this.spec = \unipolar; // also sets value
		
		editable = true;

		string = value.round(round).asString;
		step = 0.001;
		mode = defaultMode;
		
		// NOTE not efficient, calls calcConstants again
		this.textMode_(\value);

		align = \center;
		label = "";
		
		
		skin = GUI.skins.default.minbox.default;

		this.oldMethodsCompat(skin);

		this.receiveDragHandler = { this.valueAction_(JSCView.currentDrag); };
		this.beginDragAction = { value.asFloat; };
		this.canReceiveDragHandler = { JSCView.currentDrag.isNumber };
		
		^this;
	}

	calcConsts { arg rect;
		or = ((0.5@0.5)@(rect.extent-(0.5@0.5)));
		ir = Rect.fromRect(or);
		
		if([\both,\show].includes(textMode)) {
			labelr = or.insetBy(2).height_((or.height*0.5).max(16));
			valuer = Rect.fromRect(labelr).bottom_(or.bottom-2);
		} {
			labelr = or.insetBy(2);
			valuer = labelr;
		};

		radius = (or.width + or.height)*0.5;
		center = or.center;
				
		^rect
	}

	bounds_ { arg rect;
		rect = this.calcConsts(rect);
		super.bounds_(rect);
	}

	mode_ { arg argMode;
		if(argMode==\default) 
		{ mode = defaultMode }
		{ mode = argMode };
		this.calcConsts(this.bounds);
	}

	textMode_ { arg argTextMode=\value;
		textMode = argTextMode;
		showLabel = [\label,\both,\show,\switch].includes(textMode);
		showValue = [\value,\both,\show,\default].includes(textMode);

		this.calcConsts(this.bounds);
	}

	draw {
		var in = this.input;
		
		JPen.width =1;
		
		
		
		switch(mode)
			{ \round } {
				JPen.fillColor = lineColor;
				JPen.fillRect(or);				// make background
				JPen.strokeColor = lineColor;

				JPen.fillColor = loColor;		// draw lo
				JPen.addAnnularWedge(or.center, 0, radius, -1.25pi, 1.5pi);
				JPen.fill;
				JPen.addAnnularWedge(or.center, 0, radius, -1.25pi, 1.5pi);
				JPen.stroke;
				
				JPen.fillColor = hiColor;		// draw hi
				JPen.addAnnularWedge(or.center, 0, radius, -1.25pi, in*1.5pi);
				JPen.fill;
				JPen.addAnnularWedge(or.center, 0, radius, -1.25pi, in*1.5pi);
				JPen.stroke;
			}
			{\vert } {
				ir.top_(or.height*(1-in));
				JPen.color = loColor;
				JPen.fillRect(or);
				JPen.color = hiColor;
				JPen.fillRect(ir);
			
				JPen.strokeColor_(lineColor);
				JPen.addRect(or);
				JPen.line((ir.leftTop),(ir.rightTop));
				JPen.stroke;
			}
			{\horiz } {
				ir.right_(or.width*in);
				JPen.color = loColor;
				JPen.fillRect(or);
				JPen.color = hiColor;
				JPen.fillRect(ir);
			
				JPen.strokeColor_(lineColor);
				JPen.addRect(or);
				JPen.line((ir.rightTop),(ir.rightBottom));
				JPen.stroke;	
			}
			{\blend} {
				JPen.fillColor = loColor.blend(hiColor, in);
				JPen.strokeColor_(lineColor);
				JPen.fillRect(or);
				JPen.strokeRect(or);				
			}
			{\clear} {
				JPen.fillColor = lineColor;
				JPen.fillRect(or);
			};

/* NOTE commented out for 3.2 compatibility - the shadow looks nicer */
//		JPen.setShadow(3@3, 1, Color.gray(0,0.75));
		JPen.color = stringColor;
		JPen.font = font;

		if(showLabel) {
			if(align==\full) { JPen.stringLeftJustIn(label, labelr) } // if full, draw left
			{ JPen.stringCenteredIn(label, labelr) }; // else centered
		};

		if(showValue) {
			switch(align)
			{ \left } { JPen.stringLeftJustIn(string, valuer) }
			{ \right } { JPen.stringRightJustIn(string, valuer) }
			{ \center } { JPen.stringCenteredIn(string, valuer) }
			{ \full } { JPen.stringRightJustIn(string, valuer) }
			{ JPen.stringCenteredIn(string, valuer) };
		};
		
	}

	mouseDown { arg x, y, modifiers, buttonNumber, clickCount;
		
		hit = x @ y;
		
		mouseDownAction.value(this, x, y, modifiers, buttonNumber, clickCount);
		
		this.mouseMove(x, y, modifiers);
		
		if(textMode==\switch) {
			showLabel = false;
			showValue = true;
		};

	}
		
	mouseUp { arg x, y, modifiers, buttonNumber, clickCount;
		if(textMode==\switch) {
			showLabel = true;
			showValue = false;
		};
		
		this.refresh;
		mouseUpAction.value(this, x, y, modifiers, buttonNumber, clickCount);
	}
	
	mouseMove { arg x, y, modifiers;
		var mp, pt, angle, inc = 0;
		var in = this.input;
		
		if(this.editable) {
			inc = case
				{ (modifiers & 262144 == 262144) } { 10 }
				{ (modifiers & 524288 == 524288) } { 0.1 }
				{ 1 };
							
			if (modifiers & 1048576 != 1048576) { // we are not dragging out - apple key
				case
					{ [\vert, \blend, \clear].includes(mode) } { 
						
						inc = inc*step*(hit.y - y);
						this.valueAction = spec.map(in + inc);
						hit = Point(x,y);
					}
					{ (mode == \horiz) } { 
						inc = inc*step.neg*(hit.x - x);
						this.valueAction = spec.map(in + inc).round(round);
						hit = Point(x,y);
					}
					{ mode == \round } {
						pt = center - Point(x,y);
						angle = Point(pt.y, pt.x.neg).theta;
						if ((angle >= -0.80pi) and: { angle <= 0.80pi} , {
							this.valueAction = spec.map([-0.75pi, 0.75pi].asSpec
								.unmap(angle)).round(round);
						});
	
					}
			};
		};

		mouseMoveAction.value(this, x, y, modifiers);	
	}	

	increment { this.valueAction = spec.map(this.input + step); }
	decrement { this.valueAction = spec.map(this.input - step); }
	
	defaultKeyDownAction { arg char, modifiers, unicode;
	
		if(this.editable) {
			
			// standard chardown
			if (unicode == 16rF700, { this.increment; ^this });
			if (unicode == 16rF703, { this.increment; ^this });
			if (unicode == 16rF701, { this.decrement; ^this });
			if (unicode == 16rF702, { this.decrement; ^this });
			if ((char == 3.asAscii) || (char == $\r) || (char == $\n), { // enter key
				if (keyString.notNil,{ // no error on repeated enter
				
					this.textMode_(textMode);

					this.valueAction_(keyString.asFloat);
				});
				^this
			});
			if (char == 127.asAscii, { // delete key
				keyString = nil;
				string = (value.round(round)).asString;
				stringColor = normalColor;

				this.textMode_(textMode);

				this.refresh;
				^this
			});
			if (char.isDecDigit || "+-.eE".includes(char), {
				if (keyString.isNil, { 
					keyString = String.new;
					stringColor = typingColor;
					
					if([\label,\switch,\none,\hide].includes(textMode)) { 
						showLabel = false;
						showValue = true 
					};
				});
				keyString = keyString.add(char);
				string = keyString;
				this.refresh;
				^this
			});
		};
		
		^nil		// bubble if it's an invalid key
	}
	
	spec_ {arg argSpec;
		spec = argSpec.asSpec;
		this.value = spec.default ?? { spec.constrain(value) };
		this.refresh;
		this.changed(\spec);
	}
	
	round_{arg argRound; round = argRound; }

	input_ { arg in; ^this.value_(spec.map(in)) }
	input { ^spec.unmap(value) }

	value_ { arg val;
		keyString = nil;
		stringColor = normalColor;
		value = spec.constrain(val);
//		string = value.asString;
		string = value.round(round).asString;
		this.refresh;
		this.changed(\synch, this);		
	}

	valueAction_ { arg val;
		var prev;
		prev = value;
		this.value = val !? { spec.constrain(val) };
		if (value != prev) { this.doAction };
		
		this.refresh;
	}
	
	label_ { arg l; label = l.asString }

	skin_ { arg newskin;
		if ( newskin.notNil ) {
			skin = newskin;
			newskin.proto_( GUI.skins.default.minbox.default );
			this.oldMethodsCompat;
			this.refresh;
		}{
			format("%: skin not found.", this.class).inform;
		};
	}
	
	oldMethodsCompat {
		loColor = skin.lo;
		hiColor = skin.hi;
		typingColor = skin.type;
		normalColor = skin.text;
		lineColor = skin.line;
		font = skin.font;
		defaultMode = skin.defaultMode;
		stringColor = normalColor;
	}

	*paletteExample{arg parent, bounds;
		^this.new(parent, bounds.asRect.height@bounds.asRect.height);	
	}
	
}