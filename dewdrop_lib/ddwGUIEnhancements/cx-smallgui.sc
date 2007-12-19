
+ Object {
	smallGui { arg  ... args;
//"Object-smallGui".debug;
		if(this.guiClass.findRespondingMethodFor(\smallGui).notNil,{
			^this.guiClass.new(this).performList(\smallGui,args);
		},{
			^Tile(this,args.first.asPageLayout)
		});
	}
}


// why is crucial so halfassed about some things?

+ ObjectGui {
	smallGui { arg lay, bounds ... args;
		var layout;
//"ObjectGui-smallGui".debug;
		layout=this.guify(lay,bounds, small:true);
		layout.flow({ arg layout;
			this.view = layout;
			this.writeName(layout);
			this.performList(\smallGuiBody,[layout] ++ args);
		},bounds).background_(this.background);
		//if you created it, front it
		if(lay.isNil,{ layout.resizeToFit.front });
	}
}


