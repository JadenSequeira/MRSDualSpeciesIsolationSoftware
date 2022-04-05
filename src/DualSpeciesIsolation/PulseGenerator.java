package DualSpeciesIsolation;

import java.util.ArrayList;



public class PulseGenerator{

    /**
     * Time in nanoseconds for 1 Cs cycle; used as a calibrant
     */
    private final static double cesiumCycle = 22682.5; //in nanoseconds

    /**
     * @param mass mass of interest for overall timeScale that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @return the suggested timeScale setting
     */
    public static int getSuggestedTimeScale(int mass, double MRSCycles, double prop){

        double timeOn = cesiumCycle*java.lang.Math.sqrt((mass/132.905))*MRSCycles;
        double timeDelay = 5*(int)((((32800)*java.lang.Math.sqrt((mass/132.905))) - (5*(int)((prop*cesiumCycle*java.lang.Math.sqrt((mass/132.905))/2)/5)/2))/5);

        return (int) (timeOn + timeDelay + (timeOn/MRSCycles));
    }


    /**
     * @param mass The Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param proportion the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @return the expected total OnTime of the MRS waveform for the specified mass
     */
    public static int normFactor(double mass, double MRSCycles, double proportion){

        return (int) ((5*((int)(((1-proportion)*cesiumCycle*java.lang.Math.sqrt((mass/132.905))/2)/5)) - 1)*(MRSCycles*2));

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
     * @return List with: the total number of same bit segments that are smaller than or equal to the adjacency break,
     * the number of switches, the maximum and minimum of the peaks ( max/min of first value), the On Time of Waveform,
     * the minimum peak width, the second minimum peak width,  the first occurrence of minimum and second minimum peak widths,
     * and the percentage normalization of OnTime.
     */
    public static int[] pulseScheme( double MOI1, double MOI2, double MRSCycles, double prop, int timeScale, int steps, int adjacencyBreak){

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

        totalTime = cesiumCycle*java.lang.Math.sqrt((heavyMass/132.905))*MRSCycles;

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, prop);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, prop, totalTime);
        try {
            Waveform mainWave = new Waveform(waveA, waveB);
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
        pkSwt[9] = (int)((double)onTime*100/normFactor(heavyMass,MRSCycles, prop));



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
     * @return a list of the counts of each adjacent segment of same bits in the wave
     */
    public static ArrayList<Integer> adjacentLengths( double MOI1, double MOI2, double MRSCycles, double prop, int timeScale, int steps){

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

        totalTime = cesiumCycle*java.lang.Math.sqrt((heavyMass/132.905))*MRSCycles;

        Waveform waveA = new Waveform(heavyMass, MRSCycles, timeScale, steps, prop);
        Waveform waveB = new Waveform(lightMass, timeScale, steps, prop, totalTime);
        try{
            Waveform mainWave = new Waveform(waveA, waveB);
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
}








