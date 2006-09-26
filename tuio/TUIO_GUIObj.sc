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

		obj = SCUserView.new(parent, bounds)
			.drawFunc_({|me|
				var x, y;
				
				#x, y = (tuio.pos[0..1]*parent.bounds.asArray[2..3]);
				Pen.use{
					Pen.translate(x+(0.5*oExtent), y+(0.5*oExtent));
					
					tuio.isEuler.if({
						Color.hsv(tuio.rotEuler[0]*2pi.reciprocal % 1, 0.43, 0.87).set;
						Pen.rotate(
							tuio.rotEuler[0], 
							0,
							0
						);
					},{
						Color.hsv(tuio.classID+1 * 0.2, 1, 1, alpha: 0.5).set;
					});
					Pen.translate(-0.5*oExtent, -0.5*oExtent);
					Pen.addRect(Rect(0,0, oExtent, oExtent));
					Pen.fill;
					tuio.id.asString.draw(me.bounds);
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
		obj.resize_(val)
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
