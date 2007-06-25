// =====================================================================
// Arduino
//
// This class encapsulates communication with an Arduino I/O
// board. Subclasses handle the details of a specific serial protocol.
//
// When a message arrives (according to the protocol), the 'action'
// instance variable is invoked, with the expanded message contents.
//
// Messages are sent to the board with the 'send' method. it takes any
// number of arguments, the meaning of which again are again protocol
// specific.

// =====================================================================
// SimpleMessageSystem_example_1 (from the SMS distribution)
// see http://www.arduino.cc/playground/Code/SimpleMessageSystem

p = ArduinoSMS("/dev/tty.usbserial-A1000ecP", 115200); // was 9600 (changed by thor)
p.action = { |... msg| msg.postln };

// read digital inputs
p.send($r, $d);

// read analog inputs
p.send($r, $a);

// write digital input 13
p.send($w, $d, 13, 1);
p.send($w, $d, 13, 0);

// flash LED on digital pin 13
(
fork {
	8.do {
		p.send($w, $d, 13, 1);
		1.25.wait;
		p.send($w, $d, 13, 0);
		0.25.wait;
	}
}
)

// write analog output 0
p.send($w, $a, 0, 128);

// close port
p.close;

// =====================================================================
// SimpleMessageSystem_analog_read

p = ArduinoSMS("/dev/tty.usbserial-A1000ecP", 115200);
p.action = { |... msg| msg.postln };

// thor reading loop
(
fork {
	100.do {
		p.send($r, $a);
		0.1.wait;
	}
}
)

p.close;

// EOF
