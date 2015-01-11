package org.apache.commons.lang3;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testTimestamp() {
        long currentTimestamp = System.currentTimeMillis();
        long anotherTimestamp = currentTimestamp + 5347;

        String currentTimestampAsString = Long.toString(currentTimestamp);
        String anotherTimestampAsString = Long.toString(anotherTimestamp);

        System.out.println(currentTimestampAsString);
        System.out.println(anotherTimestampAsString);
        System.out.println("Levensthein: " + StringUtils.getLevenshteinDistance(currentTimestampAsString, anotherTimestampAsString));
        System.out.println("JaroWinkler: " + StringUtils.getJaroWinklerDistance(currentTimestampAsString, anotherTimestampAsString));
    }

}