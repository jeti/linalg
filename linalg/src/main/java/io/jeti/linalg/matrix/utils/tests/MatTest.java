package io.jeti.linalg.matrix.utils.tests;

import io.jeti.linalg.matrix.Mat;
import io.jeti.linalg.matrix.Mat.Filler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

abstract public class MatTest {

    @org.junit.Test
    public void newInstance() throws Exception {
        forAllSizes((base, rows, cols) -> {

            /*
             * Construct vectors of zeros using all of the available methods
             */
            List<List<Integer>> zerosList = generateList(rows, cols, (r, c) -> 0);
            Integer[][] zerosArray = new Integer[rows][cols];
            for (int r = 0; r < rows; r++) {
                zerosArray[r] = zerosList.get(r).toArray(new Integer[0]);
            }
            assertEquals(base.newInstance(rows, cols), (r, c) -> 0);
            assertEquals(base.newInstance(rows, cols, 0), (r, c) -> 0);
            assertEquals(base.newInstance(rows, cols, (r, c) -> 0), (r, c) -> 0);
            assertEquals(base.zeros(rows, cols), (r, c) -> 0);
            assertEquals(base.newInstance(zerosList), (r, c) -> 0);
            assertEquals(base.newInstance(zerosArray), (r, c) -> 0);

            /*
             * Construct vectors of ones using all of the available methods
             */
            List<List<Integer>> onesList = generateList(rows, cols, (r, c) -> 1);
            Integer[][] onesArray = new Integer[rows][cols];
            for (int r = 0; r < rows; r++) {
                onesArray[r] = onesList.get(r).toArray(new Integer[0]);
            }

            assertEquals(base.newInstance(rows, cols, 1), (r, c) -> 1);
            assertEquals(base.newInstance(rows, cols, (r, c) -> 1.0), (r, c) -> 1);
            assertEquals(base.ones(rows, cols), (r, c) -> 1);
            assertEquals(base.newInstance(onesList), (r, c) -> 1);
            assertEquals(base.newInstance(onesArray), (r, c) -> 1);

            /*
             * Make some counting vectors (0,1,2,...) using all of the available
             * methods
             */
            List<List<Integer>> countingList = generateList(rows, cols,
                    (r, c) -> index(r, c, rows));
            Integer[][] countingArray = new Integer[rows][cols];
            for (int r = 0; r < rows; r++) {
                countingArray[r] = countingList.get(r).toArray(new Integer[0]);
            }

            assertEquals(base.newInstance(rows, cols, (r, c) -> index(r, c, rows)),
                    (r, c) -> index(r, c, rows));
            assertEquals(base.newInstance(countingList), (r, c) -> index(r, c, rows));
            assertEquals(base.newInstance(countingArray), (r, c) -> index(r, c, rows));
        });
    }

    /**
     * All we are going to do here is to check that they are the correct sizes
     */
    @org.junit.Test
    public void rand() throws Exception {
        forAllSizes((base, rows, cols) -> {
            TestCase.assertEquals(base.rand(rows, cols).rows(), rows);
            TestCase.assertEquals(base.rand(rows, cols).cols(), cols);
        });
    }

    /**
     * All we are going to do here is to check that they are the correct sizes
     */
    @org.junit.Test
    public void randn() throws Exception {
        forAllSizes((base, rows, cols) -> {
            TestCase.assertEquals(base.randn(rows, cols).rows(), rows);
            TestCase.assertEquals(base.randn(rows, cols).cols(), cols);
        });
    }

    /**
     * Create a random matrix, then a shallow list copy. Make sure that both
     * have the same contents, then try to clear the list, and make sure that it
     * is unchanged.
     */
    @org.junit.Test
    public void toList() throws Exception {
        forAllSizes((base, rows, cols) -> {

            Mat mat = base.randn(rows, cols);
            List<List<Double>> list = mat.toList();
            List<List<Double>> listCopy = mat.toList();

            assertEquals(mat, (r, c) -> list.get(r).get(c));
            list.clear();

            TestCase.assertEquals(listCopy.size(), rows);
            assertEquals(mat, (r, c) -> listCopy.get(r).get(c));
        });
    }

