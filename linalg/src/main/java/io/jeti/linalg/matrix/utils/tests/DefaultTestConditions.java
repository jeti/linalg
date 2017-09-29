package io.jeti.linalg.matrix.utils.tests;

public class DefaultTestConditions {

    public static double getEqualityTolerance() {
        return 1e-8;
    }

    public static int getElemMin() {
        return 1;
    }

    public static int getElemMax() {
        return 10;
    }

    public static int getMinRows() {
        return 1;
    }

    public static int getMaxRows() {
        return 4;
    }

    public static int getMinCols() {
        return 1;
    }

    public static int getMaxCols() {
        return 10;
    }

    public static int getMinInner() {
        return getMinCols();
    }

    public static int getMaxInner() {
        return getMaxCols();
    }
}
