/***************************************************************************
 *   This file is an example program using libdatanetwork                  *
 *                                                                         *
 *   Copyright (C) 2009 by Marije Baalman                                  *
 *   nescivi _at_ gmail _dot_ com                                          *
 *                                                                         *
 *   This library is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

/// TODO: access data in datanode/dataslot!


#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include <iostream>
#include <cstdlib>

/// this is the only file we need to include to interact with the datanetwork
#include "datanetwork.h"

using namespace std;

/// this is the namespace used by the datanetwork classes
using namespace SWDataNetwork;

int main(int argc, char *argv[])
{
  DataNode * node;
  DataNode * node2;
  DataSlot * slot;
  DataNetwork * dn;

  if ( argc < 4 ){
    printf( "Start this DataNetwork example client with 3 arguments:\n");
    printf( "host ip (e.g. 127.0.0.1), the IP address of the host running the DataNetwork server\n");
    printf( "port (e.g. 7000), the port this client will use for OSC messages\n");
    printf( "name (e.g. example_client), the name by which this client will be identified in the DataNetwork\n");
    printf( "For example:\n");
    printf( "%s 127.0.0.1 7000 DatanetworkExampleClient\n", argv[0]); 
    return EXIT_SUCCESS;
  }

// create a data network:
  dn = new DataNetwork();
// create an osc client interface for it:
  dn->createOSC( argv[1], argv[2], argv[3] );

  dn->debug( false ); // is false by default, but turn on if you want

// register with the host:
  dn->registerMe();

  printf( "registering\n" );

  sleep( 3 );
  printf( "query\n" );

// query all there is to know about the network
  dn->query();

  sleep( 3 );
  printf( "get data once\n" );
  
// get data from a node once
  dn->updateNode( 5, true );
// get data from a slot once
  dn->updateSlot( 5, 2, true );

  sleep( 2 );
  printf( "subscribing\n" );

// subscribe to a node, so data is always received when it is changed:
  dn->subscribeNode( 5, true );
// subscribe to a slot, so data is always received when it is changed (doesn't really make sense if you are already subscribed to the full node, you should only use it when you only need one specific slot of a node):
  dn->subscribeSlot( 5, 1, true );

    sleep( 1 );
  
   /// access the data from a node:
   node2 = dn->getNode( 5 );
   if ( node2 != NULL ){
    float * nodeData = node2->getData(); // use getStringData() if the node's type is a string
    for ( int i=0; i<node2->size(); i++ ){
	printf( "Node 5, slot %i, value %f\n", i, nodeData[i] );
    }
    // NOTE copy the data from the array if you want threadsafe access!

    /// access the data from a slot:
    slot = dn->getSlot( 5, 1 );
    float slotData = slot->getValue(); // use getStringValue() if the slot's type is a string
    printf( "Node 5, slot 1, value %f\n", slotData );
   }
   
   sleep(1);
   
   printf( "creating a node\n" );

	// create a node:
  dn->createNode( 4, "world", 5, 0, true );
	// label one of its slots:
  dn->labelSlot( 4, 2, "hithere", true );

  
float dummydata[] =  {0.1, 0.3, 0.4, 0.5, 0.6};

	// get a reference to the node:
   node = dn->getNode( 4 );
	// set data to the node:
   node->setData( 5, dummydata);
	// send the data to the network:
	node->send( true );

	sleep( 3 );
float dummydata2[] =  {0.4, 0.34, 0.4, 0.2, 0.6};
   node->setData( 5, dummydata2);
	node->send( true );

  sleep( 5 );
  printf( "removing the node\n" );

// remove a node from the network
  dn->removeNode( 4, true );

  sleep( 5 );

  printf( "unregistering\n" );
// unregister from the host:
  dn->unregisterMe();

  sleep( 5 );

  return EXIT_SUCCESS;
}
