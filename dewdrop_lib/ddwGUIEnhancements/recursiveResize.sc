
// resize everything in a view's chain

+ SCContainerView {
	recursiveResize { // arg level = 0;
		children.do({ arg c;
			c.recursiveResize/*(level+1)*/;
		});
		this.tryPerform(\reflowAll);
		this.tryPerform(\resizeToFitContents).isNil.if({
			this.tryPerform(\resizeToFit);
		});
	}
	
	findRightBottom {		// a containerview can find the lowest-right point occupied by
//arg level;
		var maxpt;
		maxpt = this.bounds.leftTop;
		children.do({ arg c;
			maxpt = maxpt.max(c.findRightBottom);
		});
		^maxpt
	}
}

+ MultiPageLayout {
	recursiveResize {
//this.checkNotClosed.debug("MPLayout-recursiveResize");
//this.dumpBackTrace;
		this.checkNotClosed.if({
//"executing resize action".debug;
			views.do({ arg v;
//v.dump; v.isActive.debug("view is active");
				v.recursiveResize;
			});
//"calling resizetofit".debug;
			this.resizeToFit;
		});
	}
}

+ SCView {
	recursiveResize { // arg level;
	 ^nil }	// the buck stops here
	
	findRightBottom { // arg level;
	^this.bounds.rightBottom }	// non-recursive: give result to caller

	isActive { ^dataptr.notNil }
}

+ StartRow {
	recursiveResize { ^nil }
}

+ Object { isActive { ^false } }		// non-views should reply with false

+ SCViewHolder {
	findRightBottom { // arg level;
	^this.bounds.rightBottom }	// non-recursive: give result to caller
}

+ FlowView {
	resizeToFitContents { // arg thorough = false/*, level*/;
			// need bounds relative to parent's bounds
		var new, maxpt, comparept, mybounds, used;
		mybounds = this.bounds;
		maxpt = mybounds.leftTop;	// w/o this, maxpt could be above or left of top left-bad
//"FlowView-resizeToFitContents - ".post; mybounds.post; "  ".post; maxpt.post; "  ".post;
		this.children.do({ arg c;
			comparept = c.findRightBottom;
			maxpt = maxpt.max(comparept);
		});
//maxpt.post;
		new = mybounds.resizeTo(maxpt.x - mybounds.left + this.decorator.margin.x,
			maxpt.y - mybounds.top + this.decorator.margin.y);
//"  ".post; new.postln;
		this.bounds_(new, reflow: false);
//		this.decorator.bounds = new;	// handled in FlowView now
		// don't reflow unless asked
		^new
	}

	recursiveResize { // arg level = 0;
//this.children.size.debug("FlowView:recursiveResize");
//this.dumpBackTrace;
//		view.recursiveResize;
		this.children.do({ arg c;
//c.debug("calling recursiveResize on child");
			c.recursiveResize/*(level+1)*/;
		});
		this.tryPerform(\reflowAll);
		this.tryPerform(\resizeToFitContents).isNil.if({
			this.tryPerform(\resizeToFit);
		});
	}
}

+ Point {
	max { arg that;
		^Point(this.x.max(that.x), this.y.max(that.y))
	}
	
	min { arg that;
		^Point(this.x.min(that.x), this.y.min(that.y))
	}
}

// for debugging

+ Integer {
	reptChar { arg c = $\t;
		var s;
		s = "";
		this.do({ s = s ++ c; });
		^s
	}
}

+ Rect {
	asString { ^"Rect( " ++ left ++ ", " ++ top ++ ", " ++ width ++ ", " ++ height ++ " )" }
}


+ ObjectGui {
	guiNoLabel { arg lay, bounds ... args;
		var layout;
		layout=this.guify(lay,bounds);
		layout.flow({ arg layout;
			view = layout;
//			this.writeName(layout);
			this.performList(\guiBody,[layout] ++ args);
		},bounds).background_(this.background);
		//if you created it, front it
		if(lay.isNil,{ layout.resizeToFit.front });
	}
}

+ Object {
	guiNoLabel { arg  ... args; 
		^this.guiClass.new(this).performList(\guiNoLabel, args);
	}
	
	isView { ^false }
}

+ SCView {
	isView { ^true }
}


//+ SCPopUpMenu {
//	defaultKeyDownAction { arg char, modifiers, unicode;
//		if (char == $ , { this.valueAction = this.value + 1; ^this });
//		if (char == $\r, { this.valueAction = this.value + 1; ^this });
//		if (char == $\n, { this.valueAction = this.value + 1; ^this });
//		if (char == 3.asAscii, { this.valueAction = this.value + 1; ^this });
//		if (unicode == 16rF700, { this.valueAction = this.value - 1; ^this });
//		if (unicode == 16rF703, { this.valueAction = this.value + 1; ^this });
//		if (unicode == 16rF701, { this.valueAction = this.value + 1; ^this });
//		if (unicode == 16rF702, { this.valueAction = this.value - 1; ^this });
//		^nil		// bubble if it's an invalid key
//	}
//}