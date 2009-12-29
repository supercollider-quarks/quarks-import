//redFrik

RedRLE {
	*encode {|input|
		var out= [], cnt, chr, i= 0;
		while({i<input.size}, {
			cnt= 1;
			chr= input[i];
			while({i+1<input.size and:{input[i+1]==chr}}, {
				cnt= cnt+1;
				i= i+1;
			});
			out= out++cnt++chr.ascii;
			i= i+1;
		});
		^out;
	}
	*decode {|input|
		var out= "", cnt, chr, i= 0;
		while({i<input.size}, {
			cnt= input[i];
			chr= input[i+1].asAscii;
			out= out++chr.dup(cnt).join;
			i= i+2;
		});
		^out;
	}
}
