package com.fabian.mylocationsideaware.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.fabian.mylocationsideaware.R;
import com.fabian.mylocationsideaware.Utils.PlaceDB;

import java.util.List;

/**
 * Created by fabian on 8/21/2016.
 */
public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.CustomViewHolder> {

    private List<PlaceDB> feedItemList;
    private final OnItemClickListener listener;
    private final OnItemClickEditListener listenerEdit;
    private final OnItemClickEraseListener listenerErase;

    public PlacesListAdapter(List<PlaceDB> feedItemList, OnItemClickListener listener, OnItemClickEditListener listenerEdit, OnItemClickEraseListener listenerErase){
        this.feedItemList = feedItemList;
        this.listener = listener;
        this.listenerEdit = listenerEdit;
        this.listenerErase = listenerErase;
    }
    public interface OnItemClickListener{
        void onItemClick(PlaceDB place);
    }
    public interface OnItemClickEditListener{
        void onItemClickEdit(PlaceDB place);
    }
    public interface OnItemClickEraseListener{
        void onItemClickErase(PlaceDB place);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitemview,null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        PlaceDB feddItem = feedItemList.get(position);
        holder.placename.setText(feddItem.getNamePlace());
        holder.placeciy.setText(feddItem.getCity());
        holder.bind(feedItemList.get(position), listener,listenerEdit,listenerErase);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size(): 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView placename;
        protected TextView placeciy;
        protected Button    editplace;
        protected Button    eraseplace;

        public CustomViewHolder(View view){
            super(view);
            this.placename = (TextView) view.findViewById(R.id.placename);
            this.placeciy = (TextView)view.findViewById(R.id.placecity);
            this.editplace = (Button)view.findViewById(R.id.editbtn);
            this.eraseplace = (Button)view.findViewById(R.id.erasebtn);
        }
        public void bind(final PlaceDB item, final OnItemClickListener listener, final  OnItemClickEditListener editListener,final OnItemClickEraseListener eraseListener){
            placename.setText(item.getNamePlace());
            placeciy.setText(item.getCity());
            editplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                editListener.onItemClickEdit(item);
                }
            });
            eraseplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                eraseListener.onItemClickErase(item);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);

                }
            });
        }


    }
}
