package marijansmetko.mojblokzabelu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    int[] ZVANJA = {0, 20, 50, 100, 150, 200};
    BelaBlokHelper dbHelper;
    EditText bodoviMi;
    EditText bodoviVi;
    EditText zvanjaMi;
    EditText zvanjaVi;
    CheckBox belaMi;
    CheckBox belaVi;
    TextView ukupanScore;
    TextView bodovi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new BelaBlokHelper(this);

        bodoviMi = (EditText) findViewById(R.id.bodovi_mi);
        bodoviVi = (EditText) findViewById(R.id.bodovi_vi);
        zvanjaMi = (EditText) findViewById(R.id.zvanja_mi);
        zvanjaVi = (EditText) findViewById(R.id.zvanja_vi);
        belaMi = (CheckBox) findViewById(R.id.bela_mi);
        belaVi = (CheckBox) findViewById(R.id.bela_vi);
        ukupanScore = (TextView) findViewById(R.id.ukupan_score);
        bodovi = (TextView) findViewById(R.id.bodovi);

        bodoviMi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!bodoviVi.isFocused()) {
                    int x = Constants.MAX_POINTS - (s.length() == 0 ? 0 : Integer.parseInt(s.toString()));
                    bodoviVi.setText(String.valueOf(x > 0 ? x : 0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        bodoviVi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!bodoviMi.isFocused()) {
                    int x = Constants.MAX_POINTS - (s.length() == 0 ? 0 : Integer.parseInt(s.toString()));
                    bodoviMi.setText(String.valueOf(x < 0 ? 0 : x));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        zvanjaMi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    zvanjaMi.setText(zvanjaVi.getText());
                    zvanjaVi.setText("");
                }
            }
        });
        zvanjaVi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    zvanjaVi.setText(zvanjaMi.getText());
                    zvanjaMi.setText("");
                }
            }
        });

        belaMi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                belaVi.setChecked(!isChecked);
            }
        });
        belaVi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                belaMi.setChecked(!isChecked);
            }
        });

        updateUI();
    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ukupanScore.setText(Utility.stringScoreFromDatabase(dbHelper.getScore()));
                bodovi.setText(Utility.stringPointsFromDatabase(dbHelper.getPoints()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nova_igra:
                Log.d(Constants.TAG, "Nova igra");
                return true;
            case R.id.action_obrisi:
                Log.d(Constants.TAG, "Obrisi");
                return true;
            case R.id.action_prethodne:
                Log.d(Constants.TAG, "Prethodne igre");
                return true;
            case R.id.action_settings:
                Log.d(Constants.TAG, "Postavke");
                return true;
            case R.id.action_about:
                Log.d(Constants.TAG, "O aplikaciji");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean provjeriZvanje(int zvanje) {
        int s1, s2, s3, s4;
        for (int i = 0; i < ZVANJA.length; i++) {
            s1 = ZVANJA[i];
            if (s1 <= zvanje) {
                for (int j = 0; j < ZVANJA.length; j++) {
                    s2 = s1 + ZVANJA[j];
                    if (s2 <= zvanje) {
                        for (int k = 0; k < ZVANJA.length; k++) {
                           if (k != 4) {    // PRESKAČEM 150 JER JE MOGUCE IMAT SAMO JEDNO TAKVO
                               s3 = s2 + ZVANJA[k];
                               if (s3 <= zvanje) {
                                   for (int l = 0; l < ZVANJA.length - 1; l++) {
                                            // PRESKACEM 200 JER JE MOGUCE IMAT SAMO JEDNO TAKVO
                                       s4 = s3 + ZVANJA[l];
                                       if (s4 == zvanje) {
                                           return true;
                                       }
                                   }
                               } else {     // presli smo sumu, dzabe provjeravat
                                   break;
                               }
                           }
                        }
                    } else {    // presli smo sumu, dzabe provjeravat
                        break;
                    }
                }
            } else {    // presli smo sumu, dzabe provjeravat
                break;
            }
        }
        return false;
    }

    public void processInput(View view) {
        hideSoftKeyboard(this);
        int miBodovi = bodoviMi.getText().length() == 0 ? 0 : Integer.parseInt(bodoviMi.getText().toString());
        int viBodovi = bodoviVi.getText().length() == 0 ? 0 : Integer.parseInt(bodoviVi.getText().toString());
        int miZvanja = zvanjaMi.getText().length() == 0 ? 0 : Integer.parseInt(zvanjaMi.getText().toString());
        int viZvanja = zvanjaMi.getText().length() == 0 ? 0 : Integer.parseInt(zvanjaVi.getText().toString());
        dbHelper.addPoints(miBodovi, viBodovi);
        updateUI();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
