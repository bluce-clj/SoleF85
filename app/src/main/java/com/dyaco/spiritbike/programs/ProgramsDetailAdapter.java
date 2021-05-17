package com.dyaco.spiritbike.programs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spiritbike.product_flavors.ModeEnum;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.ProgramInfo;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.support.banner.adapter.BannerAdapter;
import com.dyaco.spiritbike.R;
import com.hpplay.sdk.sink.pass.PassBean;

import java.util.List;

import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XE395ENT;

public class ProgramsDetailAdapter extends BannerAdapter<ProgramInfo, RecyclerView.ViewHolder> {
    Context m_Context;
    List<ProgramInfo> m_ProgramInfoList;


//    public void setData2View(List<ProgramInfo> m_ProgramInfoList) {
//        this.m_ProgramInfoList = m_ProgramInfoList;
//        notifyDataSetChanged();
//    }

    public ProgramsDetailAdapter(Context context, List<ProgramInfo> mDatas) {
        super(mDatas);
        // this.m_ProgramInfoList = mDatas;
        this.m_Context = context;
    }

//    @Override
//    public int getItemCount() {
//
//        return m_ProgramInfoList.size();
//    }

    public static class ProgramsViewHolder extends RecyclerView.ViewHolder {

        TextView m_tvItem_ProgramsDetailsItem;
        TextView m_tvDesc_ProgramsDetailsItem;
        ImageView m_ivDiagramItem1_ProgramsDetailsItem;
        ImageView m_ivDiagramItem2_ProgramsDetailsItem;
        TextView m_tvDiagramItem2_ProgramsDetailsItem;
        ImageView m_ivPhoto_ProgramsDetailsItem;
        Button m_btChoose_ProgramsDetailsItem;
        CheckBox m_cbTag_ProgramsDetailsItem;
        ConstraintLayout m_clDiagram_ProgramsDetailsItem;
        ConstraintLayout m_clPhoto_ProgramsDetailsItem;
        ConstraintLayout cl_custom;
        ConstraintLayout cl_custom_text;
        ImageView ibCustomLevel;
        ImageView ibCustomIncline;
        TextView tv_custom_level;
        TextView tv_custom_incline;
        ImageView iv_hr_attention;
        TextView tv_hr_attention;

        ImageView ivLevelEdit;
        ImageView ivInclineEdit;

        TextView custom_ww_text;

        public ProgramsViewHolder(View itemView) {
            super(itemView);
            m_tvItem_ProgramsDetailsItem = itemView.findViewById(R.id.tvItem_ProgramsDetailsItem);
            m_tvDesc_ProgramsDetailsItem = itemView.findViewById(R.id.tvDesc_ProgramsDetailsItem);
            m_ivDiagramItem1_ProgramsDetailsItem = itemView.findViewById(R.id.ivDiagramItem1_ProgramsDetailsItem);
            m_ivDiagramItem2_ProgramsDetailsItem = itemView.findViewById(R.id.ivDiagramItem2_ProgramsDetailsItem);
            m_tvDiagramItem2_ProgramsDetailsItem = itemView.findViewById(R.id.tvDiagramItem2_ProgramsDetailsItem);

            m_ivPhoto_ProgramsDetailsItem = itemView.findViewById(R.id.ivPhoto_ProgramsDetailsItem);
            m_btChoose_ProgramsDetailsItem = itemView.findViewById(R.id.btChoose_ProgramsDetailsItem);
            m_clDiagram_ProgramsDetailsItem = itemView.findViewById(R.id.clDiagram_ProgramsDetailsItem);
            m_clPhoto_ProgramsDetailsItem = itemView.findViewById(R.id.clPhoto_ProgramsDetailsItem);
            m_cbTag_ProgramsDetailsItem = itemView.findViewById(R.id.cbTag_ProgramsDetailsItem);
            cl_custom = itemView.findViewById(R.id.cl_custom);
            ibCustomLevel = itemView.findViewById(R.id.iv_custom_level);
            ibCustomIncline = itemView.findViewById(R.id.iv_custom_incline);
            cl_custom_text = itemView.findViewById(R.id.cl_custom_text);
            tv_custom_level = itemView.findViewById(R.id.tv_custom_level);
            tv_custom_incline = itemView.findViewById(R.id.tv_custom_incline);
            iv_hr_attention = itemView.findViewById(R.id.iv_hr_attention);
            tv_hr_attention = itemView.findViewById(R.id.tv_hr_attention);

            ivLevelEdit = itemView.findViewById(R.id.iv_level_edit);
            ivInclineEdit = itemView.findViewById(R.id.iv_incline_edit);

            custom_ww_text = itemView.findViewById(R.id.custom_ww_text);
        }
    }

