// this file is part of redUniverse /redFrik


RedMRCM {
	var <>drawFunc, <>matrices, <>n,
		matrices2;
	*new {|drawFunc, matrices, n= 1|
		^super.newCopyArgs(drawFunc, matrices, n);
	}
	draw {|width= 400, height= 400|
		matrices2= matrices.collect{|x| x*[1, 1, 1, 1, width, height]};
		this.prDraw(0);
	}
	makeWindow {|bounds|
		var b= bounds ?? {Rect(100, 200, 400, 400)};
		var win= GUI.window.new(this.class.name, b, false);
		win.drawHook_{
			Pen.translate(b.width*0.5, b.height*0.5);
			this.draw(b.width, b.height);
			Pen.stroke;
		};
		win.front;
		^win;
	}
	
	//--private
	prDraw {|index|
		if(index==n, {
			drawFunc.value;
		}, {
			matrices2.do{|x|
				Pen.use{
					Pen.matrix= x;
					this.prDraw(index+1);
				};
			};
		});
	}
}