package top.vncnliu.carve.java.algorithms.sort;

import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vncnliu@gmail.com on 2015/10/27.
 */
public class InsertionSort {

    public static void main(String[] args) {

        int[] disorderly = {5,7,7,71};
        List<Integer> orderly = new ArrayList<Integer>();

        for (int i=0; i<disorderly.length; i++) {
            boolean inserted = false;
            for (int i1 = 0; i1 < orderly.size(); i1++) {
                if(disorderly[i]<=orderly.get(i1)){
                    orderly.add(i1,disorderly[i]);
                    inserted=true;
                    break;
                }
            }
            if(!inserted){
                orderly.add(disorderly[i]);
            }
        }

        for (Integer integer : orderly) {
            System.out.println(integer);
        }
    }
}
