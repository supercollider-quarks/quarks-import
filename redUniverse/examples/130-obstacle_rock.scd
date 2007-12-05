//simple test of an obstacle.

(
	var width= 300, height= 300, w, wgrav, world, n= 1,
		rock;
	
	//--world
	w= GUI.redWindow.new("rock test", Rect(128, 64, width, height)).front;
	wgrav= RedVector2D[-0.4, 0.98];					//world xy gravity
	world= RedWorld3(RedVector2D[width, height], wgrav, 20, 0.25); //dim,grav,maxvel,damp
	
	//--objects
	{
		var loc= RedVector2D[250, 10];				//initial xy location
		var vel= RedVector2D[3.5, 0];				//initial velocity
		var acc= RedVector2D[0, 0];					//initial acceleration
		RedObject(world, loc, vel, acc, 1.5, 10); //world,loc,vel,acc,mass,size
	}.dup(n);
	rock= RedRock(world, RedVector2D[width.rand, 150.rrand(height)]); //world,loc,vel,acc,mass,size
	rock.mass.postln;								//near inf mass
	
	//--loop
	w.draw{
		GUI.pen.strokeColor_(Color.white);
		world.objects.do{|o, i|						//iterate all objects in this world
			o.addForce(o.world.gravity);			//add gravity
			world.objects.do{|oo, j| if(i!=j, {o.collide(oo)})};
			o.update;								//object update
			world.contain(o);						//keep within world borders
			
			//--render
			GUI.pen.strokeRect(Rect.aboutRedObject2D(o));
		}
	}.play;
)
