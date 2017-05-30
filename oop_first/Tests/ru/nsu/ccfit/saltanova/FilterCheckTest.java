package ru.nsu.ccfit.saltanova;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FilterCheckTest {
    @Test

    public void check() throws Exception {
        String goodStringForAll = ".txt >0";
        String badStringforAnd = ".txt .cpp";
        File file = new File("Tests/ru/nsu/ccfit/saltanova/testDir/testfile.txt");

        AndFilter and = new AndFilter(goodStringForAll);
        AndFilter and2 = new AndFilter(badStringforAnd);
        OrFilter or = new OrFilter(goodStringForAll);
        OrFilter or2 = new OrFilter(badStringforAnd);

        Assert.assertTrue(and.check(file));
        Assert.assertFalse(and2.check(file));
        Assert.assertTrue(or.check(file));
        Assert.assertTrue(or2.check(file));
    }
}
