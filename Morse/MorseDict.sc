		// based on hairi vogel's morse class
	


		strings = [$., $-, " / "];

		codes =  Dictionary[ 	
			$a -> [0,1],
			$z -> [1,1,0,0],
			
			$? -> [0,0,1,1,0,0],
			$- -> [1,0,0,0,0,1],
			$/ -> [1,0,0,1,0],  
			
			$( -> [1,0,1,1,0],  
			$) -> [1,0,1,1,0,1],  
			$" -> [0,1,0,0,1,0],  	// inverted commas, quotation marks
			$= -> [1,0,0,0,1],  

			$+ -> [1,0,1,0,1],  	// cross, plus
			$* -> [1,0,0,1],		// multiply, also x
			$@ -> [0,1,1,0,1,0],	// commercial at
			
			'understood' -> [0,0,0,1,0],  
			'error' -> [0,0,0,0,0,0,0,0],  
			'invitationToTransmit' -> [1,0,1],  
			'wait' -> [0,1,0,0,0],  
			'endOfWork' -> [0,0,0,1,0,1],  
			'startingSignal' -> [1,0,1,0,1],  

			$  -> [2]				// stop - is that so? 
	 	// single char
	*signs { |char| ^strings[this.at(char)].join }

	 	// a word
	*wordSigns { |word| ^word.as(Array).collect { |char| this.signs(char) ++ " "; }.join }
	 
	*fromAscii { arg code; ^this.at(code) }
	
	*at { arg code; 
		 if (code.isKindOf(Symbol).not) { code = code.asAscii.toLower };
	
	*keys { ^codes.keys.asArray.sort }
	
	*postKeys { this.keys.printcsAll.postcs }