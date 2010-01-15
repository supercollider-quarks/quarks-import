

// a simple example startup file



// change the post window
(
{
var fontcolor = Color.new255(10, 10, 40);
var backcolor = Color.new255(153, 204, 102);
var font = Font("Andale Mono", 14);
var bounds;

try {
	Document.listener.background_(backcolor).stringColor_(fontcolor);
	Document.postColor_(fontcolor);
	Document.listener.font_(font);
	bounds = SCWindow.screenBounds;
	Document.listener.bounds_(Rect(8, bounds.height - 8, 510, bounds.height - 280));
};
}.value
);



// set the server to internal
Server.default = s = Server.internal;
s.waitForBoot {
	s.scope;
	defer { Document.listener.front };
};


// make a default environments p and q, don't push for now
q = ();
p = ProxySpace.new(s);

// add some information
q.numChans = 2; // mutlichannel setup: q.numChans = 8;

// reduce info posting. 
SkipJack.verbose = false;

Platform.case(\osx, {
	
	
			// make custom Library menu: 
	CocoaMenuItem.clearCustomItems;
			// Custom Menu Items - OSX only - configure here
	CocoaMenuItem.add(["scope", "scope Ins"],
		{ q.scope = s.scope(q.numChans, index: s.options.numOutputBusChannels, zoom: 9) });
	CocoaMenuItem.add(["scope", "scope Outs"],
		{ q.scope = s.scope(q.numChans, index: 0, zoom: 9) });
	CocoaMenuItem.add(["scope", "FreqScope"],
		{ q.fscope = FreqScope.new });
	CocoaMenuItem.add(["sound check", "noise pan"],{ 
		p[\zztest] = { 
			PanAz.ar(q.numChans, PinkNoise.ar(1), SinOsc.kr(0.125));
		};
		p[\zztest].play(vol: 0.25);
	});
	CocoaMenuItem.add(["sound check", "test sound"],{ 
		p[\zztest] = { 
			BPF.ar(Dust.ar(5 ! q.numChans, 50), { exprand(200, 2010) } ! q.numChans, 0.05) 
			* GrayNoise.ar(0.1, 1) 
		};
		p[\zztest].playN(vol: 0.25);
	});

	CocoaMenuItem.add(["extensions", "extensionDirs"],{ 
		unixCmd("open " ++ Platform.userExtensionDir.asCompileString);
		unixCmd("open " ++ Platform.systemExtensionDir.asCompileString); 
	});
		
	CocoaMenuItem.add(["extensions", "Quarks.gui"], { Quarks.gui; });
	CocoaMenuItem.add(["extensions", "Quarks.update"], { Quarks.checkoutAll; });

});

