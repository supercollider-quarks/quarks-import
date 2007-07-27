// (c) 2006, Thor Magnusson - www.ixi-software.net
// GNU licence - google it.

// (coverted to GUI.sc usage by sciss dec-2006)

ParaSpace {

	var <>paraNodes, connections; 
	var chosennode, mouseTracker;
	var win, bounds;
	var downAction, upAction, trackAction, keyDownAction, rightDownAction, overAction, connAction;
	var backgrDrawFunc;
	var background, fillcolor;
	var nodeCount, shape;
	var startSelPoint, endSelPoint, refPoint;
	var selNodes, outlinecolor, selectFillColor, selectStrokeColor;
	var keytracker, conFlag;
	var nodeSize, swapNode;
	var font, fontColor;
	
	var refresh 			= true;	// false during 'reconstruct'
	var refreshDeferred	= false;
	var lazyRefreshFunc;
	
	*new { arg w, bounds; 
		^super.new.initParaSpace(w, bounds);
	}
	
	initParaSpace { arg w, argbounds;
		var a, b, rect, relX, relY, pen;

		lazyRefreshFunc = { this.refresh; refreshDeferred = false; };

		bounds = argbounds ? Rect(20, 20, 400, 200);
		bounds = Rect(bounds.left + 0.5, bounds.top + 0.5, bounds.width, bounds.height);

		pen	= GUI.pen;
		
		if((win= w).isNil, {
			win = GUI.window.new("ParaSpace",
				Rect(10, 250, bounds.left + bounds.width + 40, bounds.top + bounds.height+30));
			win.front
		});

		win.acceptsMouseOver = false;

		background = Color.white;
		fillcolor = Color.new255(103, 148, 103);
		outlinecolor = Color.red;
		selectFillColor = Color.green(alpha:0.2);
		selectStrokeColor = Color.black;
		paraNodes = List.new;
		connections = List.new;
		nodeCount = 0;
		startSelPoint = 0@0;
		endSelPoint = 0@0;
		refPoint = 0@0;
		shape = "rect";
		conFlag = false;
		nodeSize = 8;
		font = GUI.font.new("Arial", 9);
		fontColor = Color.black;
		
		
		keytracker = GUI.userView.new(win, if(GUI.current.id == \swing, 
							{Rect(-10, -10, 10, 10)}, {Rect(-10, -10, 2000, 2000)}))
			.canFocus_(true)
			.keyDownAction_({ |me, key, modifiers, unicode |
				if(unicode == 127, {
					selNodes.do({arg box; 
						paraNodes.copy.do({arg node, i; 
							if(box === node, {this.deleteNode(i)});
						})
					});
				});
				if(unicode == 99, {conFlag = true;}); // c is for connecting
				keyDownAction.value(key, modifiers, unicode);
				this.refresh;
			})
			.keyUpAction_({ |me, key, modifiers, unicode |
				if(unicode == 99, {conFlag = false;}); // c is for connecting
			});
			
		mouseTracker = GUI.userView.new(win, Rect(bounds.left, bounds.top, bounds.width + 1, bounds.height + 1))
			.canFocus_(false)
			.mouseDownAction_({|me, x, y, mod|
				chosennode = this.findNode(x, y);
				if( (mod & 0x00040000) != 0, {	// == 262401
					paraNodes.add(ParaNode.new(x,y, fillcolor, bounds, nodeCount, nodeSize));
					nodeCount = nodeCount + 1;
					paraNodes.do({arg node;
					 		node.outlinecolor = Color.black; 
							node.refloc = node.nodeloc;
					});
					startSelPoint = x-10@y-10;
					endSelPoint =   x-10@y-10;
				}, {
					if(chosennode !=nil, { // a node is selected
						relX = chosennode.nodeloc.x - bounds.left - 0.5;
						relY = chosennode.nodeloc.y - bounds.top - 0.5;
						refPoint = x@y; // var used here for reference in trackfunc
						
						if(conFlag == true, { // if selected and "c" then connection is possible
							paraNodes.do({arg node, i; 
								if(node === chosennode, {a = i;});
							});
							selNodes.do({arg selnode, j; 
								paraNodes.do({arg node, i; 
									if(node === selnode, {b = i;
										this.createConnection(a, b);
									});
								});
							});
						});
						downAction.value(chosennode);
					}, { // no node is selected
					 	paraNodes.do({arg node; // deselect all nodes
					 		node.outlinecolor = Color.black; 
							node.refloc = node.nodeloc;
					 	});
						startSelPoint = x@y;
						endSelPoint = x@y;
						this.lazyRefresh;
					});
				});
			})
			.mouseMoveAction_({|me, x, y, mod|
				if(chosennode != nil, { // a node is selected
					relX = chosennode.nodeloc.x - bounds.left - 0.5;
					relY = chosennode.nodeloc.y - bounds.top - 0.5;
					chosennode.setLoc_(Point(x,y));
					block {|break|
						selNodes.do({arg node; 
							if(node === chosennode,{ // if the mousedown box is one of selected
								break.value( // then move the whole thing ...
									selNodes.do({arg node; // move selected boxes
										node.setLoc_(Point(
											node.refloc.x + (x - refPoint.x),
											node.refloc.y + (y - refPoint.y)
										));
									});
								);
							}); 
						});
					};
					trackAction.value(chosennode);
					this.lazyRefresh;
				}, {
					endSelPoint = x@y;
					this.lazyRefresh;
				});
			})
			.mouseOverAction_({arg me, x, y;
				chosennode = this.findNode(x, y);
				if(chosennode != nil, {  
					relX = chosennode.nodeloc.x - bounds.left - 0.5;
					relY = chosennode.nodeloc.y - bounds.top - 0.5;
					overAction.value(chosennode);
				});
			})
			.mouseUpAction_({|me, x, y, mod|
				if(chosennode !=nil, { // a node is selected
					relX = chosennode.nodeloc.x - bounds.left - 0.5;
					relY = chosennode.nodeloc.y - bounds.top - 0.5;
					upAction.value(chosennode);
					paraNodes.do({arg node; 
						node.refloc = node.nodeloc;
					});
					this.lazyRefresh;
				},{ 
					selNodes = List.new;
					paraNodes.do({arg node;
						if(Rect(	startSelPoint.x, // + rect
								startSelPoint.y,									endSelPoint.x - startSelPoint.x,
								endSelPoint.y - startSelPoint.y)
								.containsPoint(node.nodeloc), {
									node.outlinecolor = outlinecolor;
									selNodes.add(node);
						});
						if(Rect(	endSelPoint.x, // - rect
								endSelPoint.y,									startSelPoint.x - endSelPoint.x,
								startSelPoint.y - endSelPoint.y)
								.containsPoint(node.nodeloc), {
									node.outlinecolor = outlinecolor;
									selNodes.add(node);
						});
						if(Rect(	startSelPoint.x, // + X and - Y rect
								endSelPoint.y,									endSelPoint.x - startSelPoint.x,
								startSelPoint.y - endSelPoint.y)
								.containsPoint(node.nodeloc), {
									node.outlinecolor = outlinecolor;
									selNodes.add(node);
						});
						if(Rect(	endSelPoint.x, // - Y and + X rect
								startSelPoint.y,									startSelPoint.x - endSelPoint.x,
								endSelPoint.y - startSelPoint.y)
								.containsPoint(node.nodeloc), {
									node.outlinecolor = outlinecolor;
									selNodes.add(node);
						});
					});
					startSelPoint = 0@0;
					endSelPoint = 0@0;
					this.lazyRefresh;
				});
			})
			.drawFunc_({		
					pen.font = font;
					pen.width = 1;
//				background.set; // background color
				pen.color = background;
				pen.fillRect(bounds); // background fill
				backgrDrawFunc.value; // background draw function
//				Color.black.set;
				pen.strokeColor = Color.black;
				connections.do({arg conn;
					pen.line(paraNodes[conn[0]].nodeloc+0.5, paraNodes[conn[1]].nodeloc+0.5);
				});
				pen.stroke;
				// the nodes or circles
				paraNodes.do({arg node;
					if(shape == "rect", {
//						node.color.set;
						pen.fillColor = node.color;
						pen.fillRect(node.rect);
//						node.outlinecolor.set;
						pen.strokeColor = node.outlinecolor;
						pen.strokeRect(node.rect);
					}, {
//						node.color.set;
						pen.fillColor = node.color;
						pen.fillOval(node.rect);
//						node.outlinecolor.set;
						pen.strokeColor = node.outlinecolor;
						pen.strokeOval(node.rect);
					});
//				    	node.string.drawInRect(Rect(node.rect.left+node.size+5,
//				    								node.rect.top-3, 80, 16),   
//				    								font, fontColor);
				    	if( node.string.size > 0, {
					    	pen.fillColor = fontColor;
				    		pen.stringInRect( node.string, Rect(node.rect.left+node.size+5, node.rect.top-3, 80, 16));
				    	});
				});
// superfluous!
//				pen.stroke;		
//				selectFillColor.set;
				pen.fillColor = selectFillColor;
				// the selection node
				pen.fillRect(Rect(	startSelPoint.x + 0.5, 
									startSelPoint.y + 0.5,
									endSelPoint.x - startSelPoint.x,
									endSelPoint.y - startSelPoint.y
									));
//				selectStrokeColor.set;
				pen.strokeColor = selectStrokeColor;
				pen.strokeRect(Rect(	startSelPoint.x + 0.5, 
									startSelPoint.y + 0.5,
									endSelPoint.x - startSelPoint.x,
									endSelPoint.y - startSelPoint.y
									));
//				Color.black.set;
				pen.strokeColor = Color.black;
				pen.strokeRect(bounds); // background frame
			});
	keytracker.focus(true);
	}
	
	clearSpace { arg refresh = true;
		paraNodes = List.new;
		connections = List.new;
		if(refresh == true, {this.refresh});
		nodeCount = 0;
	}
		
	createConnection {arg node1, node2, refresh = true;
		if((nodeCount < node1) || (nodeCount < node2), {
			"Can't connect - there aren't that many nodes".postln;
		}, {
			block {|break|
				connections.do({arg conn; 
					if((conn == [node1, node2]) || (conn == [node2, node1]), {
						break.value;
					});	
				});
				connections.add([node1, node2]);
				connAction.value(paraNodes[node1], paraNodes[node2]);
				if(refresh == true, {this.refresh});
			}
		});
	}

	deleteConnection {arg node1, node2, refresh = true;
		connections.do({arg conn, i; if((conn == [node1, node2]) || (conn == [node2, node1]),
			 { connections.removeAt(i)})});
		if(refresh == true, {this.refresh});
	}

	deleteConnections { // delete all connections
		connections = List.new; // list of arrays with connections eg. [2,3]
		this.refresh;	
	}

	createNode {arg x, y, color, refresh = true;
		fillcolor = color ? fillcolor;
		paraNodes.add(ParaNode.new(bounds.left+x+0.5, bounds.top+y+0.5, fillcolor, bounds, nodeCount, nodeSize));
		nodeCount = nodeCount + 1;
		if(refresh == true, {this.refresh});
	}
	
	createNode1 {arg argX, argY, color, refresh = true;
		var x, y;
		x = (argX * bounds.width).round(1);
		y = (argY * bounds.height).round(1);
		fillcolor = color ? fillcolor;
		paraNodes.add(ParaNode.new(bounds.left+x+0.5, bounds.top+y+0.5, fillcolor, bounds, nodeCount, nodeSize));
		nodeCount = nodeCount + 1;
		if(refresh == true, {this.refresh});
	}
	
	deleteNode {arg nodenr; var del;
		del = 0;
		connections.copy.do({arg conn, i; 
			if(conn.includes(nodenr), { connections.removeAt((i-del)); del=del+1;})
		});
		connections.do({arg conn, i; 
			if(conn[0]>nodenr,{conn[0]=conn[0]-1});if(conn[1]>nodenr,{conn[1]= conn[1]-1});
		});
		if(paraNodes.size > 0, {paraNodes.removeAt(nodenr)});
		this.refresh;		
	}
	
	setNodeLoc_ {arg index, argX, argY, refresh = true;
		var x, y;
		x = argX+bounds.left + 0.5;
		y = argY+bounds.top + 0.5;
		paraNodes[index].setLoc_(Point(x, y));
		if(refresh == true, {this.refresh});
	}
	
	setNodeLocAction_ {arg index, argX, argY, action, refresh=true;
		var x, y;
		x = argX+bounds.left + 0.5;
		y = argY+bounds.top + 0.5;
		paraNodes[index].setLoc_(Point(x, y));
		switch (action)
			{\down} 	{downAction.value(paraNodes[index])}
			{\up} 	{upAction.value(paraNodes[index])}
			{\track} 	{trackAction.value(paraNodes[index])};
		if(refresh == true, {this.refresh});
	}


	getNodeLoc {arg index;
		var x, y;
		x = paraNodes[index].nodeloc.x - bounds.left;
		y = paraNodes[index].nodeloc.y - bounds.top;
		^[x-0.5, y-0.5];
	}

	setNodeLoc1_ {arg index, argX, argY, refresh = true;
		var x, y;
		x = (argX * bounds.width).round(1);
		y = (argY * bounds.height).round(1);
		paraNodes[index].setLoc_(Point(x+bounds.left+0.5, y+bounds.top+0.5));
		if(refresh == true, {this.refresh});
	}

	setNodeLoc1Action_ {arg index, argX, argY, action, refresh=true;
		var x, y;
		x = (argX * bounds.width).round(1);
		y = (argY * bounds.height).round(1);
		paraNodes[index].setLoc_(Point(x+bounds.left+0.5, y+bounds.top+0.5));
		switch (action)
			{\down} 	{downAction.value(paraNodes[index])}
			{\up} 	{upAction.value(paraNodes[index])}
			{\track} 	{trackAction.value(paraNodes[index])};
		if(refresh == true, {this.refresh});
	}

	getNodeLoc1 {arg index;
		var x, y;
		x = (paraNodes[index].nodeloc.x - bounds.left) / bounds.width;
		y = (paraNodes[index].nodeloc.y - bounds.top) / bounds.height;
		^[x, y];
	}
	
	getNodeStates {
		var locs, color, size;
		locs = List.new; color = List.new; size = List.new;
		paraNodes.do({arg node; 
			locs.add(node.nodeloc);
			color.add(node.color); 
			size.add(node.size);
		});
		^[locs, connections, color, size];
	}

	setNodeStates_ {arg array, refresh = true; // array with [locs, connections, color, size]
		if(array[0].isNil == false, {
			paraNodes = List.new; 
			array[0].do({arg loc; 
				paraNodes.add(ParaNode.new(loc.x, loc.y, fillcolor, bounds, nodeCount));
				nodeCount = nodeCount + 1;
				})
		});
		if(array[1].isNil == false, { connections = array[1];});
		if(array[2].isNil == false, { paraNodes.do({arg node, i; node.setColor_(array[2][i];)})});
		if(array[3].isNil == false, { paraNodes.do({arg node, i; node.setSize_(array[3][i];)})});
		if(refresh == true, {this.refresh});
	}

	setBackgrColor_ {arg color;
		background = color;
		this.refresh;
	}
		
	setFillColor_ {arg color;
		fillcolor = color;
		paraNodes.do({arg node; 
			node.setColor_(color);
		});
		this.refresh;
	}
	
	setOutlineColor_ {arg color;
		outlinecolor = color;
		this.refresh;
	}
	
	setSelectFillColor_ {arg color;
		selectFillColor = color;
	}

	setSelectStrokeColor_ {arg color;
		selectStrokeColor = color;
	}
	
	setShape_ {arg argshape;
		shape = argshape;
		this.refresh;	
	}
	
	reconstruct { arg aFunc;
		refresh = false;
		aFunc.value( this );
		refresh = true;
		this.refresh;
	}

	refresh {
		if( refresh, { {mouseTracker.refresh}.defer; });
	}

	lazyRefresh {
		if( refreshDeferred.not, {
			AppClock.sched( 0.02, lazyRefreshFunc );
			refreshDeferred = true;
		});
	}
	
	setNodeSize_ {arg index, size, refresh = true;
		if(size == nil, {
			nodeSize = index;
			paraNodes.do({arg node; node.setSize_(nodeSize)});
			//"nodesize is :".post; nodeSize.postln;
		}, {
			paraNodes[index].setSize_(size);
		});
		if(refresh == true, {this.refresh});
	}

	getNodeSize {arg index;
		^paraNodes[index].size;
	}
	
	setNodeColor_ {arg index, color, refresh = true;
		paraNodes[index].setColor_(color);
		if(refresh == true, {this.refresh});
	}
	
	getNodeColor {arg index;
		^paraNodes[index].getColor;	
	}
	
	setFont_ {arg f;
		font = f;
	}
	
	setFontColor_ {arg fc;
		fontColor = fc;
	}
	
	setNodeString_ {arg index, string;
		paraNodes[index].string = string;
		this.refresh;		
	}
	
	getNodeString {arg index;
		^paraNodes[index].string;
	}
	
	// PASSED FUNCTIONS OF MOUSE OR BACKGROUND
	nodeDownAction_ { arg func;
		downAction = func;
	}
	
	nodeUpAction_ { arg func;
		upAction = func;
	}
	
	nodeTrackAction_ { arg func;
		trackAction = func;
	}
	
	nodeOverAction_ { arg func;
		overAction = func;
		win.acceptsMouseOver = true;
	}
	
	connectAction_ {arg func;
		connAction = func;
	}
	
	setMouseOverState_ {arg state;
		win.acceptsMouseOver = state;
	}
	
	keyDownAction_ {arg func;
		keyDownAction = func;
	}
	
	setBackgrDrawFunc_ { arg func, refresh = true;
		backgrDrawFunc = func;
		if(refresh == true, {this.refresh});
	}
	
	// local function
	findNode {arg x, y;
		paraNodes.do({arg node; 
			if(node.rect.containsPoint(Point.new(x,y)), {
				^node;
			});
		});
		^nil;
	}
}


