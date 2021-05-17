package com.dyaco.spiritbike.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.SpaceItemDecoration;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.room.entity.HistoryEntity;
import com.dyaco.spiritbike.R;

import java.util.List;

import static com.dyaco.spiritbike.MyApplication.getInstance;


public class ProfileHistoryFragment extends BaseFragment {

    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ProfileHistoryAdapter profileHistoryAdapter;


    public ProfileHistoryFragment() {
    }

    public static ProfileHistoryFragment newInstance(String param1, String param2) {
        ProfileHistoryFragment fragment = new ProfileHistoryFragment();
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
        return inflater.inflate(R.layout.fragment_profile_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
       // recyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
     //   recyclerView.addItemDecoration(new SpaceItemDecoration(13));
        profileHistoryAdapter = new ProfileHistoryAdapter(mActivity);
        recyclerView.setAdapter(profileHistoryAdapter);


        profileHistoryAdapter.setOnItemClickListener(historyEntity -> {
            MyApplication.SSEB = false;
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("historyEntity", historyEntity);
            intent.putExtras(bundle);
            intent.setClass(mActivity, HistorySummaryDetailActivity.class);
            startActivity(intent);
        });


    //    getData();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("WEB_API_ROOM", "onResume: ");
        getData();
    }

    private void getData() {
        Log.d("WEB_API_ROOM", "ProfileHistoryFragment - getData: ");
        DatabaseManager.getInstance(MyApplication.getInstance()).getHistoryList(userProfileEntity.getUid(),
                new DatabaseCallback<HistoryEntity>() {
                    @Override
                    public void onDataLoadedList(List<HistoryEntity> historyEntityList) {
                        super.onDataLoadedList(historyEntityList);
                        profileHistoryAdapter.setData2View(historyEntityList);

                    //    DatabaseManager.getInstance(MyApplication.getInstance()).roomClear();
                    }
                });
    }

//    List<HistoryEntity> historyEntityList = new ArrayList<>();
//    private void getData() {
//        DatabaseManager.getInstance(mActivity).getHistoryFromUserProfile(userProfileEntity.getUid(),
//                new DatabaseCallback<UserProfileAndHistory>() {
//                    private int compare(HistoryEntity o1, HistoryEntity o2) {
//                        if (o1.getUpdateTime() == null || o2.getUpdateTime() == null)
//                            return 0;
//                        return o2.getUpdateTime().compareTo(o1.getUpdateTime());
//                    }
//
//                    @Override
//            public void onDataLoadedList(List<UserProfileAndHistory> userProfileAndHistoryList) {
//                super.onDataLoadedList(userProfileAndHistoryList);
//
//                for (UserProfileAndHistory userProfileAndHistory : userProfileAndHistoryList) {
//                    historyEntityList.addAll(userProfileAndHistory.historyEntityList);
//                }
//
//                historyEntityList.sort(this::compare);
//
////                historyEntityList.sort((HistoryEntity o1, HistoryEntity o2) -> {
////                    if (o1.getUpdateTime() == null || o2.getUpdateTime() == null)
////                        return 0;
////                    return o2.getUpdateTime().compareTo(o1.getUpdateTime());
////                });
//
//                profileHistoryAdapter.setData2View(historyEntityList);
//            }
//        });
//    }
}