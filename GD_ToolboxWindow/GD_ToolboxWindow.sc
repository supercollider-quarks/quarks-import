/*	
	GD_ToolboxWindow()
	
	select view(s) and apple-drag between windows to copy
	select view(s) and apple-drag inside a window, or press c, to copy
	press delete to delete
		

*/
GD_PanelWindow
{
	var window,userview,>parent,isSelected=false;
	
	var selection,<selectedViews,views;
	
	var <gridStep = 10,<gridOn = false,dragging,indent,multipleDragBy;
	
	var resizeHandles,resizeFixed, dropX, dropY;
	
	
	*new { |bounds| ^super.new.init(bounds) }
	
	
	init { |bounds|
		window = JSCWindow("Panel",bounds).front;
		views = Array.new;			
		this.makeUserview;
	}
	
	gridOn_ { |bool|
		gridOn = bool;
		window.refresh;
	}
	
	gridStep_ { |step|
		gridStep = step;
		if(gridOn, { window.refresh })
	}
	
	makeUserview {
		var w = window.bounds.width, h = window.bounds.height;
		
		userview !? { userview.remove };
		userview = JSCUserView(window,Rect(0,0,w,h)).resize_(5);
		
		userview.beginDragAction = {
			var classes,rects;
			
			if ( selectedViews.size > 0,{
				#classes, rects = flop(selectedViews.collect({ |view|
					[ view.class, view.bounds ]
				}));
				
				GD_MultipleDrag(classes,rects)
			})
		};
		
		userview.keyDownFunc = { |v,c,m,u| this.panelSelect.keyDown(c,u) };
		userview.mouseBeginTrackFunc = { |v,x,y| this.panelSelect.mouseDown(x,y) };
		userview.mouseEndTrackFunc = { |v,x,y| this.panelSelect.mouseUp };
		
		userview.mouseOverAction = { |v,x,y| dropX = x; dropY = y };
		
		userview.canReceiveDragHandler = {
			JSCView.currentDrag.isKindOf( GD_Drag )
		};
		
		userview.receiveDragHandler = {
			var addedViews = Array.new;
			
			JSCView.currentDrag.do({ |class, rect|
				rect = rect.moveBy( dropX, dropY );
				addedViews = addedViews.add( class.paletteExample(window,rect) );
			});
			
			views = views.addAll( addedViews );
			
			dragging = true;
			selectedViews = addedViews;
			indent = dropX@dropY - views.last.bounds.origin;
			
			this.makeUserview.updateResizeHandles;
			window.front.refresh;
			
			this.panelSelect
		};
		
		userview.mouseTrackFunc = { |v,x,y| this.drag(x,y) };
		userview.focus;
		
		this.initDrawFunc
	}
	
	initDrawFunc {
		userview.drawFunc = {
			var b,n,h,w;
			
			if(gridOn,{
				b = window.view.bounds;
				h = b.height;
				w = b.width;
				
				Color.yellow(1,0.4).set;
				
				n = h / gridStep;
				(n-1).do({ |i| i=i+1*gridStep;
					Pen.moveTo(0@i).lineTo(w@i).stroke;
				});
				
				n = w / gridStep;
				(n-1).do({ |i| i=i+1*gridStep;
					Pen.moveTo(i@0).lineTo(i@h).stroke;
				})
			});
			
			Color.blue.set;
			
			if(isSelected,{
				Pen.width_(4).strokeRect(window.bounds.moveTo(0,0));
			});
			
			selectedViews.do({ |v|
				Pen.strokeRect(v.bounds)
			});
			Pen.width_(1);
			
			resizeHandles.do({ |r|
				Pen.fillRect(r)
			});
			
			if(selection.notNil, {
				Pen.strokeRect(selection.rect)
			});
			
		}
	}
	
	deselect {
		isSelected = false;
		window.refresh
	}
	
	updateResizeHandles { var r,d=4;
		resizeHandles = if( selectedViews.size == 1,{
			r = selectedViews.first.bounds;
			[ r.leftTop, r.rightTop, r.rightBottom, r.leftBottom ]
				.collect({ |center| Rect.aboutPoint(center,d,d) })
		});
		window.refresh
	}
	
	panelSelect {
		isSelected = true;
		if(parent.notNil,
			{ parent.panelSelect(this) })
	}
	
	
	setResizeFixed { |resizeHandle|
		var r = selectedViews.first.bounds,i = resizeHandles.indexOf(resizeHandle);
		resizeFixed=r.perform([ \rightBottom, \leftBottom, \leftTop, \rightTop ][i])
	}
	
	
	mouseDown { |x,y|
		var view,p,handle;
		
		p = x@y;
		
		if( resizeHandles.notNil and: {
			(handle = resizeHandles.detect({ |h| h.containsPoint(p) }) ).notNil
		},
		{
			this.setResizeFixed(handle)
		},
		{
			resizeFixed = nil;
			view = this.viewContainingPoint(p);
			
			dragging = view.notNil;
			
			if( dragging, {
				indent = p - view.bounds.origin;
				
				if( (selectedViews.size > 1) and: 
					{ selectedViews.includes(view) },
				{
					multipleDragBy = view
				},
				{
					multipleDragBy = nil;
					selectedViews = [ view ]
				})
			},{
				selectedViews = [];
				selection = GD_AreaSelection(p)
			})
		});
		
		this.updateResizeHandles
	}
	
	drag { |x,y|
		var view,f,p=x@y;
		if( dragging, {
		
			if( resizeFixed.isNil,
			{
				if(multipleDragBy.notNil,
				{
					f = p - ( multipleDragBy.bounds.origin + indent );
					
					selectedViews.do({ |v| 
						this.quantSetBounds(v,v.bounds.moveBy(f.x,f.y))
					})
				},{
					view = selectedViews.first;
					this.quantSetBounds(view,view.bounds.moveToPoint(p-indent));
					
					this.updateResizeHandles
				})
			},{
				if(gridOn,{ p = p.round(gridStep) });
				selectedViews.first.bounds = Rect.fromPoints(p,resizeFixed);
				this.updateResizeHandles
			})
		},
		{
			selection.mouseDrag(p);
			selectedViews = views.select({ |view|
				selection.selects(view.bounds)
			});
			window.refresh
		})
	}
	
	mouseUp { |x,y|
		if(selection.notNil,{
			selection = nil; window.refresh
		})
	}
	
	keyDown { |c,u|
		var newViews;
		case (
		// delete
		{u==127}, {
			if(selectedViews.isEmpty.not,{
				selectedViews.do({ |v|
					views.remove(v.remove);
				});
				selectedViews=[];
				
				this.updateResizeHandles
			})
		},
		// clone
		{(c==$c) or: (c==$C)},{
			if(selectedViews.isEmpty.not,{
				newViews=selectedViews.collect({ |v|
					v.class.paletteExample(window,v.bounds.moveBy(40,40))
				});
				views=views++newViews;
				selectedViews=newViews;
				
				this.makeUserview.updateResizeHandles
			})
		})
	}
	
	
	quantSetBounds { |view,rect|
		view.bounds=if(gridOn,
			{ rect.moveToPoint(rect.origin.round(gridStep)) },
			{ rect })
	}
	
	
	viewContainingPoint { |point|
		views.do({ |view|
			if(view.bounds.containsPoint(point),
				{ ^view })
		})
		^nil
	}
	
	aJSCompileString {
		var str = "";
		views.do({ |v| str = str ++ format("%.new(w,%);\n",v.class,v.bounds) });
		^format( "(\nvar w = JSCWindow.new(\"\",%).front;\n%\n)",window.bounds,str )
	}
	
}

