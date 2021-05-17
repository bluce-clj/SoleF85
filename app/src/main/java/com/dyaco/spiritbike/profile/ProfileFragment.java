package com.dyaco.spiritbike.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BaseFragment;

import static com.dyaco.spiritbike.MyApplication.getInstance;

public class ProfileFragment extends BaseFragment {


    private TextView m_tvHistory_Programs;
    private TextView m_tvInfo_Programs;
    private TextView m_tvFavorites_Programs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(false, false,0, -1);

        RadioGroup rgOption_Profile = view.findViewById(R.id.rgOption_Profile);

        RadioButton m_rbInfo_Programs = view.findViewById(R.id.rbInfo_Profile);
//        RadioButton m_rbHistory_Programs = view.findViewById(R.id.rbHistory_Profile);
//        RadioButton m_rbFavorites_Programs = view.findViewById(R.id.rbFavorite_Profile);

        m_tvInfo_Programs = view.findViewById(R.id.tvInfo_Profile);
        m_tvHistory_Programs = view.findViewById(R.id.tvHistory_Profile);
        m_tvFavorites_Programs = view.findViewById(R.id.tvFavorite_Profile);

        //預設選項
        m_rbInfo_Programs.setChecked(true);

        final FragmentManager fragmentManager = ((FragmentActivity)mActivity).getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fl_content, new ProfileInfoFragment());
        transaction.commit();

        rgOption_Profile.setOnCheckedChangeListener((group, checkedId) -> {

           // ((DashboardActivity)requireActivity()).startSleepTimer();

            FragmentTransaction transaction1 = fragmentManager.beginTransaction();
            if (checkedId == R.id.rbInfo_Profile) {
                m_tvInfo_Programs.setTextColor(ContextCompat.getColor(getInstance(), R.color.colorE4002B));
                m_tvHistory_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));
                m_tvFavorites_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));

                transaction1.replace(R.id.fl_content, new ProfileInfoFragment());

            } else if (checkedId == R.id.rbHistory_Profile) {

                m_tvInfo_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));
                m_tvHistory_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorE4002B));
                m_tvFavorites_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));
             //   transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction1.replace(R.id.fl_content, new ProfileHistoryFragment());

            } else if (checkedId == R.id.rbFavorite_Profile) {
                m_tvInfo_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));
                m_tvHistory_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));
                m_tvFavorites_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorE4002B));
             //   transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction1.replace(R.id.fl_content, new ProfileFavoritesFragment());
            }
            transaction1.commit();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        m_tvFavorites_Programs = null;
        m_tvHistory_Programs = null;
        m_tvInfo_Programs = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}