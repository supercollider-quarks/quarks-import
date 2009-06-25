RoundView : SCUserView {

	// fix for drawing slightly outside an SCUserView
	// this class doesn't draw the focusring itself,
	// it only handles the resizing.
	
	classvar <>focusRingSize = 3;
	
	var <expanded = true;
	var <shrinkForFocusRing = false; // only when expanded == false
	
	init { |parent, bounds|
		relativeOrigin = true;
		if( parent.isKindOf( SCLayoutView ) ) { expanded = false; };
		super.init( parent, if( expanded ) 
				{ bounds.asRect.insetBy(focusRingSize.neg,focusRingSize.neg) } 
				{ bounds } 
			);
		super.focusColor = Color.clear;
		}
	
	drawBounds { ^if( expanded ) 
			{ this.bounds.moveTo(focusRingSize,focusRingSize); } 
			{ if( shrinkForFocusRing )
				{ this.bounds.insetBy(focusRingSize,focusRingSize)
						.moveTo(focusRingSize,focusRingSize) }
				{ this.bounds.moveTo(0,0); }; 
			}
		}
			
	bounds { ^if( expanded ) 
			{ super.bounds.insetBy(focusRingSize,focusRingSize); } 
			{ super.bounds; }; 
		}
	
	bounds_ { |newBounds| 
		if( expanded ) 
			{ super.bounds = newBounds.asRect.insetBy(focusRingSize.neg,focusRingSize.neg); }
			{ super.bounds = newBounds; } ;
		}
		
	expanded_ { |bool|
		var bnds;
		bnds = this.bounds;
		expanded = bool ? expanded;
		this.bounds = bnds;
		}
		
	shrinkForFocusRing_ { |bool|
		shrinkForFocusRing = bool ? shrinkForFocusRing;
		this.refresh;
		}
		
	}