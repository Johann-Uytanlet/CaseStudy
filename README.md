# CaseStudy
STALCGM project for two way accepters <br>
Input Style for files <br>
Number of states: 2 <br>
State Names: A B (put spaces between each name) <br>
Number of Inputs: 2 <br>
Inputs: q r (Adding end markers > < are optional) <br>
(if not part if the inputs they will be added automatically) <br>
Number of Transitions: 8 <br>
Transitions: Follow the format(currentState input direction transitionedState) <br>
(double check if it is a DFA also include <br>
< and > as part of the input symbols set) <br>
A q + A <br>
A r + B <br>
A < + A <br> 
A > + B <br> 
B q + A <br>
B r + B <br>
B > + A <br>
B < + B <br>
Start State: A <br>
Number of End States: 1 <br>
End States: B <br>
give String: qrqrqrq <br>
T/F (T: give another string, F: stop) <br>
ex. <br>

qrqrqrq <br>
T <br>
rqrqrqrqqrq <br>
T <br>
qerqqrqrw <br>
F <br>
