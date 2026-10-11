package model;

public class Tuple<X> {
    public X getEl1() {
        return el1;
    }

    public X getEl2() {
        return el2;
    }

    private X el1;
    private X el2;

    public Tuple(X e1, X e2)
    {
        el1 = e1;
        el2 = e2;
    }
}
