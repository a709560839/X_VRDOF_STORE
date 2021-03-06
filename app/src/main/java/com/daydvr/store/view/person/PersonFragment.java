package com.daydvr.store.view.person;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daydvr.store.R;
import com.daydvr.store.base.BaseFragment;
import com.daydvr.store.bean.UserBean;
import com.daydvr.store.presenter.person.PersonContract;
import com.daydvr.store.presenter.person.PersonPresenter;
import com.daydvr.store.util.GlideImageLoader;
import com.daydvr.store.view.custom.RoundImageView;
import com.daydvr.store.view.login.LoginActivity;
import com.daydvr.store.view.setting.SettingActivity;

import static com.daydvr.store.base.PersonConstant.LOGIN_OK;
import static com.daydvr.store.base.PersonConstant.LOGIN_REQUEST_CODE;
import static com.daydvr.store.base.PersonConstant.USER_MESSGAE;
import static com.daydvr.store.base.PersonConstant.isLogin;

/**
 * @author a79560839
 * @version Created on 2018/1/5. 16:30
 */

public class PersonFragment extends BaseFragment implements PersonContract.View {

    private PersonPresenter mPresenter;

    private View mRootView;
    //private ViewGroup mHotViewGroup, mGameViewGroup, mVideoViewGroup;
    private ViewGroup mAppListViewGroup;
    private RoundImageView mHeadRoundImageView;
    private TextView mUserNameTextView;
    private TextView mIntegralTextView;
    private RoundImageView mLoginedRoundImageView;
    private ConstraintLayout mSettingConstraintLayout;
    private TextView mHeadTextView;
    private TextView mSignTextView;
    private ImageView mSignImageView;
    private ViewGroup mDownloadManagerViewGroup;
    private ViewGroup mRecodrViewGroup;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_person, container, false);
        mPresenter = new PersonPresenter(this);

        initView();

        return mRootView;
    }

    @Override
    protected void onFragmentFirstVisible() {
        initSearchBar(mRootView, null);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
    }

    private void initView() {
        mLoginedRoundImageView = mRootView.findViewById(R.id.riv_person_logined);
        mHeadTextView = mRootView.findViewById(R.id.tv_person_login);
        mSignTextView = mRootView.findViewById(R.id.tv_person_sign);
        mSignImageView = mRootView.findViewById(R.id.iv_person_sign);
        mUserNameTextView = mRootView.findViewById(R.id.tv_user_id);
        mIntegralTextView = mRootView.findViewById(R.id.tv_person_integral);
        mHeadRoundImageView = mRootView.findViewById(R.id.riv_person);
        mSettingConstraintLayout = mRootView.findViewById(R.id.cl_person_setting);
        mAppListViewGroup = mRootView.findViewById(R.id.cl_person_applist);
        mDownloadManagerViewGroup = mRootView.findViewById(R.id.cl_person_download);
        mRecodrViewGroup = mRootView.findViewById(R.id.cl_person_record);

        configComponent();
    }

    private void configComponent() {
        mHeadRoundImageView.setOnClickListener(mOnClickListener);
        mHeadTextView.setOnClickListener(mOnClickListener);
        mSettingConstraintLayout.setOnClickListener(mOnClickListener);
        mDownloadManagerViewGroup.setOnClickListener(mOnClickListener);
        mAppListViewGroup.setOnClickListener(mOnClickListener);
        mLoginedRoundImageView.setOnClickListener(mOnClickListener);
        mSignImageView.setOnClickListener(mOnClickListener);
        mSignTextView.setOnClickListener(mOnClickListener);
        mRecodrViewGroup.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_person_login:
                case R.id.riv_person:
                    if (!isLogin) {
                        mPresenter.intoLogin();
                    }
                    break;

                case R.id.riv_person_logined:
                    mPresenter.intoPersonMessage();
                    break;

                case R.id.cl_person_setting:
                    mPresenter.intoSetting();
                    break;

                case R.id.cl_person_applist:
                    mPresenter.intoAppList();
                    break;

                case R.id.cl_person_download:
                    mPresenter.intoDownloadManager();
                    break;

                case R.id.tv_person_sign:
                case R.id.iv_person_sign:
                    mPresenter.intoSign();
                    break;

                case R.id.cl_person_record:
                    mPresenter.intoRecord();
                    break;

                default:
                    break;

            }
        }
    };

    @Override
    public void setPresenter(PersonContract.Presenter presenter) {
        mPresenter = (PersonPresenter) presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.freeView();
        mPresenter = null;
    }

    @Override
    public void showPersonalMessage(Intent intent) {
        mHeadTextView.setVisibility(View.GONE);
        mHeadRoundImageView.setVisibility(View.GONE);

        mSignImageView.setVisibility(View.VISIBLE);
        mSignTextView.setVisibility(View.VISIBLE);
        mLoginedRoundImageView.setVisibility(View.VISIBLE);
        mUserNameTextView.setVisibility(View.VISIBLE);
        mIntegralTextView.setVisibility(View.VISIBLE);

        UserBean bean = intent.getParcelableExtra(USER_MESSGAE);

        mUserNameTextView.setText(getString(R.string.person_account) + bean.getUserName());
        mIntegralTextView.setText(getString(R.string.person_integral) + bean.getIntegral());
        GlideImageLoader.commonLoader(mRootView.getContext(), bean.getAvatarUrl(), mLoginedRoundImageView);
    }

    @Override
    public void jumpLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
    }

    @Override
    public void jumpPersonMessage() {
        Intent intent = new Intent(getActivity(), PersonMessageActivity.class);
        startActivity(intent);
    }

    @Override
    public void jumpSetting() {
        Intent i = new Intent(getActivity(), SettingActivity.class);
        startActivity(i);
    }

    @Override
    public void jumpAppList() {
        startActivity(new Intent(getActivity(), AppManagerActivity.class));
    }

    @Override
    public void jumpDownloadManager() {
        Intent intent = new Intent(getActivity(), DownloadManagerActivity.class);
        startActivity(intent);
    }

    @Override
    public void jumpSign() {
        Intent intent = new Intent(getActivity(), SignActivity.class);
        startActivity(intent);
    }

    @Override
    public void jumpRecord() {
        Intent intent = new Intent(getActivity(), ExchangeRecordsActivity.class);
        startActivity(intent);
    }

    @Override
    public Context getViewContext() {
        return mRootView.getContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LOGIN_OK) {
            switch (requestCode) {
                case LOGIN_REQUEST_CODE:
                    if (data != null) {
                        showPersonalMessage(data);
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
