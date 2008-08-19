//redFrik - released under gnu gpl license

RedMstGUI {
	var	<win, <>dur= 0.25;
	*new {|size= 24, skin|
		^super.new.initRedMstGUI(size, skin);
	}
	initRedMstGUI {|size, skin|
		var	guiPlay, guiPrev, guiNext, guiSection, guiMaxSection,
			guiTempo, guiQuant, guiUser, guiMetro,
			task, fnt, fnt2, colBack, colFore, colBack2, colFore2;
		if(skin.isNil, {
			GUI.skins.put(\redMstGUI, (
				background: Color.red(0.8),
				foreground: Color.black,
				fontSpecs: ["Monaco", 9]
			));
			skin= GUI.skins.redMstGUI;
		});
		skin.postln;
		fnt= GUI.font.new(skin.fontSpecs[0], size);
		colBack= skin.background;
		colFore= skin.foreground;
		fnt2= fnt.copy.size_(9);
		colBack2= colBack.complementary.alpha_(0.3);
		colFore2= colFore.complementary.alpha_(0.7);
		win= GUI.window.new("RedMst", Rect(300, 16, size*9.5+20, size*6+225), false)
			.alpha_(skin.unfocus ? 0.9)
			.front;
		win.view.background_(colBack);
		win.view.decorator= FlowLayout(win.view.bounds);
		win.view.keyDownAction_{|view, char, mod, uni, key|
			switch(uni,
				63235, {RedMst.next},				//<- key
				63234, {RedMst.prev},				//-> key
				27, {RedMst.next},					//escape key
				32, {guiPlay.valueAction_(1-guiPlay.value)}//space
			);
		};
		
		guiPlay= GUI.button.new(win, Rect(0, 0, size*4, size*1.5))
			.canFocus_(false)
			.states_([["play", colFore, colBack], ["stop", colBack, colFore]])
			.action_{|view|
				if(view.value==1, {
					RedMst.play;
				}, {
					RedMst.stop;
				});
			};
		guiPrev= GUI.button.new(win, Rect(0, 0, size*1.5, size*1.5))
			.canFocus_(false)
			.states_([["<", colFore, colBack]])
			.action_{|view|
				RedMst.prev;
			};
		guiNext= GUI.button.new(win, Rect(0, 0, size*1.5, size*1.5))
			.canFocus_(false)
			.states_([[">", colFore, colBack]])
			.action_{|view|
				RedMst.next;
			};
		guiMetro= GUI.userView.new(win, Rect(0, 0, size*1.5, size*1.5))
			.relativeOrigin_(true)
			.drawFunc_{|view|
				var midPnt= Point(view.bounds.height, view.bounds.width)*0.5;
				var inner= view.bounds.height*0.3;
				var outer= view.bounds.height*0.5;
				var slice= 2pi/RedMst.clock.beatsPerBar;
				GUI.pen.color_(colFore);
				RedMst.clock.beatsPerBar.do{|x|
					GUI.pen.addAnnularWedge(
						midPnt,
						inner,
						outer,
						x*slice+1.5pi,
						slice
					)
				};
				GUI.pen.stroke;
				GUI.pen.addAnnularWedge(
					midPnt,
					inner,
					outer,
					1.5pi,
					RedMst.clock.beatInBar/RedMst.clock.beatsPerBar*2pi
				);
				GUI.pen.addAnnularWedge(
					midPnt,
					0,
					inner*0.8,
					1.5pi,
					2pi*(RedMst.quant-(RedMst.clock.nextTimeOnGrid(RedMst.quant)-RedMst.clock.beats))/RedMst.quant;
				);
				GUI.pen.fill;
			};
		win.view.decorator.nextLine;
		GUI.staticText.new(win, Rect(0, 0, size*4, size*1.5)).string_("sect:");
		guiSection= GUI.staticText.new(win, Rect(0, 0, size*2, size*1.5));
		guiMaxSection= GUI.staticText.new(win, Rect(0, 0, size*3.25, size*1.5));
		win.view.decorator.nextLine;
		GUI.staticText.new(win, Rect(0, 0, size*4, size*1.5)).string_("bpm:");
		guiTempo= GUI.staticText.new(win, Rect(0, 0, size*4, size*1.5));
		win.view.decorator.nextLine;
		GUI.staticText.new(win, Rect(0, 0, size*4, size*1.5)).string_("quant:");
		guiQuant= GUI.staticText.new(win, Rect(0, 0, size*1.5, size*1.5));
		win.view.decorator.nextLine;
		guiUser= GUI.userView.new(win, Rect(0, 0, win.bounds.width-7, 200))
			.relativeOrigin_(true)
			.drawFunc_{|view|
				var w, h, str;
				if(RedMst.tracks.notEmpty, {
					w= view.bounds.width/(RedMst.maxSection+1);
					h= view.bounds.height/RedMst.tracks.size;
					GUI.pen.font_(fnt2);
					RedMst.tracks.do{|trk, y|
						GUI.pen.color_(colFore);
						GUI.pen.strokeRect(Rect(0, y*h, view.bounds.width, h*0.9));
						trk.sections.do{|x|
							GUI.pen.fillRect(Rect(x*w, y*h, w, h*0.9));
						};
						str= trk.key.asString+"("++trk.item.class++")";
						GUI.pen.fillColor_(colFore2);
						GUI.pen.stringAtPoint(str, Point(0, y*h));
					};
					GUI.pen.fillColor_(colBack2);
					GUI.pen.fillRect(Rect(RedMst.section*w, 0, w, view.bounds.height-(h*0.1)));
				});
			};
		
		win.view.children.do{|x|
			if(x.respondsTo(\font), {x.font_(fnt)});
			if(x.respondsTo(\stringColor_), {x.stringColor_(colFore)});
		};
		
		task= Routine{
			inf.do{
				{
					guiPlay.value_(RedMst.isPlaying.binaryValue);
					guiSection.string_(RedMst.section);
					guiMaxSection.string_("/"++RedMst.maxSection);
					try{
						guiTempo.string_(RedMst.clock.tempo*60);
					} {
						guiTempo.string_("-");
					};
					guiQuant.string_(RedMst.quant);
					guiMetro.refresh;
					guiUser.refresh;
				}.defer;
				dur.wait;
			};
		}.play(RedMst.clock);
		CmdPeriod.doOnce({
			if(win.isClosed.not, {
				win.close;
				task.stop;
			});
		});
		win.onClose_{task.stop};
	}
}
