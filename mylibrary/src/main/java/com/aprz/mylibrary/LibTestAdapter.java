package com.aprz.mylibrary;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aprz.multitypeknife.annotation.ItemBinder;
import com.aprz.multitypeknife.annotation.ItemLayoutId;
import com.aprz.multitypeknife.api.BaseViewHolder;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

public class LibTestAdapter extends MultiTypeAdapter {

    public LibTestAdapter(@NonNull List<?> items) {
        super(items);
        register(Item.class, new LibTestItemBinder());
    }

    @ItemBinder(name = "LibTestItemBinder")
    static class LibTestViewHolder extends BaseViewHolder<Item> {

        @ItemLayoutId
        static int layoutId = R.layout.content_lib;

        TextView libText;

        public LibTestViewHolder(@NonNull View itemView) {
            super(itemView);
            libText = id(R.id.item_text);
        }

        @Override
        public void bindView(Item item) {
            libText.setText("item text with set");
        }
    }

}
