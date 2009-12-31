//redFrik

RedLZSS {
	classvar <>window= 4096, <>length= 16, <>pad;
	
	*compress {|input|
		var out= "", i= 0, match, len, j, sub, win;
		var bitsWin= window.log2.ceil.asInteger;
		var bitsLen= length.log2.ceil.asInteger;
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
				out= out++0++sub[0].asBinaryString(8);
				i= i+1;
			}, {
				len= sub.size;
				if(len>2, {
					out= out++1++(i.min(window-1)-match-1).asBinaryString(bitsWin)++(len-2).asBinaryString(bitsLen);
					if(i+len<input.size, {
						out= out++input[i+len].asBinaryString(8);
					});
					i= i+len+1;
				}, {
					sub.do{|x|
						out= out++0++x.asBinaryString(8);
						i= i+1;
					};
				});
			});
		});
		^out;
	}
	*decompress {|input|
		var out= [], i= 0, match, len;
		var bitsWin= window.log2.ceil.asInteger;
		var bitsLen= length.log2.ceil.asInteger;
		while({i<input.size}, {
			if(input[i].digit==0, {
				out= out++("2r"++input.copyRange(i+1, i+8)).interpret;
				i= i+9;
			}, {
				i= i+1;
				match= ("2r"++input.copyRange(i, i+bitsWin-1)).interpret;
				i= i+bitsWin;
				len= ("2r"++input.copyRange(i, i+bitsLen-1)).interpret+2;
				i= i+bitsLen;
				match= out.size-match-1;
				out= out++out.copyRange(match, match+len-1);
				if(i<input.size, {
					out= out++("2r"++input.copyRange(i, i+7)).interpret;
				});
				i= i+8;
			});
		});
		^out;
	}
	*binaryStringToBytes {|str|
		pad= 0;
		^str.clump(8).collect{|x|
			while({x.size<8}, {
				x= x++0;
				pad= pad+1;
			});
			("2r"++x).interpret;
		};
	}
	*bytesToBinaryString {|arr|
		var str= arr.collect{|x|
			x.asBinaryString(8);
		}.join;
		^str.copyRange(0, str.size-1-pad);
	}
}
