/***************************************************************************
 *   This file is part of libdatanetwork                                   *
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

#include "datanetwork.h"


namespace SWDataNetwork {

/**
	Create an instance of the DataNetwork
*/
DataNetwork::DataNetwork( )
{
	registered = false;

	osc = NULL;
}

/**  create an osc client interface for it: */
void DataNetwork::createOSC( const char *hostip, const char *myport, const char *myName )
{
	osc = new DataNetworkOSC( hostip, myport, myName );
	osc->setNetwork( this );
}

/**  turn on/off debugging */
void DataNetwork::debug( bool onoff )
{
	osc->postDebug = onoff;
}

/**  register with the host: */
void DataNetwork::registerMe()
{
	osc->registerClient();
}

/**  unregister with the host: */
void DataNetwork::unregisterMe()
{
	osc->unRegisterAtHost();
}

/*
Try to find the host back, when receiving a quit message from the host
If the host quits, while this client is running, registered will be set to "false"
The autosearch tries to reconnect to the host every second, this takes place in the osc server thread, so doesn't hold up the rest of the program however for any interaction with the network (creating new nodes, new subscriptions), you should check whether the datanetwork is registered, before executing them. You can do this manually, or use the waitForRegister flag with any of the methods to interact with the network
*/
// void DataNetwork::autoSearch( bool onoff )
// {
// 	osc->autoSearchHost = onoff;
// }

/**
	This delays execution, until the network is registered with the host, used if the waitForRegister flag is set in the call to any of the methods.
*/
void DataNetwork::checkRegistered()
{
	while ( !registered ){
		registerMe();
		sleep(1);
	}
}

/** 
Query all there is to know about the network
clients, expected nodes, nodes, slots, setters, and subscriptions
 */

void DataNetwork::query( bool waitForRegister )
{
  if ( waitForRegister ){
	checkRegistered();	
	}
  osc->queryAll();
}

/** 
	Remove a node from the network (only works if the node was created by this client)
*/
void DataNetwork::removeNode( int id, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	if ( nodeExists( id ) ){
		osc->removeNode( id );
	}
}

/**
	Create a node with a specific label, number of slots, and a specific type
*/
int DataNetwork::createNode( int id, const char * label, int noslots, int type, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	if ( !nodeExists( id ) ){
		addNode( id, label );
		DataNode * node = getNode( id );
		if ( node != NULL ){
			node->setLabel( label );
			node->setNoSlots( noslots );
			node->setType( type );
			osc->addExpectedPlus( id, noslots, label, type );
		}
		return 0;
	}
	return -1;
}

/**
	Get the data from a node a single time
*/
void DataNetwork::updateNode( int id, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	osc->getNode( id );
}
/**
	Get the data from a slot a single time
*/

void DataNetwork::updateSlot( int id, int sid, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	osc->getSlot( id, sid );
}

/**
	Label a node (only works if this client is the setter of the node)
*/
void DataNetwork::labelNode( int id, const char *label, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	if ( !nodeExists( id ) ){
		return;
	}
	DataNode * node = getNode( id );
	if ( node != NULL ){
		osc->labelNode( id, label );
	}
}

/**
	Label a slot from a specific node (only works if this client is the setter of the node)
*/
void DataNetwork::labelSlot( int id, int sid, const char *label, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	if ( !nodeExists( id ) ){
		return;
	}
	DataNode * node = getNode( id );
	if ( node != NULL ){
// 		node->setSlotLabel( id, label ); // this is actually set locally, once we have a message back from the host
		osc->labelSlot( id, sid, label );
	}
}

/**
	Subscribe to a node and get the data from the node each time there is new data
*/
void DataNetwork::subscribeNode( int id, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	osc->subscribeNode( id );
}

/**
	Subscribe to a slot and get the data from the slot each time there is new data
*/
void DataNetwork::subscribeSlot( int id, int sid, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	osc->subscribeSlot( id, sid );
}

/**
	Unsubscribe to a node and stop receiving data from it
*/
void DataNetwork::unSubscribeNode( int id, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	osc->unSubscribeNode( id );
}

/**
	Unsubscribe to a slot and stop receiving data from it
*/
void DataNetwork::unSubscribeSlot( int id, int sid, bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	osc->unSubscribeSlot( id, sid );
}

/**
	Get a pointer to a specific node, in order to use it in your program
*/
DataNode * DataNetwork::getNode( int id )
{
	nodeMap::iterator iter = dataNodes.find( id );
	if ( iter != dataNodes.end() )
		return iter->second;
	else
		return NULL;
}

