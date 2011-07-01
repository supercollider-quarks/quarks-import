
AbstractBend {
	classvar currentBuildSynthDef, ugens;
	
	*use { |func|
		var res;
		currentBuildSynthDef = UGen.buildSynthDef;
		UGen.buildSynthDef = this;
		
		res = func.value;
		
		UGen.buildSynthDef = currentBuildSynthDef;
		^res
	}
	
	
	*bendAllUGens { |... args|
		this.subclassResponsibility(thisMethod)
	}
	
	*extractUGenMethod { |class, selectors|
		selectors.asArray.do { |selector|
			var res = class.findRespondingMethodFor(selector);
			if(res.notNil) { ^res }	
		};
		^nil
	}
	
	// forward necessary methods to SynthDef
	
	*addUGen { |ugen|
		currentBuildSynthDef.addUGen(ugen);
		ugens = ugens.add(ugen);		
	}


	*available {
		^currentBuildSynthDef.available
	}
	
	*available_ { |value|
		currentBuildSynthDef.available_(value)
	}
	
	*addConstant { |value|
		currentBuildSynthDef.addConstant(value)
	}
	
	*constants {
		^currentBuildSynthDef.constants
	}


	
	
}


Bend : AbstractBend {
	
	*new { |bendFunc, ugenFunc| // todo multichannel expand bendFunc
		
		var res = this.use(ugenFunc);
		this.bendAllUGens(bendFunc);
		ugens = nil;
		
		^res
	}
	
	*time { |factor, ugenFunc|
		^this.new({ |argument, argName, ugen|
			if(#[\freq, \rate].includes(argName)) {
				argument * factor.value(argument, argName, ugen)
			} {
				if(#[\dur, \duration, \delaytime, \decaytime].includes(argName)) {
					argument * factor.value(argument, argName, ugen).reciprocal
				} {
					argument	
				}
			}
		}, ugenFunc)
	}
	
	*rand { |coin, bendFunc, ugenFunc|
		^this.new({ |argument, argName, ugen|
			if(coin.value.coin) {
				bendFunc.value(argument, argName, ugen)
			} {
				argument
			}
		}, ugenFunc)
	}
	
	*bendAllUGens { |bendFunc|
		
		ugens.do { |ugen| 
			this.bendUGen(ugen, bendFunc) 
		}
	}
		
	*bendUGen { |ugen, bendFunc|
		
		var inputs, arguments;
		var method, argNames;
		
		method = this.extractUGenMethod(ugen.class.class, [\ar, \kr]);
		argNames =  method.argNames;
		inputs = ugen.inputs;
		if(inputs.isNil or: { argNames.isNil }) { ^this };
		
		argNames.drop(1).do { |argName, argIndex|  // drop "this"
				var argument, replaceArg;
				argument = inputs.at(argIndex);
				if(argument.notNil) {
						replaceArg = bendFunc.value(argument, argName, ugen);
						if(replaceArg !== argument and: { replaceArg.notNil }) {
							ugen.inputs.put(argIndex, replaceArg);
						}
				}
		}	
	}
	
	
		
}


CircuitBend : AbstractBend {
	
	classvar <>excludedUGens = #[\CombN, \AllpassN, \Filter];  // exclude risky UGens
	
	*new { |bendFunc, ugenFunc, size = 16| // todo multichannel expand bendFunc
		
		var res;
		
		var arins = LocalIn.ar(size);
		var krins = LocalIn.kr(size);
		
		res = this.use(ugenFunc);
		
		this.bendAllUGens(bendFunc, arins, krins);
		
		ugens = nil;
		
		^res
		
	}
	
	
	*bendAllUGens { |bendFunc, arins, krins|
		var krugens, arugens;
		
		krugens = ugens.select { |x| x.rate == \control };
		arugens = ugens.select { |x| x.rate == \audio };
		
		ugens.do { |ugen, i|
	
			if(ugen.rate == \audio) {
				this.bendUGen(ugen, bendFunc, arins, i) 
			};
			if(ugen.rate == \control) {
				this.bendUGen(ugen, bendFunc, krins, i)
			};
		};
		
		arugens = Normalizer.ar(arugens);
		arugens = this.shapeOutputs(arugens.asArray, arins.size);
		
		krugens = krugens.collect { |x| x / Amplitude.kr(x) };
		krugens = this.shapeOutputs(krugens.asArray, krins.size);
		
		LocalOut.ar(arugens);
		LocalOut.kr(krugens);
		
	}
	
	*shapeOutputs { |array, size|
		if(array.size >= size) { ^array.keep(size) };
		^if(array.first.rate == \audio) {
			array.extend(size, Silent.ar)
		} {
			array.extend(size, 0.0)
		}
	}
	
	*bendUGen { |ugen, bendFunc, others, index|
			var inputs, controls;
			excludedUGens.do { |class|
				if(ugen.isKindOf(class.asClass)) { ^this }
			};			
			inputs = ugen.inputs;
			if(inputs.isEmpty) { ^this };
			
			inputs.size.do { |i|
				var res = bendFunc.value(index, i, inputs.at(i), others);
				if(res.notNil) { inputs.put(i, res) };
			}
	}
	
	*central { |factor, ugenFunc|
		^this.new({ |i, j, in, others|
			others.scramble.keep(rrand(1, others.size)).mean * factor + in
		}, ugenFunc)
	}
	
	*drift { |factor, rate, ugenFunc|
		^this.new({ |i, j, in, others|
			(others * ({ LFDNoise1.kr(rate).max(0) } ! others.size) * factor).mean + in
		}, ugenFunc)
	}
	
	*controls { |ugenFunc, default = ({ 0.001.rand }), defaultLag, controlPrefix = "bend"|
		^this.new({ |i, j, in, others|
			var control, lagControl;
			if(defaultLag.notNil) {
				lagControl = 	NamedControl.kr(
				"%_lag_%_%".format(controlPrefix, i, j),
				defaultLag ! others.size);
			};
			control = NamedControl.kr(
				"%_%_%".format(controlPrefix, i, j).postln,
				default ! others.size,
				lagControl
			);
			(others * control).sum / (control.sum.max(1)) + in
		}, ugenFunc)
	}
	
	*controls1 { |ugenFunc, default = 0.001, defaultLag, controlPrefix = "bend"|
		^this.new({ |i, j, in, others|
			var control, indexControl, lagControl;
			if(defaultLag.notNil) {
				lagControl = 	NamedControl.kr(
				"%_lag_%_%".format(controlPrefix, i, j),
				defaultLag);
			};
			control = NamedControl.kr(
				"%_%_%".format(controlPrefix, i, j).postln,
				default,
				lagControl
			);
			indexControl = NamedControl.kr(
				"%_index_%_%".format(controlPrefix, i, j).postln,
				default,
				lagControl
			);
			// todo: use indexControl to choose input
			others.sum * control + in
		}, ugenFunc)
	}
		
}
