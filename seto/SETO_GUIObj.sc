/*
The GUI representation of SETObjects (needed by SETOServerView)
	http://tuio.lfsaw.de/
	http://modin.yuri.at/publications/tuio_gw2005.pdf

Author: 
	2004, 2005, 2006, 2007
	Till Bovermann 
	Neuroinformatics Group 
	Faculty of Technology 
	Bielefeld University
	Germany
*/

/*
	Changes
		2007-10-29	renamed to SETObject
*/


SETO_GUIObj{
	var <obj, setObj, tServer, <>isEditable = true;
	classvar <>oExtent = 40;

	*new {|parent, setObj, setoServer|
		^super.new.initGUIObj(parent, setObj, setoServer)
	}
	initGUIObj { arg parent, argsetObj, setoServer;
		var bounds = parent.bounds.asArray;

		setObj = argsetObj;
		tServer = setoServer;

		obj = GUI.userView.new(parent, bounds);
		obj.drawFunc_({|me|
				var x, y;
				
				#x, y = (setObj.pos[0..1] * parent.bounds.asArray[2..3]);
//				GUI.pen.use{
					//GUI.pen.translate(x+(0.5*oExtent), y+(0.5*oExtent));
					GUI.pen.translate(0,0);
					
					setObj.isEuler.if({
//						GUI.pen.color = Color.hsv(setObj.rotEuler[0]*2pi.reciprocal % 1, 0.43, 0.87);
						GUI.pen.color = Color.hsv(setObj.classID+1 * 0.2, 1, 1, alpha: 0.5);
						setObj.rotEuler[0].notNil.if({
							GUI.pen.rotate(
								setObj.rotEuler[0], 
								0,
								0
							);
						})
					},{
						GUI.pen.color = Color.hsv(setObj.classID+1 * 0.2, 1, 1, alpha: 0.5);
					});
					//GUI.pen.translate(-0.5*oExtent, -0.5*oExtent);
					//GUI.pen.addRect(Rect(0,0, oExtent, oExtent));
					//GUI.pen.fill;
					GUI.pen.fillRect(Rect(0,0, oExtent, oExtent));
					GUI.pen.use{
						GUI.pen.color = Color.black;
						GUI.pen.string(setObj.id.asString);//(me.bounds);
					};
					this.pr_updateBoundsFromSETO;
//				};
		});
		obj.mouseTrackFunc_({|me, x, y|
				var bounds;
				isEditable.if{
					bounds = obj.parent.bounds.asArray;
					x = (x-(oExtent*0.5) * bounds[2].reciprocal).clip(0, 1);
					y = (y-(oExtent*0.5) * bounds[3].reciprocal).clip(0, 1);
	
					tServer.setWithFormat("xy", setObj.id, [x, y]);
					tServer.allAlive;
				}
			})
			.keyDownFunc_({|me, key, modifiers, unicode|
				(unicode == 127).if{
					tServer.deleteObjs(setObj.id);
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
	pr_updateBoundsFromSETO {
		var bounds,x, y;
		
		bounds = obj.parent.bounds.asArray;
		x = setObj.pos[0] * (bounds[2]);
		y = setObj.pos[1] * (bounds[3]);

		obj.bounds = Rect(x, y, oExtent, oExtent);
	}
	refresh {
		obj.refresh
	}
}
