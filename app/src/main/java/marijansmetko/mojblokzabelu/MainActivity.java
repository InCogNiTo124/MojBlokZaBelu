package marijansmetko.mojblokzabelu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    RadioGroup zvaliMi;

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
        zvaliMi = (RadioGroup) findViewById(R.id.radio_group);

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
                if (isChecked) {
                    belaVi.setChecked(!isChecked);
                }
            }
        });
        belaVi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    belaMi.setChecked(!isChecked);
                }
            }
        });

        bodovi.setMovementMethod(new ScrollingMovementMethod());

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
                dbHelper.addGame();
                this.updateUI();
                return true;
            case R.id.action_obrisi:
                Log.d(Constants.TAG, "Obrisi");
                dbHelper.deleteLastPoints();
                updateUI();
                return true;
            case R.id.action_prethodne:
                Cursor cursor = dbHelper.getAllGames();
                final GameAdapter gameAdapter = new GameAdapter(this, cursor);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setAdapter(gameAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                        builderInner.setMessage(R.string.continue_game);
                        builderInner.setTitle(R.string.upozorenje);
                        builderInner.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int nevermind) {
                                dialog.dismiss();
                                dbHelper.setCurrentGame(gameAdapter.getItem(which));
                                updateUI();
                            }
                        });
                        builderInner.setNeutralButton(R.string.button_cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                        Log.d(Constants.TAG, "Clicked no." + which);
                    }
                });
                builder.show();
                this.updateUI();
                return true;
//            case R.id.action_settings:
//                Log.d(Constants.TAG, "Postavke");
//                return true;
            case R.id.action_about:
                AlertDialog.Builder builderAbout = new AlertDialog.Builder(this);
                builderAbout.setTitle(R.string.app_about_title)
                        .setMessage(R.string.app_about_msg)
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                Log.d(Constants.TAG, "O aplikaciji");
                return true;
            case R.id.action_promjeni_djelitelja:
                Log.d(Constants.TAG, "Promjeni djelitelja");
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
        int viZvanja = zvanjaVi.getText().length() == 0 ? 0 : Integer.parseInt(zvanjaVi.getText().toString());
        boolean miBela = belaMi.isChecked();
        boolean viBela = belaVi.isChecked();
        int miZvali = zvaliMi.getCheckedRadioButtonId();
        if (miZvali < 0) {
            Toast.makeText(this, R.string.toast_error_zvali, Toast.LENGTH_SHORT).show();
        } else if (!provjeriZvanje(miZvanja) || !provjeriZvanje(viZvanja)) {
            Toast.makeText(this, R.string.toast_error_zvanja, Toast.LENGTH_SHORT).show();
        } else if (miBodovi + viBodovi != Constants.MAX_POINTS &&
                !(miBodovi == Constants.STIGLJA || viBodovi == Constants.STIGLJA)) {
            Toast.makeText(this, R.string.toast_error_bodovi, Toast.LENGTH_SHORT).show();
        } else if (miZvanja != 0 && viZvanja != 0) {
            /* SHOULD NEVER HAPPEN BUT ANYWAYS... */
            Toast.makeText(this, R.string.toast_error_zvanja, Toast.LENGTH_SHORT).show();
        } else {
            addPoints(miBodovi, viBodovi, miZvanja, viZvanja, miBela, viBela, miZvali  == R.id.label_mi);
            updateUI();
        }
    }

    public void addPoints(int miBodovi, int viBodovi, int miZvanja, int viZvanja, boolean miBela, boolean viBela, boolean miZvali) {
        int mi = miBodovi + miZvanja;
        int vi = viBodovi + viZvanja;

        if (miBela) {
            mi += Constants.BELA;
        } else if (viBela) {
            vi += Constants.BELA;
        }

        if (miZvali && mi <= vi || vi >= Constants.STIGLJA) {
            vi += mi;
            mi = 0;
        } else if (!miZvali /* == viZvali*/ && vi <= mi || mi >= Constants.STIGLJA) {
            mi += vi;
            vi = 0;
        }

        dbHelper.addPoints(mi ,vi);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private class GameAdapter extends CursorAdapter {

        GameAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.game_info, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView gameDate = (TextView) view.findViewById(R.id.game_date);
            TextView gameScore = (TextView) view.findViewById(R.id.game_score);
            gameDate.setText(cursor.getString(1));
            int[] a = new int[] {cursor.getInt(2), cursor.getInt(3)};
            if (a[0] > a[1] && a[0] > Constants.WIN_POINTS) {
                gameScore.setTextColor(Color.parseColor("#00A000"));
            } else if (a[1] > a[0] && a[1] > Constants.WIN_POINTS) {
                gameScore.setTextColor(Color.parseColor("#ff0000"));
            } else {
                gameScore.setTextColor(Color.parseColor("#0080f0"));
            }
            gameScore.setText(Utility.stringScoreFromDatabase(a));
        }

        @Override
        public Integer getItem(int position) {
            Cursor c = this.getCursor();
            int ID = -1;
            if (c.moveToPosition(position)) {
                ID = c.getInt(0);
            }
            return ID;
        }
    }
}
