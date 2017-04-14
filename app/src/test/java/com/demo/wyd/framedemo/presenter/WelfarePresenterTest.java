package com.demo.wyd.framedemo.presenter;

import com.demo.wyd.framedemo.bean.Welfare;
import com.demo.wyd.framedemo.carrier.CarrierWelfare;
import com.demo.wyd.framedemo.request.APIStore;
import com.demo.wyd.framedemo.request.RequestEngine;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Description:@Parameterized注解，用于输入多个参数进行测试
 * Created by wyd on 2017/3/21.
 */
@RunWith(Parameterized.class)
public class WelfarePresenterTest {

    /*
     * 方式一：使用注解赋值
     */
//    @Parameterized.Parameter
//    public String urlSite;

    /**
     * 方式二：使用构造方法赋值
     */
    private String path;
    public WelfarePresenterTest(String path) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static Collection<String> initData() {
        List<String> urlSite = new ArrayList<>();
        urlSite.add("Android");
        urlSite.add("Ios");
        urlSite.add("福利");
        return urlSite;
    }

    @Test
    public void gainWelfare() throws Exception {
        APIStore.WelfareService welfareService = RequestEngine.getInstance().create(APIStore.WelfareService.class);
        welfareService.getWelfare(path,10)
                .map(new Function<CarrierWelfare, List<Welfare>>() {
                    @Override
                    public List<Welfare> apply(@NonNull CarrierWelfare carrierWelfare) throws Exception {
                        Assert.assertNotNull(carrierWelfare);
                        return carrierWelfare.getResults();
                    }
                })
                .filter(new Predicate<List<Welfare>>() {
                    @Override
                    public boolean test(@NonNull List<Welfare> welfares) throws Exception {
                        Assert.assertNotNull(welfares);
                        Assert.assertTrue(welfares.size() > 0);
                        return welfares.size() > 0;
                    }
                })
                .subscribe(new Consumer<List<Welfare>>() {
                    @Override
                    public void accept(@NonNull List<Welfare> welfares) throws Exception {
                        System.out.println(welfares.get(0).getUrl());
                        System.out.println(path + welfares.size());
                    }
                });
    }
}