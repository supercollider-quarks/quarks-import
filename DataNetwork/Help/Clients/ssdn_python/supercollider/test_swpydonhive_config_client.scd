// =====================================================================
// SuperCollider Workspace
// =====================================================================

y = SWDataNetworkClient.new( "127.0.0.1", "marije" );
y.verbose_( 2 )

y.configBee( 11, 31 );

y.deleteConfig( 31 );

y.createConfig( 31, "testconfig", 1, 25, 2, 1, 'A1', 'AnalogIn', 'licht', 'D9','DigitalOut', 'light', 1, 'ADXL345', 'versnelling' ); // minibee id, config id

y.saveConfig( "hello.xml" );
y.loadConfig( "hello.xml" );

y.addExpected( 111 );
y.setData( 111, [1] );

y.unmapBee( 111, 11 );
y.setData( 111, [1] );

y.setData( 111, [0] );




y.mapBee( 4, 2 );