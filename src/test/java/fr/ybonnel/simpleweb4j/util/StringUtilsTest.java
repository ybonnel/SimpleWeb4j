package fr.ybonnel.simpleweb4j.util;

import org.junit.Assert;
import org.junit.Test;

/**
 */
public class StringUtilsTest {

    @Test
    public void testGetIndexOfBlank() {
        int index = StringUtils.getIndexOfBlank("test t");
        Assert.assertEquals(4, index);

        index = StringUtils.getIndexOfBlank("test\tt");
        Assert.assertEquals(4, index);

        index = StringUtils.getIndexOfBlank("test");
        Assert.assertEquals(-1, index);
    }
}
