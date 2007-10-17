/*
kd-tree implementation for SuperCollider, by Dan Stowell (c) 2007
Distributed under the terms of the GPL.
*/
KDTree {

var <depth, <axis, 
	// "location" is array representing the k-dimensional position found at the median
	<location,
	// "label" optional, can be anything
	<label,
	// flag allows for elements to be deleted
	<>notDeleted=true,
	// automatically allocated, used mainly for testing equality. root is binary 1; root.leftChild binary 10; root.rightChild binary 11; etc
	<uniqueid,
	<leftChild, <rightChild, <parent;



*new { |array, depth=0, parent, lastIsLabel = false, uniqueid=1|
	^super.new.init(array, depth, parent, lastIsLabel, uniqueid)
}

init { |array, dep=0, par, lastIsLabel=false, uid=1|
	var sorted, medianPos;
	depth = dep;
	parent = par;
	uniqueid = uid;
	
	axis = depth % (array[0].size - if(lastIsLabel, 1, 0));
	
	// We want to find the median index, but if even-sized data we want to
	//   make sure we find a point, so we don't use the average-of-two-centre-points that .median uses
	medianPos = array.size >> 1;
	
	sorted = array.copy;
	sorted.hoareFind(medianPos, { |a,b| a[axis] < b[axis] });

	location = sorted[medianPos];
	if(lastIsLabel, { label = location.pop });
	
	leftChild  = if(medianPos==0  ,            nil, { KDTree.new(sorted[..medianPos-1], depth+1, this, lastIsLabel, uniqueid << 1) });
	rightChild = if(medianPos==(array.size-1), nil, { KDTree.new(sorted[medianPos+1..], depth+1, this, lastIsLabel, uniqueid << 1 | 1)});
}

nearest { |point, ignoreLabel, nearestSoFar, bestDist=inf|
	var nearest, searchParent, dist, max, min, sibling;
	
	// Descend to the leaf that would be parent of the point if it was in the data.
	// Actually, because the partition may leave exact matches on either side of the partition, we use a modified descent.
	nearest = this.pr_QuickDescend(point);
	
	dist = if(nearest.notDeleted && (ignoreLabel.isNil || (ignoreLabel != nearest.label)), {
		(nearest.location - point).abs.squared.sum.sqrt
	}, {
		inf
	});
	
	// externally-supplied guess may be better - let's check
	if(bestDist<dist){
		dist = bestDist;
		nearest = nearestSoFar;
	};
	
	// Next we descend from the root, examining child nodes only if the cut-line makes it possible 
	// for a point to be closer than the nearest-so-far.
	^nearest.pr_nearest_ascend(point, nearest, dist, stopAtDepth: this.depth)
}

pr_BestLeafFor{ |point|
	// Finds the leaf closest to a certain point, not in Euclidean terms but in terms of the space slicing. Used by add.
	var chosen;
	
	if(this.isLeaf, { ^this });
	
	chosen = if((point[axis] <= location[axis]) && leftChild.isNil.not, {leftChild}, {rightChild});
	^if(chosen.isNil, {
		this
	}, {
		chosen.pr_BestLeafFor(point);
	});
}

pr_QuickDescend{ |point|
	// Finds a quick first guess as to the nearest item. Used by NN search.
	var l, r;
	
	if(this.isLeaf || (this.location==point), { ^this });
	
	if(point[axis] == location[axis] && leftChild.isNil.not && rightChild.isNil.not){
		// We don't know which side to look down (partitioning could have put points on either side), so we must examine both.
		l =  leftChild.pr_QuickDescend(point);
		r = rightChild.pr_QuickDescend(point);
		^if(((l.location-point).abs.squared.sum) < ((r.location-point).abs.squared.sum)){
			l
		}{
			r
		};
	};
	
	
	// We know there is exactly one leaf to investigate
	^if(leftChild.isNil, {
		rightChild
	},{
		if(rightChild.isNil || (point[axis] <= location[axis])){
			leftChild
		}{
			rightChild
		};
	}).pr_QuickDescend(point);
}

// Recursive, and called by pr_nearest_ascend
pr_nearest_descend {|point, ignoreLabel, nearestSoFar, dist|
	var curDist;

	// Check self location, NB leave it squared
	curDist = (location - point).abs.squared.sum;
	if(notDeleted && (ignoreLabel.isNil || (ignoreLabel != this.label)) && (curDist < dist.squared), {
		nearestSoFar = this; 
		dist = curDist.sqrt;
	});
	
	// Descend into children only if logically necessary.

	if(leftChild.isNil.not && ((point[axis]-dist) < location[axis]), {
		# nearestSoFar, dist =  leftChild.pr_nearest_descend(point, ignoreLabel, nearestSoFar, dist);
	});
	if(rightChild.isNil.not && ((point[axis]+dist) > location[axis]), {
		# nearestSoFar, dist = rightChild.pr_nearest_descend(point, ignoreLabel, nearestSoFar, dist);
	});
	
	^[nearestSoFar, dist];
}

// Private recursive method.
// Will first be called on the query node itself; eventually will be called on the root.
// What this does is assumes that we've searched inside the current node and its subtree, 
// and it checks the parent to see if the sibling should be searched.
pr_nearest_ascend { |point, nearestSoFar, bestDist, stopAtDepth=0|
	var cur, curDist;
	
	if(this.depth <= stopAtDepth){
		// collapse out of the recursion
		^[nearestSoFar, bestDist]
	};
	
	// Only if the perp distance from the query point to the division plane
	// is nearer than the best dist so far, is it logically possible for a nearer
	// one to be in the parent's location or the sibling
	if(this.isRightChild){
		if(point[parent.axis] - parent.location[parent.axis] <= bestDist){
			if((curDist=(parent.location - point).abs.squared.sum) < bestDist.squared){
				nearestSoFar = parent;
				bestDist = curDist.sqrt;
			};
			if(parent.leftChild.isNil.not){
				
				// My benchmarks indicate that using .pr_nearest_descend rather than a full .nearest is generally faster
				# cur, curDist = parent.leftChild.pr_nearest_descend(point, nil, nearestSoFar, bestDist);
				//# cur, curDist = parent.leftChild.nearest(point, nil, nearestSoFar: nearestSoFar, bestDist: bestDist);
				if(curDist < bestDist){
					nearestSoFar = cur;
					bestDist = curDist;
				};
				
			};
		};
		
	}{ // is left child:
		if(parent.location[parent.axis] - point[parent.axis] <= bestDist){
			if((curDist=(parent.location - point).abs.squared.sum) < bestDist.squared){
				nearestSoFar = parent;
				bestDist = curDist.sqrt;
			};
			if(parent.rightChild.isNil.not){
				
				// My benchmarks indicate that using .pr_nearest_descend rather than a full .nearest is generally faster
				# cur, curDist = parent.rightChild.pr_nearest_descend(point, nil, nearestSoFar, bestDist);
				//# cur, curDist = parent.rightChild.nearest(point, nil, nearestSoFar: nearestSoFar, bestDist: bestDist);
				if(curDist < bestDist){
					nearestSoFar = cur;
					bestDist = curDist;
				};
				
			};
		};
		
	};
	
	// OK, so we've checked our sibling and parent, pass on up to the parent to do the same
	^parent.pr_nearest_ascend(point, nearestSoFar, bestDist, stopAtDepth: stopAtDepth);
	
}

// Compared against .nearest, this should be faster due to knowledge about where the query node is in the tree.
// Users aren't expected to supply bestSoFar, bestDist values - they're used internally
// (They're fed in when the allNearest algorithm runs, making use of this method)
nearestToNode { |nearestSoFar, bestDist=inf|
	var curr, curDist;
	
	location;
	
	if(leftChild.isNil.not, {
		# curr, curDist = leftChild.nearest(location, nearestSoFar: nearestSoFar, bestDist: bestDist);
		if(curDist < bestDist){
			bestDist = curDist;
			nearestSoFar = curr;
		};
	});
	if(rightChild.isNil.not, {
		# curr, curDist = rightChild.nearest(location, nearestSoFar: nearestSoFar, bestDist: bestDist);
		if(curDist < bestDist){
			bestDist = curDist;
			nearestSoFar = curr;
		};
	});
	
	// Now ascend up the tree, checking if we need to search the sibling subtrees.
	^this.pr_nearest_ascend(location, nearestSoFar, bestDist)
}

allNearest {
	// Runs .nearestToNode for each item, but optimised slightly to re-use data sometimes.
	// Actually it doesn't provide a big speedup, which is a shame. Maybe there are cleverer all-nearest-neighbours algos.

	// dict will map a node's uniqueid to an array containing exactly two elements:
	//      [0] the KDTree object representing its nearest neighbour
	//      [1] the distance measured
	// the "results" array is similar, associating a node with the two-element answer.
	var dict, results, guess, best;
	dict = Array.newClear(this.highestUniqueId + 1); // For numeric indexing, array is much faster than an actual Dictionary
	results = Array.new(this.size);
	
	this.do{|node|
		if((guess = dict[node.uniqueid]).isNil.not){
			if(guess[1]==0){
				best = guess; // can't do better than 0 distance
			}{
				// Ordinary search but with a first guess added in
				best = node.nearestToNode(nearestSoFar: guess[0], bestDist: guess[1]);
			};
		}{
			// Ordinary search
			best = node.nearestToNode;
		};
		
		// Store the definite "actual" nearest neighbour
		dict[node.uniqueid] = best;
		results = results.add(node -> best);
		
		// Also, if the NN doesn't already have anything stored, let's store the reverse as a guess
		if(dict[best[0].uniqueid].isNil){
			dict[best[0].uniqueid] = [node, best[1]];
		};
	};
	^results
}

sibling {
	if(parent.isNil, {^nil});
	// May be nil, even if parent exists
	^if(this == parent.leftChild, { parent.rightChild }, { parent.leftChild });
}

find { |point, incDeleted = false|
	var ret = nil;
	if((notDeleted || incDeleted) && (location == point), {
		^this 
	}, {
		
		if(point[axis] <= location[axis], {
			
			leftChild  !? { ret = leftChild.find(point, incDeleted)  };
			if(ret.isNil.not, { ^ret });
		
		});	
		if(point[axis] >= location[axis], {
			
			rightChild !? { ret = rightChild.find(point, incDeleted) };
			if(ret.isNil.not, { ^ret });
			
		});
		
		^nil
	});
}

add { |point, label|
	var addTo;
	addTo = this.pr_BestLeafFor(point).pr_add(point, label);
}
pr_add{ |point, label|
	if(point[axis] < location[axis], {
		leftChild  = KDTree([point ++ label], depth+1, this, label.isNil.not, uniqueid: uniqueid << 1);
	}, {
		rightChild = KDTree([point ++ label], depth+1, this, label.isNil.not, uniqueid: uniqueid << 1 | 1);
	});
}

delete { |point|
	var res;
	res = this.find(point);
	if(res.isNil.not, {"deleted".postln; res.notDeleted = false});
}
undelete { |point|
	var res;
	res = this.find(point, incDeleted: true);
	if(res.isNil.not, {"undeleted".postln; res.notDeleted = true});
}

recreate {
	^this.class.new(this.asArray(incLabels: true), lastIsLabel: true);
}

// Search within a rectangle (hyperrectangle) area
rectSearch { | lo, hi |
	var points = Array.new;
	if(leftChild.isNil.not && (location[axis] >= lo[axis])){
		points = points ++ leftChild.rectSearch(lo, hi);
	};
	if(rightChild.isNil.not && (location[axis] <= hi[axis])){
		points = points ++ rightChild.rectSearch(lo, hi);
	};
	if(notDeleted 
	     && (location >= lo).indexOf(false).isNil
	     && (location <= hi).indexOf(false).isNil){
		points = points ++ this;
	};
	^points;
}

// Search within a spherical area.
// Currently fairly lazy, using rectSearch and then pruning the results.
// There may be fancier ways to do this.
radiusSearch { |point, radius=1|
	var results;
	results = this.rectSearch(point - radius, point + radius);
	results = results.select({|res| (res.location-point).abs.squared.sum.sqrt <= radius });
	^results;
}

min {
	var min = location;
	leftChild  !? { if(leftChild.notDeleted , { min = min(min, leftChild.min )}) };
	rightChild !? { if(rightChild.notDeleted, { min = min(min, rightChild.min)}) };
	^min;
}
max {
	var max = location;
	leftChild  !? { if(leftChild.notDeleted , { max = max(max, leftChild.max )}) };
	rightChild !? { if(rightChild.notDeleted, { max = max(max, rightChild.max)}) };
	^max;
}

do { |func, incDeleted=false|
	leftChild  !? {  leftChild.do(func, incDeleted) };
	rightChild !? { rightChild.do(func, incDeleted) };
	// DEPTH-FIRST iteration - important for .allNearest
	if(notDeleted || incDeleted, {
		func.value(this);
	});
}

// Users should not supply arraySoFar
collect { |func, incDeleted=false, arraySoFar|
	if(arraySoFar.isNil, {arraySoFar = Array.new(this.size)});
	
	if(notDeleted || incDeleted, {
		arraySoFar = arraySoFar.add(func.value(this));
	});
	leftChild  !? {  leftChild.collect(func, incDeleted, arraySoFar) };
	rightChild !? { rightChild.collect(func, incDeleted, arraySoFar) };
	^arraySoFar
}

// Users should not supply an argument "arr".
// For efficiency this is used to initialise an array of the appropriate size and pass that around the tree.
asArray { |incLabels=false, arr|
	arr = arr ?? Array.new(this.size);
	if(notDeleted, {arr = arr.add(if(incLabels, {location ++ [label]}, {location});)});
	if(leftChild.isNil.not,  { arr = leftChild.asArray( incLabels, arr) });
	if(rightChild.isNil.not, { arr = rightChild.asArray(incLabels, arr) });
	^arr;
}

dumpTree { |maxDepth=inf|
	("  ".dup(depth).flat.as(String)  ++ if(depth!=0, {if(this.isLeftChild, {"l"}, {"r"})}, {""}) ++ location 
			+ " (id" + uniqueid++"):" + label 
			+ if(notDeleted.not, {"---DELETED"}, {""})).postln;	if(depth < maxDepth){
		leftChild  !? {leftChild.dumpTree(maxDepth)};
		rightChild !? {rightChild.dumpTree(maxDepth)};
	};
}

isRoot {
	^parent.isNil
}
isLeftChild {
	^parent.leftChild==this
}
isRightChild {
	^parent.rightChild==this
}
isLeaf {
	^leftChild.isNil && rightChild.isNil
}

size { |incDeleted = false|
	^ if(notDeleted || incDeleted, 1, 0) 
		+ if(leftChild.isNil , 0, {leftChild.size }) 
		+ if(rightChild.isNil, 0, {rightChild.size});
}

highestUniqueId {
	var val;
	val = uniqueid;
	leftChild  !? { val = max(val,  leftChild.highestUniqueId)};
	rightChild !? { val = max(val, rightChild.highestUniqueId)};
	^val;
}

== { |that|
	^	
		   // Within tree, uniqueid is sufficient. 
		   (this.uniqueid == that.uniqueid)
		   // Between trees, we're not sure so we should check other things
		   // Note: put the easiest checks first! boolean, integer - push location and label checks later
		&& (this.notDeleted == that.notDeleted)
		&& (this.depth      == that.depth) 
		&& (this.location   == that.location) 
		&& (this.label      == that.label)
}

} // End class