GD_ToolboxWindow
{
	var window,viewPallatte,panels,selectedPanel;
	
	*new { ^super.new.init }
	
	init
	{
		var n = GD_ViewPallatte.viewList.size;
		var vh = 24, vw = 146,gridNB,gridBut;
		
		var height = n + 4 * (vh + 2) +2, os;
		var vw2 = div(vw,2);
		
		var funcButCol = Color.blue;
		
		window = JSCWindow("GD",Rect(50,800,vw+4,height)).front;
		
		viewPallatte = GD_ViewPallatte(window,Rect(2, 2, vw, vh));
		
		panels = Array.new;
		
		os = vh + 2 * n + 2;
		JSCButton(window,Rect(2,os,vw,vh)).states_([["NEW WINDOW",nil,funcButCol]])
			.canFocus_(false).action = {
				var panel = GD_PanelWindow.new(Rect(100,100,400,400)).parent_(this);
				panel.gridStep_(gridNB.value);
				panel.gridOn = gridBut.value==1;
				
				panels = panels.add(panel)
		};
		
		os = os + vh + 2;
		JSCButton(window,Rect(2,os,vw,vh)).states_([ ["-> CODE",nil,funcButCol]])
			.canFocus_(false).action = { if ( selectedPanel.notNil, 
				{ selectedPanel.aJSCompileString.postln  } )
		};
		
		os = os + vh + 2;
		JSCButton(window,Rect(2,os,vw,vh)).states_([ ["TEST",nil,funcButCol]])
			.canFocus_(false).action = {
				selectedPanel.aJSCompileString.interpret
		};
		
		os = os + vh + 2;
		gridBut = JSCButton(window,Rect(2,os,vw2,vh))
			.canFocus_(false).action = { |v|
				gridNB.visible = v.value == 1;
				panels.do({ |panel|
					panel.gridOn_(v.value == 1).gridStep = gridNB.value;
				})
		};
		
		gridBut.states_([["Q ON",nil,funcButCol],["Q OFF",nil,funcButCol]]);

		gridNB = JSCNumberBox(window,Rect(2+vw2,os,vw2,vh))
			.action = { |v| 
				v.value = v.value.asInt.clip(3,40);
				panels.do({ |panel| panel.gridStep = v.value })
				
		};
		
		gridNB.align_(\center).value_(10).visible = false
	}
	
	panelSelect { |panel|
		if( panel !== selectedPanel,{
			if(selectedPanel.notNil,{ selectedPanel.deselect });
			selectedPanel = panel
		})
	}
}

