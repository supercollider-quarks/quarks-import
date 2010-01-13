

// a simple example startup file



// change the post window
(
{
var fontcolor = Color.new255(204, 153, 0);
var font = Font("Andale Mono", 18);

try {
	Document.listener.background_(Color.black).stringColor_(fontcolor);
	Document.postColor_(fontcolor);
	Document.listener.font_(font);
	Document.listener.bounds_(Rect(300, 8, 1000, 240));
};
}.value
);




// boot the server and make a noise

Server.default.waitForBoot {
	{ Pulse.ar(1000 * [1, 1.01]) * EnvGen.kr(Env.linen(0, 0.05, 0, 0.2), doneAction: 2) }.play;
}


