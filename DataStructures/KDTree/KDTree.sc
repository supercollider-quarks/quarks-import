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

nearest { |point, nearestSoFar, bestDist=inf, incExact=true|
	^this.kNearest(point, 1, nearestSoFar, bestDist, incExact)
}
kNearest { |point, k, nearestSoFar, bestDist=inf, incExact=true|
	var quickGuess, searchParent, quickGuessDist, max, min, sibling;

	
	// Descend to the leaf that would be parent of the point if it was in the data.
	// Actually, because the partition may leave exact matches on either side of the partition, we use a modified descent.
	quickGuess = this.pr_QuickDescend(point, incExact);
	
	quickGuessDist = if(quickGuess.notDeleted, {
		(quickGuess.location - point).sum{|x| x * x}.sqrt
	}, {
		inf
	});
	if(incExact.not and:{quickGuessDist==0}){
		quickGuessDist = inf; // Needs to be done after the distance calc, NOT with equality test
	};
	
	// externally-supplied guess may be better - let's check
	# nearestSoFar, bestDist = this.pr_updateNearest(quickGuess, quickGuessDist, nearestSoFar, bestDist, incExact);
	
	// Next we ascend back up from the QUICK GUESS (NOT from the best so far), examining other branches only if the cut-line makes it possible 
	// for a point to be closer than the nearest-so-far.
	^quickGuess.pr_nearest_ascend(point, nearestSoFar, bestDist, this.depth, incExact)
}

// Checks to see if the "item" at distance "dist" is better than the bestItem at bestDist, and returns the winner.
// This is intended in future to use a *list* of bestItems, to enable kNN.
// Returns [newBestItem, newBestDist]
pr_updateNearest { |item, dist, bestItem, bestDist, incExact=true|
	^if((incExact or: {dist != 0}) and: {dist < bestDist}){
		[item, dist]
	}{
		[bestItem, bestDist]
	}
}
// often efficient to avoid calculating the sqrt
pr_updateNearestSq { |item, distSq, bestItem, bestDist, incExact=true|
	^if((incExact or: {distSq != 0}) and: {distSq < (bestDist*bestDist)}){
		[item, distSq.sqrt]
	}{
		[bestItem, bestDist]
	}
}

pr_BestLeafFor{ |point|
	// Finds the leaf closest to a certain point, not in Euclidean terms but in terms of the space slicing. Used by add.
	var chosen;
	
	if(this.isLeaf, { ^this });
	
	chosen = if((point[axis] <= location[axis]) and:{leftChild.notNil}, {leftChild}, {rightChild});
	^if(chosen.isNil, {
		this
	}, {
		chosen.pr_BestLeafFor(point);
	});
}

pr_QuickDescend{ |point, incExact=true|
	// Finds a quick first guess as to the nearest item. Used by NN search.
	var l, r;
	
	if(this.isLeaf or:{incExact and:{this.location==point}}, { ^this });
	
	if(point[axis] == location[axis] and:{leftChild.notNil and: {rightChild.notNil}}){
		// We don't know which side to look down (partitioning could have put points on either side), so we must examine both.
		l =  leftChild.pr_QuickDescend(point);
		r = rightChild.pr_QuickDescend(point);
		^if(((l.location-point).sum{|x| x * x}) < ((r.location-point).sum{|x| x * x})){
			l
		}{
			r
		};
	};
	
	// We know there is exactly one leaf to investigate
	^if(leftChild.isNil, {
		rightChild
	},{
		if(rightChild.isNil or:{point[axis] <= location[axis]}){
			leftChild
		}{
			rightChild
		};
	}).pr_QuickDescend(point, incExact);
}

// Recursive, and called by pr_nearest_ascend.
// Note: pr_allnearest_descend is similar, make sure code updates are done in parallel
pr_nearest_descend {|point, nearestSoFar, dist, incExact=true|
	var curDist;

	// Check self location, NB leave it squared
	curDist = (location - point).sum{|x| x * x};
	# nearestSoFar, dist = this.pr_updateNearestSq(this, curDist, nearestSoFar, dist, incExact);
	
	// Descend into children only if logically necessary.

	if(leftChild.notNil and:{(point[axis]-dist) < location[axis]}, {
		# nearestSoFar, dist =  leftChild.pr_nearest_descend(point, nearestSoFar, dist, incExact);
	});
	if(rightChild.notNil and:{(point[axis]+dist) > location[axis]}, {
		# nearestSoFar, dist = rightChild.pr_nearest_descend(point, nearestSoFar, dist, incExact);
	});

	^[nearestSoFar, dist];
}

