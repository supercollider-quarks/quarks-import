// this file is part of redUniverse toolkit /redFrik

RedWindow : SCWindow {
	var <mouse, <isPlaying= false, <userView;
	*new {|name= "redWindow", bounds, resizable= false, border= true, server, scroll= false|
		^super.new.initSCWindow(name, bounds, resizable, border, scroll)
	}
	initSCWindow {|argName, argBounds, resizable, border, scroll|
		name= argName.asString;
		argBounds= argBounds ?? {Rect(128, 64, 300, 300)};
		allWindows= allWindows.add(this);
		if(scroll, {"RedWindow: can't scroll".warn});
		view= SCTopView(nil, argBounds.moveTo(0, 0));
		this.prInit(name, argBounds, resizable, border, false, view, false);
		
		this.background_(Color.black);
		mouse= RedVector2D[view.bounds.width/2, view.bounds.height/2];
		userView= SCUserView(this, view.bounds)
			.mouseMoveAction_({|v, x, y| mouse= RedVector2D[x, y]});
	}
	draw {|func| this.drawHook_(func)}
	play {|fps= 40|
		isPlaying= true;
		{while{this.isOpen&&isPlaying} {this.refresh; fps.reciprocal.wait}}.fork(AppClock);
	}
	stop {isPlaying= false}
	resize {|redVec| this.setInnerExtent(redVec[0], redVec[1])}
	background_ {|color| view.background_(color)}
	isOpen {^this.isClosed.not}
}
