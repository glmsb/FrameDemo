package com.demo.wyd.framedemo.tester;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Description:Junit基本注解使用
 * Created by wyd on 2017/3/21.
 */
public class JunitLifeCycle {

    @BeforeClass //该方法在所有测试方法之前调用，只会被调用一次
    public static void prepareDataForTest() {
        System.out.println("------method prepareDataForTest called------\n @BeforeClass");
    }

    @Before  //该方法在每次测试方法调用前都会调用
    public void init() {
        System.out.println("------method init called-----\n @Before-");
    }

    @Test   //该方法需要测试
    public void test1() {
        System.out.println("------method test1 called------\n @Test");
    }

    @Test
    public void test2() {
        System.out.println("------method test2 called------\n @Test");
    }

    @Test
    public void test3() {
        System.out.println("------method test3 called------\n @Test");
    }

    @Ignore  //忽略该方法
    public void test4() {
        System.out.println("------method test4 called------\n @Ignore");
    }


    @After  //该方法在每次测试方法调用后都会调用
    public void clearDataForTest() {
        System.out.println("------method clearDataForTest called------\n @After");
    }

    @AfterClass  //该方法在所有测试方法之后调用，只会被调用一次
    public static void finish() {
        System.out.println("------method finish called------\n @AfterClass");
    }

}
