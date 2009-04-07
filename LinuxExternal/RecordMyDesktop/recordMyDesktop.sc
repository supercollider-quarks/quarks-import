RecordMyDesktop{

	classvar <all;

	var <name;
	var <pid;

	*initClass{
		all = IdentityDictionary.new;
	}

	*new{ |name|
		^super.new.init(name);
	}

	init{ |name|
		pid = ("recordmydesktop -use-jack SuperCollider:out_1 SuperCollider:out_2 -v_quality 30 -s_quality 5 -o" + name ++ ".ogv --on-the-fly-encoding").unixCmd;
		all.put( name, this );
	}

	stop{
		("kill"+pid).unixCmd;
		all.removeAt( name );
	}
	
}