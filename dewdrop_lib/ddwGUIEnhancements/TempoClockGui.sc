
TempoClockGui : ObjectGui {
		// displays current status of a tempoclock; updates every beat
	classvar 		namewidth = 100, nameheight = 20,
				numheight = 40, numwidth = 70,
				height = 50, width = 500,
				<font;

	var <w, <name, <namev, <bars, <beats, updater;	// counter guis
	var <tempoEditor, <tempoFlow, <mainLayout, <tempoEditGui;
	
	*initClass {
		StartUp.add({ font = GUI.font.new("Helvetica", 24) });
	}
	
	gui { arg lay, bounds ... args;	// must do some things felix doesn't
		var layout;
		mainLayout = layout = this.guify(lay,bounds);	// like save my mainLayout (the MPLayout)
		layout.flow({ arg layout;
			view = layout;
			this.writeName(layout);
			this.performList(\guiBody,[layout] ++ args);
		},bounds).background_(this.background);
		//if you created it, front it
		if(lay.isNil,{ layout.front });
	}

	guify { arg lay, bounds, title;
		
		lay.isNil.if({	// if no window given...
				// use the previously opened TempoClock window or make a new one if needed
			lay = w ?? { w = FixedWidthMultiPageLayout.new("TempoClock", 
				Rect(0, 0, width + 150, height))
			};
		}, {
			mainLayout = lay;
			lay = lay.asPageLayout(title,bounds);
		});
		lay.removeOnClose(this);
		^lay
	}

	guiBody { arg lay, n;
		
		namev.isNil.if({	// only make views if we don't already have them
			name = n ? name ? "";

			tempoFlow = FixedWidthFlowView(lay, Rect.new(0, 0, width, height));

			namev = GUI.staticText.new(tempoFlow, Rect.new(0, 0, namewidth, nameheight))
				.align_(\center);

			tempoEditor = NumberEditor.new(model.tempo*60, [20, 500, \linear, 1]);
			tempoEditGui = tempoEditor.gui(tempoFlow);
			
			tempoEditor.action = { arg v; model.tempo_(v/60) };
			
			this.makeCounter;	// make the bars and beats views
		});

			// fix window
		mainLayout.recursiveResize;
		this.update;	// set initial display value
		namev.string_(name);

		updater.isNil.if({
			updater = Routine.new({ 	// routine to update every beat
				{ model.isRunning }.while({
					this.updateCounter;
					1.wait
				});
			});
				// start it running on the next beat
			model.schedAbs(model.elapsedBeats.ceil, updater);
		});
	}
	
	makeCounter {
		bars = GUI.numberBox.new(tempoFlow, Rect.new(0, 0, numwidth, numheight))
			.font_(font)
			.align_(\right)
			.stringColor_(Color.new255(157, 63, 145));
		beats = GUI.numberBox.new(tempoFlow, Rect.new(0, 0, numwidth, numheight))
			.font_(font)
			.align_(\right)
			.stringColor_(Color.new255(157, 63, 145));
	}		
	
	update { arg obj, changer;
		(changer.isView).if({
			model.tempo_(tempoEditor.value / 60);
		}, {
			(changer != \tempo).if({
				this.updateCounter;
			}, { 
				tempoEditor.value_(model.tempo*60).changed; 
			});
		});
	}
	
	updateCounter {
		(bars.notNil).if({
			{
				model.isRunning.if({
					bars.value = (model.elapsedBeats / model.beatsPerBar).trunc;
					beats.value = (model.elapsedBeats % model.beatsPerBar).trunc;
				});
			}.defer;
		});
	}
	
	remove {
		updater.stop;
		model.removeDependant(this);
		view.notClosed.if({
			view.remove;
			mainLayout.recursiveResize;
		});
		namev = bars = beats = updater = nil;
	}
}
