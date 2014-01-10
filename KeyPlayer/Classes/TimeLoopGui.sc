

TimeLoopGui : JITGui {

	var <nameBut, <playBut, <pauseBut, <recBut, <mapBut;
	var <tempoSl, <lpStartSl, <lengthSl, <jitterSl;
	var <loopBut, <revBut;
	var <listText, indexBox;
	// <invBut, <sclBut;
	// var <scalerSl, <shiftSl;

	accepts { |obj| ^(obj.isNil) or: { obj.isKindOf(TimeLoop) }; }

	setDefaults { |options|
		defPos = 10@260;
		minSize = 210 @ (skin.buttonHeight * 8 + 10);
		if (parent.notNil) { skin = skin.copy.put(\margin, 0@0) };
	//	"% - minSize: %\n".postf(this.class, minSize);
	}

	makeViews { |options|
		var height = skin.buttonHeight;
		var lineWidth = zone.bounds.width - (skin.margin.y * 2);
		var zoneMargin = if ( (numItems > 0) or:
			{ parent.isKindOf(Window.implClass) }) { skin.margin } { 0@0 };

		zone.decorator = FlowLayout(zone.bounds, zoneMargin, skin.gap);
		zone.resize_(2);
		// top line

		this.makeTopLine(lineWidth, height);
		this.makeLoopSliders(lineWidth, height);
		this.makeLoopButtons(lineWidth, height);
		this.makeListControls(lineWidth, height);
	}

	makeListControls { |lineWidth, height|
		listText = StaticText(zone, (lineWidth * 0.29)@height).align_(\center);
		indexBox = EZNumber(zone, (lineWidth * 0.17)@height, \i,
			[0, 99, \lin, 1].asSpec, { |b| object.setList(b.value.asInteger) },
			0, labelWidth: 10);
		indexBox.labelView.align_(\center);
		this.setNumLists(0);
	}

	setNumLists { |num = 0|
		listText.string_(num.asString + "lists");
	}

	makeTopLine { |lineWidth, height|
		var width = lineWidth * 0.62 / 4;
		var nameWidth = lineWidth * 0.38 - 4;

		nameBut = Button(zone, Rect(0,0, nameWidth, height))
			.font_(font)
			.resize_(2)
			.states_([ [" ", skin.fontColor, skin.onColor] ]);

		playBut = Button(zone, Rect(0,0, width, height))
			.font_(font)
			.resize_(3)
			.states_([
				[" >", skin.fontColor, skin.offColor],
				[" _", skin.fontColor, skin.onColor ],
				[" |", skin.fontColor, skin.offColor ]
			])
			.action_({ |but|
				[ { object.play }, { object.play }, { object.stop } ][but.value].value;
				this.checkUpdate;
			});

		pauseBut = Button(zone, Rect(0,0, width, height))
			.font_(font)
			.resize_(3)
			.states_([
				["paus", skin.fontColor, skin.onColor],
				["rsum", skin.fontColor, skin.offColor]
			])
			.action_({ |but| var string;
				[ { object.resume },{ object.pause } ][but.value].value;
				this.checkUpdate;
			});

		recBut = Button(zone, Rect(0,0, width, height))
			.font_(font)
			.resize_(3)
			.states_([
				["rec", skin.fontColor, skin.offColor],
				["stop", Color.white, Color.red]
			])
			.action_({ |but|
				[ { object.stopRec }, { object.startRec } ][but.value].value;
			});

	}

	makeLoopSliders { |lineWidth, height|
		var sliders;

		tempoSl = EZSlider(zone, lineWidth@height, \tempo, [0.1, 10, \exp],
			{ |sl| object.tempo = sl.value }, 1, labelWidth: 40);

		lpStartSl = EZSlider(zone, lineWidth@height, \lpStart, [0.0, 1.0],
			{ |sl| object.lpStart = sl.value }, 0, labelWidth: 40);

		lengthSl = EZSlider(zone, lineWidth@height, \length, [0.0, 1.0],
			{ |sl| object.length = sl.value }, 1, labelWidth: 40);

		jitterSl = EZSlider(zone, lineWidth@height, \jitter, [0.0, 1, \amp],
			{ |sl| object.jitter = sl.value }, 0, labelWidth: 40);

		sliders = [tempoSl, lpStartSl, lengthSl, jitterSl];
		sliders.do { |sl| sl.view.resize_(2); };
	}

	makeLoopButtons { |lineWidth, height|

		Button(zone, Rect(0,0, lineWidth * 0.23, height))
		    .font_(font)
		    // .resize_(3)
			.states_([["resetLp", skin.fontColor, skin.offColor]])
			.action_({ |but| object.resetLoop });

			// Button(zone, Rect(0,0, lineWidth * 0.27 - 1, height))
			// .font_(font).resize_(3)
			// .states_([["rsScale", skin.fontColor, skin.offColor]])
			// .action_({ |but| object.resetLoop });


		loopBut = Button(zone, Rect(0,0, lineWidth * 0.15, height))
			.font_(font)
			.resize_(3)
			.states_([
				["once", skin.fontColor, skin.offColor],
				["loop", skin.fontColor, skin.onColor]
			])
			.action_({ |but| object.loop = but.value > 0; });

		revBut = Button(zone, Rect(0,0, lineWidth * 0.15, height))
			.font_(font)
			.resize_(3)
			.states_([
				["rev", skin.fontColor, skin.offColor],
				["fwd", skin.fontColor, skin.onColor]
			])
			.action_({ |but|
				[ { object.reverse }, { object.forward } ][but.value].value;
			});

			// invBut = Button(zone, Rect(0,0, width, height))
			// .font_(font)
			// .resize_(3)
			// .states_([
			// 	["inv", skin.fontColor, skin.offColor],
			// 	["up", skin.fontColor, skin.onColor]
			// ])
			// .action_({ |but|
			// 	[ { object.invert }, { object.up } ][but.value].value;
			// });
			//
			// sclBut = Button(zone, Rect(0,0, width, height))
			// .font_(font)
			// .resize_(3)
			// .states_([
			// 	["rscl", skin.fontColor, skin.offColor],
			// 	["norm", skin.fontColor, skin.onColor]
			// ])
			// .action_({ |but| object.rescaled_(but.value > 0); });

			// scalerSl = EZSlider(zone, lineWidth@height, \scale, [0.0, 4, \amp],
			// { |sl| object.scaler = sl.value }, 1, labelWidth: 40);
			//
			// shiftSl = EZSlider(zone, lineWidth@height, \shift, [-0.5, 0.5],
			// { |sl| object.shift = sl.value }, 0, labelWidth: 40);

	//	this.checkUpdate;
	}

	getState {
		if (object.isNil) {
			^(object: nil, name: " ", isPlaying: false, isRecording: false,
			reverse: false, inverse: false, rescaled: false);
		};

		^(
			object: object,
			name: object.key,
			isPlaying: object.isPlaying.binaryValue,
			isActive: object.task.isActive.binaryValue,
			canPause: object.task.canPause.binaryValue,
			isPaused: object.task.isPaused.binaryValue,
			isRecording: object.isRecording.binaryValue,

			isReversed: object.isReversed.binaryValue,


			tempo: object.tempo,
			lpStart: object.lpStart,
			loop: object.loop,
			length: object.length,
			jitter: object.jitter,
			step: object.step
		);
	}

	checkUpdate {
		var newState = this.getState;
		var playState;

		if (newState == prevState) {
		//	"no change.".postln;
			^this
		};

		if (newState[\object].isNil) {
		//	"no object.".postln;
			prevState = newState;
			zone.visible_(false);
			^this;
		};

		if (newState[\name] != prevState[\name]) {  // name
			zone.visible_(true);
			nameBut.states_(nameBut.states.collect(_.put(0, object.key.asString))).refresh;
		};

		playState = newState[\isPlaying] * 2 - newState[\isActive];
		newState.put(\playState, playState);

		if (playState != prevState[\playState]) {
				// stopped/playing/ended
				// 0 is stopped, 1 is active, 2 is playing but waiting:
			playBut.value_(playState).refresh;
		};

		if (newState[\canPause] != prevState[\canPause]) {
			pauseBut.visible_(newState[\canPause] > 0).refresh;
		};

		if (newState[\isPaused] != prevState[\isPaused]) {
			pauseBut.value_(newState[\isPaused]).refresh;
		};

		if (newState[\isRecording] != prevState[\isRecording]) {
			recBut.value_(newState[\isRecording]).refresh;
		};

		// if (newState[\isReversed] != prevState[\isReversed]) {
		// 	revBut.value_(newState[\isReversed]).refresh;
		// };


		if (newState[\tempo] != prevState[\tempo]) {
			tempoSl.value_(newState[\tempo]);
		};

		if (newState[\lpStart] != prevState[\lpStart]) {
			lpStartSl.value_(newState[\lpStart]);
		};

		if (newState[\length] != prevState[\length]) {
			lengthSl.value_(newState[\length]);
		};

		if (newState[\jitter] != prevState[\jitter]) {
			jitterSl.value_(newState[\jitter]);
		};

		// if (newState[\isInverse] != prevState[\isInverse]) {
		// 	revBut.value_(newState[\isInverse]).refresh;
		// };
		//
		// if (newState[\rescaled] != prevState[\rescaled]) {
		// 	sclBut.value_(newState[\rescaled]).refresh;
		// };
		//
		//
		// if (newState[\scaler] != prevState[\scaler]) {
		// 	scalerSl.value_(newState[\scaler]);
		// };
		//
		// if (newState[\shift] != prevState[\shift]) {
		// 	shiftSl.value_(newState[\shift]);
		// };


		prevState = newState.copy;
	}
}

KeyLoopGui : TimeLoopGui {

}
