OscGroupClient {
	var serveraddress, username, password, groupname, grouppassword, serverport, localtoremoteport, 
		localtxport, localrxport, responders, <pid, <netAddr;
	classvar <>program;
	
	*new {arg serveraddress, username, password, groupname, grouppassword, serverport = 22242,
			localtoremoteport = 22243, localtxport = 22244, localrxport;
		^super.newCopyArgs(serveraddress, username, password, groupname, grouppassword, serverport,
			localtoremoteport, localtxport, localrxport).init;
		}
			
	*initClass {
		program = "./OscGroupClient"
		}
	
	init {
		responders = IdentityDictionary.new;
		localrxport.isNil.if({
			localrxport = NetAddr.langPort;
			});
		}
		
	join { 
		(program + serveraddress + serverport + localtoremoteport + localtxport + localrxport +
			username + password + groupname + grouppassword).unixCmdInferPID({arg id;
				pid = id;
				pid.notNil.if({
					("Successfully joined the OscGroup at" + serveraddress).postln;
					netAddr = NetAddr("localhost", localtxport);
					UI.registerForShutdown({("kill" + pid).systemCmd});
					}, {
					"Check connections... the client could not be started".warn
					});
				})
		}
		
	close {
		("kill" + pid).systemCmd;
		pid = nil;
		responders.do({arg resp; resp.postln; resp.remove});
		responders = IdentityDictionary.new;
		}
		
	sendMsg { arg ... msg;
		msg[0] = this.formatSymbol(msg[0]);
		netAddr.sendMsg(*msg);
	}
	
	addResp { arg id, function;
		pid.notNil.if({
			// there are two ways to pass in the symbol id... fix it here
			id = this.formatSymbol(id);
			responders.add(id -> OSCresponderNode(nil, id, function).add);
			}, {
			"You must register your client on an OscGroupServer before you add a responder".warn
			})
		}
		
	removeResp {arg id;
		id = this.formatSymbol(id);
		responders[id].remove;
		responders[id] = nil;
		}
		
	formatSymbol {arg symbol;
		var str;
		str = symbol.asString;
		(str[0] == $/).if({
			^str.asSymbol;
			}, {
			^("/"++str).asSymbol
			})
		}

}
