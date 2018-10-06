package com.example.thita.fabapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<ImageUploadInfo> mListUpload;

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
        Picasso.with(mContext)
                .load(target.getImageURL())
                .placeholder(R.mipmap.ic_launcher)
                .fit().centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (mListUpload == null)   { return  0;}
        return mListUpload.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView imageNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view_list);
            imageNameTextView = (TextView) itemView.findViewById(R.id.imageNameTextView);

        }

        public void removeItem(int position) {
            mListUpload.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListUpload.size());
        }
    }

}
