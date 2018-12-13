package com.mongo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/17 9:39
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description: 使用快速排序的方法对 list 进行排序
 */
public class ListSort {

    /**
     * 对传入的list进行快速排序
     *
     * @param list 待排序的list
     * @return
     */
    public static void sort(List<Long> list, int low, int high) {
        int start = low;
        int end = high;
        long key = list.get(low);

        while (end > start) {
            //从后往前比较, 如果没有比关键值小的，比较下一个，直到有比关键值小的交换位置，然后又从前往后比较
            while (end > start && list.get(end) >= key)
                end--;
            if (list.get(end) <= key) {
                long temp = list.get(end);
                list.set(end, list.get(start));
                list.set(start, temp);
            }
            //从前往后比较,如果没有比关键值大的，比较下一个，直到有比关键值大的交换位置
            while (end > start && list.get(start) <= key)
                start++;
            if (list.get(start) >= key) {
                Long temp = list.get(start);
                list.set(start, list.get(end));
                list.set(end, temp);
            }
            //此时第一次循环比较结束，关键值的位置已经确定了。左边的值都比关键值小，右边的值都比关键值大，但是两边的顺序还有可能是不一样的，进行下面的递归调用
        }
        if (start > low) sort(list, low, start - 1);//左边序列。第一个索引位置到关键值索引-1
        if (end < high) sort(list, end + 1, high);//右边序列。从关键值索引+1到最后一个
    }


    public static void main(String[] args) {
        ListSort listSort = new ListSort();
        ArrayList<Long> lists = new ArrayList<Long>();
        lists.add(12l);
        lists.add(8l);
        lists.add(7l);
        lists.add(14l);
        listSort.sort(lists, 0, lists.size() - 1);
        System.out.println(lists);
    }

}
