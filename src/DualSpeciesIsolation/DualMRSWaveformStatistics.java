package DualSpeciesIsolation;


import javax.swing.JProgressBar;
import javax.swing.JTextField;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DualMRSWaveformStatistics {


    /**
     * Writes data on MRS wave merging for different masses to a specified file. The overall range of the scan
     * is a trapezoidal shape with the initial height set at window size and starting at the lowerBound mass
     * @param MRSCycles the number of MRSCycles; 0 < MRSCycles <= 850
     * @param proportional the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
      * @param adjacencyBreak the minimal acceptable same bit sequence, ie. minimal length of bits in a Hi or Lo section.
     *                       Must be grater than 1.
     * @param window window size of masses to be scanned; non-null and window >= 0 and window < inputMax-lowerBound
     * @param lowerBound smallest mass MRS waveform to start scan at; greater than 0
     * @param inputMax the Max mass MRS waveform, where the scan finishes; greater or equal to lowerBound
     * @param normOnly if Normalized On Times is the only wanted data
     * @param writer1 file writer that is non-null and writes to a specified file
     * @param field a textfield for displaying progress
     * @param progBar a progress bar for displaying the end of task
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     */
    public static void DualMRSMassScanner(int window, int lowerBound, int inputMax, double MRSCycles, double proportional, int adjacencyBreak, boolean normOnly, FileWriter writer1, JTextField field, JProgressBar progBar, double cycleCalib) {

        int corecount = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(corecount);
        System.out.println(corecount);


        int counter = 0;
        int upperBound = lowerBound + window;
        int max = inputMax + 5;

        try {
            if(normOnly){
                writer1.write("M1 M2 %C\n");
            }
            else {
                writer1.write("M1   M2  Mi  Br  Ma  OT  Sw  Mc Ss %C\n");
            }
            for (int i = lowerBound; i <= upperBound; i++) {
                for (int j = i; j <= upperBound; j++) {
                    if ((i <= max-5 && j <= max-5)||(i > (max-5) && j > (max-5))){
                        service.execute(new Grapher(i, j, MRSCycles, proportional, PulseGenerator.getSuggestedTimeScale(j, MRSCycles, proportional, cycleCalib), PulseGenerator.getSuggestedTimeScale(j, MRSCycles, proportional, cycleCalib), adjacencyBreak, writer1, max, normOnly, field, progBar, cycleCalib));
                        System.out.println(i + "   " + j);
                        counter++;
                        System.out.println(counter);
                    }
                }
                if (upperBound < max) {
                    upperBound++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


