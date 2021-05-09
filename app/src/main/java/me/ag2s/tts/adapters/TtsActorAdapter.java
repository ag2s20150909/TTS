package me.ag2s.tts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.ag2s.tts.R;
import me.ag2s.tts.services.TtsActor;

public class TtsActorAdapter extends BaseAdapter {
    List<TtsActor> mData;
    Context mContext;

    public TtsActorAdapter(Context context) {
        this.mData = new ArrayList<>();
        this.mContext = context;
    }

    public void upgrade(List<TtsActor> d) {
        this.mData = d;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TtsActor data = mData.get(position);
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.tts_actor_item, null);

            viewHolder = new ViewHolder();
            viewHolder.tv_title = convertView.findViewById(R.id.actor_name);
            viewHolder.iv_flag=convertView.findViewById(R.id.act_flags);
            viewHolder.tv_des=convertView.findViewById(R.id.act_des);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        viewHolder.tv_title.setText(data.getName());
        viewHolder.tv_des.setText(data.getNote());
        if(data.getLocale().getLanguage().equals("zh")){
            if (data.getGender()){
                viewHolder.iv_flag.setImageResource(R.drawable.ic_woman);
            }else {
                viewHolder.iv_flag.setImageResource(R.drawable.ic_man);
            }
        }


        return convertView;
    }

    public class ViewHolder {


        TextView tv_title;
        TextView tv_des;
        ImageView iv_flag;


    }
}
