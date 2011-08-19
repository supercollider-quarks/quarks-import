// ============================================
// SuperCollider Code to work with SWPydonHive
// ============================================

NetAddr.langPort;

n = NetAddr("127.0.0.1", 57600 );
n = NetAddr("192.168.0.107", 57600 );
n = NetAddr("10.0.10.71", 57600 );


n.sendMsg( "/hello" );

~o1 = OSCresponderNode( nil, "/reply", { |t,r,msg| msg.postln; } ).add;


x = SWDataNetwork.new.createHost;
x.makeGui;

x.osc.clients.do{ |it| it.dump }

x.removeNode( 11 )

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

x.verbose_( 1 )

// set configuration to a minibee:
x.osc.clients[0].addr.sendMsg( "/configure/minibee", 1, 1 ); // minibee id, config id
~o1 = OSCresponderNode( nil, "/configured/minibee", { |t,r,msg| msg.postln; } ).add;

x.osc.hiveNotifier.interessees['/minihive/configuration/delete']['31']

x.osc.hiveNotifier.interessees['/map/minibee/output']['11']
x.osc.hiveNotifier.interessees['/unmap/minibee/output']

// set configuration to a minibee:
x.osc.clients[0].addr.sendMsg( "/minihive/configuration/create", 30, "testconfig", 1, 25, 2, 1, 'A1', 'AnalogIn', 'licht', 'D11', 'DigitalIn', 'beweging', 1, 'ADXL345', 'versnelling' ); // minibee id, config id
~o2 = OSCresponderNode( nil, "/minihive/configuration/created", { |t,r,msg| msg.postln; } ).add;

x.osc.clients[0].addr.sendMsg( "/minihive/configuration/delete", 30 ); // minibee id, config id
~o3 = OSCresponderNode( nil, "/minihive/configuration/deleted", { |t,r,msg| msg.postln; } ).add;
~o3a = OSCresponderNode( nil, "/minihive/configuration/deleted/error", { |t,r,msg| msg.postln; } ).add;


x.osc.clients[0].addr.sendMsg( "/minihive/configuration/save", "mysaveconfig.xml"  ); // minibee id, config id
~o4 = OSCresponderNode( nil, "/minihive/configuration/saved", { |t,r,msg| msg.postln; } ).add;

x.osc.clients[0].addr.sendMsg( "/minihive/configuration/load", "mysaveconfig.xml"  ); // minibee id, config id
~o5 = OSCresponderNode( nil, "/minihive/configuration/loaded", { |t,r,msg| msg.postln; } ).add;





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
