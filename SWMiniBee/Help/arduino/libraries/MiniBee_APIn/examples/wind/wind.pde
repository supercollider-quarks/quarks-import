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

int adc[2];

int aup = 50;
int adown = 99;

int aup2 = 50;
int adown2 = 1;

int envtrack[2];

void timer_cb(){
  timer_callback();
}


void timer_callback()
{
  adc[0] = analogRead( 0 );
  adc[1] = analogRead( 1 );
  
  for ( uint8_t i=0; i<2; i++ ){
    if ( envtrack[i] < adc[i] ){
      envtrack[i] *= aup;
      envtrack[i] += aup2*adc[i];
    } else {
      envtrack[i]*= adown;
      envtrack[i] += adown2*adc[i];  
    }
    envtrack[i] = envtrack[i] / 100;
  }
  count++;
}

void setup(){
  Bee.setup(57600,'D');

  /// in our case we use pin 10 (2 bytes/int)
  Bee.setCustomPin( 14, 2); // A0 is pin 14
  Bee.setCustomPin( 15, 2); // A1 is pin 15

  Timer1.initialize(1000);         // initialize timer1, and set at 1 ms 
  
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

// int result[2];

void loop(){
  if ( count > 100 ){
    count = 0;
      // add our customly measured data to the data package:
    Bee.addCustomData( envtrack, 2 );
    // do a loop step of the remaining firmware: (no delay! conflict with TimerOne)
    Bee.loopStep( false );
  }
}
