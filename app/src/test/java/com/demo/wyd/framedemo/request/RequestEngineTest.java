package com.demo.wyd.framedemo.request;

import org.junit.Before;
import org.junit.Test;

import retrofit2.Retrofit;

import static org.junit.Assert.assertEquals;


/**
 * Description:
 * Created by wyd on 2017/3/17.
 */
public class RequestEngineTest {
    private Retrofit retrofit;

    @Before
    //该方法在每次测试方法调用前都会调用
    public void setUp() throws Exception {
        retrofit = RequestEngine.getInstance();
        System.out.println("setUp()...");
    }

    @Test
    public void getInstance() throws Exception {
       assertEquals("http://gank.io/api/data/",retrofit.baseUrl().toString());
        System.out.println(retrofit.baseUrl().toString());
    }

}