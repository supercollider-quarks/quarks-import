/* Code adapted from "Numerical Recipes 3rd edition" Cambridge Press, 2007. Translation to SC done 
 * by Michael Dzjaparidze, 2010
 */
BracketMethod {
	classvar >ax, >bx, >cx, >fa, >fb, >fc, >thisFunc;
	
	*bracket { arg a, b, func; var gold = 1.618034, gLimit = 100.0, tiny = 1e-20, fu, fa, fb, r, q, 	u, ulim, ux, temp;
		//Check and parse input args
		if(a.isKindOf(SimpleNumber) and: { b.isKindOf(SimpleNumber) } and: 		{ func.isKindOf(Function) }, {
			thisFunc = func;
			ax = a; bx = b;
			fa = thisFunc.value(ax);
			fb = thisFunc.value(bx);
			if(fb > fa, { fb = fa; fa = thisFunc.value(bx); ax = b; bx = a });
			cx = bx + (gold * (bx - ax));
			fc = thisFunc.value(cx);
			while({ fb > fc }, {
				r = (bx - ax) * (fb - fc);
				q = (bx - cx) * (fb - fa);
				u = bx - ((((bx-cx)*q) - ((bx-ax)*r)) / (2.0*((q-r).abs.max(tiny)*(q-r).sign)));
				ulim = bx + (gLimit * (cx - bx));
				if(((bx - u) * (u - cx)) > 0.0, {
					fu = thisFunc.value(u);
					if(fu < fc, {
						ax = bx; bx = u; fa = fb; fb = fu;
						^this;
					}, { 
						if(fu < fb, {
							cx = u; fc = fu;
							^this;
						})
					});
					ux = cx + (gold * (cx - bx));
					fu = thisFunc.value(u)
				}, {
					if(((cx - u) * (u - ulim)) > 0.0, {
						fu = thisFunc.value(u);
						if(fu < fc, {
							temp = BracketMethod.shft3(bx, cx, u, u + (gold * (u - cx)));
							bx = temp[0]; cx = temp[1]; u = temp[2];
							temp = BracketMethod.shft3(fb, fc, fu, thisFunc.value(u));
							fb = temp[0]; fc = temp[1]; fu = temp[2];
						})
					}, {
						if(((u - ulim) * (ulim - cx)) >= 0.0, {
							u = ulim;
							fu = thisFunc.value(u);
						}, {
							u = cx + (gold * (cx - bx));
							fu = thisFunc.value(u)
						})
					})
				});
				temp = BracketMethod.shft3(ax, bx, cx, u);
				ax = temp[0]; bx = temp[1]; cx = temp[2];
				temp = BracketMethod.shft3(fa, fb, fc, fu);
				fa = temp[0]; fb = temp[1]; fc = temp[2];
			})
		}, {
			Error("Input is not in required format.\n").throw
		})
	}
	
	*shft2 { arg a, b, c;
		a = b;
		b = c;
		^[a, b]
	}
	
	*shft3 { arg a, b, c, d;
		a = b;
		b = c;
		c = d;
		^[a, b, c]
	}
	
	*mov3 { arg a, b, c, d, e, f;
		a = d;
		b = e;
		c = f;
		^[a, b, c]
	}
}

Golden : BracketMethod {
	classvar xmin, fmin, tol = 3e-8;
	
	*minimize { arg func; var r = 0.61803399, c = 1.0 - r, x1, x2, x0 = ax, x3 = cx, f1, f2, temp;
		if(func.isKindOf(Function), {
			if((cx - bx).abs > (bx - ax).abs, {
				x1 = bx;
				x2 = bx + (c * (cx - bx));
			}, {
				x2 = bx;
				x1 = bx - (c * (bx - ax));
			});
			f1 = thisFunc.value(x1);
			f2 = thisFunc.value(x2);
			while({ (x3 - x0).abs > (tol * (x1.abs + x2.abs)) }, {
				if(f2 < f1, {
					temp = super.shft3(x0, x1, x2, (r * x2) + (c * x3));
					x0 = temp[0]; x1 = temp[1]; x2 = temp[2];
					temp = super.shft2(f1, f2, thisFunc.value(x2));
					f1 = temp[0]; f2 = temp[1];
				}, {
					temp = super.shft3(x3, x2, x1, (r * x1) + (c * x0));
					x3 = temp[0]; x2 = temp[1]; x1 = temp[2];
					temp = super.shft2(f2, f1, thisFunc.value(x1));
					f2 = temp[0]; f1 = temp[1];
				})
			});
			if(f1 < f2, {
				xmin = x1;
				fmin = f1
			}, {
				xmin = x2;
				fmin = f2
			})
			^xmin
		}, {
			Error("Input is not in required format.\n").throw
		})
	}
}
			