//redFrik

//related: RedGIF

//todo:
//* fix 4, 16 and 24bit support for when creating images from scratch and writing to disk
//* support 1bit images
//* still some 24bit files not loading correctly

RedBMP {
	var	<type,									//string "BM"
		<>width,									//integer
		<>height,									//integer
		<>depth,									//integer
		<fileSize,								//integer
		<offset,									//integer
		<headerSize,								//integer
		<planes,									//integer
		<compression,								//integer
		<>imageSize,								//integer
		<>horizontalResolution,						//integer
		<>verticalResolution,						//integer
		<>numColors,								//integer
		<>numImportantColors,						//integer
		<>topToBottom,							//flag
		<>palette,								//array of color objects
		<>data;									//array of color objects
	*new {|width= 320, height= 240, depth= 32|
		^super.new.initRedBMP(width, height, depth);
	}
	initRedBMP {|argWidth, argHeight, argDepth|
		type= "BM";
		width= argWidth;
		height= argHeight;
		depth= argDepth;
		if(depth!=32, {"only depth 32 supported for now".warn});
		fileSize= switch(depth,
			1, {},//TODO
			4, {(((width/4).ceil*4)*height/2).asInteger},//TODO
			8, {(((width/4).ceil*4)*height).asInteger},//TODO
			24, {(((width/4).ceil*4)*height*3).asInteger},//TODO
			32, {width*height*4+54}
		);
		offset= 54;
		headerSize= 40;
		planes= 1;
		compression= 0;
		imageSize= 0;
		horizontalResolution= 2835;					//hardcoded as 72dpi
		verticalResolution= 2835;					//hardcoded as 72dpi
		numColors= 0;
		numImportantColors= 0;
		topToBottom= false;
	}
	*read {|path|
		^super.new.read(path);
	}
	read {|path|
		path= path.standardizePath;
		if(File.exists(path), {
			this.prRead(path);
		}, {(this.class.name++": file"+path+"not found").error});
	}
	write {|path|
		path= path.standardizePath;
		this.prWrite(path);
	}
	makeWindow {|bounds|
		var b= bounds ?? {Rect(300, 300, width, height)};
		var win= Window(this.class.name, b, false);
		win.drawHook= {
			Pen.smoothing= false;
			data.do{|c, i|
				var x, y;
				x= i%width;
				y= i.div(width);
				if(topToBottom.not, {
					y= height-y;
				});
				Pen.fillColor= c;
				Pen.fillRect(Rect(x, y, 1, 1));
			};
		};
		win.front;
		^win;
	}
	
	//--private
	prRead {|path|
		var file= File(path, "r");
		this.prReadFileHeader(file);
		if(type=="BM", {							//possibly add "BA", "CI", "CP", "IC", "PT"
			this.prReadInfoHeader(file);
			if(headerSize==40, {
				if(compression==0, {
					if(depth<16, {
						this.prReadPalette(file);
					}, {
						if(offset!=54, {
							(this.class.name++": offset incicates there is a palette but bitdepth >= 16").warn;
						});
					});
					if(file.pos!=offset, {
						(this.class.name++": offset file position mismatch: offset:"+offset+"file.pos:"+file.pos).warn;
					});
					this.prReadData(file);
				}, {
					(this.class.name++": compression"+(#[\BI_RGB, \BI_RLE8, \BI_RLE4, \BI_BITFIELDS, \BI_JPEG, \BI_PNG][compression])+"not yet supported").error;
				});
			}, {
				(this.class.name++": only Windows V3 supported").error;
			});
		}, {
			(this.class.name++": type"+type+"not recognized").error;
		});
		file.close;
	}
	prReadFileHeader {|file|
		type= {file.getChar}.dup(2).join;
		fileSize= file.getInt32LE;					//file size
		file.getInt16;							//creator 1
		file.getInt16;							//creator 2
		offset= file.getInt32LE;					//data offset
	}
	prReadInfoHeader {|file|
		headerSize= file.getInt32LE;				//size of this header (40)
		width= file.getInt32LE;						//bitmap width in pixels
		height= file.getInt32LE;					//bitmap height in pixels
		if(height<0, {
			topToBottom= true;
			height= 0-height;
		}, {
			topToBottom= false;
		});
		planes= file.getInt16LE;					//number of color planes (must be 1)
		depth= file.getInt16LE;						//number of bits per pixel (1, 4, 8, 16...)
		compression= file.getInt32LE;				//compression method (0, 1, 2, 3, 4, 5)
		imageSize= file.getInt32LE;					//image size of the raw bitmap data
		horizontalResolution= file.getInt32LE;		//horizontal resolution
		verticalResolution= file.getInt32LE;			//vertical resolution
		numColors= file.getInt32LE;					//number of colors in the color palette
		numImportantColors= file.getInt32LE;			//number of important colors (often 0)
	}
	prReadPalette {|file|
		if(numColors==0, {
			numColors= offset-54/4;
		});
		palette= {
			var b= file.getInt8&0xFF;
			var g= file.getInt8&0xFF;
			var r= file.getInt8&0xFF;
			var a= file.getInt8&0xFF;
			Color.new255(r, g, b);
		}.dup(numColors);
	}
	prReadData {|file|
		data= Array.newClear(width*height);
		switch(depth,
			1, {	//TODO
			},
			4, {
				height.do{|y|
					var cnt= width;
					width.do{|x|
						var i, l, r;
						if(x%2==0, {
							i= file.getInt8&0xFF;
							l= i&0xF0>>4;
							r= i&0x0F;
							data[y*width+x]= palette[l];
							if(x+1<width, {
								data[y*width+x+1]= palette[r];
								cnt= cnt+1;
							});
						});
					};
					while({cnt%4>0}, {				//read padding bytes
						file.getInt8;
						cnt= cnt+1;
					});
				};
			},
			8, {
				height.do{|y|
					var cnt= width;
					width.do{|x|
						var i= file.getInt8&0xFF;
						data[y*width+x]= palette[i];
					};
					while({cnt%4>0}, {				//read padding bytes
						file.getInt8;
						cnt= cnt+1;
					});
				};
			},
			24, {
				height.do{|y|
					width.do{|x|
						var b= file.getInt8&0xFF;
						var g= file.getInt8&0xFF;
						var r= file.getInt8&0xFF;
						data[y*width+x]= Color.new255(r, g, b);
					};
					(width-(width.div(4)*4)).do{	//read padding bytes
						file.getInt8;
					};
				};
			},
			32, {
				height.do{|y|
					width.do{|x|
						var b= file.getInt8&0xFF;
						var g= file.getInt8&0xFF;
						var r= file.getInt8&0xFF;
						var a= file.getInt8&0xFF;
						data[y*width+x]= Color.new255(r, g, b, a);
					};
				};
			},
			{(this.class.name++": bitdepth"+depth+"not yet implemented").error}
		);
		if(fileSize!=file.pos, {
			(this.class.name++": file ending mismatch: fileSize:"+fileSize+"file.pos:"+file.pos).error
		});
	}
	prWrite {|path|
		var file= File(path, "w");
		this.prWriteFileHeader(file);
		this.prWriteInfoHeader(file);
		if(palette.notNil, {
			if(depth<16, {
				this.prWritePalette(file);
			}, {
				(this.class.name++": palette not used because depth >= 16").error
			});
		});
		this.prWriteData(file);
		file.close;
	}
	prWriteFileHeader {|file|
		file.putChar(type[0]);
		file.putChar(type[1]);
		file.putInt32LE(fileSize);
		file.putInt16(0);
		file.putInt16(0);
		file.putInt32LE(offset);
	}
	prWriteInfoHeader {|file|
		file.putInt32LE(headerSize);
		file.putInt32LE(width);
		if(topToBottom, {
			file.putInt32LE(0-height);
		}, {
			file.putInt32LE(height);
		});
		file.putInt16LE(planes);
		file.putInt16LE(depth);
		file.putInt32LE(compression);
		file.putInt32LE(imageSize);
		file.putInt32LE(horizontalResolution);
		file.putInt32LE(verticalResolution);
		file.putInt32LE(numColors);
		file.putInt32LE(numImportantColors);
	}
	prWritePalette {|file|
		//TODO
	}
	prWriteData {|file|
		switch(depth,
			1, {	//TODO
			},
			4, {	//TODO
			},
			8, {	//TODO
			},
			24, {	//TODO
			},
			32, {
				height.do{|y|
					width.do{|x|
						var c= data[y*width+x];
						file.putInt8((c.blue*255).round.asInteger);
						file.putInt8((c.green*255).round.asInteger);
						file.putInt8((c.red*255).round.asInteger);
						file.putInt8((c.alpha*255).round.asInteger);
					};
				};
			},
			{(this.class.name++": bitdepth"+depth+"not yet implemented").error}
		);
	}
}
