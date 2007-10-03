
+ Ref {
	draggedIntoVoicerGUI { |dest| value.draggedIntoVoicerGUI(dest) }
}

+ Object {
	draggedIntoVoicerGCGUI { |gui|
		gui.model.spec = this.asSpec;
	}
}
