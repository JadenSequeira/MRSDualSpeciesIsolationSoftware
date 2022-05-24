package DualSpeciesIsolation;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;


public class Waveform {


    /**
     * Waveform values (values are 0 or 1)
     */
    private final List<Integer> Wave;

    /**
     * Resolution of waveform - time between values
     */
    private final double Resolution;

    /**
     * List of timings for waveform's corresponding values
     */
    private final List<Double> timings;

    /*Abstraction Function:
    A 2D wave represented through digital values and times. Wave contains values of 1 corresponding to Hi/Blocking
    and 0 corresponding to Lo/Passing. Each value is separated by a specific time interval in the nanoseconds
    calculated given the resolution. Timings contains the corresponding time (in nanoseconds) for each value of the wave.
     */



    /*Rep Invariant:
    Wave only contains 0's or 1's and is non-null. Each wave has a corresponding timing.
    Timings are in ascending order and all timings are unique.
     */

    /**
     * Check the Rep Invariant is met
     */
    private Boolean checkRep(){

        double difference;

        if(Wave.size() != timings.size()){
            return false;
        }

        difference = timings.get(1) - timings.get(0);
        if (difference != Resolution){
            return false;
        }

        for (int i = 0; i < Wave.size(); i++){
            if (i != 1 && i != 0){
                return false;
            }
        }

        for (int i = 1; i < Wave.size()-1; i++){
            if (timings.get(i+1) - timings.get(i) != difference){
                return false;
            }
        }

        return true;
    }

    /**
     * Constructs an MRS wave given an ion mass and scaling details
     * @param MOI Mass of Interest that is non zero and non negative
     * @param MRSCycles Number of MRS Cycles/duty cycles for the wave; non-zero and non-negative, <= 850
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     *              and therefore sets the resolution; greater than zero
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param cycleCalib time for 1 Cs 1333 cycle in ns; greater than zero
     */
    public Waveform(double MOI, double MRSCycles, int timeScale, int steps, double prop, double cycleCalib, double MRSStartCycle){
        timings = new ArrayList<>();
        Wave = new ArrayList<>(waveGenerator(MOI, MRSCycles, timeScale, steps, prop, cycleCalib, MRSStartCycle, 0));
        Resolution = timings.get(1)-timings.get(0);
    }

    /**
     * Constructs a wave using time MRS is ON and Mass of Interest
     * @param MOI Mass of Interest that is non zero and non negative
     * @param timeScale the time window for the wave in nanoseconds; greater than zero
     * @param steps The number of steps plus 1 sets the number of data points
     *            and therefore sets the resolution; greater than zero
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeOn the time the MRS is ON in nanoseconds; greater than 0
     * @param cycleCalibration time for 1 Cs 1333 cycle in ns; greater than zero
     */
    public Waveform(double MOI, int timeScale, int steps, double prop, double timeOn, double cycleCalibration, double MRSStartTime){
        timings = new ArrayList<>();
        double MRSCycles = (timeOn/(cycleCalibration*java.lang.Math.sqrt((MOI/132.905))));
        Wave = new ArrayList<>(waveGenerator(MOI, MRSCycles, timeScale, steps, prop, cycleCalibration, 0, MRSStartTime));
        Resolution = timings.get(1)-timings.get(0);
    }


    /**
     * Constructs a blank wave
     */
    public Waveform(){
        Wave = new ArrayList<>();
        timings = new ArrayList<>();
        Resolution = 0;
    }

    /**
     * Constructs a copy of a wave
     * @param Wave an instance of DualSpeciesIsolation.Waveform used for new wave copy and is non-null
     */
    public Waveform(Waveform Wave){
        this.Wave = Wave.getWave();
        this.timings = Wave.getTimings();
        this.Resolution = Wave.getResolution();

    }

    /**
     * Constructs a wave given digital values
     * @param digitalValues values are 0's or 1's
     * @param Resolution time between each value in the list
     */
    public Waveform(ArrayList<Integer> digitalValues, double Resolution) throws RepresentationViolation{
        this.Wave = new ArrayList<>(digitalValues);
        this.timings = new ArrayList<>();

        for (int i = 0; i < digitalValues.size(); i++){
            timings.add(Resolution*i);
        }

        this.Resolution = Resolution;

        if(!checkRep()){
            throw new RepresentationViolation("The representation invariant has been violated.");
        }

    }



