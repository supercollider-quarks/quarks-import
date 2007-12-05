//simple plotting of functions

(
	var width= 100, height= 100, w,
		x, y, dx, dy, widthCartPlane, heightCartPlane, rad, theta, func, cnt= 0;
	
	w= GUI.redWindow.new("func plot", Rect(128, 64, width, height)).front;
	
	widthCartPlane= 16;
	heightCartPlane= 16;
	
	//--loop
	w.draw{
		cnt= cnt+0.05%width;
		dx= widthCartPlane/width;
		dy= heightCartPlane/height;
		x= widthCartPlane.neg/2;
		width.do{|i|
			y= heightCartPlane.neg/2;
			height.do{|j|
				rad= x.sumsqr(y).sqrt;
				theta= atan2(y, x);
				
				//func= cos(cnt+rad);
				//func= sin(theta);
				//func= sin(rad);
				//func= cos(theta);
				func= sin(cnt*rad.cos+(5*theta));
				y= y+dy;
				
				//--render
				GUI.pen.strokeColor_(Color.grey(func+1));
				GUI.pen.strokeRect(Rect(i, j, 1, 1));
			};
			x= x+dx;
		}
	}.play;
)