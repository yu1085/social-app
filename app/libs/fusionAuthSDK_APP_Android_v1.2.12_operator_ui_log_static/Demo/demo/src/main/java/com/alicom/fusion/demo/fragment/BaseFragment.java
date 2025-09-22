package com.alicom.fusion.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alicom.fusion.auth.demo.R;


/**
 * @Package: com.example.fusionauthdemo
 * @Description:
 * @CreateDate: 2023/2/7
 */
public class BaseFragment extends Fragment {
    private View root;
    TextView mTextView;
    LinearLayout mPersonalInfoLayout;

    TextView mPersonInfoTitleTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_base, container, false);
        }

        mTextView = root.findViewById(R.id.title);
        mPersonalInfoLayout = root.findViewById(R.id.personal_info_layout);
        mPersonalInfoLayout.setVisibility(View.INVISIBLE);
        mPersonInfoTitleTv = root.findViewById(R.id.personal_info_title);
        return root;

    }
}
