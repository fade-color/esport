package com.fadecolor.esport.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.fadecolor.esport.GymDetailActivity;
import com.fadecolor.esport.OrderGymActivity;
import com.fadecolor.esport.R;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.domain.Gym;

import java.util.List;

public class SubGymAdapter extends RecyclerView.Adapter<GymAdapter.ViewHolder> {

private List<Gym> gyms;
String[] SportName  = new String[]{"羽毛球", "篮球", "足球", "乒乓球", "排球"};
int[] ImagePath = new int[]{R.drawable.ic_yu,R.drawable.ic_lan,R.drawable.ic_zu,R.drawable.ic_ping,R.drawable.ic_pai,};
public SubGymAdapter (List<Gym> gyms) {
        this.gyms = gyms;
        }

static class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}

    @NonNull
    @Override
    public GymAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gym_item, parent, false);
        final GymAdapter.ViewHolder holder = new GymAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GymAdapter.ViewHolder holder, final int position) {
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
            Gym gym = gyms.get(position);
            Intent intent = new Intent(v.getContext(), OrderGymActivity.class);
            intent.putExtra("GymId",gym.getGymId());
            intent.putExtra("Id",gym.getId());
            v.getContext().startActivity(intent);
        }
    });
        Gym gym = gyms.get(position);
//        if (gym.getImageSrc() != null && !gym.getImageSrc().equals("null")) {
//            Glide.with(holder.itemView)
//                    .load(Constant.GYM_IMG_PATH + gym.getImageSrc())
//                    .into(holder.imageView);
//        } else {
//            holder.imageView.setImageResource(R.drawable.ic_user_default);
//        }
        holder.imageView.setImageResource(ImagePath[gym.getType()-1]);
        holder.name.setText(SportName[gym.getType()-1]+"场");
        holder.position.setText("场馆类型：" + (gym.getKind()==1?"室外":"室内"));
        holder.tel.setText("容纳人数：" + gym.getMaxCount());
        holder.name.setTextSize(16);
    }

    @Override
    public int getItemCount() {
        return gyms.size();
    }
}
