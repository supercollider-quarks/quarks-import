
+ UGen {
	// override original implementation
	*multiNewList { arg args;
		var size = 0, newArgs, results;
		
		/////////////////////
		if(buildSynthDef.isNil) { ^this.asUGenNode(args) };
		/////////////////////
		
		args = args.asUGenInput;
			
		args.do({ arg item; 
			(item.class == Array).if({ size = max(size, item.size) });
		});
		if (size == 0) { ^this.new1( *args ) };
		newArgs = Array.newClear(args.size);
		results = Array.newClear(size);
		size.do({ arg i;
			args.do({ arg item, j;
				newArgs.put(j, if (item.class == Array, { item.wrapAt(i) },{ item }));
			});
			results.put(i, this.multiNewList(newArgs));
		});
		^results
	}
	
	*asUGenNode { arg args = #[];
		^UGenNode.new(this, this.methodSelectorForRate(args[0]), args[1..])
	}
}

+ MulAdd {
	*asUGenNode { arg args = #[];
		if(args[2] == 1 and: { args[3] == 0 }) { ^args[1] };
		^super.asUGenNode(args)
	}
	*ar { arg in, mul = 1.0, add = 0.0;
		^this.multiNew('audio', in, mul, add)
	}
	*kr { arg in, mul = 1.0, add = 0.0;
		^this.multiNew('control', in, mul, add)
	}
}

+ Control {
	*names { arg names;
		var synthDef, index;
		synthDef = UGen.buildSynthDef;
		
		/////////////////////
		if(synthDef.isNil) {
			^this.asUGenNode.names_(names)
		};
		/////////////////////
		
		index = synthDef.controlIndex;
		names = names.asArray;
		names.do { |name, i|
			synthDef.addControlName(
				ControlName(name.asString, index + i, 'control', 
					nil, synthDef.allControlNames.size)
			);
		};
	}
	*asUGenNode { arg args = #[];
		^ControlUGenNode.new(this, this.methodSelectorForRate(args[0]), args[1..])
	}

}
