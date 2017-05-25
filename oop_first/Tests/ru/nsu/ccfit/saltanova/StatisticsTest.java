package ru.nsu.ccfit.saltanova;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class StatisticsTest {

    @Test
    public void addMatchingFile() throws Exception {
        ExtensionFilter filter1 = new ExtensionFilter(".cpp");
        ExtensionFilter filter2 = new ExtensionFilter(".cpp");
        ExtensionFilter filter3 = new ExtensionFilter(".txt");
        Statistics stats = new Statistics();
        stats.addMatchingFile(filter1, 10);
        stats.addMatchingFile(filter2, 10);
        stats.addMatchingFile(filter3, 10);
        Assert.assertTrue(stats.getMapSize() == 2);
    }

    @Test
    public void countLines() throws Exception {
        File file1 = new File("Tests\\ru\\nsu\\ccfit\\saltanova\\testDir\\testfile.txt");
        File file2 = new File("Tests\\ru\\nsu\\ccfit\\saltanova\\testDir\\testfileLong.txt");
        Statistics stats = new Statistics();
        stats.countLines(file1);
        stats.countLines(file2);
        Assert.assertTrue(stats.getTotalLinesCount() == 20 + 140);
    }
}