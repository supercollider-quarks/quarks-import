	// KeyPlayer should be able to save/write and load as code
	//
	// KeyPlayerGui should be a JITGui
	// should be put itself on any window
	// etc

KeyPlayer {
	classvar <>verbose=false, <all, gui;

	var <key, <actions, <upActions, <bothActions, <pressed;
	var <>rec;

	*initClass {
		all = ();
	}
	*at { |key| ^all.at(key); }

	*new { arg key = \k, inDict, ignoreCase = true;
		var kp = this.at(key);
		if (kp.isNil) {
			kp = super.newCopyArgs(key);
			if (inDict.isNil, { kp.init(inDict, ignoreCase) });
		};
		^kp
	}

	init { arg inDict, ignoreCase = false;

		all.put(key.asSymbol, this);

		actions = ();
		upActions = ();
		pressed = ();
		bothActions = (down: actions, up: upActions);

			// this needs reworking.
			// upactions/downactions, ignoreCase ? how?

		inDict !? {
				// it is a bothActions dict
			[\down, \up].do { |where|
				this.putAll(inDict[where], ignoreCase, where);
			};
				// else assume just keyDowns - OK?
			inDict.keys.removeAll([\down, \up]).do { |key|
				this.put(key, inDict[key], ignoreCase, \down);
			}
		};
	}

	*gui { if (gui.isNil or: { gui.w.isClosed }){ gui = KeyPlayerGui() }; ^gui.front; }

	gui { ^KeyPlayerGui(this) }

	makeRec { rec = KeyPlayerRec(this); }

	putAll { |dict, both=false, where=\down|
		dict.keysValuesDo{ |k, f| this.put(k, f, both, where) }
	}

	put { |char, func, both = false, where = \down|
		if (both and: { char.isKindOf(Char) } and: { char.isAlpha }) {
			[char.toLower, char.toUpper].do { |char|
				this.putUni(char, func, where);
			};
		} {
			this.putUni(char, func, where);
		};
	}
	putUp { |char, func, both = false|
		this.put(char, func, both, \up);
	}

	putDown { |char, func, both = false, noRep=false|
		var wrapFunc;
		if (noRep) {
			wrapFunc = { |...args| if (this.isUp(char)) { func.value(*args) } }
		};
		this.put(char, wrapFunc ? func, both, \down);
	}

	putBoth { |char, func, both = false, noRep=false|
		this.put(char, func, both, \down, noRep);
		this.put(char, func, both, \up);
	}

	putUni { |charOrUni, func, where=\down|
		bothActions[where].put(charOrUni.asUnicode, func);
	}

	isUp { |char| ^this.isDown(char).not }
	isDown { |char| ^this.isPressed(char.asUnicode) }
	isPressed { |char| ^pressed[char.asUnicode] ? false }

	at { |char, where=\down| ^bothActions[where][char.asUnicode] }

	removeAt { |char, where=\down, both=false| this.put(char, nil, both, where) }

	keyAction { |char, modifiers, unicode, keycode, which=\down, press = true|

		 var whichActions, action, result;

		 if (verbose) { [KeyPlayer, char, modifiers, unicode, keycode].postcs; };

		 if (rec.notNil) { rec.recordEvent(unicode, which) };

				// call the function
		unicode = unicode ?? { char.asUnicode };
		whichActions = bothActions[which];
		action = whichActions[unicode];

		result = action.value(char, modifiers, unicode, keycode);
		pressed.put(unicode, press);

			// if the result is a function, that function
			// becomes the new action for the key
		if (result.isKindOf(Function)) {
			whichActions[char] = result;
		};
	}
	keyDown { |char, modifiers, unicode, keycode|
		this.keyAction(char, modifiers, unicode, keycode, \down, true);
	}
	keyUp { |char, modifiers, unicode, keycode|
		this.keyAction(char, modifiers, unicode, keycode, \up, false);
	}

	makeKeyAction { |which=\down, press = true|
					// define a function to handle key downs.
		^{ |view, char, modifiers, unicode, keycode|
			this.keyAction(char, modifiers, unicode, keycode, which, press);
		};
	}

	makeKeyDownAction { ^this.makeKeyAction(\down, true) }
	makeKeyUpAction { ^this.makeKeyAction(\up, false) }

	// write { |path| /* save directly to a path ... */ }
	//
	// read { |path| this.putAll(path.load ? ()); }

	saveDoc {
		Document("save my actions").string_(this.actions.asCompileString);
	}
}

