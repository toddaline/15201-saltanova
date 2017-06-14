package ru.nsu.ccfit.saltanova;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Statistics {

    Statistics() {
        totalLinesCount = 0;
        totalFilesCount = 0;
    }

    private int totalLinesCount;
    private int totalFilesCount;
    private Map<IFilter, StatisticData> statMap = new HashMap<>();
    private ArrayList<SortedStatisticData> sortedArray = new ArrayList<>();

    int getTotalLinesCount() {
        return totalLinesCount;
    }

    int getTotalFilesCount() { return totalFilesCount; }

    int getMapSize() {
        return statMap.size();
    }

    ArrayList<SortedStatisticData> getStatistics() { return sortedArray; }

    void addMatchingFile(IFilter filter, int linesCountInFile) {
        statMap.computeIfAbsent(filter, k -> new StatisticData());
        StatisticData stat = statMap.get(filter);
        stat.filesCount++;
        stat.linesCount += linesCountInFile;
    }

    int countLines(File fileName) {
        BufferedReader bufferedReader;
        int i = 0;
        try {
            FileReader fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            while(bufferedReader.readLine() != null) {
                i++;
            }
            bufferedReader.close();
        }
        catch(Exception e){
            System.exit(-1);
        }
        totalLinesCount+=i;
        return i;
    }

    void countFiles() {
        totalFilesCount++;
    }

    void printStatistics() {
        if (sortedArray.size() == 0) {
            sort();
        }
        System.out.println("Total - " + totalLinesCount + " lines in " + totalFilesCount + " files");
        System.out.println("------------");
        for (SortedStatisticData aSortedArray : sortedArray) {
            System.out.println(aSortedArray.filter.getFilter() + " - " + aSortedArray.linesCount + " line(s) in " + aSortedArray.filesCount + " file(s)");
        }
    }

    void sort() {
        for (Map.Entry<IFilter, StatisticData> entry : statMap.entrySet()) {
            SortedStatisticData stat = new SortedStatisticData();
            stat.filter = entry.getKey();
            stat.linesCount = entry.getValue().linesCount;
            stat.filesCount = entry.getValue().filesCount;
            sortedArray.add(stat);
        }
        sortedArray.sort((o1, o2) -> o2.linesCount - o1.linesCount);
    }

    class StatisticData {
        StatisticData() {
            linesCount = 0;
            filesCount = 0;
        }
        private int linesCount;
        private int filesCount;
    }

    class SortedStatisticData {
        SortedStatisticData() {
            filter = null;
            linesCount = 0;
            filesCount = 0;
        }
        IFilter filter;
        int linesCount;
        int filesCount;
    }
}
