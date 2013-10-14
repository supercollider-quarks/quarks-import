/*
The plan is to reimplement the not-quite-correct TelepathicClock
as to match a future time sharing protocol for laptop ensembles.
TelepathicClock may change implementation.
ListeningClock and SoftClock will stay the same.
*/

(
// basic test:
a = ReferenceClock.new;
b = ListeningClock.new;
b.addClock(a, 1);
b.startListen;
b.verbose = true;
);

// push reference into the future
a.adjust(1, b.elapsedBeats + 3);
// push reference into the past
a.adjust(1, b.elapsedBeats - 3);

// regular updates
fork { loop { 1.wait; a.adjust(1, b.elapsedBeats + 0.1.rand2) } }

