package com.mrw.wzmrecyclerview_sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mrw.wzmrecyclerview.Divider.BaseItemDecoration;
import com.mrw.wzmrecyclerview.HeaderAndFooter.OnItemClickListener;
import com.mrw.wzmrecyclerview.HeaderAndFooter.OnItemLongClickListener;
import com.mrw.wzmrecyclerview.PullToLoad.OnLoadListener;
import com.mrw.wzmrecyclerview.PullToLoad.PullToLoadRecyclerView;
import com.mrw.wzmrecyclerview.PullToRefresh.OnRefreshListener;
import com.mrw.wzmrecyclerview.SimpleAdapter.SimpleAdapter;
import com.mrw.wzmrecyclerview.SimpleAdapter.ViewHolder;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/30.
 */
public class StaggredGridManagerActivity extends AppCompatActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, StaggredGridManagerActivity.class);
        context.startActivity(intent);
    }

    private PullToLoadRecyclerView rcv;
    private ArrayList<String> imgs;
    private Handler handler;

    private ArrayList<Integer> mItemHeights = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_manager);
        imgs = ImgDataUtil.getImgDatas();
        handler = new Handler();

        rcv = (PullToLoadRecyclerView) findViewById(R.id.rcv);

        rcv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        for (int i = 0; i < 10;i++) {
            mItemHeights.add((int) ((i+1)/5.0*500));
        }


//        设置适配器，封装后的适配器只需要实现一个函数
        rcv.setAdapter(new SimpleAdapter<String>(this, imgs, R.layout.item_test) {

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                ViewGroup.LayoutParams layoutParams = holder.getConvertView().getLayoutParams();
                layoutParams.height = mItemHeights.get(position%10);
                holder.getConvertView().setLayoutParams(layoutParams);

                super.onBindViewHolder(holder, position);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, String data) {
                ImgDataUtil.loadImage(mContext,data, holder.<ImageView>getView(R.id.iv));
            }
        });
//        设置刷新监听
        rcv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onStartRefreshing() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgs.clear();
                        imgs.addAll(ImgDataUtil.getImgDatas());
                        rcv.completeRefresh();
                    }
                }, 1000);
            }
        });
//        设置加载监听
        rcv.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onStartLoading(int skip) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgs.addAll(ImgDataUtil.getImgDatas());
                        rcv.completeLoad();
                    }
                }, 1000);
            }
        });
//        设置分割线
        rcv.addItemDecoration(new BaseItemDecoration(this, R.color.colorAccent));

        rcv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Toast.makeText(StaggredGridManagerActivity.this, "item" + position + " has been clicked", Toast.LENGTH_SHORT).show();
            }
        });
        rcv.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                Toast.makeText(StaggredGridManagerActivity.this, "item" + position + " has been long clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_header:
                rcv.addHeaderView(getHeaderView());
                break;
            case R.id.btn_remove_header:
                if (headerViews.size() == 0) return;
                rcv.removeHeaderView(headerViews.get(headerViews.size() - 1));
                headerViews.remove(headerViews.size() - 1);
                break;
            case R.id.btn_add_footer:
                rcv.addFooterView(getFooterView());
                break;
            case R.id.btn_remove_footer:
                if (footerViews.size() == 0) return;
                rcv.removeFooterView(footerViews.get(footerViews.size() - 1));
                footerViews.remove(footerViews.size() - 1);
                break;
        }
    }

    private ArrayList<View> headerViews = new ArrayList<>();
    private ArrayList<View> footerViews = new ArrayList<>();

    private View getHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.item_header, rcv, false);
        ((TextView) view.findViewById(R.id.tv)).setText("Header" + headerViews.size());
        headerViews.add(view);
        return view;
    }

    private View getFooterView() {
        View view = getLayoutInflater().inflate(R.layout.item_footer, rcv, false);
        ((TextView) view.findViewById(R.id.tv)).setText("Footer" + footerViews.size());
        footerViews.add(view);
        return view;
    }


}
