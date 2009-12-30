//redFrik

RedLZ77 {
	classvar <>window= 4096, <>length= 16;
	
	*compress {|input|
		var out= [], i= 0, match, len, j, sub, win;
		while({i<input.size}, {
			match= nil;
			j= length-1;
			while({match.isNil and:{j>=0}}, {
				sub= input.copyRange(i, i+j);
				j= j.min(sub.size-1);
				win= input.copyRange((i-window+1).max(0), i-1);
				match= win.find(sub);
				j= j-1;
			});
			if(match.isNil, {
				out= out++[0, 0, sub[0]];
				i= i+1;
			}, {
				len= sub.size;
				out= out++[i.min(window-1)-match-1, len];
				if(i+len<input.size, {
					out= out++input[i+len];
				});
				i= i+len+1;
			});
		});
		^out;
	}
	*decompress {|input|
		var out= [], i= 0, match, len;
		while({i<input.size}, {
			len= input[i+1];
			if(len==0, {
				out= out++input[i+2];
			}, {
				match= out.size-input[i]-1;
				out= out++out.copyRange(match, match+len-1);
				if(i+2<input.size, {
					out= out++input[i+2];
				});
			});
			i= i+3;
		});
		^out;
	}
}
