{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 478.0, 76.0, 533.0, 449.0 ],
		"bglocked" : 0,
		"defrect" : [ 478.0, 76.0, 533.0, 449.0 ],
		"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
		"openinpresentation" : 0,
		"default_fontsize" : 12.0,
		"default_fontface" : 0,
		"default_fontname" : "Arial",
		"gridonopen" : 0,
		"gridsize" : [ 15.0, 15.0 ],
		"gridsnaponopen" : 0,
		"toolbarvisible" : 1,
		"boxanimatetime" : 200,
		"imprint" : 0,
		"boxes" : [ 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Bang in right inlet re-initializes, as does \"init\" message sent to \"dn.node\".",
					"linecount" : 4,
					"fontname" : "Arial",
					"patching_rect" : [ 285.0, 195.0, 127.0, 62.0 ],
					"id" : "obj-5",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "s dn.node",
					"fontname" : "Arial",
					"patching_rect" : [ 420.0, 240.0, 63.0, 20.0 ],
					"id" : "obj-4",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "init",
					"fontname" : "Arial",
					"outlettype" : [ "" ],
					"patching_rect" : [ 420.0, 210.0, 32.5, 18.0 ],
					"id" : "obj-12",
					"fontsize" : 12.0,
					"numinlets" : 2,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"outlettype" : [ "bang" ],
					"patching_rect" : [ 45.0, 120.0, 20.0, 20.0 ],
					"id" : "obj-21",
					"numinlets" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "9",
					"fontname" : "Arial",
					"outlettype" : [ "" ],
					"patching_rect" : [ 135.0, 150.0, 32.5, 18.0 ],
					"id" : "obj-19",
					"fontsize" : 12.0,
					"numinlets" : 2,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "3.4",
					"fontname" : "Arial",
					"outlettype" : [ "" ],
					"patching_rect" : [ 90.0, 150.0, 32.5, 18.0 ],
					"id" : "obj-18",
					"fontsize" : 12.0,
					"numinlets" : 2,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "0.1",
					"fontname" : "Arial",
					"outlettype" : [ "" ],
					"patching_rect" : [ 45.0, 150.0, 32.5, 18.0 ],
					"id" : "obj-13",
					"fontsize" : 12.0,
					"numinlets" : 2,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "pack 0. 0. 0.",
					"fontname" : "Arial",
					"outlettype" : [ "" ],
					"patching_rect" : [ 45.0, 180.0, 109.0, 20.0 ],
					"id" : "obj-9",
					"fontsize" : 12.0,
					"numinlets" : 3,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "999 0.1 3.4 9.",
					"fontname" : "Arial",
					"outlettype" : [ "" ],
					"patching_rect" : [ 60.0, 300.0, 94.0, 18.0 ],
					"id" : "obj-8",
					"fontsize" : 12.0,
					"numinlets" : 2,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.node 999",
					"fontname" : "Arial",
					"outlettype" : [ "" ],
					"patching_rect" : [ 135.0, 270.0, 77.0, 20.0 ],
					"id" : "obj-7",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Send data as a list of floats. Each element corresponds to a slot.",
					"linecount" : 2,
					"fontname" : "Arial",
					"patching_rect" : [ 285.0, 135.0, 207.0, 34.0 ],
					"id" : "obj-3",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.makenode 999 3 TestData",
					"fontname" : "Arial",
					"patching_rect" : [ 45.0, 210.0, 169.0, 20.0 ],
					"id" : "obj-1",
					"fontsize" : 12.0,
					"numinlets" : 2,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "This abstraction learns the host IP address using the \"pv dn-host\" object - this MUST be included in the parent patcher.",
					"linecount" : 3,
					"fontname" : "Arial",
					"patching_rect" : [ 15.0, 375.0, 246.0, 48.0 ],
					"id" : "obj-11",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Arguments: Node, Number of Slots, and Name of the Node",
					"linecount" : 2,
					"fontname" : "Arial",
					"patching_rect" : [ 285.0, 90.0, 207.0, 34.0 ],
					"id" : "obj-10",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "pv dn-host 192.168.0.104",
					"fontname" : "Arial",
					"outlettype" : [ "" ],
					"patching_rect" : [ 30.0, 90.0, 147.0, 20.0 ],
					"id" : "obj-6",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "bpatcher",
					"patching_rect" : [ 315.0, 285.0, 211.0, 145.0 ],
					"args" : [  ],
					"id" : "obj-64",
					"name" : "dot.menu.maxpat",
					"numinlets" : 0,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Easy interaction with the SenseStage Data Network!",
					"fontname" : "Arial",
					"patching_rect" : [ 15.0, 45.0, 484.0, 23.0 ],
					"id" : "obj-16",
					"fontsize" : 14.0,
					"numinlets" : 1,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "autohelp_top_title",
					"text" : "dn.makenode",
					"fontname" : "Arial",
					"patching_rect" : [ 15.0, 15.0, 485.0, 30.0 ],
					"frgb" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-22",
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"fontface" : 3,
					"fontsize" : 20.871338,
					"numinlets" : 1,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"patching_rect" : [ 501.0, 15.0, 4.0, 304.0 ],
					"id" : "obj-60",
					"bgcolor" : [ 0.0, 0.0, 0.0, 1.0 ],
					"numinlets" : 1,
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"varname" : "autohelp_top_panel[1]",
					"patching_rect" : [ 15.0, 15.0, 490.0, 31.0 ],
					"id" : "obj-2",
					"mode" : 1,
					"grad1" : [ 0.0, 0.0, 0.0, 1.0 ],
					"angle" : 180.0,
					"background" : 1,
					"numinlets" : 1,
					"grad2" : [ 1.0, 0.0, 0.0, 1.0 ],
					"numoutlets" : 0
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-12", 0 ],
					"destination" : [ "obj-4", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-7", 0 ],
					"destination" : [ "obj-8", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-9", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-13", 0 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-18", 0 ],
					"destination" : [ "obj-9", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-19", 0 ],
					"destination" : [ "obj-9", 2 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 0 ],
					"destination" : [ "obj-19", 0 ],
					"hidden" : 0,
					"midpoints" : [ 54.5, 147.0, 144.5, 147.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 0 ],
					"destination" : [ "obj-18", 0 ],
					"hidden" : 0,
					"midpoints" : [ 54.5, 147.0, 99.5, 147.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 0 ],
					"destination" : [ "obj-13", 0 ],
					"hidden" : 0,
					"midpoints" : [ 54.5, 141.0, 54.5, 141.0 ]
				}

			}
 ]
	}

}
