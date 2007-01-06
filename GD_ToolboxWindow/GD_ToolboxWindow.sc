/*
	GD_ToolboxWindow()
	scott micheli-smith

	press c to clone
	press delete to delete
	add/remove GUI classes at line 399 and recompile

*/
GD_PanelWindow
{
	var window,tablet,>parent,isSelected=false;

	var selection,<selectedViews,views;

	var <gridStep = 10,<gridOn = false,dragging,indent,multipleDragBy;

	var resizeHandles,resizeFixed,>dropView;


	*new { |bounds|
		 ^super.new.init(bounds)
	}


	init { |bounds|

		 window = SCWindow("Panel",bounds).front;

		 views = [
			  SCButton(window,Rect(14,16,200,30)).states_([["hello"]]),
			  SCButton(window,Rect(52,57,200,30)).states_([["there"]])
		 ];

		 this.initDrawHook.makeTablet;
	}

	gridOn_ { |bool|
		 gridOn = bool;
		 window.refresh;
	}

	gridStep_ { |step|
		 gridStep = step;
		 if(gridOn, { window.refresh })
	}

	initDrawHook {
		 window.drawHook = {
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
				   Pen.width_(1)
			  });

			  selectedViews.do({ |v|
				   Pen.strokeRect(v.bounds)
			  });

			  resizeHandles.do({ |r|
				   Pen.fillRect(r)
			  });

			  if(selection.notNil, {
				   if(dropView.notNil, { Color.green.set });
				   Pen.strokeRect(selection.rect)
			  });

		 }
	}

	makeTablet {
		 var w = window.bounds.width, h = window.bounds.height;

		 tablet !? { tablet.remove };
		 tablet = SCTabletView(window,Rect(0,0,w,h)).resize_(5);

		 tablet.keyDownAction = { |v,c,m,u| this.panelSelect.keyDown(c,u) };
		 tablet.mouseDownAction = { |v,x,y| this.panelSelect.mouseDown(x,y) };
		 tablet.mouseUpAction = { |v,x,y| this.panelSelect.mouseUp(x,y) };

		 tablet.action = { |v,x,y| this.drag(x,y) };
		 tablet.focus
	}

	deselect {
		 isSelected = false;
		 window.refresh
	}

	updateResizeHandles { var r,d=3;
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
				   selection = GD_AreaSelection(p, if(gridOn, { dropView !? gridStep }) )
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
			  if(dropView.isNil,{
				   selectedViews = views.select({ |view|
					    selection.selects(view.bounds)
				   })
			  });
			  window.refresh
		 })
	}

	mouseUp { |x,y|
		 selection !? {
			  if(dropView.notNil,{
				   views = views.grow(1).add(
					    dropView.paletteExample(window,selection.rect)
				   );
				   this.makeTablet;
			  });
			  selection = nil;
			  window.refresh;
		 }
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

				   this.makeTablet.updateResizeHandles
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

	asCompileString {
		 var str = postf( "var w = SCWindow.new(\"\",%).front;",window.bounds );
		 views.do({ |v|
			  str = str ++ postf("%.new(w,%);\n",v.class,v.bounds)
		 })
	}

}

