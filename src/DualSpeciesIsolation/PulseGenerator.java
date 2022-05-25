package DualSpeciesIsolation;

import java.util.ArrayList;
import java.util.List;


public class PulseGenerator{

    /**
     * @param mass mass of interest for overall timeScale that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     * @return the suggested timeScale setting
     */
    public static int getSuggestedTimeScale(double mass, double MRSCycles, double prop, double cycleCalib){

        double timeOn = cycleCalib*java.lang.Math.sqrt((mass/132.905))*MRSCycles;
        double timeDelay = 5*(int)((((32800)*java.lang.Math.sqrt((mass/132.905))) - (5*(int)((prop*cycleCalib*java.lang.Math.sqrt((mass/132.905))/2)/5)/2))/5);

        return (int) (timeOn + timeDelay + (timeOn/MRSCycles));
    }

    public static int getSuggestedTimeScaleShifted(double mass, double MRSCycles, double prop, double cycleCalib){

        double timeOn = cycleCalib*java.lang.Math.sqrt((mass/132.905))*MRSCycles;

        return (int) (timeOn + (timeOn/MRSCycles));
    }


    /**
     * @param mass The Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param proportion the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     * @return the expected total OnTime of the MRS waveform for the specified mass
     */
    public static int normFactor(double mass, double MRSCycles, double proportion, double cycleCalib){

        return (int) ((5*((int)(((1-proportion)*cycleCalib*java.lang.Math.sqrt((mass/132.905))/2)/5)) - 1)*(MRSCycles*2));

    }



