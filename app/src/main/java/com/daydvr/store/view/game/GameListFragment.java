package com.daydvr.store.view.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import com.daydvr.store.R;
import com.daydvr.store.base.BaseNotifyDatasFragment;
import com.daydvr.store.bean.GameListBean;
import com.daydvr.store.presenter.game.GameListContract;
import com.daydvr.store.presenter.game.GameListPresenter;
import com.daydvr.store.util.AppInfoUtil;
import com.daydvr.store.view.adapter.GameListAdapter;
import com.daydvr.store.view.custom.AppNestedScrollView;
import com.daydvr.store.view.custom.BannerLayout;
import com.daydvr.store.util.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

import static com.daydvr.store.base.BaseConstant.GAME_LIST_UI_UPDATE;
import static com.daydvr.store.base.GameConstant.APK_ID;

/**
 * @author LoSyc
 * @version Created on 2017/12/26. 19:23
 */

public class GameListFragment extends BaseNotifyDatasFragment implements GameListContract.View {
    public static final String TAG = "daydvr.GameListFragment";

    private boolean isLoadData;
    private int mCurrentPage = 1;
    private int mLastPage = 3;
    private GameListPresenter mPresenter;
    private View mRootView;
    private RecyclerView mGameRecyclerView;
    private GameListAdapter mGameAdapter;
    private BannerLayout mBannerLayout;
    private ConstraintLayout mActionTypeConstraintLayout;
    private ConstraintLayout mScienceTypeConstraintLayout;
    private ConstraintLayout mAdventureConstraintLayout;
    private ConstraintLayout mDreamConstraintLayout;
    private AppNestedScrollView mAppNestedScrollView;
    private ScrollViewListener mScrollViewListener;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_game_list, container, false);
        mPresenter = new GameListPresenter(this);

        initHandler();
        initView();

        initDatas();
        return mRootView;
    }

    @Override
    protected int getCurrentUiView() {
        return GAME_LIST_UI_UPDATE;
    }

    @Override
    protected void notifyDatasForPresenter() {
        mPresenter.notifyDownloadDatas(this);
    }

    @Override
    public GameListAdapter getListAdapter() {
        return mGameAdapter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.freeView();
        mPresenter = null;
    }

    private void initHandler() {
        mScrollViewListener = new ScrollViewListener();
        mScrollViewListener.setHandler(mScrollViewHandle);
    }

    private void initView() {
        mAppNestedScrollView = mRootView.findViewById(R.id.ansv_game_scroll);
        mBannerLayout = mRootView.findViewById(R.id.bl_game_list_ad);
        mGameRecyclerView = mRootView.findViewById(R.id.rv_game_list);
        mActionTypeConstraintLayout = mRootView.findViewById(R.id.cl_person_hot);
        mScienceTypeConstraintLayout = mRootView.findViewById(R.id.cl_science_type);
        mAdventureConstraintLayout = mRootView.findViewById(R.id.cl_adventure_type);
        mDreamConstraintLayout = mRootView.findViewById(R.id.cl_dream_type);


        configComponent();
    }

    private void configComponent() {
        initSearchBar(mRootView, mScrollViewListener);
        mBannerLayout.setImageLoader(new GlideImageLoader());
        mAppNestedScrollView.setNestedScrollViewListener(mScrollViewListener);

        mActionTypeConstraintLayout.setOnClickListener(mTypeButtonListener);
        mScienceTypeConstraintLayout.setOnClickListener(mTypeButtonListener);
        mAdventureConstraintLayout.setOnClickListener(mTypeButtonListener);
        mDreamConstraintLayout.setOnClickListener(mTypeButtonListener);

        mGameRecyclerView.setHasFixedSize(true);
        mGameRecyclerView.setNestedScrollingEnabled(false);
        mGameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLinearLayoutManager = new LinearLayoutManager(mRootView.getContext(), LinearLayoutManager.VERTICAL, false);
        mLinearLayoutManager.setSmoothScrollbarEnabled(true);
        mLinearLayoutManager.setAutoMeasureEnabled(true);
        mGameRecyclerView.setLayoutManager(mLinearLayoutManager);
    }

    private void initDatas() {
        mPresenter.loadAD();
        mPresenter.loadGame(mCurrentPage++);
    }

    @Override
    public void setPresenter(GameListContract.Presenter presenter) {
        mPresenter = (GameListPresenter) presenter;
    }

    @Override
    public <T> void showAD(List<T> beans) {
        mBannerLayout.setViewUrls((ArrayList<String>) beans);
//        List<View> testList = new ArrayList<>();
//        ImageView imageView1 = new ImageView(mRootView.getContext());
//        imageView1.setImageResource(R.mipmap.test3);
//        ImageView imageView2 = new ImageView(mRootView.getContext());
//        imageView2.setImageResource(R.mipmap.test2);
//        testList.add(imageView1);
//        testList.add(imageView2);
//        mBannerLayout.setViews(testList);
    }

    @Override
    public <T> void showGame(List<T> beans, int start, int count) {
        if (mGameAdapter == null) {
            mGameAdapter = new GameListAdapter(mRootView.getContext(), false, 15);
            mGameAdapter.setListener(mItemListener);
            mGameRecyclerView.setAdapter(mGameAdapter);
            mGameAdapter.setDatas((ArrayList<GameListBean>) beans);
        } else {
            mGameAdapter.notifyItemRangeInserted(start, count);
        }
        dismissLoadingDialog();
    }

    @Override
    public <T> void showCategoryGame(List<T> beans) {
        if (mGameAdapter == null) {
            mGameAdapter = new GameListAdapter(mRootView.getContext(), false, 15);
            mGameAdapter.setListener(mItemListener);
            mGameRecyclerView.setAdapter(mGameAdapter);
        }
        mGameAdapter.setDatas((ArrayList<GameListBean>) beans);
        mGameAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDownload() {

    }

    @Override
    public void jumpGameDetail(int apkId) {
        Intent intent = new Intent(mRootView.getContext(), GameDetailActivity.class);
        intent.putExtra(APK_ID, apkId);
        startActivity(intent);
    }

    @Override
    public Context getViewContext() {
        return mRootView.getContext();
    }

    /**
     * Listener 与 Handler
     */
    private GameListAdapter.ItemOnClickListener mItemListener = new GameListAdapter.ItemOnClickListener() {

        @Override
        public void onItemClick(View view, GameListBean bean) {
            mPresenter.intoGameDetail(bean.getId());
        }

        @Override
        public void onButtonClick(final View view, final GameListBean bean) {
            final GameListAdapter.ViewHolder holder = (GameListAdapter.ViewHolder) mGameRecyclerView.getChildViewHolder(view);
            mPresenter.downloadManager(holder, bean);
            AppInfoUtil.setHolderDownloadProgress(bean, holder);
        }



        @Override
        public void onCancelButtonClick(View view, GameListBean bean) {

        }
    };

    private View.OnClickListener mTypeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cl_person_hot:
                    mPresenter.gameCategory(10);
                    break;

                case R.id.cl_adventure_type:
                    mPresenter.gameCategory(30);
                    break;

                case R.id.cl_dream_type:
                    mPresenter.gameCategory(20);
                    break;

                case R.id.cl_science_type:
                    mPresenter.gameCategory(40);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * Listener
     */
    private ScrollViewHandle mScrollViewHandle = new ScrollViewHandle() {
        @Override
        public void onNestedScrollChanged(AppNestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY == (nestedScrollView.getChildAt(0).getMeasuredHeight() - nestedScrollView.getMeasuredHeight())) {
                mPresenter.loadGame(mCurrentPage++);
            }
        }
    };

    /**
     * 内部类
     */

    class ScrollViewListener extends BaseScrollViewLisenter {

        @Override
        protected int getBannerHeight() {
            return mBannerLayout.getHeight();
        }

        @Override
        protected View getSearchBackground() {
            return mRootView.findViewById(R.id.v_search_bg);
        }

        @Override
        protected Activity getHostActivity() {
            return getActivity();
        }

        @Override
        protected View getSearchToolbar() {
            return mRootView.findViewById(R.id.tv_search);
        }
    }
}
