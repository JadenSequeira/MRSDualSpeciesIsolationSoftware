package DualSpeciesIsolation;


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
     */
    public static void DualMRSMassScanner(int window, int lowerBound, int inputMax, double MRSCycles, double proportional, int adjacencyBreak, boolean normOnly) {

        Scanner a = new Scanner(System.in);
        System.out.println("Please enter file name and location (ie: C:/Users/) :  ");
        String file = a.nextLine();
        StringBuilder fileDestination = new StringBuilder();
        fileDestination.append(file);

        if(normOnly){
            fileDestination.append("NormalizedOnTimes.txt");
        }
        else {
            fileDestination.append("Data.txt");
        }

        String fileNameA = fileDestination.toString();

        int corecount = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(corecount);
        System.out.println(corecount);


        int counter = 0;
        int upperBound = lowerBound + window;
        int max = inputMax + 5;

        try {
            FileWriter writer1 = new FileWriter(fileNameA);
            if(normOnly){
                writer1.write("M1 M2 %C\n");
            }
            else {
                writer1.write("M1   M2  Mi  Br  Ma  OT  Sw  Mc Ss %C\n");
            }
            for (int i = lowerBound; i <= upperBound; i++) {
                for (int j = i; j <= upperBound; j++) {
                    if ((i <= max-5 && j <= max-5)||(i > (max-5) && j > (max-5))){
                        service.execute(new Grapher(i, j, MRSCycles, proportional, PulseGenerator.getSuggestedTimeScale(j, MRSCycles, proportional), PulseGenerator.getSuggestedTimeScale(j, MRSCycles, proportional), adjacencyBreak, writer1, max, normOnly));
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


