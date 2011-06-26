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

#include "datanetworkosc.h"
#include "datanetwork.h"


#include <iostream>
#include <lo/lo.h>
#include <string>

namespace SWDataNetwork {

	// Write any errors in here  
	static char curlErrorBuffer[CURL_ERROR_SIZE];
	// Write all expected data in here  
	static string curlBuffer;

// XXX:these have to be set at runtime
DataNetwork*   DataNetworkOSC::datanetwork = NULL;


DataNetworkOSC::DataNetworkOSC( const char *hostip, const char *port, const char *myName ) : OSCServer( port )
{
	name = string( myName );
	hostIP = string( hostip );

	postDebug = false;
// 	autoSearchHost = false;
}

void DataNetworkOSC::resetHost( const char *hostip, int hostport )
{
	hostIP = string( hostip );

	char hp [6];
	sprintf( hp, "%u", hostport );
  
	hostAddress = lo_address_new( hostip, hp );
	port = getPort();
	registerAtHost( name.data() );
}

void DataNetworkOSC::registerClient()
{
	port = getPort();
	// find the host
	if ( findHost( hostIP.data() ) == 0 ){
		registerAtHost( name.data() );
	}
}


// void DataNetworkOSC::searchHost()
// {
// 	while ( !datanetwork->registered ){
// 		sleep( 1 );
// 		registerClient();
// 	}
// }


void DataNetworkOSC::handleError( int errorid, const char *errorMsg, const char *prefix )
{
	switch ( errorid ){
		case 1:
			registerClient();
// 			break; // needed here?
		case 2:
			// only unregister when I think I'm not registered
			if ( !datanetwork->registered ){
				unRegisterAtHost();
				registerAtHost( name.data() );
			}
		case 4: // should have other handler
		case 5: // should have other handler
		case 6: // should have other handler
		case 12: // should have other handler
		case 13: // should have other handler
		case 14: // should have other handler
		case 3:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
			cout << prefix << " : " << errorMsg << "\n";
		break;
	}
}

DataNetworkOSC::~DataNetworkOSC()
{
	if ( hostAddress )
		lo_address_free( hostAddress );
}

void DataNetworkOSC::setNetwork( DataNetwork* network )
{
	DataNetworkOSC::datanetwork = network;
	addMethods();
}

int DataNetworkOSC::findHost( const char *hostip )
{
	if ( curlHost( hostip ) == 0 ){
		cout << "Found host at " << hostip << ", port " << curlBuffer << "\n";
		hostAddress = lo_address_new( hostip, curlBuffer.data() );
		start();
		return 0;
	} else {
		cout << "Could not find host port at " << hostip << "/SenseWorldDataNetwork" << "\n";
		return -1;
	}
}

//-------------- messages from the host -----------------

void DataNetworkOSC::addMethods()
{
	addMethod( "/datanetwork/announce", "si", announceHandler, this );
	addMethod( "/datanetwork/quit", "si", quitHandler, this );

	addMethod( "/ping", "is", pingHandler, this );

	addMethod( "/error", "ssi", errorHandler, this );
	addMethod( "/warn",  "ssi", warnHandler, this );

	addMethod( "/registered",   "is", registeredHandler, this );
	addMethod( "/unregistered", "is", unregisteredHandler, this );

	addMethod( "/info/expected", "is",   infoExpectedHandler, this );
	addMethod( "/info/expected", "i",   infoExpectedHandler, this );

	addMethod( "/info/node",     "isii", infoNodeHandler, this );
	addMethod( "/info/slot",     "iisi",  infoSlotHandler, this );
	addMethod( "/info/client",   "sis",  infoClientHandler, this );
	addMethod( "/info/setter",   "isii",  infoSetterHandler, this );

	addMethod( "/info/minibee",   "iii", infoBeeHandler, this );
	addMethod( "/mapped/minibee",   "ii", mapBeeHandler, this );

	addMethod( "/subscribed/node",   "isi",  subscribedNodeHandler, this );
	addMethod( "/unsubscribed/node",   "isi",  unSubscribedNodeHandler, this );
	addMethod( "/subscribed/slot",   "isii",  subscribedSlotHandler, this );
	addMethod( "/unsubscribed/slot",   "isii",  unSubscribedSlotHandler, this );

	addMethod( "/data/node", NULL,  dataNodeHandler, this );

	addMethod( "/data/slot", "iif",  dataSlotHandler, this );
	addMethod( "/data/slot", "iis",  dataStringSlotHandler, this );

	addMethod( "/removed/node",   "i",  removedNodeHandler, this );

 // The generic handler must be added last. 
    // Otherwise it would be called instead of the handlers. 
	addMethod( NULL, NULL, genericHandler, this );
}

int DataNetworkOSC::announceHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
		cout << "[DataNetworkOSC::announce] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	(( DataNetworkOSC* ) user_data)->resetHost( &(argv[0]->s), argv[1]->i );
	// reregister
	// reannounce nodes
    return 0;
}

