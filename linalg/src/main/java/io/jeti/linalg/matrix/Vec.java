package io.jeti.linalg.matrix;

import io.jeti.linalg.matrix.utils.Check;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A {@link Vec} is a one-dimensional data structure which supports element-wise
 * addition, subtraction, and multiplication, as well as special vector
 * operations, such as the dot product (aka "inner" product). However, so that
 * this interface can support immutable implementations, setters and other
 * mutable operations are not implemented.
 * </p>
 * <p>
 * Note that {@link Vec} implementations should use the signature:
 * </p>
 * 
 * <pre>
 * public class Vector implements Vec&lt;Vector&gt;
 * </pre>
 * <p>
 * because the generic type supplied to {@link Vec} is used as the input and
 * return type for many methods in the {@link Vec} interface. For instance,
 * consider the addition operation:
 * </p>
 * 
 * <pre>
 * T plus(T B)
 * </pre>
 * <p>
 * This implies that an instance of one implementation cannot be easily added to
 * an instance of a different implementation. While this may at first seem
 * illogical, it was done on purpose so that implementations can use whatever
 * optimizations are possible to make them as fast as possible. For example, if
 * we have an implementation that creates vectors on the GPU, then for the sake
 * of speed, we should only require that it supports addition and other
 * operations for other vectors that live on the GPU.
 * </p>
 * <p>
 * Here are some guidelines for constructing vectors:
 * </p>
 * <ol>
 * <li>If you want a vector of _zeros, use either {@link #_zeros(int)},
 * {@link #newInstance(int)}, or {@link #newInstance(int, Number)} with a value
 * of 0.</li>
 * <li>If you want a vector of _ones, use either {@link #_ones(int)} or
 * {@link #newInstance(int, Number)} with a value of 1.</li>
 * <li>If you want a vector where all the elements are the same number, use
 * {@link #newInstance(int, Number)}.</li>
 * <li>If you want a vector where you want to set all of the elements
 * individually, use {@link #newInstance(List)}, {@link #newInstance(Number[])},
 * or {@link #newInstance(int, Filler)}.</li>
 * </ol>
 * <p>
 * However, with regard to the last option, some additional comments are in
 * order:
 * <ul>
 * <li>When using the constructors, {@link #newInstance(List)} and
 * {@link #newInstance(Number[])}, note that implementations of this interface
 * are free to either shallow copy or reference the passed-in list or array. So
 * you should make sure you understand which semantics are being used by the
 * implementation you use.</li>
 * <li>The {@link Filler} implementation should be the default constructor you
 * use almost every time you construct a vector that needs to have distinct
 * elements. The reason is simply that using the List or array constructor is
 * not as portable because if you change the vector implementation you are
 * using, then those constructor semantics may be different. With the
 * {@link Filler} constructor, you are essentially guaranteed that the
 * implementation will not be holding a reference to the passed-in list or
 * array, but will instead have a shallow copy. To use the Filler class you
 * simply define a Filler which specifies how each entry of the vector should be
 * set:</li>
 * </ul>
 * 
 * <pre>
 * // Suppose that v is an already-existing instance of a {@link Vec} implementation called MyVec.
 * // The following produces a vector with entries 0,..,9
 * MyVec tmp = v.newInstance(10, index -&gt; index );
 *
 * // If you want to construct a vector from a list or array, you can use one of the direct newInstance
 * // constructors. However, since the semantics may vary from implementation to implementation,
 * // it would be preferable to use the following {@link Filler}, which will guarantee that a shallow copy is
 * // created, and no references are being held.
 * List myList ...
 * MyVec vecFromList = v.newInstance( myList.size(), index -&gt; myList.get(index));
 * </pre>
 */
public interface Vec<T extends Vec<T>> extends Tsr<T> {

    interface Filler {
        double apply(int i);
    }

    /*
     * --------------------------------------------------
     *
     * Constructors
     *
     * --------------------------------------------------
     */

    /**
     * @return A {@link Vec} with the specified number of elements, where all of
     *         the elements are set using the specified {@link Filler}.
     */
    T newInstance(int elems, Filler filler);

    /**
     * @return A {@link Vec} with the specified number of elements, where all of
     *         the elements are set to zero.
     */
    default T newInstance(int elems) {
        return newInstance(elems, index -> 0d);
    }

    /**
     * @return A {@link Vec} with the specified number of elements, where all of
     *         the elements are set to val.
     */
    default T newInstance(int elems, final Number val) {
        return newInstance(elems, index -> val.doubleValue());
    }

    /**
     * @return A {@link Vec} wrapper around the provided data. Note that it is
     *         up to the implementation to decide whether this is a copy or
     *         reference to the data.
     */
    default T newInstance(final List<Number> data) {
        return newInstance(data.size(), index -> data.get(index).doubleValue());
    }

    /**
     * @return A {@link Vec} wrapper around a copy of the provided data. Note
     *         that it is up to the implementation to decide whether this is a
     *         copy or reference to the data.
     */
    default T newInstance(final Number[] data) {
        return newInstance(data.length, index -> data[index].doubleValue());
    }

    /**
     * @return A {@link Vec} with the specified number of elements, where all of
     *         the elements are set to 1.
     */
    default T _ones(int elems) {
        return newInstance(elems, index -> 1d);
    }

    /**
     * @return A {@link Vec} with the specified number of elements, where all of
     *         the elements are set to 0.
     */
    default T _zeros(int elems) {
        return newInstance(elems, index -> 0d);
    }

    /**
     * @return A {@link Vec} with the specified number of elements, where each
     *         entry is a uniform random number in [0,1].
     */
    default T _rand(int elems) {
        return newInstance(elems, index -> random.nextDouble());
    }

    /**
     * @return A {@link Vec} with the specified number of elements, where each
     *         entry is a Gaussian random number drawn from a distribution with
     *         mean 0 and variance 1.
     */
    default T _randn(int elems) {
        return newInstance(elems, index -> random.nextGaussian());
    }

    /*
     * --------------------------------------------------
     *
     * Getters
     *
     * --------------------------------------------------
     */

    /**
     * @return A deep copy of the data contained in this {@link Vec}.
     */
    default List<Double> toList() {
        List<Double> out = new ArrayList<>(size());
        for (int i = 0; i < size(); i++)
            out.add(get(i));
        return out;
    }

    /**
     * @return The size of this {@link Vec}.
     */
    int size();

    /**
     * @return The specified element in this {@link Vec}, where indices start
     *         from 0.
     */
    Double get(int element);

    /**
     * If (from&lt;to), then return {@link #get(int, int, int)} where stride =
     * 1.
     * <p>
     * Otherwise, return {@link #get(int, int, int)} where stride = -1.
     */
    default T get(int from, int to) {
        if (from < to)
            return get(from, to, 1);
        else
            return get(from, to, -1);
    }

    /**
     * @return A view of the subvector in [from,to), that is, inclusive "from"
     *         and exclusive "to", with the specified stride. For example, if
     *         this {@link Vec} contains the data [0,1,2,3,4,5,6,7,8], then
     *         get(1,6,2) means the subvector:
     *         <ul>
     *         <li>starting (and including) the value at index 1</li>
     *         <li>up to (but excluding) the value at index 6</li>
     *         <li>with a step of 2</li>
     *         </ul>
     *         that is, the subvector [1,3,5]. All implementations must impose
     *         the constraint that to!=from, because otherwise the returned
     *         vector would be null, which is not allowed.
     *         <p>
     *         Finally, note that stride cannot be zero, and that stride must
     *         have the same sign as the difference (to-from). For example, if
     *         stride=-2, from=7, and to=1, then the previous example yields a
     *         vector with the data [7,5,3].
     *         </p>
     */
    T get(int from, int to, int stride);

    /*
     * --------------------------------------------------
     *
     * Vector/Vector Operations
     *
     * --------------------------------------------------
     */
    @Override
    default T apply(Operation operation, T B) {
        if (B.size() == 1) {
            return apply(operation, B.get(0));
        } else {
            Check.sameSize(this, B);
            return newInstance(size(), index -> operation.apply(get(index), B.get(index)));
        }
    }

    /**
     * @return this * B, that is, the dot (aka inner) product. This is only
     *         allowed when this.size()==B.size()
     */
    default Double dot(T B) {
        Check.sameSize(this, B);
        double sum = 0;
        for (int i = 0; i < size(); i++)
            sum += (get(i) * B.get(i));
        return sum;
    }

    /*
     * --------------------------------------------------
     *
     * Vector/Scalar Operations
     *
     * --------------------------------------------------
     */
    @Override
    default T apply(Operation operation, Number B) {
        return newInstance(size(), index -> operation.apply(get(index), B.doubleValue()));
    }

    /*
     * --------------------------------------------------
     *
     * Other Functions
     *
     * --------------------------------------------------
     */
    @Override
    default String asString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size(); i++)
            builder.append(format(get(i))).append(", ");
        return builder.toString();
    }
}