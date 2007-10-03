
HJHObjectGui : ObjectGui {
		// like felix's but it saves the layout that was passed in
	var	<masterLayout, <layout, <iMadeMasterLayout = false,
		<argBounds;

	guify { arg lay,bounds,title;
		argBounds = bounds;	// some of my gui's need to know this
		if(lay.isNil,{
			masterLayout = lay = FixedWidthMultiPageLayout
				(title ?? { model.asString.copyRange(0,50) },
				bounds);
			iMadeMasterLayout = true;	// now when I'm removed, I'll close the window too
		},{
			masterLayout = lay;	// should only pass in the FixedWidthMultiPageLayout
			lay = lay.asPageLayout(title,bounds);
		});
		// i am not really a view in the hierarchy
		lay.removeOnClose(this);
		^lay
	}

	remove {
		model.notNil.if({
			view.isActive.if({
				view.remove;
				masterLayout.recursiveResize;
			});
			model.view = nil;
			model = nil;
			iMadeMasterLayout.if({
				masterLayout.close;
			});
		});
	}

}