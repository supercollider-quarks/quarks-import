SCJConnection { classvar <>alsadef, <>scdef, <>prepend, <allports, <connections, <properties;
	var <srcchan, <deschan, <source, <destination;
	classvar <>verbose = false;
	
	*initClass{
		alsadef = ["system:","playback_","capture_"];
		scdef = ["SuperCollider:","in_","out_"];
		prepend = "";
		this.getallports;
		this.getproperties;
		this.getconnections;
	}

	*getconnections{
		var pp, line1, line2;
		connections = ();
		pp = Pipe.new(prepend++"jack_lsp -c", "r");
		line1 = pp.getLine;							
		while({line1.notNil}, {
			line2 = pp.getLine;
			if ( line2.notNil,
				{
					if ( line2.containsStringAt(0, "   "),
						{
							connections.put( line1.asSymbol, line2.copyToEnd(3).asSymbol );
							line1 = pp.getLine; 
						},
						{
							line1 = line2;
						});
				},
				{ line1 = line2 });
		});	
		pp.close;
		^connections;
	}

	*getproperties{
	var pp,line1, line2, prop;
		properties = ();
		pp = Pipe.new(prepend++"jack_lsp -p", "r");
		line1 = pp.getLine;							
			while({line1.notNil}, {
			line2 = pp.getLine;
			if ( line2.notNil,
				{
					if ( line2.contains("properties"),
						{
							prop = line2.split($:).at(1).split($,);
							prop[0] = prop[0].copyToEnd(1);
							prop = prop.keep( prop.size -1 );
							properties.put( line1.asSymbol, prop );
							line1 = pp.getLine; 
						},
						{
							line1 = line2;
						});
				},
				{ line1 = line2 });
		});	
		pp.close;
		^properties;
	}

	*getallports{
		var pp,line,cnt;
		cnt = 0;
		allports = ();
		pp = Pipe.new(prepend++"jack_lsp", "r");
		line = pp.getLine;							
		while({line.notNil}, {
			allports.put( cnt, line.asSymbol );
			line = pp.getLine; 
			cnt = cnt + 1;
		});	
		pp.close;
		^allports;
	}

	*connect{ |srcch, desch, src, des|
		var command;
		if ( src.isNil or: des.isNil,
			{ 
				Task({ 
					srcch.do{ |it,i|
						command = prepend++"jack_connect" + allports.at(it) + allports.at( desch[i] );
						if ( verbose ){ command.postln; };
						command.unixCmd;
						0.2.wait;
					};
					this.getconnections;
				}).play;
			},
			{
				if ( src == \alsa, { src=alsadef[0]++alsadef[1] });
				if ( src == \sc, { src=scdef[0]++scdef[1] });
				if ( des == \alsa, { des=alsadef[0]++alsadef[2] });
				if ( des == \sc, { des=scdef[0]++scdef[2] });
				Task({ srcch.do{ |it,i|
					command = prepend++"jack_connect" + src++it + des++desch[i];
					if ( verbose ){ command.postln; };
					command.unixCmd;
					0.2.wait;};
					this.getconnections;
				}).play;
			});
	}
	
	*disconnect{ |srcch, desch, src, des|
		var command;
		if ( src.isNil or: des.isNil,
			{ 
				Task({ srcch.do{ |it,i|
					command = prepend++"jack_disconnect" + allports.at(it) + allports.at( desch[i] );
					command.unixCmd;
					if ( verbose ){ command.postln; };
					0.2.wait;};
					this.getconnections;
				}).play;
			},
			{
				if ( src == \alsa, { src=alsadef[0]++alsadef[1] });
				if ( src == \sc, { src=scdef[0]++scdef[1] });
				if ( des == \alsa, { des=alsadef[0]++alsadef[2] });
				if ( des == \sc, { des=scdef[0]++scdef[2] });
				Task({srcch.do{ |it,i|
					command = prepend++"jack_disconnect" + src++it + des++desch[i];
					command.unixCmd;
					if ( verbose ){ command.postln; };
					0.2.wait;};
					this.getconnections;
				}).play;
			});
	}

	*new { arg srcch, desch, src, des;
		^super.new.init(srcch, desch, src, des);
	}

	init { arg srcch, desch, src, des;
		source = src;
		destination = des;
		srcchan = srcch;
		deschan = desch;
	}

	connect{
		SCJConnection.connect( srcchan, deschan, source, destination );
	}

	disconnect{
		SCJConnection.disconnect( srcchan, deschan, source, destination );
	}
}