/**
	Get a pointer to a specific slot, in order to use it in your program
*/
DataSlot * DataNetwork::getSlot( int id, int sid )
{
	nodeMap::iterator iter = dataNodes.find( id );
	if ( iter != dataNodes.end() )
		return iter->second->getSlot( sid );
/*		if ( sid < iter->second->slotsize ){
			return &(iter->second->dataSlots[sid]);
		} else {
			return NULL;
		}
*/
	else
		return NULL;
}


/**
	(private)
	add a node to the nodeMap
*/
void DataNetwork::addNode( int id, const char *label )
{
	DataNode * node = new DataNode( id, label );
	node->setNetwork( this );
	// insert the node into the map
	dataNodes.insert( make_pair(id, node) );
}

/**
	(private)
	remove a node from the nodeMap
*/
void DataNetwork::removeNodeFromMap( int id )
{
	nodeMap::iterator iter = dataNodes.find( id );
	if ( iter != dataNodes.end() )
		{
		// iter->second free
		dataNodes.erase( iter );
		}
}

/**
	(Set from the OSC implementation)
	Sets whether or not the client is registered
*/
void DataNetwork::setRegistered( bool reg )
{
	registered = reg;
	if ( registered )
		refreshNodeSettings();
}

/**
	This re-subscribes all nodes and adds all nodes of which this client is the setter. 
*/
void DataNetwork::refreshNodeSettings()
{
	DataNode * node;
	nodeMap::iterator iter;
    for( iter = dataNodes.begin(); iter != dataNodes.end(); ++iter ) {
      node = iter->second;
		if ( node->subscribed ){
			subscribeNode( node->id );
		}
		node->resubscribeSlots();
		if ( node->setter ){
			osc->addExpectedPlus( node->id, node->size(), node->label.data(), node->type );
			node->send();
		}
    }
}

/**
	This sends data to the network. This function is called from DataNode::send
*/
void DataNetwork::sendData( int id, int size, float data[], bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	osc->setData( id, size, data );
}

/**
	This sends data to the network. This function is called from DataNode::send
*/
void DataNetwork::sendData( int id, int size, string data[], bool waitForRegister )
{
	if ( waitForRegister ){
		checkRegistered();	
	}
	osc->setData( id, size, data );
}

bool DataNetwork::nodeExists( int id )
{
	nodeMap::iterator iter = dataNodes.find( id );
	if ( iter != dataNodes.end() )
		return true;
	else
		return false;
}

void DataNetwork::removedNode( int id )
{
	removeNodeFromMap( id );
}


void DataNetwork::dataForNode( int id, int size, float * data )
{
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setData( size, data );
	}
}

void DataNetwork::dataForNode( int id, int size, string * data )
{
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setData( size, data );
	}
}

void DataNetwork::dataForSlot( int id, int sid, float data )
{
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setSlotValue( sid, data );
	}
}

void DataNetwork::dataForSlot( int id, int sid, string data )
{
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setSlotValue( sid, data );
	}
}

void DataNetwork::subscribedNode( int id )
{
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setSubscribed( true );
	}
}

void DataNetwork::unSubscribedNode( int id )
{
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setSubscribed( false );
	}
}

void DataNetwork::subscribedSlot( int id, int sid )
{
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setSlotSubscribed( sid, true );
	}
}

void DataNetwork::unSubscribedSlot( int id, int sid )
{
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setSlotSubscribed( sid, true );
	}
}
void DataNetwork::expectedNode( int id, const char *label )
{
	if ( !nodeExists( id ) ){
		addNode( id, label );
	}
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setLabel( label );
	}
}
void DataNetwork::infoNode( int id, const char *label, int noslots, int type )
{
	if ( !nodeExists( id ) ){
		addNode( id, label );
	}
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setLabel( label );
		node->setNoSlots( noslots );
		node->setType( type );
	}
}

void DataNetwork::infoSlot( int nodeid, int id, const char *label, int type )
{
	// set characteristic of a slot inside a node
	if ( !nodeExists( nodeid ) ){
		return;
	}
	DataNode * node = getNode( nodeid );
	if ( node != NULL ){
		node->setSlotLabel( id, label );
		node->setSlotType( id, type );
	}
}

  void DataNetwork::setterNode( int id, const char *label, int noslots, int type )
{
	if ( !nodeExists( id ) ){
		addNode( id, label );
	}
	DataNode * node = getNode( id );
	if ( node != NULL ){
		node->setLabel( label );
		node->setSetter( true );
		node->setNoSlots( noslots );
		node->setType( type );
	}
}


DataNetwork::~DataNetwork()
{
	// free all nodes?
	dataNodes.clear();
	if ( osc != NULL )
		delete osc;
}


}
