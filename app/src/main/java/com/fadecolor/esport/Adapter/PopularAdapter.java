package com.fadecolor.esport.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fadecolor.esport.CommentActivity;
import com.fadecolor.esport.PhotoViewActivity;
import com.fadecolor.esport.R;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.domain.Activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private List<Activity> activities;

    public PopularAdapter(List<Activity> activities) {
        this.activities = activities;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemLayout;

        CircleImageView userHead;

        TextView userName;

        TextView time;

        TextView text;

        ImageView image;

        TextView location;

        TextView commentNum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.item_layout);
            userHead = itemView.findViewById(R.id.user_head);
            userName = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.tv_now);
            text = itemView.findViewById(R.id.tv_text);
            image = itemView.findViewById(R.id.iv_image);
            location = itemView.findViewById(R.id.tv_location);
            commentNum = itemView.findViewById(R.id.tv_comment_num);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Activity activity = activities.get(position);
                int activityId = activity.getActivityId();
                String userName = activity.getUserName();
                String headPath = activity.getHeadPath();
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtra("activityId", activityId);
                intent.putExtra("userName", userName);
                intent.putExtra("headPath", headPath);
                view.getContext().startActivity(intent);
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Activity activity = activities.get(position);
                String imageSrc = activity.getImageSrc();
                Intent intent = new Intent(view.getContext(), PhotoViewActivity.class);
                intent.putExtra("imageSrc", imageSrc);
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        Activity activity = activities.get(position);
        if (activity.getHeadPath()!=null && !"null".equals(activity.getHeadPath())) {
            Glide.with(holder.itemView).load(Constant.HEAD_PATH + activity.getHeadPath()).into(holder.userHead);
        } else {
            holder.userHead.setImageResource(R.drawable.ic_user_default);
        }
        holder.userName.setText(activity.getUserName().equals("null")? activity.getUserTel():activity.getUserName());
        Date date = activity.getTime();
        if (year == (date.getYear() + 1900) && month == (date.getMonth() + 1) && day == date.getDate()) {
            holder.time.setText(format2.format(date));
        } else {
            holder.time.setText(format1.format(date));
        }
        holder.text.setText(activity.getContent());
        if (activity.getImageSrc() != null && !activity.getImageSrc().equals("null")) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView).load(Constant.ACTIVITY_IMG_PATH + activity.getImageSrc()).into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }
        holder.location.setText(activity.getLocation());
        holder.commentNum.setText(activity.getCommentNum()>99? "99+":activity.getCommentNum()+"");
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }
}
