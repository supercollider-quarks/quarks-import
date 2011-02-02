Command8Ktl : MIDIKtl {
	classvar <>verbose = false;
	
	*defaultsDict {
			
		//preset for default MIDI mapping of Command8
		//select buttons were assigned cc 16, since by default they are not assigned any cc.
		// bank 1 is from 1 to 8, bank 2 is from 9 to 16
		// sliders  	sl1 		... sl16
		// encoders	enc1		... enc16
		// select		sel1		... sel16
		// solo		solo1	... solo16
		// mute 		mute1 	... mute16
		// eq,dynamics,insert,pan,page<, page> buttons
		//			button1	... button16		
		
		var dict = Dictionary.new;
		
		16.do{ |i|
		
		//sliders		
			dict.put( ("sl"++(i+1)).asSymbol, (i.asString++"_7").asSymbol );
				
		//encoders
			dict.put( ("enc"++(i+1)).asSymbol, (i.asString++"_10").asSymbol );
			
		//select
			dict.put( ("sel"++(i+1)).asSymbol, (i.asString++"_16").asSymbol );
		
		//solo
			dict.put( ("solo"++(i+1)).asSymbol, (i.asString++"_15").asSymbol );
		
		//mute
			dict.put( ("mute"++(i+1)).asSymbol, (i.asString++"_14").asSymbol );
		
		};
						
		//eq,dynamics,insert,pan,page<, page> buttons
		(122..127).do{ |i,j|
			dict.put( ("button"++(j+1)).asSymbol, ("0_"++i).asSymbol )
		};
		
		^dict
	}
	
	*makeDefaults { 	
		defaults.put(this,this.defaultsDict);
		
	}
	
}

Command8PagedKtl : MIDIPagedKtl {
	classvar <>verbose = false;
	
	*makeDefaults { 		
		defaults.put(this,Command8Ktl.defaultsDict);	
	}
	
	init{		
		super.init;
		
		//page < and  page > buttons are used to flip between scenes
		this.mapAll(\button5,{ this.previousScene; });
		this.mapAll(\button6,{ this.nextScene; });
	}
	
}
