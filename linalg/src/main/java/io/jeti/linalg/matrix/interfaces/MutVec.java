package io.jeti.linalg.matrix.interfaces;

import io.jeti.linalg.matrix.Check;

/**
 * A {@link MutVec} is a one-dimensional data structure which extends the
 * {@link Vec} and {@link MutTsr} interfaces to support mutable operations, such
 * as setters.
 */
public interface MutVec<T extends MutVec<T>> extends Vec<T>, MutTsr<T> {

    /*
     * --------------------------------------------------
     *
     * Setters
     *
     * --------------------------------------------------
     */

    /**
     * Set this(element) = val;
     */
    void set(int elem, Number val);

    /**
     * Set all of the elements in the vector using the specified {@link Filler}.
     */
    default void set(Filler filler) {
        for (int i = 0; i < size(); i++) {
            set(i, filler.apply(i));
        }
    }

    /*
     * --------------------------------------------------
     *
     * Vector Self Operations
     *
     * --------------------------------------------------
     */

    /**
     * Swap the i^th and j^th entries.
     */
    default void swap(int i, int j) {
        Double tmp = get(i);
        set(i, get(j));
        set(j, tmp);
    }

    /*
     * --------------------------------------------------
     *
     * Vector/Vector Operations
     *
     * --------------------------------------------------
     */
    @Override
    default void applyEquals(Operation operation, final T B) {
        if (B.size() == 1) {
            applyEquals(operation, B.get(0));
        } else {
            Check.sameSize(this, B);
            set(integer -> operation.apply(get(integer), B.get(integer)));
        }
    }

    /*
     * --------------------------------------------------
     *
     * Vector/Scalar Operations
     *
     * --------------------------------------------------
     */
    @Override
    default void applyEquals(Operation operation, final Number B) {
        set(integer -> operation.apply(get(integer), B.doubleValue()));
    }
}