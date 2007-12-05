//simple pendulum in a world with gravity

(
	var width= 300, height= 300, w, wgrav, world, n= 1,
		ploc;
	
	//--world
	w= GUI.redWindow.new("pendulum test1", Rect(128, 64, width, height)).front;
	wgrav= RedVector2D[0, 0.98];
	world= RedWorld(RedVector2D[width, height], wgrav, 20, 0.02); //dim,grav,maxvel,damp
	
	//--objects
	{
		var loc= RedVector2D[width/2, height/2];
		RedPendulum(world, loc, nil, nil, 1.5, 50) //world,loc,vel,acc,mass,size
			.theta_(-1).angularVel_(-0.5);
	}.dup(n);
	
	//--loop
	w.draw{
		GUI.pen.strokeColor_(Color.white);
		world.objects.do{|o|
			o.addForceAngular2D(world.gravity);
			//o.addForce(world.gravity);
			o.update;
			//world.contain(o);
			a= o;
			
			//--render
			ploc= o.pendulumLoc2D.asPoint;
			GUI.pen.line(o.loc.asPoint, ploc);
			GUI.pen.strokeOval(Rect.aboutPoint(ploc, o.size/5, o.size/5));
		}
	}.play;
)

/*
//manual control
a.addForceAngular2D(30)
a.addForceAngular2D(5)
a.addForceAngular2D(-5)
*/