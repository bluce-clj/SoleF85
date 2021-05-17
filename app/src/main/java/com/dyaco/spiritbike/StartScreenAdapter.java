package com.dyaco.spiritbike;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.room.UserProfileEntity;

import java.util.List;


import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.isConnected;

public class StartScreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserProfileEntity> profileBeanList;
    private View mGuestView;
    private View mAddUserView;
    private View mPlaceHolderView;
    public static final int TYPE_GUEST_VIEW = 0;
    public static final int TYPE_ADD_USER_VIEW = 1;
    public static final int TYPE_USER_PROFILE_VIEW = 2;
    public static final int TYPE_PLACE_HOLDER_VIEW = 3;
    private GridLayoutManager gridLayoutManager;
    private final Context context;

    StartScreenAdapter(Context context, GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
        this.context = context;
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (profileBeanList.size() == 0) {
//                    return 4; // the item in position now takes up 4 spans
//                }
//                return 1;
//
//            }
//        });
    }

    void setData2View(List<UserProfileEntity> list) {
        profileBeanList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (mGuestView != null && viewType == TYPE_GUEST_VIEW) {
            return new MyRecyclerViewHolder(mGuestView);
        }

        if (mAddUserView != null && viewType == TYPE_ADD_USER_VIEW) {
            return new MyRecyclerViewHolder(mAddUserView);
        }

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_start_screen_profile_list, parent, false);
        return new MyRecyclerViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        int viewType = getItemViewType(i);
        final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;

        switch (viewType) {
            case TYPE_USER_PROFILE_VIEW:
                final int position = viewHolder.getAdapterPosition() - 1;
                final UserProfileEntity profileBean = profileBeanList.get(position);

                viewHolder.tvUserName.setText(profileBean.getUserName());

                if (checkStr(profileBean.getSoleHeaderImgUrl()) && isConnected(context)) {
                    //有網路圖片
                    GlideApp.with(context)
                            .load(profileBean.getSoleHeaderImgUrl())
                            .centerInside()
                            .error(profileBean.getUserImage())
                            .circleCrop()
                            // .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(viewHolder.ivUserImage);
                } else {
                    viewHolder.ivUserImage.setImageResource(profileBean.getUserImage());
                }

                //   viewHolder.ivUserImageBackground.setImageResource(R.drawable.avatar_start_no);
                viewHolder.vBase.setOnClickListener(view -> onItemClickListener.onItemClick(profileBean, 1));
                break;

            case TYPE_GUEST_VIEW:
                //GUEST
                UserProfileEntity userProfileEntity = new UserProfileEntity();
                userProfileEntity.setUserName("GUEST");
                userProfileEntity.setUserImage(R.drawable.avatar_header_guest);
                userProfileEntity.setUserType(0);

                viewHolder.ivUserImage.setImageResource(R.drawable.avatar_start_guest);
                //  viewHolder.ivUserImageBackground.setImageResource(R.drawable.avatar_start_guest);
                viewHolder.tvUserName.setText("Guest");
                viewHolder.vBase.setOnClickListener(view -> onItemClickListener.onItemClick(userProfileEntity, 0));
                break;

            case TYPE_ADD_USER_VIEW:
                viewHolder.ivUserImage.setImageResource(R.drawable.avatar_start_add);
                //  viewHolder.ivUserImageBackground.setImageResource(R.drawable.avatar_start_add);
                viewHolder.tvUserName.setText("");
                viewHolder.vBase.setOnClickListener(view -> onItemClickListener.onItemClick(null, 2));
                break;

            case TYPE_PLACE_HOLDER_VIEW:
                viewHolder.ivUserImage.setImageResource(R.drawable.avatar_start_no);
                viewHolder.tvUserName.setText("");
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = profileBeanList == null ? 0 : profileBeanList.size();

        if (mGuestView == null && mAddUserView == null && mPlaceHolderView == null) {
            return 0;
        } else {
            return 10;
        }

//        if (mGuestView == null && mAddUserView == null && mPlaceHolderView == null) {
//            return count;
//        } else if (mAddUserView == null) {
//            return count + 1;
//        } else if (mAddUserView != null && mPlaceHolderView == null) {
//            return count + 2;
//        } else {
//            return count + 3;
//        }

//        if (mGuestView == null && mAddUserView == null) {
//            //只有user profile
//            return count;
//        } else if (mGuestView == null) {
//            //add user view + userProfile
//            return count + 1;
//        } else if (mAddUserView == null) {
//            //guest + userProfile
//            return count + 1;
//        } else {
//            //add user view + guest + userProfile
//            return count + 2;
//        }
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final ImageView ivUserImage;
        //   private final ImageView ivUserImageBackground;
        private final View vBase;


        MyRecyclerViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.userName);
            //   ivUserImageBackground = itemView.findViewById(R.id.userImageBackground);
            ivUserImage = itemView.findViewById(R.id.userImage);
            vBase = itemView.findViewById(R.id.cl_avatar_base);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(UserProfileEntity bean, int type);
    }

    private OnItemClickListener onItemClickListener;

    //使用
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public int getItemViewType(int position) {

        if (mGuestView == null && mAddUserView == null && mPlaceHolderView == null) {
            return TYPE_USER_PROFILE_VIEW;
        }
        if (mGuestView != null && position == 0) {
            //第一個item guest
            return TYPE_GUEST_VIEW;
        }
        // if (mAddUserView != null && position == getItemCount() - 1) {
        if (mAddUserView != null && position == profileBeanList.size() + 1) {
            //最後一個, add user
            return TYPE_ADD_USER_VIEW;
        }

        if (mPlaceHolderView != null && position >= (profileBeanList.size() + 2)) {
            return TYPE_PLACE_HOLDER_VIEW;
        }

        return TYPE_USER_PROFILE_VIEW;
    }

    //  public View getHeaderView() {
    //     return mHeaderView;
    //  }

    public void setGuestView(View guestView) {
        mGuestView = guestView;
     //   notifyItemInserted(0);
    }

    //  public View getFooterView() {
    //     return mFooterView;
    // }

    public void setAddUserView(View addUserView) {
        mAddUserView = addUserView;
        //   notifyItemInserted(getItemCount() - 1);
   //     notifyItemInserted(profileBeanList.size() + 1);
    }

    public void setPlaceHolderView(View placeHolderView) {
        mPlaceHolderView = placeHolderView;
    //    notifyItemInserted(profileBeanList.size() + 2);
    }
}
