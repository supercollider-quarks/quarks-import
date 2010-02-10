//redFrik

RedToolsMenu {
	classvar <>list, <>server;
	*initClass {
		server= Server.default;
		list= [
//			['redSys', 'RedDiskInPlayer'], {
//				RedDiskInPlayer.new;
//			},
			['redSys', '~redEfx'], {
				if(~redEfx.isKindOf(RedEffectsRack), {
					"overwrote ~redEfx with a new".warn;
					~redEfx.free;
				});
				~redEfx= RedEffectsRack(
					RedEffectsRack.defaultClasses
				);
				RedEffectsRackGUI(~redEfx);
			},
			['redSys', '~redEfx2'], {
				if(~redEfx2.isKindOf(RedEffectsRack), {
					"overwrote ~redEfx2 with a new".warn;
					~redEfx2.free;
				});
				if(~redEfx.notNil, {
					~redEfx2= RedEffectsRack(RedEffectModule.subclasses, 0, ~redEfx.group);
				}, {
					~redEfx2= RedEffectsRack(RedEffectModule.subclasses);//bus 0, after defaultGroup
				});
				RedEffectsRackGUI(~redEfx2, 595@333);
			},
			['redSys', '~redMatrixMixer'], {
				if(~redMatrixMixer.isKindOf(RedMatrixMixer), {
					"overwrote ~redMatrixMixer with a new".warn;
				});
				~redMatrixMixer= RedMatrixMixer.new;
				RedMatrixMixerGUI(~redMatrixMixer);
			},
			['redSys', '~redMixer'], {
				if(~redMixer.isKindOf(RedMixer), {
					"overwrote ~redMixer with a new".warn;
				});
				~redMixer= RedMixer.new;
				RedMixerGUI(~redMixer);
			},
			['redSys', '~redMixStereo'], {
				if(~redMixStereo.isKindOf(RedMixStereo), {
					"overwrote ~redMixStereo with a new".warn;
				});
				~redMixStereo= RedMixStereo.new;
				RedMixGUI(~redMixStereo);
			},
			['redSys', 'RedTapTempoGUI'], {
				RedTapTempoGUI(TempoClock(1));		//or which clock to use?
			},
			['redSys', 'RedTest'], {
				RedTest.openHelpFile;
			},
			['redSys', 'Redraw'], {
				Redraw.new;
			},
			['redSys', 'redSys overview'], {
				RedSys.openHelpFile;
			},
			['system', 'SynthDescLib read+browse'], {
				SynthDescLib.read.global.browse;
			},
			['system', 'post Event defaults'], {
				Event.default.parent.associationsDo(_.postln);
			},
			['system', 'post path'], {
				Dialog.getPaths{|paths| paths.do{|x| x.postln}};
			},
			['system', 'post specs'], {
				Spec.specs.keysValuesDo{|key, val| if(val.class==ControlSpec, {[key, val].postln})};
			},
			['system', 'post all window postitions'], {
				Window.allWindows.do{|x| x.name.post; "   ".post; x.bounds.postln};
			},
			['system', 'post all document positions'], {
				Document.allDocuments.do{|x| x.title.post; "   ".post; x.bounds.postln};
			},
			['template', 'post incoming osc'], {
				Document(
					"listen to all incoming osc",
					"//start\nthisProcess.recvOSCfunc= {|time, addr, msg| if(msg[0].asString.contains(\"status.reply\").not, {(\"time:\"+time+\"sender:\"+addr+\"\\nmessage:\"+msg).postln})};\n//stop\nthisProcess.recvOSCfunc= nil;"
				).syntaxColorize;
			},
			['template', 'normalize soundfile'], {
				Document(
					"normalize soundfile",
					"//--edit paths and evaluate...\nSoundFile.normalize(\n\t\"~/Music/SuperCollider Recordings/SC_090410_125330.aiff\".standardizePath,\n\t\"~/Music/SuperCollider Recordings/SC_090410_125330+.aiff\"standardizePath,\n\tnil, //\"AIFF\" \"WAVE\"\n\t\"int16\"\n)"
				).syntaxColorize;
			},
			['template', 'animationview'], {
				Document(
					"animationview",
					"(\nvar width= 500, height= 500;\nvar win= Window(\"animationview\", Rect(300, 300, width, height), false);\nvar usr= AnimationView(win, Rect(0, 0, width, height));\nusr.background= Color.white;\nusr.clearOnRefresh= true;\nusr.showInfo= false;\nusr.drawFunc= {|view, index|\n\tPen.setSmoothing(true);\n\tPen.width= 1;\n\tPen.fillColor= Color.red;\n\tPen.fillRect(Rect(index*3%width, index*4%height, 20, 20));\n};\nwin.front;\nCmdPeriod.doOnce({if(win.isClosed.not, {win.close})});\n)"
				).syntaxColorize;
			},
			['extras', 'random helpfile'], {
				Document.open(PathName("Help").deepFiles.reject{|x| #[\jpg, \png, \qtz].includes(x.extension.asSymbol)}.choose.fullPath);
			},
			['extras', 'swing boot'], {
				SwingOSC.default.boot;
			},
			['extras', 'Quarks.gui'], {
				Quarks.gui;
			},
			['extras', 'Quarks.checkoutAll'], {
				Quarks.checkoutAll;
			},
			['extras', 'open RedToolsMenu.sc'], {
				RedToolsMenu.openCodeFile
			},
			['extras', 'open startup'], {
				Document.open(PathName(Platform.userExtensionDir).pathOnly++"startup.rtf");
			}
		];
		StartUp.add({RedToolsMenu.listToMenuRed;});
	}
	*listToLibrary {
		list.pairsDo{|x, y| Library.putList([\redTools]++x, y)}
	}
	*listToMenuLibrary {
		list.pairsDo{|x, y| CocoaMenuItem.add(([\redTools]++x).collect{|x| x.asString}, y)}
	}
	*listToMenuRed {
		var red= CocoaMenuItem.topLevelItems.detect{|x| x.name=="Red"};
		if(red.isNil, {
			red= SCMenuGroup(nil, "Red");
		});
		list.pairsDo{|x, y|
			SCMenuItem(red, "".catList([x, [$/]].lace(x.size*2-1)))
				.action_(y);
		};
		SCMenuItem(red, "makeWindow")
			.setShortCut("r", false, false)
			.action_{RedToolsMenu.makeWindow};
	}
	*makeWindow {|position|
		var w, names= [], fnt= RedFont.new;
		position= position ?? {6@50};
		list.pairsDo{|x, y| names= names.add(x[1])};
		w= Window("_redTools", Rect(position.x, position.y, 175, "".bounds(fnt).height+3*names.size), false)
			.alpha_(GUI.skins[\redFrik].unfocus).front;
		ListView(w, w.view.bounds.width@w.view.bounds.height)
			.font_(RedFont.new)
			.focus
			.background_(GUI.skins[\redFrik].background)
			.stringColor_(GUI.skins[\redFrik].foreground)
			.hiliteColor_(GUI.skins[\redFrik].selection)
			.items_(names)
			.enterKeyAction_{|view|
				(list[view.value*2+1]).value;
				w.close;
			};
	}
}
