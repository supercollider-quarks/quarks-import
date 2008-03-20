// 06/2006 blackrain at realizedsound dot net
// 03.10.2008:
//	- relative origin
//	- A subclass of SCViewHolder

VuView : SCViewHolder {
	var <value=0;
	*new { arg parent, bounds;
		^super.new.init(parent, bounds);
	}
	init { arg parent, bounds;
		var area;
		
		this.view_(GUI.userView.new(parent, bounds))
			.relativeOrigin_(true);
		
		this.view.drawFunc_({
			var bounds;
			bounds = Rect(0, 0, this.view.bounds.width, this.view.bounds.height);
			// frame
			Color.black.alpha_(0.4).set;
			GUI.pen.width = 2;
			GUI.pen.moveTo(bounds.left @ (bounds.top + bounds.height));
			GUI.pen.lineTo(bounds.left @ bounds.top);
			GUI.pen.lineTo((bounds.left + bounds.width) @ bounds.top);
			GUI.pen.stroke;
	
			Color.white.alpha_(0.4).set;
			GUI.pen.moveTo(bounds.left @ (bounds.top + bounds.height));
			GUI.pen.lineTo((bounds.left + bounds.width) @ (bounds.top +
				bounds.height));
			GUI.pen.lineTo((bounds.left + bounds.width) @ bounds.top);
			GUI.pen.stroke;
	
			// center
			Color.black.alpha_(0.2).set;
			GUI.pen.addWedge(bounds.center.x @ (bounds.top + bounds.height - 1), 
				bounds.height * 0.20, 0, -pi);
			GUI.pen.perform(\fill);
	
			// scale
			Color.black.alpha_(0.2).set;
			GUI.pen.addAnnularWedge(bounds.center.x @
				(bounds.top + bounds.height - 1), 
				bounds.height * 0.8, bounds.height * 0.95, -0.75pi, 0.5pi);
			GUI.pen.perform(\fill);
	
			// dial
			Color.black(0.8, 0.8).set;
			GUI.pen.width = 1;
			GUI.pen.moveTo(bounds.center.x @ (bounds.top + bounds.height - 1));
			GUI.pen.lineTo(Polar.new(bounds.height * 0.95, 
				[-0.75pi, -0.25pi, \linear].asSpec.map(value)).asPoint +
					(bounds.center.x @ (bounds.top + bounds.height)));
			GUI.pen.stroke;
		});
	}

	value_ { arg val;
		value = val;
		this.refresh;
	}
}

/*
VuView : SCUserView  {
	var <value=0;

	*viewClass { ^SCUserView }
	init { arg parent, bounds;
		super.init(parent, bounds);
		this.relativeOrigin_(false);
	}	
	draw {
		// frame
		Color.black.alpha_(0.4).set;
		GUI.pen.width = 2;
		GUI.pen.moveTo(bounds.left @ (bounds.top + bounds.height));
		GUI.pen.lineTo(bounds.left @ bounds.top);
		GUI.pen.lineTo((bounds.left + bounds.width) @ bounds.top);
		GUI.pen.stroke;

		Color.white.alpha_(0.4).set;
		GUI.pen.moveTo(bounds.left @ (bounds.top + bounds.height));
		GUI.pen.lineTo((bounds.left + bounds.width) @ (bounds.top +
			bounds.height));
		GUI.pen.lineTo((bounds.left + bounds.width) @ bounds.top);
		GUI.pen.stroke;

		// center
		Color.black.alpha_(0.2).set;
		GUI.pen.addWedge(bounds.center.x @ (bounds.top + bounds.height - 1), 
			bounds.height * 0.20, 0, -pi);
		GUI.pen.perform(\fill);

		// scale
		Color.black.alpha_(0.2).set;
		GUI.pen.addAnnularWedge(bounds.center.x @
			(bounds.top + bounds.height - 1), 
			bounds.height * 0.8, bounds.height * 0.95, -0.75pi, 0.5pi);
		GUI.pen.perform(\fill);

		// dial
		Color.black(0.8, 0.8).set;
		GUI.pen.width = 1;
		GUI.pen.moveTo(bounds.center.x @ (bounds.top + bounds.height - 1));
		GUI.pen.lineTo(Polar.new(bounds.height * 0.95, 
			[-0.75pi, -0.25pi, \linear].asSpec.map(value)).asPoint +
				(bounds.center.x @ (bounds.top + bounds.height)));
		GUI.pen.stroke;
	}

	value_ { arg val;
		value = val;
		this.refresh;
	}
}
*/

