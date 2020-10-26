package com.neu.lab;

/**
 * Created by 17921 on 2020/10/26
 */
public class Test{
    public static void add(Byte b)
    {
        b = b++;
    }

    public static void main(String[] args) {
        test();
    }
    public static  int test()
    {
        Byte a = 127;
        Byte b = 127;
        add(++a);
        System.out.print(a + " ");
        add(b);
        System.out.print(b + "");
    }
}
