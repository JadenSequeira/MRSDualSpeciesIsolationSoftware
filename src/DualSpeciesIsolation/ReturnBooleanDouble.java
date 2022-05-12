package DualSpeciesIsolation;

public class ReturnBooleanDouble {

    private Boolean Value;
    private double time;

    public ReturnBooleanDouble(Boolean value, double time){
        this.Value = value;
        this.time = time;
    }

    public ReturnBooleanDouble(ReturnBooleanDouble a){
        this.Value = a.getValue();
        this.time = a.getTime();
    }

    public ReturnBooleanDouble(){
        this.Value = false;
        this.time = 0;
    }

    public void changeBoleanDouble(Boolean value, double time){
        this.Value = value;
        this.time = time;
    }

    public Boolean getValue(){
        return Value;
    }

    public double getTime() {
        return time;
    }
}
