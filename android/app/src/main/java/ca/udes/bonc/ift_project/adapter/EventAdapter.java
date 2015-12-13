package ca.udes.bonc.ift_project.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.dataObject.Event;

/**
 * Created by cbongiorno on 12/12/2015.
 */
public class EventAdapter extends ArrayAdapter<Event> {
    Context context;
    int layoutResourceId;
    private List<Event> data;

    public EventAdapter(Context context, int layoutResourceId, List<Event> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

        Log.i("EventAdapter", "Created View");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EventHolder holder = null;
        Event event = data.get(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new EventHolder();
            holder.title = (TextView)row.findViewById(R.id.title);
            holder.date = (TextView)row.findViewById(R.id.title);
            holder.author = (TextView)row.findViewById(R.id.author);
            holder.image = (ImageView)row.findViewById(R.id.image);

            row.setTag(holder);
        }
        else
        {
            holder = (EventHolder)row.getTag();
        }

        holder.title.setText(event.getTitle());
        holder.date.setText(DateFormat.format("dd MMM -> k:m", event.getDate()));
        holder.author.setText("By "+event.getAuthor());

        /*switch (event.getCategorie()){
            case ... :
                holder.image.setImageDrawable(R.drawable.run);
            break;
            default:
                holder.image.setImageDrawable(R.drawable.run);
        }*/

        return row;
    }

    static class EventHolder
    {
        TextView title;
        TextView date;
        TextView author;
        ImageView image;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        Log.i("EventAdapter","Update View");
    }

    public List<Event> getData(){
        return data;
    }
}