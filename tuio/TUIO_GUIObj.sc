/*
The GUI representation of TUIObjects (needed by TUIOServerView)
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

TUIO_GUIObj{
	var <obj, tuio, tServer, <>isEditable = true;
	classvar <>oExtent = 40;

	*new {|parent, tuio, tuioServer|
		^super.new.initGUIObj(parent, tuio, tuioServer)
	}
	initGUIObj { arg parent, argtuio, tuioServer;
		var bounds;

		tuio = argtuio;
		tServer = tuioServer;

		obj = GUI.userView.new(parent, bounds);
		obj.drawFunc_({|me|
				var x, y;
				
				#x, y = (tuio.pos[0..1] * parent.bounds.asArray[2..3]);
				GUI.pen.use{
					GUI.pen.translate(x+(0.5*oExtent), y+(0.5*oExtent));
					
					tuio.isEuler.if({
//						GUI.pen.color = Color.hsv(tuio.rotEuler[0]*2pi.reciprocal % 1, 0.43, 0.87);
						GUI.pen.color = Color.hsv(tuio.classID+1 * 0.2, 1, 1, alpha: 0.5);
						tuio.rotEuler[0].notNil.if({
							GUI.pen.rotate(
								tuio.rotEuler[0], 
								0,
								0
							);
						})
					},{
						GUI.pen.color = Color.hsv(tuio.classID+1 * 0.2, 1, 1, alpha: 0.5);
					});
					GUI.pen.translate(-0.5*oExtent, -0.5*oExtent);
					GUI.pen.addRect(Rect(0,0, oExtent, oExtent));
					GUI.pen.fill;
					GUI.pen.use{
						GUI.pen.color = Color.black;
						GUI.pen.string(tuio.id.asString);//(me.bounds);
					};
					this.pr_updateBoundsFromTUIO;
				};
		});
		obj
			.mouseTrackFunc_({|me, x, y|
				var bounds;
				isEditable.if{
					bounds = obj.parent.bounds.asArray;
					x = (x-(oExtent*0.5) * bounds[2].reciprocal).clip(0, 1);
					y = (y-(oExtent*0.5) * bounds[3].reciprocal).clip(0, 1);
	
					tServer.setWithFormat("xy", tuio.id, [x, y]);
					tServer.allAlive;
				}
			})
			.keyDownFunc_({|me, key, modifiers, unicode|
				(unicode == 127).if{
					tServer.deleteObjs(tuio.id);
				}
			});
	} // end initGUIObj
	canFocus_ { arg state = false;
		obj.canFocus_(state);
	}
	canFocus {
		^obj.canFocus;
	}
	visible_ { arg bool;
		obj.visible_(bool)
	}
	visible {
		^obj.visible
	}
	resize_ { arg val;
		obj.resize_(val) // TODO!!!!
	}
/////////// private
	pr_updateBoundsFromTUIO {
		var bounds,x, y;
		
		bounds = obj.parent.bounds.asArray;
		x = tuio.pos[0] * (bounds[2]);
		y = tuio.pos[1] * (bounds[3]);

		obj.bounds = Rect(x, y, oExtent, oExtent);
	}
}
