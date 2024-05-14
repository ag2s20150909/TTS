package me.ag2s.tts.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import me.ag2s.tts.R;
import me.ag2s.tts.data.TtsActor;
import me.ag2s.tts.utils.CommonTool;

public class TtsActorAdapter extends RecyclerView.Adapter<TtsActorAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(int position, TtsActor item);
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @SuppressWarnings("unused")
    public int getSelect() {
        return select;
    }

    public void setSelect(RecyclerView rv, int select) {
        setSelect(select);
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.scrollToPosition(select);
        }
    }

    public void setSelect(int select) {
        int oldSelect = this.select;
        this.select = select;
        this.notifyItemChanged(oldSelect);
        this.notifyItemChanged(select);
    }

    private int select = 0;

    private OnItemClickListener itemClickListener;
    final List<TtsActor> mData;

    public TtsActorAdapter(List<TtsActor> data) {
        this.mData = data;
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
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                setSelect(position);
                itemClickListener.onItemClick(position, mData.get(position));
            }
        });
        if (select == position) {
            holder.itemView.setBackground(holder.itemView.getContext().getDrawable(R.drawable.select));
        } else {
            holder.itemView.setBackground(holder.itemView.getContext().getDrawable(R.drawable.unselect));
        }
        TtsActor data = mData.get(position);
        holder.tv_title.setText(data.getShortName());
        Locale locale = data.getLocale();
        //locale.getDisplayCountry(Locale.getDefault());
        holder.tv_des.setText(String.format("%s%s\n%s", CommonTool.localeToEmoji(locale), locale.getDisplayLanguage(Locale.getDefault()), data.getNote()));
        if (data.getGender()) {
            holder.iv_flag.setImageResource(R.drawable.ic_woman);
            //holder.tv_title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_woman,0,0,0);
        } else {
            holder.iv_flag.setImageResource(R.drawable.ic_man);
            //holder.tv_title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_man,0,0,0);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tv_title;
        final TextView tv_des;
        final ImageView iv_flag;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.actor_name);
            iv_flag = itemView.findViewById(R.id.act_flags);
            tv_des = itemView.findViewById(R.id.act_des);
        }
    }
}
