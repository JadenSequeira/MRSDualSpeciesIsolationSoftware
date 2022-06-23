package DualSpeciesIsolation;

public class ReturnBooleanDouble {

    /**
     * A value of Hi or Lo (true of false)
     */
    private Boolean Value;

    /**
     * The corresponding time for the value
     */
    private double time;

    /*Abstraction Function:
    A time in nanoseconds and its corresponding value in a Digital Waveform
    */

    /*Representation Invariant:
    The time must always be greater or equal to zero, the value can either by Hi or Lo. Must be non-null.
     */

    private Boolean checkRep(){
        if (time >= 0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Constructs a value and Boolean pair
     * @param value A value of Hi or Lo (true of false)
     * @param time The corresponding time for the value in nanoseconds; must be greater equal to zero
     */
    public ReturnBooleanDouble(Boolean value, double time){
        this.Value = value;
        this.time = time;
    }

    /**
     * Constructs a value and boolean pair from another pair (copy)
     * @param a a value and boolean pair; must be non-null
     */
    public ReturnBooleanDouble(ReturnBooleanDouble a){
        this.Value = a.getValue();
        this.time = a.getTime();
    }

    /**
     * Construct a blank time-value pair
     */
    public ReturnBooleanDouble(){
        this.Value = false;
        this.time = 0;
    }

    /**
     * Change the value and time of the time-boolean pair
     * @param value A value of Hi or Lo (true of false)
     * @param time The corresponding time for the value in nanoseconds; must be greater equal to zero
     */
    public void changeBoleanDouble(Boolean value, double time){
        this.Value = value;
        this.time = time;
    }

    /**
     * @return value of time-value pair
     */
    public Boolean getValue(){
        return Value;
    }

    /**
     * @return time in nanoseconds of time-value pair
     */
    public double getTime() {
        return time;
    }

    @Override
    public boolean equals(Object O) {
        if (O == this) {
            return true;
        }
        if (!(O instanceof ReturnBooleanDouble)) {
            return false;
        }

        ReturnBooleanDouble pair  = (ReturnBooleanDouble) O;

        if(pair.getTime() == time && pair.getValue() == this.Value){
            return true;
        } else {
            return false;
        }

    }

    @Override
    public int hashCode(){
        int j = 1;
        if(!Value){
            j = 0;
        }
        return ((int)time*j) + (int)time;
    }

}
