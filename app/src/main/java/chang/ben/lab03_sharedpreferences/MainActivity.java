package chang.ben.lab03_sharedpreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "chang.ben.lab03_sharedpreferences.sharedpref";
    Button bRight, bLeft;
    TextView tRight, tLeft;
    SeekBar seekBar;
    TextView[] views;
    ConstraintLayout layout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    long startTime, clicks;
    float cPS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bRight = findViewById(R.id.bottomright);
        bLeft = findViewById(R.id.bottomleft);
        tRight = findViewById(R.id.topright);
        tLeft = findViewById(R.id.topleft);
        seekBar = findViewById(R.id.seekbar);
        layout = findViewById(R.id.activity_main_layout);
        views = new TextView[]{bRight, bLeft, tRight, tLeft};
        bRight.setOnClickListener(this);
        bLeft.setOnClickListener(this);
        tRight.setOnClickListener(this);
        tLeft.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editor.clear().apply();
                setInitialValues();
                return true;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int lastProgress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                for (TextView tv: views) { tv.setTextSize(i);}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastProgress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Snackbar snackbar = Snackbar.make(layout, "Font size changed to " + seekBar.getProgress() + "sp", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        seekBar.setProgress(lastProgress);
                        for(TextView tv : views) {tv.setTextSize(lastProgress);}
                        Snackbar.make(layout, "Font size changed to " + lastProgress + "sp", Snackbar.LENGTH_LONG).show();
                    }
                });
                snackbar.setActionTextColor(Color.BLUE);
                View snackBarView = snackbar.getView();
                TextView textView = snackBarView.findViewById(R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        });

        setInitialValues();
        startTime = System.currentTimeMillis();
    }
    private void setInitialValues(){
        for (TextView tv: views) {
            tv.setText(sharedPreferences.getString(tv.getTag().toString(), "0"));
        }
        seekBar.setProgress(50);
    }
    Toast lastToast, currentToast;
    @Override
    public void onClick(View view) {
        TextView b = (TextView) view;
        b.setText("" + (Integer.parseInt(b.getText().toString()) + 1));
        editor.putString(b.getTag().toString(), b.getText().toString()).apply();
        cPS = ++clicks / ((System.currentTimeMillis()-startTime)/1000f);
        if(lastToast != null)
            lastToast.cancel();
        currentToast = Toast.makeText(this, "" + cPS, Toast.LENGTH_SHORT);
        lastToast = currentToast;
        currentToast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setInitialValues();
    }
}