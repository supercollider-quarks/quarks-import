
// note: the window's width is not fixed, but the width of the master flowview
// inside the window is fixed

FixedWidthMultiPageLayout : MultiPageLayout {
	init { arg title,bounds,argmargin,argmetal=true;
		var w,v;
		bounds = if(bounds.notNil,{  bounds.asRect },{GUI.window.screenBounds.insetAll(10,20,0,25)});
		windows=windows.add
		(	
			w=GUI.window.new("< " ++ title.asString ++ " >",
						bounds, border: true )
				.onClose_({
					this.close; // close all windows in this layout
				})
		);
		metal = argmetal;
		if(metal.not,{
			w.view.background_(bgcolor);
		});
		isClosed = false;
		v = FixedWidthFlowView(w);
		margin = argmargin;
		if(margin.notNil,{
			v.decorator.margin_(margin);
		});
		views = views.add(v );
		autoRemoves = [];
	}
}
