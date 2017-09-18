package io.jeti.linalg.matrix.interfaces;

import io.jeti.linalg.matrix.Check;
import java.util.Random;

public interface Tsr<T extends Tsr<T>> {

    interface Operation {
        double apply(double a, double b);
    }

    Random random = new Random();

    /**
     * Check that the bounds are stride are valid. Specifically, this function
     * assumes that you are trying to select elements from a dimension of size
     * "maxElements". For example, if the {@link Tsr} is the vector
     * {0,1,2,3,4,5,6,7,8,9}, a typical use case might be that you want to
     * select the subvector starting at index 1 (inclusive) and going to index 7
     * (exclusive) with a stride of 2, that is, you want to select the subvector
     * {1,3,5}. You would check whether this is a valid selection by calling
     * <code>checkSelection(1,7,2,10)</code>.
     * <p>
     * The aforementioned selection is valid, but there are many things that
     * would make a selection invalid. For example, if
     * <ol>
     * <li>the stride is zero.</li>
     * <li>to = from (which would result in an empty selection)</li>
     * <li>the stride has a different sign than (to - from)</li>
     * <li>from and to are in the bounds [0,maxElements)</li>
     * </ol>
     *
     * Note that this defers to the {@link Check} class to do the actual
     * checking. Hence if the selection is invalid for some reason, then an
     * Exception will be thrown.
     *
     * @return the number of elements in the selection.
     */
    default int checkSelection(int from, int to, int stride, int maxElements) {

        Check.notZero(stride);

        /*
         * Make sure that the selection leaves at least one element. That means
         * ensure that from != to...
         */
        int diff = to - from;
        Check.notZero(diff);

        /* Stride should have the same sign as (to - from) */
        Check.positive(diff * stride);

        /* Calculate the number of elements */
        int elems;
        if (stride > 0) {
            elems = 1 + (to - 1 - from) / stride;
        } else {
            elems = 1 + (to + 1 - from) / stride;
        }

        /* Make sure that the indices are in bounds */
        Check.inBounds(from, 0, maxElements);
        Check.inBounds(from + (elems - 1) * stride, 0, maxElements);

        /*
         * If you got this far, then everything checks out. Return the number of
         * elements in this selection.
         */
        return elems;
    }

    /**
     * Get the index along an offset dimension with a stride, using the
     * {@link Check} class to max sure that the argument is in bounds.
     */
    default int index(int elem, int from, int stride, int elems) {
        Check.inBounds(elem, 0, elems);
        return from + stride * elem;
    }

    /*
     * --------------------------------------------------
     *
     * Tsr/Self Operations
     *
     * --------------------------------------------------
     */

    /**
     * @return {@link #apply(Operation, Number)} with the power operator
     * 
     *         <pre>
     * <code>
     * (a, b) -&gt; pow(a,i)
     * </code>
     *         </pre>
     */
    default T pow(int i) {
        return apply((a, b) -> Math.pow(a, i), 0);
    }

    /*
     * --------------------------------------------------
     *
     * Tsr/Tsr Operations
     *
     * --------------------------------------------------
     */

    /**
     * @return {@link #apply(Operation, Tsr)} with the addition operator
     * 
     *         <pre>
     * <code>
     * (a, b) -&gt; a + b
     * </code>
     *         </pre>
     */
    default T plus(T B) {
        return apply((a, b) -> a + b, B);
    }

    /**
     * @return {@link #apply(Operation, Tsr)} with the subtraction operator
     * 
     *         <pre>
     * <code>
     * (a, b) -&gt; a - b
     * </code>
     *         </pre>
     */
    default T minus(T B) {
        return apply((a, b) -> a - b, B);
    }

    /**
     * @return {@link #apply(Operation, Tsr)} with the multiplication operator
     * 
     *         <pre>
     * <code>
     * (a, b) -&gt; a * b
     * </code>
     *         </pre>
     */
    default T times(T B) {
        return apply((a, b) -> a * b, B);
    }

    /**
     * @return a {@link Tsr} obtained by applying the specified operation in an
     *         element-wise fashion. Specifically, return a {@link Tsr} where
     *         the i^th element of is determined according to the following
     *         rules:
     *         <ul>
     *         <li>If B is scalar, then result(i) = operation(this(i),B).</li>
     *         <li>If this is scalar, then result(i) = operation(this,B(i)).
     *         </li>
     *         <li>If this and B are the same size, then result(i) =
     *         operation(this(i),B(i)).</li>
     *         </ul>
     *         <p>
     *         Note: the above is pseudo-code. We do not force tensors of higher
     *         dimensions to be linearly indexable, but hopefully the above
     *         conveys the idea.
     */
    T apply(Operation operation, T B);

    /*
     * --------------------------------------------------
     *
     * Tsr/Scalar Operations
     *
     * --------------------------------------------------
     */

    /**
     * @return {@link #apply(Operation, Number)} with the addition operator
     * 
     *         <pre>
     * <code>
     * (a, b) -&gt; a + b
     * </code>
     *         </pre>
     */
    default T plus(Number B) {
        return apply((a, b) -> a + b, B);
    }

    /**
     * @return {@link #apply(Operation, Number)} with the subtraction operator
     * 
     *         <pre>
     * <code>
     * (a, b) -&gt; a - b
     * </code>
     *         </pre>
     */
    default T minus(Number B) {
        return apply((a, b) -> a - b, B);
    }

    /**
     * @return {@link #apply(Operation, Number)} with the multiplication
     *         operator
     * 
     *         <pre>
     * <code>
     * (a, b) -&gt; a * b
     * </code>
     *         </pre>
     */
    default T times(Number B) {
        return apply((a, b) -> a * b, B);
    }

    /**
     * @return a {@link Tsr} with the same size as "this", where the i^th
     *         element of the result is given by
     * 
     *         <pre>
     * <code>
     * operation(this.get(i),B)
     * </code>
     *         </pre>
     * 
     *         Note: the above is pseudo-code. We do not force tensors of higher
     *         dimensions to be linearly indexable, but hopefully the above
     *         conveys the idea.
     */
    T apply(Operation operation, Number B);

    /*
     * --------------------------------------------------
     *
     * Printing Functions
     *
     * --------------------------------------------------
     */

    /**
     * Convert this {@link Tsr} to a String. Normally you would do this by
     * overriding "toString()". However, we cannot change the default
     * "toString()" implementation in an interface. Hence it is up to the
     * individual implementations to override "toString()" to return the output
     * of this function. It is recommended to do this so that all
     * implementations have the same behavior.
     */
    String asString();

    /**
     * See {@link #print(String)} where name = null.
     */
    default void print() {
        print(null);
    }

    /**
     * If the specified name is null, then print {@link #asString()} to
     * {@link System#out}. Otherwise, print name + " =\n" followed by
     * {@link #asString()} to {@link System#out}.
     */
    default void print(String name) {
        if (name != null) {
            System.out.println(name + " = ");
        }
        System.out.println(asString());
    }

    /**
     * Converted a double to string.
     */
    default String format(Double d) {
        return String.format("%+9.4f ", d);
    }
}
