{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 91.0, 77.0, 673.0, 586.0 ],
		"bglocked" : 0,
		"defrect" : [ 91.0, 77.0, 673.0, 586.0 ],
		"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
		"openinpresentation" : 0,
		"default_fontsize" : 12.0,
		"default_fontface" : 0,
		"default_fontname" : "Arial",
		"gridonopen" : 0,
		"gridsize" : [ 15.0, 15.0 ],
		"gridsnaponopen" : 0,
		"toolbarvisible" : 0,
		"boxanimatetime" : 200,
		"imprint" : 0,
		"globalpatchername" : "network[1][1][2][1]",
		"metadata" : [  ],
		"boxes" : [ 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "initialize ",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 113.0, 227.0, 74.0, 20.0 ],
					"id" : "obj-8",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "server messages ",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 483.0, 233.0, 123.0, 20.0 ],
					"id" : "obj-7",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
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
					"patching_rect" : [ 321.0, 229.0, 35.0, 20.0 ],
					"id" : "obj-4",
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
					"patching_rect" : [ 35.0, 171.0, 202.0, 34.0 ],
					"id" : "obj-18",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"numinlets" : 1,
					"numoutlets" : 1,
					"patching_rect" : [ 89.0, 217.0, 20.0, 20.0 ],
					"outlettype" : [ "bang" ],
					"id" : "obj-17"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Messages to server go in rightmost inlet.",
					"linecount" : 2,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 470.0, 146.0, 150.0, 34.0 ],
					"id" : "obj-16",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "info ",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 279.0, 280.0, 43.0, 20.0 ],
					"id" : "obj-15",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "other",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 449.0, 280.0, 42.0, 20.0 ],
					"id" : "obj-14",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "error ",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 196.0, 280.0, 43.0, 20.0 ],
					"id" : "obj-13",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "data",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 109.0, 280.0, 50.0, 20.0 ],
					"id" : "obj-12",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Double-click to access more options",
					"frgb" : [ 0.909804, 0.019608, 0.019608, 1.0 ],
					"fontface" : 1,
					"fontsize" : 14.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 28.0, 123.0, 260.0, 23.0 ],
					"id" : "obj-10",
					"fontname" : "Arial",
					"textcolor" : [ 0.909804, 0.019608, 0.019608, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "loadmess 0",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 1,
					"patching_rect" : [ 479.0, 102.0, 72.0, 20.0 ],
					"outlettype" : [ "" ],
					"id" : "obj-11",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "autohelp_top_title",
					"text" : "dn.sink (Data Network)",
					"frgb" : [ 0.93, 0.93, 0.97, 1.0 ],
					"fontface" : 3,
					"fontsize" : 20.871338,
					"presentation_rect" : [ 22.0, 23.0, 485.0, 30.0 ],
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 25.0, 19.0, 485.0, 30.0 ],
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
					"text" : "Manage subscriptions and datastreams from the Data Network",
					"frgb" : [ 0.93, 0.93, 0.97, 1.0 ],
					"fontsize" : 12.754705,
					"presentation_rect" : [ 23.0, 51.0, 490.0, 21.0 ],
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 24.0, 47.0, 485.0, 21.0 ],
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
					"text" : "Attributes: host and optional port number (default local listening port is  6009). \n\nSet the host ( Data Network server)  by ip address or hostname. \n\nOptional file attribute specifies file containing data network subscriptions (node and slot pairs, with * wild card slot option).\n\nRefer to Data Network documentation for namespace and message explanations.",
					"linecount" : 12,
					"fontsize" : 11.595187,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 91.0, 351.0, 250.0, 166.0 ],
					"id" : "obj-42",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "ping",
					"frgb" : [ 0.403922, 0.109804, 0.701961, 1.0 ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 368.0, 280.0, 43.0, 20.0 ],
					"id" : "obj-38",
					"fontname" : "Arial",
					"textcolor" : [ 0.403922, 0.109804, 0.701961, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"numinlets" : 1,
					"numoutlets" : 1,
					"patching_rect" : [ 348.0, 324.0, 20.0, 20.0 ],
					"outlettype" : [ "bang" ],
					"id" : "obj-35",
					"ignoreclick" : 1
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
					"patching_rect" : [ 273.0, 197.0, 32.5, 18.0 ],
					"outlettype" : [ "" ],
					"id" : "obj-6",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.sink @host 192.168.0.104 @port 6009 @file subscription.mtxt",
					"fontsize" : 12.0,
					"numinlets" : 3,
					"numoutlets" : 5,
					"patching_rect" : [ 110.0, 254.0, 357.0, 20.0 ],
					"color" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"outlettype" : [ "", "", "", "", "" ],
					"id" : "obj-5",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Bang to edit node subsciption information.  Send resubscribe message after editing this file.",
					"linecount" : 4,
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 298.0, 128.0, 163.0, 62.0 ],
					"id" : "obj-2",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"numinlets" : 1,
					"numoutlets" : 1,
					"patching_rect" : [ 307.0, 197.0, 20.0, 20.0 ],
					"outlettype" : [ "bang" ],
					"id" : "obj-30"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "t s b",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"numoutlets" : 2,
					"patching_rect" : [ 451.0, 219.0, 33.0, 20.0 ],
					"outlettype" : [ "", "bang" ],
					"id" : "obj-24",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"items" : [ "/register", ",", "/unregister", ",", "/subscribe", ",", "/unsubscribe", ",", "/query/expected", ",", "/query/nodes", ",", "/query/slots", ",", "/get" ],
					"numoutlets" : 3,
					"types" : [  ],
					"patching_rect" : [ 452.0, 189.0, 100.0, 20.0 ],
					"outlettype" : [ "int", "", "" ],
					"id" : "obj-25",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"varname" : "autohelp_top_panel",
					"mode" : 1,
					"presentation_rect" : [ 21.0, 19.0, 495.0, 52.0 ],
					"numinlets" : 1,
					"numoutlets" : 0,
					"background" : 1,
					"patching_rect" : [ 20.0, 16.0, 495.0, 52.0 ],
					"presentation" : 1,
					"grad1" : [ 0.407843, 0.360784, 0.043137, 1.0 ],
					"id" : "obj-44",
					"grad2" : [ 0.85, 0.85, 0.85, 1.0 ]
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-11", 0 ],
					"destination" : [ "obj-25", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 1 ],
					"destination" : [ "obj-24", 0 ],
					"hidden" : 0,
					"midpoints" : [ 502.0, 211.0, 460.5, 211.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-17", 0 ],
					"destination" : [ "obj-5", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 3 ],
					"destination" : [ "obj-35", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-6", 0 ],
					"destination" : [ "obj-5", 1 ],
					"hidden" : 0,
					"midpoints" : [ 282.5, 240.0, 288.5, 240.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-24", 0 ],
					"destination" : [ "obj-5", 2 ],
					"hidden" : 0,
					"midpoints" : [ 460.5, 240.0, 457.5, 240.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-30", 0 ],
					"destination" : [ "obj-5", 1 ],
					"hidden" : 0,
					"midpoints" : [ 316.5, 240.0, 288.5, 240.0 ]
				}

			}
 ]
	}

}
