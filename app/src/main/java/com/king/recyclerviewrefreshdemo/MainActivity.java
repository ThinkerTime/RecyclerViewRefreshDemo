package com.king.recyclerviewrefreshdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private Handler mHandler = new Handler();
    private List<String> data = new ArrayList<>();
    private RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, data);
    boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        //刷新图标颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        //进入自动刷新
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("data","刷新");
                        data.clear();
                        getData();
                    }
                }, 2000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * 当RecyclerView的滑动状态改变时触发
             * newState#0->手指离开屏幕$SCROLL_STATE_IDLE = 0
             * newState#1->手指触摸屏幕$SCROLL_STATE_DRAGGING = 1
             * newState#2->手指加速滑动并放开，此时滑动状态伴随SCROLL_STATE_IDLE$SCROLL_STATE_SETTLING = 2
             * 由于上拉加载更多的是在滑动到最后一个item时自动触发的，与手指是否在屏幕上无关，即与滑动状态无关
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("data", "StateChanged = " + newState);

            }
            /**
             * 当RecyclerView滑动时触发
             * 类似点击事件的MotionEvent.ACTION_MOVE
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("data", "onScrolled");
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                Log.d("data", "lastVisibleItemPosition:"+lastVisibleItemPosition);
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();
                    Log.d("test", "isRefreshing:"+isRefreshing);
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                                Log.d("test", "load more completed");
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });

        //添加点击事件
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("data", "item position = " + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d("data", "item long position = " + position);
            }
        });


    }

    private void initData() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);

    }

    /**
     * 获取测试数据
     */
    private void getData() {
        for (int i = 0; i < 6; i++) {
            data.add("201"+i+"-12-11 12:00");
        }
        adapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
        //加载数据完毕时需要将foot_item移除掉
        adapter.notifyItemRemoved(adapter.getItemCount());
    }
}
