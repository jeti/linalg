package io.jeti.linalg.matrix.utils.tests;

import io.jeti.linalg.matrix.Vec;
import io.jeti.linalg.matrix.Vec.Filler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

abstract public class VecTest {

    @org.junit.Test
    public void newInstance() throws Exception {
        forAllSizes((base, elems) -> {

            /* Construct vectors of zeros using all of the available methods */
            List<Float> zerosList = generateList(elems, ind -> 0f);
            Float[] zerosArray = zerosList.toArray(new Float[0]);

            assertEquals(base.newInstance(elems), ind -> 0);
            assertEquals(base.newInstance(elems, 0), ind -> 0);
            assertEquals(base.newInstance(elems, ind -> 0), ind -> 0);
            assertEquals(base.newInstance(zerosList), ind -> 0);
            assertEquals(base.newInstance(zerosArray), ind -> 0);

            /* Construct vectors of ones using all of the available methods */
            List<Double> onesList = generateList(elems, ind -> 1.0);
            Double[] onesArray = onesList.toArray(new Double[0]);

            assertEquals(base.newInstance(elems, 1), ind -> 1);
            assertEquals(base.newInstance(elems, ind -> 1.0), ind -> 1);
            assertEquals(base.newInstance(onesList), ind -> 1);
            assertEquals(base.newInstance(onesArray), ind -> 1);

            /* Make counting vectors (0,1,2,...) using the available methods */
            List<Integer> countingList = generateList(elems, ind -> ind);
            Integer[] countingArray = countingList.toArray(new Integer[0]);

            assertEquals(base.newInstance(elems, index -> index), ind -> ind);
            assertEquals(base.newInstance(countingList), ind -> ind);
            assertEquals(base.newInstance(countingArray), ind -> ind);

        });
    }

    /**
     * All we are going to do here is to check that they are the correct sizes
     */
    @org.junit.Test
    public void rand() throws Exception {
        forAllSizes((base, elems) -> {
            TestCase.assertEquals(base.rand(elems).size(), elems);
        });
    }

    /**
     * All we are going to do here is to check that they are the correct sizes
     */
    @org.junit.Test
    public void randn() throws Exception {
        forAllSizes((base, elems) -> {
            TestCase.assertEquals(base.randn(elems).size(), elems);
        });
    }

    /**
     * Create a random matrix, then a shallow list copy. Make sure that both
     * have the same contents, then try to clear the list, and make sure that
     * the vec is unchanged.
     */
    @org.junit.Test
    public void toList() throws Exception {
        forAllSizes((base, elems) -> {

            Vec vec = base.randn(elems);
            List<Double> list = vec.toList();
            List<Double> listCopy = vec.toList();

            assertEquals(vec, ind -> list.get(ind));

            list.clear();

            TestCase.assertEquals(vec.size(), elems);
            TestCase.assertEquals(listCopy.size(), elems);
            assertEquals(vec, ind -> listCopy.get(ind));
        });
    }

    /**
     * Check that the size function returns the same value as the value we
     * construct the vector with.
     */
    @org.junit.Test
    public void size() throws Exception {
        forAllSizes((base, elems) -> {

            /* Construct vectors of zeros using all of the available methods */
            Set<Vec> vecs = new HashSet<>();
            vecs.add(base.newInstance(elems));
            vecs.add(base.newInstance(elems, 0));
            vecs.add(base.newInstance(elems, index -> 0));
            vecs.add(base.zeros(elems));
            vecs.add(base.ones(elems));
            vecs.add(base.rand(elems));
            vecs.add(base.randn(elems));
            List<Integer> zerosList = generateList(elems, ind -> 0);
            Integer[] zerosArray = zerosList.toArray(new Integer[0]);
            vecs.add(base.newInstance(zerosList));
            vecs.add(base.newInstance(zerosArray));
            for (Vec vec : vecs) {
                TestCase.assertTrue(vec.size() == elems);
            }
        });
    }

    /**
     * Create counting matrices {0,1,2,...} and make sure that the get function
     * returns the index, that is,
     *
     * <pre>
     * <code>
     * vec.get(i) == i
     * </code>
     * </pre>
     */
    @org.junit.Test
    public void get() throws Exception {
        forAllSizes((base, elems) -> {
            assertEquals(base.newInstance(elems, ind -> ind), ind -> ind);
        });
    }

