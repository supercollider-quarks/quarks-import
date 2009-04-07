SWBusNode - analysis of bus data to provide new node data to a SWDataNetwork
common arguments to synth:
out, in, lag, mul, gate

AmpTrackNode - amplitude tracker
atime, rtime

SumBusesNode - sum of all buses (N in, 1 out)

LeakyNode
posSlope1, posSlope2, negSlope

StdDevNode
length (before start)

SumStdDevNode - sum of all stddevs (N in, 1 out)
length

VectorNode - vector from left,right,up,down, etc nodes in a set order

ASRNode - attack, sustain, release
attack, release, curve

ASRMulNode - as above, but multiplies the envelope with running max of input value

InRangeNode - 1 if in range, 0 outside
low, hi

InRangeGateNode - value if in range, 0 outside

SchmidtNode - schmidt trigger
low, hi

SchmidtGateNode - as above * value
low, hi

ToggleFFNode - toggle between 0 and 1

ToggleFFGateNode - as above * value

LagUDNode
lagup, lagdown

SlopeNode
(doesnt work yet)

---------- triggers ---------

SWTrigBusNode
triggers above a certain threshold, otherwise not
(doesnt seem to be working, use InRange instead)


TransitNode
transition to another node within certain time frame
(might not work, may need rewrite with InRange)

MouseGridNode
simulate a grid with the mouse