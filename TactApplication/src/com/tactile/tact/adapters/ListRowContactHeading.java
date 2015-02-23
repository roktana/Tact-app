package com.tactile.tact.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tactile.tact.R;

public class ListRowContactHeading {

    private String heading = null;

    public ListRowContactHeading(String heading) {
        this.heading = heading;
    }

    public View inflate(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contacts_group_view, null);
        }

        if (convertView != null) {
            TextView headingView = (TextView) convertView.findViewById(R.id.contact_group_heading);

            // Replace the heading for miscellaneous characters
            if (this.heading.equals("_")) {
                this.heading = "!@#";
            }

            headingView.setText(this.heading);
        }
        return convertView;
    }
}
