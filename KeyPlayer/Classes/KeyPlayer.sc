/*

- KeyPlayer should be able to save/write and load as code
- can KeyPlayer be ut into a single button?


*/

KeyPlayer {
	classvar <>verbose=false, <all, gui;

	var <key, <actions, <upActions, <bothActions, <pressed;
	var <altActions;
	var <>rec;

	*initClass {
		all = ();
	}

	*at { |key| ^all.at(key); }

	*new { arg key = \k, inDict, ignoreCase = true;
		var res = this.at(key);
		if (res.isNil) {
			res = super.newCopyArgs(key)
			    .init(inDict, ignoreCase);
		};
		^res
	}

	init { arg inDict, ignoreCase = false;

		all.put(key.asSymbol, this);

		actions = ();
		upActions = ();
		pressed = ();
		bothActions = (down: actions, up: upActions);

		altActions = ();

			// this needs full rework.
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

		this.makeDefaultMetaActions;
	}

	makeDefaultMetaActions {
		// put in some alt-commands for the KeyLoop:
		this.putAlt($r.asUnicode, { this.rec.toggleRec });
		this.putAlt($p.asUnicode, { this.rec.togglePlay });
		this.putAlt($l.asUnicode, { this.rec.toggleLooped });

		this.putAlt($f.asUnicode, { this.rec.playOnce; });
	}

	storeArgs { ^[key] }

	*gui { if (gui.isNil or: { gui.parent.isClosed }){ gui = KeyPlayerGui() }; ^gui.front; }

	gui { ^KeyPlayerGui(this) }

	makeLoop { rec = KeyLoop(key, KeyLoop.keyPlayerFunc(this)); }

	makeRec {
		warn("KeyPlayer:makeRec will be deprecated; use KeyPlayer:makeLoop.");
		rec = KeyPlayerRec(this);
	}

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

	// in 3.7.0, only alt mod is working...
	putAlt { |uni, action|
		altActions.put(uni,action);
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

		// early exit if meta key was pressed - only down
		if (this.isMetaAction(modifiers, which)) {
			^this.doMetaAction(char, modifiers, unicode, keycode, which);
		};
		// maybe adapt whether KeyLoop or KeyPlayerRec?
		// better remove KeyPlayerRec ASAP.
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

	isMetaAction { |modifiers, which|
		^modifiers.notNil and: { (which == \down) and: { modifiers.isAlt } };
	}

	doMetaAction { |char, modifiers, unicode, keycode, which|

		if (which == \down) {
			altActions[unicode].value(char, modifiers, unicode, keycode);
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

