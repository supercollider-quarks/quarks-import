/*

KeyPlayer.all.clear;
g = KeyPlayerGui2();
h = KeyPlayerGui2(options: [\useList]);

"asdfg".do { |char| KeyPlayer(char.asSymbol) }
"hjkl".do { |char| KeyPlayer(char.asSymbol) }

g.object_(KeyPlayer(\x));
h.object_(g.object);

g.object.put($t, {"t!".postln });
g.object.put($x, {"x".postln }, both: true);
g.object.putUp($x, {"x up!".postln });
g.object.putUp($x, {"x up!".postln }, both: true);

g.object.putUp($y, {"y up only!".postln }, both: true);

h.listView.action = { |l| "yo".postln; l.items.postln };
h.listView.keyDownAction = { |l, char|
if (char == $\r) { l.object_( KeyPlayer.all[l.items[l.value]]) };
};

l.items[l.value].postcs;
l = h.listView;

h.listView.bounds_( h.listView.bounds.height + 20)
KeyPlayer[].dump;

*/


KeyPlayerGui2 : JITGui {

	classvar <>colors;
	classvar <>lineOffsets;
	classvar <>keyboard;
	classvar <>keyboardNoShift;
	classvar <>keyboardShift;

	var <buttons, <drags, <font, <listView;
	var <>activeColor;

	*initClass {

		colors = [
			Color(0.8, 0.8, 0.8, 0.5),	// normal - 	grey
			Color(0.8, 0.2, 0.2, 1),	// ctl		red
			Color(0.2, 0.8, 0.2, 1),	// shift		green
			Color(0.8, 0.8, 0.2, 1),	// alt		blue
			Color(0.2, 1, 1, 1),		// alt shift	blue+green - cyan
			Color(1, 1, 0.2, 1),		// ctl shift	red+green - yellow
			Color(1, 0.2, 1, 1),		// ctl alt	red+blue - violet
			Color(1, 1, 1, 1)			// ctl alt shift	red green blue - white
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

	winName { ^"KeyPlayer" + (try { object.key } ? "") }

			// these methods should be overridden in subclasses:
	setDefaults { |options|
		var minWidth, minHeight;
		if (options.includes(\useList)) {
			minWidth = 480;
			minHeight = 170;
		} {
			minWidth = 420; minHeight = 150;
			minHeight = minHeight + (numItems * skin.buttonHeight + skin.headHeight);
		};

		if (parent.isNil) {
			defPos = 10@260
		} {
			defPos = skin.margin;
		};
		minSize = minWidth @ minHeight;
		//	"KPGui2 - minSize: %\n".postf(minSize);
	}

	calcBounds {
		var defBounds;
		if(bounds.isKindOf(Rect)) {
			bounds.setExtent(
				max(bounds.width, minSize.x),
				max(bounds.height, minSize.y)

			);
			^this
		};

		defBounds = Rect.fromPoints(defPos, defPos + minSize + (skin.margin * 2));
		if (bounds.isNil) {
			bounds = defBounds;
			^this
		};

		if (bounds.isKindOf(Point)) {
			bounds = defBounds.setExtent(
				max(bounds.x, minSize.x),
				max(bounds.y, minSize.y));
		}
	}

	getState {
		// get all the state I need to know of the object I am watching
		var news = (
			kpKeys: KeyPlayer.all.keys.asArray.sort,
			object: object);

		if (object.notNil) {
			news.put(\downKeys, object.actions.keys);
			news.put(\upKeys, object.upActions.keys);
			if (object.rec.notNil) {
				news.put(\recIsOn, object.rec.isOn);
				news.put(\recIsPlaying, object.rec.task.isActive);
				news.put(\recLoop, object.rec.loop);
				news.put(\recListSize, object.rec.lists.size);
				news.put(\recCurrList, object.rec.list);
			};
		};
		^news
	}

	checkUpdate {
		var news = this.getState;

		if (news[\object] != prevState[\object] or:
			(news[\kpKeys] != prevState[\kpKeys])
		) {
			this.updateGlobal(news);
		};

		if (news[\object] != prevState[\object] or:
			(news[\kpKeys] != prevState[\kpKeys])
		) {
			this.updateGlobal(news);
		};

		if (news[\object] != prevState[\object] or:
			(news[\kpKeys] != prevState[\kpKeys])
		) {
			this.updateGlobal(news);
		};

		this.updateDrags(news); // does the checking

		prevState = news;
	}

	accepts { |obj| ^obj.isNil or: { obj.isKindOf(KeyPlayer) } }

	// better in JITGui?
	front { if (hasWindow) { parent.front } }

	updateAll {
		this.updateButtons;
		if (object.notNil) {
			this.updateDrags;
			zone.refresh;
		}
	}

	updateGlobal { |news|
		var keys = news[\kpKeys];

		var myIndex = -1;
		if (object.notNil) { myIndex = keys.indexOf(object.key); };

			// if buttons == nil, nothing
		buttons.do { |b, i|
			var col = if (i == myIndex) { skin.onColor } { skin.offColor };
			var key = keys[i];
			var keyExists = keys[i].notNil;
			b.states_([[key ? "", Color.black, col]]).enabled_(keyExists);
		};

		listView !? { listView.items_(keys).value_(myIndex); };
	}

	updateDrags { |news|
		var downKeys = news[\downKeys];
		var upKeys = news[\upKeys];

		if (downKeys == prevState[\downKeys]
			and: { upKeys == prevState[\upKeys] }) {
			^this
		};
		downKeys = downKeys ? [];
		upKeys = upKeys ? [];

		drags.keysValuesDo { |uni, drag|
			var val = 0;
			if (downKeys.includes(uni)) { val = val + 1 };
			if (upKeys.includes(uni)) { val = val + 2 };
			drag.background_(colors[val]);
			[uni, val, colors[val]];
		};
		zone.refresh;
	}

	object_ { |obj|
		if(this.accepts(obj).not) { ^this };
		object = obj;
		if (obj.notNil) {
			parent.asView.keyDownAction_(object.makeKeyDownAction);
			parent.asView.keyUpAction_(object.makeKeyUpAction);
		};
	}

	///// make all the view elements: //////

	makeViews { |options|
		zone.addFlowLayout; // use a flat one
		// seethru!
		parent.asView.background_(Color(0.5, 0.5, 0.5, 0.1));
		zone.background_(Color(0.5, 0.5, 0.5, 0.1));

		if (options.includes(\useList)) {
			this.makeListView;
			zone.decorator.nextLine;
			zone.decorator.bounds = zone.bounds.left_(64);
			zone.decorator.top_(zone.bounds.top);
		} {
			this.makeButtons;
		};
		this.makeDrags;
	}


	makeListView {
		listView = ListView(zone, Rect(2,2,60,160));
		listView.background_(Color.grey(1.0, 0.5));
		listView.keyDownAction = { |l, char|
			if (char == $\r) { this.object_( KeyPlayer.all[l.items[l.value]]) };
		};
	}

	makeButtons {
		var keys = KeyPlayer.all.keys.asArray.sort;
		var navButL, navButR;
		navButL = Button(zone, Rect(0, 0, 14, 16)).states_([[ "<" ]]);
		buttons = 10.collect { |i|
			Button(zone, Rect(0, 0, 33, 16)).states_([[ keys[i] ? "-" ]])
				.action_({ |b|
					var nuKP = KeyPlayer.all[b.states[0][0]];
					if (nuKP.notNil) { this.object_(nuKP); };
					this.checkUpdate;
				})
		};
		navButR = Button(zone, Rect(0, 0, 14, 16)).states_([[ ">" ]]);
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

	}

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
