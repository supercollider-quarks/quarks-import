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

#ifndef DATANETWORKDATANODE_H
#define DATANETWORKDATANODE_H

#include <cstdlib>
#include <string>

#include "dataslot.h"

using namespace std;

namespace SWDataNetwork {

class DataNetwork;

/**
	@author Marije Baalman <nescivi@gmail.com>
*/
class DataNode{
public:
    DataNode( int ident, const char *myLabel );

	void send( bool waitForRegister = false );

	void setData( int size, float * indata );
	void setData( int size, string * indata );

	void setNetwork( DataNetwork * nw );

	void setLabel( const char *myLabel );
	void setSetter( bool amSetter );
	void setSubscribed( bool amSubscribed );
	void setNoSlots( int noslots );
	void setType( int tp );

	void setSlotSubscribed( int id, bool subs );
	void setSlotType( int id, int tp );
	void setSlotLabel( int id, const char *myLabel );
	void setSlotValue( int id, float value );
	void setSlotValue( int id, string value );

	void resubscribeSlots();

    ~DataNode();

	bool subscribed;
	bool setter;
	int id;
	int type;
	string label;

	DataNetwork * datanetwork;

	int slotsize;
	DataSlot * dataSlots;
	float * data;
	string * stringData;
};

}

#endif
