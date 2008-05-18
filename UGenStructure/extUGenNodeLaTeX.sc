+ UGenNode {

	asLaTeX {
		if(ugenClass == MulAdd) { ^this.maddLaTeX };
		if(ugenClass == Integrator) { ^this.integLaTeX };
		^ugenClass.name ++ "_{" ++ selector 
			++ "}\\left(" 
			++ arguments.collect(_.asLaTeX).join(", ")
			++ " \\right)"
	}
	
	maddLaTeX {
		var res = arguments[0].asLaTeX;
		if(arguments[1] != 1) {
			res = res ++ " \cdot " ++ arguments[1].asLaTeX;
		};
		if(arguments[2] != 0) {
			res = res ++ " + " ++ arguments[2].asLaTeX;
		};
		^res
	}
	
	integLaTeX {
		^"\\int_{" ++ selector ++ "}^{" ++ arguments[1].asLaTeX 
			++ "} {" ++ arguments[0].asLaTeX ++ "} dt"
	}
}

+ UnaryOpUGenNode {
	asLaTeX {
		var op = arguments[0];
		var x = arguments[1].asLaTeX;
		if(op == 'sqrt') { ^"\\sqrt{" ++ x ++ "}"};
		if(op == 'squared') { ^x ++ "^2 "};
		if(op == 'cubed') { ^x ++ "^3 "};
		if(op == 'reciprocal') { ^"\\frac{1}{" ++ x ++ "}"};
		if(op == 'abs') { ^"\\left|{" ++ x ++ "}\\right|"};
		if(op == 'exp') { ^"e^{" ++ x ++ "}"};
		if(op == 'log10') { ^"\\log_{10}{" ++ x ++ "}"};
		if(op == 'log2') { ^"\\log_{2}{" ++ x ++ "}"};
		if(op == 'log') { ^"\\log_{e}{" ++ x ++ "}"};
		if(op == 'floor') { ^"\\lfloor{" ++ x ++ "}\\rfloor"};
		if(op == 'ceil') { ^"\\lceil{" ++ x ++ "}\\rceil"};
		
		^op  ++ " \\left(" ++ x ++ " \\right)"
	}
	
}

+ BinaryOpUGenNode {
	asLaTeX {
		var op = arguments[0];
		if(op == '/') {
			if(arguments[2].isNumber) { 
				^"\\frac{1}{" ++ arguments[2].asLaTeX ++ "} " + arguments[1].asLaTeX 
			};
			^"\\frac {" ++ arguments[1].asLaTeX ++ "}{" ++ arguments[2].asLaTeX ++ "}"
		};
		if(op == '*') {
			^arguments[1].asLaTeX ++ " \\cdot " ++ arguments[2].asLaTeX 
		};
		if(op == 'pow') {
			^"{" ++ arguments[1].asLaTeX ++ "}^{" ++ arguments[2].asLaTeX ++ "}"
		};
		if(op.isBasicOperator) {
			^" \\left(" ++ 
				arguments[1].asLaTeX + op + arguments[2].asLaTeX ++ " \\right)"
		}; 
		^op.asLaTeX  ++ " \\left("  ++ this.argsAsLaTeX ++ " \\right)"
	}
	
	argsAsLaTeX {
		^arguments[1..].collect(_.asLaTeX).join(", ")
	}
}
+ ControlUGenNode {
	asLaTeX {
		var nm = names;
		nm = (nm ?? ["\\dots"]).asArray.extend(arguments.size.max(1), "\\dots");
		nm = nm.collect { |x, i| 
					if(arguments[i].notNil) {
						x = x ++  "^{" ++ arguments[i] ++ "}";
					};
				x
		};
		^nm.asLaTeX
	}
}

+ Collection {
	asLaTeX {
		var items = this.collect(_.asLaTeX);
		var res = "\n \\left[ \\begin{array}{ll}\n";
		res = res ++ items.join("\\\\\n");
		res = res ++ "\\end{array} \\right] \n";
		^res
	}
	asFracLaTeX {
		^"\\frac{" ++ this[0] ++  "}{" ++ this[1] ++ "}"
	}
}

+ Object {
	asLaTeX {
		^this.asCompileString
	}
	asLaTexNodeDoc {
		^"\\documentclass[12pt,a4paper]{article}"
		"\\begin{document}"
		"\\begin{displaymath}"
		++ this.asLaTeX ++
		"\n\\end{displaymath}"
		"\\end{document}"
	}
}

+ Symbol {
	asLaTeX {
		^this.asString
	}
}

+ String {
	asLaTeX {
		^this
	}
}

+ Float {
	asLaTeX {
		var frac;
		frac = (this / pi).asFraction;
		if(frac[0] <= 5 and: { frac[1] <= 5 }) {
			^frac.asFracLaTeX ++ " \\pi"
		};
		frac = this.asFraction;
		if(frac[0] <= 100 and: { frac[1] <= 100 }) {
			^frac.asFracLaTeX
		};
		^this.asString
	}
}

+ NodeProxyUGenNode {
	asLaTeX {
		^ugenClass.keyAsLaTeX
	}
}

+ NodeProxy {
	sourceAsLaTeX {
		var ctl, defArgs, argNames, obj = this.source; // for now only return first object
		^if(obj.isFunction) { 
			argNames = obj.def.argNames;
			defArgs = obj.def.prototypeFrame;
			ctl = Control.names(argNames).kr(defArgs);
			obj.valueArray(ctl).asLaTeX
		} {
			obj.asLaTeX
		}
	}
	
	keyAsLaTeX { |key|
		var rateSym = "", chanSym = "";
		if(this.isNeutral.not) {
				chanSym = this.numChannels;
				rateSym = if(this.rate == \audio) { "ar" } { "kr" };
		};
		key = key ?? { this.key } ? "{}";
		^key ++ "^{" ++ rateSym  ++ "}_{" ++ chanSym ++ "}"
	}
}

+ ProxySpace {
	asLaTeX {
		var keys = envir.keys.asArray.sort;
		var res = "";
		res = res ++ "\\begin{array}{ll}\n";
		keys.do { |key|
			var proxy = this.envir.at(key);
			if(proxy.source.notNil) {
				res = res ++ proxy.keyAsLaTeX(key) + "& = " + proxy.sourceAsLaTeX + "\\\\\n"
			};
		};
		res = res ++ "\\end{array}\n";
		^res
	}
}
