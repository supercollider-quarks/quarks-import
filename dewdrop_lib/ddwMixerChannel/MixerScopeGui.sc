
MixerScopeGui : ObjectGui {
	classvar	defaultWidth = 500, defaultHeight = 100;
	var	<layout, <masterLayout, <view, <iMadeMasterLayout;

	guify { arg lay,bounds,title;
		if(lay.isNil,{
			masterLayout = lay = FixedWidthMultiPageLayout
				(title ?? { model.asString.copyRange(0,50) },
				bounds ?? { Rect(0, 0, defaultWidth, 
					defaultHeight * model.channel.outChannels) });
			iMadeMasterLayout = true;	// now when I'm removed, I'll close the window too
		},{
			masterLayout = lay;	// should only pass in the FixedWidthMultiPageLayout
			lay = lay.asPageLayout(title,bounds);
		});
		// i am not really a view in the hierarchy
		lay.removeOnClose(this);
		^lay
	}

	guiBody { arg lay;
		layout = lay;
		view = GUI.scopeView.new(lay, lay.bounds)
			.bufnum_(model.buffer.bufnum);
	}
	
	remove { arg dummy, freeModel = true;	// when model frees programmatically, this is false
		model.dependants.remove(this);	// to avoid recursion with model.free below
		
		view.notClosed.if({	
			view.remove;
//			masterLayout.recursiveResize;
		});

		iMadeMasterLayout.if({
			masterLayout.close;
		});
		
		freeModel.if({ model.free; });
		
		layout = masterLayout = view = iMadeMasterLayout = nil;
	}
}
