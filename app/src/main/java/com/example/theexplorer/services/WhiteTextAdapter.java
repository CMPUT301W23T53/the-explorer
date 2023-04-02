package com.example.theexplorer.services;

public class WhiteTextAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mData;

    public WhiteTextAdapter(Context context, List<String> data) {
        super(context, android.R.layout.simple_list_item_1, data);
        mContext = context;
        mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mData.get(position));
        holder.textView.setTextColor(Color.WHITE);

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
