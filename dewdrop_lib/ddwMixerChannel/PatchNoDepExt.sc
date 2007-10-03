//
//+ Patch {
//	createArgs { arg argargs;
//		this.prCreateArgs(argargs, true);
//	}
//	prCreateArgs { arg argargs, addDependants = true;
//		var argsSize;
//		argsForSynth = [];
//		argNamesForSynth = [];
//		patchIns = [];
//		synthPatchIns = [];
//		argsSize = this.instr.argsSize;
//		synthArgsIndices = Array.newClear(argsSize);
//		
//		args=Array.fill(argsSize,{arg i; 
//			var proto,spec,ag,patchIn,darg;
//			spec = instr.specs.at(i);
//			ag = 
//				argargs.at(i) // explictly specified
//				?? 
//				{ //  or auto-create a suitable control...
//					darg = instr.initAt(i);
//					if(darg.isNumber,{
//						proto = spec.defaultControl(darg);
//					},{
//						proto = spec.defaultControl;
//					});
//					proto
//				};
//				
//			patchIn = PatchIn.newByRate(spec.rate);
//			patchIns = patchIns.add(patchIn);
//
//			// although input is control, arg could overide that
//			if(spec.rate != \scalar
//				and: {ag.rate != \scalar}
//			,{
//				argsForSynth = argsForSynth.add(ag);
//				argNamesForSynth = argNamesForSynth.add(this.argNameAt(i));
//				synthPatchIns = synthPatchIns.add(patchIn);
//				synthArgsIndices.put(i,synthPatchIns.size - 1);
//			},{
//				// watch scalars for changes. 
//				// if Env or Sample or quanity changed, synth def is invalid
//				addDependants.if({ ag.addDependant(this); });
//			});
//			ag		
//		});
//	}
//}
