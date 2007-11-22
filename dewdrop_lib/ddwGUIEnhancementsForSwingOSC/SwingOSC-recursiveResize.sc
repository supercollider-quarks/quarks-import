
// resize everything in a view's chain

+ JSCContainerView {
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

+ JSCView {
	recursiveResize { ^nil }	// the buck stops here
	
	findRightBottom { ^this.bounds.rightBottom }	// non-recursive: give result to caller

	isActive { ^dataptr.notNil }

	isView { ^true }
}

