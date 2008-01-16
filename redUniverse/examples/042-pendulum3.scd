//many pendulums in a world with gravity

(
	var width= 300, height= 300, w, wgrav, world, n= 100,
		ploc;
	
	//--world
	w= GUI.redWindow.new("pendulum test3", Rect(128, 64, width, height)).front;
	wgrav= RedVector2D[0, 0];
	world= RedWorld3(RedVector2D[width, height], wgrav, 20, 0); //dim,grav,maxvel,damp
	
	//--objects
	{
		var loc= RedVector2D[width, height].rand;
		var vel= RedVector2D[0.5, 0.5].rand2;
		RedPendulum(world, loc, vel, 0, 60.rand, 20.rand) //world,loc,vel,acc,mass,size
			.theta_(2pi.rand).angularVel_(0.5.rand);
	}.dup(n);
	
	//--loop
	w.draw{
		GUI.pen.strokeColor_(Color.white);
		GUI.pen.width_(1);
		world.objects.do{|o, i|
			o.addForceAngular2D(world.gravity);
			//o.addForce(world.gravity);
			o.update;
			world.contain(o);
			
			//--render
			ploc= o.pendulumLoc2D.asPoint;
			
			GUI.pen.line(o.loc.asPoint, ploc);
			GUI.pen.strokeOval(Rect.aboutPoint(ploc, o.size/5, o.size/5));
		}
	}.play;
)