ParaNode {
	var <>fillrect, <>state, <>size, <rect, <>nodeloc, <>refloc, <>color, <>outlinecolor;
	var <>spritenum, <>temp;
	var bounds;
	var <>string;
	
	*new { arg x, y, color, bounds, spnum, size; 
		^super.new.initGridNode(x, y, color, bounds, spnum, size);
	}
	
	initGridNode {arg argX, argY, argcolor, argbounds, spnum, argsize;
		spritenum = spnum;
		nodeloc =  Point(argX, argY);	
		refloc = nodeloc;
		color = argcolor;	
		outlinecolor = Color.black;
		size = argsize ? 8;
		bounds = argbounds;
		rect = Rect((argX-(size/2))+0.5, (argY-(size/2))+0.5, size, size);
		string = "";
		temp = nil;
	}
		
	setLoc_ {arg point;
		nodeloc = point;
		// keep paranode inside the bounds
		if((point.x) > (bounds.left+bounds.width), 
			{nodeloc.x = bounds.left+bounds.width - 0.5});
		if((point.x) < (bounds.left), 
			{nodeloc.x = bounds.left + 0.5});
		if((point.y) > (bounds.top+bounds.height), 
			{nodeloc.y = bounds.top+bounds.height -0.5});
		if((point.y) < (bounds.top), 
			{nodeloc.y = bounds.top + 0.5});
		rect = Rect((nodeloc.x-(size/2))+0.5, (nodeloc.y-(size/2))+0.5, size, size);
	}
		
	setState_ {arg argstate;
		state = argstate;
	}
	
	getState {
		^state;
	}
	
	setSize_ {arg argsize;
		size = argsize;
		rect = Rect((nodeloc.x-(size/2))+0.5, (nodeloc.y-(size/2))+0.5, size, size);
	}
	
	getSize {
		^size;
	}
	
	setColor_ {arg argcolor;
		color = argcolor;
	}
	
	getColor {
		^color;
	}
}


