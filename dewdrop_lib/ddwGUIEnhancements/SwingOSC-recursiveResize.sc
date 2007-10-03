
// resize everything in a view's chain

+ JSCContainerView {
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

+ JSCView {
	recursiveResize { // arg level;
	 ^nil }	// the buck stops here
	
	findRightBottom { // arg level;
	^this.bounds.rightBottom }	// non-recursive: give result to caller

	isActive { ^dataptr.notNil }

	isView { ^true }
}

//+ JSCViewHolder {
//	findRightBottom { // arg level;
//	^this.bounds.rightBottom }	// non-recursive: give result to caller
//}
