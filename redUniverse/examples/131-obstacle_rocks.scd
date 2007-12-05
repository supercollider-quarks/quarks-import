//test of many obstacles.

(
	var width= 300, height= 300, w, wgrav, world, n= 6,
		rocks= 10;
	
	//--world
	w= GUI.redWindow.new("rocks test", Rect(128, 64, width, height)).front;
	wgrav= RedVector2D[0, 0.98];					//world xy gravity
	world= RedWorld3(RedVector2D[width, height], wgrav, 15, 0.5); //dim,grav,maxvel,damp
	
	//--objects
	{
		var loc= RedVector2D[250, 10].rand;			//initial xy location
		var vel= RedVector2D[3.5, 0].rand;			//initial velocity
		var acc= RedVector2D[0, 0];					//initial acceleration
		RedObject(world, loc, vel, acc, 1.5, 10); //world,loc,vel,acc,mass,size
	}.dup(n);
	{RedRock(world, RedVector2D[width.rand, height].rand)}.dup(rocks);
	
	//--loop
	w.draw{
		world.objects.do{|o, i|						//iterate all objects in this world
			o.addForce(o.world.gravity);			//add gravity
			world.objects.do{|oo, j| if(i!=j, {o.collide(oo)})};
			o.update;								//object update
			world.contain(o);						//keep within world borders
			
			//--render
			if(o.isKindOf(RedRock), {
				GUI.pen.fillColor_(Color.blue(1, 0.5));
				GUI.pen.fillOval(Rect.aboutRedObject2D(o));
			}, {
				GUI.pen.strokeColor_(Color.grey(0.7, 0.5));
				GUI.pen.strokeOval(Rect.aboutRedObject2D(o));
			});
		}
	}.play;
)
