package Tests;

import DualSpeciesIsolation.DualMRSWaveformStatistics;
import DualSpeciesIsolation.SpecViolation;
import DualSpeciesIsolation.WaveGrapher;
import DualSpeciesIsolation.Waveform;

public class Main {

    public static void main(String[] args){


        WaveGrapher.getNormOnTime(88, 87, 30, 0.4);
        DualMRSWaveformStatistics.DualMRSMassScanner(20, 60, 80, 30, 0.4,10, false);



    }

}
