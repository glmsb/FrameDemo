package com.demo.wyd.framedemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.demo.wyd.framedemo.R;
import com.demo.wyd.framedemo.adapter.WelfareAdapter;
import com.demo.wyd.framedemo.bean.Welfare;
import com.demo.wyd.framedemo.iView.IViewWelfare;
import com.demo.wyd.framedemo.presenter.WelfarePresenter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IViewWelfare {

    private RecyclerView recyclerView;
    private WelfareAdapter adapter;
    private TextView tvGain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);
        init();

        tvGain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WelfarePresenter(getBaseContext(), MainActivity.this).gainWelfare();
            }
        });
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_welfare);
        tvGain = ((TextView) findViewById(R.id.btn_gain));
    }

    @Override
    public void updateLayout(List<Welfare> welfareList) {
        if (adapter == null) {
            adapter = new WelfareAdapter(this, welfareList);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
