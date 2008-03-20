
EZKnobEditor : NumberEditor {
	var <>name;
	*new { arg name, value=1.0, spec='amp';
		^super.new.init(value,spec).name_(name);
	}
	guiClass { ^EZKnobEditorGui }
}