/*
Interaction Class of the server-side implementation of TUIO 
	http://tuio.lfsaw.de/
	http://modin.yuri.at/publications/tuio_gw2005.pdf

Author: 
	2004, 2005, 2006
	Till Bovermann 
	Neuroinformatics Group 
	Faculty of Technology 
	Bielefeld University
	Germany
*/

TUIOInteraction {
	var parts;
	// abstract class
	*new{|... parts| ^super.new.initInt(parts)}
	initInt{|argParts| parts = argParts}
	update {
//		parts.every(_.visible).if{
			this.interaction(parts.any{|item| item.visible.not}.not);
//		}
	}
	interaction {|isValid|
		postf("TUIOInteraction: % <-> %\n", *parts.collect(_.id));
	}
}

TUIOIDistance : TUIOInteraction {
	classvar <>distFunc;
	interaction {|isValid|
		var posA, posB;
		
		posA = parts[0].pos[0..2].reject(_.isNil);
		posB = parts[1].pos[0..2].reject(_.isNil);
		
		distFunc.value(posA.collect{|val, i| (val - posB[i]).squared}.sum.sqrt, isValid, parts);
	}
}