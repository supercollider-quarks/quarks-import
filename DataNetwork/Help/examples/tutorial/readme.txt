Tutorial

- start the host in SuperCollider
(host/BasicHost.scd)

  x = SWDataNetwork.new.createHost;
  x.makeGui;

- start the python client

$ python swpydonhive.py -c tutorialconfig.xml

(instead of "tutorialconfig.xml" use the full path to it; pydon/tutorialconfig.xml)

The configuration we are using is this:

<?xml version="1.0" ?>
<xml>
  <hive name="myprojectname">
    <minibee caps="7" id="1" libversion="3" name="" revision="D" serial="13A20040497F77" configuration="1">
    </minibee>

    <configuration id="1" message_interval="50" name="accelero" samples_per_message="1">
      <pin config="AnalogIn" id="A0" name="ldr"/>
      <pin config="AnalogOut" id="D9" name="led"/>
      <pin config="TWIClock" id="A5" name="None"/>
      <pin config="TWIData" id="A4" name="None"/>
      <twi device="ADXL345" id="1" name="accelero">
        <twislot id="0" name="x"/>
        <twislot id="1" name="y"/>
        <twislot id="2" name="z"/>
      </twi>
    </configuration>
  </hive>
</xml>

So we have one MiniBee, with XBee serial number 13A20040497F77, that uses configuration number 1.
This configuration is using one analog input, one analog output (PWM), and the accelerometer.

As you have started the python program, you will see the client appear in the clients list, opened when you press on "View Clients" in the host GUI.

- turn on the minibee(s)

You will see in the terminal of the python client, the message: "minibee 1 is configured", and you'll see the datanode appear in the network.
In the clients window, you will see that pydonhive has created a node 1 and is subscribed to it.
In the data node window, you will see the node appear.


---- a series of examples in the clients ----

1. Create a datanode and post data to it.
2. Subscribe to a datanode and use it's data.
3. Receive data from a MiniBee and use it.
4. Send data to a MiniBee.
5. Receive data from a MiniBee, manipulate it, and send it to the MiniBee.
6. Receive data from one MiniBee, manipulate it, and send it to another MiniBee.

For SuperCollider:	sc_client/SuperCollider_Client.scd
For PureData: 		pd_client/
For Processing:		processing_client/
For Max:		max_client/
