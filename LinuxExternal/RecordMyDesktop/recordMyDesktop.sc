RecordMyDesktop{

	classvar <all;

	var <name;
	var <pid;

	*initClass{
		all = IdentityDictionary.new;
	}

	*new{ |name,v=30,s=5|
		^super.new.init(name,v,s);
	}

	init{ |name,v=30,s=5|
		name = name ? ("RecordMyDesktop"++Date.localtime.stamp);
		pid = ("recordmydesktop -use-jack SuperCollider:out_1 SuperCollider:out_2 -v_quality"+v+"-s_quality"+s+"-o" + name ++ ".ogv --on-the-fly-encoding").unixCmd;
		all.put( name, this );
	}

	stop{
		("kill"+pid).unixCmd;
		all.removeAt( name );
	}
	
}