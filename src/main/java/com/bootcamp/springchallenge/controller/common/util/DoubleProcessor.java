package com.bootcamp.springchallenge.controller.common.util;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleProcessor {
    public static boolean equalNullable(@Nullable Double d1, @Nullable Double d2) {
        if (d1 == null) return d2 == null;
        else if (d2 != null) return equal(d1, d2);
        return false;
    }

    public static boolean equal(double d1, double d2) {
        return Double.compare(d1, d2) == 0;
    }

    public static double roundTwoDecimals(double d) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
