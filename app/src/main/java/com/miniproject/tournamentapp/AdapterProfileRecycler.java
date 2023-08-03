package com.miniproject.tournamentapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterProfileRecycler extends RecyclerView.Adapter<AdapterProfileRecycler.MyViewHolder> {

    List<ModelInterests> list;
    Context context;

    private AdapterProfileRecycler.OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onClick(int i);
    }

    public void setOnItemClickListener(AdapterProfileRecycler.OnItemClickListener listener){
        mListener = listener;
    }

    public AdapterProfileRecycler(Context context,List<ModelInterests> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_interests,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelInterests i = list.get(position);

        holder.mGameName.setText(i.getGameName());
        holder.mGameIcon.setImageResource(Keys.getGame(i.getGameName()));

        if (i.isSelected()){
            holder.ll.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.lightGreen)));
        }else {
            holder.ll.setBackground(null);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mGameName;
        ShapeableImageView mGameIcon;
        LinearLayout ll;

        public MyViewHolder(@NonNull View itemView,final AdapterProfileRecycler.OnItemClickListener listener) {
            super(itemView);

            mGameName = itemView.findViewById(R.id.gamename_interest);
            mGameIcon = itemView.findViewById(R.id.gameicon_interest);
            ll = itemView.findViewById(R.id.con_interest);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener!=null && position!=RecyclerView.NO_POSITION){
                        listener.onClick(position);
                    }
                }
            });

        }
    }
}
