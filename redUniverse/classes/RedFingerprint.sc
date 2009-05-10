// this file is part of redUniverse /redFrik


RedFingerprint {
	var <>array, <>normalize, <>points;
	*new {|array, normalize= true|
		^super.newCopyArgs(array, normalize).calc;
	}
	calc {
		var p= Point(0, 0);
		points= array.copyRange(0, array.size.div(2)*2-1).clump(2).collect{|pair|
			var distance, angle;
			#distance, angle= pair;
			p= p+Point(distance, distance).rotate(angle*2pi);
		};
		if(normalize, {
			points= points.collect{|x| x.asArray}.flat.normalize(-1, 1);
			points= points.clump(2).collect{|x| x.asPoint};
		});
	}
	draw {|length= 1|
		GUI.pen.moveTo(points[0]*length);
		points.do{|x| GUI.pen.lineTo(x*length)};
		GUI.pen.stroke;
	}
	
	makeWindow {|name, bounds, scale= 1, background, color, width= 1|
		var win, w, h, centerX, centerY;
		name= name ?? {"fingerprint"+points.size+"points"};
		bounds= bounds ?? {Rect(128, 64, 300, 300)};
		w= bounds.width;
		h= bounds.height;
		centerX= w/2;
		centerY= h/2;
		scale= scale*centerX.min(centerY);
		win= GUI.window.new(name, bounds, false);
		win.view.background= background ?? {Color(0.2, 0.1843, 0.2235)};
		color= color ?? {Color.white};
		win.drawHook= {
			GUI.pen.width_(width);
			GUI.pen.translate(centerX, centerY);
			GUI.pen.strokeColor_(color);
			this.draw(scale);
		};
		^win.front;
	}
	gui {|name, bounds, scale= 1, background, color, width= 1|
		this.deprecated(thisMethod, RedFingerprint.findMethod(\makeWindow));
		^this.makeWindow(name, bounds, scale, background, color, width);
	}
}
