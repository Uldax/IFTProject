package ca.udes.bonc.ift_project.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.dataObject.Event;

/**
 * Created by cbongiorno on 14/12/2015.
 */
public class ParticipantAdapter extends ArrayAdapter<Event> {
    Context context;
    int layoutResourceId;
    private HashMap<String,String> data;
    private List<String> listData;
    private String idUser;

    public ParticipantAdapter(Context context, int layoutResourceId, HashMap<String,String> data, String idUser) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.idUser = idUser;
        this.listData = new ArrayList<String>();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            listData.add((String)pair.getKey());
        }

        Log.i("ParticipantAdapter", "Created View");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EventHolder holder = null;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new EventHolder();
            holder.title = (TextView)row.findViewById(R.id.title);
            holder.me = (TextView)row.findViewById(R.id.me);

            row.setTag(holder);
        }
        else
        {
            holder = (EventHolder)row.getTag();
        }

        String idParticipate = this.listData.get(position);
        String nameParticipate = data.get(idParticipate);

        holder.title.setText(nameParticipate);
        if(!idParticipate.equals(idUser))
            holder.me.setVisibility(View.GONE);

        return row;
    }

    static class EventHolder
    {
        TextView title;
        TextView me;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        Log.i("ParticipantAdapter","Update View");
    }

    public HashMap<String,String> getData(){
        return data;
    }
}
