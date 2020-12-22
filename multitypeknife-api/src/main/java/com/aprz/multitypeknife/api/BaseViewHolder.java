package com.aprz.multitypeknife.api;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<DATA> extends RecyclerView.ViewHolder {

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindView(DATA data);

    public final <T extends View> T id(@IdRes int id) {
        return itemView.findViewById(id);
    }

}
