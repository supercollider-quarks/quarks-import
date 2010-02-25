/* Code partially adapted from "Elements of Computer Music", F. Richard Moore, Prentice-Hall, 1990.  
 * Translation to SC and all other functionality by Michael Dzjaparidze, 2010.
 */
FilterCoef {
	var <>poles, <>zeros, <>acoefs, <>bcoefs;
	
	*new { arg poles, zeros;
		^super.new.init(poles, zeros)
	}
	
	init { arg poles, zeros; var angle;
		//Check and parse input args
		if(poles != nil, {
			if(poles.isKindOf(Array), {
				poles.do({ arg pole;
					if(pole.isKindOf(Polar), {
						angle = pole.angle.wrap(-pi, pi);
						this.poles = this.poles.add(Polar.new(pole.magnitude, angle));
						//Post a warning if the new pole magnitude is > 1.0
						if(pole.magnitude > 1.0, {
							postf("WARNING: Pole magnitude is larger than 1.0.\n")
						})
					}, {
						Error("Input is not in required format.\n").throw 
					})
				});
				this.poles = this.poles.insert(0, Polar.new(nil, nil))   //Insert dummy pole
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
					}, {
						Error("Input is not in required format.\n").throw 
					})
				});
				this.zeros = this.zeros.insert(0, Polar.new(nil, nil))   //Insert dummy zero
			}, {
				Error("Input is not in required format.\n").throw
			})
		})
	}
	
	*calc { arg poles, zeros, norm = true;
		^this.new(poles, zeros).calc(norm)
	}
	
	calc { arg norm = true; var cnegmult, cadd, getcoef;
		if(poles != nil and: { zeros != nil }, {
			//Return complex product of -a and b
			cnegmult = { |a, b|
				var v = Polar.new(a.magnitude.neg * b.magnitude, a.angle + b.angle);
			
				//Negate magnitude when the phase of the result is pi
				if((v.angle.abs - pi).abs < 1e-6, {
					v = Polar.new(v.magnitude.neg, 0.0)
				});
				v
			};
			
			//Return complex sum of a and b
			cadd = { |a, b|
				var v = (a + b).asPolar;
			
				//Negate magnitude when the phase of the result is pi
				if((v.angle.abs - pi).abs < 1e-6, {
					v = Polar.new(v.magnitude.neg, 0.0)
				});
				v
			};
		
			/* Multiply polynomial factors of form (1 - root[i]*x) to find coefficients. Note that 			 * these coefficients may not be real if complex conjugate poles or zeros do not 			 * always occur in conjugate pairs
			 */
			getcoef = { |coef, root|
				//Recursive multiplication to find polynomial coefficients
				(coef.size-1).do({ arg i; i = i + 1;
					coef[i] = cnegmult.value(root[i], coef[i-1]);
					block { |break|
						i.reverseDo({ arg j;
							if(j >= 1, {
								coef[j] = cadd.value(coef[j], cnegmult.value(root[i], 									coef[j-1]));
							}, {
								break.value
							})
						})
					}
				});
			};
			
			acoefs = Array.newClear(zeros.size);
			bcoefs = Array.newClear(poles.size);
			acoefs[0] = Polar.new(1.0, 0.0);
			bcoefs[0] = Polar.new(1.0, 0.0);
			
			getcoef.value(acoefs, zeros);	//Find the a coefficients
			getcoef.value(bcoefs, poles);	//Find the b coefficients
			
			//If norm is set to true, normalize the frequency response to 1.0
			if(norm, { acoefs = acoefs * this.returnMaxMag.reciprocal });

			^[acoefs.select({ |item| item.magnitude.abs > 0.0 }).magnitude, 				bcoefs.select({ |item, i| i > 0 and: { item.magnitude.abs > 0.0 } }).magnitude]
		}, {
			Error("There are no poles and/or zeros specified.\n").throw
		})
	}
	
	calcImpResp { var imp = Array.fill(80, { arg i; if(i == 0, { 1 }, { 0 }) }), impResp = 	Array.new, poll = Array.fill(3, { 0.1 }), i = 0;
		//Run the while loop as long as there is a noticeable response and i < 60
		while({ poll.abs.sum > 0.001 and: { i < 60 } }, {
			impResp = impResp.add(0);
			acoefs.size.do({ arg j;
				if(imp[i-j] == nil, {
					impResp[i] = impResp[i] + 0
				}, {
					impResp[i] = impResp[i] + (imp[i-j] * acoefs[j].magnitude)
				})
			});
			(bcoefs.size-1).do({ arg j; j = j + 1;
				if(imp[i-j] == nil, {
					impResp[i] = impResp[i] - 0
				}, {
					impResp[i] = impResp[i] - (impResp[i-j] * bcoefs[j].magnitude)
				})
			});
			i = i + 1;
			if(i >= 3, { poll = impResp[(i-3)..i] })
		});
		^impResp
	}
	
	//PRIVATE METHODS
	
	//Returns the maximum magnitude in the frequency response of the filter. Can be used for the a0 	//coefficient to normalize the frequency response to 1.0
	returnMaxMag { var func;
		//Function to extract maximum from (In this case a modified version of the method to 		//calculate the frequency response of the filter, see calcFreqResponse in ZPlane)
		func = { arg omega; var den, num, amp, dist;
			//Return the distance from pole/zero location to frequency omega on the unit circle
			dist = { arg omega, item; var x, y;
				x = item.real - cos(omega);	//X dist from pole/zero to freq omega on unit circle
				y = item.imag - sin(omega);	//Y dist from pole/zero to freq omega on unit circel
		
				sqrt((x*x) + (y*y))		//Return magnitude of the distance
			};
			den = num = 1.0;
			zeros.select({ |item, i| i > 0 }).do({ arg zero; 
				num = num * dist.value(omega, zero);
			});
			poles.select({ |item, i| i > 0 }).do({ arg pole;
				den = den * dist.value(omega, pole);
			});
			if(den != 0.0, { 
				amp = num / den 
			}, { 
				if(num >= 0.0, { amp = inf }, { amp = inf.neg }) 
			});
			amp.neg	//Negate result because Golden minimizes a function and we want the maximum
		};
		//Golden returns the independant var (a freq in our case). Evaluating the frequency 		//response at this frequency and negating the result gives us the maximum magnitude in the 		//frequency response of the filter
		^func.value(Golden.bracket(0, pi, func).minimize(func)).neg;
	}
}