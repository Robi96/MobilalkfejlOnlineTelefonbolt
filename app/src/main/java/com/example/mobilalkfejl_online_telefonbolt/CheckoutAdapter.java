package com.example.mobilalkfejl_online_telefonbolt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckViewHolder>{
    private ArrayList<PhoneItem> mPhoneItemsData;
    private Context mContext;
    private int lastPosition = -1;
    CheckoutAdapter(Context context, ArrayList<PhoneItem> phoneData){
        this.mPhoneItemsData = phoneData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CheckoutAdapter.CheckViewHolder(LayoutInflater.from(mContext).inflate(R.layout.checkout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CheckoutAdapter.CheckViewHolder holder, int position) {
        PhoneItem current = mPhoneItemsData.get(position);

        holder.bindTo(current);
    }

    @Override
    public int getItemCount() {
        return mPhoneItemsData.size();
    }

    class CheckViewHolder extends RecyclerView.ViewHolder{
        private TextView mPhoneItemText;
        private TextView mItemNameText;
        private TextView mItemPriceText;
        private ImageView mPhoneImage;
        public CheckViewHolder(View itemView) {
            super(itemView);
            mPhoneItemText = itemView.findViewById(R.id.check_phoneTitle);
            mItemNameText = itemView.findViewById(R.id.check_phoneDesc);
            mItemPriceText = itemView.findViewById(R.id.check_phonePrice);
            mPhoneImage = itemView.findViewById(R.id.check_imageItem);
        }

        public void bindTo(PhoneItem current) {
            mPhoneItemText.setText(current.getName());
            mItemNameText.setText(current.getDesc());
            mItemPriceText.setText(current.getPrice());

            Glide.with(mContext).load(current.getResource()).into(mPhoneImage);
            itemView.findViewById(R.id.removeCart).setOnClickListener(v -> ((CheckoutActivity) mContext).update(current));
        }
    }
}
