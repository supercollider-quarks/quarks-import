//an area middle of the screen has super friction - no gravity in this world

(
	var width= 300, height= 300, w, world, acc, vel, loc, obj, fric, ff;
	
	//--world
	w= GUI.redWindow.new("superfriction test", Rect(128, 64, width, height)).front;
	world= RedWorld(RedVector2D[width, height], 0, 30, 0.8); //dim,grav,maxvel,damp
	
	//--objects
	acc= RedVector2D[0, 0];
	vel= RedVector2D[1.5, 1.5];
	loc= RedVector2D[250, 50];
	obj= RedObject(world, loc, vel, acc, 1.5, 10);
	
	fric= Rect(0, height*0.4, width, 40);		//area with super friction
	ff= 1.23;								//amount of friction
	
	//--loop
	w.draw{
		if(fric.containsPoint(obj.loc.asPoint), {
			obj.addForce(obj.frictionForce(ff));
		});
		obj.update;
		world.contain(obj);
		
		//--render
		GUI.pen.fillColor_(Color.green);
		GUI.pen.fillRect(fric);
		GUI.pen.strokeColor_(Color.white);
		GUI.pen.strokeRect(Rect.aboutRedObject2D(obj));
	}.play;
)