    @NonNull
    @Override
    public ProgramsViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.programs_details_item, parent, false);
        return new ProgramsViewHolder(view);
    }

    @Override
    public void onBindView(@NonNull RecyclerView.ViewHolder viewHolder, ProgramInfo programInfo, int position, int size) {
        final ProgramsViewHolder holder = (ProgramsViewHolder) viewHolder;

        boolean photoFill = programInfo.is_PhotoFill();

        holder.m_tvItem_ProgramsDetailsItem.setText(programInfo.get_Item());
        holder.m_tvDesc_ProgramsDetailsItem.setText(programInfo.get_Desc());
        holder.m_clDiagram_ProgramsDetailsItem.setVisibility(photoFill ? View.INVISIBLE : View.VISIBLE);
        holder.m_clPhoto_ProgramsDetailsItem.setVisibility(photoFill ? View.VISIBLE : View.INVISIBLE);

        //有照片
        if (photoFill) {
            holder.m_ivPhoto_ProgramsDetailsItem.setImageResource(programInfo.get_PhotoResId());
        } else {
            holder.m_ivDiagramItem1_ProgramsDetailsItem.setImageResource(programInfo.get_DiagramRes1());
            holder.m_ivDiagramItem2_ProgramsDetailsItem.setImageResource(programInfo.get_DiagramRes2());
        }

        if (programInfo.getProgramsEnum() == ProgramsEnum.HEART_RATE) {
            holder.iv_hr_attention.setVisibility(View.VISIBLE);
            holder.tv_hr_attention.setVisibility(View.VISIBLE);
        } else {
            holder.iv_hr_attention.setVisibility(View.INVISIBLE);
            holder.tv_hr_attention.setVisibility(View.INVISIBLE);
        }


        if (MODE != XE395ENT) {
            holder.m_ivDiagramItem2_ProgramsDetailsItem.setVisibility(View.INVISIBLE);
            holder.m_tvDiagramItem2_ProgramsDetailsItem.setVisibility(View.INVISIBLE);
        }

        //CUSTOM
        if (programInfo.getProgramsEnum() == ProgramsEnum.CUSTOM) {
            holder.m_clDiagram_ProgramsDetailsItem.setVisibility(View.INVISIBLE);
            holder.m_clPhoto_ProgramsDetailsItem.setVisibility(View.INVISIBLE);

            //CUSTOM的圖
            holder.cl_custom.setVisibility(View.VISIBLE);

            if (MODE == XE395ENT) {
                if (programInfo.getImgLevel() == null || programInfo.getImgIncline() == null) {
                    //Custom 有圖還沒設定
                    //顯示Custom未設定的訊息
                    holder.cl_custom_text.setVisibility(View.VISIBLE);
                    holder.m_btChoose_ProgramsDetailsItem.setVisibility(View.INVISIBLE);
                    holder.custom_ww_text.setText("level and incline Profiles are not set yet");
                } else {
                    //Custom圖全都已設定
                    //顯示CHOOSE
                    holder.cl_custom_text.setVisibility(View.INVISIBLE);
                    holder.m_btChoose_ProgramsDetailsItem.setVisibility(View.VISIBLE);
                }
            } else {
                if (programInfo.getImgLevel() == null) {
                    //Custom 有圖還沒設定
                    //顯示Custom未設定的訊息
                    holder.cl_custom_text.setVisibility(View.VISIBLE);
                    holder.m_btChoose_ProgramsDetailsItem.setVisibility(View.INVISIBLE);
                    holder.custom_ww_text.setText("level Profiles are not set yet");
                } else {
                    //Custom圖全都已設定
                    //顯示CHOOSE
                    holder.cl_custom_text.setVisibility(View.INVISIBLE);
                    holder.m_btChoose_ProgramsDetailsItem.setVisibility(View.VISIBLE);
                }
            }


            //LEVEL Custom
            if (programInfo.getImgLevel() != null) {

                GlideApp.with(m_Context)
                        .load(programInfo.getImgLevel())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        //  .placeholder(R.drawable.insert_custom_diagram)
                        .into(holder.ibCustomLevel);
                holder.ivLevelEdit.setVisibility(View.VISIBLE);
                holder.ibCustomLevel.setBackgroundResource(0);
            } else {
                holder.tv_custom_level.setCompoundDrawables(null, null, null, null);
                holder.ibCustomLevel.setBackgroundResource(R.drawable.diagram_custom_empty);
                holder.ivLevelEdit.setVisibility(View.INVISIBLE);
            }

            if (MODE == XE395ENT) {
                //Custom INCLINE 圖
                if (programInfo.getImgIncline() != null) {
                    GlideApp.with(m_Context)
                            .load(programInfo.getImgIncline())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            //   .placeholder(R.drawable.insert_custom_diagram)
                            .skipMemoryCache(true)
                            .into(holder.ibCustomIncline);

                    holder.ivInclineEdit.setVisibility(View.VISIBLE);

                    holder.ibCustomIncline.setBackgroundResource(0);
                } else {
                    holder.tv_custom_incline.setCompoundDrawables(null, null, null, null);
                    holder.ibCustomIncline.setBackgroundResource(R.drawable.diagram_custom_empty);
                    holder.ivInclineEdit.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.tv_custom_incline.setVisibility(View.INVISIBLE);
                holder.ibCustomIncline.setVisibility(View.INVISIBLE);
                holder.ivInclineEdit.setVisibility(View.INVISIBLE);
            }

            holder.ibCustomLevel.setOnClickListener(v -> onImageClickListener.onImageClick("LEVEL"));
            holder.ibCustomIncline.setOnClickListener(v -> onImageClickListener.onImageClick("INCLINE"));

        } else {
            holder.cl_custom.setVisibility(View.INVISIBLE);
            holder.m_clDiagram_ProgramsDetailsItem.setVisibility(photoFill ? View.INVISIBLE : View.VISIBLE);
            holder.m_clPhoto_ProgramsDetailsItem.setVisibility(photoFill ? View.VISIBLE : View.INVISIBLE);

            //還沒設定custom圖時的訊息
            holder.cl_custom_text.setVisibility(View.INVISIBLE);
            holder.m_btChoose_ProgramsDetailsItem.setVisibility(View.VISIBLE);
        }


        holder.m_cbTag_ProgramsDetailsItem.setChecked(programInfo.isCheck());
        holder.m_cbTag_ProgramsDetailsItem.setOnClickListener(v -> {
            boolean isChecked = holder.m_cbTag_ProgramsDetailsItem.isChecked();
            programInfo.setCheck(isChecked);
            onCheckItemClickListener.onCheckItemClick(programInfo.getProgramsEnum(), isChecked);
        });

        holder.m_btChoose_ProgramsDetailsItem.setOnClickListener(view -> onItemClickListener.onItemClick(programInfo.getProgramsEnum()));
    }

    public interface OnItemClickListener {
        void onItemClick(ProgramsEnum programsEnum);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnCheckItemClickListener {
        void onCheckItemClick(ProgramsEnum programsEnum, boolean isCheck);
    }

    private OnCheckItemClickListener onCheckItemClickListener;

    public void setOnCheckItemClickListener(OnCheckItemClickListener onCheckItemClickListener) {
        this.onCheckItemClickListener = onCheckItemClickListener;
    }


    public interface OnImageClickListener {
        void onImageClick(String type);
    }

    private OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }
}
