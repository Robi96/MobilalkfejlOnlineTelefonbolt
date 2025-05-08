package com.example.mobilalkfejl_online_telefonbolt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<PhoneItem> mPhoneItemsData;
    private ArrayList<PhoneItem> mPhoneItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;
    ItemAdapter(Context context, ArrayList<PhoneItem> phoneData){
        this.mPhoneItemsData = phoneData;
        this.mPhoneItemsDataAll = phoneData;
        this.mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        PhoneItem current = mPhoneItemsData.get(position);

        holder.bindTo(current);

        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mPhoneItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shopFilter;
    }

    private Filter shopFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<PhoneItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0){
                results.count = mPhoneItemsDataAll.size();
                results.values = mPhoneItemsDataAll;
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(PhoneItem item : mPhoneItemsDataAll){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mPhoneItemsData = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mPhoneItemText;
        private TextView mItemNameText;
        private TextView mItemPriceText;
        private ImageView mPhoneImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mPhoneItemText = itemView.findViewById(R.id.phoneTitle);
            mItemNameText = itemView.findViewById(R.id.phoneDesc);
            mItemPriceText = itemView.findViewById(R.id.phonePrice);
            mPhoneImage = itemView.findViewById(R.id.imageItem);
        }

        public void bindTo(PhoneItem current) {
            mPhoneItemText.setText(current.getName());
            mItemNameText.setText(current.getDesc());
            mItemPriceText.setText(current.getPrice());

            Glide.with(mContext).load(current.getResource()).into(mPhoneImage);
            itemView.findViewById(R.id.addCart).setOnClickListener(v -> {
                ((ShopMainSite) mContext).update(current);
                Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.blink);
                itemView.startAnimation(anim);
            });
            itemView.findViewById(R.id.removeItem).setOnClickListener(v -> ((ShopMainSite) mContext).delete(current));
            itemView.findViewById(R.id.updatePrice).setOnClickListener(v -> {
                EditText temp = itemView.findViewById(R.id.newPrice);
                String temp2 = temp.getText().toString();
                ((ShopMainSite) mContext).updatePrice(current, temp2);
            });
        }
    }
}
