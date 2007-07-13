/*
See the Help file for [UnixTools]
*/

+ String {

/*
"sleep 10".unixCmd
"sleep 10".unixCmdInferPID({|pid| ("The PID is" + pid).postln})
*/

unixCmdInferPID { |action|
	var cmdname, pipe, line, lines, prepids, postpids, diff, pid;
	Task({
		
		// cmdname is the command name we'll be monitoring
		cmdname = this.split($ ).first;
		
		// List processes before we launch
		pipe = Pipe.new("ps -xc -o \"pid command\" | grep" + cmdname + "| sed 's/" ++ cmdname ++ "//; s/ //g'", "r");
		line = pipe.getLine;
		while({line.notNil}, {lines = lines ++ line ++ "\n"; line = pipe.getLine; });
		pipe.close;
		prepids = if(lines.isNil, [], {lines.split($\n).collect(_.asInteger)});
		//("PIDS pre:  " + prepids).postln;
		
		// Run the cmd! NB use .unixCmd because we don't want to wait for a result (as would .systemCmd).
		this.unixCmd;

		0.1.wait;
		
		// List processes after we launch
		lines = "";
		pipe = Pipe.new("ps -xc -o \"pid command\" | grep" + cmdname + "| sed 's/" ++ cmdname ++ "//; s/ //g'", "r");
		line = pipe.getLine;
		while({line.notNil}, {lines = lines ++ line ++ "\n"; line = pipe.getLine; });
		pipe.close;
		postpids = if(lines.isNil, [], {lines.split($\n).collect(_.asInteger)});
		//("PIDS post: " + postpids).postln;
		
		
		// Can we spot a single addition?
		diff = difference(postpids, prepids).select(_ > 0);
		if(diff.size != 1, {
			("String.unixCmdInferPID - unable to be sure of the " ++ cmdname ++ " PID").warn;
			pid = nil;
		}, {
			pid = diff[0];
		});
		
		action.value(pid);
		
	}).play(AppClock);
} // End .unixCmdInferPID

unixCmdThen { |action, checkevery=0.3|
	this.unixCmdInferPID({|pid|
		if(pid.isNil, {
			("String:unixCmdThen - could not infer PID, therefore couldn't wait until done!").error;
		}, {
			Task({
				checkevery.wait;
				while({ pid.isPIDRunning }, { checkevery.wait });
				action.value(pid);
			}).play(AppClock);
		});
	});
} // End .unixCmdThen


} // End String


//////////////////////////////////////////////////////////////////////////////////////////////////////////////

+ Integer {

isPIDRunning {
	var pipe, lines, line;

	pipe = Pipe.new("ps -p" ++ this, "r");
	lines = "";
	line = pipe.getLine;
	while({line.notNil}, {lines = lines ++ line ++ "\n"; line = pipe.getLine; });
	pipe.close;
	
	//"ps fetched the following:".postln;
	//lines.postln;
	
	^lines.contains(this.asString);
} // End isPIDRunning

} // End Integer

