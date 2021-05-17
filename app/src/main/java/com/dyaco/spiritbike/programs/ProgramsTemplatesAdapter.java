package com.dyaco.spiritbike.programs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.profile.HistoryPojo;

import java.util.List;

public class ProgramsTemplatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private List<TemplateEntity> templateEntityList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    GridLayoutManager gridLayoutManager;

    private Context mContext;

    ProgramsTemplatesAdapter(Context context, GridLayoutManager gridLayoutManager) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.gridLayoutManager = gridLayoutManager;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (templateEntityList == null || templateEntityList.size() == 0) {
                    return 4; // the item in position now takes up 4 spans
                }
                return 1;

            }
        });
    }

    void setData2View(List<TemplateEntity> list) {
        templateEntityList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            view = mLayoutInflater.inflate(R.layout.items_templates_list, parent, false);
            viewHolder = new MyRecyclerViewHolder(view);
        } else {
            view = mLayoutInflater.inflate(R.layout.items_no_data, parent, false);
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
            final TemplateEntity templateEntity = templateEntityList.get(position);

            String title = templateEntity.getTemplateName();
            viewHolder.tvTitle.setText(title);

           // Bitmap bitmap = getImageBitmap(mContext, templateEntity.getDiagramLevel(), 20, 400);

           //userProfileEntity.getLevelDiagram() test
            GlideApp.with(mContext)
                  //  .load(bitmap)
                    .load(templateEntity.getLevelDiagram())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(viewHolder.image);

            viewHolder.cl_base.setOnClickListener(v -> onItemClickListener.onItemClick(templateEntity));

        } else {
            ((ViewHolderEmpty) holder).tvTitle.setText(R.string.nodata_templates);
        }
    }

    @Override
    public int getItemCount() {
        if (templateEntityList == null || templateEntityList.size() <= 0) {
            return 1;
        } else {
            return templateEntityList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (templateEntityList == null || templateEntityList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        }

        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
       // private ImageView ivDel;
        private View cl_base;
        private ImageView image;

        MyRecyclerViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_name);
         //   ivDel = itemView.findViewById(R.id.iv_close);
            cl_base = itemView.findViewById(R.id.cl_base);
            image = itemView.findViewById(R.id.image);

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
        void onItemClick(TemplateEntity bean);
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

        void onItemLongClick(HistoryPojo bean);
    }

    private OnItemLongClickListener onItemLongClickListener;

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void deleteMessage(String templateName) {
        int n = 0;
        for (TemplateEntity messageEntity : templateEntityList) {

            if (messageEntity.getTemplateName().equals(templateName)) {
                templateEntityList.remove(n);
                notifyItemRemoved(n);
                break;
            }
            n++;
        }
    }
}