
/*
it woud be possible to do this outside a SynthDef, then UGen would automatically produce this representation. (UGen.buildSynthDef == nil)

or: a node proxy could register the structure!?
put a pseudo SynthDef in.

*/

UGenNode : UGen {
	var <>ugenClass, <>selector, <>arguments;
	
	*initClass {
		"UGenNode extension: overriding Meta_UGen:multiNew method, and Meta_Control:names".postln;
	}
	
	*new { |ugenClass, selector, arguments|
		^super.new.ugenClass_(ugenClass).selector_(selector).arguments_(arguments)
	}
	asUGenInput {
		^ugenClass.performList(selector, arguments)
	}
	
	composeUnaryOp { arg aSelector;
		^UnaryOpUGenNode(aSelector, this)
	}
	composeBinaryOp { arg aSelector, something;
		^BinaryOpUGenNode(aSelector, this, something)
	}
	reverseComposeBinaryOp { arg aSelector, something, adverb;
		^BinaryOpUGenNode(aSelector, something, this)
	}
	composeNAryOp { arg aSelector, anArgList;
		^this.notYetImplemented(thisMethod)
	}
		
	/*reducedArguments {
		var defaultArguments, n = arguments.size;
		var method = ugenClass.class.findRespondingMethodFor(selector);
		method !? {
			defaultArguments = method.prototypeFrame.keep(arguments.size);
			arguments.size.reverseDo { |i| if(arguments[i] == defaultArguments[i]) { n = i } };
			n.postln;
			^arguments.keep(n.neg);
		};
		"no method found".postln;
		^arguments
	}*/
	storeArgs { ^[ugenClass, selector, arguments] }
	
}

UnaryOpUGenNode : UGenNode {
	*new { |selector, operand|
		^super.new(UnaryOpUGen, \new, [selector, operand])
	}

}

BinaryOpUGenNode : UGenNode {
	*new { |selector, op1, op2|
		^super.new(BinaryOpUGen, \new, [selector, op1, op2])
	}
}



ControlUGenNode : UGenNode {
	var <>names = #[];
	
	kr { arg values;
		arguments = values.asArray;
		selector = \kr;
	}
	ir { arg values;
		arguments = values.asArray;
		selector = \ir;
	}
	*kr { arg ugenClass, values;
		^this.new([], ugenClass, \kr, values.asArray)
	}
	*ir { arg ugenClass, values;
		^this.new([], ugenClass, \ir, values.asArray)
	}
	asUGenInput {
		if(names.notNil) { ugenClass.perform(\names, names) };
		^ugenClass.perform(selector, arguments)
	}
}