    /**
     * @param MOI1 The first Mass of Interest that is non-null and greater than 0
     * @param MOI2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     *              and therefore sets the resolution; steps must be greater than zero
     * @param adjacencyBreak the minimal acceptable same bit sequence, ie. minimal length of bits in a Hi or Lo section.
     *                       Must be grater than 1.
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     * @return List with: the total number of same bit segments that are smaller than or equal to the adjacency break,
     * the number of switches, the maximum and minimum of the peaks ( max/min of first value), the On Time of Waveform,
     * the minimum peak width, the second minimum peak width,  the first occurrence of minimum and second minimum peak widths,
     * and the percentage normalization of OnTime.
     */
    public static int[] pulseScheme( double MOI1, double MOI2, double MRSCycles, double prop, int timeScale, int steps, int adjacencyBreak, double cycleCalib){

        double heavyMass;
        double lightMass;
        int counter;
        int indetCounter;
        int inclCounter;
        int value;
        int peaks = 0;
        int indetPeaks = 0;
        int onTime = 0;
        int inclPeaks = 0;
        int switches = 0;
        int minAdj = -1;
        int secondSmallest = -1;
        double totalTime;
        int minTime = -1;
        int secondMinTime = -1;
        int[] pkSwt = new int[10];


        if (MOI1 > MOI2){
            heavyMass = MOI1;
            lightMass = MOI2;
        }
        else{
            heavyMass = MOI2;
            lightMass = MOI1;
        }

        totalTime = cycleCalib*java.lang.Math.sqrt((heavyMass/132.905))*MRSCycles;

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, prop, cycleCalib,0);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, prop, totalTime, cycleCalib,0);
        try {
            Waveform mainWave = new Waveform(waveA, waveB, false);
            ArrayList<Integer> bitList = mainWave.getWave();



        if (bitList.get(bitList.size()-1) != bitList.get(bitList.size()-2)){
            peaks++;
            indetPeaks++;
            inclPeaks++;
            switches++;
        }

        counter = 1;
        inclCounter = 1;
        indetCounter = 1;
        value = bitList.get(0);
        if (value == 1){
            onTime++;
        }
        for(int i = 0; i < bitList.size()-1; i++){

            if (bitList.get(i+1) == value){
                counter++;
                inclCounter++;
                indetCounter++;
                if(value == 1){
                    onTime++;
                }
            }
            else{
                switches++;
                if (value == 1){
                    inclCounter++;
                }
                if (value == 0){
                    indetCounter--;
                }
                if(minAdj < 0 || counter < minAdj){
                    if (minAdj > 0){
                        secondSmallest = minAdj;
                        secondMinTime = minTime;
                    }
                    minAdj = counter;
                    minTime = i;

                }

                if(minAdj > 0 && counter > minAdj && (secondSmallest > counter || secondSmallest < 0)){
                    secondSmallest = counter;
                    secondMinTime = i;
                }

                if(counter < adjacencyBreak){
                    peaks++;
                }
                if (inclCounter < adjacencyBreak){
                    inclPeaks++;
                }
                if (indetCounter < adjacencyBreak){
                    indetPeaks++;
                }

                if(value == 0){
                    inclCounter = 2;
                    counter = 1;
                    indetCounter = 1;
                    onTime++;
                }
                else {
                    counter = 1;
                    inclCounter = 1;
                    indetCounter = 0;
                }
                value = bitList.get(i+1);

            }
        }

        if (secondSmallest < 0 || minAdj < 0){
            minAdj = 0;
            secondSmallest = 0;
        }


        pkSwt[0] = peaks;
        pkSwt[1] = switches;
        pkSwt[2] = indetPeaks;
        pkSwt[3] = inclPeaks;
        pkSwt[4] = onTime;
        pkSwt[5] = minAdj;
        pkSwt[6] = secondSmallest;
        pkSwt[7] = minTime;
        pkSwt[8] = secondMinTime;
        pkSwt[9] = (int)((double)onTime*100/normFactor(heavyMass,MRSCycles, prop, cycleCalib));



        } catch (SpecViolation e){
            e.printStackTrace();
        } finally {

            return pkSwt;

        }

    }


    /**
     *
     * @param MOI1 The first Mass of Interest that is non-null and greater than 0
     * @param MOI2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     *              and therefore sets the resolution; steps must be greater than zero
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     * @return a list of the counts of each adjacent segment of same bits in the wave
     */
    public static ArrayList<Integer> adjacentLengths( double MOI1, double MOI2, double MRSCycles, double prop, int timeScale, int steps, double cycleCalib){

        double heavyMass;
        double lightMass;
        int counter;
        int value;
        double totalTime;

        ArrayList<Integer> adjCounts = new ArrayList<>();


        if (MOI1 > MOI2){
            heavyMass = MOI1;
            lightMass = MOI2;
        }
        else{
            heavyMass = MOI2;
            lightMass = MOI1;
        }

        totalTime = cycleCalib*java.lang.Math.sqrt((heavyMass/132.905))*MRSCycles;

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, prop, cycleCalib,0);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, prop, totalTime,cycleCalib,0);
        try{
            Waveform mainWave = new Waveform(waveA, waveB, false);
            ArrayList<Integer> bitList = mainWave.getWave();


        counter = 1;
        value = bitList.get(0);
        for(int i = 0; i < bitList.size()-1; i++){

            if (bitList.get(i+1) == value){
                counter++;
            }
            else{

                adjCounts.add(counter);
                counter = 1;
                value = bitList.get(i+1);

            }
        }

        }catch (SpecViolation e){
            e.printStackTrace();
        } finally {

            return new ArrayList<>(adjCounts);
        }
    }


    /**
     * @param MOI1 The first Mass of Interest that is non-null and greater than 0
     * @param MOI2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     *              and therefore sets the resolution; steps must be greater than zero
     * @param IOI Ion of Interest; greater than zero
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     * @return total time when IOI is Lo and combination waveform of MOI 1 and MOI 2 is Hi (in ns)
     */
    public static int IOIWaveformOnTime( double MOI1, double MOI2, double IOI, double MRSCycles, double prop, int timeScale, int steps, double cycleCalib, double startCycle, Boolean dualAndComb){

        int OnTime = 0;
        double heavyMass;
        double lightMass;
        double totalTime;
        double startTime = 0;
        double delayOverwrite = 0;

        if (MOI1 > MOI2){
            heavyMass = MOI1;
            lightMass = MOI2;
        }
        else{
            heavyMass = MOI2;
            lightMass = MOI1;
        }


        //TODO: Change Start times to include delay (+ rounding) and then test
        double cycleCalibration = cycleCalib*java.lang.Math.sqrt((heavyMass/132.905));
        totalTime = cycleCalib*java.lang.Math.sqrt((heavyMass/132.905))*MRSCycles;
        if (startCycle != 0){
        startTime = 5 * (int) ((((32800) * java.lang.Math.sqrt((heavyMass / 132.905))) - (5 * (int) ((prop * cycleCalibration / 2) / 5) / 2)
            + (startCycle*(cycleCalibration))) / 5);
        }

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, prop, cycleCalib, startCycle);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, prop, totalTime,cycleCalib, startTime);
        Waveform waveIOI = new Waveform(IOI, timeScale, steps, prop, totalTime, cycleCalib,startTime);

        try {
            Waveform mainWave = new Waveform();
            if (dualAndComb) {
                mainWave = new Waveform(waveA, waveB, false);
            } else{
                mainWave = new Waveform(waveA);
            }
            Waveform finalWave = new Waveform(mainWave, waveIOI, true);
            ArrayList<Integer> bitList = finalWave.getWave();

            for (int i = 0; i < bitList.size(); i++){
                if (bitList.get(i) == 1){
                    OnTime++;
                }
            }

        } catch (SpecViolation e){

            e.printStackTrace();

        }

        return OnTime;

    }


    /**
     * @param MOI1 The first Mass of Interest that is non-null and greater than 0
     * @param MOI2 The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     *              and therefore sets the resolution; steps must be greater than zero
     * @param IOI Ion of Interest; greater than zero
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     * @return list of all segment lengths for specialised XOR combination of dual/single
     *         MRS waveform and IOI MRS waveform
     */
    public static ArrayList<Integer> adjacentIOILengths( double MOI1, double MOI2, double IOI, double MRSCycles, double prop, int timeScale, int steps, double cycleCalib, double startCycle, Boolean dualAndComb){

        double heavyMass;
        double lightMass;
        int counter;
        int value;
        double totalTime;
        double startTime = 0;
        double delayOverwrite = 0;


        ArrayList<Integer> adjCounts = new ArrayList<>();


        if (MOI1 > MOI2){
            heavyMass = MOI1;
            lightMass = MOI2;
        }
        else{
            heavyMass = MOI2;
            lightMass = MOI1;
        }

        totalTime = cycleCalib*java.lang.Math.sqrt((heavyMass/132.905))*MRSCycles;
        double cycleCalibration = cycleCalib*java.lang.Math.sqrt((heavyMass/132.905));
        if (startCycle != 0) {
            startTime = 5 * (int) ((((32800) * java.lang.Math.sqrt((heavyMass / 132.905))) -
                (5 * (int) ((prop * cycleCalibration / 2) / 5) / 2)
                + (startCycle * (cycleCalib * java.lang.Math.sqrt((heavyMass / 132.905))))) / 5);
        }



//        if (startCycle != 0){
//            delayOverwrite = 5 * (int) ((((32800) * java.lang.Math.sqrt((heavyMass / 132.905))) -
//                (5 * (int) ((prop * cycleCalibration / 2) / 5) / 2)) / 5);
//        }

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, prop, cycleCalib, startCycle);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, prop, totalTime,cycleCalib, startTime);
        Waveform waveIOI = new Waveform(IOI, timeScale, steps, prop, totalTime, cycleCalib, startTime);

        try {
            Waveform mainWave = new Waveform();
            if (dualAndComb) {
                mainWave = new Waveform(waveA, waveB, false);
            } else{
                mainWave = new Waveform(waveA);
            }
            Waveform finalWave = new Waveform(mainWave, waveIOI, true);
            ArrayList<Integer> bitList = finalWave.getWave();


            counter = 1;
            value = bitList.get(0);
            for(int i = 0; i < bitList.size()-1; i++){

                if (bitList.get(i+1) == value){
                    counter++;
                }
                else{

                    adjCounts.add(counter);
                    counter = 1;
                    value = bitList.get(i+1);

                }
            }

        }catch (SpecViolation e){
            e.printStackTrace();
        } finally {

            return new ArrayList<>(adjCounts);

        }
    }




    public static ArrayList<List<Integer>> SingleMRSdeltaTPairs(double Mass1, double IOI, double MRSCycles, double Proportion, double cycleCalib, double startCycle){

        ArrayList<List<Integer>> MRSdeltaTPairs = new ArrayList<>();
        int deltaT;
        double time = 0;
        int index = 0;
        int timeScale = 0;


        double cycleCalibration = cycleCalib*java.lang.Math.sqrt((Mass1/132.905));
        ArrayList<Double> MRSEnds = new ArrayList<>(getSingleMRSEnds(Mass1,MRSCycles,Proportion, cycleCalib, startCycle));
        if (startCycle != 0) {
            timeScale += getSuggestedTimeScaleShifted(Mass1, MRSCycles,
                Proportion, cycleCalib);
            time +=  5 * (int) ((((32800) * java.lang.Math.sqrt((Mass1 / 132.905))) -
                (5 * (int) ((Proportion * cycleCalibration / 2) / 5) / 2)) / 5);

            for (double j = 0.5; j <= startCycle; j += 0.5){
                time += 5*(int)((Proportion*cycleCalibration/2)/5);
                time += 5*(int)(((1-Proportion)*cycleCalibration/2)/5);
            }

        } else{
            timeScale += getSuggestedTimeScale(Mass1, MRSCycles,
                Proportion, cycleCalib);
        }
        ArrayList<Integer> adjLengths = new ArrayList<>( adjacentIOILengths(Mass1, Mass1, IOI, MRSCycles, Proportion, timeScale
                    , timeScale, cycleCalib, startCycle,false));


        for (int i = 0; i < adjLengths.size(); i += 2){
            time += adjLengths.get(i);
            time += adjLengths.get(i+1);
            deltaT = adjLengths.get(i+1);
            ArrayList<Integer> temp = new ArrayList<>();

            while (index < MRSEnds.size() && time > MRSEnds.get(index)){
                index++;
            }
            if (index < MRSEnds.size() && time <= MRSEnds.get(index)){
                temp.add((index+1)*10/2);
                temp.add(deltaT);
                MRSdeltaTPairs.add(temp);
            }


        }

        return new ArrayList<>(MRSdeltaTPairs);
    }



    private static ArrayList<Double> getSingleMRSEnds(double Mass1, double MRSCycles, double Proportion, double cycleCalib, double startCycle){

        ArrayList<Double> MRSEnds = new ArrayList<>();
        double time = 0;
        double cycleCalibration = cycleCalib*java.lang.Math.sqrt((Mass1/132.905));
        double timeDelay = 5*(int)((((32800)*java.lang.Math.sqrt((Mass1/132.905))) - (5*(int)((Proportion*cycleCalibration/2)/5)/2))/5);

        if(startCycle != 0){
            time += 5 * (int) ((((32800) * java.lang.Math.sqrt((Mass1 / 132.905))) -
                (5 * (int) ((Proportion * cycleCalibration / 2) / 5) / 2)
                + (startCycle * (cycleCalib * java.lang.Math.sqrt((Mass1 / 132.905))))) / 5);

        } else {
            time += timeDelay;
        }

        for(double i = 0.5; i <= MRSCycles; i += 0.5){

            time += 5*(int)((Proportion*cycleCalibration/2)/5);
            time += 5*(int)(((1-Proportion)*cycleCalibration/2)/5);
            MRSEnds.add(time);
//            System.out.println(time);
        }



        return new ArrayList<>(MRSEnds);

    }



}











