
"simple JIT startup example.".postln;

	// choose which server to use, 
	// e.g. for teaching, take internal, so scope works
Server.default = s = Server.internal;
{ try { Server.local.window.close } }.defer(0.5);

s.options
	.numAudioBusChannels_(1024)
	.zeroConf_(false)			// don't wait for zeroconf when booting server
	.memSize_(8192 * 32)		// memSize is in kB, so 8 * 32 = 256 MB for soundfiles, buffers

//		// keep several combinations of audio devices + numChannels
//	.device_("MicLineOut")		// an aggregate device
//	.hardwareBufferSize_(512)	// 512 is default
//	.numInputBusChannels_(8)	// 8 is default
//	.numOutputBusChannels_(8)	// 8 is default
	
		// comment out unused devices.
//	.device_("Fireface 400")
//	.hardwareBufferSize_(128)
//	.numInputBusChannels_(18)
//	.numOutputBusChannels_(18)
;

	
q = q ? (); 		// make a global dictionary for everything

p = Ndef.dictFor(s);	// make a proxyspace for Ndefs on the default server
p.push(s); 			// and push it - so Ndef(\asd) === ~asd now.

s.quit; 	// in case the server is still running, quit it.

			// decide what should happen after server has booted: 
s.doWhenBooted({ 
	1.wait;
	"server is booted - do further setup here...".postln;
				// make some JIT guis: 
	try { q.pxmix.win.close }; q.pxmix = ProxyMixer(p, 16);
	try { q.tdg.win.close }; q.tdg = TdefAllGui(16); q.tdg.moveTo(210, 600);
	try { q.pdg.win.close }; q.pdg = PdefAllGui(16); q.pdg.moveTo(420, 600);

	1.wait; 
	
	Env.perc.test;	// and play a test sound if you like
	"simple JIT setup done.".postln;
});

Task({ 
	1.wait;	// wait till quit has happened - make longer if needed.
	s.boot;	// boot again
}).play(AppClock);

