package io.jeti.linalg.matrix.interfaces;

public interface MutTsr<T extends MutTsr<T>> extends Tsr<T> {

    /*
     * --------------------------------------------------
     *
     * MutTsr/Self Operations
     *
     * --------------------------------------------------
     */

    /**
     * {@link #applyEquals(Operation, Number)} with the power operator
     * 
     * <pre>
     * <code>
     * (a, b) -&gt; pow(a,i)
     * </code>
     * </pre>
     */
    default void powEquals(int i) {
        applyEquals((a, b) -> Math.pow(a, i), 0);
    }

    /*
     * --------------------------------------------------
     *
     * MutTsr/MutTsr Operations
     *
     * --------------------------------------------------
     */

    /**
     * {@link #applyEquals(Operation, MutTsr)} with the addition operator
     * 
     * <pre>
     * <code>
     * (a, b) -&gt; a + b
     * </code>
     * </pre>
     */
    default void plusEquals(T B) {
        applyEquals((a, b) -> a + b, B);
    }

    /**
     * {@link #applyEquals(Operation, MutTsr)} with the subtraction operator
     * 
     * <pre>
     * <code>
     * (a, b) -&gt; a - b
     * </code>
     * </pre>
     */
    default void minusEquals(T B) {
        applyEquals((a, b) -> a - b, B);
    }

    /**
     * {@link #applyEquals(Operation, MutTsr)} with the multiplication operator
     * 
     * <pre>
     * <code>
     * (a, b) -&gt; a * b
     * </code>
     * </pre>
     */
    default void timesEquals(T B) {
        applyEquals((a, b) -> a * b, B);
    }

    /**
     * Update this {@link MutTsr} so that the i^th element of the result is
     * determined according to the following rules:
     * <ul>
     * <li>If B is scalar, then this(i) = operation(this(i),B).</li>
     * <li>If this and B are the same size, then result(i) =
     * operation(this(i),B(i)).</li>
     * </ul>
     * Note: the above is pseudo-code. We do not force tensors of higher
     * dimensions to be linearly indexable, but hopefully the above conveys the
     * idea.
     */
    void applyEquals(Operation operation, final T B);

    /*
     * --------------------------------------------------
     *
     * MutTsr/Scalar Operations
     *
     * --------------------------------------------------
     */

    /**
     * {@link #applyEquals(Operation, Number)} with the addition operator
     * 
     * <pre>
     * <code>
     * (a, b) -&gt; a + b
     * </code>
     * </pre>
     */
    default void plusEquals(Number B) {
        applyEquals((a, b) -> a + b, B);
    }

    /**
     * {@link #applyEquals(Operation, Number)} with the subtraction operator
     * 
     * <pre>
     * <code>
     * (a, b) -&gt; a - b
     * </code>
     * </pre>
     */
    default void minusEquals(Number B) {
        applyEquals((a, b) -> a - b, B);
    }

    /**
     * {@link #applyEquals(Operation, Number)} with the multiplication operator
     * 
     * <pre>
     * <code>
     * (a, b) -&gt; a * b
     * </code>
     * </pre>
     */
    default void timesEquals(Number B) {
        applyEquals((a, b) -> a * b, B);
    }

    /**
     * Update this {@link MutTsr} so that the i^th element of the result is
     * given by
     * 
     * <pre>
     * <code>
     * this(i) = operation(this(i),B)
     * </code>
     * </pre>
     * 
     * Note: the above is pseudo-code. We do not force tensors of higher
     * dimensions to be linearly indexable, but hopefully the above conveys the
     * idea.
     */
    void applyEquals(Operation operation, final Number B);
}
