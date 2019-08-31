package com.heliumfarticle.memo51;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView = findViewById(R.id.textView);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void changeImage(View view) {
        GifImageView gifImageView = findViewById(R.id.gifImageView);
        ImageView imageView = findViewById(R.id.imageView);
        if(view.getId() == R.id.imageView) {
            gifImageView.setVisibility(View.VISIBLE);
            gifImageView.setImageResource(R.drawable.wall);
            imageView.setVisibility(View.INVISIBLE);
        }else{
            gifImageView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
