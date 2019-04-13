package bgu.spl.net;

public class Pair<L,R>  {
    private L l;
    private R r;

    public Pair(L l1,R r1) {
        l=l1;
        r=r1;
    }

    public R getR() {
        return r;
    }

    public L getL() {
        return l;
    }
}
