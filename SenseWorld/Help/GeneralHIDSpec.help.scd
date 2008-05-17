// =====================================================================
// GeneralHIDSpec
// =====================================================================

GeneralHIDSpec is a class to create access by names to slots of a GeneralHIDDevice.



// Look for the devices that are attached:

GeneralHID.buildDeviceList;

// Get the list of devices:

d = GeneralHID.deviceList;

// Check which devices have been found:

GeneralHID.postDevices;

// Pick the 6th device and open it and create an instance of it:

a = GeneralHID.open( d[6] )

// Get info on the device to see if it is the right one:

a.info;

// Start eventloop:

GeneralHID.startEventLoop

// Get the capabilities of the device in a readable format:

a.caps;

// See if data is coming in:
a.debug_( true );

b = GeneralHIDSpec.new( a );

( // defining the spec.
// trick: look at the debug output, by using each input thing on the gamepad and use the first two numbers posted as indexes
b.add( \rx, [3,0] );
b.add( \ry, [3,1] );
b.add( \lx, [3,2] );
b.add( \ly, [3,5] );

b.add( \b1, [1,288] );
b.add( \b2, [1,289] );
b.add( \b3, [1,290] );
b.add( \b4, [1,291] );
b.add( \b5, [1,292] );
b.add( \b6, [1,293] );
b.add( \b7, [1,294] );
b.add( \b8, [1,295] );

b.add( \b5, [1,292] );
b.add( \b6, [1,293] );
b.add( \b7, [1,294] );
b.add( \b8, [1,295] );

b.add( \cx, [3,16] );
b.add( \cy, [3,17] );

b.add( \bl, [1,296] );
b.add( \br, [1,297] );

b.add( \bj1, [1,298] );
b.add( \bj2, [1,299] );
)

// Stop it:
a.debug_( false );

// look at the map
b.map.postcs;

// examples of use:
s = Server.local.boot;


// create a bus
b.createBus( \rx, s );
b.createBus( \b1, s );


// simple example:
(
SynthDef( \hidbus_help, { |out=0,amp=0.5|
	Out.ar( out, SinOsc.ar( 300, 0, 0.2*(amp-0.5) ) );
}).load( s );
)

x = Synth.new( \hidbus_help );
x.map( \amp, b.at( \rx ).bus );
x.free;

( // a nicer version:
SynthDef( \hidbus_help, { |out=0,amp=0.5,amp2=0|
Out.ar( out, SinOsc.ar( 300, 0, 0.2*(amp-0.5).lag( 0.1 ) * amp2.lag(0.01,0.99)
) );
}).load( s );
)

x = Synth.new( \hidbus_help );
x.map( \amp, b.at( \rx ).bus );
x.map( \amp2, b.at( \b1 ).bus );
x.free;

( // an even nicer version:
SynthDef( \hidbus_help, { |out=0,freqadd=0,amp=0,fmmul=200|
Out.ar( out, SinOsc.ar( 300 + (freqadd.lag(0.2,1)*fmmul), 0, 0.2*amp.lag
(0.01,0.99) ) );
}).load( s );
)


// if you want to have buses for all the things defined in the spec, you can use:
b.createAllBuses( s );

(
x = [ Synth.new( \hidbus_help ), Synth.new( \hidbus_help ) ];
x[0].map( \freqadd, b.at( \ly ).bus );
x[0].map( \amp, b.at( \b8 ).bus );

x[1].map( \freqadd, b.at( \lx ).bus );
x[1].map( \amp, b.at( \b7 ).bus );

y = [ Synth.new( \hidbus_help, [\fmmul,400] ), Synth.new( \hidbus_help, [\fmmul,400] ) ];
y[0].map( \freqadd, b.at( \ry ).bus );
y[0].map( \amp, b.at( \b5 ).bus );

y[1].map( \freqadd, b.at( \rx ).bus );
y[1].map( \amp, b.at( \b6 ).bus );
)

// see what's going on on the server
s.queryAllNodes( true );

// free the synths
y.do{ |it| it.free; }; x.do{ |it| it.free; }; 



// To free all buses
b.freeAllBuses;

// Close the device after use:

a.close;

GeneralHID.stopEventLoop

