package DualSpeciesIsolation;

import javax.swing.JProgressBar;
import javax.swing.JTextField;
import java.awt.TextField;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WriteFile {


    /**
     * Writes data derived from the AND combination of two MRS waveforms to a specified file
     * @param writerA write data to specified file; non-null
     * @param mass1 non-null and greater than zero mass of interest
     * @param mass2 non-null and greater than zero second mass of interest
     * @param indetPeak the maximum amount of peaks when the edges of the waveform are not included; non-null and non-negative
     * @param peak the expected amount of peaks when the edges are counted only as Lo; non-null and non-negative
     * @param inclPeak the minimum amount of peaks when the edges are counted as Lo and Hi; non-null and non-negative
     * @param max the max mass that can be used; greater than zero and non-null
     * @param switches the number of Fall/Rise switches in the waveform
     * @param onTime the total time the waveform is on Hi; non-negative and non-null
     * @param minCount the smallest count of same adjacent values in the waveform; non-null and greater than zero
     * @param secondSmallest the second smallest count of same adjacent values in the waveform; non-null and greater than zero; greater than the smallest count
     * @param normOnTime the normalized onTime for the MRS waveform
     * @param normOnly if Normalized On Times is the only wanted data
     * @param field text field to display progress
     * @param progBar progress bar to display end of task
     */
    public static synchronized void writeToFile(FileWriter writerA, int mass1, int mass2, int indetPeak, int peak, int inclPeak, int max, int switches, int onTime, int minCount, int secondSmallest, int normOnTime, boolean normOnly, JTextField field, JProgressBar progBar){
        try {
           if (mass1 <= (max-5) && mass2 <= (max-5)) {
               if(!normOnly) {
                   writerA.write(
                       mass1 + "  " + mass2 + "   " + indetPeak + "   " + peak + "    " + inclPeak +
                           "    " + onTime + "  " + switches + "    " + minCount + "    " +
                           secondSmallest + "  " + normOnTime + "\n");
               }
               else{
                   writerA.write(mass1 + "  " + mass2 + "   " + normOnTime + "\n");
               }
               field.setText(mass1 + "   " + mass2);

           }
           else{
               field.setText(mass1 + "   " + mass2 + "  No value Recorded");
           }



            if (mass1 == max && mass2 == max){
                try{
                    TimeUnit.SECONDS.sleep(15);
                    writerA.close();
                    //System.exit(-1);
                    field.setText("Done!");
                    progBar.setIndeterminate(false);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }


}
