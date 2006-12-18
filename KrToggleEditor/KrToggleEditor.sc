KrToggleEditor : KrNumberEditor {

	*new { arg val=0;
		^super.new.value_(val)
	}
	guiClass { ^KrToggleEditorGui }

}
