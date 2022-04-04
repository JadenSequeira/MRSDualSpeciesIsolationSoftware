package DualSpeciesIsolation;

import java.io.FileWriter;

class Grapher  implements Runnable{

    /**
     * Mass of interest 1
     */
    private final int i;

    /**
     * Mass of interest 2
     */
    private final int j;

    /**
     * Maximum mass
     */
    private final int max;

    /**
     * non-null file to write to
     */
    private final FileWriter writerA;

    /**
     * Number of MRSCycles
     */
    private final double MRSCycles;

    /**
     * Proportion of Off/(Off+ON) time for MRS duty cycle
     */
    private final double proportional;

    /**
     * time window size for waveform
     */
    private final int timeScale;

    /**
     * steps to control resolution of waveform
     */
    private final int steps;

    /**
     * Minimum acceptable amount of same adjacent values in waveform
     */
    private final int adjacencyBreak;

    /**
     * True if Normalized On Times is the only wanted parameter
     */
    private final boolean normOnly;


    /**
     * @param i The first Mass of Interest that is non-null and greater than 0
     * @param j The second Mass of Interest that is non-null and greater than 0
     * @param MRSCycles The number of MRSCycles; 0 < MRSCycles <= 850
     * @param proportional the percentage (in decimal) the duty cycle is OFF, 0 <= prop <= 1
     * @param timeScale The time window for the wave in nanoseconds
     * @param steps The number of steps plus 1 sets the number of data points
     * and therefore sets the resolution; steps must be greater than zero
     * @param writerA writes mass pair data to specified file
     * @param max The maximum mass that can be used in the specified instance of Grapher
     */
     public Grapher(int i, int j, double MRSCycles, double proportional, int timeScale, int steps, int adjacencyBreak, FileWriter writerA, int max, boolean normOnly){
         this.i = i;
         this.j = j;
         this.MRSCycles = MRSCycles;
         this.proportional = proportional;
         this.timeScale = timeScale;
         this.steps = steps;
         this.adjacencyBreak = adjacencyBreak;
         this.writerA = writerA;
         this.max = max;
         this.normOnly = normOnly;
     }

     @Override
     /**
      * Writes mass pair data to file
      */
     public void run() {

         int[] temp = PulseGenerator
             .pulseScheme( i, j, MRSCycles, proportional, timeScale, steps, adjacencyBreak);


         synchronized(this){
             WriteFile.writeToFile(writerA,i,j,temp[2],temp[0],temp[3], max, temp[1], temp[4], temp[5], temp[6], temp[9], normOnly);
         }
     }
 }

