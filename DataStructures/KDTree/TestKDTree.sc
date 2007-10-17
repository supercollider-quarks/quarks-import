/*
KDTree.test;

TestKDTree.tree.dumpTree
TestKDTree.tree.highestUniqueId

TestKDTree.tree.nearest([0,1], verbose: true)
TestKDTree.tree2.nearest([7,9], verbose: true).location

TestKDTree.tree.nearest([5,9]).location
~bl = TestKDTree.tree.pr_BestLeafFor([5,9])
~bl = TestKDTree.tree.pr_QuickDescend([5,9])
~bl.location
~bl.leftChild
~bl.rightChild
~bl.isLeftChild
~bl.parent.location



*/
TestKDTree : UnitTest {
	classvar <array, <size, <dims, <tree, <array2, <tree2, <>dumpTrees=false;
	
	setUp {
	}
	tearDown {
	}
	
	// Creates a 3D data structure and tests whether certain searches return the same 
	// results under simple data rotation.
	test_rotate {
		// int
		this.rotationTest(2,50, 10);
		this.rotationTest(3,50, 10);
		// float
		this.rotationTest(2,50, 10.0);
		this.rotationTest(3,50, 10.0);
	}
	
	rotationTest { |dims=3, size=100, randLimit=10|
		var probe, probe2, match1, match2, dist1, dist2;
		
		array = {{randLimit.rand}.dup(dims)}.dup(size);
		
		//"rotationTest array:".postln;
		//array.do(_.postln);
		
		array2 = array.collect(_.rotate(1));
		
		// Data is spatially the same but rotated 90 degrees in each dim.
		// Because of the way KDTree uses dimensions to chop the data,
		// the actual tree structure will be very different,
		// but the results of queries should be the same (after the 
		// rotation is compensated).
		tree  = KDTree(array);
		tree2 = KDTree(array2);
		
		this.assert(tree.size==array.size, "tree.size==array.size");
		this.assert(tree.min==array.flop.collect(_.minItem), "tree.min==array.flop.collect(_.minItem) : % == %".format(tree.min,array.flop.collect(_.minItem)));
		this.assert(tree.max==array.flop.collect(_.maxItem), "tree.max==array.flop.collect(_.maxItem) : % == %".format(tree.max,array.flop.collect(_.maxItem)));
		
		if(this.class.dumpTrees){
			tree.dumpTree;
			tree2.dumpTree;
		};
		
		// Easy test - nearest neighbour to actual node, *without* excluding that node,
		// should be either that node itself or another node with exact same location.
		tree.do{|node|
			probe = node.location;
			# match1, dist1 = tree.nearest(probe);
			this.assert(dist1==0, "tree.nearest(%) dist==0".format(probe, dist1));
			
			probe2 = node.location.rotate(1);
			# match2, dist2 = tree2.nearest(probe2);
			this.assert(dist2==0, "tree2.nearest(%) dist==0".format(probe2, dist2));
			
			// When we use .nearestToNode, we should *not* get ourself back.
			# match1, dist1 = node.nearestToNode;
			this.assert(match1 != node, "node.nearestToNode should not return the self node: %, depth %".format(node.location, node.depth));
		};
		
		
		// Now we create some random spatial points, and do a NN query.
		// If there is a "tie-break" in NN queries then the order of preference
		// is arbitrary, so we have to test not on what the actual NN is, but 
		// by checking its distance from the probe.
		50.do{
			probe = {randLimit.rand}.dup(dims);
			probe2 = probe.rotate(1);
			
			# match1, dist1 = tree.nearest(probe);
			# match2, dist2 = tree2.nearest(probe2);
						
			this.assert(dist1 == dist2, "rotated space check: tree.nearest(%) same distance away as tree2.nearest(%)".format(probe, probe2));
		};
	}
}