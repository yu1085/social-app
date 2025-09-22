package com.alicom.fusion.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @Package: com.example.fusionauthdemo
 * @Description:
 * @CreateDate: 2023/2/7
 */
public class OrdersFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        super.mTextView.setText("订单");
    }
}
