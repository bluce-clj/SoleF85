package com.dyaco.spiritbike.workout;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.UnitEnum;
import com.dyaco.spiritbike.support.banner.adapter.BannerAdapter;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.programs.ProgramSummary4Pojo;

import java.util.List;
import java.util.Locale;

import static com.dyaco.spiritbike.support.CommonUtils.convertUnit;
import static com.dyaco.spiritbike.support.CommonUtils.formatSec2H;

public class WorkoutResultSummaryAdapter extends BannerAdapter<ProgramSummary4Pojo, RecyclerView.ViewHolder> {
    Context m_Context;
    List<ProgramSummary4Pojo> m_ProgramInfoList;
    WorkoutBean workoutBean;


    public WorkoutResultSummaryAdapter(Context context, List<ProgramSummary4Pojo> mDatas, WorkoutBean workoutBean) {
        super(mDatas);
        this.m_Context = context;
        this.m_ProgramInfoList = mDatas;
        this.workoutBean = workoutBean;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_programs_summary, parent, false);
                return new Summary_1_ViewHolder(view);

            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_programs_summary2, parent, false);
                return new Summary_Level_ViewHolder(view);

            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_programs_summary3, parent, false);
                return new Summary_Incline_ViewHolder(view);

            case 4:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_programs_summary4, parent, false);
                return new Summary_HeartRate_ViewHolder(view);

        }
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_programs_summary, parent, false);
        return new Summary_1_ViewHolder(view);
    }

    @Override
    public void onBindView(@NonNull RecyclerView.ViewHolder viewHolder, ProgramSummary4Pojo programInfo, int position, int size) {
        int viewType = viewHolder.getItemViewType();

        switch (viewType) {
            case 1:
                Summary_1_ViewHolder holder1 = (Summary_1_ViewHolder) viewHolder;

                holder1.tvTotalDistance.setText(String.format(Locale.getDefault(),"%.2f",convertUnit(UnitEnum.DISTANCE, workoutBean.getUnit(), Double.parseDouble(workoutBean.getTotalDistance()))));
                holder1.tvTime.setText(formatSec2H(workoutBean.getRunTime()));
                Log.d("@@@@@@@", "onBindView: " + workoutBean.getAvgSpeed());
                holder1.tvAvgSpeed.setText(CommonUtils.formatDecimal(Double.parseDouble(workoutBean.getAvgSpeed()), 1));
                holder1.tvCalories.setText(CommonUtils.formatDecimal(Double.parseDouble(workoutBean.getCalories()), 1));
                holder1.tvAvgMET.setText(CommonUtils.formatDecimal(Double.parseDouble(workoutBean.getAvgMET()), 1));

                holder1.tvAvgPower.setText(CommonUtils.formatDecimal(Double.parseDouble(workoutBean.getAvgSpeed()), 1));
                holder1.tvMaxWATT.setText(workoutBean.getMaxWATT());

                holder1.tv_total_distance_unit.setText(UnitEnum.getUnit(UnitEnum.DISTANCE));
              //  holder1.tv_total_speed_unit.setText(UnitEnum.getUnit(UnitEnum.SPEED));

                break;
            case 2:
                Summary_Level_ViewHolder holder2 = (Summary_Level_ViewHolder) viewHolder;

                holder2.tv_v2_avg_level.setText(workoutBean.getAvgLevel());
                holder2.tv_v2_max_level.setText(String.valueOf(workoutBean.getMaxLevel()));

             //   holder2.tv_total_speed_unit1.setText(UnitEnum.getUnit(UnitEnum.SPEED));
            //    holder2.tv_total_speed_unit2.setText(UnitEnum.getUnit(UnitEnum.SPEED));

              //  Bitmap bitmap = new CommonUtils().getImageBitmap(m_Context, workoutBean.getLevelDiagramNum(), 60, 1200);
                Bitmap bitmap = new CommonUtils().getDiagramBitmapXX(m_Context, workoutBean.getLevelDiagramNum());
                GlideApp.with(m_Context)
                        .load(bitmap)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder2.ivDiagramLevel);
                break;
            case 3:
                Summary_Incline_ViewHolder holder3 = (Summary_Incline_ViewHolder) viewHolder;

               // holder3.tv_v3_avg_incline.setText(workoutBean.getAvgIncline());
                holder3.tv_v3_avg_incline.setText(String.valueOf(CommonUtils.incI2F(Integer.parseInt(workoutBean.getAvgIncline()))));
              //  holder3.tv_v3_max_incline.setText(String.valueOf(workoutBean.getMaxIncline()));
                holder3.tv_v3_max_incline.setText(String.valueOf(CommonUtils.incI2F(workoutBean.getMaxIncline())));

              //  Bitmap bitmap2 = new CommonUtils().getImageBitmap(m_Context, workoutBean.getInclineDiagramNum(), 60, 1200);
                Bitmap bitmap2 = new CommonUtils().getDiagramBitmapXX(m_Context, workoutBean.getInclineDiagramNum());

                GlideApp.with(m_Context)
                        .load(bitmap2)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder3.ivDiagramIncline);
                break;
            case 4:

                Summary_HeartRate_ViewHolder holder4 = (Summary_HeartRate_ViewHolder) viewHolder;
                holder4.tv_v4_avg_hr.setText(workoutBean.getAvgHR());
                holder4.tv_v4_max_hr.setText(workoutBean.getMaxHR());
              //  Bitmap bitmap4 = new CommonUtils().getHRDiagramBitmap(m_Context, workoutBean.getHrDiagramNum());
                Bitmap bitmap4 = new CommonUtils().getDiagramBitmapXX(m_Context, workoutBean.getHrDiagramNum());
                GlideApp.with(m_Context)
                        .load(bitmap4)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder4.iv_diagram_hr);
                break;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int itemName);
    }

    private OnItemClickListener onItemClickListener;

    //使用
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public static class Summary_1_ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTotalDistance;
        TextView tvTime;
        TextView tvAvgSpeed;
        TextView tvCalories;
        TextView tvMaxWATT;
        TextView tvAvgMET;
        TextView tvAvgPower;

        TextView tv_total_distance_unit;
        TextView tv_total_speed_unit;

        public Summary_1_ViewHolder(View itemView) {
            super(itemView);
            tvTotalDistance = itemView.findViewById(R.id.tv_v1_km);
            tvTime = itemView.findViewById(R.id.tv_v1_time);
            tvAvgSpeed = itemView.findViewById(R.id.tv_v1_avgspeed);
            tvCalories = itemView.findViewById(R.id.tv_v1_calories);
            tvMaxWATT = itemView.findViewById(R.id.tv_v1_max_power);
            tvAvgMET = itemView.findViewById(R.id.tv_v1_avgmets);
            tvAvgPower = itemView.findViewById(R.id.tv_v1_avg_power);

            tv_total_distance_unit = itemView.findViewById(R.id.tv_total_distance_unit);
            tv_total_speed_unit = itemView.findViewById(R.id.tv_total_speed_unit);
        }
    }

    //level
    public static class Summary_Level_ViewHolder extends RecyclerView.ViewHolder {

        TextView tvKm;
        TextView m_tvDesc_ProgramsDetailsItem;
        ImageView ivDiagramLevel;
        TextView tv_total_speed_unit1;
        TextView tv_total_speed_unit2;
        TextView tv_v2_avg_level;
        TextView tv_v2_max_level;

        public Summary_Level_ViewHolder(View itemView) {
            super(itemView);
          //  tvKm = itemView.findViewById(R.id.tv_v1_km);
            ivDiagramLevel = itemView.findViewById(R.id.iv_diagram_level);
            tv_v2_avg_level = itemView.findViewById(R.id.tv_v2_avg_level);
            tv_v2_max_level = itemView.findViewById(R.id.tv_v2_max_level);
        //    tv_total_speed_unit1 = itemView.findViewById(R.id.tv_total_speed_unit1);
        //    tv_total_speed_unit2 = itemView.findViewById(R.id.tv_total_speed_unit2);

        }
    }

    //incline
    public static class Summary_Incline_ViewHolder extends RecyclerView.ViewHolder {

        TextView tvKm;
        TextView m_tvDesc_ProgramsDetailsItem;
        ImageView ivDiagramIncline;
        TextView tv_v3_avg_incline;
        TextView tv_v3_max_incline;
        public Summary_Incline_ViewHolder(View itemView) {
            super(itemView);
        //    tvKm = itemView.findViewById(R.id.tv_v1_km);
            ivDiagramIncline = itemView.findViewById(R.id.iv_diagram_incline);
            tv_v3_avg_incline = itemView.findViewById(R.id.tv_v3_avg_incline);
            tv_v3_max_incline = itemView.findViewById(R.id.tv_v3_max_incline);
        }
    }

    public static class Summary_HeartRate_ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_diagram_hr;

        TextView tv_v4_avg_hr;
        TextView tv_v4_max_hr;


        public Summary_HeartRate_ViewHolder(View itemView) {
            super(itemView);
            iv_diagram_hr = itemView.findViewById(R.id.iv_diagram_hr);
            tv_v4_avg_hr = itemView.findViewById(R.id.tv_v4_avg_hr);
            tv_v4_max_hr = itemView.findViewById(R.id.tv_v4_max_hr);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return getData(getRealPosition(position)).viewType;
    }

}
