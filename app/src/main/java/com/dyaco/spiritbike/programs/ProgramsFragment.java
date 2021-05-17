package com.dyaco.spiritbike.programs;

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


public class ProgramsFragment extends BaseFragment {

    //private RadioButton m_rbFitnessTests_Programs;
    private TextView m_tvPrograms_Programs;
   // private TextView m_tvFitnessTests_Programs;
    private TextView m_tvTemplates_Programs;
    private boolean Open_Templates;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Open_Templates = getArguments().getBoolean("Open_Templates", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_programs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(false, false,0, -1);


        RadioGroup rgOption_Profile = view.findViewById(R.id.rgOption_Programs);

        RadioButton m_rbPrograms_Programs = view.findViewById(R.id.rbPrograms_Programs);
      //  m_rbFitnessTests_Programs = view.findViewById(R.id.rbFitnessTests_Programs);
        RadioButton m_rbTemplates_Programs = view.findViewById(R.id.rbTemplates_Programs);

        m_tvPrograms_Programs = view.findViewById(R.id.tvPrograms_Programs);
     //   m_tvFitnessTests_Programs = view.findViewById(R.id.tvFitnessTests_Programs);
        m_tvTemplates_Programs = view.findViewById(R.id.tvTemplates_Programs);

        //預設選項

        fragmentManager = ((FragmentActivity)mActivity).getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (Open_Templates) {
            //   rgOption_Profile.check(R.id.rbTemplates_Programs);
            m_rbTemplates_Programs.setChecked(true);
            m_tvPrograms_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));
       //     m_tvFitnessTests_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBCBCBC));
            m_tvTemplates_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorE4002B));
            transaction.replace(R.id.fl_content, new ProgramsTemplatesFragment());
        } else {
            m_rbPrograms_Programs.setChecked(true);
            transaction.replace(R.id.fl_content, ProgramsProgramFragment.newInstance("", 0));
        }
        transaction.commit();

        rgOption_Profile.setOnCheckedChangeListener((group, checkedId) -> {
        //    ((DashboardActivity)requireActivity()).startSleepTimer();
            FragmentTransaction transaction1 = fragmentManager.beginTransaction();
         //   transaction1.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            if (checkedId == R.id.rbPrograms_Programs) {

                m_tvPrograms_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorE4002B));
              //  m_tvFitnessTests_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBCBCBC));
                m_tvTemplates_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));

                transaction1.replace(R.id.fl_content, new ProgramsProgramFragment());

//            } else if (checkedId == R.id.rbFitnessTests_Programs) {
//
//                m_tvPrograms_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBCBCBC));
//                m_tvFitnessTests_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.color2F3031));
//                m_tvTemplates_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBCBCBC));
//
//                transaction1.replace(R.id.fl_content, new ProgramsFitnesstestFragment());

            } else if (checkedId == R.id.rbTemplates_Programs) {
                m_tvPrograms_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));
              //  m_tvFitnessTests_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBCBCBC));
                m_tvTemplates_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorE4002B));

                transaction1.replace(R.id.fl_content, new ProgramsTemplatesFragment());

            }
            transaction1.commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        m_tvPrograms_Programs = null;
        m_tvTemplates_Programs = null;
        fragmentManager = null;
    }
}