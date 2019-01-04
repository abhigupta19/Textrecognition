package com.sar.user.mlkit;

import android.app.Notification;
import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
ImageView imageView;
TextView textView;
int SElECT_IMAGE=1;
Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.textView);
        Button button=findViewById(R.id.button);
        Button button2=findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap==null)
                {
                    Toast.makeText(MainActivity.this,"Image id null",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
                    FirebaseVisionTextDetector firebaseVisionTextDetector=FirebaseVision.getInstance().getVisionTextDetector();
                    firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            process(firebaseVisionText);
                        }
                    });
                }
            }

            private void process(FirebaseVisionText firebaseVisionText) {
                List<FirebaseVisionText.Block >blocks=firebaseVisionText.getBlocks();
                if(blocks.size()==0)
                {
                     Toast.makeText(MainActivity.this,"no text in picture",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for(FirebaseVisionText.Block block:firebaseVisionText.getBlocks())
                    {
                        String text=block.getText();
                        textView.setText(text);
                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select picture"),SElECT_IMAGE );

            }

        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK)
        {
            Uri uri=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



}
