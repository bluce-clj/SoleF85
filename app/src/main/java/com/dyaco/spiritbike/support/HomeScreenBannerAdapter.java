package com.dyaco.spiritbike.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spiritbike.support.banner.adapter.BannerAdapter;
import com.dyaco.spiritbike.R;

import java.util.List;

public class HomeScreenBannerAdapter extends BannerAdapter<HomeScreenBannerPojo,RecyclerView.ViewHolder> {
    Context m_Context;

    public HomeScreenBannerAdapter(Context context, List<HomeScreenBannerPojo> mDatas) {
        super(mDatas);
        this.m_Context = context;
    }

    public static class ProgramsViewHolder extends RecyclerView.ViewHolder {

        TextView textView1;
        TextView textView2;
        TextView textView3;


        public ProgramsViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.text_1);
            textView2 = itemView.findViewById(R.id.text_2);
            textView3 = itemView.findViewById(R.id.text_3);
        }
    }

    @NonNull
    @Override
    public ProgramsViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        int layout;
        if (viewType == 1){
            layout = R.layout.items_home_screen_banner1;
        } else {
            layout = R.layout.items_home_screen_banner2;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new ProgramsViewHolder(view);
    }

    @Override
    public void onBindView(@NonNull RecyclerView.ViewHolder viewHolder, HomeScreenBannerPojo programInfo, int position, int size) {
        final ProgramsViewHolder holder = (ProgramsViewHolder) viewHolder;

        holder.textView1.setText(programInfo.getText1());
        holder.textView2.setText(programInfo.getText2());
        holder.textView3.setText(programInfo.getText3());

    }


    @Override
    public int getItemViewType(int position) {
        return getData(getRealPosition(position)).getBannerType();
    }

}
