package com.miniproject.tournamentapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyViewHolder>{

    List<ModelUser> list;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onClick(int position);
        void onDeleteUser(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public AdapterUser(Context context, List<ModelUser> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_user,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelUser m = list.get(position);
        holder.mName.setText(m.getName());
        if (m.isDelete())
            holder.del.setVisibility(View.VISIBLE);
        else
            holder.del.setVisibility(View.GONE);


        if (m.getAvatar()==20)
            holder.mDP.setImageResource(R.drawable.icon_users);
        else
            holder.mDP.setImageResource(Keys.getAvatar(m.getAvatar()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        ShapeableImageView mDP;
        ImageButton del;
        LinearLayout ll;

        public MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);

            mName = itemView.findViewById(R.id.name_user);
            mDP = itemView.findViewById(R.id.dp_user);
            ll = itemView.findViewById(R.id.con_user);
            del = itemView.findViewById(R.id.remove_user);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(getAdapterPosition());
                }
            });
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteUser(getAdapterPosition());
                }
            });
        }
    }
}
