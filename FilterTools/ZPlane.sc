/* Code partially adapted from "Elements of Computer Music", F. Richard Moore, Prentice-Hall, 1990.  
 * Translation to SC and all other functionality by Michael Dzjaparidze, 2010.
 */
ZPlane {
	var <>poles, <>zeros, <>freqResp, <>phaseResp, >print, window, pos;
	
	*new { arg poles, zeros, print;
		^super.new.init(poles, zeros, print)
	}
	
	init { arg poles, zeros, print; var angle;
		this.poles = Array.new; 	//Array holding the poles
		this.zeros = Array.new;		//Array holding the zeros
				
		//Check and parse input args
		if(poles != nil, {
			if(poles.isKindOf(Array), {
				poles.do({ arg pole;
					if(pole.isKindOf(Polar), {
						angle = pole.angle.wrap(-pi, pi);
						this.poles = this.poles.add(Polar.new(pole.magnitude, angle));
						//Add conjugate if the angle is not 0.0 or pi
						if(angle.abs != 0.0 and: { angle.abs != pi }, {
							this.poles = this.poles.add(Polar.new(pole.magnitude, angle.neg))
						});
						//Post a warning if the new pole magnitude is > 1.0
						if(pole.magnitude > 1.0, {
							postf("WARNING: Pole magnitude is larger than 1.0.\n")
						})
					}, {
						Error("Input is not in required format.\n").throw 
					})
				})
			}, {
				Error("Input is not in required format.\n").throw
			})
		});
		
		if(zeros != nil, {
			if(zeros.isKindOf(Array), {
				zeros.do({ arg zero;
					if(zero.isKindOf(Polar), {
						angle = zero.angle.wrap(-pi, pi);
						this.zeros = this.zeros.add(Polar.new(zero.magnitude, angle));
						//Add conjugate if the angle is not 0.0 or pi
						if(angle.abs != 0.0 and: { angle.abs != pi }, {
							this.zeros = this.zeros.add(Polar.new(zero.magnitude, angle.neg))
						})
					}, {
						Error("Input is not in required format.\n").throw 
					})
				})
				
			}, {
				Error("Input is not in required format.\n").throw
			})
		});
		this.updNumPolesAndZeros;
		
		if(print.isKindOf(Boolean), {
			this.print = print
		}, {
			if(print == nil, {
				this.print = false
			}, {
				Error("Input is not in required format.\n").throw
			})
		})
	}
	
	addPole { arg index, item;
		if(index.isKindOf(Integer) or: { index == nil }, {
			if(item.isKindOf(Polar) and: { item.magnitude.isKindOf(SimpleNumber) and: 			{ item.angle.isKindOf(SimpleNumber) } }, {
				//Wrap pole angle between -pi and pi
				item = Polar.new(item.magnitude, item.angle.wrap(-pi, pi));
				//Check for duplicate pole zero pairs
				if(zeros.detect(_ == item).notNil, { 
					postf("WARNING: There is already a zero at this loc. Pole removed.\n"); 
				}, {
					//If the index arg is nil, add the pole to the tail of the array
					if(index == nil or: { poles[index] == nil }, {
						poles = poles.add(item);
						//Add conjugate when the angle is not 0 or pi radians
						if(item.angle.abs != 0.0 and: { item.angle.abs != pi }, {
							poles = poles.add(Polar.new(item.magnitude, item.angle.neg))
						})
					}, {
						//Else, insert the pole at the specified index into the array
						//Make sure we add the new pole AFTER a conjugate 
						if(poles[index].angle > 0.0, {
							poles = poles.insert(index, item);
							//Add conjugate when the angle is not 0 or pi radians
							if(item.angle.abs != 0.0 and: { item.angle.abs != pi }, {
								poles = poles.insert(index+1, Polar.new(item.magnitude, 									item.angle.neg))
							})
						}, {
							poles = poles.insert(index+1, item);
							//Add conjugate when the angle is not 0 or pi radians
							if(item.angle.abs != 0.0 and: { item.angle.abs != pi }, {
								poles = poles.insert(index+2, Polar.new(item.magnitude, 									item.angle.neg))
							})
						})
					});
					//Post a warning if the new pole magnitude is > 1.0
					if(item.magnitude > 1.0, {
						postf("WARNING: Pole magnitude is larger than 1.0.\n")
					});
					this.updNumPolesAndZeros;
					if(window != nil, { window.refresh })
				})
			}, {
				Error("Input is not in required format.\n").throw
			})
		}, {
			Error("Index is not an Integer or nil")
		})
	}
	
	addZero { arg index, item;
		if(index.isKindOf(Integer) or: { index == nil }, {
			if(item.isKindOf(Polar) and: { item.magnitude.isKindOf(SimpleNumber) and: 			{ item.angle.isKindOf(SimpleNumber) } }, {
				//Wrap zero angle between -pi and pi
				item = Polar.new(item.magnitude, item.angle.wrap(-pi, pi));
				//Check for duplicate pole zero pairs
				if(poles.detect(_ == item).notNil, { 
					postf("WARNING: There is already a pole at this loc. Zero removed.\n"); 
				}, {
					//If the index arg is nil, add the zero to the tail of the array
					if(index == nil or: { zeros[index] == nil }, {
						zeros = zeros.add(item);
						//Add conjugate when the angle is not 0 or pi radians
						if(item.angle.abs != 0.0 and: { item.angle.abs != pi }, {
							zeros = zeros.add(Polar.new(item.magnitude, item.angle.neg))
						})
					}, {
						//Else, insert the zero at the specified index into the array
						//Make sure we add the new zero AFTER a conjugate 
						if(zeros[index].angle > 0.0, {
							zeros = zeros.insert(index, item);
							//Add conjugate when the angle is not 0 or pi radians
							if(item.angle.abs != 0.0 and: { item.angle.abs != pi }, {
								zeros = zeros.insert(index+1, Polar.new(item.magnitude, 									item.angle.neg))
							})
						}, {
							zeros = zeros.insert(index+1, item);
							//Add conjugate when the angle is not 0 or pi radians
							if(item.angle.abs != 0.0 and: { item.angle.abs != pi }, {
								zeros = zeros.insert(index+2, Polar.new(item.magnitude, 									item.angle.neg))
							})
						})
					});
					this.updNumPolesAndZeros;
					if(window != nil, { window.refresh })
				})
			}, {
				Error("Input is not in required format.\n").throw
			})
		}, {
			Error("Index is not an Integer or nil")
		})
	}
	
	//Return possibly modified index
	removePole { arg index; var item, cIndex;
		if(index.isKindOf(Integer) and: { poles[index] != nil }, {
			item = poles.removeAt(index);
			//If there is a conjugate pole, remove it too
			if(item.angle.abs != 0.0 and: { item.angle.abs != pi }, {
				cIndex = poles.detectIndex(_ == Polar.new(item.magnitude, item.angle.neg));
				poles.removeAt(cIndex)
			});
			this.updNumPolesAndZeros;
			if(window != nil, { window.refresh });
			if(cIndex == nil, { ^index }, { ^cIndex.min(index) })
		}, {
			Error("Either no pole at this location, or index is not an Integer.\n").throw
		})
	}
	
	//Return possibly modified index
	removeZero { arg index; var item, cIndex;
		if(index.isKindOf(Integer) and: { zeros[index] != nil }, {
			item = zeros.removeAt(index);
			//If there is a conjugate zero, remove it too
			if(item.angle.abs != 0.0 and: { item.angle.abs != pi }, {
				cIndex = zeros.detectIndex(_ == Polar.new(item.magnitude, item.angle.neg));
				zeros.removeAt(cIndex)
			});
			this.updNumPolesAndZeros;
			if(window != nil, { window.refresh });
			if(cIndex == nil, { ^index }, { ^cIndex.min(index) })
		}, {
			Error("Either no zero at this locaction, or index is not an Integer.\n").throw
		})
	}
	
	removeAllPoles {
		if(poles.size > 0, { poles = Array.new })
	}
	
	removeAllZeros {
		if(zeros.size > 0, { zeros = Array.new })
	}
	
	setPole { arg index, newLoc;
		index = this.removePole(index);
		this.addPole(index, newLoc);
	}
	
	setZero { arg index, newLoc;
		index = this.removeZero(index);		
		this.addZero(index, newLoc);
	}
		
	getPoles { 
		if(poles.size > 0, {
			^poles
		}, {
			^nil
		})
	}
	
	getZeros { 
		if(zeros.size > 0, {
			^zeros
		}, {
			^nil
		})
	}
	
	getPolesAndZeros {
		if(poles.size > 0 and: { zeros.size > 0 }, {
			^[poles, zeros]
		}, {
			if(poles.size > 0 and: { zeros.size == 0 }, {
				^[poles, nil]
			}, {
				if(poles.size == 0 and: { zeros.size > 0 }, {
					^[nil, zeros]
				}, {
					^[nil, nil]
				})
			})
		})
	}
	
	//Calculate frequency response of the filter
	calcFreqResp { arg res = 512, norm = true; var den, num;
		if(res.isKindOf(Integer) and: { norm.isKindOf(Boolean) }, {
			freqResp = Array.new;
			res.do({ arg i; var omega, mag;
				den = num = 1.0;
				omega = (i/(res-1)) * pi;
				//Calculate sum of numerators
				zeros.do({ arg zero;
					num = num * this.dist(omega, zero)
				});
				//Calculate sum of denominators
				poles.do({ arg pole; 
					den = den * this.dist(omega, pole)
				});
				if(den != 0.0, { 
					mag = num / den 
				}, { 
					if(num >= 0.0, { mag = inf }, { mag = inf.neg }) 
				});
				freqResp = freqResp.add(mag)
			});
			//If norm is true, normalize the frequency response
			if(norm, {
				freqResp = freqResp * freqResp.maxItem.reciprocal
			});
			^freqResp
		}, {
			Error("Input is not in required format.\n").throw
		})	
	}
	
	//Calculate phase response of the filter
	calcPhaseResp { arg res = 512, unwrap = true, derivative = true; var phase;
		if(res.isKindOf(Integer) and: { unwrap.isKindOf(Boolean) }, {
			phaseResp = Array.new;
			res.do({ arg i; var omega;
				phase = 0.0;
				omega = (i/(res-1)) * pi;
				//Calculate sum of zero angles
				zeros.do({ arg zero;
					phase = phase + this.angle(omega, zero)
				});
				//Subtract sum of pole angles from zero angles
				poles.do({ arg pole;
					phase = phase - this.angle(omega, pole)
				});
				phaseResp = phaseResp.add(phase)
			});
			//If unwrap is true, unwrap the phase response (could be done more efficient???)
			if(unwrap, {
				(phaseResp.size-1).do({ arg i; i = i + 1;
					while({ (phaseResp[i] - phaseResp[i-1]).abs > pi }, {
						if((phaseResp[i] - phaseResp[i-1]) > 0.0, {
							phaseResp[i] = phaseResp[i] - 2pi }, {
							phaseResp[i] = phaseResp[i] + 2pi
						})
					})
				})
			});
			if(derivative, {
				^[phaseResp, phaseResp.differentiate[1..phaseResp.lastIndex]]
			}, {
				^phaseResp
			})
		}, {
			Error("Input is not in required format.\n").throw
		})	
	}
	
	//Calculate frequency and phase response simultaneously (bit more efficient if one requires 	//both responses, thus calling calcFreqResp and calcPhaseResp separately)
	calcFreqAndPhaseResp { arg res = 512, norm = true, unwrap = true, derivative = true; var num, 	den, phase;
		if(res.isKindOf(Integer) and: { norm.isKindOf(Boolean) }, {
			freqResp = Array.new;
			phaseResp = Array.new;
			res.do({ arg i; var omega, mag;
				num = den = 1.0;
				phase = 0.0;
				omega = (i/(res-1)) * pi;
				//Calculate sum of numerators and zero angles
				zeros.do({ arg zero;
					num = num * this.dist(omega, zero);
					phase = phase + this.angle(omega, zero)
				});
				//Calculate sum of denominators and subtract sum of pole angles from zero angles
				poles.do({ arg pole;
					den = den * this.dist(omega, pole);
					phase = phase - this.angle(omega, pole)
				});
				if(den != 0.0, { 
					mag = num / den 
				}, { 
					if(num >= 0.0, { mag = inf }, { mag = inf.neg }) 
				});
				freqResp = freqResp.add(mag);
				phaseResp = phaseResp.add(phase)
			});
			//If norm is true, normalize the frequency response
			if(norm, {
				freqResp = freqResp * freqResp.maxItem.reciprocal
			});
			//If unwrap is true, unwrap the phase response (could be done more efficient???)
			if(unwrap, {
				(phaseResp.size-1).do({ arg i; i = i + 1;
					while({ (phaseResp[i] - phaseResp[i-1]).abs > pi }, {
						if((phaseResp[i] - phaseResp[i-1]) > 0.0, {
							phaseResp[i] = phaseResp[i] - 2pi }, {
							phaseResp[i] = phaseResp[i] + 2pi
						})
					})
				})
			});
			if(derivative, {
				^[freqResp, phaseResp, phaseResp.differentiate[1..phaseResp.lastIndex]]
			}, {
				^[freqResp, phaseResp]
			})
		}, {
			Error("Input is not in required format.\n").throw
		})	
	}
	
	printInfo { arg print;
		if(print.isKindOf(Boolean), {
			this.print = print
		}, {
			Error("Input is not in required format.\n").throw
		})
	}
	
	gui { arg bounds = Rect(50, 320, 300, 300); 
		var zplane, drawzplane, dragcorner, point, index, inRange = false;
		
		drawzplane = { arg me;
			var dimH = me.bounds.height, dimW = me.bounds.width, it = 6;
			Pen.use {
				//Draw the grid
				GUI.pen.width = 0.5;
				it.do({ arg i;
					if(i == it.div(2), { 
						GUI.pen.strokeColor = Color.red }, { 
						GUI.pen.strokeColor = Color.gray(0.2) 
					});
					GUI.pen.line(((dimW/it)*i)@0, ((dimW/it)*i)@dimH);
					GUI.pen.stroke
				});
				it.do({ arg i;
					if(i == it.div(2), { 
						GUI.pen.strokeColor = Color.red }, { 
						GUI.pen.strokeColor = Color.gray(0.2) 
					});
					GUI.pen.line(0@((dimH/it)*i), dimW@((dimH/it)*i));
					GUI.pen.stroke
				});
			
				//Draw the unit circle
				GUI.pen.addArc((dimW/2)@(dimH/2), dimW/3, 0, 2pi);
				GUI.pen.strokeColor = Color.black(0.7);
				GUI.pen.fillColor = Color.gray(0.9, 0.2);
				GUI.pen.draw(4);
				
				//Draw the poles
				if(poles.size > 0, {
					poles.do({ arg pole; var point = pole.asPoint;
						2.do({ arg j;
							if(j == 0, {
								GUI.pen.line(
									(((dimW/2)-(dimW/60)) + ((dimW/3) * point.x))@
									(((dimH/2)-(dimH/60)) - ((dimW/3) * point.y)), 
									(((dimW/2)+(dimW/60)) + ((dimW/3) * point.x))@
									(((dimH/2)+(dimH/60)) - ((dimW/3) * point.y))
								)
							}, {
								GUI.pen.line(
									(((dimW/2)+(dimW/60)) + ((dimW/3) * point.x))@
									(((dimH/2)-(dimH/60)) - ((dimW/3) * point.y)),
									(((dimW/2)-(dimW/60)) + ((dimW/3) * point.x))@
									(((dimH/2)+(dimH/60)) - ((dimW/3) * point.y))
								)
							})
						})
					});
					GUI.pen.strokeColor = Color.black;
					GUI.pen.stroke;
				});
								
				//Draw the zeros
				if(zeros.size > 0, {
					zeros.do({ arg zero; var point = zero.asPoint;
						GUI.pen.addArc(
							((dimW/2) + ((dimW/3) * point.x))@
							((dimH/2) - ((dimH/3) * point.y)),
							dimW/45, 0, 2pi
						)
					});
					GUI.pen.strokeColor = Color.black;
					GUI.pen.fillColor = Color.white;
					GUI.pen.draw(4)
				})
			}
		};
		
		window = GUI.window.new("ZPlane", bounds, resizable: false)
			/*.drawHook_({ 
				zplane.bounds = zplane.bounds.width_(window.view.bounds.width)
					.height_(window.view.bounds.height)
			})*/
			.front;
		window.view.background = Color.gray(0.5);
		window.view.resize = 5;
					
		zplane = GUI.userView.new(window, Rect(0, 0, 300, 300))
			.background_(Color.gray(0.5))
			.drawFunc_(drawzplane)
			.keyDownAction_({ arg me, char, mod, uni, key;
				if(mod == 262401 and: { uni == 16 }, {
					this.addPole(nil, pos);
				}, {
					if(mod == 262401 and: { uni == 26 }, {
						this.addZero(nil, pos);
					})
				});
				if(mod == 262401 and: { uni == 4 }, {
					if((index - poles.size) < 0, {
						this.removePole(index);
					}, {
						this.removeZero(index - poles.size);
					})
				});
				me.refresh
			})
			.mouseDownAction_({ arg me, x, y, mod; var diff;
				pos = (((x@y)-me.bounds.center)/(me.bounds.width/3));
				pos = (pos.x@pos.y.neg).asPolar;
				diff = poles.copy.addAll(zeros.copy).collect({ |item| (item - pos).magnitude });
				index = diff.minIndex;
				if((diff.minItem ? 0.0) < 0.05, { inRange = true }, { inRange = false });
				me.refresh
			})
			.mouseUpAction_({ arg me, x, y, mod;
				if(print, {
					postf("poles: %\nzeros: %\n", poles, zeros)
				});
				me.refresh
			})
			.mouseMoveAction_({ arg me, x, y, mod; var diff;
				pos = (((x@y)-me.bounds.center)/(me.bounds.width/3));
				pos = (pos.x@pos.y.neg).asPolar;
				index = poles.copy.addAll(zeros.copy).collect({ |item| (item - 
					pos).magnitude }).minIndex;
				if(inRange, {
					if((index - poles.size) < 0, {
						this.setPole(index, pos)
					}, {
						this.setZero(index - poles.size, pos)
					})
				});
				me.refresh
			})
			.resize_(5);	//Does the same as the .drawHook
			
		dragcorner = GUI.userView.new(window, Rect(window.view.bounds.width-12, 			window.view.bounds.height-12, 10, 10))
			.background_(Color.gray(0.6))
			.mouseDownAction_({ arg me, x, y, mod;
				point = x@y
			})
			.mouseMoveAction_({ arg me, x, y, mod; var delta;
				delta = (x@y) - point;
				delta = delta.x.asInteger@delta.x.asInteger;
				window.bounds = window.bounds.width_((window.bounds.width+delta.x).max(10))
					.height_((window.bounds.height+delta.y).max(10))
					.top_(if(window.bounds.width > 10, { 
							window.bounds.top-delta.y }, {
							window.bounds.top
						}))
					})
			.resize_(9)
	}
	
	close {
		if(window != nil, { window.close });
	}
	
	//PRIVATE METHODS:
		
	//Return the distance from pole/zero location to frequency omega on the unit circle
	dist { arg omega, item; var x, y;
		if(item.isKindOf(Polar) and: { item.magnitude.isKindOf(SimpleNumber) and: 		{ item.angle.isKindOf(SimpleNumber) } }, {
			x = item.real - cos(omega);	//X dist from pole/zero to freq omega on unit circle
			y = item.imag - sin(omega);	//Y dist from pole/zero to freq omega on unit circel
		
			^sqrt((x*x) + (y*y))		//Return magnitude of the distance
		}, {
			Error("Input is not in required format.\n").throw
		})
	}
	
	//Return the angle between pole/zero location to frequency omega on the unit circle
	angle { arg omega, item;
		if(item.isKindOf(Polar) and: { item.magnitude.isKindOf(SimpleNumber) and: 		{ item.angle.isKindOf(SimpleNumber) } }, {
			^atan2(sin(omega) - item.imag, cos(omega) - item.real)
		}, {
			Error("Input is not in required format.\n").throw
		})
	}
	
	//Check if both arrays contain the same nr. of elements. If not add a pole/zero at 0.0, 0.0
	updNumPolesAndZeros { var norm = Polar.new(0.0, 0.0);
		if(poles.size != zeros.size, {
			if(poles.size > zeros.size, {
				while({ poles.size != zeros.size }, {
					if(poles.detect(_ == norm).notNil, {
						poles.removeAt(poles.detectIndex(_ == norm))
					}, {
						zeros = zeros.insert(0, norm)
					})
				})
			}, {
				while({ poles.size != zeros.size }, {
					if(zeros.detect(_ == norm).notNil, {
						zeros.removeAt(zeros.detectIndex(_ == norm))
					}, {
						poles = poles.insert(0, norm)
					})
				})
			})
		});
		while({ poles.detect(_ == norm).notNil and: { zeros.detect(_ == norm).notNil } }, {
			poles.removeAt(poles.detectIndex(_ == norm));
			zeros.removeAt(zeros.detectIndex(_ == norm))
		});
		if(window != nil, { window.refresh })
	}
}