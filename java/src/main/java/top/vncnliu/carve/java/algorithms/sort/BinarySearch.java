package top.vncnliu.carve.java.algorithms.sort;

/**
 * Created by vncnliu@gmail.com on 2015/10/30.
 */
public class BinarySearch {

    static int[] data = {49,38,65,97,76,13,27,49};
    //static int[] data = {22,11,3,1,12,42,5,25};
    //static int[] data = {1,11,3};

    public static void main(String[] args) {

        new BinarySearch().binarySearch(0, data.length - 1);
        for (int i = 0; i < data.length; i++) {
            System.out.println(data[i]);
        }
    }

    public void binarySearch(int begin, int end){

        if(begin < end){
            int middle = partition(begin,end);
            binarySearch(begin, middle-1);
            binarySearch(middle + 1, end);
        }

    }

    private void swap(int m, int n) {
        int temp = data[m];
        data[m] = data[n];
        data[n] = temp;
    }

    public int partition(int low, int high){
        int privotKey = data[low];                             //基准元素
        while(low < high){                                     //从表的两端交替地向中间扫描
            while(low < high  && data[high] >= privotKey) --high;  //从high 所指位置向前搜索，至多到low+1 位置。将比基准元素小的交换到低端
            swap(low, high);
            while(low < high  && data[low] <= privotKey ) ++low;
            swap(low, high);
        }
        return low;
    }
}

