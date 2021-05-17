package com.dyaco.spiritbike.settings;


//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.dyaco.spiritbike.R;
//import com.hacknife.wifimanager.IWifi;
//
//import java.util.List;
//
//public class WifiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private LayoutInflater mLayoutInflater;
//    private List<IWifi> messageDataList;
//    private static final int VIEW_TYPE_EMPTY = 0;
//    private static final int VIEW_TYPE_MESSAGE = 1;
//
//    private Context mContext;
//
//    public WifiAdapter(Context context) {
//        this.mContext = context;
//        this.mLayoutInflater = LayoutInflater.from(context);
//    }
//
//    public void setData2View(List<IWifi> list) {
//        messageDataList = list;
//
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        RecyclerView.ViewHolder viewHolder;
//        if (viewType == VIEW_TYPE_MESSAGE) {
//            view = mLayoutInflater.inflate(R.layout.items_wifi_list, parent, false);
//            viewHolder = new MyRecyclerViewHolder(view);
//        } else {
//            view = mLayoutInflater.inflate(R.layout.items_no_data, parent, false);
//            viewHolder = new ViewHolderEmpty(view);
//        }
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
//
//        int viewType = getItemViewType(i);
//        if(viewType == VIEW_TYPE_MESSAGE) {
//            final MyRecyclerViewHolder viewHolder = (MyRecyclerViewHolder) holder;
//            final int position = viewHolder.getAdapterPosition();
//            final IWifi messageData = messageDataList.get(position);
//
//            String title = messageData.name();
//            viewHolder.tvTitle.setText(title);
//
//            if (messageData.isEncrypt()) {
//                viewHolder.tvConnect.setImageResource(R.drawable.icon_password);
//            } else {
//                viewHolder.tvConnect.setImageResource(0);
//            }
//
//            if (messageData.isConnected()) {
//                viewHolder.iv_done.setImageResource(R.drawable.icon_wifi_done);
//                viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorE4002B));
//            } else {
//                viewHolder.iv_done.setImageResource(0);
//                viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.color597084));
//            }
//
//            viewHolder.cl_base.setOnClickListener(view -> onItemClickListener.onItemClick(messageData));
//
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        if (messageDataList == null || messageDataList.size() <= 0) {
//            return 1;
//        } else {
//            return messageDataList.size();
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        int viewType = VIEW_TYPE_MESSAGE;
//        if (messageDataList == null || messageDataList.size() <= 0)
//            viewType = VIEW_TYPE_EMPTY;
//
//        return viewType;
//    }
//
//    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
//        private final TextView tvTitle;
//        private final ImageView tvConnect;
//        private final ImageView iv_done;
//        private View cl_base;
//
//        MyRecyclerViewHolder(View itemView) {
//            super(itemView);
//            tvTitle = itemView.findViewById(R.id.tv_wifi_name);
//            tvConnect = itemView.findViewById(R.id.tv_connect);
//            iv_done = itemView.findViewById(R.id.iv_done);
//            cl_base = itemView.findViewById(R.id.cl_base);
//
//        }
//    }
//
//    public static class ViewHolderEmpty extends RecyclerView.ViewHolder {
//        private TextView tvTitle;
//
//        ViewHolderEmpty(View itemView) {
//            super(itemView);
//            tvTitle = itemView.findViewById(R.id.tv_nodata);
//            tvTitle.setText("");
//        }
//    }
//
//
//    /**
//     * Item Interface Callback
//     */
//    public interface OnItemClickListener {
//        void onItemClick(IWifi bean);
//    }
//
//    private OnItemClickListener onItemClickListener;
//
//    public OnItemClickListener getOnItemClickListener() {
//        return onItemClickListener;
//    }
//
//    //使用
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    public interface OnItemLongClickListener {
//
//        void onItemLongClick(WifiBean bean);
//    }
//
//    private OnItemLongClickListener onItemLongClickListener;
//
//    public OnItemLongClickListener getOnItemLongClickListener() {
//        return onItemLongClickListener;
//    }
//
//    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
//        this.onItemLongClickListener = onItemLongClickListener;
//    }
//}




