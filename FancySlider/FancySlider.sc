// Jost Muxfeldt, 2012.


FancySlider : ViewRedirect { *key { ^\fancySlider }}


SCFancySlider : SCUserView {

	var <>step, <value=0, <>sliderColor,<>knobColor, 
		<orientation=\horizontal,<>frameColor,<>widgetFunction,
		<>thumbSize=7;	var <>shift_scale = 100.0, <>ctrl_scale = 10.0, <>alt_scale = 0.1;  
	
	*viewClass { 
			^SCUserView;
		} 
	
	init { |argParent, argBounds|
	
		super.init(argParent, argBounds);  
				
		sliderColor=Color.grey(0.6);
		knobColor=Color.red;
		frameColor=Color.grey;
		step=this.pixelStep;
		
		widgetFunction = {arg uview; this.drawwidget(uview)};
		if( argBounds.width<argBounds.height){orientation=\vertical};
		this.drawFunc = {arg uview; widgetFunction.value(uview)}; 

	}
	
	bounds_{arg rect;
		if( rect.width<rect.height){orientation=\vertical}{orientation=\horizontal};
		^super.bounds_(rect);
	}
	

		
		
	getScale { |modifiers|
		^case
			 { modifiers & 131072 == 131072 } { shift_scale }
			 { modifiers & 262144 == 262144 } { ctrl_scale }
			 { modifiers & 524288 == 524288 } { alt_scale }
			 { 1 };
	}

	drawwidget{|uview|
		var thumbwidth=thumbSize*2.sqrt.reciprocal;
		(orientation==\horizontal).if{
			// Draw the fill
			Pen.fillColor =sliderColor;
			Pen.addRect(Rect(0,0, uview.bounds.width*value,uview.bounds.height));
			Pen.fill;
			// Draw the triangle
			Pen.fillColor = knobColor;
			Pen.moveTo(((uview.bounds.width*value)-thumbwidth) @ uview.bounds.height);
			Pen.lineTo(((uview.bounds.width*value)+thumbwidth) @ uview.bounds.height);
			Pen.lineTo(((uview.bounds.width*value)) @ (uview.bounds.height-thumbSize));
			Pen.lineTo(((uview.bounds.width*value)-thumbwidth) @ uview.bounds.height);
			Pen.fill;
		}{	
			Pen.fillColor =sliderColor;
			Pen.addRect(Rect(0,uview.bounds.height, uview.bounds.width,uview.bounds.height*value.neg));
			Pen.fill;
			// Draw the triangle
			Pen.fillColor = knobColor;
			Pen.moveTo((uview.bounds.width
				@((uview.bounds.height*value.neg)+thumbwidth + uview.bounds.height))  );
			Pen.lineTo(((uview.bounds.width-thumbSize)
				@((uview.bounds.height*value.neg) + uview.bounds.height)) );
			Pen.lineTo((uview.bounds.width
				@((uview.bounds.height*value.neg)-thumbwidth + uview.bounds.height))  );
			Pen.lineTo((uview.bounds.width	
				@((uview.bounds.height*value.neg)+thumbwidth)+ uview.bounds.height)  );
			Pen.fill;		};
		// Draw the frame
		Pen.strokeColor = frameColor;
		Pen.addRect(Rect(0,0, uview.bounds.width,uview.bounds.height+1));
		Pen.stroke;
	
	}
		
		
	valueAction_{ arg val; 		this.value=val;
		this.doAction;
	}
	value_{ |val|  	  
		value=val;
		this.refresh;
	}

	
	
	mouseDown{ arg x, y, modifiers, buttonNumber, clickCount;
		var newVal;
		mouseDownAction.value(this, x, y, modifiers, buttonNumber, clickCount); 

		([256, 0].includes(modifiers)).if{ // restrict to no modifier
			if (orientation==\horizontal)
				{newVal= x.linlin(0,this.bounds.width,0,1); }
				{newVal= (this.bounds.height-y).linlin(0,this.bounds.height,0,1); };
			
			if (newVal != value) {this.valueAction_(newVal)}; 		};
	}
	
	mouseMove{ arg x, y, modifiers, buttonNumber, clickCount;
		var newVal;  
		mouseMoveAction.value(this, x, y, modifiers, buttonNumber, clickCount);
		
			if (orientation==\horizontal){
				newVal= x.linlin(0,this.bounds.width,0,1);}{
			    newVal= (this.bounds.height-y).linlin(0,this.bounds.height,0,1);};
			 
			
			if (newVal != value) {this.valueAction_(newVal)}; 
		
	}
	
	defaultKeyDownAction { arg char, modifiers, unicode, keycode;
		var zoom = this.getScale(modifiers);

		// standard keydown
			// rand could also use zoom factors
		if (char == $r, { this.valueAction = 1.0.rand; ^this });
		if (char == $n, { this.valueAction = 0.0; ^this });
		if (char == $x, { this.valueAction = 1.0; ^this });
		if (char == $c, { this.valueAction = 0.5; ^this });

		if (char == $], { this.increment(zoom); ^this });
		if (char == $[, { this.increment(zoom); ^this });
		if (unicode == 16rF700, { this.increment(zoom); ^this });
		if (unicode == 16rF703, { this.increment(zoom); ^this });
		if (unicode == 16rF701, { this.decrement(zoom); ^this });
		if (unicode == 16rF702, { this.decrement(zoom); ^this });

		^nil		// bubble if it's an invalid key
	}
	
	defaultGetDrag {^value} 
	
	defaultCanReceiveDrag  {^currentDrag.isNumber} 
	defaultReceiveDrag { this.valueAction = currentDrag;} 
	
	increment { |zoom=1| ^this.valueAction = this.value + (max(this.step, this.pixelStep) * zoom) }
	decrement { |zoom=1| ^this.valueAction = this.value - (max(this.step, this.pixelStep) * zoom) }

	pixelStep {  // like in SCSlider
		var bounds = this.bounds; 
		^(bounds.width-1).reciprocal;
	}
	
	*paletteExample { arg parent, bounds;
		^this.new(parent, bounds);
	}

}


