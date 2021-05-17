package com.dyaco.spiritbike.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.GridViewSpaceItemDecoration;
import com.dyaco.spiritbike.support.banner.util.BannerUtils;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndFavorites;
import com.dyaco.spiritbike.R;

import java.util.ArrayList;
import java.util.List;

import static com.dyaco.spiritbike.MyApplication.getInstance;

public class ProfileFavoritesFragment extends BaseFragment {
    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    private ProfileFavoritesAdapter profileFavoritesAdapter;
    private static final String TAG = "ProfileFavoritesFragmen";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileFavoritesFragment() {
        // Required empty public constructor
    }

    public static ProfileFavoritesFragment newInstance(String param1, String param2) {
        ProfileFavoritesFragment fragment = new ProfileFavoritesFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(View view) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 4);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridViewSpaceItemDecoration((int) BannerUtils.dp2px(28)));
        profileFavoritesAdapter = new ProfileFavoritesAdapter(mActivity, gridLayoutManager);
        recyclerView.setAdapter(profileFavoritesAdapter);
        //刪除
        profileFavoritesAdapter.setOnItemClickListener(bean -> {
            profileFavoritesAdapter.deleteMessage(bean.getFavoriteType());
            
            deleteFavorite(userProfileEntity.getUid(), bean.getFavoriteType());
        });

        profileFavoritesAdapter.setOnItemLongClickListener(this::goProgram);

        getData();
    }

    List<FavoritesEntity> favoritesEntityList = new ArrayList<>();
    private void getData() {
        DatabaseManager.getInstance(MyApplication.getInstance()).getFavoriteFromUserProfile(userProfileEntity.getUid(), new DatabaseCallback<UserProfileAndFavorites>() {
            @Override
            public void onDataLoadedList(List<UserProfileAndFavorites> userProfileAndFavoritesList) {
                super.onDataLoadedList(userProfileAndFavoritesList);

                for (UserProfileAndFavorites userProfileAndFavorites : userProfileAndFavoritesList) {
                    favoritesEntityList.addAll(userProfileAndFavorites.favoritesEntityList);
                }
                initData();

                DatabaseManager.getInstance(MyApplication.getInstance()).roomClear();
            }
        });
    }

    private void initData() {

        profileFavoritesAdapter.setData2View(favoritesEntityList);
    }


    private void deleteFavorite(int favoriteParentUid, int favoriteType) {
        DatabaseManager.getInstance(MyApplication.getInstance()).
                deleteFavorite(favoriteParentUid, favoriteType,
                        new DatabaseCallback<FavoritesEntity>() {
                            @Override
                            public void onDeleted() {
                                super.onDeleted();


                            }

                            @Override
                            public void onError(String err) {
                                super.onError(err);
                                Log.i(TAG, "onError: " + err);
                            }
                        });
    }

    private void goProgram(FavoritesEntity bean) {
//        Intent intent = new Intent(mActivity, DashboardActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt("openProgram", ProgramsEnum.getProgram(bean.getFavoriteType()).getCode());
//        intent.putExtras(bundle);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(intent);
    }
}