For detailed installation information, look at http://docs.sensestage.eu/puredata-installation-and-general-usage


Sense Stage Documentation: Pure Data client
2009-12-15
Joseph Thibodeau

Here is what you need to know in order to use the Pure Data patches for interaction with the SenseWorld Datanetwork:

1. Assumptions
- The senseworld server is already running (the Pd patches won't be useful without an operational server). If you haven't gotten the server going, please refer to its documentation and get it up and running.
- You must know the IP address of the datanetwork server.

2. Getting started
- The first patch you should look at is called "dn.makenode-help.pd". It gives you a basic example of how to create a data node using the "dn.makenode" object and how to subscribe to this node on the network using "dn.node" objects.
- You will have to modify the "pv dn-host <IP address>" object to reflect the IP address of your datanetwork server. Save, close, and re-open the patch if you have to modify this object so that you don't have to do it again.
- When you open the patch you should see a bunch of messages in the Pd console. Some of these will be prefaced with "MAKENODE" and "NODE" (corresponding to the respective objects), and others will be prefaced with "SENSEWORLD" (corresponding to the server). If all goes well the "dn.makenode" and "dn.node" objects will both connect and register to the datanetwork. If you don't see anything from "SENSEWORLD" then you aren't connected to the server (or the server is not running).
- If you change the numbers going into the "pack" object above "dn.makenode", you should see the data appearing in the Pd console as messages from the server as well as in the message box underneath the "nd.node" object.

3. Going further
- Using "dn.makenode" and "dn.node" objects in a custom patch, you can get data from any node for which you know the ID number, and you can publish any data you like by making your own node. You can even take data from a node, process it using your Pd skills, and publish the output using a "dn.makenode" object --- then other clients in any of the supported languages (max/MSP, processing, C++, Pd, SuperCollider) can use your processed data for further processing. Have fun and be safe!


-------------
MiniHiveOsc pd patch

This patch shows a simple example of how to interact with minihiveosc.py for interaction with a Sense/Stage MiniBee network, without using the SenseWorld DataNetwork.

---
(c) 2012 - Marije Baalman