GD_ToolboxWindow
{
	var window,viewPallatte,panels,selectedPanel,dropView;

	*new { ^super.new.init }

	init
	{
		 var n = GD_ViewPallatte.viewList.size;
		 var vh = 24, vw = 146,gridNB,gridBut;

		 var height = n + 3 * (vh + 2) +2, os;
		 var vw2 = div(vw,2);

		 var funcButCol = Color.blue;

		 window = SCWindow("GD",Rect(50,800,vw+4,height)).front;

		 viewPallatte = GD_ViewPallatte(window,Rect(2, 2, vw, vh),this);

		 panels = Array.new;

		 os = vh + 2 * n + 2;
		 SCButton(window,Rect(2,os,vw,vh)).states_([["NEW WINDOW",nil,funcButCol]])
			  .action = {
				   var panel = GD_PanelWindow.new(Rect(100,100,400,400)).parent_(this);
				   panel.gridStep_(gridNB.value).dropView_(dropView);
				   panel.gridOn = gridBut.value==1;

				   panels = panels.grow(1).add(panel)
		 };

		 os = os + vh + 2;
		 SCButton(window,Rect(2,os,vw,vh)).states_([ ["-> CODE",nil,funcButCol]])
			  .action = { if ( selectedPanel.notNil,
				   { selectedPanel.asCompileString.postln  } )
		 };

		 os = os + vh + 2;
		 gridBut = SCButton(window,Rect(2,os,vw2,vh))
			  .action = { |v|
				   gridNB.visible = v.value == 1;
				   panels.do({ |panel|
					    panel.gridOn_(v.value == 1).gridStep = gridNB.value;
				   })
		 };

		 gridBut.states_([["Q ON",nil,funcButCol],["Q OFF",nil,funcButCol]]);

		 gridNB = SCNumberBox(window,Rect(2+vw2,os,vw2,vh))
			  .action = { |v|
				   v.value = v.value.asInt.clip(3,40);
				   panels.do({ |panel| panel.gridStep = v.value })

		 };

		 gridNB.align_(\center).value_(10).visible = false
	}

	setInsertView { |class|
		 dropView = class;
		 panels.do(_.dropView=dropView)
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
	var viewSelectButtons,selectedView;

	*new { |window,rect,parent|
		 ^super.new.init(window,rect,parent)
	}

	init { |window,rect,parent|
		 var x = rect.top, y = rect.left;
		 var bW = rect.width, bH = rect.height;

		 viewSelectButtons = viewList.collect({ |class,i|
			  var cs = class.asString;
			  SCButton(window,Rect(x+2,i*(bH+2)+2+y,bW,bH)).states_
				   ([ [cs], [cs,nil,Color.red] ]).action =
			  { |v|
				   if(v.value == 0,{
					    parent.setInsertView(nil);
					    selectedView = nil;
				   },{
					    parent.setInsertView(class);
					    this.deselect;
					    selectedView = i
				   })
			  }
		 })
	}

	deselect {
		 if( selectedView.notNil, {
			  viewSelectButtons[selectedView].value=0
		 })
	}

	*initClass {
		 viewList = [
			  SCButton,
			  SCStaticText,
			  SCNumberBox,
			  SCSlider,
			  SCRangeSlider,
			  SCMultiSliderView,
			  SCPopUpMenu,
			  SC2DTabletSlider,
			  SC2DSlider,
			  SCTabletView,
			  SCEnvelopeView,
			  SCDragBoth,
			  SCDragSink,
			  SCDragSource,
		//	  Knob,
		//	  SNBox,
		//	  VuView,
		//	  TriggerView,
		//	  ToggleView
			  //SCTextView,
			  //SCMovieView
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

GD_TestCompositeView
{
	var <bounds,views,boundsList;


	*new { |parent,bounds|
		 ^super.new.init(parent,bounds)
	}

	init { |parent,argBounds|
		 var w = parent, originalRect = Rect(0, 0, 200, 210);
		 var x, y, wScale, hScale;

		 bounds = argBounds;

		 x = bounds.left;
		 y = bounds.top;
		 wScale = bounds.width / originalRect.width;
		 hScale = bounds.height / originalRect.height;

		 boundsList = [
			  Rect(10,10,150,20),
			  Rect(10,40,150,20),
			  Rect(10,70,150,20),
			  Rect(170,10,20,80),
			  Rect(10,100,30,20),
			  Rect(50,100,30,20),
			  Rect(90,100,30,20),
			  Rect(130,100,30,20),
			  Rect(10,130,150,20),
			  Rect(10,160,150,40),
			  Rect(170,100,20,80),
			  Rect(0,0,200,210)
		 ];

		 views = Array.new(12);
		 views.add(SCSlider(w,this.scaleAndOffset(boundsList[0],x,y,wScale,hScale) ));
		 views.add(SCSlider(w,this.scaleAndOffset(boundsList[1],x,y,wScale,hScale) ));
		 views.add(SCSlider(w,this.scaleAndOffset(boundsList[2],x,y,wScale,hScale) ));
		 views.add(SCSlider(w,this.scaleAndOffset(boundsList[3],x,y,wScale,hScale) ));
		 views.add(SCButton(w,this.scaleAndOffset(boundsList[4],x,y,wScale,hScale)).states_([[]]));
		 views.add(SCButton(w,this.scaleAndOffset(boundsList[5],x,y,wScale,hScale)).states_([[]]));
		 views.add(SCButton(w,this.scaleAndOffset(boundsList[6],x,y,wScale,hScale)).states_([[]]));
		 views.add(SCButton(w,this.scaleAndOffset(boundsList[7],x,y,wScale,hScale)).states_([[]]));
		 views.add(SCButton(w,this.scaleAndOffset(boundsList[8],x,y,wScale,hScale)).states_([[]]));
		 views.add(SC2DSlider(w,this.scaleAndOffset(boundsList[9],x,y,wScale,hScale)));
		 views.add(SCSlider(w,this.scaleAndOffset(boundsList[10],x,y,wScale,hScale)));
		 views.add(SCStaticText(w,this.scaleAndOffset(boundsList[11],x,y,wScale,hScale)).backColor_(Color.green));


	}

	*paletteExample { |w,bounds|
		 ^this.new(w,bounds)
	}

	scaleAndOffset { |rect,x,y,wScale,hScale|
		 ^Rect(
			  rect.left * wScale + x,
			  rect.top * hScale + y,
			  rect.width * wScale,
			  rect.height * hScale
		 )
	}

	bounds_ { |argBounds|
		 var  originalRect = Rect(0, 0, 200, 210);
		 var x, y, wScale, hScale;

		 bounds = argBounds;

		 x = bounds.left;
		 y = bounds.top;
		 wScale = bounds.width / originalRect.width;
		 hScale = bounds.height / originalRect.height;

		 views.do({ |view,i|
			  view.bounds = this.scaleAndOffset(boundsList[i],x,y,wScale,hScale)
		 })

	}

	remove {
		 views.do(_.remove)
	}

}
