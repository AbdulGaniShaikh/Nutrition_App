package com.miniproject.tournamentapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterTournament extends RecyclerView.Adapter<AdapterTournament.MyViewHolder> {

    List<ModelTournament> list;

    Context context;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onJoinClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public AdapterTournament(Context context, List<ModelTournament> list) {
        this.list = list;
        this.context = context;
//        filteredList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_tournament,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelTournament m = list.get(position);

        holder.mName.setText(m.getName());
        holder.mDate.setText(m.getDate());
        holder.mTime.setText(m.getTime());
        holder.mJoined.setText(m.getpJoined());
        holder.mGame.setText(m.getGame());
        holder.mParticipation.setText(m.getpType());

        holder.mImageView.setImageResource(Keys.getGame(m.getGame()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mName,mGame,mDate,mTime,mParticipation,mJoin,mJoined;
        ImageView mImageView;
        LinearLayout ll;


        public MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.gameimg_itemtour);

            mName = itemView.findViewById(R.id.name_itemtour);
            mGame = itemView.findViewById(R.id.game_itemtour);
            mDate = itemView.findViewById(R.id.date_itemtour);
            mTime= itemView.findViewById(R.id.time_itemtour);
            mParticipation = itemView.findViewById(R.id.ptype_itemtour);
            mJoined = itemView.findViewById(R.id.joined_itemtour);
            mJoin = itemView.findViewById(R.id.join_itemtour);

            ll = itemView.findViewById(R.id.con_itemtour);

            mJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener!=null && position!=RecyclerView.NO_POSITION)
                        listener.onJoinClick(position);
                }
            });

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener!=null && position!=RecyclerView.NO_POSITION)
                        listener.onJoinClick(position);
                }
            });
        }
    }

}