    /**
     * Constructs a new waveform through the AND combination of two waveforms
     * wave 1 and wave 2 must be the same size with same timings for each value
     * @param wave1 wave that is non-null
     * @param wave2 wave that is non-null
     * @param XOR conducts a specialized XOR gate where the wave 1 is a combined MRS and wave 2 is an IOI MRS
     * @throws SpecViolation if timings are not the same size for AND gate
     */
    public Waveform( Waveform wave1, Waveform wave2, Boolean XOR) throws SpecViolation{

        ArrayList<Double> wave1Timings = new ArrayList<>(wave1.getTimings());
        ArrayList<Double> wave2Timings = new ArrayList<>(wave2.getTimings());
        Boolean wave1Larger = false;

        if (wave1Timings.size() >= wave2Timings.size()) {
            this.timings = wave1.getTimings();
            wave1Larger = true;
        }
        else{
            this.timings = wave2.getTimings();
        }


        ArrayList<Integer> waveA = new ArrayList<>(wave1.getWave());
        ArrayList<Integer> waveB = new ArrayList<>(wave2.getWave());
        this.Wave = new ArrayList<>();

        if (XOR){
            if (!wave1Timings.get(0).equals(wave2Timings.get(0)) || wave1.getResolution() != wave2.getResolution()){
                throw new SpecViolation("Timings Do Not Align");
            }

            int size;
            if(wave1Larger){
                size = wave2Timings.size();
            } else{
                size = wave1Timings.size();
            }

            for (int index = 0; index < size; index++) {
                if (waveA.get(index) == 1 && waveB.get(index) == 0){
                    this.Wave.add(1);
                } else {
                    this.Wave.add(0);
                }
            }

            if(wave1Larger){
                for (int i = size; i < wave1Timings.size(); i++){
                    this.Wave.add(waveA.get(i));
                }
            } else {
                for (int i = size; i < wave2Timings.size(); i++){
                    this.Wave.add(0);
                }
            }
        }
        else {

            if (wave1Timings.size() != wave2Timings.size() || !wave1Timings.get(0).equals(wave2Timings.get(0)) || wave1.getResolution() != wave2.getResolution()){
             //   System.out.println(wave1Timings.size() + " " + wave2Timings.size() + " " + wave1Timings.get(0) + " " + wave2Timings.get(0) + " " + wave1.getResolution() + " " + wave2.getResolution());
                throw new SpecViolation("Timings Do Not Match");
            }

            for (int index = 0; index < waveA.size(); index++) {

            //    System.out.println(waveA.size() + " " + waveB.size() + " " + wave1Timings.size() + " " + wave2Timings.size());
              //  exit(0);

                this.Wave.add(waveA.get(index) & waveB.get(index));

            }
        }

        this.Resolution = timings.get(1)-timings.get(0);

    }



