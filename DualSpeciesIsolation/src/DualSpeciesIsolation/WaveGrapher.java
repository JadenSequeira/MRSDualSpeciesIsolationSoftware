package DualSpeciesIsolation;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WaveGrapher {


    /**
     * Writes the values and timings for the heavy, lighter, and combination mass waveforms to three separate files for graphing
     * @param Mass1 The first Mass of Interest that is non-null and greater than 0
     * @param Mass2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param Proportion the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     * and therefore sets the resolution; steps must be greater than zero
     * @param writer1 writes the waveform (timings and values) for the heavier mass to a specified file,
     *                not the same instance as writer2 or writer3
     * @param writer2 writes the waveform (timings and values) for the lighter mass to a specified file,
     *                not the same instance as writer1 or writer3
     * @param writer3 writes the waveform (timings and values) for the AND combination of masses to a specified file,
     *                not the same instance as writer1 or writer2
     */
    public static void singleMassPairWaveGrapher(int Mass1, int Mass2, int timeScale, int steps, double MRSCycles, double Proportion, FileWriter writer1, FileWriter writer2, FileWriter writer3){

        int heavyMass, lightMass;

        if (Mass1 > Mass2){
            heavyMass = Mass1;
            lightMass = Mass2;
        }
        else{
            heavyMass = Mass2;
            lightMass = Mass1;
        }

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, Proportion);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, Proportion, 22682.5*java.lang.Math.sqrt((heavyMass/132.905))*MRSCycles);

        try {
            Waveform mainWave = new Waveform(waveA, waveB);

            try {
                ArrayList<Double> testTimingsA = waveA.getTimings();
                ArrayList<Integer> testValuesA = waveA.getWave();
                ArrayList<Double> testTimingsB = waveB.getTimings();
                ArrayList<Integer> testValuesB = waveB.getWave();
                ArrayList<Double> testTimingsC = mainWave.getTimings();
                ArrayList<Integer> testValuesC = mainWave.getWave();

                for (int i = 0; i < testTimingsA.size(); i++) {

                    writer1.write(testTimingsA.get(i) + "    " + testValuesA.get(i) + "\n");
                    writer2.write(testTimingsB.get(i) + "    " + testValuesB.get(i) + "\n");
                    writer3.write(testTimingsC.get(i) + "    " + testValuesC.get(i) + "\n");


                }
                writer1.close();
                writer2.close();
                writer3.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SpecViolation e){
            e.printStackTrace();
        }

    }


    /**
     * Writes the lengths of each Hi and Lo segment in order (without the last segment due to resolution extension),
     * to a specified file
     * @param Mass1 The first Mass of Interest that is non-null and greater than 0
     * @param Mass2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param Proportion the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     * and therefore sets the resolution; steps must be greater than zero
     * @param writerA writes the adjacent lengths in order of occurrence to a specified file
     */
    public static void checkAdjLengths(int Mass1, int Mass2, int timeScale, int steps, double MRSCycles, double Proportion, FileWriter writerA){
        ArrayList<Integer> lengths = new ArrayList<>(PulseGenerator.adjacentLengths(Mass1, Mass2, MRSCycles, Proportion, timeScale, steps));
        try {
            writeLengthsToFile(writerA,lengths);
            writerA.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Writes  list of lengths to a file
     * @param writerA writes lengths to a specified file
     * @param lengths list of lengths that is non-null
     * @throws IOException if writing to file is interrupted/fails
     */
    private static synchronized void writeLengthsToFile(FileWriter writerA, ArrayList<Integer> lengths) throws IOException{
        for (Integer length : lengths) {
            writerA.write(length + "\n");
        }
    }



    /**
     * Writes the values of interest of the AND combination waveform; the values of interest are the values
     * without the left tail and right tails (the delay and the extended ending due to resolution)
     * @param mass1 The first Mass of Interest that is non-null and greater than 0
     * @param mass2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     * and therefore sets the resolution; steps must be greater than zero
     * @param writer1 writes the values of interest to a file for graphing//fft/dft usage
     */
    public static void writeValuesOfInterest(int mass1, int mass2, double MRSCycles, int timeScale, int steps, double prop, FileWriter writer1){
        int timeSum; //timeSum uses nanoseconds only
        int start;
        Waveform waveA = new Waveform(mass1, MRSCycles, timeScale, steps, prop);
        Waveform waveB = new Waveform(mass2, timeScale, steps, prop, 22682.5*java.lang.Math.sqrt((94/132.905))*50);

        try {
            Waveform mainWave = new Waveform(waveA, waveB);

            try {
                ArrayList<Double> testTimingsA = waveA.getTimings();
                ArrayList<Integer> testValuesA = waveA.getWave();
                timeSum = arrSum(PulseGenerator
                    .adjacentLengths(mass1, mass2, MRSCycles, prop, timeScale, steps));
                start =
                    PulseGenerator.adjacentLengths(mass1, mass2, MRSCycles, prop, timeScale, steps)
                        .get(0);

                for (int i = 0; i < testTimingsA.size(); i++) {
                    if (i >= start && i < timeSum) {
                        writer1.write(testValuesA.get(i) + "\n");
                    }
                }
                writer1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SpecViolation e){
            e.printStackTrace();
        }

    }


    /**
     *
     * @param arr non null list of integers
     * @return sum of all elements in arr
     */
    private static int arrSum(ArrayList<Integer> arr){
        int sum = 0;

        for (Integer integer : arr) {
            sum += integer;
        }

        return sum;
    }


    /**
     * @param mass1 The first Mass of Interest that is non-null and greater than 0
     * @param mass2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     *              and therefore sets the resolution; steps must be greater than zero
     * @param adjBreak the minimum number of same adjacent values that will not have their
     *                 time value recorded to be used a point for difference calculation
     * @return a list of time differences between adjacency sequences that have adjacent counts
     *         below the adjBreak threshold
     */
    public ArrayList<Double> testFreq(int mass1, int mass2, double MRSCycles, int timeScale, int steps, double prop, int adjBreak) {

        int heavyMass, lightMass;
        ArrayList<Double> Differences = new ArrayList<>();

        if (mass1 > mass2){
            heavyMass = mass1;
            lightMass = mass2;
        }
        else{
            heavyMass = mass2;
            lightMass = mass1;
        }

        double totalTime = 22682.5 * java.lang.Math.sqrt((heavyMass / 132.905)) * MRSCycles;

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, prop);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, prop, totalTime);
        try {

            Waveform mainWave = new Waveform(waveA, waveB);
            ArrayList<Integer> bitList = mainWave.getWave();
            ArrayList<Double> mainTimings = mainWave.getTimings();
            ArrayList<Double> transferTimes = new ArrayList<>();

            int counter = 1;
            int value = bitList.get(0);
            for (int i = 0; i < bitList.size() - 1; i++) {

                if (bitList.get(i + 1) == value) {
                    counter++;

                } else {
                    if (counter < adjBreak) {
                        if (value == 0) {
                            transferTimes.add(mainTimings.get(i - counter + 1));
                        } else {
                            transferTimes.add(mainTimings.get(i - counter));
                        }
                    }

                    counter = 1;
                    value = bitList.get(i + 1);

                }

            }


            for (int i = 0; i < transferTimes.size() - 1; i++) {
                Differences.add(transferTimes.get(i + 1) - transferTimes.get(i));
            }
        }catch (SpecViolation e){
            e.printStackTrace();
        } finally {

            return new ArrayList<>(Differences);
        }
    }



    /**
     * Writes the number of peaks and total switches between Hi and Lo for the combination Wave
     * of each mass pair from start Mass to endMass
     * This is a linear implementation of the scan and has a longer running time
     * The scan area wil be a triangle of size (endMass-startMass)^2/2
     * @param startMass The lowest mass for the scan; starting point of scan
     * @param endMass The last mass for the scan; no data will be taken using larger masses
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param adjBreak the minimum number of same adjacent values that will not have their
     *                 time value recorded to be used a point for difference calculation
     * @param writerA writes the mass pairs and their corresponding peaks and switches to a
     *                specified file
     */
    public void graphGenerator(int startMass, int endMass, double MRSCycles, int timeScale, double prop, int adjBreak, FileWriter writerA) {

        ArrayList<Integer> Mass1 = new ArrayList<>();
        ArrayList<Integer> Mass2 = new ArrayList<>();
        ArrayList<Integer> Peaks = new ArrayList<>();
        ArrayList<Integer> Switches = new ArrayList<>();


        for (int i = startMass; i <= endMass; i++) {
            for (int j = i; j <= endMass; j++) {
                int[] temp = PulseGenerator
                    .pulseScheme(i, j, MRSCycles, prop, timeScale, timeScale, adjBreak);
                Peaks.add(temp[0]);
                Switches.add(temp[1]);
                Mass1.add(i);
                Mass2.add(j);
            }
        }
        try {
            for (int i = 0; i < Peaks.size(); i++) {
                writerA.write(Mass1.get(i) + "  " + Mass2.get(i) + "    " + Peaks.get(i) + "    " + Switches.get(i) + "\n");
            }
            writerA.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param MOI1 The first Mass of Interest that is non-null and greater than 0
     * @param MOI2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @return the Normalized On Time for the Mass pair MRS waveform
     */
    public static int getNormOnTime(double MOI1, double MOI2, double MRSCycles, double prop){

        double heavyMass, lightMass;

        if (MOI1 > MOI2){
            heavyMass = MOI1;
            lightMass = MOI2;
        }
        else{
            heavyMass = MOI2;
            lightMass = MOI1;
        }
        return PulseGenerator.pulseScheme(heavyMass, lightMass, MRSCycles, prop, PulseGenerator.getSuggestedTimeScale((int)heavyMass,MRSCycles,prop), PulseGenerator.getSuggestedTimeScale((int)heavyMass,MRSCycles,prop), 5)[9];
    }

}
