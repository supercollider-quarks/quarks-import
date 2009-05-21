//--
work in progress.  happy for any comments or contributions.

note! this quark make one serious change by overriding Collection:species {^this.class}.  other things in your sc system may break.

note! some of the examples require additional classes like RedGA etc.  these are available online at my homepage http://www.fredrikolofsson.com under code->sc


//--

090521 - RedWindow and RedJWindow now draws in the UserView - thanks Thor
090514 - growth example and asPoint added to RedVector
090510 - added RedLSystem and RedLTurtle classes
	updated example 070-lsystem.scd
	edited links in all helpfiles and in the overview
	added s.sync to a few examples
	updated and moved redFingerprint quark into redUniverse
081111 - moved RedWindow and RedJWindow into separate folders (osx, linux, windows) so that cocoa gui code is ignored on linux+windows
080929 - some minor additions
	added extPoint asRedVector2D helper methods
	added RedHiddenObject.  useful when using attractors
	added <userView for RedWindow and RedJWindow
080219 - updated for sc3.2 and swingosc0.59
	fixed all pendulum examples to draw line on swingosc (added a GUI.pen.stroke)
	changed from mouseover to mousemove so now it's required to click&drag to update mouse position.
	fixed 150-track_synth.scd and took away RedTrack.  now using only standard ugens for audio tracking.
080116 - changed from 25 to 40 fps for RedWindow and RedJWindow .play
	some small corrections to match Pen changes to width_
071205 - converted all help.rtf to .html and the examples to .scd
	added the example overview file.
	changed so that RedJWindow is disabled by default.
071031 - update for 3.1
	removed penExt.sc with its strokeColor_ and fillColor_ extensions.
	tiny fix for 150-track_synth.rtf.
071029 - bugfix
	now RedWindow shouldn't crash sc anymore.  it was due to scott's window scoll implemented earlier this autumn.

