/*
Implementing a general style of TUIObjects
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

/*
NOTES

...accessing TUIObject-freeSpace (P) actually not implemented
In order to add functionality to a TUIObject you have to subclass from it and reimplement TUIObject:update. More fancy stuff to come... 

... it is not allowed to change classID when object is alreadily instanciated (micht behave strange) 

*/

/*
	==Changes==

		2006-09-19	keys are now equiv. to object id's
		2006-09-15	added tStamp support
*/

TUIObject { 	// abstract class
	var <format; 			/// subset of {ixyzabcXYZABCmrP} or one entry as defined in formatDict.
	var <id;				/// object ID
	var <>classID;		/// object's class ID
	var <>pos;   			/// position  
	var <>tStamp;			/// time, object where last updated
	var <>tServer;
	/**
	 * rotation if Axis notation is used
	 * !!Attention: might be empty and defined in rotEuler !! 
	 * 	-> use rotMat to be sure to get valid data
	 */
	var <>rotAxis;			

	/**
	 * rotation if Euler notation is used
	 * !!Attention: might be empty and defined in rotAxis !! 
	 * 	-> use rotMat to be sure to get valid data
	 */
	var <>rotEuler;
	
	var <>velocity;		/// X,Y,Z,A,B,C
	var <>acceleration;		/// m,r
	var <>freeSpace;		/// [P]
	
	/**
	 * once objects are created, they are visible until they are destroyed 
	 * (i.e. do not appear in status anymore). 
	 */
	var <>visible;
	
	var rotMat, homogeneMat;			/// rotation matrix
	
	/**
	 * defines if tuio uses euler- or axis-notation for rotation. 
	 * if format contains at least one of "u, v, w" -> axis-notation is used.
	 */
	var <>isEuler;

//	var <>lastUpdated;
	var <>isUpdated = true;
	
	classvar <formatDict;	// the dictionary containing the predefined formats
	classvar <keyDict;		// dictionary mapping the keys to elements in the object
	
	*initClass {
		super.initClass;
		//init
		formatDict = Dictionary.newFrom(IdentityDictionary[
			'2Dobj'	->'ixyaXYAmr',
			'3Dobj'	->'ixyzabcXYZABCmr',
			'25Dobj'	->'ixyzabcXYZABCmr',
			'2Dcurs'	->'ixyXYm',
			'3Dcurs'	->'ixyzXYZm',
			'25Dcurs'	->'ixyzabcXYZABCmr',
			'tDObj' 	->'ixya',
			'tDobj' 	->'ixya', 
			'tdObj' 	->'ixya' 
		]);
		
		
		keyDict = IdentityDictionary[
			'i'	-> {|aTUIO, val| aTUIO.classID     = val; aTUIO},

			'x'	-> {|aTUIO, val| aTUIO.pos[0]      = val; aTUIO},
			'y'	-> {|aTUIO, val| aTUIO.pos[1]      = val; aTUIO},
			'z'	-> {|aTUIO, val| aTUIO.pos[2]      = val; aTUIO},
			
			'a'	-> {|aTUIO, val| aTUIO.dirty; aTUIO.rotEuler[0] = val; aTUIO},
			'b'	-> {|aTUIO, val| aTUIO.dirty; aTUIO.rotEuler[1] = val; aTUIO},
			'c'	-> {|aTUIO, val| aTUIO.dirty; aTUIO.rotEuler[2] = val; aTUIO},
			
			'u'	-> {|aTUIO, val| aTUIO.dirty; aTUIO.rotAxis[0]  = val; aTUIO},
			'v'	-> {|aTUIO, val| aTUIO.dirty; aTUIO.rotAxis[1]  = val; aTUIO},
			'w'	-> {|aTUIO, val| aTUIO.dirty; aTUIO.rotAxis[2]  = val; aTUIO},
			
			'X'	-> {|aTUIO, val| aTUIO.velocity[0] = val; aTUIO},
			'Y'	-> {|aTUIO, val| aTUIO.velocity[1] = val; aTUIO},
			'Z'	-> {|aTUIO, val| aTUIO.velocity[2] = val; aTUIO},
			'A'	-> {|aTUIO, val| aTUIO.velocity[3] = val; aTUIO},
			'B'	-> {|aTUIO, val| aTUIO.velocity[4] = val; aTUIO},
			'C'	-> {|aTUIO, val| aTUIO.velocity[5] = val; aTUIO},
			
			'm'	-> {|aTUIO, val| aTUIO.acceleration[0] = val; aTUIO},
			
			'r'	-> {|aTUIO, val| aTUIO.acceleration[1] = val; aTUIO},
			
			'P'	-> {|aTUIO, val| aTUIO.freeSpace   = val; aTUIO}
		]

	}
	*new {|format = "2Dobj", id = 0|
		^super.new.pr_initTUIObject(format, id)
	}
	*newFrom {|aTUIO|
		var out;
		
		^(aTUIO.class === this).if({
			aTUIO
		},{
			out = this.new(aTUIO.format, aTUIO.id);
			out.classID 	= aTUIO.classID;
			out.pos		= aTUIO.pos;
			out.tStamp 	= aTUIO.tStamp;
			out.rotAxis 	= aTUIO.rotAxis;
			out.rotEuler 	= aTUIO.rotEuler;
			out.velocity 	= aTUIO.velocity;
			out.acceleration = aTUIO.acceleration;
			out.freeSpace	= aTUIO.freeSpace;
			out.visible 	= aTUIO.visible;
			out.isEuler 	= aTUIO.isEuler;
			out.isUpdated = aTUIO.isUpdated;
			out.tServer = aTUIO.tServer;
			out;
		})
	}
	/**
	 * called if object is not valid anymore (as when updating rotation)
	 */
	dirty {
		rotMat = nil;
		homogeneMat = nil;
	}
	free {
		"TUIObject-free called".postln;
	}
	/**
	 * an object is identical to another one if their ids are identical.
	 */
	== { arg that; 
		^that respondsTo: #[\id, \classID] 
			and: { id == that.id
		}
	}
/*
	hash {
		^hash(id@format)
	}
*/
	pr_initTUIObject {|argFormat, argID|
		format 		= argFormat;
		id 			= argID;
		tStamp		= SystemClock.seconds;
		// init vars
		classID		= 0;
		pos			= Array.newClear(3);
		rotEuler	 	= Array.newClear(3);
		rotAxis	 	= Array.newClear(3);		
		velocity		= Array.newClear(6);
		acceleration	= Array.newClear(2);
		freeSpace		= [];
		visible 		= true;
		
		// default value for rotation
		isEuler = true;
	}
	update {
	}
	/**
		returns the rotation Matrix if 3D, (2D: rotation around x[3])
	*/
	rotMat {
		var theta;
		var v1, v2, v3, ca, sa, v1v2ca, v2v3ca, v1v3ca;

		// test if already computed (rotMat is set to nil in rotEuler_ resp. rotAxis_)
		rotMat.isNil.if({
			rotEuler.first.notNil.if ({
				"TUIObject:rotMat (rotEuler): currently not implemented".warn;
			});
			
			rotMat = [[1,0,0], [0,1,0], [0,0,1]];

			rotAxis.first.notNil.if ({

				/* my version (tboverma) with respect to 
					http://de.wikipedia.org/wiki/Rotationsmatrix#allgemeine_Definition
					[row, row, row]
				*/
				theta = rotAxis.squared.sum.sqrt;
				
				
				(theta > 1e-15).if{
					#v1, v2, v3 = rotAxis / theta;	// normed v
					
					ca = cos(theta);
					sa = sin(theta);
				
					v1v2ca = (v1 * v2 * (1-ca));
					v2v3ca = (v2 * v3 * (1-ca));
					v1v3ca = (v1 * v3 * (1-ca));
				
					rotMat[0][0] = ca + (v1.squared*(1-ca));
					rotMat[0][1] = v1v2ca - (v3 * sa);
					rotMat[0][2] = v1v3ca + (v2 * sa);
					
					rotMat[1][0] = v1v2ca + (v3 * sa);
					rotMat[1][1] = ca + (v2.squared*(1-ca));
					rotMat[1][2] = v2v3ca - (v1 * sa);
					
					rotMat[2][0] = v1v3ca - (v2 * sa);
					rotMat[2][1] = v2v3ca + (v1 * sa);
					rotMat[2][2] = ca + (v3.squared*(1-ca));
				}; // fi not infty small theta 
			});  // fi rotAxis
		});		// fi notNil
		^(rotMat ?? {[[1,0,0], [0,1,0], [0,0,1]]})
	}
	/** returns the homogene transformation martrix
		[row, row, row]
	 */
	homogeneMat {
		var rotMat, invRot, invTrans;
		homogeneMat.notNil.if({
			^homogeneMat
		}, {
			invRot = this.rotMat.flop;
			invTrans = invRot.collect{|row|
				(row * pos).sum
			};
			^homogeneMat = [
				invRot[0] ++ invTrans[0].neg,
				invRot[1] ++ invTrans[1].neg,
				invRot[2] ++ invTrans[2].neg,
				[0,0,0, 1]
			];
		});
	}
	transformPoint {|point|
		point = point ++ 1;
		// make sure homogeneMat contains a valid transformation matrix

		^this.homogeneMat.collect{|row|
			(row * point).sum;
		}[0..2]
	}
}
JITuio : TUIObject {
	classvar <>action;
	update {
		this.process;
		this.class.action.value(this);
	}
	/** reimplement this when subclassing */
	process {
		
	}
}
/**
	A TUIObject factory which builds TUIOs according to their classID.
	only works for formats including classID.
*/
TUIOmeta : JITuio {

	classvar <keyDict;
	classvar <>tuioClasses;	
	classvar <fid2oscClassIDs;
		
	*initClass {
		// when assigning a classID, keyDict[\i] is evaluated; 
		// this replaces actual object with new one and returns it.
		keyDict = super.keyDict.deepCopy;
		keyDict[\i] = {|aTUIO, val| 
			aTUIO = aTUIO.as(tuioClasses.wrapAt(val));
			aTUIO.classID     = val;
			aTUIO.tServer.replaceInteractionFor(aTUIO);
			
			aTUIO
		};
		tuioClasses = [this];
		
		fid2oscClassIDs = (
			green: 		(ids:[44], 		classId:1),
			greenFlip: 	(ids:[29, 30], 	classId:2),
			roundRed: 	(ids:[32], 		classId:3),
			powerMate: 	(ids:[4], 		classId:4),
			default:		(ids:(10..30),	classId:0)
		)
	}
}

