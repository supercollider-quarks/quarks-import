{
	"patcher" : 	{
		"fileversion" : 1,
		"rect" : [ 60.0, 143.0, 1121.0, 611.0 ],
		"bglocked" : 0,
		"defrect" : [ 60.0, 143.0, 1121.0, 611.0 ],
		"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
		"openinpresentation" : 0,
		"default_fontsize" : 18.0,
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
					"maxclass" : "comment",
					"text" : "A way for you to see all the Nodes on the network by selected from the drop down menu.\n\nIf PINGS are blinking, it means you're connected to the network",
					"linecount" : 10,
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"id" : "obj-45",
					"patching_rect" : [ 336.0, 377.0, 132.0, 144.0 ],
					"fontname" : "Arial",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-46",
					"patching_rect" : [ 332.0, 373.0, 134.0, 155.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "See DN.NODE help for more info on the object",
					"linecount" : 3,
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"id" : "obj-12",
					"patching_rect" : [ 691.0, 478.0, 118.0, 48.0 ],
					"fontname" : "Arial",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "See The Network!",
					"numoutlets" : 0,
					"fontsize" : 24.0,
					"id" : "obj-8",
					"patching_rect" : [ 82.0, 381.0, 203.0, 34.0 ],
					"frgb" : [ 1.0, 1.0, 1.0, 1.0 ],
					"fontname" : "Arial",
					"numinlets" : 1,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "This object subscribes to the node you made above using the unique ID specified (111). This is how others using Max will see your data..\n\n",
					"linecount" : 4,
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"id" : "obj-22",
					"patching_rect" : [ 696.0, 382.0, 282.0, 62.0 ],
					"fontname" : "Arial",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "dn.makenode",
					"numoutlets" : 0,
					"fontsize" : 24.0,
					"bgcolor" : [ 0.184314, 0.184314, 0.184314, 1.0 ],
					"id" : "obj-20",
					"patching_rect" : [ 75.0, 45.0, 230.0, 34.0 ],
					"frgb" : [ 0.768627, 0.941176, 0.086275, 1.0 ],
					"fontname" : "Arial",
					"numinlets" : 1,
					"textcolor" : [ 0.768627, 0.941176, 0.086275, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "0.81 0.7 0.05",
					"numoutlets" : 1,
					"fontsize" : 13.0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"outlettype" : [ "" ],
					"gradient" : 1,
					"id" : "obj-14",
					"patching_rect" : [ 519.0, 488.0, 141.0, 19.0 ],
					"fontname" : "Arial",
					"numinlets" : 2
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.node max_client 111",
					"numoutlets" : 2,
					"fontsize" : 12.0,
					"outlettype" : [ "", "" ],
					"id" : "obj-13",
					"patching_rect" : [ 519.0, 431.0, 138.0, 20.0 ],
					"fontname" : "Arial",
					"numinlets" : 1,
					"color" : [ 0.156863, 0.156863, 0.156863, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "0.81 0.7 0.05",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"bgcolor2" : [ 1.0, 1.0, 1.0, 1.0 ],
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"outlettype" : [ "" ],
					"gradient" : 1,
					"id" : "obj-10",
					"patching_rect" : [ 404.0, 225.0, 117.0, 18.0 ],
					"fontname" : "Arial",
					"numinlets" : 2
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "p randomData",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"id" : "obj-29",
					"patching_rect" : [ 404.0, 180.0, 87.0, 20.0 ],
					"fontname" : "Arial",
					"numinlets" : 0,
					"color" : [ 0.258824, 0.258824, 0.258824, 1.0 ],
					"patcher" : 					{
						"fileversion" : 1,
						"rect" : [ 729.0, 111.0, 640.0, 480.0 ],
						"bglocked" : 0,
						"defrect" : [ 729.0, 111.0, 640.0, 480.0 ],
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
						"boxes" : [ 							{
								"box" : 								{
									"maxclass" : "toggle",
									"numoutlets" : 1,
									"outlettype" : [ "int" ],
									"id" : "obj-6",
									"patching_rect" : [ 31.0, 51.0, 20.0, 20.0 ],
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "loadbang",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "bang" ],
									"id" : "obj-4",
									"patching_rect" : [ 30.0, 20.0, 60.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "vexpr $f1* 0.01",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-13",
									"patching_rect" : [ 33.0, 193.0, 91.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "pack i i i",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-3",
									"patching_rect" : [ 30.0, 165.0, 169.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 3
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "drunk 100 2",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "int" ],
									"id" : "obj-2",
									"patching_rect" : [ 180.0, 135.0, 74.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 3
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "drunk 100 2",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "int" ],
									"id" : "obj-1",
									"patching_rect" : [ 105.0, 135.0, 74.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 3
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "drunk 100 2",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "int" ],
									"id" : "obj-24",
									"patching_rect" : [ 30.0, 135.0, 74.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 3
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "metro 100",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "bang" ],
									"id" : "obj-15",
									"patching_rect" : [ 30.0, 90.0, 65.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 2
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "outlet",
									"numoutlets" : 0,
									"id" : "obj-28",
									"patching_rect" : [ 33.0, 229.0, 25.0, 25.0 ],
									"numinlets" : 1,
									"comment" : ""
								}

							}
 ],
						"lines" : [ 							{
								"patchline" : 								{
									"source" : [ "obj-6", 0 ],
									"destination" : [ "obj-15", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-4", 0 ],
									"destination" : [ "obj-6", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-13", 0 ],
									"destination" : [ "obj-28", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-3", 0 ],
									"destination" : [ "obj-13", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-1", 0 ],
									"destination" : [ "obj-3", 1 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-15", 0 ],
									"destination" : [ "obj-1", 0 ],
									"hidden" : 0,
									"midpoints" : [ 39.5, 122.0, 114.5, 122.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-15", 0 ],
									"destination" : [ "obj-2", 0 ],
									"hidden" : 0,
									"midpoints" : [ 39.5, 122.0, 189.5, 122.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-15", 0 ],
									"destination" : [ "obj-24", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-2", 0 ],
									"destination" : [ "obj-3", 2 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-24", 0 ],
									"destination" : [ "obj-3", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
 ]
					}
,
					"saved_object_attributes" : 					{
						"fontface" : 0,
						"fontsize" : 12.0,
						"default_fontface" : 0,
						"globalpatchername" : "",
						"fontname" : "Arial",
						"default_fontname" : "Arial",
						"default_fontsize" : 12.0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "dn.makenode max_client 111 3 mySensor",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"id" : "obj-1",
					"patching_rect" : [ 383.0, 111.0, 237.0, 20.0 ],
					"fontname" : "Arial",
					"numinlets" : 2,
					"color" : [ 0.156863, 0.156863, 0.156863, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : " dn.node",
					"numoutlets" : 0,
					"fontsize" : 24.0,
					"bgcolor" : [ 0.635294, 0.682353, 0.494118, 1.0 ],
					"id" : "obj-49",
					"patching_rect" : [ 520.0, 380.0, 139.0, 34.0 ],
					"frgb" : [ 1.0, 1.0, 1.0, 1.0 ],
					"fontname" : "Arial",
					"numinlets" : 1,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "3\\",
					"numoutlets" : 0,
					"fontsize" : 24.0,
					"id" : "obj-44",
					"patching_rect" : [ 345.0, 161.0, 30.0, 30.0 ],
					"frgb" : [ 1.0, 1.0, 1.0, 1.0 ],
					"fontname" : "Helvetica",
					"numinlets" : 1,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "Data must be sent as a list of floats, each of which correspond to a slot",
					"linecount" : 2,
					"numoutlets" : 0,
					"fontsize" : 13.0,
					"id" : "obj-39",
					"patching_rect" : [ 662.0, 232.0, 307.0, 32.0 ],
					"fontname" : "Helvetica",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bordercolor" : [ 0.811765, 0.839216, 0.709804, 0.0 ],
					"bgcolor" : [ 0.917647, 0.505882, 0.458824, 1.0 ],
					"grad1" : [ 0.8, 0.839216, 0.709804, 1.0 ],
					"id" : "obj-41",
					"patching_rect" : [ 345.0, 161.0, 286.0, 105.0 ],
					"grad2" : [ 0.0, 0.0, 0.0, 1.0 ],
					"angle" : 11.130005,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-42",
					"patching_rect" : [ 658.0, 230.0, 325.0, 36.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"id" : "obj-43",
					"patching_rect" : [ 547.0, 244.0, 327.0, 4.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "You will have to modify the \"pv dn-host <IP address>\" object to reflect the IP address of your datanetwork server - this MUST be included in the parent patcher.",
					"linecount" : 3,
					"numoutlets" : 0,
					"fontsize" : 13.0,
					"id" : "obj-11",
					"patching_rect" : [ 662.0, 42.0, 322.0, 45.0 ],
					"fontname" : "Helvetica",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-33",
					"patching_rect" : [ 657.0, 37.0, 325.0, 55.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "1\\",
					"numoutlets" : 0,
					"fontsize" : 24.0,
					"id" : "obj-2",
					"patching_rect" : [ 345.0, 45.0, 30.0, 30.0 ],
					"frgb" : [ 1.0, 1.0, 1.0, 1.0 ],
					"fontname" : "Helvetica",
					"numinlets" : 1,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "After DN.MAKENODE the arguments are:\n1)Your network name (you choose)\n2) The Node ID (a unique number that others use to subscribe to your data)\n3) The number of Slots (how much information it will handle)\n4) The name of the new Node (choose something that others can identify)",
					"linecount" : 8,
					"numoutlets" : 0,
					"fontsize" : 13.0,
					"id" : "obj-24",
					"patching_rect" : [ 662.0, 109.0, 328.0, 110.0 ],
					"fontname" : "Helvetica",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "This object both CREATES a node on the network and allows SENDING of information to that node",
					"linecount" : 4,
					"numoutlets" : 0,
					"fontsize" : 18.0,
					"bgcolor" : [ 0.184314, 0.184314, 0.184314, 1.0 ],
					"id" : "obj-16",
					"patching_rect" : [ 75.0, 105.0, 231.0, 89.0 ],
					"frgb" : [ 0.898039, 0.937255, 0.94902, 1.0 ],
					"fontname" : "Arial",
					"numinlets" : 1,
					"textcolor" : [ 0.898039, 0.937255, 0.94902, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "2\\",
					"numoutlets" : 0,
					"fontsize" : 24.0,
					"id" : "obj-27",
					"patching_rect" : [ 345.0, 105.0, 29.0, 30.0 ],
					"frgb" : [ 1.0, 1.0, 1.0, 1.0 ],
					"fontname" : "Helvetica",
					"numinlets" : 1,
					"textcolor" : [ 1.0, 1.0, 1.0, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "data",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"id" : "obj-15",
					"patching_rect" : [ 258.0, 490.0, 42.0, 20.0 ],
					"fontname" : "Arial",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"numoutlets" : 1,
					"outlettype" : [ "bang" ],
					"id" : "obj-18",
					"patching_rect" : [ 242.0, 491.0, 20.0, 20.0 ],
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "umenu",
					"items" : [ "/query/expected", ",", "/query/nodes", ",", "/query/slots" ],
					"numoutlets" : 3,
					"fontsize" : 12.0,
					"outlettype" : [ "int", "", "" ],
					"id" : "obj-25",
					"patching_rect" : [ 77.0, 430.0, 108.0, 20.0 ],
					"types" : [  ],
					"fontname" : "Arial",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "print",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"id" : "obj-23",
					"patching_rect" : [ 212.0, 430.0, 32.0, 20.0 ],
					"fontname" : "Arial",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "toggle",
					"numoutlets" : 1,
					"outlettype" : [ "int" ],
					"id" : "obj-21",
					"patching_rect" : [ 242.0, 431.0, 20.0, 20.0 ],
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "comment",
					"text" : "pings",
					"numoutlets" : 0,
					"fontsize" : 12.0,
					"id" : "obj-19",
					"patching_rect" : [ 139.0, 488.0, 42.0, 20.0 ],
					"fontname" : "Arial",
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "button",
					"numoutlets" : 1,
					"outlettype" : [ "bang" ],
					"id" : "obj-17",
					"patching_rect" : [ 122.0, 489.0, 20.0, 20.0 ],
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "p talk_to_datanetwork",
					"numoutlets" : 2,
					"fontsize" : 12.0,
					"outlettype" : [ "", "" ],
					"id" : "obj-9",
					"patching_rect" : [ 122.0, 460.0, 139.0, 20.0 ],
					"fontname" : "Arial",
					"numinlets" : 2,
					"patcher" : 					{
						"fileversion" : 1,
						"rect" : [ 723.0, 59.0, 640.0, 480.0 ],
						"bglocked" : 0,
						"defrect" : [ 723.0, 59.0, 640.0, 480.0 ],
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
						"boxes" : [ 							{
								"box" : 								{
									"maxclass" : "outlet",
									"numoutlets" : 0,
									"id" : "obj-18",
									"patching_rect" : [ 330.0, 180.0, 25.0, 25.0 ],
									"numinlets" : 1,
									"comment" : ""
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "append 6009 max_client",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-20",
									"patching_rect" : [ 60.0, 180.0, 141.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "button",
									"numoutlets" : 1,
									"outlettype" : [ "bang" ],
									"id" : "obj-19",
									"patching_rect" : [ 120.0, 105.0, 20.0, 20.0 ],
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "r dn.node",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-9",
									"patching_rect" : [ 120.0, 75.0, 61.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 0
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "print",
									"numoutlets" : 0,
									"fontsize" : 12.0,
									"id" : "obj-17",
									"patching_rect" : [ 555.0, 180.0, 34.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "unpack s i",
									"numoutlets" : 2,
									"fontsize" : 12.0,
									"outlettype" : [ "", "int" ],
									"id" : "obj-16",
									"patching_rect" : [ 390.0, 180.0, 65.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "outlet",
									"numoutlets" : 0,
									"id" : "obj-15",
									"patching_rect" : [ 270.0, 180.0, 25.0, 25.0 ],
									"numinlets" : 1,
									"comment" : ""
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "route /datanetwork/announce",
									"numoutlets" : 2,
									"fontsize" : 12.0,
									"outlettype" : [ "", "" ],
									"id" : "obj-14",
									"patching_rect" : [ 390.0, 150.0, 166.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "dn.netport",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-13",
									"patching_rect" : [ 195.0, 225.0, 65.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "pv dn-host",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-12",
									"patching_rect" : [ 195.0, 150.0, 67.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "deferlow",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-11",
									"patching_rect" : [ 195.0, 105.0, 56.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "loadbang",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "bang" ],
									"id" : "obj-10",
									"patching_rect" : [ 195.0, 75.0, 60.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "gate",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-8",
									"patching_rect" : [ 555.0, 150.0, 34.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 2
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "inlet",
									"numoutlets" : 1,
									"outlettype" : [ "int" ],
									"id" : "obj-7",
									"patching_rect" : [ 555.0, 75.0, 25.0, 25.0 ],
									"numinlets" : 0,
									"comment" : ""
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "route /ping /data/node",
									"numoutlets" : 3,
									"fontsize" : 12.0,
									"outlettype" : [ "", "", "" ],
									"id" : "obj-6",
									"patching_rect" : [ 270.0, 105.0, 139.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "udpreceive 6009",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-5",
									"patching_rect" : [ 270.0, 75.0, 99.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "prepend host",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-4",
									"patching_rect" : [ 105.0, 225.0, 81.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "prepend port",
									"numoutlets" : 1,
									"fontsize" : 12.0,
									"outlettype" : [ "" ],
									"id" : "obj-3",
									"patching_rect" : [ 195.0, 270.0, 79.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "newobj",
									"text" : "udpsend localhost 7474",
									"numoutlets" : 0,
									"fontsize" : 12.0,
									"id" : "obj-2",
									"patching_rect" : [ 60.0, 315.0, 137.0, 20.0 ],
									"fontname" : "Arial",
									"numinlets" : 1
								}

							}
, 							{
								"box" : 								{
									"maxclass" : "inlet",
									"numoutlets" : 1,
									"outlettype" : [ "" ],
									"id" : "obj-1",
									"patching_rect" : [ 60.0, 75.0, 25.0, 25.0 ],
									"numinlets" : 0,
									"comment" : ""
								}

							}
 ],
						"lines" : [ 							{
								"patchline" : 								{
									"source" : [ "obj-1", 0 ],
									"destination" : [ "obj-20", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-10", 0 ],
									"destination" : [ "obj-11", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-11", 0 ],
									"destination" : [ "obj-12", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-12", 0 ],
									"destination" : [ "obj-13", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-12", 0 ],
									"destination" : [ "obj-4", 0 ],
									"hidden" : 0,
									"midpoints" : [ 204.5, 212.0, 114.5, 212.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-13", 0 ],
									"destination" : [ "obj-3", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-14", 0 ],
									"destination" : [ "obj-16", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-16", 1 ],
									"destination" : [ "obj-3", 0 ],
									"hidden" : 0,
									"midpoints" : [ 445.5, 257.0, 204.5, 257.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-16", 0 ],
									"destination" : [ "obj-4", 0 ],
									"hidden" : 0,
									"midpoints" : [ 399.5, 212.0, 114.5, 212.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-19", 0 ],
									"destination" : [ "obj-12", 0 ],
									"hidden" : 0,
									"midpoints" : [ 129.5, 137.0, 204.5, 137.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-20", 0 ],
									"destination" : [ "obj-2", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-3", 0 ],
									"destination" : [ "obj-2", 0 ],
									"hidden" : 0,
									"midpoints" : [ 204.5, 302.0, 69.5, 302.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-4", 0 ],
									"destination" : [ "obj-2", 0 ],
									"hidden" : 0,
									"midpoints" : [ 114.5, 287.0, 69.5, 287.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-5", 0 ],
									"destination" : [ "obj-6", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-6", 2 ],
									"destination" : [ "obj-14", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-6", 0 ],
									"destination" : [ "obj-15", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-6", 1 ],
									"destination" : [ "obj-18", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-6", 2 ],
									"destination" : [ "obj-8", 1 ],
									"hidden" : 0,
									"midpoints" : [ 399.5, 137.0, 579.5, 137.0 ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-7", 0 ],
									"destination" : [ "obj-8", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-8", 0 ],
									"destination" : [ "obj-17", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
, 							{
								"patchline" : 								{
									"source" : [ "obj-9", 0 ],
									"destination" : [ "obj-19", 0 ],
									"hidden" : 0,
									"midpoints" : [  ]
								}

							}
 ]
					}
,
					"saved_object_attributes" : 					{
						"fontface" : 0,
						"fontsize" : 12.0,
						"default_fontface" : 0,
						"globalpatchername" : "",
						"fontname" : "Arial",
						"default_fontname" : "Arial",
						"default_fontsize" : 12.0
					}

				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "pv dn-host 192.168.0.104",
					"numoutlets" : 1,
					"fontsize" : 12.0,
					"outlettype" : [ "" ],
					"id" : "obj-6",
					"patching_rect" : [ 386.0, 52.0, 154.0, 20.0 ],
					"fontname" : "Arial",
					"numinlets" : 1,
					"color" : [ 0.254902, 0.258824, 0.243137, 1.0 ]
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bordercolor" : [ 0.811765, 0.839216, 0.709804, 0.0 ],
					"bgcolor" : [ 0.8, 0.839216, 0.294118, 1.0 ],
					"grad1" : [ 0.8, 0.839216, 0.709804, 1.0 ],
					"id" : "obj-30",
					"patching_rect" : [ 345.0, 45.0, 284.0, 35.0 ],
					"grad2" : [ 0.0, 0.0, 0.0, 1.0 ],
					"angle" : 11.130005,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bordercolor" : [ 0.811765, 0.839216, 0.709804, 0.0 ],
					"bgcolor" : [ 0.709804, 0.839216, 0.831373, 1.0 ],
					"grad1" : [ 0.8, 0.839216, 0.709804, 1.0 ],
					"id" : "obj-32",
					"patching_rect" : [ 345.0, 105.0, 285.0, 34.0 ],
					"grad2" : [ 0.0, 0.0, 0.0, 1.0 ],
					"angle" : 11.130005,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"id" : "obj-34",
					"patching_rect" : [ 547.0, 60.0, 327.0, 4.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-36",
					"patching_rect" : [ 657.0, 106.0, 326.0, 114.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"id" : "obj-35",
					"patching_rect" : [ 548.0, 116.0, 327.0, 4.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bordercolor" : [ 0.811765, 0.839216, 0.709804, 0.0 ],
					"bgcolor" : [ 0.83, 0.88, 0.7, 1.0 ],
					"grad1" : [ 0.8, 0.839216, 0.709804, 1.0 ],
					"id" : "obj-31",
					"patching_rect" : [ 508.0, 371.0, 161.0, 156.0 ],
					"grad2" : [ 0.0, 0.0, 0.0, 1.0 ],
					"angle" : 11.130005,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 1.0 ],
					"id" : "obj-5",
					"patching_rect" : [ 692.0, 377.0, 290.0, 60.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"id" : "obj-7",
					"patching_rect" : [ 552.0, 393.0, 150.0, 4.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bordercolor" : [ 0.0, 0.0, 0.0, 0.0 ],
					"bgcolor" : [ 0.94902, 1.0, 0.85098, 1.0 ],
					"grad1" : [ 0.905882, 1.0, 0.854902, 1.0 ],
					"id" : "obj-48",
					"patching_rect" : [ 64.0, 23.0, 927.0, 284.0 ],
					"grad2" : [ 0.843137, 0.858824, 0.717647, 1.0 ],
					"border" : 2,
					"rounded" : 0,
					"angle" : -90.0,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bordercolor" : [ 0.811765, 0.839216, 0.709804, 0.0 ],
					"bgcolor" : [ 0.427451, 0.572549, 0.615686, 1.0 ],
					"grad1" : [ 0.8, 0.839216, 0.709804, 1.0 ],
					"id" : "obj-26",
					"patching_rect" : [ 80.0, 378.0, 209.0, 40.0 ],
					"grad2" : [ 0.0, 0.0, 0.0, 1.0 ],
					"angle" : 11.130005,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bordercolor" : [ 0.811765, 0.839216, 0.709804, 0.0 ],
					"bgcolor" : [ 0.709804, 0.839216, 0.831373, 1.0 ],
					"grad1" : [ 0.8, 0.839216, 0.709804, 1.0 ],
					"id" : "obj-38",
					"patching_rect" : [ 69.0, 370.0, 238.0, 156.0 ],
					"grad2" : [ 0.0, 0.0, 0.0, 1.0 ],
					"angle" : 11.130005,
					"numinlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "panel",
					"numoutlets" : 0,
					"bgcolor" : [ 1.0, 1.0, 1.0, 0.0 ],
					"id" : "obj-40",
					"patching_rect" : [ 222.0, 396.0, 150.0, 4.0 ],
					"border" : 2,
					"rounded" : 0,
					"numinlets" : 1
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-29", 0 ],
					"destination" : [ "obj-10", 1 ],
					"hidden" : 0,
					"midpoints" : [ 413.5, 207.0, 511.5, 207.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-21", 0 ],
					"destination" : [ "obj-9", 1 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-25", 1 ],
					"destination" : [ "obj-9", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-9", 0 ],
					"destination" : [ "obj-17", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-9", 1 ],
					"destination" : [ "obj-18", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-29", 0 ],
					"destination" : [ "obj-10", 0 ],
					"hidden" : 0,
					"midpoints" : [ 413.5, 207.0, 413.5, 207.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-13", 0 ],
					"destination" : [ "obj-14", 1 ],
					"hidden" : 0,
					"midpoints" : [ 528.5, 461.0, 650.5, 461.0 ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-10", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [ 413.5, 254.0, 380.0, 254.0, 380.0, 106.0, 392.5, 106.0 ]
				}

			}
 ]
	}

}
