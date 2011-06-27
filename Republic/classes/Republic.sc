/* Problems / todo : 

** sometimes myServer does not stop on Cmd.period, 
	maybe it appears not local by accident?
	// test: 

{ SinOsc.ar * 0.1 }.play(r.myServer);
thisProcess.stop;	// should stop sinosc.

** r.requestSynthDefs - ask all others for their synthdefs; 
	do it in r.statusData ? 

** decouple server a little more 
	- even if it does not boot, do everything else; 
	when it has booted, inform the server from the lang.

hasJoined
*/

Republic : SimpleRepublic {
	
	classvar <maxID = 31;

	var <serverPort = 57109;
	var <servers, <>latency;
	var <republicServer, <clientID, <>options;
	var <synthDefResp, synthDefSendCmd;
	var <requestResp;
	var <allClientIDs;
	var <>onJoinServerAction;
	// var <time;
	
	var <>usesRandIDs = true, <>usesSeparateState = true, <>postSource = true;
	
	init {
		super.init;
		servers = ();
		allClientIDs = ();
		synthDefSendCmd =  republicName.asString ++ "/synthDef";
		options = this.defaultServerOptions;
		// time = RepublicStandardTime(this).permanent_(true).phaseWrap_(4);
	}
	
	canJoin { |name, argClientID|

		if (argClientID.isNil) { 
			warn("Republic - % could not join: clientID was nil.".format(name));
			^false;
		};
		^super.canJoin(name)
	}
	
	join { | name, argClientID, argServerPort |

		if (name.isNil) { 
			warn("Republic:join - name was nil! Please pick a name when you join.");
			^this
		};
		
		name = name.asSymbol;

		if (allClientIDs.includes(argClientID)) {
			warn("clientID % is already in use."
				.format(argClientID)
			);
			argClientID = nil; 
		};

		argClientID = argClientID ?? { this.nextFreeID }; 
				
		if (this.canJoin(name, argClientID)) {
			if (this.hasJoined(nickname)) { this.leave };
			nickname = name;
			clientID = argClientID;
			serverPort = argServerPort ? serverPort;
			this.initEventSystem;
			// time.startListen;
			this.joinStart;			
		}
	}
	
	leave { |free = false| 
		
		synthDefResp.remove; 
		requestResp.remove;
		servers.do { |sharedServer| sharedServer.freeAll };
		try { servers.at(nickname).quit };
		servers.keysValuesDo(this.removeServer(_));
		
		"servers: %\n".postf(servers);
		clientID = nil;
	//	time.stopListen;
	//	servers = ();
				
		super.leave(free);	// keep lurking by default
	}
			
	sendServer { |name ... args|
		// "send server %\nmessages: %\n".postf(name, [args]);
		this.prSendWithDict(servers, name, [args], latency)
	}
	
	sendServerBundle { |latency, name ... args|
		// "send server %\nmessages: %\n".postf(name, args);
		this.prSendWithDict(servers, name, args, latency)
	}
	
	addSynthDef { | synthDef |
		var name = synthDef.name.asSymbol;
		this.sendSynthDef(\all, synthDef);
	}
	
	removeSynthDef { | name |
		// synthDefs.removeAt(name);
		// maybe remove all synthDescs for everyone?
		^this.notYetImplemented
	}
			
	// private implementation
	
	nextFreeID { 
		var res;
		if (usesRandIDs) { 
			res = (0..maxID).removeAll(allClientIDs.keys).choose;
		} { 
			res = (0 .. maxID).detect { |i| allClientIDs.includes(i).not };
		}; 
		if (res.isNil) { 
			warn("Republic: no more free clientIDs!"); 
			^nil	
		} { ^res }
	}

	addParticipant { | key, addr, otherClientID, config |
		
		super.addParticipant(key, addr); 
		allClientIDs.put(key, otherClientID);
			
		if (clientID.notNil) { // I play with my own id on remote server
			this.addServer(key, addr, serverPort, config);
		} { 
			warn("no clientID for participant %!\No server made yet.".format(key));
		};
		\\ time.update;
	}
			
	removeParticipant { | key |
		super.removeParticipant(key);
		allClientIDs.removeAt(key);
		this.removeServer(key);
		
		// time.update;
	}
	
		
	addServer { | name, addr, port, config |
		
		var server = Server.named.at(name);
		var isLocalAndInRepublic = addrs.at(nickname).notNil and: {
				addrs.at(nickname).hostname == addr.hostname
		};
		// if nonexistent or in the republic already but local, we just add the server anew
		// this only happens when we keep several republics with the same name locally
		if(server.isNil or: isLocalAndInRepublic) { 
			server = this.makeNewServer(name, addr, port, config);
		} {
			this.displayServer(server);
			"\nRepublic (%): server % already there - fine.\n".postf(nickname, name);
			"You may still need to boot the server\n".postln;
		};
			
		servers.put(name, server);
		server.sendBundle(nil, ['/error', 0], ['/notify', 1]);
		
		// send all synthdefs to the new server
		this.shareSynthDefs(name);		
	}
	
	removeServer { | who |
		var oldserv = servers.removeAt(who).remove;
		defer { try { oldserv.window.close } };
	}
	
	displayServer { |server|
		defer { try { server.makeGui } };	
	}
		
	makeNewServer { | name, addr, port, config |
			
			var newServer, serverOptions;
					
			addr = addr.addr.asIPString;
			if(name == nickname) { addr = "127.0.0.1" }; // replace by loopback
			port = port ?? { serverPort };
			
			// make a new server representation
			newServer = SharedServer.new(name, NetAddr(addr, port), clientID: clientID);
			
			"\nRepublic (%): new server added: %\n".postf(nickname, name);
			
			if(name == nickname) {
				newServer.options = options;
				this.displayServer(newServer);
				newServer.waitForBoot { 
					0.5.wait; 
					this.informServer;
					0.5.wait;
					onJoinServerAction.value(this, newServer) 
				};		
			} {
				newServer.options = this.defaultServerOptions(config);
				"\nRepublic (%): server % not my own, assume running.\n".postf(nickname, name);
				newServer.serverRunning_(true);
			};
			
			newServer.latency = latency; // not sure if compatible
			^newServer
	}
	
	defaultServerOptions { |config|
		var op = SharedServerOptions.fromConfig(config);
		var maxNumClients = (maxID + 1);
		if(usesSeparateState) {
			op.numAudioBusChannels = 128 * maxNumClients;
			op.numControlBusChannels = 4096 * maxNumClients;
			op.memSize = 8192 * maxNumClients;
			op.numClients = maxID;
		};
		^op
	}
		
	statusData {
		var res = super.statusData;
		options !? { res = res ++ options.asConfig }; // send hardware info etc.
		^res
	}
	
		// sharing synth defs - should be called after a new server is up.
		
	shareSynthDefs { | who |
		fork {
			rrand(0.0, 1.0).wait; 		// wait to distribute network traffic
			this.synthDescs.do { |synthDesc|
					var sentBy, bytes, doSend, sourceCode;
					synthDesc.metadata !? { 
						sentBy = synthDesc.metadata.at(\sentBy);
						bytes = synthDesc.metadata.at(\bytes);
						sourceCode = synthDesc.metadata.at(\sourceCode);
					};
					
					doSend = sentBy.notNil and: { bytes.notNil }
							and: {
								nickname == sentBy	// was me
								or: { addrs.at(sentBy).isNil } // has left
							};
					if(doSend) {
						this.sendSynthDefBytes(who, synthDesc.name, bytes, sourceCode, false);
						1.0.rand.wait; // distribute load
					}
			}
		}
	}
	
	sendSynthDef { | who, synthDef, toServer = true |
		this.sendSynthDefBytes(who, synthDef.name, 
				synthDef.asBytes, synthDef.asCompileString, toServer);
		if(verbose) { "Republic (%): sent synthdef % to %\n".postf(nickname, synthDef.name, who) };
	}
	
	sendSynthDefBytes { | who, defName, bytes, sourceCode, toServer = true |
		this.send(who, synthDefSendCmd, nickname, defName, sourceCode, bytes);
		if (toServer) { this.sendServer(who, "/d_recv", bytes) };
	}

		// maybe these should be cached locally
		// the gui asks for them often, and incoming 
		// synthdefs could just go here directly.
	synthDescs {
		^SynthDescLib.global.synthDescs.select { |desc| 
			desc.metadata.notNil and: { desc.metadata[\sentBy].notNil } 
		}
	}
	
	informServer { |fromSourceCode = false| 
		var synthdescs = this.synthDescs;
		"informing r.server % of % synthdefs.".format(this.myServer, synthdescs.size).postln;
			// interpret is unsafe
		if (fromSourceCode) { 
			"interpreting synthdefs: ".post;
			synthdescs.do { |desc| 
				(desc.name.asCompileString ++ " ").post;
				desc.metadata[\sourceCode].interpret.add;
			};		
			"\n".postln; 
			
			^this;
		};
			"sending bytes of: ".post; 
			// using bytes should be safe.
		synthdescs.do { |desc|
			var whereFrom = desc.metadata[\sentBy];
			var bytes = desc.metadata[\bytes];
			if (bytes.notNil) { 
				(desc.name.asCompileString ++ " ").post;
				this.myServer.sendMsg("/d_recv", bytes);
			} { 
				warn("//// Republic:informServer - no bytes in metaData for %."
					" Maybe do: \ntt r.informServer(true);".format(desc.name));
				
			};
		};
		"\n".postln; 
	}
	
	storeRemoteSynthDef { | sentBy, name, sourceCode, bytes |

		var lib = SynthDescLib.global;
		var stream = CollStream(bytes);
		var dict = SynthDesc.readFile(stream, false, lib.synthDescs);
		var args = [\instrument, name];
		var synthDesc = lib.at(name);
		
		try { this.manipulateSynthDesc(name) };
		
		// add the origin and SynthDef data to the metadata field
		synthDesc.metadata = (sentBy: sentBy, bytes: bytes, sourceCode: sourceCode);
		
//		// post a prototype event:	
//		dict.at(name).controls.do { |ctl| 
//			args = args.add(ctl.name.asSymbol).add((ctl.defaultValue ? 0.0).round(0.00001))
//		};

		"\n// SynthDef \"%\" added:".postf(name);
		if(postSource) {
			sourceCode = format("%.share", sourceCode);
			if(sourceCode.find("\n").notNil) { sourceCode = format("(\n%\n);", sourceCode) };
			"\n%\n".postf(sourceCode);
		};
		().putPairs(args).postcs;
	}
	
	manipulateSynthDesc { | name |
		var synthDesc = SynthDescLib.at(name);
		var ctls = synthDesc.controlNames;
		// this is done to guarantee that the "where" parameter is collected from the event
		synthDesc !? {
			if(ctls.isNil or: { synthDesc.controlNames.includes("where").not }) {
				synthDesc.controlNames = synthDesc.controlNames.add("where");
				synthDesc.controls = synthDesc.controls.add(
					ControlName().name_("where")
						.index_(synthDesc.controls.size)
						.rate_('scalar').defaultValue_(0);
				);
				synthDesc.makeMsgFunc; // make msgFunc again
			};
		}
	}
	
	s { ^republicServer ? Server.default }
	
	myServer {
		^servers.at(nickname)
	}
	
	asTarget {
		^this.myServer.asTarget
	}
	
	homeServer {
		this.deprecated(thisMethod);
		^this.myServer
	}

	initEventSystem { 
		synthDefResp = OSCresponderNode(nil, synthDefSendCmd, { | t, r, msg |
				
				var sentBy = msg[1];
				var name = msg[2];
				var sourceCode = msg[3];
				var bytes = msg[4];

				this.storeRemoteSynthDef(sentBy, name, sourceCode, bytes)
			}).add;
		
		requestResp = OSCresponderNode(nil, \request, { | t, r, msg |
			var sentBy = msg[1];
			msg.postcs;
			if (msg[2] == \shareSynthDefs) { 
				this.shareSynthDefs(sentBy);
			};	
		}).add;
		
			// this is only an interface to the event system
			republicServer = RepublicServer(this, clientID); 
	}

//	hasJoined { |name| ^servers.at(name).notNil }

	requestSynthDefs { 
		addrs.do(_.sendMsg(\request, nickname, \shareSynthDefs));
	}
	
		// make servers whenever necessary
	assemble {
		super.assemble; 
				// make servers if clientID was added, 
				// and server is missing
		if (clientID.notNil) { 
			presence.keys.do { |key|
				var serv = servers[key];
				if (serv.isNil) { this.addServer(key, addrs[key]) }
			};
		}
	}
}

