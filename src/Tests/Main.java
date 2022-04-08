package Tests;

import DualSpeciesIsolation.DualMRSWaveformStatistics;
import DualSpeciesIsolation.SpecViolation;
import DualSpeciesIsolation.WaveGrapher;
import DualSpeciesIsolation.Waveform;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

//    private static class Drawer extends JPanel {
//        public void paintComponent(Graphics comp) {
//            super.paintComponent(comp);
//            comp.drawString("ABC", 20,30);
//
//        }
//    }
//
//    private static class ButtonCheck implements ActionListener{
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.exit(0);
//        }
//    }
//
    public static void main(String[] args){

  //      Drawer displayPanel = new Drawer();
  //      JButton Button = new JButton("OK");
  //      ButtonCheck check = new ButtonCheck();
  //      Button.addActionListener(check);
//
  //      JButton Button1 = new JButton("OK");
  //      ButtonCheck check1 = new ButtonCheck();
  //      Button1.addActionListener(check1);
//
  //      JPanel content = new JPanel();
  //      content.setLayout(new FlowLayout());
  //      content.add(displayPanel);
  //      content.add(Button);
  //      content.add(Button1);
//
//
//        JFrame window = new JFrame("MRS Dual Species Waveform Simulator");
//        window.setContentPane(content);
//        window.setSize(500,250);
//        window.setLocation(25,50);
//        window.setVisible(true);



   //     WaveGrapher.getNormOnTime(88, 87, 30, 0.4);
        try {

            FileWriter writerA = new FileWriter("C:/Users/Jaden/Desktop/Hi.txt");
            DualMRSWaveformStatistics.DualMRSMassScanner(20, 60, 80, 30, 0.4, 10, false, writerA);


        } catch (IOException e){
            e.printStackTrace();
        }




    }

}
