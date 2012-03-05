/**
 * Copyright (c) 2011 Marije Baalman. All rights reserved
 *
 * This file is part of the MiniBee API library.
 *
 * MiniBee_API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniBee_API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniBee_API.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <Wire.h>
// 
#include <LIS302DL.h>
#include <ADXL345.h>
#include <TMP102.h>
#include <BMP085.h>
#include <HMC5843.h>

#include <XBee.h>
#include <MiniBee_APIn.h>

#include <NewSoftSerial.h>

NewSoftSerial mySerial(5, 3);

MiniBee_API mbee = MiniBee_API();

unsigned long time;

/// this will be our parser for the data messages we will receive from other nodes:
/// msg[0] and msg[1] will be msg type ('d') and message ID
/// the remainder are the actual contents of the message
void dataMsgParser( uint8_t * msg, uint8_t size, uint16_t source ){
     time = millis() - time;
     mySerial.print( time );
     mySerial.print( "," );
     mySerial.print( source );
     mySerial.print( "," );
     for ( uint8_t i=1; i<size; i++ ){
	mySerial.print( msg[i], DEC );
	if ( i < (size-1) ){
	    mySerial.print( "," );
	}
     }
     mySerial.println("");
}

void setup() {
  mbee.setRemoteConfig( 0 ); // no remote config, we're just listening in
  mbee.setup( 57600, 'D' );

  mbee.setCustomPin( 5, 0 );
  mbee.setCustomPin( 3, 1 );
  
  // set the data rate for the NewSoftSerial port
  mySerial.begin(57600);
  //   mySerial.println("Hello, world?");
  
  delay(1000); //Wait a second for OpenLog to init

  time = millis();
  
  // set the data message function
  mbee.setDataCall( &dataMsgParser );

}

void loop() {
  mbee.loopStep( false );
  delay(5);
}
