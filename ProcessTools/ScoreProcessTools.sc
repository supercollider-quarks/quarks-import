+ Score {
	recordNRTWaitUntilDone { arg oscFilePath, outputFilePath, inputFilePath, sampleRate = 44100, headerFormat =
		"AIFF", sampleFormat = "int16", options, completionString="", duration = nil, action = nil, checkevery=1;
		this.writeOSCFile(oscFilePath, 0, duration);
		
		unixCmdWaitUntilDone(program + " -N" + oscFilePath + (inputFilePath ? "_") + "\""++outputFilePath++"\""
		 	+ sampleRate + headerFormat + sampleFormat +
			(options ? Score.options).asOptionsString
			+ completionString, 
			// The extra args:
			action, checkevery);
	}
	
	*recordNRTWaitUntilDone { arg list, oscFilePath, outputFilePath, inputFilePath, sampleRate = 44100, 
		headerFormat = "AIFF", sampleFormat = "int16", options, completionString="", duration = nil, action = nil, checkevery=1;
		
		this.new(list).recordNRTWaitUntilDone(oscFilePath, outputFilePath, inputFilePath, sampleRate = 44100, 
		headerFormat, sampleFormat, options, completionString, duration, 
		// The extra args:
		action, checkevery);
	}
}