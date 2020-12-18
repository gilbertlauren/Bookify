package com.example.bookify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class Upload extends AppCompatActivity implements View.OnClickListener {
    private EditText edtTitle, edtAuthor, editDescription;
    private Button upload;
    private ImageView imageView;
    String cover_url;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        
        edtTitle = findViewById(R.id.editTitle);
        edtAuthor = findViewById(R.id.editAuthor);
        editDescription = findViewById(R.id.editDescription);
        imageView = findViewById(R.id.imageCover);
        upload = findViewById(R.id.buttonUploadPDF);
        upload.setOnClickListener(this);
        imageView.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("books");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonUploadPDF:
                selectPDF();
                break;
            case R.id.imageCover:
                selectCover();
                break;
            default:
                break;
        }
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select your PDF"),1);
    }

    private void selectCover() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select your Cover"),2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data !=null && data.getData() !=null){
            uploadPDF(data.getData());
        }
        if(requestCode == 2 && resultCode == RESULT_OK && data !=null && data.getData() !=null){
            data.getData();
            uploadCover(data.getData());
        }
    }

    private void uploadCover(Uri data) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image");
        pd.show();
        String fileName = "default_file_name";
        Cursor returnCursor =
                getContentResolver().query(data, null, null, null, null);
        try {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
        }catch (Exception e){
            //handle the failure cases here
        } finally {
            returnCursor.close();
        }
        StorageReference reference = storageReference.child("covers/"+ fileName);
        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();
                cover_url = url.toString();
                Toast.makeText(Upload.this, cover_url, Toast.LENGTH_SHORT).show();
                MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                        new CenterCrop(),
                        new RoundedCornersTransformation(25, 0, RoundedCornersTransformation.CornerType.ALL)
                );

                Glide.with(Upload.this).load(cover_url)
                        .transform(multi).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                }).placeholder(R.drawable.ic_launcher_foreground).into(imageView);
                pd.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double percent = (100.00 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) percent + "%");
            }
        });
    }

    private void uploadPDF(Uri data) {
        final ProgressDialog pd = new ProgressDialog(this);
        StorageReference reference = storageReference.child("uploads/"+edtTitle.getText().toString()+".pdf");
        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();
                String key = databaseReference.push().getKey();
                Book book = new Book(edtTitle.getText().toString(), editDescription.getText().toString(), "Category1", cover_url, key, edtAuthor.getText().toString(), url.toString());
                databaseReference.child(key).setValue(book);
                pd.dismiss();
                finish();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double percent = (100.00 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) percent + "%");
            }
        });
    }
}