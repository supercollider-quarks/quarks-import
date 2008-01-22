
// resize everything in a view's chain

+ SCContainerView {
	recursiveResize {
		children.do({ arg c;
			c.recursiveResize;
		});
	}
	
		// a containerview can find the lowest-right point occupied by its children
	findRightBottom {
		var origin = this.bounds.leftTop, maxpt;
//[this.class.name, this.bounds, this.bounds.rightBottom].debug(">> SCContainerView:findRightBottom");
//"view pointer: ".post; dataptr.postln;
		if(this.tryPerform(\relativeOrigin) ? false) {
			maxpt = Point(0, 0);
		} {
			maxpt = origin;
		};
		children.do({ arg c;
			maxpt = maxpt.max(c.findRightBottom);
		});
		if(this.tryPerform(\relativeOrigin) ? false) {
//"adding origin".debug;
			maxpt = maxpt + origin;
		};
		if(decorator.notNil) {
//"adding margin".debug;
			maxpt = maxpt + decorator.margin;
		};
//[this.class.name, maxpt].debug("<< SCContainerView:findRightBottom");
//"view pointer: ".post; dataptr.postln;
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
	
		// non-recursive: give result to caller
	findRightBottom {
//[this.class.name, this.bounds, this.bounds.rightBottom].debug("SCView:findRightBottom");
//"view pointer: ".post; dataptr.postln;
		^this.bounds.rightBottom
	}

	isActive { ^dataptr.notNil }
}

+ StartRow {
	recursiveResize { ^nil }
	findRightBottom { ^Point(0, 0) }
}

+ Object { isActive { ^false } }		// non-views should reply with false

+ SCViewHolder {
	findRightBottom { 
		var	out;
//[this.class.name, this.bounds, this.bounds.rightBottom].debug(">> SCViewHolder:findRightBottom");
//"view pointer: ".post; view.instVarAt(0).postln;
		out = view.findRightBottom;
//out.debug("<< SCViewHolder:findRightBottom");
//"view pointer: ".post; view.instVarAt(0).postln;
		^out
	}
}

+ FlowView {
	resizeToFitContents {
			// need bounds relative to parent's bounds
		var new, maxpt, comparept, mybounds, used;
//">> FlowView:resizeToFitContents - ".post; view.instVarAt(0).postln;
		mybounds = this.bounds;
//mybounds.debug("initial bounds");
		if(view.tryPerform(\relativeOrigin) ? false) {
			maxpt = Point(0, 0);
		} {
			maxpt = mybounds.leftTop;
		};
		this.children.do({ arg c;
			comparept = c.findRightBottom;
			maxpt = maxpt.max(comparept);
		});
//maxpt.debug("bottom right point");
		if(view.tryPerform(\relativeOrigin) ? false) {
			new = mybounds.resizeTo(maxpt.x + this.decorator.margin.x,
				maxpt.y + this.decorator.margin.y);
		} {
			new = mybounds.resizeTo(maxpt.x - mybounds.left + this.decorator.margin.x,
				maxpt.y - mybounds.top + this.decorator.margin.y);
		};
		this.bounds_(new, reflow: false);	// don't reflow unless asked
//new.debug("set bounds to");
//"<< FlowView:resizeToFitContents - ".post; view.instVarAt(0).postln;
		^new
	}

	recursiveResize {
//">> FlowView:recursiveResize - ".post; view.instVarAt(0).postln;
		this.children.do({ arg c;
			c.recursiveResize;
		});
//"reflowing - ".post; view.instVarAt(0).postln;
		this.tryPerform(\reflowAll);
//"resizing to fit contents - ".post; view.instVarAt(0).postln;
		this.tryPerform(\resizeToFitContents).isNil.if({
//"resizing to fit - ".post; view.instVarAt(0).postln;
			this.tryPerform(\resizeToFit);
		});
//"<< FlowView:recursiveResize - ".post; view.instVarAt(0).postln;
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
		^(c ! this).as(String);
	}
}

//+ Rect {
//	asString { ^"Rect( " ++ left ++ ", " ++ top ++ ", " ++ width ++ ", " ++ height ++ " )" }
//}


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
