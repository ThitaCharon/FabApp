package com.example.thita.fabapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<ImageUploadInfo> mListUpload;
    private OnItemClickListener mListener;

    public RecyclerViewAdapter (Context context, List<ImageUploadInfo> upload){
        mContext = context;
        mListUpload = upload;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycleview, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageUploadInfo target = mListUpload.get(position);
        holder.imageNameTextView.setText(target.getImageName());
        holder.quntityTextView.setText(target.getImageQuantity());
        holder.unitTextView.setText(target.getImageUnit());
        Picasso.with(mContext)
                .load(target.getImageURL())
                .placeholder(R.mipmap.ic_launcher)
                .fit().centerCrop()
                .into(holder.imageView);
    }

    public List<ImageUploadInfo> getData() {
        return mListUpload;
    }

    public void removeItem(int position) {
        mListUpload.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(mContext, R.string.REMOVE_ITEM, Toast.LENGTH_LONG).show();

    }

    @Override
    public int getItemCount() {
        if (mListUpload == null)   { return  0;}
        return mListUpload.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView;
        public TextView imageNameTextView, quntityTextView, unitTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view_list);
            imageNameTextView = (TextView) itemView.findViewById(R.id.item_title_display);
            quntityTextView = (TextView) itemView.findViewById(R.id.quantity_display);
            unitTextView = (TextView) itemView.findViewById(R.id.unit_display);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    mListener.onDelete(position);
                }
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener ){
        mListener = listener;
    }


    public interface OnItemClickListener{
        void onDelete(int position);
    }
}
