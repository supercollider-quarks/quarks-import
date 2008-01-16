//objects shoot out off each other.  with friction and world damping to compensate.

(
	var width= 300, height= 300, w, wgrav, world, n= 18;
	
	//--world
	w= GUI.redWindow.new("bounce test", Rect(128, 64, width, height)).front;
	wgrav= RedVector2D[-0.1, 0.38];
	world= RedWorld3(RedVector2D[width, height], wgrav, 20, 0.92); //dim,grav,maxvel,damp
	
	//--objects
	{|i|
		var mass= 1;
		RedObject(world, RedVector2D[i*20+10, 20], RedVector2D[0.001, 0.001], nil, mass, mass*5); //world,loc,vel,acc,mass,size
	}.dup(n);
	
	//--loop
	w.draw{
		GUI.pen.strokeColor_(Color.grey(0.7, 0.5));
		GUI.pen.width_(1);
		world.objects.do{|o, i|
			o.addForce(world.gravity);
			o.addForce(o.frictionForce(-0.3));
			o.update;
			world.objects.do{|oo, j|
				if(i!=j, {
					if(o.contains(oo), {
						o.addForce(o.gravityForce(oo).neg*100);
					});
				});
			};
			world.contain(o);
			
			//--render
			GUI.pen.strokeOval(Rect.aboutRedObject2D(o));
		};
	}.play;
)
