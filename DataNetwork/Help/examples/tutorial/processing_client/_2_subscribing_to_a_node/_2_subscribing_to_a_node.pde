/**
 * SenseWorldDataNetwork Processing library example.
 *
 * Part of Sense/Stage 
 * http://sensestage.hexagram.ca
 *
 * example by Vincent de Belleval (v@debelleval.com)
 *
 * Please look through the reference folder for all specifics about the client
 */

import datanetwork.*;	//import the datanetwork pacakage
import datanetwork.javaosc.*;	//required for dnEvent(OSCMessages message)

DNConnection dn;	//DNConnection
DNNode node;	//DNNode


void setup() {
	size(200, 200);

	//Create a DNConnection.  Parameters are IP, outgoing port, incoming port, client name.
	dn = new DNConnection(this, "127.0.0.1", dn.getServerPort("127.0.0.1"), 6008, "p5Client");
	dn.setVerbo(4);	//set the verbosity to receive all messages but server pings.
	dn.register();	//register the client on the network.  Can be done anytime.
		
	//Create a DNNode.  Parameters are NodeId, number of slots, type, node name.
        //type: data type the node is expecting.  0 is float, 1 is String.
        node = new DNNode(2000, 2, 0, "p5Node");
	dn.addExpected(node);
}

// two variables to set the background color:
float bgred = 0;
float bggreen = 0;

void draw() {
	background( bgred, bggreen, 0);
}

/**
 * this dnEvent(String addr, floatp[] args) method receives the message's address as a String and all of its arguments in a String array.
 */
// here we filter for data coming from a node, and only use it if it comes from the node we subscribed to.
// we then use it to set the background color.
void dnEvent(String addr, float[] args) {
  if ( addr.equals( "/data/node" ) ){
    if ( args[0] == 2000 ){
      for ( int i=1; i<3; i++ ){
        println( "data slot "  + i + " is " + args[i] );
      }
      // assign the background color variables based on the data coming from the node:
      bgred = args[1];
      bggreen = args[2];
    }  
  }
}


//setting the node's data with the mouse posisiton.
void mouseMoved() {
	if(dn.isRegistered()) {	//nothing until client is registered and node is supposed to be set on the server.
		float[] mousePostion = {mouseX, mouseY};	//store mouse position coordinates.
		dn.setData(node	, mousePostion);	//set the mousePosition array on our node.
	}
}

// subscribe to node 2000 on a mouse click
void mouseClicked() {
    dn.subscribeNode( 2000 );   
//   println("subscribed to" + dn.subscribtion_list ); 
}


/**
 * Always close the DNConnection.  
 * It's optional to remove all the nodes created by the client but the sever currently will not recognize the client 
 * as the setter of its nodes if the client is launched again.
 */
void stop() {
  dn.unsubscribeAll();
  dn.close();	//unregisters the client and closes every port and connection.
}

