//redFrik

//--todo:
//file read/write

RedHuffman {
	classvar <>tree, <>dict;
	
	*encode {|str|
		var out;
		
		//--build a forest of small trees
		tree= [];
		str.do{|chr|
			var n= tree.detect{|x| x.key==chr};
			if(n.isNil, {
				tree= tree.add((chr -> 1));			//char and counter association
			}, {
				n.value= n.value+1;				//increase counter
			});
		};
		
		//--collect greedy as a single big tree
		while({tree.size>2}, {
			var n0= this.prTakeMinimum;
			var n1= this.prTakeMinimum;
			tree= tree.add(([n0, n1] -> (n0.value+n1.value)));
		});
		
		//--remove counters from tree
		tree= this.prRebuildTree(tree);
		
		//--build dictionary
		dict= ();
		this.prBuildDict(tree, "");
		
		//--create binary string
		out= "";
		str.do{|chr|
			out= out++dict[chr];
		};
		^out;
	}
	*decode {|str|
		var out= "", tmp= tree;
		str.do{|x|
			tmp= tmp[x.digit];
			if(tmp.isArray.not, {
				out= out++tmp;
				tmp= tree;
			});
		};
		^out;
	}
	
	//--private
	*prTakeMinimum {
		var ii, nn, min= 2147483647;
		tree.do{|x, i|
			if(x.value<min, {
				nn= x;
				ii= i;
				min= x.value;
			});
		};
		tree.removeAt(ii);
		^nn;
	}
	*prRebuildTree {|arr|
		^arr.collect{|x|
			if(x.key.isArray, {
				this.prRebuildTree(x.key);
			}, {
				x.key;
			});
		};
	}
	*prBuildDict {|arr, str|
		arr.do{|x, i|
			if(x.isArray, {
				this.prBuildDict(x, str++i);
			}, {
				dict.put(x, str++i);
			});
		};
	}
}
