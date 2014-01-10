    // the random keyboard:
(
s.boot;
s.latency = nil;    // make sure we respond quickly
thisThread.randSeed = 2008;
k = KeyPlayer(\rand);
"qwertzuiopasdfghjklyxcvbnm".do { |char|
    var note = (midinote: rrand(36, 96), dur: rrand(0.1, 1.0));
   k.putDown(char, { |char| char; note.play });
};
k.makeLoop;
g = KeyPlayerGui2(k);
h = TimeLoopGui(k.rec);
)

h.setNumLists(99);

// put in some alt-commands for the KeyLoop:
k.putMeta($r.asUnicode, { k.rec.toggleRec });
k.putMeta($p.asUnicode, { k.rec.togglePlay });
k.putMeta($l.asUnicode, { k.rec.loop = k.rec.loop.not });
k.putMeta($f.asUnicode, { "k.rec.fork".postln; });

k.putMeta($g.asUnicode, {
	TimeLoopGui(k.rec).moveTo(*g.parent.bounds.rightTop.asArray);
});
)
k.rec.loop;
k.rec.startRec;
k.rec.stopRec;

KeyPlayer(\rand).rec.listInfo;
KeyPlayer(\rand).rec.printLists;

KeyPlayer(\rand).rec.play;


CtLoopGui(CtLoop('a'), 10, bounds: 200@200);


k.rec.dump;

