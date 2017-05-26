package ru.nsu.ccfit.saltanova;

import java.io.File;
import java.nio.file.NoSuchFileException;

public class Controller {

    private Statistics stats;
    private IFilter[] filters;

    public Controller(String fileName) throws Exception {
        if (fileName == null) {
            throw new NoSuchFileException("No config file");
        }
        filters = Parser.parse_method(fileName);
        stats = new Statistics();
    }

    public void findCorrect(File file) {
        stats.countFiles();
        boolean flag = false;
        int linesCount = 0;
        for (IFilter filter : filters) {
            if (filter.check(file)) {
                if (!flag) {
                    linesCount = stats.countLines(file);
                    flag = true;
                }
                stats.addMatchingFile(filter, linesCount);
            }
        }
    }

    public void bypass(String dirPath) throws NoSuchFileException {
        File file = new File(dirPath);
        String[] dirList = file.list();
        if (dirList == null) {
            throw new NoSuchFileException("No such directory: " + dirPath);
        }
        int i;
        for (i = 0; i < dirList.length; i++)
        {
            File f1 = new File(dirPath + File.separator + dirList[i]);
            if(f1.isFile()) {
                findCorrect(f1);
            }
            else {
                bypass(dirPath + File.separator + dirList[i]);
            }
        }
    }

    public void printStatistic() {
        stats.printStatistics();
    }
    public Statistics getStatistics() {
        return stats;
    }
}
