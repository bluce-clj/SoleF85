package com.dyaco.spiritbike.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spiritbike.profile.ProfileHistoryAdapter;
import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.R;

import java.util.List;

public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.HomeScreenViewHolder> {
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_END = 2;
    Context m_Context;
    List<FavoritesEntity> favoritesEntityList;
    private final int TYPE_FAVORITE = 0;
    private final int TYPE_QUICK_START = 1;
    private final LayoutInflater mLayoutInflater;

    public HomeScreenAdapter(Context context) {
        this.m_Context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData2View(List<FavoritesEntity> list) {
        favoritesEntityList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (favoritesEntityList != null ) {
            return favoritesEntityList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;
        if (favoritesEntityList == null || favoritesEntityList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        } else if (favoritesEntityList.get(position).getFavoriteType() < 0) {
            viewType = VIEW_TYPE_END;
        }
        return viewType;
    }

    public static class HomeScreenViewHolder extends RecyclerView.ViewHolder {
        Button m_btFavorite_HomeScreenItem;
        ImageView m_ivDiagram_HomeScreenItem;
        ImageView m_ivTag_HomeScreenItem;

        public HomeScreenViewHolder(View itemView) {
            super(itemView);

            m_btFavorite_HomeScreenItem = itemView.findViewById(R.id.btFavorite_HomeScreenItem); //名
            m_ivDiagram_HomeScreenItem = itemView.findViewById(R.id.ivDiagram_HomeScreenItem); //圖
            m_ivTag_HomeScreenItem = itemView.findViewById(R.id.ivTag_HomeScreenItem); //按鈕
        }
    }

    @NonNull
    @Override
    public HomeScreenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        HomeScreenViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            view = mLayoutInflater.inflate(R.layout.home_screen_favorite_item, parent, false);
        } else {
            view = mLayoutInflater.inflate(R.layout.home_screen_seemore_item, parent, false);
        }
        viewHolder = new HomeScreenViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeScreenViewHolder holder, final int position) {
        final FavoritesEntity homeScreenInfo = favoritesEntityList.get(position);

        holder.m_ivDiagram_HomeScreenItem.setImageResource(ProgramsEnum.getProgram(homeScreenInfo.getFavoriteType()).getDiagramResLevel()); //圖
        holder.m_ivTag_HomeScreenItem.setVisibility(homeScreenInfo.getFavoriteType() >= 0 ? View.VISIBLE : View.INVISIBLE); //標籤
        holder.m_btFavorite_HomeScreenItem.setText(homeScreenInfo.getFavoriteName()); //名
        holder.m_btFavorite_HomeScreenItem.setOnClickListener(v -> onItemClickListener.onItemClick(homeScreenInfo));
    }

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
}