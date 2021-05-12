package me.ag2s.tts.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.ag2s.tts.R;
import me.ag2s.tts.services.TtsStyle;

public class TtsStyleAdapter extends RecyclerView.Adapter<TtsStyleAdapter.MyHolder> {

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, TtsStyle item);
    }

    private int select=0;
    private List<TtsStyle> mList;//数据源
    private OnItemClickListener itemClickListener;

    public TtsStyleAdapter(List<TtsStyle> data) {
        this.mList = data;
    }

    public void setItemClickListener(OnItemClickListener listener){
        this.itemClickListener=listener;
    }


    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tts_style_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            setSelect(position);
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position, mList.get(position));

            }
        });
        holder.textView.setText(mList.get(position).name);
        holder.tv_des.setText(mList.get(position).extra);
        if (position==select){
            holder.itemView.setBackgroundColor(Color.CYAN);
        }else {
            holder.itemView.setBackgroundColor(Color.GRAY);
        }


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 自定义的ViewHolder
     */
    static class MyHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView tv_des;

        public MyHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tts_style_name);
            tv_des=itemView.findViewById(R.id.tts_style_des);
        }
    }
}