    /**
     * Try to get a view of the underlying vector. Since this method should
     * return a view, not a copy, we check that we are getting the same
     * instances. Furthermore, we want to check that views of views are working
     * properly. We do this by creating a vector {0,1,2,3,...9}, then grabbing
     * subvectors {1,2,3,...}, until there is only one element left.
     */
    @org.junit.Test
    public void getSelection() throws Exception {
        forAllSizes((base, elems) -> {

            Vec randn = base.randn(elems);
            Vec sliceStart = randn.get(0, randn.size());
            Vec sliceEnd = randn.get(0, randn.size());
            int its = 0;

            while (sliceStart.size() > 0) {

                /* Check that slicing returns a view */
                final int fits = its;
                assertSame(sliceStart, ind -> randn.get(ind + fits));
                assertSame(sliceEnd, ind -> randn.get(ind));

                /* Slice until there is nothing left. */
                if (sliceStart.size() > 1) {
                    its++;
                    sliceStart = sliceStart.get(1, sliceStart.size());
                    sliceEnd = sliceEnd.get(0, sliceEnd.size() - 1);
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
        int stride = 4;
        forAllSizes((base, elems) -> {

            Vec randn = base.randn(elems);
            Vec slice = randn.get(0, randn.size());
            int its = 0;

            while (slice.size() > 0) {

                /* Check that slicing returns a view */
                final int fits = its;
                assertSame(slice, ind -> randn.get(ind * (int) Math.pow(stride, fits)));

                /* Slice until there is nothing left. */
                if (slice.size() > 1) {
                    its++;
                    slice = slice.get(0, slice.size(), stride);
                } else {
                    break;
                }
            }
        });
    }

    /**
     * Apply the elementwise operation a * b + b.
     */
    @org.junit.Test
    public void applyVector() throws Exception {
        forAllSizes((base, elems) -> {

            /* Create vectors of ones, zeros, and counting */
            Vec zeros = base.zeros(elems);
            Vec ones = base.ones(elems);
            Vec counting = base.newInstance(elems, ind -> ind);

            /* All of these should be zero */
            assertEquals(zeros.apply((a, b) -> a * b + b, zeros), ind -> 0);
            assertEquals(ones.apply((a, b) -> a * b + b, zeros), ind -> 0);
            assertEquals(counting.apply((a, b) -> a * b + b, zeros), ind -> 0);

            /* This should be ones */
            assertEquals(zeros.apply((a, b) -> a * b + b, ones), ind -> 1);

            /* This should be counting + 1 */
            assertEquals(counting.apply((a, b) -> a * b + b, ones), ind -> ind + 1);
        });
    }

    @org.junit.Test
    public void dot() throws Exception {
        forAllSizes((base, elems) -> {

            /* Create vectors of ones, zeros, and counting */
            Vec zeros = base.zeros(elems);
            Vec ones = base.ones(elems);
            Vec counting = base.newInstance(elems, ind -> ind);

            /* Compute the dot product of all of these combinations */
            TestCase.assertEquals(zeros.dot(zeros).doubleValue(), 0, getEqualityTolerance());
            TestCase.assertEquals(zeros.dot(ones).doubleValue(), 0, getEqualityTolerance());
            TestCase.assertEquals(zeros.dot(counting).doubleValue(), 0, getEqualityTolerance());
            TestCase.assertEquals(ones.dot(zeros).doubleValue(), 0, getEqualityTolerance());
            TestCase.assertEquals(counting.dot(zeros).doubleValue(), 0, getEqualityTolerance());

            TestCase.assertEquals(ones.dot(ones).doubleValue(), elems, getEqualityTolerance());

            TestCase.assertEquals(ones.dot(counting).doubleValue(), elems * (elems - 1) / 2.0,
                    getEqualityTolerance());
            TestCase.assertEquals(counting.dot(ones).doubleValue(), elems * (elems - 1) / 2.0,
                    getEqualityTolerance());

            TestCase.assertEquals(counting.dot(counting).doubleValue(),
                    elems * (elems - 1) * (2 * elems - 1) / 6.0, getEqualityTolerance());
        });
    }

    @org.junit.Test
    public void plus() throws Exception {
        forAllSizes((base, elems) -> {

            /* Create some random matrices and see if their sums are ok. */
            Vec rand = base.rand(elems);
            Vec randn = base.randn(elems);

            /* TODO, why do we need the cast here? */
            Vec sum1 = (Vec) rand.plus(randn);
            Vec sum2 = (Vec) randn.plus(rand);
            Vec zero = base.newInstance(1);
            Vec sum3 = (Vec) randn.plus(zero);

            assertEquals(sum1, ind -> sum2.get(ind));
            assertEquals(sum1, ind -> rand.get(ind) + randn.get(ind));
            assertEquals(sum3, ind -> randn.get(ind));
        });
    }

    @org.junit.Test
    public void minus() throws Exception {
        forAllSizes((base, elems) -> {

            /*
             * Create some random matrices and see if their differences are ok.
             */
            Vec rand = base.rand(elems);
            Vec randn = base.randn(elems);

            /* TODO, why do we need the cast here? */
            Vec sum1 = (Vec) rand.minus(randn);
            Vec sum2 = (Vec) randn.minus(rand);
            Vec zero = base.newInstance(1);
            Vec sum3 = (Vec) randn.minus(zero);

            assertEquals(sum1, ind -> -sum2.get(ind));
            assertEquals(sum1, ind -> rand.get(ind) - randn.get(ind));
            assertEquals(sum3, ind -> randn.get(ind));
        });
    }

    @org.junit.Test
    public void times() throws Exception {
        forAllSizes((base, elems) -> {

            /*
             * Create some random matrices and see if their differences are ok.
             */
            Vec rand = base.rand(elems);
            Vec randn = base.randn(elems);

            /* TODO, why do we need the cast here? */
            Vec sum1 = (Vec) rand.times(randn);
            Vec sum2 = (Vec) randn.times(rand);
            Vec zero = base.newInstance(1);
            Vec sum3 = (Vec) randn.times(zero);

            assertEquals(sum1, ind -> sum2.get(ind));
            assertEquals(sum1, ind -> rand.get(ind) * randn.get(ind));
            assertEquals(sum3, ind -> 0);
        });
    }

    /**
     * Create a random list and try squaring everything and then adding 3 to
     * each element.
     */
    @org.junit.Test
    public void apply() throws Exception {
        forAllSizes((base, elems) -> {
            Vec rand = base.rand(elems);
            assertEquals(rand.apply((a, b) -> a * a + b, 3),
                    ind -> rand.get(ind) * rand.get(ind) + 3);
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
     *         {@link Vec} class to be tested.
     */
    abstract public Vec getInstance();

    /** @return the tolerance used to test for equality between two numbers. */
    public double getEqualityTolerance() {
        return DefaultTestConditions.getEqualityTolerance();
    }

    /**
     * @return the minimum number of elements a vector should have during
     *         testing. Typically this will be 1.
     */
    public int getElemMin() {
        return DefaultTestConditions.getElemMin();
    };

    /**
     * @return the maximum number of elements a vector should have during
     *         testing. Typically this should be a "large" enough number
     *         (something like 10 or 20). Obviously, the large you chose, the
     *         better code coverage you will have, but the longer the tests will
     *         take to run.
     */
    public int getElemMax() {
        return DefaultTestConditions.getElemMax();
    }

    /**
     * @return A class capable of filling an array with an arbitrary type.
     *         Specifically, each element of an array can be set by calling this
     *         class's {@link #apply(int)} method.
     */
    private interface ListFiller<T extends Number> {
        T apply(int i);
    }

    /**
     * @return a {@link List} generated by using the speicified
     *         {@link ListFiller}.
     */
    private <T extends Number> List<T> generateList(int elems, ListFiller<T> filler) {
        List<T> list = new ArrayList<>();
        for (int ind = 0; ind < elems; ind++) {
            list.add(filler.apply(ind));
        }
        return list;
    }

    /**
     * Ensure that each element of the specified
     * {@link Vec} takes the value specified by
     * the given {@link Filler}, where equality is tested using the
     * {@link TestCase#assertEquals(double, double, double)} method with the
     * tolerance specified by the
     * {@link DefaultTestConditions#getEqualityTolerance()} method.
     */
    private void assertEquals(Vec vec, Filler filler) throws Exception {
        for (int ind = 0; ind < vec.size(); ind++) {
            TestCase.assertEquals(vec.get(ind), filler.apply(ind), getEqualityTolerance());
        }
    }

    /**
     * Ensure that each element of the specified
     * {@link Vec} is the same as the value
     * returned by the given {@link Filler}, where sameness is tested using the
     * {@link TestCase#assertSame(Object, Object)} method.
     */
    private <T extends Number> void assertSame(Vec vec, ListFiller<T> filler) throws Exception {
        for (int ind = 0; ind < vec.size(); ind++) {
            TestCase.assertSame(vec.get(ind), filler.apply(ind));
        }
    }

    private interface TestElems {
        void test(Vec base, int elems) throws Exception;
    }

    private void forAllSizes(TestElems testCase) throws Exception {
        Vec base = getInstance();
        for (int elems = getElemMin(); elems <= getElemMax(); elems++) {
            testCase.test(base, elems);
        }
    }
}