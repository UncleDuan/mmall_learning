package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by ionolab-DP on 2018/12/24.
 */
public class BigDecimalTest {

    @Test
    public void test1(){
        System.out.println(0.05+0.01);
        System.out.println(4.015*100);
    }

    @Test
    public void test2(){
        BigDecimal b1=new BigDecimal(0.05);
        BigDecimal b2=new BigDecimal(0.01);
        System.out.println(b1.add(b2));
        System.out.println(4.015*100);
        //采用String构造器
        BigDecimal b3=new BigDecimal("0.05");
        BigDecimal b4=new BigDecimal("0.01");
        System.out.println("____________");
        System.out.println(b3.add(b4));
    }
}
