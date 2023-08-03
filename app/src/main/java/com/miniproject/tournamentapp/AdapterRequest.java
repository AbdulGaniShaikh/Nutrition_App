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

public class AdapterRequest extends RecyclerView.Adapter<AdapterRequest.MyViewHolder>{

    List<ModelRequest> list;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onAcceptClick(int position);
        void onRejectUser(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public AdapterRequest(Context context, List<ModelRequest> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_request,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelRequest m = list.get(position);
        holder.mName.setText(m.getName());
        holder.mDP.setImageResource(Keys.getAvatar(m.getDp()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        ShapeableImageView mDP;
        ImageButton accept,reject;
        LinearLayout ll;

        public MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);

            mName = itemView.findViewById(R.id.name_userrequest);
            mDP = itemView.findViewById(R.id.dp_userrequest);
            ll = itemView.findViewById(R.id.con_userrequest);
            accept = itemView.findViewById(R.id.accept_userrequest);
            reject = itemView.findViewById(R.id.reject_userrequest);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener!=null)
                        listener.onAcceptClick(getAdapterPosition());
                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener!=null)
                        listener.onRejectUser(getAdapterPosition());
                }
            });
        }
    }
}
