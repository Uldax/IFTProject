package ca.udes.bonc.ift_project.adapter;

import android.app.Activity;
import android.content.Context;
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
import ca.udes.bonc.ift_project.dataObject.Categories;
import ca.udes.bonc.ift_project.dataObject.Team;

/**
 * Created by cbongiorno on 12/12/2015.
 */
public class TeamAdapter extends ArrayAdapter<Team> {
    Context context;
    int layoutResourceId;
    private List<Team> data;
    private String idUser;

    public TeamAdapter(Context context, int layoutResourceId, List<Team> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;


        Log.i("TeamAdapter", "Created View");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TeamHolder holder;
        Team team = data.get(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TeamHolder();
            holder.name = (TextView)row.findViewById(R.id.name);
            holder.id = (TextView)row.findViewById(R.id.id);


            row.setTag(holder);
        }
        else
        {
            holder = (TeamHolder)row.getTag();
        }

        holder.name.setText(team.getName());
        holder.id.setText(team.getId());



        return row;
    }

    static class TeamHolder
    {
        TextView name;
        TextView id;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        Log.i("TeamAdapter","Update View");
    }

    public List<Team> getData(){
        return data;
    }
}
