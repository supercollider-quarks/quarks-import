// =====================================================================
// SuperCollider DataNetwork Client example
// =====================================================================

// create a client for a host on the local machine,
// arguments: ip address of the host, name for the client
x = SWDataNetworkClient.new( "127.0.0.1", "scclient");

/// ------- example 1 - creating a node ------------

// creating a node on the datanetwork:
x.addExpected( 1000, "scnode1", 5 ); // we will create a node with number 1000, name "scnode1" and 5 slots;

// set data to it:
x.setData( 1000, [1,2,3,4,5]/5 );

// doing this from a task (the data will change every second):
Tdef( \setdata, { loop{ x.setData(1000, Array.rand(5,0,1.0) ); 1.0.wait;} } );
Tdef( \setdata ).play;
// stop the task again:
Tdef( \setdata ).stop;

/// ------- example 2 - subscribing to a node and using the data ------

// subscribe to the node:
x.subscribeNode( 1000 );

// access the node and put a reference in a variable:

// by number:
~mynode = x.nodes[1000];

// by name:
~mynode = x[\scnode1];

// access the values of all slots of the node:
~mynode.value;
// this returns an array of five values, one value for each slot of the node.

// boot the server:
s.boot;

( // example synth:
SynthDef(\swexample,{ |out=0,amp=0.1,freq=400|
    Out.ar( out, SinOsc.ar( freq, 0, amp ) );
}).send(s)
)

// use it at synth instantiation (here we use the third element of the array, so the value of the third slot):
b = Synth.new( \swexample, [\amp, ~mynode.value.at( 2 ) ] );

// set it again:
b.set( \amp, ~mynode.value.at(2) );

// free the synth
b.free;

// instead of setting it manually each time, you can assign
// an action to the data node to do this automatically:

b = Synth.new( \swexample, [\amp,~mynode.value.at(2) ] );

~mynode.action= { |data| b.set( \amp, data[2] )};

// another method is to put the data automatically on a bus,
// and grab the data from there:

// reset the action:
~mynode.action = {};

// create a bus, this will be a multichannel bus:
~mynode.createBus(s);

// get a single channel subbus of the multichannel bus:
~myampbus = ~mynode.bus.subBus(2,1);

// map the amplitude of b to the bus:
b.map( \amp, ~myampbus );

b.free;

// alternatively, you can use In.kr in your synthdef to read
// from the bus:
(
SynthDef(\swexampleBus,{ |out=0,ampbus=0,freq=400|
    Out.ar( out, SinOsc.ar( freq, 0, In.kr( ampbus, 1 ) ) );
}).send(s)
)

b = Synth.new( \swexampleBus, [\ampbus, ~myampbus ] );

b.free;

// ------ example 3 - receive data from a MiniBee ------

// make sure the python client to interface with the minibees is running.

// subscribe to a datanode corresponding to a minibee:
x.subscribeNode( 1 );

// access its value:
x[\minibee1].value;
//or
x.nodes[1].value;

// in this case we have four values: 1 value from the LDR - light sensor, and three values from the accelerometer; they have labels:

x[\minibee1].slots.collect{ |it| it.key }.postln;

// you can use the label to directly access the slot data:
b = Synth.new( \swexample, [\amp, x[\minibee1_ldr].value ] );

// and automatically update it through the action:
x[\minibee1_ldr].action= { |data| b.set( \amp, data )};

// reset the action:
x[\minibee1_ldr].action={};

b.free;

// ------ example 4 - send data to a MiniBee ------

// sending data to a MiniBee is a two step process:
// 1. we create a datanode, with as many slots as the minibee has outputs
// 2. we send a message to the host telling that we want to map this datanode to the minibee.

// create a datanode:
x.addExpected( 1001, \scNodeOut1, 1 );

// set some data (value for pwm out, or analog out, should be between 0 and 255):
x.setData( 1001, [ 255 ] );

// tell the host to map it to the minibee:
x.mapBee( 1001, 1 );

// the LED will turn on!

x.setData( 1001, [ 127 ] );

x.setData( 1001, [ 0 ] );

(
Tdef( \fadeled, { loop{ 
	255.do{ |i| x.setData(1001, [i] ); 0.05.wait; };
	"full brightness!".postln;
	255.reverseDo{ |i| x.setData(1001, [i] ); 0.05.wait; };
	"completely off".postln;
} } );
);
Tdef( \fadeled ).play;

// stop the task again:
Tdef( \fadeled ).stop;


// ------ example 5 - use the data from the minibee to send to the minibee ----

// the value of the ldr is between 0 and 1, so we need to multiply it to get to a scale between 0 and 255, and we round it to be sure we have an integer value

x[\minibee1_ldr].action= { |data| x.setData( 1001, [ (data * 255).round(1)] )};

// now the range of the ldr data is roughly:
x[\minibee1_ldr].action= { |data| data.postln; };
// 0.58 to 0.98
// so we should rescale that:

x[\minibee1_ldr].action= {  };

x[\minibee1_ldr].value.specMap( [0.5,0.98], [0,255]);

x[\minibee1_ldr].action= { |data| [data, data.specMap( [0.5,0.98], [0,255]) ].postln; };

x[\minibee1_ldr].action= { |data| x.setData( 1001, [ data.specMap( [0.5,0.98], [0,255]) ] ); };

// ------ example 6 - receive data from one minibee, manipulate it and send to another minibee

// subscribe to another minibee:
x.subscribeNode( 2 );

x[\minibee2].slots.collect{ |it| it.key }.postln;

//x.mapBee( 1001, 1 );

x[\minibee2_ldr].action= { |data| x.setData( 1001, [ data.specMap( [0.58,0.98], [0,255]) ] ); };

