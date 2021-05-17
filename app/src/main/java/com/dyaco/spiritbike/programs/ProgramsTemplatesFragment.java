package com.dyaco.spiritbike.programs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndTemplates;
import com.dyaco.spiritbike.R;

import java.util.ArrayList;
import java.util.List;

import static com.dyaco.spiritbike.MyApplication.getInstance;

public class ProgramsTemplatesFragment extends BaseFragment {
    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ProgramsTemplatesAdapter profileHistoryAdapter;

    public ProgramsTemplatesFragment() {
    }

    public static ProgramsTemplatesFragment newInstance(String param1, String param2) {
        ProgramsTemplatesFragment fragment = new ProgramsTemplatesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_programs_templates, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    List<TemplateEntity> templateEntityList = new ArrayList<>();
    private void initView(View view) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 4);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
      //  recyclerView.addItemDecoration(new GridSpaceItemDecoration(4,13));
//  recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        profileHistoryAdapter = new ProgramsTemplatesAdapter(mActivity, gridLayoutManager);
        recyclerView.setAdapter(profileHistoryAdapter);

        profileHistoryAdapter.setData2View(templateEntityList);

        profileHistoryAdapter.setOnItemClickListener(templateEntity -> {
            MyApplication.SSEB = false;
            Intent intent = new Intent(mActivity, TemplateStartActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("templateParentUid", userProfileEntity.getUid());
            bundle.putSerializable("templateEntity", templateEntity);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        getData();
    }

    private void getData() {
        Log.d("WEB_API_ROOM", "getData(): " + userProfileEntity.getUid());
        DatabaseManager.getInstance(MyApplication.getInstance()).getTemplateFromUserProfile(userProfileEntity.getUid(), new DatabaseCallback<UserProfileAndTemplates>() {
            @Override
            public void onDataLoadedList(List<UserProfileAndTemplates> userProfileAndTemplatesList) {
                super.onDataLoadedList(userProfileAndTemplatesList);

                for (UserProfileAndTemplates userProfileAndTemplates : userProfileAndTemplatesList) {
                    templateEntityList.addAll(userProfileAndTemplates.templateEntityList);
                }
                Log.d("WEB_API_ROOM", "onDataLoadedList: " + templateEntityList.size());
                profileHistoryAdapter.setData2View(templateEntityList);

            }
        });
    }
}