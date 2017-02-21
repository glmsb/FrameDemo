package com.demo.wyd.framedemo.request;

import com.demo.wyd.framedemo.carrier.CarrierWelfare;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Description:
 * Created by wyd on 2017/2/21.
 */

public class APIStore {
    /**
     * 分类数据: http://gank.io/api/data/数据类型/请求个数/第几页
     * 数据类型： 福利 | Android | iOS | 休息视频 | 拓展资源 | 前端 | all
     * 请求个数： 数字，大于0
     * 第几页：数字，大于0
     */
    public interface WelfareService {
        @GET("{type}/50/8")
        Call<CarrierWelfare> getWelfareList(@Path("type") String type);
    }
}
