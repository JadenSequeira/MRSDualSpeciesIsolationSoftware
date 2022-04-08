package Tests;

import DualSpeciesIsolation.DualMRSWaveformStatistics;
import DualSpeciesIsolation.PulseGenerator;
import DualSpeciesIsolation.WaveGrapher;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//TODO: Fix Dual Species exit()
//TODO: Add Loading Bar

public class DualSpeciesMRSSim extends SwingWorker {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton saveButton;
    private JTextField textField1;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JCheckBox normOnTimesOnlyCheckBox;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JTextField textField9;
    private JTextField textField10;
    private JTextField textField11;
    private JTextField textField2;
    private JButton calculateButton;
    private JProgressBar progressBar2;
    private JButton cancelButton;
    private JButton generateButton;
    private JTextField textField12;
    private JTextField textField13;
    private JTextField textField14;
    private JTextField textField15;
    private JTextField textField16;
    private JTextField textField17;
    private JTextField textField18;
    private JTextField textField19;
    private JButton generateButton1;
    private JTextField textField20;
    private JTextField textField21;
    private JTextField textField22;
    private JTextField textField23;
    private JButton generateButton2;

    public DualSpeciesMRSSim() {
        //setContentPane(panel1);
        //setModal(true);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == saveButton){

                    //task = new Task();
                    //task.addPropertyChangeListener(this);
                    //task.execute();

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Specify a file to save");

                    int selection = fileChooser.showSaveDialog(new JFrame());

                    if (selection == JFileChooser.APPROVE_OPTION) {
                        File fileSave = fileChooser.getSelectedFile();
                        System.out.println("Save as: " + fileSave.getAbsolutePath());
                        String filePath = fileSave.getAbsolutePath();

                        try {
                            int minMass = Integer.parseInt(textField1.getText());
                            int maxMass = Integer.parseInt(textField3.getText());
                            int window = Integer.parseInt(textField4.getText());

                            double proportion = Double.parseDouble(textField6.getText());
                            double MRSCycles = Double.parseDouble(textField7.getText());
                            int adjBreak = Integer.parseInt(textField5.getText());
                            if (maxMass-minMass >= window && minMass > 0 && window > 0 && MRSCycles > 0 && proportion > 0 && proportion <= 1 && adjBreak > 0) {
                                Boolean normOnly = false;

                                if (normOnTimesOnlyCheckBox.isSelected()) {
                                    normOnly = true;
                                }

                                try {

                                    FileWriter writerA = new FileWriter(filePath + ".txt");
                                    DualMRSWaveformStatistics
                                        .DualMRSMassScanner(window, minMass, maxMass, MRSCycles,
                                            proportion, adjBreak, normOnly, writerA);
                                    progressBar2.setIndeterminate(true); //TODO: Implement progress
                                    cancelButton.addActionListener(new ActionListener(){

                                        @Override
                                        public void actionPerformed(ActionEvent g) {
                                            if (g.getSource() == cancelButton){
                                                //TODO: Implement stop if cancel or new save pressed
                                            }
                                        }
                                    });

                                } catch (IOException g) {
                                    g.printStackTrace();
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(null,"Please ensure that the window is smaller or equal to the difference between max and min. Also ensure that all numbers are greater than 0 and the proportion is a decimal between 0 and 1.");
                            }

                        } catch (NumberFormatException f){
                            f.printStackTrace();
                            JOptionPane.showMessageDialog(null,"Please enter numbers only.");
                        }
                    }
                }
            }
        });
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == calculateButton){

                    try {

                        double Mass1 = Double.parseDouble(textField8.getText());
                        double Mass2 = Double.parseDouble(textField10.getText());
                        double proportion = Double.parseDouble(textField11.getText());
                        double MRSCycles = Double.parseDouble(textField9.getText());

                        if (Mass1 > 0 && Mass2 > 0 && proportion <=1 && proportion >= 0 && MRSCycles > 0) {
                            textField2.setText((String) (
                                WaveGrapher.getNormOnTime(Mass1, Mass2, MRSCycles, proportion) +
                                    "  "));
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"Please ensure that all numbers are greater than 0 and the proportion is a decimal between 0 and 1.");
                        }

                    } catch (NumberFormatException f){
                        f.printStackTrace();
                        JOptionPane.showMessageDialog(null,"Please enter numbers only.");
                    }

                }
            }
        });
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == generateButton){

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Specify a file to save");

                    int selection = fileChooser.showSaveDialog(new JFrame());

                    if (selection == JFileChooser.APPROVE_OPTION) {
                        File fileSave = fileChooser.getSelectedFile();
                        System.out.println("Save as: " + fileSave.getAbsolutePath());
                        String filePath = fileSave.getAbsolutePath();


                     try{
                        int Mass1 = Integer.parseInt(textField12.getText());
                        int Mass2 = Integer.parseInt(textField15.getText());
                        double proportion = Double.parseDouble(textField14.getText());
                        double MRSCycles = Double.parseDouble(textField13.getText());

                        int heavyMass;

                        if (Mass1 > Mass2){
                            heavyMass = Mass1;
                        }
                        else{
                            heavyMass = Mass2;
                        }

                         if (Mass1 > 0 && Mass2 > 0 && proportion <=1 && proportion >= 0 && MRSCycles > 0) {
                             try {
                                 FileWriter writerA = new FileWriter(filePath+ ".txt");


                                 WaveGrapher.checkAdjLengths(Mass1, Mass2, PulseGenerator
                                         .getSuggestedTimeScale(heavyMass, MRSCycles, proportion),
                                     PulseGenerator
                                         .getSuggestedTimeScale(heavyMass, MRSCycles, proportion),
                                     MRSCycles, proportion, writerA);
                             } catch (IOException p) {
                                 p.printStackTrace();
                             }
                         }

                        else{
                            JOptionPane.showMessageDialog(null,"Please ensure that all numbers are greater than 0 and the proportion is a decimal between 0 and 1.");
                        }

                    } catch (NumberFormatException f){
                        f.printStackTrace();
                        JOptionPane.showMessageDialog(null,"Please enter numbers only.");
                    }

                    }
                }

            }
        });
        generateButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == generateButton1){

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Specify a file to save");

                    int selection = fileChooser.showSaveDialog(new JFrame());

                    if (selection == JFileChooser.APPROVE_OPTION) {
                        File fileSave = fileChooser.getSelectedFile();
                        System.out.println("Save as: " + fileSave.getAbsolutePath());
                        String filePath = fileSave.getAbsolutePath();


                        try{
                            int Mass1 = Integer.parseInt(textField16.getText());
                            int Mass2 = Integer.parseInt(textField17.getText());
                            double proportion = Double.parseDouble(textField18.getText());
                            double MRSCycles = Double.parseDouble(textField19.getText());

                            int heavyMass;

                            if (Mass1 > Mass2){
                                heavyMass = Mass1;
                            }
                            else{
                                heavyMass = Mass2;
                            }

                            if (Mass1 > 0 && Mass2 > 0 && proportion <=1 && proportion >= 0 && MRSCycles > 0) {
                                try {
                                    FileWriter writerA = new FileWriter(filePath+ ".txt");


                                    WaveGrapher.writeValuesOfInterest(Mass1, Mass2,MRSCycles, PulseGenerator
                                            .getSuggestedTimeScale(heavyMass, MRSCycles, proportion),
                                        PulseGenerator
                                            .getSuggestedTimeScale(heavyMass, MRSCycles, proportion),
                                        proportion, writerA);
                                } catch (IOException p) {
                                    p.printStackTrace();
                                }
                            }

                            else{
                                JOptionPane.showMessageDialog(null,"Please ensure that all numbers are greater than 0 and the proportion is a decimal between 0 and 1.");
                            }

                        } catch (NumberFormatException f){
                            f.printStackTrace();
                            JOptionPane.showMessageDialog(null,"Please enter numbers only.");
                        }

                    }
                }

            }
        });


        generateButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == generateButton2){

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Specify a file to save");

                    int selection = fileChooser.showSaveDialog(new JFrame());

                    if (selection == JFileChooser.APPROVE_OPTION) {
                        File fileSave = fileChooser.getSelectedFile();
                        System.out.println("Save as: " + fileSave.getAbsolutePath());
                        String filePath = fileSave.getAbsolutePath();


                        try{
                            int Mass1 = Integer.parseInt(textField20.getText());
                            int Mass2 = Integer.parseInt(textField21.getText());
                            double proportion = Double.parseDouble(textField22.getText());
                            double MRSCycles = Double.parseDouble(textField23.getText());

                            int heavyMass;

                            if (Mass1 > Mass2){
                                heavyMass = Mass1;
                            }
                            else{
                                heavyMass = Mass2;
                            }

                            if (Mass1 > 0 && Mass2 > 0 && proportion <=1 && proportion >= 0 && MRSCycles > 0) {
                                try {
                                    FileWriter writerA = new FileWriter(filePath+ ".txt");


                                    WaveGrapher.singleMassPairWaveGrapher(Mass1, Mass2, PulseGenerator
                                            .getSuggestedTimeScale(heavyMass, MRSCycles, proportion),
                                        PulseGenerator
                                            .getSuggestedTimeScale(heavyMass, MRSCycles, proportion),
                                        MRSCycles, proportion, writerA);
                                } catch (IOException p) {
                                    p.printStackTrace();
                                }
                            }

                            else{
                                JOptionPane.showMessageDialog(null,"Please ensure that all numbers are greater than 0 and the proportion is a decimal between 0 and 1.");
                            }

                        } catch (NumberFormatException f){
                            f.printStackTrace();
                            JOptionPane.showMessageDialog(null,"Please enter numbers only.");
                        }

                    }
                }
            }
        });
    }

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dual Species MRS Waveform Simulator");
        frame.setContentPane(new DualSpeciesMRSSim().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

