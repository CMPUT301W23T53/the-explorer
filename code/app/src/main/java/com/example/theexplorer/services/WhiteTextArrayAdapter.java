/**
 * A custom ArrayAdapter that sets the text color of the items in the list to white.
 */
package com.example.theexplorer.services;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WhiteTextArrayAdapter extends ArrayAdapter<String> {
    /**
     * Constructs a new WhiteTextArrayAdapter object.
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public WhiteTextArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }
    /**
     * Returns a View that displays the data at the specified position in the data set.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setTextColor(Color.WHITE); // Set text color to white
        return view;
    }
}
