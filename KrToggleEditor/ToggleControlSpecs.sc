// 08 2006 - blackrain at realizedsound dot net

ToggleControlSpec : ControlSpec {

	*initClass {
		specs.addAll(
		 [
		 	\boolean -> this.new,
		 	\toggle -> this.new,
			\bypass -> this.new
			];
		)
	}
	defaultControl { arg val; 
		^KrToggleEditor.new(this.constrain(val ? this.default)) 
	}
	rate { ^\control }
}

