package com.sportsdigest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private Button selectTeamsButton;
    private Button selectPlayersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectTeamsButton = findViewById(R.id.selectTeamsButton);
        selectPlayersButton = findViewById(R.id.selectPlayersButton);

        selectTeamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement team selection
            }
        });

        selectPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement player selection
            }
        });
    }
}