    /**
     * Check that the size function returns the same value as the value we
     * construct the matrix with.
     */
    @org.junit.Test
    public void size() throws Exception {
        forAllSizes((base, rows, cols) -> {

            /*
             * Construct vectors of zeros using all of the available methods
             */
            List<List<Integer>> onesList = generateList(rows, cols, (r, c) -> 1);
            Integer[][] onesArray = new Integer[rows][cols];
            for (int r = 0; r < rows; r++) {
                onesArray[r] = onesList.get(r).toArray(new Integer[0]);
            }

            Set<Mat> mats = new HashSet<>();
            mats.add(base.newInstance(rows, cols, 0));
            mats.add(base.newInstance(rows, cols, (row, col) -> 0));
            mats.add(base.zeros(rows, cols));
            mats.add(base.ones(rows, cols));
            mats.add(base.rand(rows, cols));
            mats.add(base.randn(rows, cols));
            mats.add(base.newInstance(onesList));
            mats.add(base.newInstance(onesArray));
            for (Mat mat : mats) {
                TestCase.assertTrue(mat.rows() == rows);
                TestCase.assertTrue(mat.cols() == cols);
            }
        });
    }

    /**
     * Create counting matrices {0,1,2,...} and make sure that the get function
     * returns the index, that is,
     * 
     * <pre>
     * <code>
     * mat.get(r,c) == r + rows * c 
     * </code>
     * </pre>
     */
    @org.junit.Test
    public void get() throws Exception {
        forAllSizes((base, rows, cols) -> {
            assertEquals(base.newInstance(rows, cols, (r, c) -> index(r, c, rows)),
                    (r, c) -> index(r, c, rows));
        });
    }

    /**
     * Try to get a view of the underlying matrix. Since this method should
     * return a view, not a copy, we check that we are getting the same
     * instances. Furthermore, we want to check that views of views are working
     * properly. We do this by creating a counting matrix and then grabbing
     * submatrices from it until there is only one element left.
     */
    @org.junit.Test
    public void getSelection() throws Exception {
        forAllSizes((base, rows, cols) -> {

            Mat mat = base.randn(rows, cols);
            Mat submatrix = mat.get(0, mat.rows(), 0, mat.cols());
            int its = 0;

            while (submatrix.rows() > 0 && submatrix.cols() > 0) {

                /* Check that slicing returns a view */
                final int fits = its;
                assertSame(submatrix, (r, c) -> mat.get(r + fits, c + 2 * fits));

                /* Slice until there is nothing left. */
                if (submatrix.rows() > 1 && submatrix.cols() > 2) {
                    its++;
                    submatrix = submatrix.get(1, submatrix.rows(), 2, submatrix.cols());
                } else {
                    break;
                }
            }
        });
    }

    /**
     * Try to get a view of the underlying vector with strides. Since this
     * method should return a view, not a copy, we check that we are getting the
     * same instances. Furthermore, we want to check that views of views are
     * working properly. We do this by creating a vector {0,1,2,3,...9}, then
     * grabbing subvectors {0,2,4,...}, until there is only one element left.
     * TODO: calculate the general formula so that we could use an arbitrary
     * stride and offset.
     */
    @org.junit.Test
    public void getWithStride() throws Exception {

        int rowStride = 2;
        int colStride = 3;

        forAllSizes((base, rows, cols) -> {

            Mat mat = base.randn(rows, cols);
            Mat submatrix = mat.get(0, mat.rows(), 0, mat.cols());
            int its = 0;

            while (submatrix.rows() > 0 && submatrix.cols() > 0) {

                /* Check that slicing returns a view */
                final int fits = its;
                assertSame(submatrix, (r, c) -> mat.get(r * (int) Math.pow(rowStride, fits),
                        c * (int) Math.pow(colStride, fits)));

                /* Slice until there is nothing left. */
                if (submatrix.rows() >= rowStride && submatrix.cols() >= colStride) {
                    its++;
                    submatrix = submatrix.get(0, submatrix.rows(), rowStride, 0, submatrix.cols(),
                            colStride);
                } else {
                    break;
                }
            }
        });
    }

