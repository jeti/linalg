package io.jeti.linalg.matrix;

import io.jeti.linalg.matrix.Gettable.Gettable1;
import io.jeti.linalg.matrix.interfaces.Vec;
import java.util.ArrayList;
import java.util.List;

/**
 * A pure Java, immutable implementation of the {@link Vec} interface.
 */
public class Vector implements Vec<Vector>, Gettable1<Double> {

    /*
     * --------------------------------------------------
     *
     * Private Fields
     *
     * --------------------------------------------------
     */
    private final Gettable1<Double> data;
    private final int               numels;
    private final int               from;
    private final int               stride;

    /*
     * --------------------------------------------------
     *
     * Constructors
     *
     * --------------------------------------------------
     */
    @Override
    public Vector newInstance(int elems, Filler filler) {
        return new Vector(elems, filler);
    }

    /**
     * See {@link #newInstance(int, Filler)}.
     */
    public Vector(int elems, Filler filler) {
        Check.positive(elems);
        List<Double> tmp = new ArrayList<>(elems);
        for (int i = 0; i < elems; i++) {
            tmp.add(filler.apply(i));
        }
        this.numels = elems;
        this.data = new Gettable.List<>(tmp);
        this.from = 0;
        this.stride = 1;
    }

    /**
     * See {@link #newInstance(int)}.
     */
    public Vector(int elems) {
        this(elems, index -> 0d);
    }

    /**
     * See {@link #newInstance(int, Number)}.
     */
    public Vector(int elems, final Number val) {
        this(elems, index -> val.doubleValue());
    }

    /**
     * See {@link #newInstance(List)}.
     */
    public Vector(final List<Number> data) {
        this(data.size(), index -> data.get(index).doubleValue());
    }

    /**
     * See {@link #newInstance(Number[])}.
     */
    public Vector(final Number[] data) {
        this(data.length, index -> data[index].doubleValue());
    }

    /**
     * A hidden constructor which does not copy the input array. This
     * constructor is critical for constructing views of the vector.
     */
    private Vector(final Vector vec, int from, int to, int stride) {

        /* Make sure that selection is valid. */
        this.numels = checkSelection(from, to, stride, vec.size());
        this.data = vec;
        this.from = from;
        this.stride = stride;
    }

    /*
     * --------------------------------------------------
     *
     * Getters
     *
     * --------------------------------------------------
     */
    @Override
    public final int size() {
        return numels;
    }

    @Override
    public final Double get(int elem) {
        return data.get(index(elem, from, stride, size()));
    }

    @Override
    public Vector get(int from, int to, int stride) {
        return new Vector(this, from, to, stride);
    }

    /*
     * --------------------------------------------------
     *
     * Other Functions
     *
     * --------------------------------------------------
     */
    @Override
    public final String toString() {
        return asString();
    }
}