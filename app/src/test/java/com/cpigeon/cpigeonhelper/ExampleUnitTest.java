package com.cpigeon.cpigeonhelper;

import com.cpigeon.cpigeonhelper.utils.CommonUitls;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        double s = 116.36;
        double s1  = 39.952556;
        System.out.println(CommonUitls.isAjLocation(s));
        System.out.println(CommonUitls.isAjLocation(s1));
    }
}