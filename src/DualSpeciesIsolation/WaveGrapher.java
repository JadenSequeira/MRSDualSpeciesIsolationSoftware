package DualSpeciesIsolation;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     */
    public static void singleMassPairWaveGrapher(double Mass1, double Mass2, int timeScale, int steps, double MRSCycles, double Proportion, FileWriter writer1, double cycleCalib){

        double heavyMass, lightMass;

        if (Mass1 > Mass2){
            heavyMass = Mass1;
            lightMass = Mass2;
        }
        else{
            heavyMass = Mass2;
            lightMass = Mass1;
        }

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, Proportion, cycleCalib,0,0);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, Proportion, cycleCalib*java.lang.Math.sqrt((heavyMass/132.905))*MRSCycles, cycleCalib,0,0);


        try {
            Waveform mainWave = new Waveform(waveA, waveB, false);

            try {
                ArrayList<Double> testTimingsA = waveA.getTimings();
                ArrayList<Integer> testValuesA = waveA.getWave();
                ArrayList<Integer> testValuesB = waveB.getWave();
                ArrayList<Integer> testValuesC = mainWave.getWave();

                writer1.write("Ti   HM  LM  CM\n");

                for (int i = 0; i < testTimingsA.size(); i++) {

                    writer1.write(testTimingsA.get(i) + "   " + testValuesA.get(i) + "  " + testValuesB.get(i) + "  " + testValuesC.get(i)+ "\n");


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
     * Writes the values and timings for the MRS combination, Ion of Interest, and Specialised XOR combination mass waveforms
     * to three separate files for graphing
     * @param MOI1 The first Mass of Interest that is non-null and greater than 0
     * @param MOI2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     * and therefore sets the resolution; steps must be greater than zero
     * @param writer1 writes the waveform (timings and values) for the heavier mass to a specified file,
     *                not the same instance as writer2 or writer3
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     * @param IOI Ion of Interest; greater than zero
     */
    public static void singleIOIPairWaveGrapher(double MOI1, double MOI2, double IOI, int timeScale, int steps, double MRSCycles, double prop, FileWriter writer1, double cycleCalib, double startCycle){
        double heavyMass;
        double lightMass;
        double totalTime;
        double startTime;
        double delayOverwrite = 0;

        if (MOI1 > MOI2){
            heavyMass = MOI1;
            lightMass = MOI2;
        }
        else{
            heavyMass = MOI2;
            lightMass = MOI1;
        }


        totalTime = cycleCalib*java.lang.Math.sqrt((heavyMass/132.905))*MRSCycles;
        startTime =  startCycle*(cycleCalib*java.lang.Math.sqrt((heavyMass/132.905)));
        double cycleCalibration = cycleCalib*java.lang.Math.sqrt((heavyMass/132.905));
        if (startCycle != 0){
            delayOverwrite = 5 * (int) ((((32800) * java.lang.Math.sqrt((heavyMass / 132.905))) -
                (5 * (int) ((prop * cycleCalibration / 2) / 5) / 2)) / 5);
        }

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, prop, cycleCalib, startCycle,0);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, prop, totalTime,cycleCalib, startTime, delayOverwrite);
        Waveform waveIOI = new Waveform(IOI, timeScale, steps, prop, totalTime, cycleCalib, startTime, delayOverwrite);

        try {

            Waveform mainWave = new Waveform(waveA, waveB, false);
            Waveform finalWave = new Waveform(mainWave, waveIOI, true);
            ArrayList<Double> testTimings = finalWave.getTimings();
            ArrayList<Integer> bitList = finalWave.getWave();
            ArrayList<Integer> IOIWave = waveIOI.getWave();
            ArrayList<Integer> combWave = mainWave.getWave();

            try {

                writer1.write("Ti   CM  IO  XR\n");

                for (int i = 0; i < testTimings.size(); i++) {

                    writer1.write(testTimings.get(i) + "   " + combWave.get(i) + "  " + IOIWave.get(i) + "  " + bitList.get(i)+ "\n");

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
     * Writes the lengths of each Hi and Lo segment in order (without the last segment due to resolution extension),
     * to a specified file
     * @param Mass1 The first Mass of Interest that is non-null and greater than 0
     * @param Mass2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param Proportion the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     * and therefore sets the resolution; steps must be greater than zero
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     * @param writerA writes the adjacent lengths in order of occurrence to a specified file
     */
    public static void checkAdjLengths(double Mass1, double Mass2, int timeScale, int steps, double MRSCycles, double Proportion, FileWriter writerA, double cycleCalib){
        ArrayList<Integer> lengths = new ArrayList<>(PulseGenerator.adjacentLengths(Mass1, Mass2, MRSCycles, Proportion, timeScale, steps, cycleCalib));
        try {
            writeLengthsToFile(writerA,lengths);
            writerA.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * @param Mass1 The first Mass of Interest that is non-null and greater than 0
     * @param Mass2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param Proportion the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     * and therefore sets the resolution; steps must be greater than zero
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     * @param writerA writes the adjacent lengths in order of occurrence to a specified file
     * @param IOI Ion of Interest; greater than zero
     */
    public static void checkIOIAdjLengths(double Mass1, double Mass2, double IOI, int timeScale, int steps, double MRSCycles, double Proportion, FileWriter writerA, double cycleCalib, double startCycle){
        ArrayList<Integer> lengths = new ArrayList<>(PulseGenerator.adjacentIOILengths(Mass1, Mass2, IOI, MRSCycles, Proportion, timeScale, steps, cycleCalib, startCycle));
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


    public static void writeMRSdeltaTPairs(double Mass1, double IOI, double MRSCycles, double Proportion, double cycleCalib, FileWriter writerA, double startCycle) throws IOException{
        ArrayList<List<Integer>> data = PulseGenerator.SingleMRSdeltaTPairs(Mass1,IOI, MRSCycles,Proportion, cycleCalib, startCycle);
        int MRSCyc, deltaT;
        writerA.write("MR   Dt\n");

        for(int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size() - 1; j++) {
                writerA.write(data.get(i).get(j)/10 + "." + data.get(i).get(j) % 10  + "    " + data.get(i).get(j+1) + "\n");
            }
        }

        writerA.close();
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
             * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
             */
            public static void writeValuesOfInterest(double mass1, double mass2, double MRSCycles, int timeScale, int steps, double prop, FileWriter writer1, double cycleCalib){
            int timeSum; //timeSum uses nanoseconds only
            int start;
            Waveform waveA = new Waveform(mass1, MRSCycles, timeScale, steps, prop, cycleCalib,0,0);
            Waveform waveB = new Waveform(mass2, timeScale, steps, prop, cycleCalib*java.lang.Math.sqrt((94/132.905))*50, cycleCalib,0,0);

            try {
                Waveform mainWave = new Waveform(waveA, waveB, false);

                try {
                    ArrayList<Double> testTimingsA = waveA.getTimings();
                    ArrayList<Integer> testValuesA = waveA.getWave();
                    timeSum = arrSum(PulseGenerator
                        .adjacentLengths(mass1, mass2, MRSCycles, prop, timeScale, steps, cycleCalib));
                    start =
                        PulseGenerator.adjacentLengths(mass1, mass2, MRSCycles, prop, timeScale, steps, cycleCalib)
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
             * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
             * @return a list of time differences between adjacency sequences that have adjacent counts
             *         below the adjBreak threshold
             */
            public ArrayList<Double> testFreq(double mass1, double mass2, double MRSCycles, int timeScale, int steps, double prop, int adjBreak, double cycleCalib) {

            double heavyMass, lightMass;
            ArrayList<Double> Differences = new ArrayList<>();

            if (mass1 > mass2){
                heavyMass = mass1;
                lightMass = mass2;
            }
            else{
                heavyMass = mass2;
                lightMass = mass1;
            }

            double totalTime = cycleCalib * java.lang.Math.sqrt((heavyMass / 132.905)) * MRSCycles;

            Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, prop, cycleCalib,0,0);
            Waveform waveB = new Waveform(lightMass, timeScale, steps, prop, totalTime, cycleCalib,0,0);
            try {

                Waveform mainWave = new Waveform(waveA, waveB, false);
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
             * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
             */
            public static void graphGenerator(int startMass, int endMass, double MRSCycles, int timeScale, double prop, int adjBreak, double cycleCalib) {

            ArrayList<Integer> Mass1 = new ArrayList<>();
            ArrayList<Integer> Mass2 = new ArrayList<>();
            ArrayList<Integer> Peaks = new ArrayList<>();
            ArrayList<Integer> Switches = new ArrayList<>();


            for (int i = startMass; i <= endMass; i++) {
                for (int j = i; j <= endMass; j++) {
                    int[] temp = PulseGenerator
                        .pulseScheme(i, j, MRSCycles, prop, PulseGenerator.getSuggestedTimeScale(j,MRSCycles,prop, cycleCalib), PulseGenerator.getSuggestedTimeScale(j,MRSCycles,prop, cycleCalib), adjBreak, cycleCalib);
                    Peaks.add(temp[0]);
                    Switches.add(temp[1]);
                    Mass1.add(i);
                    Mass2.add(j);
                }
            }
            for (int i = 0; i < Peaks.size(); i++) {
                System.out.println(Mass1.get(i) + "  " + Mass2.get(i) + "    " + Peaks.get(i) + "    " + Switches.get(i) + "\n");
            }

        }


            /**
             * @param MOI1 The first Mass of Interest that is non-null and greater than 0
             * @param MOI2 The second Mass of Interest that is non-null and greater than 0
             * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
             * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
             * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
             * @return the Normalized On Time for the Mass pair MRS waveform
             */
            public static int getNormOnTime(double MOI1, double MOI2, double MRSCycles, double prop, double cycleCalib){

            double heavyMass, lightMass;

            if (MOI1 > MOI2){
                heavyMass = MOI1;
                lightMass = MOI2;
            }
            else{
                heavyMass = MOI2;
                lightMass = MOI1;
            }
            return PulseGenerator.pulseScheme(heavyMass, lightMass, MRSCycles, prop, PulseGenerator.getSuggestedTimeScale(heavyMass,MRSCycles,prop, cycleCalib), PulseGenerator.getSuggestedTimeScale(heavyMass,MRSCycles,prop, cycleCalib), 5, cycleCalib)[9];
        }

    }