// this can be sneaked into events.
// again, no gated synths currently

RepublicServer {
	var <republic, <clientID;
	var nodeAllocator, audioBusAllocator, controlBusAllocator, bufferAllocator;
	var <>verbose = false;
	
	*new { |republic, clientID|
		^super.newCopyArgs(republic, clientID).init
	}
	
	init {
		this.newAllocators;
	}

	sendBundle { |time ... msgs|
	//	"sendBundle".postln;
		// possible optimize: if all wheres are the same, send them as one bundle
		var serverNames = msgs.collect (this.findWhere(_));
		
			// multichan expand, e.g. to spread chords across servers
		serverNames.do { |servname, i| 
			republic.sendServerBundle(time, servname, msgs[i]);
		}
	}
	
	sendMsg { |... msg|
	//	"sendMsg".postln;
		republic.sendServerBundle(nil, this.findWhere(msg), msg);
	}
		// supports numbers in namelist as well.
	findWhere { |msg|
		var indexWhere, where;
	//	"findWhere - msg and index are : ".postln; msg.postcs;
		indexWhere = msg.indexOf(\where); 
		if (indexWhere.isNil) {
			^nil
		};
		
		where = msg[indexWhere + 1];
		^where
	}
	
	nodeAllocator { ^nodeAllocator.value }
	audioBusAllocator { ^audioBusAllocator.value }
	controlBusAllocator { ^controlBusAllocator.value }
	bufferAllocator { ^bufferAllocator.value }
	
	nextNodeID { |where|
		 ^nodeAllocator.value.alloc
		// ^-1
	}
	
	asTarget {
		^republic.asTarget	
	}
	
	latency {
		^republic.latency
	}
	
	name {
		^republic.nickname
	}
	
	// this needs some work, other allocators..
	newAllocators {
		nodeAllocator = { republic.myServer.nodeAllocator };
		audioBusAllocator = { republic.myServer.audioBusAllocator };
		controlBusAllocator = { republic.myServer.controlBusAllocator };
		
	}

	doesNotUnderstand { |selector, args|
		if (verbose) { 
			"RepublicServer forwards message '%' with args % to myServer.\n".postf(selector, args);
		};
		if (args.isNil) { 
			^republic.myServer.perform(selector);
		} { 
			^republic.myServer.perform(selector, args);
		};
	}
	
}



