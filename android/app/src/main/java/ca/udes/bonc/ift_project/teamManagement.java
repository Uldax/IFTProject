package ca.udes.bonc.ift_project;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.Override;

import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.communication.QueryEventService;
import ca.udes.bonc.ift_project.communication.QueryIntentService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;
import ca.udes.bonc.ift_project.utils.ConvertJson;

public class teamManagement extends Activity {
    private static RestApiResultReceiver mReceiver;
    private String idEvent;
    private Button addTeamButton = null;
    private Button shuffleParticipantsButton = null;
    private EditText teamNameEditText = null;
    private ListView teamListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_management);

        if (getArguments() != null) {
            idEvent = getArguments().getString(ARG_PARAM1);
        }

        //Gettings Views
        addTeamButton = (Button) findViewById(R.id.addTeamButton);
        shuffleParticipantsButton = (Button)findViewById(R.id.shuffleParticipantsButton);
        teamNameEditText= (EditText) findViewById(R.id.teamNameEditText);
        teamNameEditText = (ListView) findViewById(R.id.teamListView);

        //Settings Button listeners
        addTeamButton.setOnClickListener(this);
        shuffleParticipantsButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addTeamButton:
                QueryEventService.startActionCreateTeam(v.getContext(), mReceiver, eventID, teamNameEditText.getText());
                teamNameEditText.getText().clear();
                break;
            case R.id.shuffleParticipantsButton:
                startActionShuffleParticipants(v.getContext(), mReceiver, eventID,String teamName);

                break;
        }
    }
}