    /**
     * Generates Digital DualSpeciesIsolation.Waveform
     * @param MOI Mass of Interest that is non zero and non negative
     * @param MRSCycles Number of MRS Cycles/duty cycles for the wave; non-zero and non-negative, <= 850
     * @param timeScale the time window for the wave in nanoseconds; greater than zero
     * @param steps The number of steps plus 1 sets the number of data points
     *             and therefore sets the resolution; greater than zero
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param cycleCalibrationTime time for 1 Cs 1333 cycle in ns; greater than zero
     * @return the list containing the digital signal in 0's and 1's, where each successive element represents the value
     * at a specific time (constant spacing)
     */
    private ArrayList<Integer> waveGenerator(double MOI, double MRSCycles, double timeScale, double steps, double prop, double cycleCalibrationTime, double startCycle, double startTime){

        int countera = 0;
        int counterb = 0;
        int counterc = 0;
        int counterd = 0;
        double tempstate = 0;



        ArrayList<Integer> wave = new ArrayList<>();
        double cycleCalibration = cycleCalibrationTime*java.lang.Math.sqrt((MOI/132.905));
        double timeOn = cycleCalibration*MRSCycles;

        Boolean extend = false;
        Boolean prevValue = false;
        double prevState = 0;
        Boolean tempval = false;


        double startB = 0; //For point placement of where TON starts
        if (startCycle != 0 ){
            startB += 5 * (int) ((((32800) * java.lang.Math.sqrt((MOI / 132.905))) -
                (5 * (int) ((prop * cycleCalibration / 2) / 5) / 2) + (startCycle * cycleCalibration)) / 5);
        }
        else if (startTime != 0){
            startB += startTime;
        } else {
            startB += 5 * (int) ((((32800) * java.lang.Math.sqrt((MOI / 132.905))) -
                (5 * (int) ((prop * cycleCalibration / 2) / 5) / 2)) / 5);
        }

        double startA = 0; //For window start
        if (startCycle != 0 ){
            startA += 5 * (int) ((((32800) * java.lang.Math.sqrt((MOI / 132.905))) -
                (5 * (int) ((prop * cycleCalibration / 2) / 5) / 2) + (startCycle * cycleCalibration)) / 5);
        }
        else if (startTime != 0){
            startA += startTime;
        } else{
            startA += 0;
        }

        //TODO: Change previous state
        if (waveFormula(timeOn + startB, MOI, MRSCycles, prop, false, cycleCalibrationTime, startA, 0, false, startCycle).getValue()){
            extend = true;
        }

        Boolean a = true;

        for(double i = startA; i < timeScale + startA; i = i + (int)(timeScale/steps)){
            timings.add(i);
            //if (tempstate != prevState) {
            //    System.out.println(prevState);
            //}
          //  if(tempval != prevValue){
          //       System.out.println(prevState + "prev: " + prevValue);
          //  }

            tempval = prevValue;
            tempstate = prevState;
            if (!extend) {

                ReturnBooleanDouble answer = new ReturnBooleanDouble(waveFormula(i, MOI, MRSCycles, prop, false, cycleCalibrationTime, startA, prevState, prevValue, startCycle));
                prevState = answer.getTime();
                prevValue = answer.getValue();


                if (prevValue) {
                    wave.add(1);
                } else {
                    wave.add(0);
                }

                countera++;
            }
            else{

                if (i < timeOn + startB || i > + timeOn + startB + 5*(int)(((1-prop)*cycleCalibration/2)/5)){

                    ReturnBooleanDouble answer1 = new ReturnBooleanDouble(waveFormula(i, MOI, MRSCycles, prop, false, cycleCalibrationTime, startA, prevState, prevValue, startCycle));
                    prevState = answer1.getTime();
                    prevValue = answer1.getValue();

                    if (prevValue) {
                        wave.add(1);
                    } else {
                        wave.add(0);
                    }
                    counterb++;

                }
                else if (i > timeOn + startB && !waveFormula(i, MOI, MRSCycles, prop, extend, cycleCalibrationTime, startA, prevState, prevValue, startCycle).getValue()){
                    ReturnBooleanDouble answer2 = new ReturnBooleanDouble(waveFormula(i, MOI, MRSCycles, prop, extend, cycleCalibrationTime, startA, prevState, prevValue, startCycle));
                    prevState = answer2.getTime();
                    prevValue = answer2.getValue();
//                    if (!prevValue){
                        extend = false;
                        wave.add(0);
//                    }

                    counterc++;
                }
                else{
                    ReturnBooleanDouble answer3 = new ReturnBooleanDouble(waveFormula(i, MOI, MRSCycles, prop, extend, cycleCalibrationTime, startA, prevState, prevValue, startCycle));
                    prevState = answer3.getTime();
                    prevValue = answer3.getValue();
                    if (prevValue) {
                        wave.add(1);
                    } else {
                        wave.add(0);
                    }
                    counterd++;
                }
            }
        }

     //   System.out.println(countera + " " + counterb + " " + counterc + " " + counterd);

        return wave;
    }

