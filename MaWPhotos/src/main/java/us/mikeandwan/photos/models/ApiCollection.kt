package us.mikeandwan.photos.models;

import java.util.ArrayList;
import java.util.List;

public class ApiCollection<T> {
    private long _count;
    private List<T> _items = new ArrayList<>();

    public long getCount() { return _count; }
    public void setCount(long count) { _count = count; }

    public List<T> getItems() { return _items; }
    public void setItems(List<T> items) { _items = items; }
}
