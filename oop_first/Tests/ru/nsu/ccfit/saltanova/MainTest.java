package ru.nsu.ccfit.saltanova;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class MainTest {
    @Test
    public void controllerTest() {
        try {
            Controller ctrl = new Controller("Tests\\ru\\nsu\\ccfit\\saltanova\\config.txt");
            ctrl.bypass("Tests\\ru\\nsu\\ccfit\\saltanova\\testDir");
            ctrl.getStatistics().sort();
            ArrayList<Statistics.SortedStatisticData> stats = ctrl.getStatistics().getStatistics();

            assertEquals(4, stats.size());

            assertEquals(new OrFilter(".cpp >0"), stats.get(0).filter);
            assertEquals(new ExtensionFilter("c"), stats.get(stats.size() - 1).filter);

            assertEquals(ctrl.getStatistics().getTotalLinesCount(), stats.get(0).linesCount);
            assertEquals(ctrl.getStatistics().getTotalFilesCount(), stats.get(0).filesCount);
            assertEquals(70, stats.get(1).linesCount);
            assertEquals(3, stats.get(1).filesCount);
            assertEquals(45, stats.get(2).linesCount);
            assertEquals(1, stats.get(2).filesCount);
            assertEquals(25, stats.get(3).linesCount);
            assertEquals(2, stats.get(3).filesCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}