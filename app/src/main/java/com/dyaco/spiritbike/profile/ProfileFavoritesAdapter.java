package com.dyaco.spiritbike.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.R;

import java.util.List;

public class ProfileFavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private List<FavoritesEntity> favoritesPojoList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    GridLayoutManager gridLayoutManager;

    private Context mContext;

    ProfileFavoritesAdapter(Context context, GridLayoutManager gridLayoutManager) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.gridLayoutManager = gridLayoutManager;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (favoritesPojoList == null || favoritesPojoList.size() == 0) {
                    return 4; // the item in position now takes up 4 spans
                }
                return 1;
            }
        });
    }

    void setData2View(List<FavoritesEntity> list) {
        favoritesPojoList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            view = mLayoutInflater.inflate(R.layout.items_favorites_list, parent, false);
            viewHolder = new MyRecyclerViewHolder(view);
        } else {
            view = mLayoutInflater.inflate(R.layout.items_no_data2, parent, false);
            viewHolder = new ViewHolderEmpty(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        int viewType = getItemViewType(i);
        if (viewType == VIEW_TYPE_MESSAGE) {
            final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
            final int position = viewHolder.getAdapterPosition();
            final FavoritesEntity messageData = favoritesPojoList.get(position);

            String title = messageData.getFavoriteName();
            viewHolder.tvTitle.setText(title);
            viewHolder.image.setImageResource(ProgramsEnum.getProgram(messageData.getFavoriteType()).getDiagramResLevel());

            viewHolder.cl_base.setOnClickListener(v -> onItemLongClickListener.onItemLongClick(messageData));

//            viewHolder.cl_base.setOnTouchListener((v, event) -> {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        viewHolder.ivDel.setImageResource(R.drawable.icon_close_tab);
//                        viewHolder.cl_base.setBackgroundResource(R.drawable.program_preview_tab);
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_MOVE:
//                        viewHolder.ivDel.setImageResource(R.drawable.icon_close);
//                        viewHolder.cl_base.setBackgroundResource(R.drawable.program_preview_default);
//                        viewHolder.cl_base.performClick();
//                        return true;
//                }
//                return false;
//            });

            viewHolder.ivDel.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(messageData);
                }
            });
        } else {
            ((ViewHolderEmpty) holder).tvTitle.setText(R.string.nodata_favorites);
        }
    }

    @Override
    public int getItemCount() {
        if (favoritesPojoList == null || favoritesPojoList.size() <= 0) {
            return 1;
        } else {
            return favoritesPojoList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (favoritesPojoList == null || favoritesPojoList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private ImageView ivDel;
        private View cl_base;
        private ImageView image;


        MyRecyclerViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_name);
            ivDel = itemView.findViewById(R.id.iv_close);
            cl_base = itemView.findViewById(R.id.cl_base);
            image = itemView.findViewById(R.id.image);
            // cbCheck = itemView.findViewById(R.id.checkBox);
            //  clView = itemView.findViewById(R.id.cl_view);
        }
    }

    public static class ViewHolderEmpty extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        ViewHolderEmpty(View itemView) {
            super(itemView);
              tvTitle = itemView.findViewById(R.id.tv_nodata);
        }
    }


    /**
     * Item Interface Callback
     */
    public interface OnItemClickListener {
        void onItemClick(FavoritesEntity bean);
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    //使用
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemLongClickListener {

        void onItemLongClick(FavoritesEntity bean);
    }
    private OnItemLongClickListener onItemLongClickListener;

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void deleteMessage(int favoriteType) {
        int n = 0;
        for (FavoritesEntity messageEntity : favoritesPojoList) {

            if (messageEntity.getFavoriteType() == favoriteType) {
                favoritesPojoList.remove(n);
                notifyItemRemoved(n);
                break;
            }
            n++;
        }
    }
}
