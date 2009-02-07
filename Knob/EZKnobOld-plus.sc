
+ EZKnobOld {
	convert {
		("########################
		
		EZKnob( w, " 
			++ this.cv.bounds.asCompileString
			++", "++this.labelView.string.asCompileString
			++", "++this.controlSpec.asCompileString
			++", {}"
			++", knobSize: " ++ this.knobView.bounds.width ++ "@"++ this.knobView.bounds.height
			++", labelHeight: " ++ this.labelView.bounds.height
			++", layout: \\vert"
			++", margin: 2@2"
			++" ).centered_("++ knobView.centered.asCompileString
			++").setColors( background: " ++ this.cv.background.asCompileString++");
			
		######################	
			").postln
	}
}