// this file is part of redUniverse toolkit /redFrik


/*RedJWindow : JSCWindow {
	var <mouse, <isPlaying= false;
	*new {|name= "redWindow", bounds, resizable= false, border= true, server|
		^super.new.initSCWindow(name, bounds, resizable, border, server)
	}
	*initClass {
		StartUp.add{
			GUI.get(\swing).put(\redWindow, RedJWindow);
		};
		UI.registerForShutdown({ this.closeAll });
	}
	initSCWindow {|argName, argBounds, resizable, border, argServer|
		name			= argName.asString;
		argBounds		= argBounds ?? { Rect.new( 128, 64, 300, 300 )};
		server		= argServer ?? { SwingOSC.default; };
		allWindows	= allWindows.add( this );
		id			= server.nextNodeID;
		dataptr		= this.id;
		this.prInit( name, argBounds, resizable, border ); // , view );
		
		this.background_(Color.black);
		this.acceptsMouseOver= true;
		mouse= RedVector2D[view.bounds.width/2, view.bounds.height/2];
		JSCUserView(this, view.bounds).mouseOverAction_({|v, x, y| mouse= RedVector2D[x, y]});
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
*/