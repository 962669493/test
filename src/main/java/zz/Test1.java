package zz;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangzheng
 * @date 2020/9/6
 */
public class Test1{
    public  native void sayHello();
    public static void main(String[] args) {
        Map<Integer, Integer> map = new HashMap<>(12);
        /*int[] a = new int[]{2,4,3,5,9,7,6};
        quickSort(a);*/
    }

    public static void quickSort(int[] args) {
        sort(args, 0, args.length - 1);
    }

    public static void sort(int[] args, int start, int end){
        if(end <= start){
            return;
        }
        int key = start, i = start, j = end;
        while (i < j) {
            while (j > i && args[j] >= args[key]) {
                j--;
            }
            while (i < j && args[i] <= args[key]) {
                i++;
            }
            int k = args[i];
            args[i] = args[j];
            args[j] = k;
        }
        int k = args[i];
        args[i] = args[key];
        args[key] = k;
        sort(args, start, key - 1);
        sort(args, key + 1, end);
    }
}
