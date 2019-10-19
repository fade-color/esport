package com.fadecolor.esport.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.fadecolor.esport.Fragment.HomeFragment;
import com.fadecolor.esport.R;
import com.fadecolor.esport.Util.Constant;
import com.fadecolor.esport.domain.Gym;

import java.util.List;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.ViewHolder> implements View.OnClickListener {

    private List<Gym> gyms;

    public GymAdapter(List<Gym> gyms) {
        this.gyms = gyms;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, position, tel;
        ImageView imageView;
        CardView itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Gym_name);
            position = itemView.findViewById(R.id.Gym_position);
            tel = itemView.findViewById(R.id.Gym_tel);
            imageView = itemView.findViewById(R.id.Gym_image);
            itemLayout = itemView.findViewById(R.id.item_info);

        }
    }

    @NonNull
    @Override
    public GymAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gym_item, parent, false);
        final GymAdapter.ViewHolder holder = new GymAdapter.ViewHolder(view);
        holder.itemLayout.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onClick(View v) {
switch (v.getId()){
//    Intent intent = new Intent(this,);
    case R.id.item_info:
        Toast.makeText(v.getContext(),"123",Toast.LENGTH_SHORT).show();
}
    }
    @Override
    public void onBindViewHolder(@NonNull GymAdapter.ViewHolder holder, int position) {
    Gym gym = gyms.get(position);
if (gym.getImageSrc()!=null && !gym.getImageSrc().equals("null")){
    Glide.with(holder.itemView)
            .load(Constant.GYM_IMG_PATH+gym.getImageSrc())
            .into(holder.imageView);
}else {
    holder.imageView.setImageResource(R.drawable.ic_user_default);
}
holder.position.setText("场馆位置："+gym.getPosition());
holder.name.setText("场馆名："+gym.getName());
holder.tel.setText("场馆电话："+gym.getTel());
    }

    @Override
    public int getItemCount() {
        return gyms.size();
    }
}
