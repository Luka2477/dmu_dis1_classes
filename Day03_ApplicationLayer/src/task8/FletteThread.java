package task8;

import java.util.ArrayList;

public class FletteThread extends Thread {
    private final ArrayList<Integer> arr;
    private final int l;
    private final int h;

    public FletteThread(ArrayList<Integer> arr, int l, int h) {
        this.arr = arr;
        this.l = l;
        this.h = h;
    }

    public void run() {
        FletteSortering fs = new FletteSortering();
        fs.mergesort(arr, l, h);
    }

}
