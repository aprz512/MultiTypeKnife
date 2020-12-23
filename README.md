# MultiTypeKnife

是MultiType的一个辅助工具，为了简化 adapter 相关类的编写。
原来需要写两个类，现在只需要这样，即可：
```java
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

```
