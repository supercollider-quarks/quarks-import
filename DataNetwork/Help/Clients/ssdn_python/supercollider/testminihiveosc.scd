// ============================================
// SuperCollider Code to work with MiniHiveOSC
// ============================================

// check SC's osc port (use as argument for minihiveosc
NetAddr.langPort;

// in 3.4:
~o1 = OSCresponderNode( nil, "/minibee/data", { |t,r,msg| msg.postln; } ).add;
~o2 = OSCresponderNode( nil, "/minibee/info", { |t,r,msg| msg.postln; } ).add;

// in 3.5.~dev:
OSCdef( \minibeedata, { |data,time,src|  data.postln; }, '/minibee/data' );
OSCdef( \minibeeinfo, { |data,time,src| [data,time,src].postln; }, '/minibee/info' );


n = NetAddr.new( "127.0.0.1" , 57600 );

// set output to the minibee:
n.sendMsg( "/minibee/output", 1, 0 );

// set configuration to a minibee:
n.sendMsg( "/minibee/configuration", 1, 1 ); // minibee id, config id
n.sendMsg( "/minibee/configuration", 2, 1 ); // minibee id, config id
n.sendMsg( "/minibee/configuration", 3, 1 ); // minibee id, config id
n.sendMsg( "/minibee/configuration", 4, 1 ); // minibee id, config id
n.sendMsg( "/minibee/configuration", 5, 1 ); // minibee id, config id

// define configuration:
n.sendMsg( "/minihive/configuration", 10, "testconfig", 1, 25, 2, 1, 'A1', 'AnalogIn', 'licht', 'D11', 'DigitalIn', 'beweging', 1, 'ADXL345', 'versnelling' );

// set configuration to a minibee:
n.sendMsg( "/minibee/configuration", 1, 10 ); // minibee id, config id


// saving and loading:
n.sendMsg( "/minihive/configuration/save", "rmhiveconfig.xml");
n.sendMsg( "/minihive/configuration/load", "rmhiveconfig.xml");


// queries are not yet implemented by minihiveosc:
//n.sendMsg( "/minihive/configuration/query", 1 );
//n.sendMsg( "/minihive/configuration/pin/query", 1, 'A1' );
//n.sendMsg( "/minihive/configuration/twi/query", 1, 1 );


// pin by pin, twi by twi configuration:
n.sendMsg( "/minihive/configuration/pin", 1, 'A1', 'AnalogIn', 'Light');
n.sendMsg( "/minihive/configuration/twi", 1, 1, 'TMP102', 'Temperature');
n.sendMsg( "/minihive/configuration/twi", 1, 2, 'ADXL345', 'Versnelling');

n.sendMsg( "/minihive/configuration/pin", 1, 'A1', 1, 'Light');
n.sendMsg( "/minihive/configuration/twi", 1, 2, 1, 'Versnelling');