GD_ViewPallatte
{
	classvar <viewList;
	
	*new { |window,rect,parent|
		^super.new.init(window,rect,parent)
	}
	
	init { |window,rect,parent|
		var x = rect.top, y = rect.left;
		var bW = rect.width, bH = rect.height;
		
		viewList.do({ |class,i|
			var drag = GD_PallatteDrag(class,Rect(0,0,100,20));
			JSCDragSource(window,Rect(x+2,i*(bH+2)+2+y,bW,bH))
				.string_(class.asString).align_(\center).object_(drag)
		})
	}
	
	*initClass {
		viewList = [
			JSCButton,
			JSCStaticText,
			JSCNumberBox,
			JSCSlider,
			JSCRangeSlider,
			JSCMultiSliderView,
			JSCPopUpMenu,
			JSC2DTabletSlider,
			JSC2DSlider,
			JSCTabletView,
			JSCEnvelopeView,
			JSCDragBoth,
			JSCDragSink,
			JSCDragSource,
			Knob
			//JSCTextView,
			//JSCMovieView
		];
	}
}

GD_AreaSelection
{
	var click,round,<rect;
	
	*new { |p,r| r !? { p = round(p,r) };
		^super.newCopyArgs(p,r).mouseDrag(p)
	}
	
	mouseDrag { |drag|
		round !? { drag = round(drag,round) };
		rect = Rect.fromPoints(click,drag)
	}
	
	selects { |aRect|
		^rect.intersects(aRect)
	}
}


GD_Drag { }

GD_MultipleDrag : GD_Drag
{
	var classes,rects,minX, minY;
	
	*new { |classes, rects|
		^super.newCopyArgs( classes, rects ).init
	}
	
	init {
		minX = inf;
		minY = inf;
		rects.do({ |r|
			if ( r.left < minX, { minX = r.left });
			if ( r.top < minY, { minY = r.top })
		});
		minX = minX.neg;
		minY = minY.neg;
	}
	
	do { |func|
		classes.do({ |class,i|
			func.( class, rects[ i ].moveBy( minX, minY ), i )
		})
	}
}

GD_PallatteDrag : GD_Drag
{
	var class, rect;
	
	*new { |class, rect|
		^super.newCopyArgs( class, rect )
	}
	
	do { |func| func.(class,rect,0) }
	
	asString { ^class.asString }
}

