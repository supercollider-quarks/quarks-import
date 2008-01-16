// this file is part of redUniverse toolkit /redFrik

//uncomment this code and recompile if you're using swingosc (linux/windows)

/*
RedJWindow : JSCWindow {
	var <mouse, <isPlaying= false;
	*new {|name= "redWindow", bounds, resizable= false, border= true, server, scroll= false|
		^super.new.initSCWindow(name, bounds, resizable, border, scroll, server)
	}
	*initClass {
		StartUp.add{
			GUI.get(\swing).put(\redWindow, RedJWindow);
		};
		UI.registerForShutdown({ this.closeAll });
	}
	initSCWindow {|argName, argBounds, resizable, argBorder, scroll, argServer|
		name			= argName.asString;
		border		= argBorder;
		argBounds		= argBounds ?? { Rect.new( 128, 64, 300, 300 )};
		server		= argServer ?? { SwingOSC.default; };
		allWindows	= allWindows.add( this );
		id			= server.nextNodeID;
		dataptr		= this.id;
		this.prInit( name, argBounds, resizable, border, scroll ); // , view );
		
		this.background_(Color.black);
		this.acceptsMouseOver= true;
		mouse= RedVector2D[view.bounds.width/2, view.bounds.height/2];
		JSCUserView(this, view.bounds).mouseOverAction_({|v, x, y| mouse= RedVector2D[x, y]});
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
*/