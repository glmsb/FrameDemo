package com.demo.wyd.framedemo.iView;

import com.demo.wyd.framedemo.bean.Welfare;

import java.util.List;

/**
 * Description:UI层的回调接口
 * Created by wyd on 2017/2/21.
 */

public interface IViewWelfare {
    void updateLayout(List<Welfare> welfareList);
}
