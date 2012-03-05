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

/// Wire needs to be included if TWI is enabled
#include <Wire.h>
/// in the header file of the MiniBee you can disable some options to save
/// space on the MiniBee. If you don't the board may not work as it runs
/// out of RAM.
#include <LIS302DL.h>
#include <ADXL345.h>
#include <TMP102.h>
#include <BMP085.h>
#include <HMC5843.h>

/// includes needed in all cases
#include <XBee.h>
#include <MiniBee_APIn.h>

MiniBee_API Bee = MiniBee_API();

#include <TimerOne.h>

bool on = false;
int count = 0;

void timer_cb(){
  timer_callback();
}

void timer_callback()
{
  if ( on ){
//     PORTB |= B1000; 
     digitalWrite( 10, 1 );
     digitalWrite( 11, 0 );
  } else {
//     PORTB |= B0100; 
     digitalWrite( 10, 0 );
     digitalWrite( 11, 1 );    
  }
  on = !on;
  count++;
}

void setup(){
  Bee.setup(57600,'D');

  /// in our case we use pin 10 (2 bytes/int)
  Bee.setCustomPin( 14, 2); // A0 is pin 14
  Bee.setCustomPin( 10, 2);
  Bee.setCustomPin( 11, 0);

  pinMode( 10, OUTPUT );
  pinMode( 11, OUTPUT );

  Timer1.initialize(500);         // initialize timer1, and set at 0.5 ms 
  
  start_wave();
}

void start_wave()
{
  Timer1.attachInterrupt(timer_cb);  // attaches callback() as a timer overflow interrupt
}

void stop_wave()
{
  Timer1.detachInterrupt();  // detaches callback() as a timer overflow interrupt
}

int result[2];

void loop(){
  
  if ( count > 100 ){
    stop_wave();
    count = 0;
  
//     PORTB |= B1000; 
     digitalWrite( 10, 1 );
     digitalWrite( 11, 0 );
    result[0] = analogRead( 0 );
  
     digitalWrite( 10, 0 );
     digitalWrite( 11, 1 );
//     PORTB |= B0100; 
    result[1] = analogRead( 0 );
  
    start_wave();

    // add our customly measured data to the data package:
    Bee.addCustomData( result, 2 );
    // do a loop step of the remaining firmware: (no delay! conflict with TimerOne)
    Bee.loopStep( false );
  }
}
