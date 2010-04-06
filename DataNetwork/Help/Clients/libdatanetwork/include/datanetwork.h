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

#ifndef DATANETWORKDATANETWORK_H
#define DATANETWORKDATANETWORK_H

#include <cstdlib>

#include <string>
#include <map>

#include "datanetworkosc.h"
#include "datanode.h"

namespace SWDataNetwork {

typedef std::map<int, DataNode*> nodeMap;

/**
	@author Marije Baalman <nescivi@gmail.com>
*/
class DataNetwork{
public:

	// use from client:

    DataNetwork( );

	void createOSC( const char *hostip, const char *myport, const char *myName );

	int createNode( int id, const char * label, int noslots, int type, bool waitForRegister = false );
	void removeNode( int id, bool waitForRegister = false );

	void subscribeNode( int id, bool waitForRegister = false );
	void subscribeSlot( int id, int sid, bool waitForRegister = false );
	void unSubscribeNode( int id, bool waitForRegister = false);
	void unSubscribeSlot( int id, int sid, bool waitForRegister = false );

	void updateNode( int id, bool waitForRegister = false );
	void updateSlot( int id, int sid, bool waitForRegister = false );

	void labelNode( int id, const char *label, bool waitForRegister = false );
	void labelSlot( int id, int sid, const char *label, bool waitForRegister = false );

	void query( bool waitForRegister = false );
	void registerMe();
	void unregisterMe();

	void checkRegistered();
	void autoSearch( bool onoff );

	void refreshNodeSettings();

	DataNode * getNode( int id );
	DataSlot * getSlot( int id, int sid );

	bool registered;
	
	void debug( bool onoff );

// ------- END - use in client ------

// ------- in response to osc messages ------
	void setRegistered( bool reg );

	void removedNode( int id );

	void subscribedNode( int id );
	void unSubscribedNode( int id );

	void subscribedSlot( int id, int sid );
	void unSubscribedSlot( int id, int sid );

	void expectedNode( int id, const char *label );
	void infoNode( int id, const char *label, int noslots, int type );
	void infoSlot( int nodeid, int id, const char *label, int type );
	void setterNode( int id, const char *label, int noslots, int type );

    void dataForNode( int id , int size, float * data  );
    void dataForNode( int id , int size, string * data  );

    void dataForSlot( int id , int sid, float data  );
    void dataForSlot( int id , int sid, string data  );

// ------- END - in response to osc messages ------

    ~DataNetwork();

	void sendData( int id, int size, float data[], bool waitForRegister = false );
	void sendData( int id, int size, string data[], bool waitForRegister = false );

	DataNetworkOSC * osc;

private:
	nodeMap dataNodes;

	void addNode( int id, const char *label  = "" );
	bool nodeExists( int id );
	void removeNodeFromMap( int id );

};

}

#endif
