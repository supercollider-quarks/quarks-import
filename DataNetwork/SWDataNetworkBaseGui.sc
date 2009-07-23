// =====================================================================
// SenseWorld DataNetwork base GUI
// =====================================================================

SWDataNetworkBaseGui {

	var <>network;
	var w,button1,button2;

	*new{ |network|
		^super.new.network_( network ).init;
	}

	init{
		w = Window.new( "SenseWorld DataNetwork", Rect( 0, 0, 400, 90 ) );
		w.view.decorator = FlowLayout.new( Rect( 0, 0, 400, 100), 5@5, 5@5 );

		button1 = Button.new( w, Rect( 0, 0, 190, 80)).states_( [["View data nodes"]]).action_( {network.makeGui} ).font_( GUI.font.new( "Helvetica", 20));

		button2 = Button.new( w, Rect( 0, 0, 190, 80)).states_( [["View clients"]]).action_( { 
			if ( network.osc.isNil )
			{ 
				"no OSC interface present, adding OSC interface to network".warn;
				network.addOSCInterface;
			};
			network.osc.makeGui;
		} ).font_( GUI.font.new( "Helvetica", 20));

		w.front;
	}
}