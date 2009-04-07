{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 324.0, 88.0, 872.0, 687.0 ],
		"bglocked" : 0,
		"defrect" : [ 324.0, 88.0, 872.0, 687.0 ],
		"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
		"openinpresentation" : 1,
		"default_fontsize" : 12.0,
		"default_fontface" : 0,
		"default_fontname" : "Arial",
		"gridonopen" : 0,
		"gridsize" : [ 15.0, 15.0 ],
		"gridsnaponopen" : 0,
		"toolbarvisible" : 1,
		"boxanimatetime" : 200,
		"imprint" : 0,
		"globalpatchername" : "network[1][1]",
		"metadata" : [  ],
		"boxes" : [ 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "loadmess 0",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 7.0, 29.0, 72.0, 20.0 ],
					"outlettype" : [ "" ],
					"id" : "obj-11",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Refer to Data Network documentation for namespace and message explanations.",
					"linecount" : 4,
					"presentation_linecount" : 2,
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 430.0, 173.0, 150.0, 62.0 ],
					"presentation" : 1,
					"id" : "obj-8",
					"fontname" : "Arial",
					"presentation_rect" : [ 184.0, 138.0, 327.0, 34.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "autohelp_top_title",
					"text" : "dn.sink (Data Network)",
					"numinlets" : 1,
					"frgb" : [ 0.93, 0.93, 0.97, 1.0 ],
					"fontface" : 3,
					"fontsize" : 20.871338,
					"numoutlets" : 0,
					"patching_rect" : [ 74.0, 47.0, 485.0, 30.0 ],
					"presentation" : 1,
					"textcolor" : [ 0.93, 0.93, 0.97, 1.0 ],
					"id" : "obj-40",
					"fontname" : "Arial",
					"presentation_rect" : [ 23.0, 23.0, 485.0, 30.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "autohelp_top_digest",
					"text" : "Manage subscriptions and datastreams from the Data Network",
					"numinlets" : 1,
					"frgb" : [ 0.93, 0.93, 0.97, 1.0 ],
					"fontsize" : 12.754705,
					"numoutlets" : 0,
					"patching_rect" : [ 74.0, 75.0, 485.0, 21.0 ],
					"presentation" : 1,
					"textcolor" : [ 0.93, 0.93, 0.97, 1.0 ],
					"id" : "obj-41",
					"fontname" : "Arial",
					"presentation_rect" : [ 23.0, 51.0, 490.0, 21.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"varname" : "autohelp_top_description",
					"text" : "Attributes: host and optional port number (default local listening port is  6009). \nSet the host ( Data Network server)  by ip address or hostname. \nOptional file attribute specifies file containing data network subscriptions (node and slot pairs, with * wild card slot option).",
					"linecount" : 4,
					"presentation_linecount" : 4,
					"numinlets" : 1,
					"fontsize" : 11.595187,
					"numoutlets" : 0,
					"patching_rect" : [ 74.0, 96.0, 490.0, 60.0 ],
					"presentation" : 1,
					"id" : "obj-42",
					"fontname" : "Arial",
					"presentation_rect" : [ 25.0, 69.0, 488.0, 60.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "ping RX",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 228.0, 312.0, 150.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-38",
					"fontname" : "Arial",
					"presentation_rect" : [ 203.0, 589.0, 69.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"numinlets" : 1,
					"numoutlets" : 1,
					"patching_rect" : [ 269.0, 311.0, 20.0, 20.0 ],
					"presentation" : 1,
					"outlettype" : [ "bang" ],
					"id" : "obj-35",
					"ignoreclick" : 1,
					"presentation_rect" : [ 255.0, 589.0, 20.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "other",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 59.0, 597.0, 65.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-26",
					"fontname" : "Arial",
					"presentation_rect" : [ 42.0, 548.0, 52.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "error ",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 66.0, 475.0, 39.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-3",
					"fontname" : "Arial",
					"presentation_rect" : [ 42.0, 484.0, 55.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "data",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 68.0, 410.0, 150.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-27",
					"fontname" : "Arial",
					"presentation_rect" : [ 42.0, 438.0, 51.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "/get/slot 6009 4 5",
					"numinlets" : 2,
					"fontsize" : 11.595187,
					"numoutlets" : 1,
					"patching_rect" : [ 132.0, 600.0, 159.0, 18.0 ],
					"presentation" : 1,
					"outlettype" : [ "" ],
					"id" : "obj-36",
					"fontname" : "Arial",
					"presentation_rect" : [ 121.0, 552.0, 352.0, 18.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "prepend set",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 132.0, 572.0, 74.0, 20.0 ],
					"outlettype" : [ "" ],
					"id" : "obj-37",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "\"Client with IP 192.168.1.9 and port 6001 is already registered. Please unregister first\"",
					"linecount" : 4,
					"presentation_linecount" : 2,
					"numinlets" : 2,
					"fontsize" : 11.595187,
					"numoutlets" : 1,
					"patching_rect" : [ 136.0, 482.0, 160.0, 58.0 ],
					"presentation" : 1,
					"outlettype" : [ "" ],
					"id" : "obj-29",
					"fontname" : "Arial",
					"presentation_rect" : [ 121.0, 484.0, 353.0, 31.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "prepend set",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 136.0, 453.0, 74.0, 20.0 ],
					"outlettype" : [ "" ],
					"id" : "obj-33",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"numinlets" : 2,
					"fontsize" : 11.595187,
					"numoutlets" : 1,
					"patching_rect" : [ 136.0, 413.0, 159.0, 18.0 ],
					"presentation" : 1,
					"outlettype" : [ "" ],
					"id" : "obj-22",
					"fontname" : "Arial",
					"presentation_rect" : [ 121.0, 438.0, 355.0, 18.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "prepend set",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 136.0, 385.0, 74.0, 20.0 ],
					"outlettype" : [ "" ],
					"id" : "obj-20",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "print sink",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 123.0, 308.0, 59.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-4",
					"fontname" : "Arial",
					"presentation_rect" : [ 124.0, 588.0, 59.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "bpatcher",
					"args" : [  ],
					"numinlets" : 2,
					"numoutlets" : 0,
					"patching_rect" : [ 330.0, 301.0, 470.0, 198.0 ],
					"presentation" : 1,
					"id" : "obj-1",
					"name" : "dn.infomenu.maxpat",
					"presentation_rect" : [ 15.0, 215.0, 525.0, 198.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "2008 Harry Smoak <harrycs@harrysmoak.com>",
					"presentation_linecount" : 2,
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 583.0, 21.0, 268.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-32",
					"fontname" : "Arial",
					"presentation_rect" : [ 327.0, 19.0, 203.0, 34.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "edit subscription file",
					"numinlets" : 2,
					"fontsize" : 12.0,
					"numoutlets" : 1,
					"patching_rect" : [ 81.0, 199.0, 142.5, 18.0 ],
					"presentation" : 1,
					"outlettype" : [ "" ],
					"id" : "obj-6",
					"fontname" : "Arial",
					"presentation_rect" : [ 393.0, 178.0, 116.0, 18.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.sink @host 192.168.1.7 @port 6009 @file subscription.mtxt",
					"numinlets" : 3,
					"fontsize" : 12.0,
					"numoutlets" : 5,
					"patching_rect" : [ 122.0, 252.0, 344.0, 20.0 ],
					"presentation" : 1,
					"outlettype" : [ "", "", "", "", "" ],
					"color" : [ 0.0, 0.25098, 1.0, 1.0 ],
					"id" : "obj-5",
					"fontname" : "Arial",
					"presentation_rect" : [ 28.0, 178.0, 344.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "edit subscription list ",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 0,
					"patching_rect" : [ 80.0, 173.0, 150.0, 20.0 ],
					"id" : "obj-2",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"numinlets" : 1,
					"numoutlets" : 1,
					"patching_rect" : [ 228.0, 196.0, 20.0, 20.0 ],
					"outlettype" : [ "bang" ],
					"id" : "obj-30"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "t s b",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 2,
					"patching_rect" : [ 372.0, 202.0, 33.0, 20.0 ],
					"outlettype" : [ "", "bang" ],
					"id" : "obj-24",
					"fontname" : "Arial"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"numinlets" : 1,
					"fontsize" : 12.0,
					"numoutlets" : 3,
					"types" : [  ],
					"patching_rect" : [ 301.0, 165.0, 100.0, 20.0 ],
					"presentation" : 1,
					"items" : [ "/register", ",", "/unregister", ",", "/subscribe", ",", "/unsubscribe", ",", "/query/expected", ",", "/query/nodes", ",", "/query/slots", ",", "/get" ],
					"outlettype" : [ "int", "", "" ],
					"id" : "obj-25",
					"fontname" : "Arial",
					"presentation_rect" : [ 28.0, 146.0, 143.0, 20.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"grad1" : [ 0.415686, 0.239216, 0.109804, 1.0 ],
					"numinlets" : 1,
					"numoutlets" : 0,
					"patching_rect" : [ 904.0, 512.0, 128.0, 128.0 ],
					"bgcolor" : [ 0.407843, 0.360784, 0.043137, 0.447059 ],
					"presentation" : 1,
					"id" : "obj-45",
					"presentation_rect" : [ 29.0, 424.0, 490.0, 217.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"varname" : "autohelp_top_panel",
					"grad1" : [ 0.407843, 0.360784, 0.043137, 1.0 ],
					"numinlets" : 1,
					"mode" : 1,
					"grad2" : [ 0.85, 0.85, 0.85, 1.0 ],
					"numoutlets" : 0,
					"patching_rect" : [ 69.0, 44.0, 495.0, 52.0 ],
					"presentation" : 1,
					"background" : 1,
					"id" : "obj-44",
					"presentation_rect" : [ 21.0, 19.0, 495.0, 52.0 ]
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-4", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-20", 0 ],
					"hidden" : 0,
					"midpoints" : [ 131.5, 285.0, 55.0, 285.0, 55.0, 372.0, 145.5, 372.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-30", 0 ],
					"destination" : [ "obj-5", 1 ],
					"hidden" : 0,
					"midpoints" : [ 237.5, 240.0, 294.0, 240.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-24", 0 ],
					"destination" : [ "obj-5", 2 ],
					"hidden" : 0,
					"midpoints" : [ 381.5, 240.0, 456.5, 240.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-6", 0 ],
					"destination" : [ "obj-5", 1 ],
					"hidden" : 0,
					"midpoints" : [ 90.5, 240.0, 294.0, 240.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 1 ],
					"destination" : [ "obj-33", 0 ],
					"hidden" : 0,
					"midpoints" : [ 212.75, 285.0, 55.0, 285.0, 55.0, 372.0, 54.0, 372.0, 54.0, 449.0, 145.5, 449.0 ]
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
					"source" : [ "obj-5", 4 ],
					"destination" : [ "obj-37", 0 ],
					"hidden" : 0,
					"midpoints" : [ 456.5, 286.0, 192.0, 286.0, 192.0, 285.0, 54.0, 285.0, 54.0, 462.0, 123.0, 462.0, 123.0, 558.0, 141.5, 558.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 2 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-24", 1 ],
					"destination" : [ "obj-1", 1 ],
					"hidden" : 0,
					"midpoints" : [ 395.5, 274.0, 790.5, 274.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-20", 0 ],
					"destination" : [ "obj-22", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-33", 0 ],
					"destination" : [ "obj-29", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-37", 0 ],
					"destination" : [ "obj-36", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 1 ],
					"destination" : [ "obj-24", 0 ],
					"hidden" : 0,
					"midpoints" : [ 351.0, 181.0, 381.5, 181.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-11", 0 ],
					"destination" : [ "obj-25", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
 ]
	}

}
