// this file is part of redUniverse toolkit /redFrik

//this class can also be used with osx-swing.  just copy it to the osx folder

RedJWindow : JSCWindow {
	var <mouse, <isPlaying= false, <userView;
	*new {|name= "redWindow", bounds, resizable= false, border= true, server, scroll= false|
		^super.new.initSCWindow(name, bounds, resizable, border, scroll, server)
	}
	*initClass {
		StartUp.add{
			GUI.get(\swing).put(\redWindow, RedJWindow);
		};
		UI.registerForShutdown({ this.closeAll });
	}
	initSCWindow {|argName, argBounds, argResizable, argBorder, scroll, argServer|
		name			= argName.asString;
		border		= argBorder;
		resizable		= argResizable;
		argBounds		= argBounds ?? { Rect.new( 128, 64, 300, 300 )};
		server		= argServer ?? { SwingOSC.default; };
		allWindows	= allWindows.add( this );
		id			= server.nextNodeID;
		dataptr		= this.id;
		this.prInit( name, argBounds, resizable, border, scroll ); // , view );
		
		this.background_(Color.black);
		mouse= RedVector2D[0, 0];
		userView= JSCUserView(this, Rect(0, 0, view.bounds.width, view.bounds.height))
			.mouseDownAction_{|view, x, y| mouse= RedVector2D[x, y]}
			.mouseMoveAction_{|view, x, y| mouse= RedVector2D[x, y]}
	}
	draw {|func| userView.drawFunc= func}
	play {|fps= 40|
		isPlaying= true;
		{while{this.isOpen&&isPlaying} {userView.refresh; fps.reciprocal.wait}}.fork(AppClock);
	}
	stop {isPlaying= false}
	resize {|redVec| this.setInnerExtent(redVec[0], redVec[1])}
	background_ {|color| view.background_(color)}
	isOpen {^this.isClosed.not}
}