QFancySlider : QUserView {

	var <>step, <value=0, <>sliderColor,<>knobColor, <orientation=\horizontal,
		<>frameColor, <>thumbSize=7;	var <>shift_scale = 100.0, <>widgetFunction,
		<>ctrl_scale = 10.0, <>alt_scale = 0.1;  
	
	  *new { arg parent, bounds;
	    var me = super.new(parent, bounds ?? {this.sizeHint} ).init;
	    me.canFocus = true;
	    ^me;
	  }
	
	init { |argParent, argBounds|
	
		argBounds=argBounds.asRect();		
		sliderColor=Color.grey(0.6);
		knobColor=Color.red;
		frameColor=Color.grey;
		step=this.pixelStep;
		
		widgetFunction = {arg uview; this.drawwidget(uview)};
		if( argBounds.width<argBounds.height){orientation=\vertical};
		this.drawFunc = {arg uview; widgetFunction.value(uview)}; 

	}
	bounds_{arg rect;
		if( rect.width<rect.height){orientation=\vertical}{orientation=\horizontal};
		^super.bounds_(rect);
	}

	getScale { |modifiers|
		^case
			 { modifiers & 131072 == 131072 } { shift_scale }
			 { modifiers & 262144 == 262144 } { ctrl_scale }
			 { modifiers & 524288 == 524288 } { alt_scale }
			 { 1 };
	}

	
	drawwidget{|uview|
		
		var thumbwidth=thumbSize*2.sqrt.reciprocal;
		(orientation==\horizontal).if{
			// Draw the fill
			Pen.fillColor =sliderColor;
			Pen.addRect(Rect(0,0, this.bounds.width*value,this.bounds.height));
			Pen.fill;
			// Draw the triangle
			Pen.fillColor = knobColor;
			Pen.moveTo(((this.bounds.width*value)-thumbwidth) @ this.bounds.height);
			Pen.lineTo(((this.bounds.width*value)+thumbwidth) @ this.bounds.height);
			Pen.lineTo(((this.bounds.width*value)) @ (this.bounds.height-thumbSize));
			Pen.lineTo(((this.bounds.width*value)-thumbwidth) @ this.bounds.height);
			Pen.fill;
		}{	
			Pen.fillColor =sliderColor;
			Pen.addRect(Rect(0,this.bounds.height, this.bounds.width,this.bounds.height*value.neg));
			Pen.fill;
			// Draw the triangle
			Pen.fillColor = knobColor;
			Pen.moveTo((this.bounds.width	
				@((this.bounds.height*value.neg)+thumbwidth + this.bounds.height))  );
			Pen.lineTo(((this.bounds.width-thumbSize)   
				@((this.bounds.height*value.neg) + this.bounds.height)) );
			Pen.lineTo((this.bounds.width	
				@((this.bounds.height*value.neg)-thumbwidth + this.bounds.height))  );
			Pen.lineTo((this.bounds.width	
				@((this.bounds.height*value.neg)+thumbwidth)+ this.bounds.height)  );
			Pen.fill;		};
		// Draw the frame
		Pen.strokeColor = frameColor;
		Pen.addRect(Rect(0,0, this.bounds.width,this.bounds.height+1));
		Pen.stroke;
	
	}
		
		
	valueAction_{ arg val; 		this.value=val;
		this.doAction;
	}
	value_{ |val|  	  
		value=val;
		this.refresh;
	}
	
	mouseDown{ arg x, y, modifiers, buttonNumber, clickCount;
		var newVal;
		mouseDownAction.value(this, x, y, modifiers, buttonNumber, clickCount); 

			if (orientation==\horizontal)
				{newVal= x.linlin(0,this.bounds.width,0,1); }
				{newVal= (this.bounds.height-y).linlin(0,this.bounds.height,0,1); };
			
			if (newVal != value) {this.valueAction_(newVal)};
	}
	
	mouseMove{ arg x, y, modifiers, buttonNumber, clickCount;
		var newVal;  
		mouseMoveAction.value(this, x, y, modifiers, buttonNumber, clickCount);
		
		([256, 0].includes(modifiers)).if{ 
			if (orientation==\horizontal){
				newVal= x.linlin(0,this.bounds.width,0,1);}{
			    newVal= (this.bounds.height-y).linlin(0,this.bounds.height,0,1);};
			 
			
			if (newVal != value) {this.valueAction_(newVal)}; 
		};
		
	}
	
	  defaultKeyDownAction {  arg char, modifiers, unicode, keycode, key;
	    var scale = this.getScale( modifiers );
	    switch( char,
	      $r, { this.valueAction = 1.0.rand },
	      $n, { this.valueAction = 0.0 },
	      $x, { this.valueAction = 1.0 },
	      $c, { this.valueAction = 0.5 },
	      {
	        switch( key,
	          16r5d, { this.increment(scale) },
	          16r1000013, { this.increment(scale) },
	          16r1000014, { this.increment(scale) },
	          16r5b, { this.decrement(scale) },
	          16r1000015, { this.decrement(scale) },
	          16r1000012, { this.decrement(scale) },
	          { ^this; } // if unhandled, let Qt process the event
	        );
	        this.doAction;
	      }
	    );
	    ^true; // accept the event and stop its processing
	  }
	
	defaultGetDrag {^value} 
	
	defaultCanReceiveDrag  {^currentDrag.isNumber} 
	defaultReceiveDrag { this.valueAction = currentDrag;} 
	
	increment { |zoom=1| ^this.valueAction = this.value + (max(this.step, this.pixelStep) * zoom) }
	decrement { |zoom=1| ^this.valueAction = this.value - (max(this.step, this.pixelStep) * zoom) }

	pixelStep {  // like in SCSlider
		var bounds = this.bounds; 
		^(bounds.width-1).reciprocal
	}

}