int DataNetworkOSC::quitHandler( handlerArgs )
{
	cout << "[DataNetworkOSC::quit] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";
	// reset hostAddress
	// start checking hostport at regular intervals
    datanetwork->setRegistered( false  );
/*	if ( (( DataNetworkOSC* ) user_data)->autoSearchHost ){
		(( DataNetworkOSC* ) user_data )->searchHost();
	}*/
    return 0;
}

int DataNetworkOSC::errorHandler( handlerArgs )
{
	// print the error
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
		cout << "[DataNetworkOSC::error] from host: " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	(( DataNetworkOSC* ) user_data )->handleError( argv[ 2 ]->i, &(argv[ 1 ]->s), "Error" );

    return 0;
}

int DataNetworkOSC::warnHandler( handlerArgs )
{
	// print the error
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
		cout << "[DataNetworkOSC::warning] from host: " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	(( DataNetworkOSC* ) user_data )->handleError( argv[ 2 ]->i, &(argv[ 1 ]->s) , "Warning" );

	return 0;
}

int DataNetworkOSC::pingHandler( handlerArgs )
{
// 	cout << "[DataNetworkOSC::genericHandler] ping: " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	( ( DataNetworkOSC* ) user_data )->sendPong();
    return 0;
}

int DataNetworkOSC::registeredHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
 		cout << "[DataNetworkOSC:registered]: " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";
//     lo_address from = lo_message_get_source( msg );

	/// TODO: check if this was really for me

    datanetwork->setRegistered( true  );
    return 0;
}

int DataNetworkOSC::unregisteredHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
 		cout << "[DataNetworkOSC:unregistered]: " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";
//     lo_address from = lo_message_get_source( msg );

	/// TODO: check if this was really for me

    datanetwork->setRegistered( false  );
    return 0;
}


int DataNetworkOSC::infoExpectedHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::expected] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	if ( argc > 1 )
		datanetwork->expectedNode( argv[ 0 ]->i, &argv[ 1 ]->s);
	else
		datanetwork->expectedNode( argv[ 0 ]->i, "" );

    return 0;
}

int DataNetworkOSC::infoNodeHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::node] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	datanetwork->infoNode( argv[ 0 ]->i, &argv[ 1 ]->s, argv[ 2 ]->i, argv[ 3 ]->i );

    return 0;
}

int DataNetworkOSC::infoSlotHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::slot] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	datanetwork->infoSlot( argv[ 0 ]->i, argv[ 1 ]->i, &argv[ 2 ]->s, argv[ 3 ]->i );

    return 0;
}

int DataNetworkOSC::infoClientHandler( handlerArgs )
{
// 	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::client] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

// 	datanetwork->infoClient( &argv[ 0 ]->s, &argv[ 1 ]->i, &argv[ 2 ]->s );

    return 0;
}

int DataNetworkOSC::infoSetterHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::setter] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	datanetwork->setterNode( argv[ 0 ]->i, &argv[ 1 ]->s, argv[ 2 ]->i, argv[ 3 ]->i );

    return 0;
}

int DataNetworkOSC::infoBeeHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::bee] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	datanetwork->infoBee( argv[ 0 ]->i, argv[ 1 ]->i, argv[ 2 ]->i );

    return 0;
}

int DataNetworkOSC::mapBeeHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::mapbee] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	datanetwork->mappedBee( argv[ 0 ]->i, argv[ 1 ]->i );

    return 0;
}

int DataNetworkOSC::subscribedNodeHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::node] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	/// TODO: check if this was really for me

	datanetwork->subscribedNode( argv[ 2 ]->i );

    return 0;
}

int DataNetworkOSC::unSubscribedNodeHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::node] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	/// TODO: check if this was really for me

	datanetwork->unSubscribedNode( argv[ 2 ]->i );

    return 0;
}

int DataNetworkOSC::subscribedSlotHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::slot] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	/// TODO: check if this was really for me

	datanetwork->subscribedSlot( argv[ 2 ]->i , argv[ 3 ]->i );

    return 0;
}

