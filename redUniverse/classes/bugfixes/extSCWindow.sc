//bugfixes for redUniverse

+SCWindow {
	setInnerExtent { arg w,h; // resize window keeping top left corner fixed
		var b;
		b = this.bounds;
		w = w ? b.width;
		h = h ? b.height;
		this.bounds = Rect(b.left,b.top + (b.height - h), w,h);
	}
}
+JSCWindow {
	setInnerExtent { arg w,h; // resize window keeping top left corner fixed
		var b;
		b = this.bounds;
		w = w ? b.width;
		h = h ? b.height;
		this.bounds = Rect(b.left,b.top + (b.height - h), w,h);
	}
}
