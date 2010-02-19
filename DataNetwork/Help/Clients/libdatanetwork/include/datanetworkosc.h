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

#ifndef DATANETWORKDATANETWORKOSC_H
#define DATANETWORKDATANETWORKOSC_H

#include <string>
#include <lo/lo.h>
#include <curl/curl.h>

#include "oscin.h"

using namespace std;

namespace SWDataNetwork {

class DataNetwork;

/**
	@author Marije Baalman <nescivi@gmail.com>
*/

// arguments of the handler functions
#define handlerArgs const char* path, const char* types, lo_arg** argv, int argc, lo_message msg, void* user_data

class DataNetworkOSC : public OSCServer {
public:
    DataNetworkOSC(const char *hostip, const char *myport, const char *myName );
	void setNetwork( DataNetwork* network );
	void addMethods();

	void registerClient();

//------- messages to the host ------
	void registerAtHost( const char *name );
	void unRegisterAtHost();

	void queryAll();
	void queryExpected();
	void queryNodes();
	void querySlots();
	void queryClients();
	void querySetters();
	void querySubscriptions();

	void subscribeNode( int id );
	void subscribeSlot( int id, int sid );
	void unSubscribeNode( int id );
	void unSubscribeSlot( int id, int sid );

	void getNode( int id );
	void getSlot( int id, int sid );

	void labelNode( int id, const char *label );
	void labelSlot( int id, int sid, const char *label );

	void removeNode( int id );

	void addExpected( int id );
	void addExpectedPlus( int id, int size, const char *label, int type=0 );

	void setData( int id, int size, float data[] );
	void setData( int id, int size, string data[] );

	void subscribeAll();
	void unSubscribeAll();

	void removeAll();
//------- end messages to the host----

	bool postDebug;
// 	bool autoSearchHost;

    ~DataNetworkOSC();

private:

	static DataNetwork* datanetwork;

	int findHost( const char *hostip );
	void sendPong();
// 	void searchHost();

	void getFloatData( float * data, const char *types, lo_arg** argv, int argc );

	void getStringData( string * data, const char *types, lo_arg** argv, int argc );

	void resetHost( const char *hostip, int hostport );

	void sendSimpleMessage( const char *path );

	void handleError( int errorid, const char *errorMsg, const char *prefix );

	lo_address hostAddress;
	int port;

// ----------- osc handlers ---------------

	static int pingHandler( handlerArgs );

	static int announceHandler( handlerArgs );
	static int quitHandler( handlerArgs );

	static int errorHandler( handlerArgs );
	static int warnHandler( handlerArgs );

	static int registeredHandler( handlerArgs );
	static int unregisteredHandler( handlerArgs );

	static int infoExpectedHandler( handlerArgs );
	static int infoNodeHandler( handlerArgs );
	static int infoSlotHandler( handlerArgs );
	static int infoClientHandler( handlerArgs );
	static int infoSetterHandler( handlerArgs );

	static int subscribedNodeHandler( handlerArgs );
	static int subscribedSlotHandler( handlerArgs );
	static int unSubscribedNodeHandler( handlerArgs );
	static int unSubscribedSlotHandler( handlerArgs );

	static int dataNodeHandler( handlerArgs );

	static int dataSlotHandler( handlerArgs );
	static int dataStringSlotHandler( handlerArgs );

	static int removedNodeHandler( handlerArgs );

	static int genericHandler( handlerArgs );
//----------- end osc handlers ------------


//---------------- begin curl -----------------
/// getting the port of the host with curl
	int curlHost( const char *hostip );

	static int gethostport(char *data, size_t size, size_t nmemb, std::string *buffer);
//---------------end curl ---------------------

	string name;
	string hostIP;

};

}

#endif