int DataNetworkOSC::unSubscribedSlotHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::slot] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	/// TODO: check if this was really for me

	datanetwork->unSubscribedSlot( argv[ 2 ]->i , argv[ 3 ]->i );

    return 0;
}


int DataNetworkOSC::dataSlotHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
	    cout << "[DataNetworkOSC::slot] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	datanetwork->dataForSlot( argv[ 0 ]->i , argv[ 1 ]->i, argv[ 2 ]->f );

    return 0;
}

int DataNetworkOSC::dataStringSlotHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::slot] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	datanetwork->dataForSlot( argv[ 0 ]->i , argv[ 1 ]->i, &argv[ 2 ]->s );

    return 0;
}
int DataNetworkOSC::dataNodeHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
    	cout << "[DataNetworkOSC::node] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << argc << "\n";

	float * fdata;
	fdata = new float [argc-1];
	string * sdata;
	sdata = new string [argc-1];
	if ( types[1] == 's' ){
 		( ( DataNetworkOSC* ) user_data )->getStringData( sdata, types, argv, argc );
		datanetwork->dataForNode( argv[ 0 ]->i , argc-1, sdata );
	} else if ( types[1] == 'f' ){
		( ( DataNetworkOSC* ) user_data )->getFloatData( fdata, types, argv, argc );
	 	datanetwork->dataForNode( argv[ 0 ]->i , argc-1, fdata );
	}

	delete [] fdata;
	delete [] sdata;
    return 0;
}

void DataNetworkOSC::getFloatData( float * data, const char *types, lo_arg** argv, int argc )
{
	for( int i = 1; i < argc; ++i )
		{
			data[i-1] = argv[i]->f;
// 			*(data+(i-1)) = argv[ i ]->f;
// 			printf( "data %f, %f\n", argv[i]->f, data[i-1] );
		}
}

void DataNetworkOSC::getStringData( string * data, const char *types, lo_arg** argv, int argc )
{
	for( int i = 1; i < argc; ++i )
		{
			data[i-1] = &argv[ i ]->s;
// 			*(data+(i-1)) = &argv[ i ]->s;
		}
}

int DataNetworkOSC::removedNodeHandler( handlerArgs )
{
	if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
	    cout << "[DataNetworkOSC::node] " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";

	datanetwork->removedNode( argv[ 0 ]->i );

    return 0;
}

int DataNetworkOSC::genericHandler( handlerArgs )
{
  // if ( ( ( DataNetworkOSC* ) user_data )->postDebug )
  cout << "[DataNetworkOSC::genericHandler] No handler implemented for message: " + ( ( DataNetworkOSC* ) user_data )->getContent( path, types, argv, argc ) << "\n";
  return 0;
}

// ------------- end messages from the host -----------

//-------------- messages to the host -----------------

void DataNetworkOSC::sendPong()
{
	sendSimpleMessage( "/pong" );
}

void DataNetworkOSC::registerAtHost( const char *name )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name );

	sendMessage( hostAddress, "/register", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::unRegisterAtHost()
{
	sendSimpleMessage( "/unregister" );
}

void DataNetworkOSC::queryAll()
{
	sendSimpleMessage( "/query/all" );
}

void DataNetworkOSC::queryExpected()
{
	sendSimpleMessage( "/query/expected" );
}

void DataNetworkOSC::queryNodes()
{
	sendSimpleMessage( "/query/nodes" );}

void DataNetworkOSC::querySlots()
{
	sendSimpleMessage( "/query/slots" );
}

void DataNetworkOSC::queryClients()
{
	sendSimpleMessage( "/query/clients" );}

void DataNetworkOSC::querySetters()
{
	sendSimpleMessage( "/query/setters" );
}

void DataNetworkOSC::querySubscriptions()
{
	sendSimpleMessage( "/query/subscriptions" );
}

void DataNetworkOSC::queryBees()
{
	sendSimpleMessage( "/query/minibees" );
}

void DataNetworkOSC::subscribeAll()
{
	sendSimpleMessage( "/subscribe/all" );
}

void DataNetworkOSC::unSubscribeAll()
{
	sendSimpleMessage( "/unsubscribe/all" );
}


void DataNetworkOSC::removeAll()
{
	sendSimpleMessage( "/remove/all" );
}

void DataNetworkOSC::subscribeNode( int id )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );

	sendMessage( hostAddress, "/subscribe/node", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::subscribeSlot( int id, int sid )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );
	lo_message_add_int32( msg, sid );

	sendMessage( hostAddress, "/subscribe/slot", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::unSubscribeNode( int id )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );

	sendMessage( hostAddress, "/unsubscribe/node", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::unSubscribeSlot( int id, int sid )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );
	lo_message_add_int32( msg, sid );

	sendMessage( hostAddress, "/unsubscribe/slot", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::getNode( int id )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );

	sendMessage( hostAddress, "/get/node", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::getSlot( int id, int sid )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );
	lo_message_add_int32( msg, sid );

	sendMessage( hostAddress, "/get/slot", msg );

	lo_message_free( msg );
}


