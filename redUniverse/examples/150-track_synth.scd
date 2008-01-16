//if preferred freq match with the human +-100 then play sound and move towards target
//sound: audio input, matching agent's sounds

(
s.waitForBoot{
	var width= 640, height= 480, w, world, n= 20,
		syn, buf, bus, human;
	
	//--world
	Routine{
	w= GUI.redWindow.new("track test1", Rect(128, 64, width, height)).front;
	world= RedWorld3(RedVector2D[width, height], damping:0.5); //dim,grav,maxvel,damp
	
	//--synthesis
	SynthDef(\beating, {|out= 0, freq= 400, beat= 1, lp= 800, amp= 0, pan= 0|
		var z;
		z= RLPF.ar(Mix(LFSaw.ar(freq+[0, beat], 0, amp)), lp);
		Out.ar(out, Pan2.ar(z, pan));
	}).send(s);
	buf= Buffer.alloc(s, 2048, 1);
	SynthDef(\human, {|out, bufnum| Out.kr(out, RedTrack.kr(AudioIn.ar, bufnum))}).send(s);
	1.wait;
	syn= Synth(\human, [\out, 10, \bufnum, buf.bufnum]);//output 6 ch control data from tracker
	bus= Bus(\control, 10, 6);						//access control data at ch 10-15
	
	//--objects
	{|i|
		var loc= RedVector2D[width, height].rand;		//initial xy location
		var mass= 2.rrand(20);
		var a= RedAgent(world, loc, RedVector2D[0.001, 0.001].rand2, nil, mass, mass); //world,loc,vel,acc,mass,size
		a.maxForce_(1);
		a.wdistance_(2);
		a.wchange_(0.9);
		a.state_((
			\synth: Synth(\beating),
			\prefFreq: mass.linexp(2, 20, 2000, 200),	//a favourite pitch depends on mass
			\target: nil							//location for that pitch if found
		));
		a.sense_{
			var listen= human.state.freq;
			if(listen>0 and:{(a.state.prefFreq-listen).abs<100}, {
				a.state.target= human.loc;
				a.state[\synth].set(
					\freq, a.state.prefFreq,
					\amp, 1/n*(width-a.loc.distance(human.loc)/width).pow(2)
				);
			}, {
				a.state[\synth].set(\amp, 0);
				a.state.target= nil;
				a.addForceWander2D;
			});
		};
		a.act_{
			if(a.state.target.notNil, {
				a.addForceArrive(a.state.target);
			});
		}
		
	}.dup(n);
	human= RedAgent(world, RedVector2D[100, 100]);
	human.state_((
		\event: 0,
		\deltax: 0,
		\freq: 0,
		\amp: 0,
		\timbre: 0,
		\centroid: 0
	));
	human.sense_{									//listen and update state
		bus.getn(6, {|arr|
			human.state.event= arr[0];
			human.state.deltax= arr[1];
			human.state.freq= arr[2];
			human.state.amp= arr[3];
			human.state.timbre= arr[4];
			human.state.centroid= arr[5];
		})
	};
	
	//--loop
	w.draw{
		world.objects.do{|o, i|						//iterate all objects in this world
			human.loc= w.mouse;
			o.update;								//object update
			world.contain(o);						//keep within world borders
			
			//--render
			if(o.state.event.notNil, {
				GUI.pen.fillColor_(Color.blue(0.7, 0.5));
			}, {
				if(o.state.target.notNil, {
					GUI.pen.fillColor_(Color.yellow);
				}, {
					GUI.pen.fillColor_(Color.grey(0.7, 0.5));
				});
			});
			GUI.pen.fillOval(Rect.aboutRedObject2D(o));
		};
		//human.state.freq.postln;
	}.play;
	w.onClose_{
		syn.free;
		bus.free;
		buf.free;
		world.objects.do{|o| o.state.at(\synth).free};
	};
	}.play(AppClock);
})
