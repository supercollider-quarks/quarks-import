//simple plotting of a lsystem
//req. the classes in redLSystem from fredrikolofsson.com

(
	var width= 300, height= 300, w,
		cnt= 0, lsys;
	
	//--world
	w= GUI.redWindow.new("lsystem test", Rect(128, 64, width, height)).front;
	
	//lsys= RedLSystem("F", "-[-F+F+F]+[+F-F-F]");
	//lsys= RedLSystem("F", "F[+F--F]+F");
	lsys= RedLSystem("F", "F+F--F+F");
	
	//--loop
	w.draw{
		cnt= cnt+1;
		RedLSystemPlot.drawLength= 10;
		RedLSystemPlot.theta= cnt%100/100*3-1.5;
		lsys.reset;
		lsys.next;
		lsys.next;
		lsys.next;
		lsys.next;
		
		//--render
		GUI.pen.strokeColor_(Color.white);
		GUI.pen.translate(width/2, height);
		RedLSystemPlot.draw(lsys);
	}.play;
)