package com.demo.wyd.framedemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.demo.wyd.framedemo.R;
import com.demo.wyd.framedemo.adapter.WelfareAdapter;
import com.demo.wyd.framedemo.bean.Welfare;
import com.demo.wyd.framedemo.iView.IViewWelfare;
import com.demo.wyd.framedemo.presenter.WelfarePresenter;
import com.demo.wyd.refreshandload.listener.OperationListener;
import com.demo.wyd.refreshandload.view.DefaultFooter;
import com.demo.wyd.refreshandload.view.DefaultHeader;
import com.demo.wyd.refreshandload.view.RLLayout;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity implements IViewWelfare {

    private WelfareAdapter adapter;
    private RecyclerView recyclerView;
    private RLLayout rlLayout;
    private List<Welfare> list;
    private int limit = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        list = new ArrayList<>();
        new WelfarePresenter(this, this).gainWelfare("福利", 10);

        recyclerView = findViewById(R.id.rv_testRL);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rlLayout = findViewById(R.id.rl_layout);
        DefaultHeader header = new DefaultHeader(this);
        DefaultFooter footer = new DefaultFooter(this);
        rlLayout.setHeaderView(header);
        rlLayout.setFooterView(footer);
        rlLayout.sentRequest(new OperationListener() {
            @Override
            public void doRefresh() {
                new WelfarePresenter(TestActivity.this, TestActivity.this).gainWelfare("福利", 2);
            }

            @Override
            public void doLoad() {
                if (list.size() < limit)
                    new WelfarePresenter(TestActivity.this, TestActivity.this).gainWelfare("福利", 3);
                else {
                    rlLayout.setLoadOver();
                }
            }
        });
    }

    @Override
    public void updateLayout(List<Welfare> welfareList) {
        if (rlLayout.isRefresh()) {
            list.clear();
        }
        list.add(welfareList.get(0));
        list.add(welfareList.get(1));

        if (adapter == null) {
            adapter = new WelfareAdapter(this, list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
