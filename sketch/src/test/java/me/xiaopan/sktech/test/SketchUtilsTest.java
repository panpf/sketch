package me.xiaopan.sktech.test;

import org.junit.Assert;
import org.junit.Test;

import me.xiaopan.sketch.util.SketchUtils;

public class SketchUtilsTest {
    @Test
    public void testConcat(){
        Assert.assertEquals(SketchUtils.concat("1", "-", "2"), "1-2");
        Assert.assertEquals(SketchUtils.concat("1"), "1");
    }
}
