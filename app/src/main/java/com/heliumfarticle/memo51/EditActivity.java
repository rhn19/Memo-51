package com.heliumfarticle.memo51;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;

public class EditActivity extends AppCompatActivity {

    int noteId;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final EditText editText = findViewById(R.id.editText);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);

        if(noteId != -1){
            editText.setText(MainActivity.notes.get(noteId));
        }else{
            MainActivity.notes.add("");
            noteId = MainActivity.notes.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.notes.set(noteId, String.valueOf(s));
                MainActivity.arrayAdapter.notifyDataSetChanged();

                String string = MainActivity.notes.get(noteId);
                String[] lines = string.split(System.getProperty("line.separator"));
                textView.setText(MainActivity.notes.get(noteId).length() + " Characters(s)," + lines.length + " Line(s)");
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.heliumfarticle.memo51", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet<>(MainActivity.notes);
                sharedPreferences.edit().putStringSet("notes", set).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String string = MainActivity.notes.get(noteId);
        String[] lines = string.split(System.getProperty("line.separator"));
        textView = findViewById(R.id.textView);
        textView.setText(MainActivity.notes.get(noteId).length() + " Characters(s)," + lines.length + " Line(s)");

    }

    @Override
    protected void onStop() {
        super.onStop();

        String string = MainActivity.notes.get(noteId);
        String[] lines = string.split(System.getProperty("line.separator"));

        setAlarm(lines);
        calcTotal();

    }

    protected void calcTotal(){
        int total = 0;
        String string = MainActivity.notes.get(noteId);
        if(!string.contains("@Rs("))
            return;

        while (true) {
            String  answer = findSpecials("@Rs(", 4, -1);
            if(answer.equals("n"))
                break;
            else {
                total += Integer.valueOf(answer);
                Toast.makeText(EditActivity.this, "Total : " + total, Toast.LENGTH_SHORT).show();
            }
        }
        string = MainActivity.notes.get(noteId);
        MainActivity.notes.set(noteId, string.concat("\nTotal : " + total));
        MainActivity.arrayAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.heliumfarticle.memo51", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet<>(MainActivity.notes);
        sharedPreferences.edit().putStringSet("notes", set).apply();
    }

    //not working for multiple alarms
    //need to add intent array
    protected void setAlarm(String[] lines){
        while (true) {
            String answer = findSpecials("@Alarm", 7, 12);
            String[] parts = answer.split("\\.");
            if(answer.equals("n")) {
                break;
            }
            else {
                int hour = Integer.valueOf(parts[0]);
                int minute = Integer.valueOf(parts[1]);
                String message = "Alarm by Memo 51";
                for(int i = 0; i < lines.length; i++) {
                    if (lines[i].contains("@Alarm(")) {
                        if (i - 1 >= 0 && !(lines[i - 1].contains("@Alarm("))) {
                            message = lines[i - 1];
                        }
                    }
                }
                Toast.makeText(EditActivity.this, "Alarm Set : " + hour + "." + minute, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                i.putExtra(AlarmClock.EXTRA_HOUR, hour);
                i.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                i.putExtra(AlarmClock.EXTRA_MESSAGE, message);
                startActivity(i);
            }
        }
    }

    protected String findSpecials(String special, int lengthSpecial, int syntaxLength){
        String string = MainActivity.notes.get(noteId);
        int startPos = string.indexOf(special);
        int endPos = string.indexOf(")");
        if(startPos == -1 || endPos == -1){
            return "n";
        }
        String answer;

        //for answer = till ")"
        if(syntaxLength == -1){
            answer = string.substring(startPos + lengthSpecial, endPos);
            MainActivity.notes.set(noteId, string.replaceFirst("@", "added").replaceFirst("\\(", " . ").replaceFirst("\\)", "!"));
            MainActivity.arrayAdapter.notifyDataSetChanged();

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.heliumfarticle.memo51", Context.MODE_PRIVATE);
            HashSet<String> set = new HashSet<>(MainActivity.notes);
            sharedPreferences.edit().putStringSet("notes", set).apply();

        }
        else if(startPos + syntaxLength == endPos){
            answer = string.substring(startPos + lengthSpecial, endPos);
            MainActivity.notes.set(noteId, string.replaceFirst("@", "set").replaceFirst("\\(", " : ").replaceFirst("\\)", "!"));
            MainActivity.arrayAdapter.notifyDataSetChanged();

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.heliumfarticle.memo51", Context.MODE_PRIVATE);
            HashSet<String> set = new HashSet<>(MainActivity.notes);
            sharedPreferences.edit().putStringSet("notes", set).apply();
            //Toast.makeText(EditActivity.this, "Found : " + answer, Toast.LENGTH_SHORT).show();
        }else{
            return "n";
        }

        return answer;
    }

}