    /**
     * Determines the digital value of a wave at a given time
     * @param time the time in nanoseconds at which the digital value of the wave is being obtained
     * @param MOI Mass of Interest that is non zero and non negative
     * @param MRSCycles Number of MRS Cycles/duty cycles for the wave; non-zero and non-negative, <= 850
     * @param prop the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param cycleCalibrationTime time for 1 Cs 1333 cycle in ns; greater than zero
     * @return the digital value of the wave; true represents Hi and false represents Lo
     */
    private ReturnBooleanDouble waveFormula(double time, double MOI, double MRSCycles, double prop, Boolean extend, double cycleCalibrationTime, double start, double prevState, Boolean Val, double startCycle){

        double timeCounter = 0;
        double cycleCalibration = cycleCalibrationTime*java.lang.Math.sqrt((MOI/132.905));
        double timeOn = cycleCalibration*MRSCycles;
        double timeDelay = 5*(int)((((32800)*java.lang.Math.sqrt((MOI/132.905))) - (5*(int)((prop*cycleCalibration/2)/5)/2))/5);
        ReturnBooleanDouble info = new ReturnBooleanDouble();
        double startTime = 0;

        if (start == 0){
            startTime += timeDelay;
        }
        else{
            startTime += start;
        }



        if (extend){
            if (time <= startTime) {
            return info;
            }
            if (time >= timeOn + startTime + 5*(int)(((1-prop)*cycleCalibration/2)/5)){
                info.changeBoleanDouble(false,timeOn + startTime + 5*(int)(((1-prop)*cycleCalibration/2)/5)); //TODO: Change PREV Time returns to ensure final edge is met
                return info;
            }
        }

        if (!extend){
            if (time <= startTime){
            return info;
            }
            if (time > timeOn + startTime){
                info.changeBoleanDouble(false,timeOn + startTime);
                return info;
            }
        }



        if (prevState == 0) {

            if (startCycle != 0){
               timeCounter += 5 * (int) ((((32800) * java.lang.Math.sqrt((MOI / 132.905))) -
                    (5 * (int) ((prop * cycleCalibration / 2) / 5) / 2) + (startCycle * cycleCalibration)) / 5);
            } else {
                timeCounter += timeDelay;
            }
        }
        else{
            timeCounter = prevState;
        }


        Boolean changed = false;

        while (timeCounter < time){

            if (!Val || changed) {
                timeCounter += 5 * (int) ((prop * cycleCalibration / 2) / 5);
                if (timeCounter >= time) {
                    info.changeBoleanDouble(false,
                        timeCounter - 5 * (int) ((prop * cycleCalibration / 2) / 5));
                    return info;
                }
                changed = true;

            }

            if (Val || changed) {
                timeCounter += 5 * (int) (((1 - prop) * cycleCalibration / 2) / 5);
                if (timeCounter > time) {
                    info.changeBoleanDouble(true,
                        timeCounter - 5 * (int) (((1 - prop) * cycleCalibration / 2) / 5));
                    return info;
                }
                changed = true;
            }

            if (timeCounter == time){
                info.changeBoleanDouble(false,timeCounter);
                return info;
            }

        }


        return info;
    }


    /**
     *
     * @return a list containing the digital values of the wave
     */
    public ArrayList<Integer> getWave(){
        return new ArrayList<>(Wave);
    }

    /**
     *
     * @return a list containing all the timings in ascending order
     */
    public ArrayList<Double> getTimings(){
        return new ArrayList<>(timings);
    }

    /**
     *
     * @return the resolution of the MRS waveform
     */
    public double getResolution(){
        return Resolution;
    }

    @Override
    public boolean equals(Object O) {
        if (O == this) {
            return true;
        }
        if (!(O instanceof Waveform)) {
            return false;
        }

        Waveform wave  = (Waveform) O;

        if (wave.getResolution() != this.Resolution){
            return false;
        }

        if (wave.getWave().size() != this.getWave().size()){
            return false;
        }
        if (wave.getTimings().size() != this.getTimings().size()){
            return false;
        }
        if (this.checkRep() && wave.checkRep()){
            return false;
        }

        ArrayList<Integer> thisWaveVals = new ArrayList<>(this.getWave());
        ArrayList<Integer> otherWaveVals = new ArrayList<>(wave.getWave());
        for (int  i = 0; i < thisWaveVals.size(); i++) {
            if (thisWaveVals.get(i) != otherWaveVals.get(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        ArrayList<Integer> thisWaveVals = new ArrayList<>(this.getWave());
        int i = 0;
        while (thisWaveVals.get(i) == 0){
            i++;
        }
        return i;
    }

}