    /** Apply the elementwise operation a * b + b. */
    @org.junit.Test
    public void applyMatrix() throws Exception {

        forAllSizes((base, rows, cols) -> {

            /* Only run the tests for square matrices. */
            if (rows != cols)
                return;

            /* Create matrices of ones, zeros, and counting */
            Mat eye = base.I(rows);
            Mat zeros = base.zeros(rows, rows);
            Mat ones = base.ones(rows, rows);
            Mat counting = base.newInstance(rows, rows, (r, c) -> r + rows * c);
            Mat randn = base.randn(rows, rows);

            /* All of these should be zero */
            assertEquals(zeros.apply((a, b) -> a * b + b, zeros), (r, c) -> 0.0);
            assertEquals(ones.apply((a, b) -> a * b + b, zeros), (r, c) -> 0.0);
            assertEquals(counting.apply((a, b) -> a * b + b, zeros), (r, c) -> 0.0);

            /* This should give back the same thing */
            assertEquals(zeros.apply((a, b) -> a * b + b, randn), (r, c) -> randn.get(r, c));

            /*
             * Remember that this operation is elementwise... This is not a
             * matrix product here.
             */
            assertEquals(randn.apply((a, b) -> a * b + b, eye),
                    (r, c) -> (r == c ? randn.get(r, c) + 1 : 0));
        });
    }

    @org.junit.Test
    public void plus() throws Exception {
        forAllSizes((base, rows, cols) -> {

            Mat zero = base.newInstance(1);
            Mat rand = base.rand(rows, cols);
            Mat randn = base.randn(rows, cols);

            /* TODO, why do we need the cast here? */
            Mat sum1 = (Mat) rand.plus(randn);
            Mat sum2 = (Mat) randn.plus(rand);
            Mat sum3 = (Mat) randn.plus(zero);

            assertEquals(sum1, (r, c) -> sum2.get(r, c));
            assertEquals(sum1, (r, c) -> rand.get(r, c) + randn.get(r, c));
            assertEquals(randn, (r, c) -> sum3.get(r, c));
        });
    }

    @org.junit.Test
    public void minus() throws Exception {
        forAllSizes((base, rows, cols) -> {

            Mat zero = base.newInstance(1);
            Mat rand = base.rand(rows, cols);
            Mat randn = base.randn(rows, cols);

            /* TODO, why do we need the cast here? */
            Mat sum1 = (Mat) rand.minus(randn);
            Mat sum2 = (Mat) randn.minus(rand);
            Mat sum3 = (Mat) randn.minus(zero);

            assertEquals(sum1, (r, c) -> -sum2.get(r, c));
            assertEquals(sum1, (r, c) -> rand.get(r, c) - randn.get(r, c));
            assertEquals(randn, (r, c) -> sum3.get(r, c));
        });
    }

    /**
     * Try multiplying a random matrix by both the identity and zero matrices.
     * Also multiply by a matrix of ones by a counting matrix.
     */
    @org.junit.Test
    public void times() throws Exception {
        forAllSizes((base, rows, cols) -> {

            for (int inner = getMinInner(); inner <= getMaxInner(); inner++) {

                final int in = inner;
                Mat zero = base.newInstance(1);
                Mat ones = base.ones(rows, inner);
                Mat eye = base.I(inner);
                Mat randn = base.randn(inner, cols);
                Mat counting = base.newInstance(inner, cols, (r, c) -> r + in * c);

                assertEquals(eye.times(randn), (r, c) -> randn.get(r, c));
                assertEquals(randn.times(zero), (r, c) -> 0);
                assertEquals(ones.times(counting), (r, c) -> 0.5 * in * (in - 1) + in * in * c);
            }
        });
    }

    /**
     * Create a random matrix and try squaring everything and then adding 3 to
     * each element.
     */
    @org.junit.Test
    public void apply() throws Exception {
        forAllSizes((base, rows, cols) -> {
            Mat rand = base.rand(rows, cols);
            Mat rand2 = rand.apply((a, b) -> a * a + b, 3);
            assertEquals(rand2, (r, c) -> rand.get(r, c) * rand.get(r, c) + 3);
        });
    }

    /*
     * --------------------------------------------------
     *
     * Helper methods
     *
     * --------------------------------------------------
     */
    /**
     * @return an instance of the concrete
     *         {@link Mat} class to be tested.
     */
    public abstract Mat getInstance();

