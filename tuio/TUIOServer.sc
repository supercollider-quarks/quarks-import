/*
Implementation of the server-side of TUIO 
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
	Change
		2006-09-25	moved tuio update after interaction update (now both in alive) to support
					relative coordinate computation in one timestep. 
		2006-09-15	added tStamp support; currently only set from within the language
		2006-08-25	added interactive gui support
		2006-08-21	added general methods for alive/set messages
		2006-07-10	split up into TUIOServer and TUIO_OSCServer
		2006-02-26	added gui support
					remove now makes objects invisible rather then destroying them
		2005-12-16 	removed tuio.play call in TUIO_OSCServer-setFunc_

*/

TUIOServer {
	var <knownObjs;
	var objectIDs;
//	var interface; /// The OSCReceiver listening to tuio-related messages
	var <format;
	var <realFormat;
	var tuioClass;
	var >setFunc;
	var >aliveFunc;
	var iClass;	/// interaction class
	var isEuler;	/// determines if using Euler or Axis Notation. standard is true.
	var interactions;
	
	
	// gui support
	var hasGUI, <window, <view;

	*new {|format='2Dobj', tuioClass, interactionClass|
		^super.new.pr_initTUIOServer(format, tuioClass, interactionClass);
	}
	/*
	*pr_hashValue{|a, b|
		^(a.hash@b.hash).hash;	
	}
	*/
	add {|anObject|
//		knownObjs.add(this.class.pr_hashValue(anObject.id, anObject.format) -> anObject);
		knownObjs.add(anObject.id -> anObject);
		objectIDs.add(anObject.id);
		
		/// check if we need a new interaction and create one 
		iClass !? {
			knownObjs.do {|obj|
				(obj == anObject).not.if{
					interactions = interactions.add(iClass.new(anObject, obj));
				}
			};
		}
	}
	replaceInteractionFor {|anObj|
		var id;
		var count = 0;
		iClass !? {
			count = interactions.size;
			// remove all interactions containing objs with this id
			interactions = interactions.select{|int|
				int.parts.detect{|obj|
					anObj.id == obj.id
				}.isNil
			};
			// create new interactions
			knownObjs.do {|obj|
				(obj == anObj).not.if{
					interactions = interactions.add(iClass.new(anObj, obj));
				}
			};
		}
	}
	start{
		"TUIOServer-start: abstract method - no effect".warn
	}
	stop{
		"TUIOServer-start: abstract method - no effect".warn
	}
	gui {|editable = true|
		var addButton, idBox, classIdBox, xBox, yBox, aBox;
		hasGUI.not.if({
			window = GUI.window.new("TUIOs", Rect(800, 0, 480, 400))
				.front
				.onClose_{
					hasGUI = false;
				};
			window.view.background = Color(0.918, 0.902, 0.886);
			view = TUIOServerView(window,  Rect(5,5,390,370), this);
//			view.background =  Color(0.81960784313725, 0.82352941176471, 0.87450980392157, 0.6);
			view.background = Color.fromArray([0.918, 0.902, 0.886] * 0.5 ++ [0.8]);
			view.resize_(5);
			addButton = GUI.button.new(window, Rect(400, 5, 75, 20))
				.states_(	[["add TUIO", Color.black, Color.gray(0.5)]])
				.action_{|butt|
					this.setWithFormat("ixya", idBox.value, [classIdBox.value, xBox.value, yBox.value, aBox.value]);
					this.allAlive;
					idBox.value = idBox.value+1;
 				}
 				.resize_(3);

 			GUI.staticText.new(window, Rect(400, 30, 10, 20)).string_("id").resize_(3);
 			idBox = 
 				GUI.numberBox.new(window, Rect(415, 30, 60, 20)).value_(100).resize_(3);
 			GUI.staticText.new(window, Rect(400, 50, 10, 20)).string_("cID").resize_(3);
 			classIdBox = 
 				GUI.numberBox.new(window, Rect(415, 50, 60, 20)).value_(100).resize_(3);
 			GUI.staticText.new(window, Rect(400, 70, 10, 20)).string_("x").resize_(3);
 			xBox  = 
 				GUI.numberBox.new(window, Rect(415, 70, 60, 20)).value_(0.5).step_(0.01).resize_(3);
 			GUI.staticText.new(window, Rect(400, 90, 10, 20)).string_("y").resize_(3);
 			yBox  = 
 				GUI.numberBox.new(window, Rect(415, 90, 60, 20)).value_(0.5).step_(0.01).resize_(3);
 			GUI.staticText.new(window, Rect(400, 110, 10, 20)).string_("a").resize_(3);
 			aBox  = 
 				GUI.numberBox.new(window, Rect(415, 110, 60, 20)).value_(0).step_(0.01).resize_(3);
 			
 			GUI.staticText.new(window, Rect(400, 180, 10, 20)).string_("ext").resize_(3);
 			aBox  = 
 				GUI.numberBox.new(window, Rect(415, 180, 60, 20)).value_(TUIO_GUIObj.oExtent).step_(1).resize_(3).action_{|me| TUIO_GUIObj.oExtent = me.value};
			hasGUI = true;
			^window.front;
		}, {
			^window
		});
	}

	// private
	pr_initTUIOServer {|aFormat, argTUIOClass, argInteractionClass|
		
		tuioClass 	= argTUIOClass ? TUIObject;
		if (tuioClass.isKindOf(Meta_TUIObject).not, {
			"Meta_TUIO_OSCServer-new: argument tuioClass is not subclass of TUIObject.".error;
		});
		knownObjs = IdentityDictionary.new;
		objectIDs = Set[];


		iClass 	= argInteractionClass; // ? TUIOInteraction;
		(iClass.notNil && {iClass.isKindOf(Meta_TUIOInteraction).not}).if{
			"Meta_TUIO_OSCServer-new: argument interactionClass is not subclass of TUIOInteraction.".error;
		};
		interactions = [];

		format = aFormat;		
		if (format.asString.beginsWith("_").not , {
			realFormat = tuioClass.formatDict.at(format);
			// if Nil -> not in Dict -> warning;
			if (realFormat.isNil, {
				("TUIO_OSCServer:pr_initTUIO_OSCServer: Format not recognized -" + format).warn;
				^nil
			})
		}, {
			realFormat = format.copyRange(1, format.size-1);
		});
		// determine if using Euler or Axis rotation notation
		isEuler = Set.newFrom(realFormat.asString).sect(Set[$u, $v, $w]).isEmpty;
		 	
		// initialize set and alive functions
		this.setFunc_{|id ... args|
			this.setWithFormat(realFormat, id, args);
		}; // end this.setFunc_
		
		this.aliveFunc_{|... argObjectIDs|
			this.alive(argObjectIDs);
		};
		// end initialize set and alive functions

		///// init GUI support ////////////////
		hasGUI = false;
	} // end pr_initTUIO_OSCServer
	pr_removeAt {|id|
		var tuio;
		
		objectIDs.remove(id);
		
		//knownObjs.removeAt(this.class.pr_hashValue(id, format)).clear;
//		tuio = knownObjs[this.class.pr_hashValue(id, format)];
		tuio = knownObjs[id];
		tuio !? {tuio.visible = false;}
	}
	allAlive {
		this.alive(objectIDs);
	}
	visibleObjs {
		^knownObjs.selectAs(_.visible, Array)
	}
	deleteObjs {|... ids|
		this.alive(objectIDs -- ids.asSet);		
	}
	alive {|argObjectIDs|
		var deadTuioIDs, tuio;
		
		hasGUI.if{{
			view.alive(argObjectIDs);
			window.refresh;
		}.defer};
		deadTuioIDs = argObjectIDs.asSet -- objectIDs;
		deadTuioIDs.do{|id|
			this.pr_removeAt(id);
		};
		//knownObjs.do{|item| item.id.postln};
		
		// interaction support
		interactions.do{|int| int.update};

		// update tuio representations.
		objectIDs.do{|id|
			tuio = knownObjs[id];
			tuio.isUpdated.if({
				tuio.update; 
				tuio.isUpdated = false
			});
		};
	}
	set {|id, args|
		this.setWithFormat(realFormat, id, args)
	}
	setWithFormat {|actFormat, id, args|
		var tuio, now;	
		
		now = SystemClock.seconds;
		
		// get related object; add it, if it is not in the list
		//tuio = knownObjs.at(this.class.pr_hashValue(id, format));
		tuio = knownObjs.at(id);
		tuio ?? {
			tuio = tuioClass.new(format, id);
			tuio.tServer = this;
			this.add(tuio);
//				"func in TUIO_OSCServer-pr_initTUIO_OSCServer: added an object". ;
			tuio.isEuler = isEuler;				// set euler flag
		};
		
		//set the params of tuio related to its format string
		actFormat.asString.do{|item, i|
			tuio = tuioClass.keyDict[item.asSymbol].value(tuio, args[i]);
		};
		objectIDs.add(tuio.id);

		tuio.visible = true;
		tuio.tStamp = now;
		tuio.isUpdated = true;
		
		knownObjs[id] = tuio;
		
		// GUI Support
		hasGUI.if{
			{view.setObj(tuio)}.defer;
		};
	}
	
}

/**
	@todo open gui -> set, alive -> close gui -> set alive -> open gui -> allAlive => no objects...
*/
TUIOServerView {
	var view, objects, tuioServer;
	
	*new{|parent, bounds, tuioServer|
		^super.new.initView(parent,bounds, tuioServer);
	}
	initView{|parent, bounds, tServer|
		tuioServer = tServer;
		view = GUI.compositeView.new(parent,bounds);
		view.background = Color.white;
		objects = ();
	}
	setObj {|tuio|
		var obj, newObj;
		
		// if object doesn't exists, create it, write it into dict and to obj
		obj = objects[tuio.id] ?? {
			newObj = TUIO_GUIObj.new(view, tuio, tuioServer);
			objects.put(tuio.id, newObj);
			newObj;
		};
		obj.visible_(true);
	}
	alive {|ids|
		objects.keysValuesDo{|key, obj, i|
			// make non-alive objects invisible
			(ids.includes(key).not).if({obj.visible=false})
		}
	}
	background {
		^view.background;
	}
	background_{|color|
		view.background_(color);
	}
	resize {
	 	^view.resize;
	}
	resize_{|val|
		view.resize_(val)
	}
}