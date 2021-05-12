package me.ag2s.tts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.ag2s.tts.R;
import me.ag2s.tts.services.TtsActor;
import me.ag2s.tts.services.TtsActorManger;
import me.ag2s.tts.utils.CommonTool;

public class TtsActorAdapter extends RecyclerView.Adapter<TtsActorAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(int position, TtsActor item);
    }
    public void setItemClickListener(OnItemClickListener listener){
        this.itemClickListener=listener;
    }
    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
        this.notifyDataSetChanged();
    }
    private int select=0;

    private OnItemClickListener itemClickListener;
    List<TtsActor> mData;
    Context mContext;

    public TtsActorAdapter(Context context) {
        this.mData = new ArrayList<>();
        this.mContext = context;
    }

    public void upgrade(List<TtsActor> d) {
        this.mData = TtsActorManger.getInstance().sortByLocale(d,Locale.US);

        this.notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tts_actor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            setSelect(position);
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position, mData.get(position));
            }
        });
        TtsActor data = mData.get(position);
        holder.tv_title.setText(data.getName());
        holder.tv_des.setText(String.format("%s%s", CommonTool.localeToEmoji(data.getLocale()), data.getNote()));
        if (data.getGender()){
            //holder.iv_flag.setImageResource(R.drawable.ic_woman);
            holder.tv_title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_woman,0,0,0);
        } else {
            //holder.iv_flag.setImageResource(R.drawable.ic_man);
            holder.tv_title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_man,0,0,0);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_des;
        //ImageView iv_flag;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.actor_name);
            //iv_flag = itemView.findViewById(R.id.act_flags);
            tv_des = itemView.findViewById(R.id.act_des);
        }
    }
}
