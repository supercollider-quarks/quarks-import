{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 30.0, 44.0, 771.0, 500.0 ],
		"bglocked" : 0,
		"defrect" : [ 30.0, 44.0, 771.0, 500.0 ],
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
		"metadata" : [  ],
		"boxes" : [ 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "301 0. 0.12 100.",
					"fontsize" : 12.0,
					"numinlets" : 2,
					"numoutlets" : 1,
					"patching_rect" : [ 429.0, 222.0, 97.0, 18.0 ],
					"outlettype" : [ "" ],
					"id" : "obj-6",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "initialize ",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 117.0, 240.0, 74.0, 20.0 ],
					"id" : "obj-21",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Bang to \"push\" patcher arguments. This resets a lot of patcher internals.",
					"linecount" : 2,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 11.0, 198.0, 202.0, 34.0 ],
					"id" : "obj-22",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"numinlets" : 1,
					"numoutlets" : 1,
					"patching_rect" : [ 86.0, 242.0, 20.0, 20.0 ],
					"outlettype" : [ "bang" ],
					"id" : "obj-23"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "edit ",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 285.0, 237.0, 35.0, 20.0 ],
					"id" : "obj-9",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "edit",
					"fontsize" : 12.0,
					"bgcolor" : [ 0.945098, 0.913725, 0.407843, 1.0 ],
					"numinlets" : 2,
					"numoutlets" : 1,
					"patching_rect" : [ 234.0, 179.0, 32.5, 18.0 ],
					"outlettype" : [ "" ],
					"id" : "obj-19",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"numinlets" : 1,
					"numoutlets" : 1,
					"patching_rect" : [ 268.0, 179.0, 20.0, 20.0 ],
					"outlettype" : [ "bang" ],
					"id" : "obj-20"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "set node::slot data",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 447.0, 243.0, 120.0, 20.0 ],
					"id" : "obj-3",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.source @host 192.168.0.104 @port 6009 @file publish.mtxt",
					"fontsize" : 12.0,
					"numinlets" : 3,
					"numoutlets" : 0,
					"patching_rect" : [ 103.0, 268.0, 346.0, 20.0 ],
					"color" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"id" : "obj-1",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "To set slot values (data) on the server, the patcher expects one or more list messages.  Each message should begin with the node ID followed by the appropriate number of slot values (float)  For example, node 301 has 3 slots.  The message might look like \"301 0.0 0.12 99.999\" ",
					"linecount" : 5,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 433.0, 126.0, 367.0, 75.0 ],
					"id" : "obj-16",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "autohelp_top_title",
					"text" : "dn.source (Data Network)",
					"frgb" : [ 0.93, 0.93, 0.97, 1.0 ],
					"fontface" : 3,
					"fontsize" : 20.871338,
					"presentation_rect" : [ 37.0, 38.0, 485.0, 30.0 ],
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 40.0, 30.0, 485.0, 30.0 ],
					"presentation" : 1,
					"id" : "obj-40",
					"fontname" : "Arial",
					"textcolor" : [ 0.93, 0.93, 0.97, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "autohelp_top_digest",
					"text" : "Publish datastreams to the Data Network",
					"frgb" : [ 0.93, 0.93, 0.97, 1.0 ],
					"fontsize" : 12.754705,
					"presentation_rect" : [ 38.0, 66.0, 490.0, 21.0 ],
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 37.0, 61.0, 485.0, 21.0 ],
					"presentation" : 1,
					"id" : "obj-41",
					"fontname" : "Arial",
					"textcolor" : [ 0.93, 0.93, 0.97, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "autohelp_top_description",
					"text" : "Attributes: host and optional port number (default local listening port is  6009). \n\nSet the host ( Data Network server)  by ip address or hostname. \n\nRefer to Data Network documentation for namespace and message explanations.",
					"linecount" : 8,
					"fontsize" : 11.595187,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 97.0, 344.0, 250.0, 113.0 ],
					"id" : "obj-42",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Bang to edit list of published datastreams.",
					"linecount" : 2,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 232.0, 106.0, 163.0, 34.0 ],
					"id" : "obj-2",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"varname" : "autohelp_top_panel",
					"mode" : 1,
					"presentation_rect" : [ 36.0, 34.0, 495.0, 52.0 ],
					"numinlets" : 1,
					"numoutlets" : 0,
					"background" : 1,
					"patching_rect" : [ 35.0, 30.0, 495.0, 52.0 ],
					"presentation" : 1,
					"grad1" : [ 0.317647, 0.709804, 0.321569, 1.0 ],
					"id" : "obj-44",
					"grad2" : [ 0.85, 0.85, 0.85, 1.0 ]
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-6", 0 ],
					"destination" : [ "obj-1", 2 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-23", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-19", 0 ],
					"destination" : [ "obj-1", 1 ],
					"hidden" : 0,
					"midpoints" : [ 243.5, 253.0, 274.0, 253.0, 274.0, 265.0, 276.0, 265.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-20", 0 ],
					"destination" : [ "obj-1", 1 ],
					"hidden" : 0,
					"midpoints" : [ 277.5, 265.0, 276.0, 265.0 ]
				}

			}
 ]
	}

}
