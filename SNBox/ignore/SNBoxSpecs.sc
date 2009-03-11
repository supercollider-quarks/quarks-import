// 08 2006 - blackrain at realizedsound dot net

KrSNBoxSpec : ControlSpec {
	*initClass {
		specs.addAll(
			[
				\unipolarSNBox -> this.new(0, 1),
				\bipolarSNBox	-> this.new(-1, 1, default: 0),
	
				\freqSNBox	-> this.new(20, 20000, \exp, 0, 440, units: " Hz"),
				\ffreqSNBox	-> this.new(20, 20000, \exp, 0, 440, units: " Hz"),
				\lofreqSNBox	-> this.new(0.1, 100, \exp, 0, 6, units: " Hz"),
				\midfreqSNBox	-> this.new(25, 4200, \exp, 0, 440, units: " Hz"),
				\widefreqSNBox	-> this.new(0.1, 20000, \exp, 0, 440, units: " Hz"),
				\phaseSNBox	-> this.new(0, 2pi),
				\rqSNBox		-> this.new(0.01, 5, \exp, 0, 0.8),
	
				\audiobusSNBox	-> this.new(0, 128, step: 1),
				\controlbusSNBox	-> this.new(0, 4096, step: 1),
	
				\midiSNBox	-> this.new(0, 127, default: 64),
				\midinoteSNBox	-> this.new(0, 127, default: 60),
				\midivelocitySNBox	-> this.new(1, 127, default: 64),
				
				\dbSNBox		-> this.new(0.ampdb, 1.ampdb, \db, units: " dB"),
				\ampSNBox		-> this.new(0, 1, \amp, 0, 0.1),
				\boostcutSNBox	-> this.new(-20, 20, units: " dB"),
				
				\panSNBox		-> this.new(-1, 1, default: 0),
				\detuneSNBox	-> this.new(-20, 20, default: 0, units: " Hz"),
				\rateSNBox	-> this.new(0.125, 8, \exp, 0, 1),
				\beatsSNBox	-> this.new(0, 20, units: " Hz"),
				
				\delaySNBox	-> this.new(0.001, 1.2, \exp, 0, 0.45, units: " secs"),
				\bigDelaySNBox	-> this.new(0.001, 5.0, \exp, 0, 0.45, units: " secs"),
				\delayDecaySNBox	-> this.new(0.001, 20, \exp, 0, 2.0, units: " secs")
			]);
	}
	defaultControl { arg val; 
		^KrSNBoxEditor.new(this.constrain(val ? this.default), this) 
	}
	rate { ^\control }
}

