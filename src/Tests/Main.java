package Tests;

import DualSpeciesIsolation.DualMRSWaveformStatistics;
import DualSpeciesIsolation.WaveGrapher;

public class Main {

    public static void main(String[] args){


        System.out.println(WaveGrapher.getNormOnTime(88,87,30,0.4));
        DualMRSWaveformStatistics.DualMRSMassScanner(20, 60, 80, 30, 0.4,10, false);


    }

}