TUIOdump : TUIObject {
	update {
		postf("TUIOdump:update: object updated"++
			"\tID :\t%\n" ++
			"\tCls:\t%\n" ++
			"\tPos:\t%\n" ++
			"\tRot:\t%\n" ++			
			"\tVel:\t%\n" ++
			"\tAcc:\t%\n", id, classID, pos, this.rotMat, velocity, acceleration);
	}	

}

TUIOdumpWin : TUIObject {
	var window;
	var posViews, rotViews;
	var counter;
	*new {|format = "2Dobj", id = 0|
		^super.new(format, id).initDumpWin;
	}
	initDumpWin {
		counter = 0;
		{
		window = SCWindow.new(format("TUIO %(%)", id, classID));

		posViews = (0..2).collect{|i| 
			SCTextView(window, Rect(10,i*30 + 10,300, 20))
				.background_(Color.white)
				.font_(Font("Helvetica", 18))
				.stringColor_(Color.blue)
		};
		rotViews = (0..2).collect{|i| 
			SCTextView(window, Rect(10,i*30 + 110,300, 20))
				.background_(Color.white)
				.font_(Font("Helvetica", 18))
				.stringColor_(Color.red)
		};
		window.front}.defer
	}
	update {
		(counter > 10).if({
			counter = 0;
			{
			rotViews.do{|view, i|
				view.string_(format("RotAxis  (%): %", i, rotAxis[i]))
			};
			posViews.do{|view, i|
				view.string_(format("Position (%): %", i, pos[i]))
			};
			}.defer
		}, {counter = counter + 1})
	}	
}