// this file is part of redUniverse toolkit /redFrik

RedWindow : SCWindow {
	var <mouse, <isPlaying= false;
	*new {|name= "redWindow", bounds, resizable= false, border= true|
		^super.new.initSCWindow(name, bounds, resizable, border)
	}
	*initClass {
		UI.registerForShutdown({ this.closeAll });
	}
	initSCWindow {|argName, argBounds, resizable, border|
		name= argName.asString;
		argBounds= argBounds ?? {Rect(128, 64, 300, 300)};
		allWindows= allWindows.add(this);
		view= SCTopView(nil, argBounds.moveTo(0,0));
		this.prInit(name, argBounds, resizable, border, view);
		
		this.background_(Color.black);
		this.acceptsMouseOver= true;
		mouse= RedVector2D[view.bounds.width/2, view.bounds.height/2];
		SCUserView(this, view.bounds).mouseOverAction_({|v, x, y| mouse= RedVector2D[x, y]});
	}
	draw {|func| this.drawHook_(func)}
	play {|fps= 25|
		isPlaying= true;
		{while{this.isOpen&&isPlaying} {this.refresh; fps.reciprocal.wait}}.fork(AppClock);
	}
	stop {isPlaying= false}
	resize {|redVec| this.setInnerExtent(redVec[0], redVec[1])}
	background_ {|color| view.background_(color)}
	isOpen {^this.isClosed.not}
}
