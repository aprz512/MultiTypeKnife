# MultiTypeKnife

### 使用
```groovy
    implementation "com.aprz:multitype-api:1.0.1"
    implementation "com.aprz:multitype-annotation:1.0.1"
    annotationProcessor "com.aprz:multitype-compiler:1.0.1"
```

### 说明
是MultiType库的一个辅助工具，为了简化 adapter 相关类的编写。原来需要写两个类 ItemVieBinder 与 ViewHolder。

由于 ItemViewBinder 里面的代码几乎是模板化的，没有什么逻辑，所以做了一个注解处理器来完成 ItemViewBinder 里面的逻辑。

而 ViewHolder 可能会有很多额外的需求，所以不生成 ViewHolder 相关代码。

现在只需要按照如下的写法即可：
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
