package com.daydvr.store.view.person;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daydvr.store.R;
import com.daydvr.store.base.BaseActivity;
import com.daydvr.store.bean.GameListBean;
import com.daydvr.store.presenter.person.DownloadManagerContract;
import com.daydvr.store.presenter.person.DownloadManagerPresenter;
import com.daydvr.store.util.AppInfoUtil;
import com.daydvr.store.util.DensityUtil;
import com.daydvr.store.view.adapter.GameListAdapter;
import com.daydvr.store.view.custom.CommonToolbar;
import com.daydvr.store.view.game.GameDetailActivity;

import java.util.ArrayList;
import java.util.List;

import static com.daydvr.store.base.BaseConstant.CURRENT_UPDTAE_UI;
import static com.daydvr.store.base.BaseConstant.GAME_MANAGER_UI_UPDATE;
import static com.daydvr.store.base.GameConstant.APK_ID;
import static com.daydvr.store.base.PersonConstant.threadTest;
import static com.daydvr.store.util.AppInfoUtil.notifyClearAll;

/**
 * @author LoSyc
 * @version Created on 2018/1/8. 14:23
 */

public class DownloadManagerActivity extends BaseActivity implements DownloadManagerContract.View, View.OnClickListener {

    private DownloadManagerPresenter mPresenter;
    private GameListAdapter mGameAdapter;

    private CommonToolbar mToolbar;
    private RecyclerView mDownloadManagerRecyclerView;
    private TextView mTipTextView;
    private ImageView mTipImageView;
    private ConstraintLayout mRecentDownloadConstraintLayout;
    private TextView mContinueDownloadTextView;
    private TextView mCancelAllTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadmanager);
        mPresenter = new DownloadManagerPresenter(this);

        initHandler();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CURRENT_UPDTAE_UI = GAME_MANAGER_UI_UPDATE;
    }

    private void initHandler() {

    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mTipTextView = findViewById(R.id.tv_tip_download_manager);
        mTipImageView = findViewById(R.id.iv_tip_download_manager);
        mDownloadManagerRecyclerView = findViewById(R.id.rv_download_manager_list);
        mRecentDownloadConstraintLayout = findViewById(R.id.cl_recent_download);
        mContinueDownloadTextView = findViewById(R.id.tv_continue_download);
        mCancelAllTextView = findViewById(R.id.tv_cancel_all);

        configComponent();
    }

    private void configComponent() {
        mToolbar.initmToolBar(this, true);
        mToolbar.setCenterTitle(R.string.person_game_manager);
        mToolbar.setPadding(0, DensityUtil.getStatusBarHeight(this), 0, 0);
        mContinueDownloadTextView.setOnClickListener(this);
        mCancelAllTextView.setOnClickListener(this);

        mDownloadManagerRecyclerView.setHasFixedSize(true);
        mDownloadManagerRecyclerView.setNestedScrollingEnabled(false);
        mDownloadManagerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDownloadManagerRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initData() {
        mPresenter.loadDownloadGame();
    }

    @Override
    public void setPresenter(DownloadManagerContract.Presenter presenter) {
        mPresenter = (DownloadManagerPresenter) presenter;
    }

    @Override
    public <T> void showDownloadGame(List<T> beans) {
        if (mGameAdapter == null) {
            mGameAdapter = new GameListAdapter(this, false, 15);
            mGameAdapter.setListener(mItemListener);
            mDownloadManagerRecyclerView.setAdapter(mGameAdapter);
            mGameAdapter.setDatas((ArrayList<GameListBean>) beans);
        } else {
            mGameAdapter.notifyDataSetChanged();
        }
        if (beans.size() > 0) {
            mTipTextView.setVisibility(View.GONE);
            mTipImageView.setVisibility(View.GONE);
            mRecentDownloadConstraintLayout.setVisibility(View.VISIBLE);
        } else {
            mTipTextView.setVisibility(View.VISIBLE);
            mTipImageView.setVisibility(View.VISIBLE);
            mRecentDownloadConstraintLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void jumpGameDetail(int apkId) {
        Intent intent = new Intent(this, GameDetailActivity.class);
        intent.putExtra(APK_ID, apkId);
        startActivity(intent);
    }

    @Override
    public RecyclerView getListView() {
        return mDownloadManagerRecyclerView;
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    private GameListAdapter.ItemOnClickListener mItemListener = new GameListAdapter.ItemOnClickListener() {

        @Override
        public void onItemClick(View view, GameListBean bean) {
            mPresenter.intoGameDetail(bean.getId());
        }

        @Override
        public void onButtonClick(final View view, final GameListBean bean) {
            GameListAdapter.ViewHolder holder = (GameListAdapter.ViewHolder) mDownloadManagerRecyclerView
                    .getChildViewHolder(view);
            mPresenter.downloadManager(holder, bean);

            AppInfoUtil.setHolderDownloadProgress(bean, holder);
        }

        @Override
        public void onCancelButtonClick(View view, GameListBean bean) {
            mGameAdapter.notifyDataSetChanged();
            if (mPresenter.getDatasSize() > 0) {
                mTipTextView.setVisibility(View.GONE);
                mTipImageView.setVisibility(View.GONE);
                mRecentDownloadConstraintLayout.setVisibility(View.VISIBLE);
            } else {
                mTipTextView.setVisibility(View.VISIBLE);
                mTipImageView.setVisibility(View.VISIBLE);
                mRecentDownloadConstraintLayout.setVisibility(View.GONE);
            }
            if (threadTest.get(bean.getId()) != null) {

            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_all:
                notifyClearAll();
                mPresenter.cancelAll();
                break;

            case R.id.tv_continue_download:
                mPresenter.continueAll();
                break;

            default:
                break;
        }
    }
}
