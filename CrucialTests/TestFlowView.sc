
TestFlowView : UnitTest {
	
	test_startRow {
		var f,g,a,b,c;

		g = SCWindow.new;
		//g.front;
		f = FlowView.new(g);
		

		a = SCSlider(f,Rect(0,0,100,100));
		b = SCSlider(f,Rect(0,0,100,100));
		f.startRow;
		c = SCSlider(f,Rect(0,0,100,100));

	
		this.assert( a.bounds.left < b.bounds.left,"b should not be on a new row, it should be to the right of a");
		this.assert( a.bounds.left == c.bounds.left,"a and c should be aligned on the left edge");
		
		g.close;
	}
	
	test_removeAll {
		var f,g;

		g = SCWindow.new;
		//g.front;
		f = FlowView.new(g);

		ActionButton(f,"a");

		SCSlider(f,Rect(0,0,100,100));

		// this is the problem: the start rows don't get removed
		f.startRow;

		ActionButton(f,"next line");

		//
		f.removeAll;
		
		this.assertEquals( f.children.size, 0 , "after removeAll there should be 0 children");
		
		g.close;
	}
	
	/*

w = GUI.window.new;
w.front;

w.view.horz({ |h|
	GUI.staticText.new(h,100@100).string_("horz").background = Color.red;

	GUI.staticText.new(h,100@100).string_("horz").background = Color.red;

	// should be to the right of the other two
	h.scroll({ |s|
		GUI.staticText.new(s,200@200)
			.string_("scroll123456789abcdefghijklmnopqrstuvwxyz").background = Color.blue;
	},100@100)


})

*/

}

