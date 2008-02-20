/******* by jostM Feb 20, 2008 version 1.15 *******/
TabbedView {
	var labels,
		labelColors,
		unfocusedColors,
		backgrounds,
		stringColor,
		stringFocusedColor,
		<>focusActions,
		<>unfocusActions,
		tabWidth = \auto,
		tabWidths,
		scroll=false,
		<tabHeight=\auto,
		tbht,
		tabCurve = 8,
		<tabViews,
		<font,
		<views,
		<resize = 1,
		<tabPosition = \top,
		<activeTab = 0,
		focusHistory = 0,
		<labelPadding = 25,
		<relativeOrigin=false,
		left=0,
		top=0,
		<>swingFactor=7,
		<view;
	
	*new{ arg w, bounds, labels, colors, name=" ", scroll=false;
		^super.new.init(w, bounds, labels, colors, name, scroll );
	}
	
	init{ arg w, bounds, lbls, colors, name, scr ;
	
		w.isNil.if{ w = GUI.window.new(name,bounds).front;
			bounds = w.view.bounds; 
			resize = 5};
			
		//must be written this way, or nested views don't work with a nil bounds.
		bounds.isNil.if{ bounds = w.asView.bounds }; 
		
		view = GUI.compositeView.new(w,bounds).resize_(resize);
		if( GUI.id === \cocoa)  {this.relativeOrigin=view.relativeOrigin};
		
		lbls= lbls ? ["tab1","tab2","tab3"];
		scroll=scr;
		labels = [];
		focusActions = [];
		unfocusActions = [];
		font=GUI.font.default;		
		stringColor = Color.black;
		stringFocusedColor = Color.white;
		if( GUI.id === \cocoa)  {
			labelColors = colors ? [Color.grey.alpha_(0.2)];
			}{
			labelColors = colors ? [Color(0.85,0.85,0.85)];
			};
		unfocusedColors = Array.fill(labelColors.size,{arg i;
			var col;
			col = labelColors[i%labelColors.size].asArray;
			if( GUI.id === \cocoa)  
				{col = col*[0.7,0.7,0.7,1];}
				{col = col*[0.9,0.9,0.9,1];};
			col = Color(*col);
				});
		backgrounds = labelColors;
		
		tabViews = [];
		views = [];
		
		tabWidths = []; 
		

		lbls.do{arg label,i;
			this.add(label);
			
		};
		this.focus(0);
		^this;
	}
	
	add { arg label,index; //actually this is an insert method with args backwards
		var tab, container, calcTabWidth, i;
		
		index = index ? labels.size; //if index is nil, add it to the end
		i=index;
		labels=labels.insert(i,label.asString);
		i = labels.size-1;
		
		label=label.asString; //allows for use of symbols as arguments
		
//		if (tabWidth == \auto) //overwrite tabWidths if autowidth
//			{ calcTabWidth=label.bounds.width + labelPadding }
//			{ calcTabWidth = tabWidth };
			
		tabWidths=tabWidths.insert(index,50);	
		
		tab = GUI.userView.new(view); //bounds are set later
		if( GUI.id === \cocoa)  {tab.relativeOrigin_(relativeOrigin)};
		tab.enabled = true;
		tab.mouseDownAction_({
			this.focus(i);
			tab.focus(false); 
		});
		tabViews = tabViews.insert(index, tab);
		
		scroll.if{container = GUI.scrollView.new(view).resize_(5)}
		{container = GUI.compositeView.new(view).resize_(5)}; //bounds are set later
		
		container.background = backgrounds[i%backgrounds.size];
		
		if( GUI.id === \cocoa)  {container.relativeOrigin_(relativeOrigin)};
		
		views = views.insert(index,container);
		
		focusActions = focusActions.insert(index,{});
		unfocusActions = unfocusActions.insert(index,{});
		tabViews.do{ arg tab, i;
			tab.mouseDownAction_({
				this.focus(i);
				tab.focus(false); 
			});
			tab.canReceiveDragHandler_({
				this.focus(i);
				tab.focus(false); 
			});
		};
		this.updateViewSizes();
		^this.views[index];

	}
		
	
	insert{ arg index,label;
		^this.add(label,index);
	}
	
	
	/** this paints the tabs with rounded edges **/
	
	paintTab{ arg tabLabelView,label = "label", background, strColor;
		switch(tabPosition)
		{\top}{this.paintTabTop(tabLabelView,label, background, strColor)}
		{\left}{this.paintTabLeft(tabLabelView,label, background, strColor)}
		{\bottom}{this.paintTabBottom(tabLabelView,label, background, strColor)}
		{\right}{this.paintTabRight(tabLabelView,label, background, strColor)};
	}

	paintTabTop{	arg tabLabelView,label = "label", background, strColor; 
		tabLabelView.drawFunc = { arg tview;
			var drawRect;	
			drawRect= if (relativeOrigin,tview.bounds.moveTo(0,0),tview.bounds);
			GUI.pen.use{
				GUI.pen.width_(1);
				GUI.pen.color_(background);
				
				GUI.pen.addWedge( (drawRect.left + tabCurve)@(drawRect.top + tabCurve),
					tabCurve, pi, pi/2);
				GUI.pen.addWedge( (drawRect.right - tabCurve)@(drawRect.top + tabCurve),
					tabCurve, 0, (pi/2).neg);
					
				GUI.pen.addRect( Rect(drawRect.left + tabCurve, 
							drawRect.top,
							drawRect.width - tabCurve - tabCurve, 
							tabCurve) 
							);
				GUI.pen.addRect( Rect(drawRect.left, 
							drawRect.top+tabCurve,
							drawRect.width,
							drawRect.height-tabCurve)
						);
				GUI.pen.fill;
				GUI.pen.font_(font);
				GUI.pen.color_(strColor);
 				GUI.pen.stringCenteredIn(label, drawRect.moveBy(0,1));
			};
		
		};
		tabLabelView.refresh;
	}
	
	paintTabLeft{	arg tabLabelView,label = "label", background, strColor; 
			
		tabLabelView.drawFunc = { arg tview;
			var drawRect;	
			drawRect= if (relativeOrigin, tview.bounds.moveTo(0,0), tview.bounds);
			GUI.pen.use{
				GUI.pen.width_(1);
				GUI.pen.color_(background);
				
				GUI.pen.addWedge( (drawRect.left + tabCurve)@(drawRect.top + tabCurve),
					tabCurve, -pi/2,- pi/2);
				GUI.pen.addWedge( (drawRect.left + tabCurve)@(drawRect.top + drawRect.height - tabCurve),
					tabCurve, -pi,- pi/2);
					
				GUI.pen.addRect( Rect(drawRect.left , 
							drawRect.top + tabCurve,
							tabCurve, 
							drawRect.height - tabCurve - tabCurve) 
							);
				GUI.pen.addRect( Rect(drawRect.left + tabCurve, 
							drawRect.top,
							drawRect.width-tabCurve,
							drawRect.height)
						);
				GUI.pen.fill;
				GUI.pen.font_(font);
				GUI.pen.color_(strColor);
 				GUI.pen.stringLeftJustIn(label, drawRect.insetAll((labelPadding/2)-2,0,0,0).moveBy(0,1));
			};
		
		};
		tabLabelView.refresh;
	}
	
	paintTabBottom{ arg tabLabelView,label = "label", background, strColor; 
			
		tabLabelView.drawFunc = { arg tview;
			var drawRect;	
			drawRect= if (relativeOrigin,tview.bounds.moveTo(0,0),tview.bounds);
			GUI.pen.use{
				GUI.pen.width_(1);
				GUI.pen.color_(background);
				
				GUI.pen.addWedge( (drawRect.left + tabCurve)@(drawRect.top+drawRect.height - tabCurve),
					tabCurve, pi, -pi/2);
				GUI.pen.addWedge( (drawRect.right - tabCurve)@(drawRect.top+drawRect.height - tabCurve),
					tabCurve, 0, (pi/2));
					
				GUI.pen.addRect( Rect(drawRect.left + tabCurve, 
							drawRect.top+drawRect.height - tabCurve,
							drawRect.width - tabCurve - tabCurve, 
							tabCurve) 
							);
				GUI.pen.addRect( Rect(drawRect.left, 
							drawRect.top,
							drawRect.width,
							drawRect.height-tabCurve)
						);
				GUI.pen.fill;
				GUI.pen.font_(font);
				GUI.pen.color_(strColor);
 				GUI.pen.stringCenteredIn(label, drawRect.moveBy(0,1));
			};
		
		};
		tabLabelView.refresh;
	}
	
	paintTabRight{ arg tabLabelView,label = "label", background, strColor; 
			
		tabLabelView.drawFunc = { arg tview;
			var drawRect;	
			drawRect= if (relativeOrigin,tview.bounds.moveTo(0,0),tview.bounds);
			GUI.pen.use{
				GUI.pen.width_(1);
				GUI.pen.color_(background);
				
				GUI.pen.addWedge( (drawRect.left+drawRect.width - tabCurve)
					@(drawRect.top + tabCurve),
					tabCurve, -pi/2, pi/2);
				GUI.pen.addWedge( (drawRect.left+drawRect.width - tabCurve)
					@(drawRect.top + drawRect.height - tabCurve),
					tabCurve, 0, pi/2);
					
				GUI.pen.addRect( Rect(drawRect.left+drawRect.width - tabCurve , 
							drawRect.top + tabCurve,
							tabCurve, 
							drawRect.height - tabCurve - tabCurve) 
							);
				GUI.pen.addRect( Rect(drawRect.left, 
							drawRect.top,
							drawRect.width-tabCurve,
							drawRect.height)
						);
				GUI.pen.fill;
				GUI.pen.font_(font);
				GUI.pen.color_(strColor);
 				GUI.pen.stringLeftJustIn(label, drawRect.insetAll((labelPadding/2)-2,0,0,0).moveBy(0,1));
			};
		
		};
		tabLabelView.refresh;

	}
	
	updateFocus{
		
		tabViews.do{ arg tab,i;
			if (activeTab == i){
				this.paintTab( tab, labels[i], 
					labelColors[ i%labelColors.size ], 
					stringFocusedColor ); // focus colors 
				views[i].visible_(true);
				// do the user focusAction only on focus
				if (focusHistory!= i){ focusActions[i].value; };
			}{
				this.paintTab( tab, labels[i], 
					unfocusedColors[ i%unfocusedColors.size ], 
					stringColor );// unfocus colors
				views[i].visible_(false);
						if (focusHistory == i)
					//do the user unfocusAction only on unfocus
					{ unfocusActions[ focusHistory ].value };
			};
		};
		focusHistory = activeTab;
	}
	
	updateViewSizes{
	
		
		left = if( relativeOrigin, 0, view.bounds.left);
		top  = if( relativeOrigin, 0, view.bounds.top);
		
		if( GUI.id === \cocoa)  {
			if ( tabHeight == \auto ){ tbht = ("A".bounds(font).height+1 )}{tbht=tabHeight};
			tabViews.do{ arg tab, i; 
				if ( tabWidth.asSymbol == \auto )
					{ tabWidths[i] = labels[i].bounds(font).width + labelPadding }
					{ tabWidths[i] = tabWidth };
					
			};
		} {
			if ( tabHeight == \auto ){ tbht = (font.size+6)}{tbht=tabHeight};
			tabViews.do{ arg tab, i; 
				if ( tabWidth.asSymbol == \auto )
					{ tabWidths[i] = (labels[i].size * swingFactor*font.size*0.09)+ labelPadding }
					{ tabWidths[i] = tabWidth };
			};
		};

		
		switch(tabPosition)
		{\top}{this.updateViewSizesTop}
		{\left}{this.updateViewSizesLeft}
		{\bottom}{this.updateViewSizesBottom}
		{\right}{this.updateViewSizesRight};
		
		
		views.do{arg v;
			v.children.notNil.if{
				if (v.children[0].class.name==\FlowView){
					v.children[0].bounds_(v.bounds);
					//this is redundant, but fixes strange behavior for some reason
					v.children[0].bounds_(v.children[0].bounds.moveBy(2,2));
				};
			};
		};
	}
	
	
	
	updateViewSizesTop{
			
		tabViews.do{ arg tab, i; 
			tab.bounds_( Rect(
					left + ( ([0]++tabWidths).integrate.at(i) ) + i,
					top,
					tabWidths[i],
					tbht) 
				);
			tab.resize = switch(resize)
				{1}{1}
				{2}{1}
				{3}{3}
				{4}{1}
				{5}{1}
				{6}{3}
				{7}{7}
				{8}{7}
				{9}{9};
		};
		
		views.do{ arg v, i; 
			v.bounds = Rect(
				left,
				top + tbht,
				view.bounds.width,
				view.bounds.height-tbht);
				v.background_( backgrounds[ i%backgrounds.size ] );
				
		};
		
		this.updateFocus;
	}
	
	updateViewSizesLeft{
		
		tabViews.do{ arg tab, i; 

			tab.bounds_( Rect(
					left,
					top + (i*tbht) + i,
					tabWidths.maxItem,
					tbht) 
				);
				
			tab.resize = switch(resize)
				{1}{1}
				{2}{1}
				{3}{3}
				{4}{1}
				{5}{1}
				{6}{3}
				{7}{7}
				{8}{7}
				{9}{9};
		};
		
		views.do{arg v, i; 
			v.bounds = Rect(
				left + tabWidths.maxItem,
				top,
				view.bounds.width-tabWidths.maxItem,
				view.bounds.height);
				v.background_( backgrounds[ i%backgrounds.size ] );
		};
		
		this.updateFocus();

	}	
	updateViewSizesBottom{
	
		tabViews.do{ arg tab, i; 
			tab.bounds_( Rect(
					left + ( ([0]++tabWidths).integrate.at(i) ) + i,
					top+view.bounds.height-tbht,
					tabWidths[i],
					tbht) 
				);
			tab.resize = switch(resize)
				{1}{1}
				{2}{1}
				{3}{3}
				{4}{7}
				{5}{7}
				{6}{9}
				{7}{7}
				{8}{7}
				{9}{9};
		};
		
		views.do{arg v, i; 
			v.bounds = Rect(
				left,
				top,
				view.bounds.width,
				view.bounds.height-tbht);
				v.background_( backgrounds[ i%backgrounds.size ] );
		};
		
		this.updateFocus;
	}	
	
	updateViewSizesRight{
			
		tabViews.do{ arg tab, i; 
			tab.bounds_( Rect(
					view.bounds.width + left - tabWidths.maxItem,
					top + (i*tbht) + i,
					tabWidths.maxItem,
					tbht) 
				);
			tab.resize = switch(resize)
				{1}{1}
				{2}{3}
				{3}{3}
				{4}{3}
				{5}{3}
				{6}{3}
				{7}{9}
				{8}{9}
				{9}{9};
			
		};
		
		views.do{arg v, i; 
			v.bounds = Rect(
				left,
				top,
				view.bounds.width-tabWidths.maxItem,
				view.bounds.height);
				v.background_( backgrounds[ i%backgrounds.size ] );
		};
		
		this.updateFocus();

	}	
	
	tabPosition_{arg symbol; // \left, \top, \right, or \bottom
	 	tabPosition=symbol;
		this.updateViewSizes();
		
	}
	
	
	resize_{arg int;
		resize = int;
		view.resize_(int);
		views.do{|v| v.resize_(int)};
		this.updateViewSizes;	
	}
	
	focus_{arg index;
		activeTab = index;
		"focus_(index) deprecated. pleas use focus(index)".postln;
		this.updateFocus();
	}
	focus{arg index;
		activeTab = index;
		this.updateFocus();
	}
	
	labelColors_{arg colorArray; 
		labelColors = colorArray; 
		this.updateViewSizes();
	}
	
 	unfocusedColors_{arg colorArray; 
 		unfocusedColors = colorArray; 
 		this.updateViewSizes();
 	}
 	
	backgrounds_{arg colorArray; 
		backgrounds = colorArray; 
		this.updateViewSizes();
	}
	
	stringColor_{arg color; 
		stringColor = color; 
		this.updateViewSizes()
	}
	stringFocusedColor_{arg color; 
		stringFocusedColor = color; 
		this.updateViewSizes()
	}
	labelPadding_{arg int; 
		labelPadding = int;
		this.updateViewSizes;
	}
	
	tabWidth_{arg int; 
		tabWidth = int;
		this.updateViewSizes();
	}
	
	tabHeight_{arg val; 
		tabHeight = val;
		this.updateViewSizes();
	}
	
	tabCurve_{arg int; 	
		tabCurve = int;
		this.updateViewSizes();
	}

	font_{arg fnt; 	
		 font = fnt;
		 this.updateViewSizes();
		 
	}
	
	relativeOrigin_{arg bool;
		relativeOrigin=bool;
		if( GUI.id === \cocoa)  {view.relativeOrigin_(relativeOrigin)};
		this.updateViewSizes();
	}
	
	
	removeAt{ arg index;
	
		labels.removeAt(index);
		tabViews[index].remove;
		tabViews.removeAt(index);
		views[index].remove;
		views.removeAt(index);
		tabWidths.removeAt(index);
		focusActions.removeAt(index);
		unfocusActions.removeAt(index);
		
		tabViews.do{ arg tab, i;
			tab.mouseDownAction_({
				this.focus(i);
				tab.focus(false); 
			});
			tab.canReceiveDragHandler_({
				this.focus(i);
				tab.focus(false); 
			});
		};
		
		this.focus(0);
		focusHistory=0;
		this.updateViewSizes();
		view.refresh;
	}
	

	// use these as examples to make your own class extentions according to your needs
	*newBasic{ arg w, bounds, labels, colors, name=" ", scroll=false;
		var q;
		q=this.new(w, bounds, labels, colors, name, scroll);
		if( GUI.id === \cocoa)  {
			q.labelColors_([Color.white.alpha_(0.3)]);
			q.backgrounds_([Color.white.alpha_(0.3)]);
		}{
			q.labelColors_([Color(0.9,0.9,0.9)]);
			q.backgrounds_([Color(0.9,0.9,0.9)]);
			q.unfocusedColors_([Color(0.8,0.8,0.8)]);
		};
		^q;
	}
	
	*newRGBLabels{ arg w, bounds, labels, colors, name=" ", scroll=false;
		"\nWarning: TabbedView.newRGBLabels deprecated. Use .newColorLabels instead".postln;
		^this.newColorLabels(w, bounds, labels, colors, name, scroll);
		}
		
	*newColorLabels{ arg w, bounds, labels, colors, name=" ", scroll=false;
		var q;
		q=this.newBasic(w, bounds, labels, colors, name, scroll);
		q.labelColors_([Color.red,Color.blue,Color.yellow]);
		if( GUI.id === \cocoa)  {
			q.backgrounds_([Color.white.alpha_(0.3)]);
		}{
			q.backgrounds_([Color(0.9,0.9,0.9)]);
			q.unfocusedColors_([Color(0.9,0.75,0.75),
							Color(0.75,0.75,0.9),
							Color(0.9,0.9,0.75)]);
		};
		^q;
	}
	
	*newRGB{ arg w, bounds, labels, colors, name=" ", scroll=false;
		"\nWarning: TabbedView.newRGB  deprecated. Use .newColor instead".postln;
		^this.newColor(w, bounds, labels, colors, name, scroll);
		}
		
	*newColor{ arg w, bounds, labels, colors, name=" ", scroll=false;
		var q;
		q=this.new(w, bounds, labels, colors, name, scroll);
		q.labelColors_([Color.red,Color.blue,Color.yellow]);
		if( GUI.id === \cocoa)  {
			q.backgrounds_([Color.red.alpha_(0.1),
								Color.blue.alpha_(0.1),
								Color.yellow.alpha_(0.1)]);
			q.unfocusedColors_([Color.red.alpha_(0.2),
								Color.blue.alpha_(0.2),
								Color.yellow.alpha_(0.2)]);
		}{
			q.backgrounds_([Color(0.9,0.85,0.85),
								Color(0.85,0.85,0.9),
								Color(0.9,0.9,0.85)]);
			q.unfocusedColors_([Color(0.9,0.75,0.75),
								Color(0.75,0.75,0.9),
								Color(0.9,0.9,0.75)]);
		};

		^q;
	}
	
	*newFlat{ arg w, bounds, labels, colors, name=" ", scroll=false;
		var q;
		q=this.newBasic(w, bounds, labels, colors, name, scroll);
		q.tabHeight=14;
		q.tabWidth= 70;
		q.tabCurve=3;
		^q;
	}
	
	*newTall{ arg w, bounds, labels, colors, name=" ", scroll=false;
		var q;
		q=this.newBasic(w, bounds, labels, colors, name, scroll);
		q.tabHeight= 30;
		q.tabWidth= 70;
		q.tabCurve=3;
	^q;
	}
	
	*newTransparent{ arg w, bounds, labels, colors, name=" ", scroll=false;
		var q;
		q=this.new(w, bounds, labels, colors, name, scroll);
		if( GUI.id === \cocoa)  {
			q.labelColors_([Color.white.alpha_(0.3)]);
		}{	
			q.labelColors_([Color(0.9,0.9,0.9)]);
			q.unfocusedColors_([Color(0.8,0.8,0.8)]);
		};
		q.backgrounds_([Color.clear]);
		^q;
	}
	
	*newPacked{ arg w, bounds, labels, colors, name=" ", scroll=false;
		var q;
		q=this.new(w, bounds, labels, colors, name, scroll);
		if( GUI.id === \cocoa)  {
			q.labelColors_([Color.white.alpha_(0.3)]);
			q.backgrounds_([Color.white.alpha_(0.3)]);
			}{
			q.labelColors_([Color(0.85,0.85,0.85)]);
			q.backgrounds_([Color(0.85,0.85,0.85)]);
			q.unfocusedColors_([Color(0.8,0.8,0.8)]);
		};
		q.tabCurve=3;
		q.labelPadding=8;
		q.tabHeight=14;
		^q;
	}
}