void DataNetworkOSC::labelNode( int id, const char *label )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );
	lo_message_add_string( msg, label );

	sendMessage( hostAddress, "/label/node", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::labelSlot( int id, int sid, const char *label )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );
	lo_message_add_int32( msg, sid );
	lo_message_add_string( msg, label );

	sendMessage( hostAddress, "/label/slot", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::mapBee( int nid, int mid, int type )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, nid );
	lo_message_add_int32( msg, mid );
// 	lo_message_add_string( msg, label );

	switch( type ){
	  case 0:
	     	sendMessage( hostAddress, "/map/minibee/output", msg );
		break;
	  case 1:
	     	sendMessage( hostAddress, "/map/minibee/pwm", msg );
		break;
	  case 2:
	     	sendMessage( hostAddress, "/map/minibee/digital", msg );
		break;
	};

	lo_message_free( msg );
}

void DataNetworkOSC::removeNode( int id )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );

	sendMessage( hostAddress, "/remove/node", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::addExpected( int id )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );

	sendMessage( hostAddress, "/add/expected", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::addExpectedPlus( int id, int size, const char *label, int type )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );
	lo_message_add_int32( msg, size );
	lo_message_add_string( msg, label );
	lo_message_add_int32( msg, type );

	sendMessage( hostAddress, "/add/expected", msg );

	lo_message_free( msg );
}

void DataNetworkOSC::setData( int id, int size, float data[] )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );

	for (int n=0; n<size; n++)
		lo_message_add_float( msg, data[n] );

	sendMessage( hostAddress, "/set/data", msg );

	lo_message_free( msg );	
}

void DataNetworkOSC::setData( int id, int size, string data[] )
{
	lo_message msg = lo_message_new();
	lo_message_add_int32( msg, port );
	lo_message_add_string( msg, name.data() );
	lo_message_add_int32( msg, id );

	for (int n=0; n<size; n++)
		lo_message_add_string( msg, data[n].data() );

	sendMessage( hostAddress, "/set/data", msg );

	lo_message_free( msg );	
}


void DataNetworkOSC::sendSimpleMessage( const char *path )
{
  lo_send_from( hostAddress, server, LO_TT_IMMEDIATE, path, "is", port, name.data() );
}

//-------------- end messages to the host -----------------


//------------- curl functions ----------------------

/// try to find the host at the right IP
int DataNetworkOSC::curlHost(const char *hostip)
{
	int result = 0;
	CURL *curl;
  	CURLcode res;

	curlBuffer = "";

	string url( hostip );
	url.append( "/SenseWorldDataNetwork" );

  curl = curl_easy_init();
  if(curl) {
     curl_easy_setopt(curl, CURLOPT_ERRORBUFFER, curlErrorBuffer);  
     curl_easy_setopt(curl, CURLOPT_URL, url.data() );  
     curl_easy_setopt(curl, CURLOPT_HEADER, 0);  
     curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1);  
     curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, gethostport);  
     curl_easy_setopt(curl, CURLOPT_WRITEDATA, &curlBuffer);  

    res = curl_easy_perform(curl);
 
    /* always cleanup */ 
    curl_easy_cleanup(curl);

    if (result == CURLE_OK)  
      {  
		return 0;
      }  
      else  
      {  
        cout << "Error: [" << result << "] - " << curlErrorBuffer;
		return -1;
      }  
  }
}

  
// This is the writer call back function used by curl  
int DataNetworkOSC::gethostport(char *data, size_t size, size_t nmemb, std::string *buffer)  
{  
  // What we will return  
  int result = 0;  
  
  // Is there anything in the buffer?  
  if (buffer != NULL)  
  {  
    // Append the data to the buffer  
    buffer->append(data, size * nmemb);  
  
    // How much did we write?  
    result = size * nmemb;  
  }  
  
  return result;  
}

//--------------- end curl ---------------

}
