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

#include "datanode.h"

#include "datanetwork.h"

namespace SWDataNetwork {

DataNode::DataNode( int ident, const char *myLabel )
{
	id = ident;
	label = string( myLabel );
	type = 0;
	slotsize = 0;

	subscribed = false;
	setter = false;

	data = NULL;
	stringData = NULL;
	dataSlots = NULL;
}


void DataNode::setData( int size, float * indata )
{
	for ( int i=0; i<size; i++ ){
		data[i] = *(indata + i );
		dataSlots[i].value = *(indata + i );
	}
}

void DataNode::setData( int size, string * indata )
{
	for ( int i=0; i<size; i++ ){
		stringData[i] = *(indata + i );
		dataSlots[i].stringValue = *(indata + i );
	}
}

void DataNode::setType( int tp )
{
	type = tp;
}

void DataNode::setLabel( const char *myLabel )
{
	label = string( myLabel );
}

void DataNode::setNetwork( DataNetwork * nw )
{
	datanetwork = nw;
}

void DataNode::send( bool waitForRegister )
{
	if ( type == 0 ){
		datanetwork->sendData( id, slotsize, data, waitForRegister );
	} else if ( type == 1 ){
		datanetwork->sendData( id, slotsize, stringData, waitForRegister );
	}
}

void DataNode::setSlotLabel( int id, const char *myLabel )
{
	dataSlots[id].setLabel( myLabel );
}

void DataNode::setSlotType( int id, int tp )
{
	dataSlots[id].setType( tp );
}

void DataNode::setSlotValue( int id, float value )
{
	dataSlots[id].value = value;
}

void DataNode::setSlotValue( int id, string value )
{
	dataSlots[id].stringValue = value;
}

void DataNode::setSlotSubscribed( int id, bool amSubscribed )
{
	dataSlots[id].setSubscribed( amSubscribed );
}

void DataNode::setSetter( bool amSetter )
{
	setter = amSetter;
}

void DataNode::setSubscribed( bool amSubscribed )
{
	subscribed = amSubscribed;
}

void DataNode::resubscribeSlots()
{
	for ( int i=0; i < slotsize; i++ ){
		if ( dataSlots[i].subscribed ){
			datanetwork->subscribeSlot( id, i );
		}
	}
}

void DataNode::setNoSlots( int noslots )
{
	slotsize = noslots;
	dataSlots = new DataSlot [noslots];
	for ( int i=0; i < noslots; i++ ){
		dataSlots[i].set( i, type, "" );
		dataSlots[i].setNode( this );
	}
	if ( type == 0 ){
		data = new float [noslots];
	} else if ( type == 1 ){
		stringData = new string [noslots];
	}
}

DataNode::~DataNode()
{
	if ( data != NULL ){
		delete [] data;
	}
	if ( stringData != NULL ){
		delete [] stringData;
	}
	if ( dataSlots != NULL ){
		delete [] dataSlots;
	}
}


}
