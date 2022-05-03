package Tests;

import DualSpeciesIsolation.PulseGenerator;
import DualSpeciesIsolation.RepresentationViolation;
import DualSpeciesIsolation.Waveform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class Tests1 {


    @Test
    /*
     * Check the overall waveform against the MAc trigger system waveform
     */
    public void checkWave() {

        //MAc injectionStart = 54.98999 us


        double firstRise, firstFall, lastFall, temp;
        Waveform Rb = new Waveform(85, 60.0, 2000000, 2000000, 0.4, 22682.5);
        Waveform Cs = new Waveform(133, 200.0, 7000000, 7000000, 0.1, 22682.5);
        Waveform K = new Waveform(39, 50.0, 750000, 750000, 0.8, 22682.5);
        Waveform Rb2 = new Waveform(85, 30.0, 1000000, 1000000, 0.4, 22682.5);
        Waveform Cs2 = new Waveform(133, 30.0, 1500000, 1500000, 0.7, 22682.5);
        Waveform K2 = new Waveform(39, 30.0, 750000, 750000, 0.8, 22682.5);


        List<List<Double>> expectedTimings = new ArrayList<>();
        List<Double> RbTimings = new ArrayList<>();
        RbTimings.add(28040.0); //FirstRise  - InjectionStart
        RbTimings.add(33480.0); //FirstFall  - InjectionStart
        RbTimings.add(1112215.0); //Last Fall  - InjectionStart
        List<Double> CsTimings = new ArrayList<>();
        CsTimings.add(33375.0); //FirstRise  - InjectionStart problem
        CsTimings.add(43585.0); //FirstFall  - InjectionStart
        CsTimings.add(4579585.0); //Last Fall  - InjectionStart
        List<Double> KTimings = new ArrayList<>();
        KTimings.add(20220.0);  //FirstRise  - InjectionStart
        KTimings.add(21445.0);  //FirstFall  - InjectionStart
        KTimings.add(628810.0); //Last Fall  - InjectionStart
        List<Double> Rb2Timings = new ArrayList<>();
        Rb2Timings.add(28040.0); //FirstRise  - InjectionStart
        Rb2Timings.add(33480.0); //FirstFall  - InjectionStart
        Rb2Timings.add(568315.0); //Last Fall  - InjectionStart
        List<Double> Cs2Timings = new ArrayList<>();
        Cs2Timings.add(36780.0); //FirstRise  - InjectionStart
        Cs2Timings.add(40180.0); //FirstFall  - InjectionStart
        Cs2Timings.add(709240.0); //Last Fall  - InjectionStart
        List<Double> K2Timings = new ArrayList<>();
        K2Timings.add(20220.0); //FirstRise  - InjectionStart
        K2Timings.add(21445.0); //FirstFall  - InjectionStart
        K2Timings.add(383410.0); //Last Fall  - InjectionStart
        expectedTimings.add(RbTimings);
        expectedTimings.add(CsTimings);
        expectedTimings.add(KTimings);
        expectedTimings.add(Rb2Timings);
        expectedTimings.add(Cs2Timings);
        expectedTimings.add(K2Timings);

        List<List<Double>> testTimings = new ArrayList<>();
        testTimings.add(Rb.getTimings());
        testTimings.add(Cs.getTimings());
        testTimings.add(K.getTimings());
        testTimings.add(Rb2.getTimings());
        testTimings.add(Cs2.getTimings());
        testTimings.add(K2.getTimings());

        List<List<Integer>> testValues = new ArrayList<>();
        testValues.add(Rb.getWave());
        testValues.add(Cs.getWave());
        testValues.add(K.getWave());
        testValues.add(Rb2.getWave());
        testValues.add(Cs2.getWave());
        testValues.add(K2.getWave());



        for (int i = 0; i < testValues.size(); i++){
            firstRise = -1;
            firstFall = -1;
            lastFall = -1;
            temp = 0;



            for (int j = 0; j < testValues.get(i).size(); j++){ //using 0 timings not 1 timings

                if (firstRise == -1 && testValues.get(i).get(j) == 1){
                    if (j == 0){
                        firstRise = testTimings.get(i).get(j);
                    }
                    else {
                        firstRise = testTimings.get(i).get(j - 1);
                    }
                    temp = 1;
                }
                if (temp == 1 && testValues.get(i).get(j) == 0){
                    firstFall = testTimings.get(i).get(j);
                    temp = 0;
                }
                if(lastFall == -1 && testValues.get(i).get(testValues.get(i).size()-1-j) == 1){
                    if (j==0){
                        lastFall = testTimings.get(i).get(testTimings.get(i).size()-1-j);
                    }
                    else{
                    lastFall = testTimings.get(i).get(testTimings.get(i).size()-j);
                    }
                }
                if (firstRise != -1 && firstFall != -1 && lastFall != -1){
                    break;
                }
            }

            Assertions.assertEquals(expectedTimings.get(i).get(0), firstRise);
            Assertions.assertEquals(expectedTimings.get(i).get(1), firstFall);
            Assertions.assertEquals(expectedTimings.get(i).get(2), lastFall);

        }

    }



    @Test
    /*
     * Check the Second Smallest adjacency peak count to ensure algorithm is correct
     */
    public void checkSecondSmallest(){
        int[] temp = PulseGenerator
            .pulseScheme( 94, 66, 50, 0.4, 1040000, 1040000, 10, 22682.5);
        Assertions.assertEquals(59, temp[6]);
        Assertions.assertEquals(29504, temp[7]);
        Assertions.assertEquals(325134, temp[8]);
    }


    @Test
    /*
     * tests the checkRep functionality to ensure Rep invariant checks are effective
     */
    public void testCheckRep(){

        ArrayList<Integer> trial = new ArrayList<>();
        trial.add(1);
        trial.add(0);
        trial.add(25);

        try{

            Waveform trialWave = new Waveform(trial,5);

        } catch (RepresentationViolation e){

            e.printStackTrace();

        }

        Assertions.assertEquals(1,1);

    }


    @Test
    /*
     * Checks the resolution calculation and storage for a waveform
     */
    public void checkResolution(){
        Waveform Rb = new Waveform(85, 60.0, 2000000, 200000, 0.4, 22682.5);
        Assertions.assertEquals(10,Rb.getResolution());
    }

    @Test
    /*
     * Check that merging of waves is accounted for when waves extend past on time
     */
    public void checkCycleExtension(){

        double firstFall = -1;
        double firstRise2 = -1;
        double firstFall2 = -1;
        double lastFall = -1;
        double lastFall2 = -1;
        double firstRise = -1;
        int temp1 = 0;
        int temp2 = 0;

        Waveform waveA = new Waveform(133, 30.0, 1000000, 1000000, 0.4, 22682.5);
        Waveform waveB = new Waveform(39, 1000000, 1000000, 0.4, 22682.5*java.lang.Math.sqrt((133/132.905))*30, 22682.5);

        ArrayList<Double> waveATimings = waveA.getTimings();
        ArrayList<Double> waveBTimings = waveB.getTimings();
        ArrayList<Integer> waveAValues = waveA.getWave();
        ArrayList<Integer> waveBValues = waveB.getWave();

        for (int i = 0 ; i < waveATimings.size(); i++){

            if (firstRise == -1 && waveAValues.get(i) == 1){
                firstRise = waveATimings.get(i-1);
                temp1 = 1;
            }
            if (temp1 == 1 && waveAValues.get(i) == 0){
                firstFall = waveATimings.get(i);
                temp1 = 0;
            }
            if(lastFall == -1 && waveAValues.get(waveAValues.size()-1-i) == 1){
                if (i==0){
                    lastFall = waveATimings.get(waveATimings.size()-1);
                }
                else{
                    lastFall = waveATimings.get(waveATimings.size()-i);
                }
            }

            if (firstRise2 == -1 && waveBValues.get(i) == 1){
                firstRise2 = waveBTimings.get(i-1);
                temp2 = 1;
            }
            if (temp2 == 1 && waveBValues.get(i) == 0){
                firstFall2 = waveBTimings.get(i);
                temp2= 0;
            }
            if(lastFall2 == -1 && waveBValues.get(waveBValues.size()-1-i) == 1){
                if (i==0){
                    lastFall2 = waveBTimings.get(waveBTimings.size()-1);
                }
                else{
                    lastFall2 = waveBTimings.get(waveBTimings.size()-i);
                }
            }

            if (firstRise != -1 && firstFall != -1 && lastFall != -1 && firstRise2 != -1 && firstFall2 != -1 && lastFall2 != -1 ){
                break;
            }
        }

        Assertions.assertEquals(35075.0, firstRise);
        Assertions.assertEquals(41880.0, firstFall);
        Assertions.assertEquals(710940.0, lastFall);
        Assertions.assertEquals(18995.0, firstRise2);
        Assertions.assertEquals(22680.0, firstFall2);
        Assertions.assertEquals(698080.0, lastFall2);


    }

    @Test
    /*
     * Check the boundary of the peak identifier to ensure that peaks are being counted properly
     * Change 4537 constant to 4536 with an expectation of 0 for peaks
     */
    public void PulseSchemeTest(){

        Waveform waveA = new Waveform(133, 30.0, 1000000, 1000000, 0.4, 22682.5);
        ArrayList<Integer> bitList = waveA.getWave();

        int peaks = 0;
        if (bitList.get(bitList.size()-1) != bitList.get(bitList.size()-2)){
            peaks++;
        }
        int counter = 1;
        int value = bitList.get(0);
        for(int i = 0; i < bitList.size()-1; i++){

            if (bitList.get(i+1) == value){
                counter++;
            }
            else{
                if(counter < 4537){
                    peaks++;
                }
                counter = 1;
                value = bitList.get(i+1);
            }
        }


        Assertions.assertEquals(59, peaks);

    }

    @Test
    /*
     * Check the implementation of pulseScheme without knowledge of internal components (black box)
     */
    public void checkPeakGenerator(){

        int[] peaks = PulseGenerator.pulseScheme( 133, 133, 30.0, 0.4, 1000000, 1000000, 4537, 22682.5);
        Assertions.assertEquals(59, peaks[0]); //Borderline value

    }

    @Test
    /*
     * Check that the suggested time scale is adjusting according to the equation provided
     * so that it is providing a large enough window for the whole waveform
     */
    public void checkSuggestedTimeScale(){
        boolean correct = PulseGenerator.getSuggestedTimeScale(85, 60, 0.4, 22682.5) > 1112215;

        Assertions.assertTrue(correct);
    }

    @Test
    /*
     * Ensure that the normalizing factor for OnTime is calibrated to the counting scheme
     * where edges are counted as 0 or as indeterminate
     */
    public void checkNormalFactor(){
        int OnTime = PulseGenerator.normFactor(24, 30.00, 0.4, 22682.5);
        Assertions.assertEquals(173340, OnTime);
    }

    @Test
    public void checkIOIonTimes(){
        double Mass1 = 85;
        double Mass2 = 85;
        double IOI = 85;
        double MRSCycles = 30;
        double proportion = 0.4;
        double cycleCalib = 22682.5;
        double heavyMass = 85;

        int a = PulseGenerator.IOIWaveformOnTime(Mass1, Mass2, IOI, MRSCycles, proportion, PulseGenerator
            .getSuggestedTimeScale(heavyMass, MRSCycles, proportion, cycleCalib),PulseGenerator
            .getSuggestedTimeScale(heavyMass, MRSCycles, proportion, cycleCalib), cycleCalib);


        Assertions.assertEquals(0, a);
    }

//    public static void main(String[] args){
//        ArrayList<Double> a = getSingleMRSEnds(85, 30, 0.4, 22682.5);
//        System.out.println(a.size());
//        for(Double e: a){
//            System.out.println(e);
//        }
//    }


}