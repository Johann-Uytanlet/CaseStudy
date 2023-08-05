import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class TwoWayAccepter extends JFrame{
    private JTextArea outputArea, transitionsArea, givenStringArea;
    private Set<String> stateSet = new HashSet<>();
    private Set<String> inputSet = new HashSet<>();
    private String startState = "";
    private Set<String> endStateSet = new HashSet<>();
    private boolean okayTransitions = false;

    private JPanel inputPanel;
    private JPanel transitionPanel;
    private JPanel outputPanel;
    private Set<transitions> transitionsSet = new HashSet<>();


    public TwoWayAccepter() {
        setTitle("Input and Output Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new GridLayout(1, 2));

        JPanel inputPanel = createInputPanel();
        JPanel transitionPanel = createTransitionPanel();
        JPanel outputPanel = createOutputPanel();

        add(inputPanel);
        add(transitionPanel);
        add(outputPanel);
    }
    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        JLabel stateLabel = new JLabel("States:");
        JTextField stateField = new JTextField();
        panel.add(stateLabel);
        panel.add(stateField);

        JLabel inputLabel = new JLabel("Inputs:");
        JTextField inputField = new JTextField();
        panel.add(inputLabel);
        panel.add(inputField);

        JLabel startStateLabel = new JLabel("Start State:");
        JTextField startStateField = new JTextField();
        panel.add(startStateLabel);
        panel.add(startStateField);

        JLabel finalStateLabel = new JLabel("Final State/s:");
        JTextField finalStateField = new JTextField();
        panel.add(finalStateLabel);
        panel.add(finalStateField);
        JButton fileInputButton = new JButton("Load from File");
        fileInputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    // Check if the file has a ".txt" extension
                    if (selectedFile.getName().endsWith(".txt")) {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(selectedFile);
                            Scanner scanner = new Scanner(fileInputStream);

                            // States
                            int numStates = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline character left by nextInt()

                            String stateNamesInput = scanner.nextLine();
                            String[] stateNames = stateNamesInput.split("\\s+");

                            if (stateNames.length != numStates) {
                                outputArea.append("Error: Number of state names entered does not match the expected number.\n");
                                return;
                            }

                            // if all unique
                            Set<String> stateNamesSet = new HashSet<>();
                            for (String state : stateNames) {
                                if (!stateNamesSet.add(state)) { // check if state is already in the set of not
                                    outputArea.append("Error: All state names must be unique.\n");
                                    return;
                                }
                            }

                            // Inputs
                            int numInputs = scanner.nextInt();
                            scanner.nextLine();

                            String inputsInput = scanner.nextLine();
                            String[] inputs = inputsInput.split("\\s+");

                            if (inputs.length != numInputs) {
                                outputArea.append("Error: Number of inputs entered does not match the expected number.\n");
                                return;
                            }

                            // if all unique and symbol do no contain < or >
                            Set<String> inputsSet = new HashSet<>();
                            for (String input : inputs) {
                                if (!inputsSet.add(input)) {
                                    outputArea.append("Error: All inputs must be unique.\n");
                                    return;
                                }
                            }
                            inputsSet.add("<");
                            inputsSet.add(">");

                            // Transitions
                            int numTransitions = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline character left by nextInt()

                            Set<transitions> transitionsSet = new HashSet<>();
                            // each transition start state, read, move direction, end state
                            for(int i = 0; i<numTransitions; i++){
                                String currTransition = scanner.nextLine();
                                String[] currTransitionFormatted = currTransition.split("\\s+");
                                if(currTransitionFormatted.length != 4){

                                    outputArea.append("Error: Invalid transition format\n");
                                    return;
                                } else if(!stateNamesSet.contains(currTransitionFormatted[0]) || !stateNamesSet.contains(currTransitionFormatted[3])){
                                    outputArea.append("Error: Transition state doesnt exist\n");
                                    return;
                                } else if(!inputsSet.contains(currTransitionFormatted[1])){
                                    outputArea.append("Error: Input state doesnt exist\n");
                                    return;
                                } else if(!currTransitionFormatted[2].equals("+") && !currTransitionFormatted[2].equals("-")){
                                    outputArea.append("Error: Invalid Direction\n");
                                    return;
                                }/*else if((currTransitionFormatted[2].equals("+") && currTransitionFormatted[1].equals("<")) ||
                                        ((currTransitionFormatted[2].equals("-") && currTransitionFormatted[1].equals(">")))) {
                                    System.out.println("Error: cannot go pass end marker");
                                }*/else {
                                    transitions addTransition = new transitions(currTransitionFormatted[0], currTransitionFormatted[1], currTransitionFormatted[2], currTransitionFormatted[3]);

                                    if(transitionsSet.contains(addTransition)){
                                        outputArea.append("Error: Duplicate Transitions\n");
                                        return;
                                    }
                                    transitionsSet.add(addTransition);
                                }
                            }

                            boolean isDFA = true;

                            for (String state : stateNamesSet) {
                                Set<String> seenInputs = new HashSet<>();

                                // check for transitions with curr state as start symbol
                                for (transitions transition : transitionsSet) {
                                    //System.out.println(transition.getStartState() + " " + transition.getInput());
                                    if (transition.getStartState().equals(state)) {
                                        // Check if the input symbol has been seen before
                                        if (seenInputs.contains(transition.getInput())) {
                                            outputArea.append("State has multiple transitions for same input\n");
                                            isDFA = false;
                                            break;
                                        } else {
                                            seenInputs.add(transition.getInput());
                                        }
                                    }
                                }
                                if (!inputsSet.equals(seenInputs)) {
                                    outputArea.append("Not all transitions are defined for state " + state + "\n");
                                    isDFA = false;
                                    break;
                                }
                                // If not a DFA, break the loop
                                if (!isDFA) {
                                    break;
                                }
                            }
                            if(!isDFA){
                                outputArea.append("Machine given is not a DFA\n");
                                return;
                            }

                            // Start State
                            String startState = scanner.nextLine();
                            if(!stateNamesSet.contains(startState)){
                                outputArea.append("Error: Invalid Start State\n");
                            }

                            // End States
                            int numEndStates = scanner.nextInt();
                            scanner.nextLine();

                            String endStateNamesInput = scanner.nextLine();
                            String[] endStateNames = endStateNamesInput.split("\\s+");

                            if (endStateNames.length != numEndStates) {
                                outputArea.append("Error: Number of end state names entered does not match the expected number.\n");
                                return;
                            }

                            // if all unique
                            Set<String> endStateSet = new HashSet<>();
                            for (String state : endStateNames) {
                                if (!stateNamesSet.contains(state) || !endStateSet.add(state)) { // check if state is already in the set or not
                                    outputArea.append("Error: End State not found in set of states given/must be unique.\n");
                                    return;
                                }
                            }
                            boolean loop = true;
                            while(loop){
                                String givenString = scanner.nextLine();

                                processString(transitionsSet, startState,
                                        endStateSet, givenString, outputArea);

                                String loopAgain = scanner.nextLine();
                                if(loopAgain.equalsIgnoreCase("F")){
                                    loop = false;
                                }
                            }
                            scanner.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            outputArea.append("Error reading the selected file\n");
                        }
                    } else {
                        outputArea.append("Selected file is not a .txt file\n");
                    }
                }
            }
        });
        panel.add(fileInputButton);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String states = stateField.getText();
                String inputs = inputField.getText();
                String startStateFromField = startStateField.getText();
                String finalStates = finalStateField.getText();

                if( states.isEmpty() ||
                        inputs.isEmpty() ||
                        startStateFromField.isEmpty() ||
                        finalStates.isEmpty()){
                    outputArea.append("Error: One or more of the entries is empty\n");
                } else {
                    stateSet = new HashSet<>();
                    inputSet = new HashSet<>();
                    startState = "";
                    endStateSet = new HashSet<>();
                    String errorMsg = "";

                    String[] stateNames = states.split("\\s+");

                    for (String state : stateNames) {
                        if (!stateSet.add(state)) {
                            errorMsg += "Error: All state names must be unique.\n";
                            break;
                        }
                    }

                    String[] inputNames = inputs.split("\\s+");

                    for (String input : inputNames){
                        if (!inputSet.add(input)) {
                            errorMsg += "Error: All input names must be unique.\n";
                            break;
                        }
                    }
                    // add end markers if not added
                    inputSet.add(">");
                    inputSet.add("<");

                    String[] startStateNames = startStateFromField.split("\\s+");
                    startState = startStateNames[0];

                    if(!stateSet.contains(startState)){
                        errorMsg += "Error: Start State is not included in the states given\n";
                    }

                    String[] endStateNames = finalStates.split("\\s+");


                    for (String finalState : endStateNames) {
                        if (!stateSet.contains(finalState) || !endStateSet.add(finalState)) {
                            errorMsg += "Error: All end state names be unique and part of the states set.\n";
                            break;
                        }
                    }
                    if(errorMsg.isEmpty()){
                        outputArea.append("OKAY with states\n");
                        transitionsArea.setText("");
                        givenStringArea.setText("");
                        okayTransitions = true;
                    } else {
                        outputArea.append(errorMsg);
                        transitionsArea.setText("");
                        givenStringArea.setText("");
                        okayTransitions = false;
                    }
                }


            }
        });
        panel.add(submitButton);

        return panel;
    }
    private JPanel createTransitionPanel() {
        JPanel transitionPanel = new JPanel();
        transitionPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5,0, 5, 0); // Padding for each component

        // title label
        JLabel titleLabel = new JLabel("Transition Input (startState, input, direction (+/-), endState)");
        titleLabel.setHorizontalAlignment(JLabel.CENTER); // Center the label horizontally
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;

        transitionPanel.add(titleLabel, constraints);
        JLabel title2Label = new JLabel("Example: A a + B");
        title2Label.setHorizontalAlignment(JLabel.CENTER); // Center the label horizontally
        constraints.gridy = 1;
        transitionPanel.add(title2Label, constraints);

        // transitions text area
        transitionsArea = new JTextArea();
        JScrollPane transitionsScrollPane = new JScrollPane(transitionsArea);
        transitionsArea.setRows(10); // Set the number of rows for the text area
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0; // Allow vertical expansion
        transitionPanel.add(transitionsScrollPane, constraints);

        // given string label
        JLabel givenStringLabel = new JLabel("Given String:");
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weighty = 0.0;
        transitionPanel.add(givenStringLabel, constraints);

        // given string input area
        givenStringArea = new JTextArea();
        givenStringArea.setRows(3); // Set the number of rows for the text area
        givenStringArea.setPreferredSize(new Dimension(150, 60)); // Adjust dimensions as needed
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        transitionPanel.add(givenStringArea, constraints);

        // submit button
        JButton submitButton = new JButton("Submit");
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Your submit button logic
                if(okayTransitions){
                    outputArea.append("Starting the Processing\n");
                    String transitions = transitionsArea.getText();
                    String givenString = givenStringArea.getText();
                    String ergMsg = "";
                    if(transitions.isEmpty() || givenString.isEmpty()){
                        ergMsg += "empty transitions/given string\n";
                    }
                    transitionsSet = new HashSet<>();
                    String[] lines = transitions.split("\n");
                    for (String line : lines) {

                        String[] parts = line.split(" ");
                        if (parts.length == 4) {
                            if(!stateSet.contains(parts[0])){
                                ergMsg += "invalid start state for transition " + line + "\n";
                            } else if (!inputSet.contains(parts[1])){
                                ergMsg += "invalid input for transition " + line + "\n";
                            } else if (!(parts[2].equals("+")|| parts[2].equals("-"))){
                                ergMsg += "invalid direction for transition " + line + "\n";
                            } else if (!stateSet.contains(parts[3])){
                                ergMsg += "invalid end state for transition " + line + "\n";
                            }else if(!transitionsSet.add(new transitions(parts[0], parts[1], parts[2], parts[3]))){
                                ergMsg += "Duplicate transition for transition" + line + "\n";
                            }
                        } else {
                            ergMsg += "Invalid transition format for line " + line + "\n";
                        }
                    }
                    outputArea.append("Done with double checking transitions\n");
                    ergMsg += isDFA(transitionsSet, stateSet, inputSet);
                    String[] givenStrings = givenString.split("\\s+");
                    givenString = givenStrings[0];
                    if(givenString.contains(">") || givenString.contains("<")){
                        ergMsg += "Do not enter end markers inside the given string\n";
                    }
                    if(ergMsg.isEmpty()){
                        outputArea.append("PROCESSING\n");
                        processString(transitionsSet,startState,endStateSet,givenString,outputArea);
                    } else {
                        outputArea.append(ergMsg);
                    }
                } else {
                    outputArea.append("The initial states are not okay yet\n");
                }
            }
        });
        transitionPanel.add(submitButton, constraints);

        return transitionPanel;
    }

    private String isDFA(Set<transitions> transitionsSet, Set<String> stateSet, Set<String> inputSet){
        for (String state : stateSet) {
            Set<String> seenInputs = new HashSet<>();
            // check for transitions with curr state as start symbol
            for (transitions transition : transitionsSet) {
                seenInputs.add(transition.getInput());
            }
            if (!inputSet.equals(seenInputs)) {
                return "Not all transitions are defined for state " + state + "\n";
            }
        }
        return "";
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TwoWayAccepter frame = new TwoWayAccepter();
                frame.setVisible(true);
            }
        });
    }

    private static void processString(Set<transitions> transitionsSet, String startState,
                                      Set<String> endStateSet, String givenString, JTextArea outputArea) {
        String givenStringwEndMarkers = ">" + givenString + "<";
        String currState = startState;
        int index = 0;

        // Loop through the given string
        while (index >= 0 && index < givenStringwEndMarkers.length()) {
            // Get current index
            String symbol = Character.toString(givenStringwEndMarkers.charAt(index));
            // Find the transition for curr state and index
            transitions matchedTransition = null;
            for (transitions transition : transitionsSet) {
                if (transition.getStartState().equals(currState) &&
                        transition.getInput().equals(symbol)) {
                    matchedTransition = transition;
                    break;
                }
            }
            // If valid transition
            if (matchedTransition != null) {
                // Update current state
                currState = matchedTransition.getEndState();
                //System.out.println(matchedTransition.toString() + "\n");
                outputArea.append(matchedTransition.toString() + " at index  " + index +"\n");
                // Update index based on direction
                if (matchedTransition.getDirection().equals("+")) {
                    index++;
                } else if (matchedTransition.getDirection().equals("-")) {
                    index--;
                }
            } else {
                outputArea.append("Error: No valid transition found for state " + currState + " and input " + symbol + "\n");
                return;
            }
        }

        // if curr state is a final state
        if (endStateSet.contains(currState)) {
            outputArea.append("Given string: " + givenString +" is accepted.\n");
        } else {
            outputArea.append("Given string: "+ givenString + " is not accepted.\n");
        }
    }
}