    /** @return the tolerance used to test for equality between two numbers. */
    public double getEqualityTolerance() {
        return DefaultTestConditions.getEqualityTolerance();
    }

    /**
     * @return the minimum number of rows a matrix should have during testing.
     *         Typically this will be 1.
     */
    public int getMinRows() {
        return DefaultTestConditions.getMinRows();
    }

    /**
     * @return the maximum number of rows a matrix should have during testing.
     *         Typically this should be a "large" enough number (something like
     *         10 or 20). Obviously, the large you chose, the better code
     *         coverage you will have, but the longer the tests will take to
     *         run.
     */
    public int getMaxRows() {
        return DefaultTestConditions.getMaxRows();
    }

    /**
     * @return the minimum number of columns a matrix should have during
     *         testing. Typically this will be 1.
     */
    public int getMinCols() {
        return DefaultTestConditions.getMinCols();
    }

    /**
     * @return the maximum number of columns a matrix should have during
     *         testing. Typically this should be a "large" enough number
     *         (something like 10 or 20). Obviously, the large you chose, the
     *         better code coverage you will have, but the longer the tests will
     *         take to run.
     */
    public int getMaxCols() {
        return DefaultTestConditions.getMaxCols();
    }

    /**
     * @return the minimum inner dimension to use when testing matrix products.
     *         Typically this will be 1.
     */
    public int getMinInner() {
        return DefaultTestConditions.getMinInner();
    }

    /**
     * @return the maximum inner dimension a matrix should have when testing
     *         matrix products. Typically this should be a "large" enough number
     *         (something like 10 or 20). Obviously, the large you chose, the
     *         better code coverage you will have, but the longer the tests will
     *         take to run.
     */
    public int getMaxInner() {
        return DefaultTestConditions.getMaxInner();
    }

    /**
     * @return A class capable of filling a double array with an arbitrary type.
     *         Specifically, each element can be set by calling this class's
     *         {@link #apply(int,int)} method.
     */
    private interface MatFiller<T extends Number> {
        T apply(int row, int col);
    }

    /**
     * @return a double {@link List} generated by using the speicified
     *         {@link MatFiller}.
     */
    private <T extends Number> List<List<T>> generateList(int rows, int cols, MatFiller<T> filler) {
        List<List<T>> list = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            List<T> row = new ArrayList<T>(cols);
            for (int c = 0; c < cols; c++) {
                row.add(filler.apply(r, c));
            }
            list.add(row);
        }
        return list;
    }

    /**
     * Ensure that each element of the specified
     * {@link Mat} takes the value specified by
     * the given {@link Filler}, where equality is tested using the
     * {@link TestCase#assertEquals(double, double, double)} method with the
     * tolerance specified by the
     * {@link DefaultTestConditions#getEqualityTolerance()} method.
     */
    private void assertEquals(Mat mat, Filler filler) throws Exception {
        for (int row = 0; row < mat.rows(); row++) {
            for (int col = 0; col < mat.cols(); col++) {
                TestCase.assertEquals(mat.get(row, col), filler.apply(row, col),
                        getEqualityTolerance());
            }
        }
    }

    /**
     * Ensure that each element of the specified
     * {@link Mat} is the same as the value
     * returned by the given {@link Filler}, where sameness is tested using the
     * {@link TestCase#assertSame(Object, Object)} method.
     */
    private <T extends Number> void assertSame(Mat mat, MatFiller<T> filler) throws Exception {
        for (int row = 0; row < mat.rows(); row++) {
            for (int col = 0; col < mat.cols(); col++) {
                TestCase.assertSame(mat.get(row, col), filler.apply(row, col));
            }
        }
    }

    private interface TestElems {
        void test(Mat base, int rows, int cols) throws Exception;
    }

    private void forAllSizes(TestElems testCase) throws Exception {
        Mat base = getInstance();
        for (int rows = getMinRows(); rows <= getMaxRows(); rows++) {
            for (int cols = getMinCols(); cols <= getMaxCols(); cols++) {
                testCase.test(base, rows, cols);
            }
        }
    }

    private int index(int row, int col, int rows) {
        return row + col * rows;
    }
}