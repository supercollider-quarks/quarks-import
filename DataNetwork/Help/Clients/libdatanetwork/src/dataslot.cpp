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

#include "dataslot.h"

namespace SWDataNetwork {

DataSlot::DataSlot( )
{
	value = 0.0;
	stringValue = string( "" );
	subscribed = false;
	slotCallback.Reset();
}

DataSlot::DataSlot( int ident, int type, const char *myLabel )
{
	id = ident;
	type = type;
	label = string( myLabel );
	subscribed = false;
	value = 0.0;
	stringValue = string( "" );
	slotCallback.Reset();
}

void DataSlot::setNode( DataNode * dn )
{
	datanode = dn;
}

void DataSlot::setValue( float v )
{
    value = v;
    if ( slotCallback.IsSet() ){
      slotCallback( this );
    }
}

void DataSlot::setValue( string v )
{
    stringValue = v;
    if ( slotCallback.IsSet() ){
      slotCallback( this );
    }
}

void DataSlot::set( int ident, int type, const char *myLabel )
{
	id = ident;
	type = type;
	label = string( myLabel );

	value = 0.0;
	stringValue = string( "" );
}

void DataSlot::setSubscribed( bool amSubscribed )
{
	subscribed = amSubscribed;
}

void DataSlot::setType( int tp )
{
	type = tp;
}

void DataSlot::setLabel( const char *myLabel )
{
	label = string( myLabel );
}

/**
  Get the current value
*/
float DataSlot::getValue()
{
    return value;
}

/**
  Get the current string value
*/
string DataSlot::getStringValue()
{
    return stringValue;
}

string DataSlot::getLabel()
{
    return label;
}

int DataSlot::getType()
{
	return type;
}


int DataSlot::getID()
{
	return id;
}

bool DataSlot::isSubscribed()
{
	return subscribed;
}

DataSlot::~DataSlot()
{
}


}
