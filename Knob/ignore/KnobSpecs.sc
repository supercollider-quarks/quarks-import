// 08 2006 - blackrain at realizedsound dot net

KrKnobSpec : ControlSpec {
	*initClass {
		specs.addAll(
			[
				\unipolarKnob	-> this.new(0, 1),
				\bipolarKnob	-> this.new(-1, 1, default: 0),
	
				\freqKnob		-> this.new(20, 20000, \exp, 0, 440, units: " Hz"),
				\ffreqKnob	-> this.new(20, 20000, \exp, 0, 440, units: " Hz"),
				\lofreqKnob	-> this.new(0.1, 100, \exp, 0, 6, units: " Hz"),
				\midfreqKnob	-> this.new(25, 4200, \exp, 0, 440, units: " Hz"),
				\widefreqKnob	-> this.new(0.1, 20000, \exp, 0, 440, units: " Hz"),
				\phaseKnob	-> this.new(0, 2pi),
				\rqKnob		-> this.new(0.01, 5, \exp, 0, 0.8),
	
				\audiobusKnob	-> this.new(0, 128, step: 1),
				\controlbusKnob	-> this.new(0, 4096, step: 1),
	
				\midiKnob		-> this.new(0, 127, default: 64),
				\midinoteKnob	-> this.new(0, 127, default: 60),
				\midivelocityKnob	-> this.new(1, 127, default: 64),
				
				\dbKnob		-> this.new(0.ampdb, 1.ampdb, \db, units: " dB"),
				\ampKnob		-> this.new(0, 1, \amp, 0, 0.1),
				\boostcutKnob	-> this.new(-20, 20, units: " dB"),
				
				\panKnob		-> this.new(-1, 1, default: 0),
				\detuneKnob	-> this.new(-20, 20, default: 0, units: " Hz"),
				\rateKnob		-> this.new(0.125, 8, \exp, 0, 1),
				\beatsKnob	-> this.new(0, 20, units: " Hz"),
				
				\delayKnob	-> this.new(0.001, 1.2, \exp, 0, 0.45, units: " secs"),
				\bigDelayKnob	-> this.new(0.001, 5.0, \exp, 0, 0.45, units: " secs"),
				\delayDecayKnob	-> this.new(0.001, 20, \exp, 0, 2.0, units: " secs")
			]);
	}
	defaultControl { arg val; 
		^KrKnobEditor.new(this.constrain(val ? this.default), this) 
	}
	rate { ^\control }
}

