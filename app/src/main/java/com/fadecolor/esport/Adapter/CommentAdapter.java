package com.fadecolor.esport.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fadecolor.esport.R;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.domain.Comment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userHead;
        TextView userName, time, content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userHead = itemView.findViewById(R.id.user_head);
            userName = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.tv_now);
            content = itemView.findViewById(R.id.tv_text);
        }
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        if (comment.getHeadPath()!=null && !"null".equals(comment.getHeadPath())) {
            Glide.with(holder.itemView).load(Constant.HEAD_PATH + comment.getHeadPath()).into(holder.userHead);
        } else {
            holder.userHead.setImageResource(R.drawable.ic_user_default);
        }
        holder.userName.setText(comment.getUserName().equals("null")? comment.getUserTel():comment.getUserName());
        Date date = comment.getTime();
        if (year == (date.getYear() + 1900) && month == (date.getMonth() + 1) && day == date.getDate()) {
            holder.time.setText(format2.format(date));
        } else {
            holder.time.setText(format1.format(date));
        }
        holder.content.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
