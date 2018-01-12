package util;

/**
 * Contains 2 values inside.
 */
public class Pair<T1,T2> {

    public T1 firstValue;
    public T2 secondValue;

    public Pair(T1 firstValue, T2 secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

}
