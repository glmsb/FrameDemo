package com.demo.wyd.framedemo.model;

import com.demo.wyd.framedemo.bean.Welfare;

import java.util.List;

/**
 * Description:统一的数据返回格式的封装
 * Created by wyd on 2018-3-26.
 */

public class BaseResponse<T>{
    /**
     * error : false
     * results : [{"_id":"58a9752b421aa93d3d15aa31","createdAt":"2017-02-19T18:36:27.16Z","desc":"2-20","publishedAt":"2017-02-20T11:56:22.616Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-19-16789884_666922016823652_4719569931841044480_n.jpg","used":true,"who":"daimajia"},{"_id":"58a641a4421aa9662f429734","createdAt":"2017-02-17T08:19:48.413Z","desc":"2-17","publishedAt":"2017-02-17T11:31:19.996Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-17-16464474_721724277990542_654863958657728512_n.jpg","used":true,"who":"daimajia"},{"_id":"58a504d1421aa966366d05ce","createdAt":"2017-02-16T09:48:01.526Z","desc":"2-16","publishedAt":"2017-02-16T10:07:37.13Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-16-16790004_607292222809583_5160406710837313536_n.jpg","used":true,"who":"daimajia"},{"_id":"58a39d1c421aa966366d05c0","createdAt":"2017-02-15T08:13:16.351Z","desc":"2-15","publishedAt":"2017-02-15T11:24:04.127Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-15-16464434_414363458902323_3665858302006263808_n.jpg","used":true,"who":"daimajia"},{"_id":"58a24ce0421aa901ef40579f","createdAt":"2017-02-14T08:18:40.781Z","desc":"2-14","publishedAt":"2017-02-14T11:42:37.303Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-14-16123260_755771577930478_8918176595718438912_n.jpg","used":true,"who":"daimajia"},{"_id":"58a10619421aa901f7902c6a","createdAt":"2017-02-13T09:04:25.996Z","desc":"2-13","publishedAt":"2017-02-13T11:54:17.922Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-13-16464498_1247940031909047_2763412151866490880_n.jpg","used":true,"who":"daimajia"},{"_id":"589d31a2421aa9270bc7332e","createdAt":"2017-02-10T11:21:06.747Z","desc":"2-10","publishedAt":"2017-02-10T11:38:22.122Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-10-16465759_171779496648995_128281069584646144_n.jpg","used":true,"who":"代码家"},{"_id":"589bb252421aa92dc0dfd3bf","createdAt":"2017-02-09T08:05:38.361Z","desc":"2-9","publishedAt":"2017-02-09T11:46:26.70Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-09-16583339_172818256542563_353855393375453184_n.jpg","used":true,"who":"daimajia"},{"_id":"589a7161421aa92db8a0041b","createdAt":"2017-02-08T09:16:17.678Z","desc":"2-8","publishedAt":"2017-02-08T11:39:51.371Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-08-16230686_191036801373876_4789664128824246272_n.jpg","used":true,"who":"daimajia"},{"_id":"58993f2c421aa970b84523ab","createdAt":"2017-02-07T11:29:48.689Z","desc":"2-7","publishedAt":"2017-02-07T11:37:03.683Z","source":"chrome","type":"福利","url":"http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-07-032924.jpg","used":true,"who":"daimajia"}]
     */

    private boolean error; // 错误码
    private T results; //这里可以是对象，也可以是数组对象

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}
