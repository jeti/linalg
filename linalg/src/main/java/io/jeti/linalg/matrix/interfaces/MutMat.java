package io.jeti.linalg.matrix.interfaces;

import io.jeti.linalg.matrix.Check;

/**
 * A {@link MutMat} is a two-dimensional data structure which extends the
 * {@link Mat} and {@link MutTsr} interfaces to support mutable operations, such
 * as setters.
 */
public interface MutMat<T extends MutMat<T>> extends Mat<T>, MutTsr<T> {

    /*
     * --------------------------------------------------
     *
     * Setters
     *
     * --------------------------------------------------
     */

    /**
     * Set this(row,col) = val;
     */
    void set(int row, int col, Number val);

    /**
     * Set all of the elements in the vector using the specified {@link Filler}.
     */
    default void set(Filler filler) {
        for (int row = 0; row < rows(); row++) {
            for (int col = 0; col < cols(); col++) {
                set(row, col, filler.apply(row, col));
            }
        }
    }

    /*
     * --------------------------------------------------
     *
     * Matrix/Matrix Operations
     *
     * --------------------------------------------------
     */
    @Override
    default void applyEquals(Operation operation, final T B) {
        if (B.isScalar()) {
            applyEquals(operation, B.get(0, 0));
        } else {
            Check.sameNumberOfRows(this, B);
            Check.sameNumberOfCols(this, B);
            set((row, col) -> operation.apply(get(row, col), B.get(row, col)));
        }
    }

    /*
     * --------------------------------------------------
     *
     * Matrix/Scalar Operations
     *
     * --------------------------------------------------
     */
    @Override
    default void applyEquals(Operation operation, final Number B) {
        set((row, col) -> operation.apply(get(row, col), B.doubleValue()));
    }
}