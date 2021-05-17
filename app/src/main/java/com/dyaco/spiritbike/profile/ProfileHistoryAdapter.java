package com.dyaco.spiritbike.profile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.UnitEnum;
import com.dyaco.spiritbike.support.room.entity.HistoryEntity;
import com.dyaco.spiritbike.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.dyaco.spiritbike.support.CommonUtils.convertUnit;
import static com.dyaco.spiritbike.support.CommonUtils.formatSec2H;
import static com.dyaco.spiritbike.support.CommonUtils.formatSecToM;


public class ProfileHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private List<HistoryEntity> historyPojoList;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    private Context mContext;

    ProfileHistoryAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    void setData2View(List<HistoryEntity> list) {
        historyPojoList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MESSAGE) {
            view = mLayoutInflater.inflate(R.layout.items_history_list, parent, false);
            viewHolder = new MyRecyclerViewHolder(view);
        } else if(viewType == VIEW_TYPE_FOOTER){
            view = mLayoutInflater.inflate(R.layout.items_history_last, parent, false);
            viewHolder = new ViewHolderEmpty(view);
        } else {
            view = mLayoutInflater.inflate(R.layout.items_no_data, parent, false);
            viewHolder = new ViewHolderEmpty(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        int viewType = getItemViewType(i);
        if(viewType == VIEW_TYPE_MESSAGE) {
            final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
            final int position = viewHolder.getAdapterPosition();
            final HistoryEntity messageData = historyPojoList.get(position);

            String title = messageData.getHistoryName();
            viewHolder.tvTitle.setText(title);

            String date = new SimpleDateFormat("hh:mm a, MMM dd, yyyy", Locale.ENGLISH).format(messageData.getUpdateTime());
            viewHolder.datetime.setText(date);

            viewHolder.tv_distance.setText(UnitEnum.getUnit(UnitEnum.DISTANCE));

            viewHolder.kmtext.setText(String.format(Locale.getDefault(),"%.2f",convertUnit(UnitEnum.DISTANCE, messageData.getUnit(), Double.parseDouble(messageData.getTotalDistance()))));

           // viewHolder.kmtext.setText(messageData.getTotalDistance());


            viewHolder.mintext.setText(formatSecToM(messageData.getRunTime()));
          //  viewHolder.mintext.setText(formatSec2H(messageData.getRunTime()));


          //  viewHolder.avgtext.setText(messageData.getAvgSpeed());
          //  viewHolder.avgtext.setText(messageData.getCalories());
            viewHolder.avgtext.setText(CommonUtils.formatDecimal(Double.parseDouble(messageData.getCalories()), 1));

            //顯示內容
            viewHolder.btSummary.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(messageData);
                }
            });

        } else if(viewType == VIEW_TYPE_EMPTY) {
            ((ViewHolderEmpty)holder).tvTitle.setText(R.string.nodata_history);
        }
    }

//    /**
//     * 一定出現一筆資料
//     * @return
//     */
//    @Override
//    public int getItemCount() {
//        if (historyPojoList.size() <= 0) {
//            return 1;
//        } else {
//            return historyPojoList.size() + 1;
//        }
//    }
//    @Override
//    public int getItemViewType(int position) {
//        int viewType = VIEW_TYPE_MESSAGE;
//
//        if (position == historyPojoList.size()) {
//            viewType = VIEW_TYPE_EMPTY;
//        }
//
//        if (historyPojoList.size() <= 0) {
//            viewType = VIEW_TYPE_EMPTY;
//        }
//        return viewType;
//    }

    @Override
    public int getItemCount() {
        if (historyPojoList == null || historyPojoList.size() <= 0) {
            return 1;
        } else {
            return historyPojoList.size() + 1;
        }
    }
    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_MESSAGE;

        if (historyPojoList == null || historyPojoList.size() <= 0) {
            viewType = VIEW_TYPE_EMPTY;
        } else if (position == historyPojoList.size()) {
            viewType = VIEW_TYPE_FOOTER;
        }
        return viewType;
    }

    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private Button btSummary;
        private TextView datetime;
        private TextView kmtext;
        private TextView mintext;
        private TextView avgtext;
        private TextView tv_distance;

        MyRecyclerViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_name);
            btSummary = itemView.findViewById(R.id.tv_summary);
            datetime = itemView.findViewById(R.id.datetime);
            kmtext = itemView.findViewById(R.id.kmtext);
            mintext = itemView.findViewById(R.id.mintext);
            avgtext = itemView.findViewById(R.id.avgtext);
            tv_distance = itemView.findViewById(R.id.tv_distance);
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
        void onItemClick(HistoryEntity bean);
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

        void onItemLongClick(HistoryEntity bean);
    }
}