// Private recursive method.
// Will first be called on the query node itself; eventually will be called on the root.
// What this does is assumes that we've searched inside the current node and its subtree, 
// and it checks the parent to see if the sibling should be searched.
// Note: pr_allnearest_ascend is similar, make sure code updates are done in parallel
pr_nearest_ascend { |point, nearestSoFar, bestDist, stopAtDepth=0, incExact=true|
	var cur, curDist;
	
	if(this.depth <= stopAtDepth){
		// collapse out of the recursion
		^[nearestSoFar, bestDist]
	};
	
	// Only if the perp distance from the query point to the division plane
	// is nearer than the best dist so far, is it logically possible for a nearer
	// one to be in the parent's location or the sibling
	if(this.isRightChild){
		if(point[parent.axis] - parent.location[parent.axis] < bestDist){
			curDist=(parent.location - point).sum{|x| x * x};
			# nearestSoFar, bestDist = this.pr_updateNearestSq(parent, curDist, nearestSoFar, bestDist, incExact);
			if(parent.leftChild.notNil){
				
				// My benchmarks indicate that using .pr_nearest_descend rather than a full .nearest is generally faster
				# cur, curDist = parent.leftChild.pr_nearest_descend(point, nearestSoFar, bestDist, incExact);
				# nearestSoFar, bestDist = this.pr_updateNearest(cur, curDist, nearestSoFar, bestDist, incExact);
				
			};
		};
		
	}{ // is left child:
		if(parent.location[parent.axis] - point[parent.axis] < bestDist){
			curDist=(parent.location - point).sum{|x| x * x};
			# nearestSoFar, bestDist = this.pr_updateNearestSq(parent, curDist, nearestSoFar, bestDist, incExact);
			if(parent.rightChild.notNil){
				
				// My benchmarks indicate that using .pr_nearest_descend rather than a full .nearest is generally faster
				# cur, curDist = parent.rightChild.pr_nearest_descend(point, nearestSoFar, bestDist, incExact);
				# nearestSoFar, bestDist = this.pr_updateNearest(cur, curDist, nearestSoFar, bestDist, incExact);
				
			};
		};
		
	};
	
	// OK, so we've checked our sibling and parent, pass on up to the parent to do the same
	^parent.pr_nearest_ascend(point, nearestSoFar, bestDist, stopAtDepth, incExact);
	
}

// Compared against .nearest, this should be faster due to knowledge about where the query node is in the tree.
// Users aren't expected to supply bestSoFar, bestDist values - they're used internally
// (They're fed in when the allNearest algorithm runs, making use of this method)
nearestToNode { |nearestSoFar, bestDist=inf, incExact=true|
	^this.kNearestToNode(1, nearestSoFar, bestDist, incExact)
}
kNearestToNode { |k, nearestSoFar, bestDist=inf, incExact=true|
	var curr, curDist;
	
	location;
	
	if(leftChild.notNil, {
		# curr, curDist = leftChild.kNearest(location, k, nearestSoFar, bestDist, incExact);
		# nearestSoFar, bestDist = this.pr_updateNearest(curr, curDist, nearestSoFar, bestDist, incExact);
	});
	if(rightChild.notNil, {
		# curr, curDist = rightChild.kNearest(location, k, nearestSoFar, bestDist, incExact);
		# nearestSoFar, bestDist = this.pr_updateNearest(curr, curDist, nearestSoFar, bestDist, incExact);
	});

	// Now ascend up the tree, checking if we need to search the sibling subtrees.
	^this.pr_nearest_ascend(location, nearestSoFar, bestDist, 0, incExact)
}

// You can speed this up by passing a bestDist value beyond which you don't want to search,
//  which may skip some values by accident and make the search slightly approximate
allNearest { |bestDist=inf, incExact=true|
	// My optimised methods are not faster :(
	// I wonder if there are methods that are genuinely better than:
	^this.collect({|n| n -> n.nearestToNode(nil, bestDist, incExact)});
}

