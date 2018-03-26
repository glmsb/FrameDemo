package com.demo.wyd.framedemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.demo.wyd.framedemo.R;
import com.demo.wyd.framedemo.adapter.WelfareAdapter;
import com.demo.wyd.framedemo.bean.Welfare;
import com.demo.wyd.framedemo.customView.MyCallBack;
import com.demo.wyd.framedemo.customView.OverLayCardLayoutManager;
import com.demo.wyd.framedemo.iView.IViewWelfare;
import com.demo.wyd.framedemo.presenter.WelfarePresenter;

import java.util.List;

/**
 * V层（需要回调接口）
 * 特点：需要持有P层的引用
 */
public class MainActivity extends AppCompatActivity implements IViewWelfare {

    private RecyclerView recyclerView;
    private WelfareAdapter adapter;
    private TextView tvGain;
    private Button btnPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);
        init();

        tvGain.setOnClickListener(v ->
                new WelfarePresenter(MainActivity.this, MainActivity.this).gainWelfare("福利", 10));
        btnPush.setOnClickListener(v ->
                ActivityUtils.startActivity(TestActivity.class));
    }

    private void init() {
        tvGain = findViewById(R.id.btn_gain);
        btnPush = findViewById(R.id.btn_push);
        recyclerView = findViewById(R.id.rv_welfare);
        recyclerView.setLayoutManager(new OverLayCardLayoutManager());
    }

    @Override
    public void updateLayout(List<Welfare> welfareList) {
        if (adapter == null) {
            adapter = new WelfareAdapter(this, welfareList);
        }
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new MyCallBack(recyclerView, adapter, welfareList);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }
}