+ SynthDef {

	share { | republic |
		republic = republic ? Republic.default;
		if(republic.isNil) {
				////////////////////////////////////////////////// v
			if(Main.versionAtMost(3, 3)) { this.memStore } { this.prAdd }
		} {
			republic.addSynthDef(this)
		}
	}
	
	*unshare { |name|
		^this.notYetImplemented;
	}
	
	/*	
	*unshare { |name|
		var republic = republic ? Republic.default;
		if(republic.notNil) { 
			republic.removeSynthDef(this)
		}
		
	}*/
	
	add { this.share; }
	
		// temp hack
	prAdd { arg libname = \global, completionMsg, keepDef = true;
		var	lib, desc = this.asSynthDesc(libname, keepDef);
		libname ?? { libname = \global };
		lib = SynthDescLib.getLib(libname);
		lib.servers.do { |each|
			each.value.sendMsg("/d_recv", this.asBytes, completionMsg.value(each))
		};
	}

	store { this.prStore.share; }
	
		// temp hack
	prStore { arg libname=\global, dir(synthDefDir), completionMsg, mdPlugin;
		var lib = SynthDescLib.getLib(libname);
		var file, path = dir ++ name ++ ".scsyndef";
		if(metadata.falseAt(\shouldNotSend)) {
			protect {
				var bytes, desc;
				file = File(path, "w");
				bytes = this.asBytes;
				file.putAll(bytes);
				file.close;
				lib.read(path);
				lib.servers.do { arg server;
					server.value.sendMsg("/d_recv", bytes, completionMsg)
				};
				desc = lib[this.name.asSymbol];
				desc.metadata = metadata;
				SynthDesc.populateMetadataFunc.value(desc);
				desc.writeMetadata(path);
			} {
				file.close
			}
		} {
			lib.read(path);
			lib.servers.do { arg server;
				this.loadReconstructed(server, completionMsg);
			};
		};
	}
}


