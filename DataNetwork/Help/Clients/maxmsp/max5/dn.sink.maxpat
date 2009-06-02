{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 35.0, 44.0, 655.0, 585.0 ],
		"bglocked" : 0,
		"defrect" : [ 35.0, 44.0, 655.0, 585.0 ],
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
		"metadata" : [  ],
		"boxes" : [ 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "loadbang",
					"patching_rect" : [ 827.0, 295.0, 60.0, 20.0 ],
					"id" : "obj-75",
					"outlettype" : [ "bang" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "prepend port",
					"patching_rect" : [ 829.0, 469.0, 79.0, 20.0 ],
					"id" : "obj-74",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "r host",
					"patching_rect" : [ 872.0, 328.0, 41.0, 20.0 ],
					"id" : "obj-34",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 0,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "print data messages  (even more messages!)",
					"presentation_linecount" : 2,
					"patching_rect" : [ 633.0, 185.0, 265.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-73",
					"fontsize" : 12.0,
					"presentation_rect" : [ 306.0, 80.0, 196.0, 34.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"patching_rect" : [ 607.0, 186.0, 20.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-72",
					"outlettype" : [ "int" ],
					"presentation_rect" : [ 280.0, 81.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "gswitch2",
					"patching_rect" : [ 651.0, 635.0, 39.0, 32.0 ],
					"id" : "obj-70",
					"outlettype" : [ "", "" ],
					"numinlets" : 2,
					"numoutlets" : 2
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "print sinkdata",
					"patching_rect" : [ 659.0, 691.0, 82.0, 20.0 ],
					"id" : "obj-71",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "push patcherargs",
					"patching_rect" : [ 92.0, 11.0, 150.0, 20.0 ],
					"id" : "obj-69",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "debugging switches",
					"patching_rect" : [ 640.0, 357.0, 150.0, 20.0 ],
					"id" : "obj-68",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "r host",
					"patching_rect" : [ 74.0, 569.0, 41.0, 20.0 ],
					"id" : "obj-67",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 0,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "s host",
					"patching_rect" : [ 267.0, 120.0, 43.0, 20.0 ],
					"id" : "obj-66",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "s port",
					"patching_rect" : [ 311.0, 119.0, 41.0, 20.0 ],
					"id" : "obj-65",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "r port",
					"patching_rect" : [ 48.0, 152.0, 39.0, 20.0 ],
					"id" : "obj-64",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 0,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "r port",
					"patching_rect" : [ 234.0, 464.0, 39.0, 20.0 ],
					"id" : "obj-63",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 0,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "r ping",
					"patching_rect" : [ 189.0, 462.0, 41.0, 20.0 ],
					"id" : "obj-62",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 0,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "r ping",
					"patching_rect" : [ 965.0, 126.0, 41.0, 20.0 ],
					"id" : "obj-61",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 0,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "s ping",
					"patching_rect" : [ 571.0, 679.0, 43.0, 20.0 ],
					"id" : "obj-60",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "bpatcher",
					"patching_rect" : [ 754.0, 515.0, 470.0, 198.0 ],
					"presentation" : 1,
					"id" : "obj-59",
					"name" : "dn.infomenu.maxpat",
					"presentation_rect" : [ 17.0, 331.0, 525.0, 198.0 ],
					"numinlets" : 2,
					"numoutlets" : 0,
					"args" : [  ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Rest server port",
					"patching_rect" : [ 854.0, 360.0, 150.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-58",
					"fontsize" : 12.0,
					"presentation_rect" : [ 88.0, 277.0, 150.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"hint" : "Press to reset  server listening port",
					"patching_rect" : [ 828.0, 330.0, 20.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-57",
					"outlettype" : [ "bang" ],
					"presentation_rect" : [ 48.0, 260.0, 37.0, 37.0 ],
					"numinlets" : 1,
					"fgcolor" : [ 0.478431, 0.709804, 0.317647, 1.0 ],
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "RX (ping)",
					"patching_rect" : [ 1002.0, 170.0, 150.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-55",
					"fontsize" : 12.0,
					"presentation_rect" : [ 84.0, 194.0, 69.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"patching_rect" : [ 977.0, 168.0, 20.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-56",
					"outlettype" : [ "bang" ],
					"presentation_rect" : [ 50.0, 192.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"ignoreclick" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "prepend ping",
					"patching_rect" : [ 598.0, 505.0, 81.0, 20.0 ],
					"id" : "obj-53",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "NOTE: host ip is is hardcoded in dn.netport for now.",
					"linecount" : 3,
					"patching_rect" : [ 834.0, 411.0, 156.0, 48.0 ],
					"id" : "obj-54",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Server listening port",
					"patching_rect" : [ 685.0, 263.0, 196.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-48",
					"fontsize" : 12.0,
					"presentation_rect" : [ 50.0, 238.0, 196.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "57120",
					"patching_rect" : [ 604.0, 266.0, 71.0, 18.0 ],
					"presentation" : 1,
					"id" : "obj-47",
					"outlettype" : [ "" ],
					"fontface" : 1,
					"fontsize" : 12.0,
					"textcolor" : [ 0.945098, 0.913725, 0.407843, 1.0 ],
					"bgcolor" : [ 0.211765, 0.133333, 0.066667, 1.0 ],
					"presentation_rect" : [ 50.0, 218.0, 70.0, 18.0 ],
					"numinlets" : 2,
					"fontname" : "Arial",
					"ignoreclick" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "r serverPort",
					"patching_rect" : [ 604.0, 227.0, 73.0, 20.0 ],
					"id" : "obj-46",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 0,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.netport",
					"patching_rect" : [ 831.0, 387.0, 65.0, 20.0 ],
					"id" : "obj-45",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"color" : [ 0.513726, 0.435294, 0.490196, 1.0 ],
					"numinlets" : 2,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "subscription file",
					"patching_rect" : [ 395.0, 97.0, 91.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-32",
					"fontsize" : 12.0,
					"presentation_rect" : [ 339.0, 271.0, 285.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Send messages to server",
					"patching_rect" : [ 717.0, 71.0, 285.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-52",
					"fontsize" : 12.0,
					"presentation_rect" : [ 308.0, 215.0, 285.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Debugging options",
					"patching_rect" : [ 564.0, 41.0, 222.0, 24.0 ],
					"presentation" : 1,
					"id" : "obj-51",
					"fontsize" : 18.0,
					"presentation_rect" : [ 50.0, 37.0, 222.0, 24.0 ],
					"numinlets" : 1,
					"fontname" : "Helvetica",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "print client messages",
					"patching_rect" : [ 631.0, 103.0, 285.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-49",
					"fontsize" : 12.0,
					"presentation_rect" : [ 92.0, 78.0, 122.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "print ping messages  (more messages)",
					"patching_rect" : [ 634.0, 159.0, 265.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-44",
					"fontsize" : 12.0,
					"presentation_rect" : [ 92.0, 141.0, 228.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"patching_rect" : [ 606.0, 160.0, 20.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-43",
					"outlettype" : [ "int" ],
					"presentation_rect" : [ 50.0, 138.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "gswitch2",
					"patching_rect" : [ 598.0, 467.0, 39.0, 32.0 ],
					"id" : "obj-42",
					"outlettype" : [ "", "" ],
					"numinlets" : 2,
					"numoutlets" : 2
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "OSC-route /ping /data/node",
					"patching_rect" : [ 599.0, 418.0, 157.0, 20.0 ],
					"id" : "obj-5",
					"outlettype" : [ "", "", "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 3
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "print server messages",
					"patching_rect" : [ 633.0, 131.0, 150.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-41",
					"fontsize" : 12.0,
					"presentation_rect" : [ 92.0, 110.0, 150.0, 20.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "unmatched",
					"patching_rect" : [ 460.0, 709.0, 76.0, 20.0 ],
					"id" : "obj-40",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "ping   ",
					"patching_rect" : [ 353.0, 700.0, 43.0, 20.0 ],
					"id" : "obj-39",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "info   ",
					"patching_rect" : [ 302.0, 705.0, 43.0, 20.0 ],
					"id" : "obj-38",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "error",
					"patching_rect" : [ 250.0, 702.0, 43.0, 20.0 ],
					"id" : "obj-37",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "data",
					"patching_rect" : [ 202.0, 702.0, 43.0, 20.0 ],
					"id" : "obj-36",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"patching_rect" : [ 606.0, 132.0, 20.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-31",
					"outlettype" : [ "int" ],
					"presentation_rect" : [ 50.0, 109.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "gswitch2",
					"patching_rect" : [ 598.0, 532.0, 39.0, 32.0 ],
					"id" : "obj-35",
					"outlettype" : [ "", "" ],
					"numinlets" : 2,
					"numoutlets" : 2
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"patching_rect" : [ 606.0, 103.0, 20.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-29",
					"outlettype" : [ "int" ],
					"presentation_rect" : [ 50.0, 78.0, 20.0, 20.0 ],
					"numinlets" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "gswitch2",
					"patching_rect" : [ 598.0, 349.0, 39.0, 32.0 ],
					"id" : "obj-28",
					"outlettype" : [ "", "" ],
					"numinlets" : 2,
					"numoutlets" : 2
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "print debug_serverMsg",
					"patching_rect" : [ 598.0, 572.0, 133.0, 20.0 ],
					"id" : "obj-26",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "print debug_clientMsg",
					"patching_rect" : [ 598.0, 390.0, 128.0, 20.0 ],
					"id" : "obj-25",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "OSC-route /datanetwork",
					"patching_rect" : [ 38.0, 414.0, 140.0, 20.0 ],
					"id" : "obj-15",
					"outlettype" : [ "", "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 2
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "outlet",
					"patching_rect" : [ 344.0, 667.0, 25.0, 25.0 ],
					"id" : "obj-13",
					"numinlets" : 1,
					"numoutlets" : 0,
					"comment" : "List of all matching resources"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"patching_rect" : [ 71.0, 8.0, 20.0, 20.0 ],
					"id" : "obj-24",
					"outlettype" : [ "bang" ],
					"numinlets" : 1,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "route host port",
					"patching_rect" : [ 267.0, 92.0, 88.0, 20.0 ],
					"id" : "obj-17",
					"outlettype" : [ "", "", "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 3
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "t b",
					"patching_rect" : [ 407.0, 44.0, 24.0, 20.0 ],
					"id" : "obj-22",
					"outlettype" : [ "bang" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "inlet",
					"patching_rect" : [ 407.0, 11.0, 25.0, 25.0 ],
					"id" : "obj-19",
					"outlettype" : [ "" ],
					"numinlets" : 0,
					"numoutlets" : 1,
					"comment" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "edit",
					"patching_rect" : [ 407.0, 71.0, 32.5, 18.0 ],
					"presentation" : 1,
					"id" : "obj-14",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"bgcolor" : [ 0.945098, 0.913725, 0.407843, 1.0 ],
					"presentation_rect" : [ 308.0, 271.0, 32.5, 18.0 ],
					"numinlets" : 2,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.manager",
					"patching_rect" : [ 436.0, 257.0, 75.0, 20.0 ],
					"id" : "obj-11",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"bgcolor" : [ 0.945098, 0.913725, 0.407843, 1.0 ],
					"color" : [ 0.513726, 0.435294, 0.490196, 1.0 ],
					"numinlets" : 2,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.announce",
					"patching_rect" : [ 18.0, 487.0, 80.0, 20.0 ],
					"id" : "obj-33",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"bgcolor" : [ 0.945098, 0.913725, 0.407843, 1.0 ],
					"color" : [ 0.513726, 0.435294, 0.490196, 1.0 ],
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "default local  listening port 6009",
					"linecount" : 2,
					"patching_rect" : [ 158.0, 200.0, 154.0, 34.0 ],
					"id" : "obj-30",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "t b l",
					"patching_rect" : [ 436.0, 293.0, 32.5, 20.0 ],
					"id" : "obj-12",
					"outlettype" : [ "bang", "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 2
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "respond to pings from Data Network",
					"patching_rect" : [ 243.0, 492.0, 203.0, 20.0 ],
					"id" : "obj-27",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.pong",
					"patching_rect" : [ 189.0, 490.0, 54.0, 20.0 ],
					"id" : "obj-18",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"bgcolor" : [ 0.945098, 0.913725, 0.407843, 1.0 ],
					"color" : [ 0.513726, 0.435294, 0.490196, 1.0 ],
					"numinlets" : 2,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "outlet",
					"patching_rect" : [ 438.0, 681.0, 25.0, 25.0 ],
					"id" : "obj-16",
					"numinlets" : 1,
					"numoutlets" : 0,
					"comment" : "List of all matching resources"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "inlet",
					"patching_rect" : [ 14.0, 11.0, 25.0, 25.0 ],
					"id" : "obj-8",
					"outlettype" : [ "" ],
					"numinlets" : 0,
					"numoutlets" : 1,
					"comment" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "outlet",
					"patching_rect" : [ 296.0, 667.0, 25.0, 25.0 ],
					"id" : "obj-7",
					"numinlets" : 1,
					"numoutlets" : 0,
					"comment" : "List of all matching resources"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "outlet",
					"patching_rect" : [ 253.0, 667.0, 25.0, 25.0 ],
					"id" : "obj-2",
					"numinlets" : 1,
					"numoutlets" : 0,
					"comment" : "List of all matching resources"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "server host  and port  ",
					"patching_rect" : [ 79.0, 75.0, 150.0, 20.0 ],
					"id" : "obj-87",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"patching_rect" : [ 603.0, 71.0, 100.0, 20.0 ],
					"presentation" : 1,
					"id" : "obj-81",
					"outlettype" : [ "int", "", "" ],
					"fontsize" : 12.0,
					"presentation_rect" : [ 308.0, 240.0, 179.0, 20.0 ],
					"numinlets" : 1,
					"items" : [ "/subscribe", ",", "/unsubscribe", ",", "/register", ",", "/unregister", ",", "/query/expected", ",", "/query/nodes", ",", "/query/slots", ",", "/get" ],
					"fontname" : "Arial",
					"numoutlets" : 3,
					"types" : [  ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "inlet",
					"patching_rect" : [ 491.0, 11.0, 25.0, 25.0 ],
					"id" : "obj-23",
					"outlettype" : [ "" ],
					"numinlets" : 0,
					"numoutlets" : 1,
					"comment" : ""
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "prepend port",
					"patching_rect" : [ 48.0, 178.0, 79.0, 20.0 ],
					"id" : "obj-4",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "OSC-route /data /error /info /ping",
					"patching_rect" : [ 260.0, 600.0, 185.0, 20.0 ],
					"id" : "obj-21",
					"outlettype" : [ "", "", "", "", "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 5
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "udpreceive 6009",
					"patching_rect" : [ 48.0, 206.0, 99.0, 20.0 ],
					"id" : "obj-20",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "prepend host",
					"patching_rect" : [ 77.0, 593.0, 81.0, 20.0 ],
					"id" : "obj-6",
					"outlettype" : [ "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "udpsend 127.0.0.1 57121",
					"patching_rect" : [ 46.0, 619.0, 147.0, 20.0 ],
					"id" : "obj-3",
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 0
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "OpenSoundControl",
					"patching_rect" : [ 40.0, 537.0, 113.0, 20.0 ],
					"id" : "obj-1",
					"outlettype" : [ "", "", "OSCTimeTag" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 3
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "outlet",
					"patching_rect" : [ 208.0, 667.0, 25.0, 25.0 ],
					"id" : "obj-10",
					"numinlets" : 1,
					"numoutlets" : 0,
					"comment" : "List of all matching resources"
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "patcherargs @host 127.0.0.1 @port 6009 @file subscription.mtxt",
					"patching_rect" : [ 14.0, 50.0, 356.0, 20.0 ],
					"id" : "obj-9",
					"outlettype" : [ "", "" ],
					"fontsize" : 12.0,
					"numinlets" : 1,
					"fontname" : "Arial",
					"numoutlets" : 2
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"patching_rect" : [ 1000.0, 130.0, 360.0, 193.0 ],
					"presentation" : 1,
					"id" : "obj-50",
					"rounded" : 0,
					"bgcolor" : [ 0.858824, 0.858824, 0.858824, 1.0 ],
					"background" : 1,
					"presentation_rect" : [ 28.0, 27.0, 516.0, 289.0 ],
					"numinlets" : 1,
					"numoutlets" : 0,
					"grad2" : [ 0.858824, 0.858824, 0.858824, 1.0 ]
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-5", 2 ],
					"destination" : [ "obj-35", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-20", 0 ],
					"destination" : [ "obj-5", 0 ],
					"hidden" : 0,
					"midpoints" : [ 57.5, 399.0, 585.0, 399.0, 585.0, 414.0, 608.5, 414.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-42", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-74", 0 ],
					"destination" : [ "obj-3", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-74", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-63", 0 ],
					"destination" : [ "obj-18", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-62", 0 ],
					"destination" : [ "obj-18", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-18", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 198.5, 534.0, 49.5, 534.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-33", 0 ],
					"destination" : [ "obj-3", 0 ],
					"hidden" : 0,
					"midpoints" : [ 27.5, 606.0, 55.5, 606.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-15", 0 ],
					"destination" : [ "obj-33", 0 ],
					"hidden" : 0,
					"midpoints" : [ 47.5, 472.0, 27.5, 472.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-9", 1 ],
					"destination" : [ "obj-17", 0 ],
					"hidden" : 0,
					"midpoints" : [ 360.5, 84.0, 276.5, 84.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-8", 0 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-24", 0 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-22", 0 ],
					"destination" : [ "obj-14", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-19", 0 ],
					"destination" : [ "obj-22", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-12", 1 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 459.0, 521.0, 102.0, 521.0, 102.0, 522.0, 49.5, 522.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-12", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 445.5, 521.0, 102.0, 521.0, 102.0, 522.0, 49.5, 522.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 0 ],
					"destination" : [ "obj-10", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 4 ],
					"destination" : [ "obj-16", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 3 ],
					"destination" : [ "obj-13", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 2 ],
					"destination" : [ "obj-7", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 1 ],
					"destination" : [ "obj-2", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-15", 1 ],
					"destination" : [ "obj-21", 0 ],
					"hidden" : 0,
					"midpoints" : [ 168.5, 585.0, 269.5, 585.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-1", 0 ],
					"destination" : [ "obj-3", 0 ],
					"hidden" : 0,
					"midpoints" : [ 49.5, 606.0, 55.5, 606.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-6", 0 ],
					"destination" : [ "obj-3", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-29", 0 ],
					"destination" : [ "obj-28", 0 ],
					"hidden" : 0,
					"midpoints" : [ 615.5, 123.0, 603.0, 123.0, 603.0, 345.0, 607.5, 345.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-31", 0 ],
					"destination" : [ "obj-35", 0 ],
					"hidden" : 0,
					"midpoints" : [ 615.5, 153.0, 603.0, 153.0, 603.0, 336.0, 585.0, 336.0, 585.0, 528.0, 607.5, 528.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-35", 1 ],
					"destination" : [ "obj-26", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-12", 1 ],
					"destination" : [ "obj-28", 1 ],
					"hidden" : 0,
					"midpoints" : [ 459.0, 336.0, 627.5, 336.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-43", 0 ],
					"destination" : [ "obj-42", 0 ],
					"hidden" : 0,
					"midpoints" : [ 615.5, 180.0, 597.0, 180.0, 597.0, 336.0, 585.0, 336.0, 585.0, 453.0, 607.5, 453.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-42", 1 ],
					"destination" : [ "obj-53", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-53", 0 ],
					"destination" : [ "obj-35", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-28", 1 ],
					"destination" : [ "obj-25", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-61", 0 ],
					"destination" : [ "obj-56", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 3 ],
					"destination" : [ "obj-60", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-20", 0 ],
					"destination" : [ "obj-15", 0 ],
					"hidden" : 0,
					"midpoints" : [ 57.5, 399.0, 47.5, 399.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-4", 0 ],
					"destination" : [ "obj-20", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-64", 0 ],
					"destination" : [ "obj-4", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-17", 1 ],
					"destination" : [ "obj-65", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-17", 0 ],
					"destination" : [ "obj-66", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-67", 0 ],
					"destination" : [ "obj-6", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-72", 0 ],
					"destination" : [ "obj-70", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-70", 1 ],
					"destination" : [ "obj-71", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 0 ],
					"destination" : [ "obj-70", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-81", 1 ],
					"destination" : [ "obj-11", 0 ],
					"hidden" : 0,
					"midpoints" : [ 653.0, 93.0, 500.0, 93.0, 500.0, 243.0, 445.5, 243.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-23", 0 ],
					"destination" : [ "obj-11", 0 ],
					"hidden" : 0,
					"midpoints" : [ 500.5, 243.0, 445.5, 243.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-14", 0 ],
					"destination" : [ "obj-11", 1 ],
					"hidden" : 0,
					"midpoints" : [ 416.5, 90.0, 381.0, 90.0, 381.0, 243.0, 501.5, 243.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-4", 0 ],
					"destination" : [ "obj-11", 1 ],
					"hidden" : 0,
					"midpoints" : [ 57.5, 198.0, 33.0, 198.0, 33.0, 243.0, 501.5, 243.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-11", 0 ],
					"destination" : [ "obj-12", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-17", 2 ],
					"destination" : [ "obj-11", 1 ],
					"hidden" : 0,
					"midpoints" : [ 345.5, 244.0, 501.5, 244.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-57", 0 ],
					"destination" : [ "obj-45", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-34", 0 ],
					"destination" : [ "obj-45", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-46", 0 ],
					"destination" : [ "obj-47", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-45", 0 ],
					"destination" : [ "obj-47", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-75", 0 ],
					"destination" : [ "obj-57", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
 ]
	}

}