sibling {
	if(parent.isNil, {^nil});
	// May be nil, even if parent exists
	^if(this.isLeftChild, { parent.rightChild }, { parent.leftChild });
}

find { |point, incDeleted = false|
	var ret = nil;
	if((notDeleted or:{incDeleted}) and:{location == point}, {
		^this 
	}, {
		
		if(point[axis] <= location[axis], {
			leftChild  !? {
				ret = leftChild.find(point, incDeleted);
				ret !? { ^ret };
			};
		});	
		if(point[axis] >= location[axis], {
			rightChild !? {
				ret = rightChild.find(point, incDeleted);
				ret !? { ^ret };
			};
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
		leftChild  = KDTree([point ++ label], depth+1, this, label.notNil, uniqueid << 1);
	}, {
		rightChild = KDTree([point ++ label], depth+1, this, label.notNil, uniqueid << 1 | 1);
	});
}

delete { |point|
	var res;
	res = this.find(point);
	if(res.notNil, {"deleted".postln; res.notDeleted = false});
}
undelete { |point|
	var res;
	res = this.find(point, true);
	if(res.notNil, {"undeleted".postln; res.notDeleted = true});
}

recreate {
	^this.class.new(this.asArray(true), lastIsLabel: true);
}

// Search within a rectangle (hyperrectangle) area
rectSearch { | lo, hi |
	var points = Array.new;
	if(leftChild.notNil and:{location[axis] >= lo[axis]}){
		points = points ++ leftChild.rectSearch(lo, hi);
	};
	if(rightChild.notNil and:{location[axis] <= hi[axis]}){
		points = points ++ rightChild.rectSearch(lo, hi);
	};
	if(notDeleted 
	     and: {(location >= lo).indexOf(false).isNil}
	     and: {(location <= hi).indexOf(false).isNil}){
		points = points ++ this;
	};
	^points;
}

// Search within a spherical area.
// Currently fairly lazy, using rectSearch and then pruning the results.
// There may be fancier ways to do this.
radiusSearch { |point, radius=1|
	var results, rsq;
	results = this.rectSearch(point - radius, point + radius);
	rsq = radius * radius;
	results = results.select({|res| (res.location-point).sum{|x| x * x} <= rsq });
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
	if(notDeleted or:{incDeleted}, {
		func.value(this);
	});
}

// Users should not supply arraySoFar
collect { |func, incDeleted=false, arraySoFar|
	if(arraySoFar.isNil, {arraySoFar = Array.new(this.size)});
	
	leftChild  !? {  leftChild.collect(func, incDeleted, arraySoFar) };
	rightChild !? { rightChild.collect(func, incDeleted, arraySoFar) };
	if(notDeleted or:{incDeleted}, {
		arraySoFar = arraySoFar.add(func.value(this));
	});
	^arraySoFar
}

// Users should not supply an argument "arr".
// For efficiency this is used to initialise an array of the appropriate size and pass that around the tree.
asArray { |incLabels=false, arr|
	arr = arr ?? Array.new(this.size);
	if(notDeleted, {arr = arr.add(if(incLabels, {location ++ [label]}, {location});)});
	if(leftChild.notNil,  { arr = leftChild.asArray( incLabels, arr) });
	if(rightChild.notNil, { arr = rightChild.asArray(incLabels, arr) });
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
	// ^parent.isNil
	 ^uniqueid==1 //faster
}
isLeftChild {
	// ^parent.leftChild==this
	 ^   (uniqueid != 1) and:{uniqueid & 1 == 0} //faster
}
isRightChild {
	// ^parent.rightChild==this
	^   (uniqueid != 1) and:{uniqueid & 1 == 1} //faster
}
isLeaf {
	^leftChild.isNil and: {rightChild.isNil}
}

size { |incDeleted = false|
	^ if(notDeleted or:{incDeleted}, 1, 0) 
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
		and:{this.notDeleted == that.notDeleted}
		and:{this.depth      == that.depth}
		and:{this.location   == that.location}
		and:{this.label      == that.label}
}

} // End class