/*
ParaNode {
	var <>fillrect, <>state, <>size, <rect, <>nodeloc, <>refloc, <>color, <>outlinecolor;
	var <>spritenum;
	var bounds;
	var <>string;
	
	*new { arg x, y, color, bounds, spnum, size;
		^super.new.initGridNode(x, y, color, bounds, spnum, size);
	}
	
	initGridNode {arg argX, argY, argcolor, argbounds, spnum, argsize;
		spritenum = spnum;
		nodeloc =  Point(argX, argY);	
		refloc = nodeloc;
		color = argcolor;	
		outlinecolor = Color.black;
		size = argsize ? 8;
		bounds = argbounds;
		rect = Rect((argX-(size/2))+0.5, (argY-(size/2))+0.5, size, size);
		string = "";
	}
		
	setLoc_ {arg point;
		nodeloc = point;
		// keep paranode inside the bounds
		if((point.x) > (bounds.left+bounds.width), 
			{nodeloc.x = bounds.left+bounds.width - 0.5});
		if((point.x) < (bounds.left), 
			{nodeloc.x = bounds.left + 0.5});
		if((point.y) > (bounds.top+bounds.height), 
			{nodeloc.y = bounds.top+bounds.height -0.5});
		if((point.y) < (bounds.top), 
			{nodeloc.y = bounds.top + 0.5});
		rect = Rect((nodeloc.x-(size/2))+0.5, (nodeloc.y-(size/2))+0.5, size, size);
	}
		
	setState_ {arg argstate;
		state = argstate;
	}
	
	getState {
		^state;
	}
	
	setSize_ {arg argsize;
		size = argsize;
		rect = Rect((nodeloc.x-(size/2))+0.5, (nodeloc.y-(size/2))+0.5, size, size);
	}
	
	getSize {
		^size;
	}
	
	setColor_ {arg argcolor;
		color = argcolor;
	}
	
	getColor {
		^color;
	}
}

*/