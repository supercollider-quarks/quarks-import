Verbosity {
	var <>level = 0;

	*new{ |level|
		^super.newCopyArgs(level);
	}

	value{ |lev,string|
		if ( level >= lev ){
			string.postln;
		}
	}

}

/*
USAGE:

~verbose = Verbosity.new(2);

~verbose.value( 3, "level 3 verbosity");

~verbose.level_(3)
*/