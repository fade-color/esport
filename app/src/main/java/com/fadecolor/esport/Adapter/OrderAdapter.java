package com.fadecolor.esport.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fadecolor.esport.OrderGymActivity;
import com.fadecolor.esport.R;
import com.fadecolor.esport.domain.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> orderList;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日", Locale.CHINA);

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView time,date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tv_time);
            date = itemView.findViewById(R.id.tv_date);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.time.setText(OrderGymActivity.timeMap.get(order.getPeriod()));
        holder.date.setText(dateFormat.format(order.getDate()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
