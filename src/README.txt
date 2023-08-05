Input Style for files
Number of states: 2
State Names: A B (put spaces between each name)
Number of Inputs: 2
Inputs: q r (Adding end markers > < are optional)
(if not part if the inputs they will be added automatically)
Number of Transitions: 8
Transitions: Follow the format(currentState input direction transitionedState)
(double check if it is a DFA also include
< and > as part of the input symbols set)
A q + A
A r + B
A < + A
A > + B
B q + A
B r + B
B > + A
B < + B
Start State: A
Number of End States: 1
End States: B
give String: qrqrqrq
T/F (T: give another string, F: stop)
ex.

qrqrqrq
T
rqrqrqrqqrq
T
qerqqrqrw
F
