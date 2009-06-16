// this file is part of redUniverse toolkit /redFrik


//--world without walls (wrapping)
RedWorld {
	var <>dim, <>gravity,							//vectors
		<>maxVel, <>damping,						//floats
		<>objects;								//array
	*new {|dim, gravity, maxVel, damping|
		^super.newCopyArgs(
			dim ?? {RedVector2D[300, 300]},			//2d world for defaults
			gravity ?? {RedVector2D[0, 0.98]},
			maxVel ? 10,
			damping ? 0.25
		).initRedWorld;
	}
	initRedWorld {
		objects= [];
		RedUniverse.add(this);						//add world to universe
	}
	add {|redObj|									//add it here and update object's world
		objects= objects.add(redObj);
		redObj.world= this;
	}
	remove {|redObj| objects.remove(redObj)}
	contain {|redObj|								//wrap object around world dim
		if(redObj.loc.any{|x, i| x<0 or:{x>dim[i]}}, {
			redObj.loc= redObj.loc%dim;
		});
	}
	contains {|redObj|								//returns boolean if object inside world dim
		^redObj.loc.any{|l, i| l-redObj.size<0 or:{l+redObj.size>dim[i]}}.not
	}
}

//--world without walls (non wrapping)
RedWorld1 : RedWorld {
	contain {|redObj|								//just not contain object at all
	}
}

//--world with soft walls
RedWorld2 : RedWorld {
	contain {|redObj|								//wrap object around world dim and damp
		if(redObj.loc.any{|x, i| x<0 or:{x>dim[i]}}, {
			redObj.vel= redObj.vel*(1-damping);
			redObj.loc= redObj.loc%dim;
		});
	}
}

//--world with hard walls
RedWorld3 : RedWorld {
	contain {|redObj|								//keep object within world dim - bounce
		redObj.loc.do{|l, i|
			if(l-redObj.size<0 or:{l+redObj.size>dim[i]}, {
				redObj.vel.put(i, redObj.vel[i]*(1-damping).neg);
				redObj.loc.put(i, l.fold(redObj.size, dim[i]-redObj.size));
			});
		}
	}
}
