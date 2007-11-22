
// resize everything in a view's chain

+ SCContainerView {
	recursiveResize {
		children.do({ arg c;
			c.recursiveResize;
		});
		this.tryPerform(\reflowAll);
		this.tryPerform(\resizeToFitContents).isNil.if({
			this.tryPerform(\resizeToFit);
		});
	}
	
	findRightBottom {		// a containerview can find the lowest-right point occupied by
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
		this.checkNotClosed.if({
			views.do({ arg v;
				v.recursiveResize;
			});
			this.resizeToFit;
		});
	}
}

+ SCView {
	recursiveResize { ^nil }	// the buck stops here
	
	findRightBottom { ^this.bounds.rightBottom }	// non-recursive: give result to caller

	isActive { ^dataptr.notNil }
}

+ StartRow {
	recursiveResize { ^nil }
}

+ Object { isActive { ^false } }		// non-views should reply with false

+ SCViewHolder {
	findRightBottom { ^this.bounds.rightBottom }	// non-recursive: give result to caller
}

+ FlowView {
	resizeToFitContents
			// need bounds relative to parent's bounds
		var new, maxpt, comparept, mybounds, used;
		mybounds = this.bounds;
		maxpt = mybounds.leftTop;	// w/o this, maxpt could be above or left of top left-bad
		this.children.do({ arg c;
			comparept = c.findRightBottom;
			maxpt = maxpt.max(comparept);
		});
		new = mybounds.resizeTo(maxpt.x - mybounds.left + this.decorator.margin.x,
			maxpt.y - mybounds.top + this.decorator.margin.y);
		this.bounds_(new, reflow: false);
		// don't reflow unless asked
		^new
	}

	recursiveResize {
		this.children.do({ arg c;
			c.recursiveResize;
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