KeyPlayerGui {
	classvar <>colors;
	classvar <>lineOffsets;
	classvar <>keyboard;
	classvar <>keyboardNoShift;
	classvar <>keyboardShift;

	var <player;
	var <w, <zone, <buttons, <drags, <font, <listview;
	var <skipjack, <>activeColor;

	*initClass {

		colors = [
			Color(0.8, 0.8, 0.8, 1),	// normal - 	grey
			Color(0.8, 0.2, 0.2, 1),	// ctl		red
			Color(0.2, 0.8, 0.2, 1),	// shift		green
			Color(0.8, 0.8, 0.2, 1),	// alt		blue
			Color(0.2, 1, 1, 1),		// alt shift	blue+green - cyan
			Color(1, 1, 0.2, 1),		// ctl shift	red+green - yellow
			Color(1, 0.2, 1, 1),		// ctl alt	red+blue - violet
			Color(1, 1, 1, 1)		// ctl alt shift	red green blue - white
		];

		// these describe the keyboard to show;
				// horizontal offsets for keys.
		lineOffsets = #[42, 48, 57, 117];

				// these are the keys you normally see (US, big keyb.)
				// customise for german or other keyboard layouts.
		keyboard = #["`1234567890-=", "QWERTYUIOP[]\\", "ASDFGHJKL;'", "ZXCVBNM,./"];

			// NOT USED YET:
				// the maps I get on my PB, US keyb., no shift
		keyboardNoShift = #["1234567890-=", "qwertyuiop[]\\", "asdfghjkl;'", "`zxcvbnm,./"];
				// and shifted	- /* double-quote */ only there for syntax colorize.
		keyboardShift = #["!@#$%^&*()_+", "QWERTYUIOP{}", "ASDFGHJKL:\"|" /*"*/, "~ZXCVBNM<>?"];

	}

	*new { |kp, win, useList=false|
		^super.new.init(kp, win, useList);
	}

	init { |kp, win, useList|
		var zonebounds;
		if (useList) {
			zonebounds = Rect(0, 0, 480, 150);
		} {
			zonebounds = Rect(0, 0, 420, 170);
		};

		w = win ?? { this.makeWindow(try { kp.key } ? "KeyPlayerAll", zonebounds) };
		zone = CompositeView(w, zonebounds);
		zone.decorator = FlowLayout(zonebounds);

		if (useList) {
			this.makeListView;
			zone.decorator.bounds = zonebounds.left_(64);
			zone.decorator.top_(zone.decorator.top);
		} {
			this.makeButtons
		};
		this.makeDrags;

		this.player_(kp);

	}
	makeWindow { |name, zonebounds|
			var win = Window("keys" + name, zonebounds).front;
			win.view.background_(Color(0.5, 0.5, 0.5, 0.0));
			win.view.decorator = FlowLayout(win.view.bounds.moveTo(0,0));
			^win;
	}

	front { w.front }

	updateAll {
		this.updateButtons;
		if (player.notNil) {
			this.updateDrags;
			w.refresh;
		}
	}
	updateButtons {
		var keys = KeyPlayer.all.keys.asArray.sort;

		var myIndex = -1;
		if (player.notNil) { myIndex = keys.indexOf(player.key); };

			// if buttons == nil, nothing
		buttons.do {�|b, i|
			var col = if (i == myIndex) { Color.white } { Color.grey(0.8) };
			var key = keys[i];
			var keyExists = keys[i].notNil;
			b.states_([[key ? "", Color.black, col]]).enabled_(keyExists);
		};

		listview.notNil.if {
			listview.items_(keys).value_(myIndex);
		};
	}
	updateDrags { |which = 0|
		var downKeys = player.actions.keys;
		var upKeys = player.upActions.keys;

			// do it for "A", "a";

		drags.keysValuesDo { |uni, drag|
			var val = 0;
			if (downKeys.includes(uni)) { val = val + 1 };
			if (upKeys.includes(uni)) { val = val + 2 };
			drag.background_(colors[val])
		}
	}
	makeButtons {
		var keys = KeyPlayer.all.keys.asArray.sort;
		buttons = 10.collect { |i|
			Button(zone, Rect(0, 0, 36, 16)).states_([[ keys[i] ? "-" ]])
				.action_({ |b|
					var nuKP = KeyPlayer.all[b.states[0][0]];
					if (nuKP.notNil) { this.player_(nuKP); };
					this.updateAll;
				})
		};
	//	buttons[keys.indexOf(player.key)].focus;
	}

	makeListView {
		listview = ListView(zone, Rect(2,2,60,25));
		listview.bounds_(Rect(2,2,60,130));

	}

	player_ { |nuplayer|
		if (nuplayer.isNil) { ^this };

		player = nuplayer;
		w.view.keyDownAction_(player.makeKeyDownAction);
		w.view.keyUpAction_(player.makeKeyUpAction);
	}

	makeDrags {

		font = Font("Courier-Bold", 14);
		drags = ();

				// make the rows of the keyboard
		keyboard.do {|row, i|
			row.do {|key| this.makeKey(key) };
			if (i==0) { this.makeKey(127.asAscii, "del", 38 @ 24) };
			if (i==2) { this.makeKey($\r, "retrn", 46 @ 24) };
			zone.decorator.nextLine;
			zone.decorator.shift(lineOffsets[i]);
		};

//		zone.decorator.shift(lineOffsets.last.neg);
//				// make the last row
//		[\norm, \shift, \ctrl, \alt].do { |lab, i|
//			Button(zone, Rect(0,0,36,18))
//				.states_([[lab]])
//				.action_{ ("not used yet:" + lab).postln };
//		};

		this.makeKey($ , "space", 150 @ 24);
		this.makeKey(3.asAscii, "enter", 48 @ 24);

				// this is maybe unnecessary?
		skipjack = SkipJack({ 8.do { |i| this.updateAll; } }, 1, { zone.isClosed }, "KPgui");
	}

	runUpdate { skipjack.start }
	stopUpdate { skipjack.stop }

	makeKey { |char, keyname, bounds|
		var v;
		keyname = keyname ? char.asString;
		bounds = bounds ? (24 @ 24);

		v = DragBoth(zone, bounds);
		v.font = font;
		v.string = keyname;
		v.align = \center;
		v.setBoth = false;
//		v.acceptDrag = {
//			View.currentDrag.isKindOf(Function)
//		};
		drags.put(char.toLower.asUnicode, v);
	//	v.action = { this.put(char.toLower, v.object, true) };
	}
}