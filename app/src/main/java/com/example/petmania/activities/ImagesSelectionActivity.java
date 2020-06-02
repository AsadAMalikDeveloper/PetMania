package com.example.petmania.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.example.petmania.R;
import com.example.petmania.model.Adds;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.example.petmania.utils.ProgressRequestBody;
import com.example.petmania.utils.UploadCallBack;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import io.paperdb.Paper;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ImagesSelectionActivity extends AppCompatActivity implements
        BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.ImageLoaderDelegate, UploadCallBack {

    private RecyclerView rvGallery;
    private GalleryAdapter galleryAdapter;
    LottieAnimationView camera, gallery;
    IPetManiaAPI mServices;
    private static final String TAG = ImagesSelectionActivity.class.getSimpleName();
    Uri selectedUri;
    Bitmap bitmap;
    //ImageView imageView;
    Dialog dialog;
    Button publishBtn;
    int add_id;
    AlertDialog alertDialog1;
    boolean flag = false;
    List<Uri> urislists;
    List<String> responseList = new ArrayList<>();

    ArrayList<String> files = new ArrayList<>();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_selection);
        mServices = Common.getAPI();

        pDialog = new ProgressDialog(this);
        camera = findViewById(R.id.camera_anim);
        gallery = findViewById(R.id.gallery_anim);
        publishBtn = findViewById(R.id.publish_btn);
        rvGallery = (RecyclerView) findViewById(R.id.rv);
        rvGallery.setHasFixedSize(true);

        alertDialog1 = new SpotsDialog.Builder()
                .setContext(ImagesSelectionActivity.this)
                .setTheme(R.style.uploadingImages)
                .build();
        //  imageView = findViewById(R.id.imageView5);
        initDialog();

        // use a linear layout manager
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(ImagesSelectionActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvGallery.setLayoutManager(horizontalLayoutManagaer);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ImagesSelectionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ImagesSelectionActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);
                }
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.example.petmania.activities.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(5)
                        .setMaximumMultiSelectCount(5)
                        .build();
                pickerDialog.show(getSupportFragmentManager(), "picker");

            }
        });
        publishBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                String isEdit = Paper.book().read(Prevalent.isEdit,null);
                if (isEdit.equals("true") && isEdit!=null) {
                    Adds adds1 = Paper.book().read(Prevalent.editAd, null);
                    if (adds1 != null) {//getExtension(file.toString())

                        if (urislists != null) {
                            int i = 0;
                            if (urislists.size() < 5) {
                                Toast.makeText(ImagesSelectionActivity.this, "Select Minimum 5 images", Toast.LENGTH_SHORT).show();
                            } else {
                                while (i < urislists.size()) {
                                    try {
                                        File file = new File(getFilePath(ImagesSelectionActivity.this, urislists.get(i)));
                                        File compressedImage = new Compressor.Builder(ImagesSelectionActivity.this)
                                                .setMaxWidth(640)
                                                .setMaxHeight(480)
                                                .setQuality(80)
                                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                                .build()
                                                .compressToFile(file);
                                        updateFile(compressedImage, i,adds1.getAdds_id());
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                    i++;
                                }
                                Toast.makeText(ImagesSelectionActivity.this, "Please Wait Uploading Files", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(ImagesSelectionActivity.this, "Empty list select images", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    if (urislists != null) {
                        int i = 0;
                        if (urislists.size() < 5) {
                            Toast.makeText(ImagesSelectionActivity.this, "Select Minimum 5 images", Toast.LENGTH_SHORT).show();
                        } else {
                            while (i < urislists.size()) {
                                try {
                                    File file = new File(getFilePath(ImagesSelectionActivity.this, urislists.get(i)));
                                    File compressedImage = new Compressor.Builder(ImagesSelectionActivity.this)
                                            .setMaxWidth(640)
                                            .setMaxHeight(480)
                                            .setQuality(80)
                                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                            .build()
                                            .compressToFile(file);
                                    uploadFile(compressedImage, i);
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                i++;
                            }
                            Toast.makeText(ImagesSelectionActivity.this, "Please Wait Uploading Files", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(ImagesSelectionActivity.this, "Empty list select images", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select image"), 1);*/

                Toast.makeText(ImagesSelectionActivity.this, "Working on it", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            selectedUri = data.getData();
            try {

                //imageView.setVisibility(View.VISIBLE);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedUri);
                //imageView.setImageBitmap(bitmap);
                /*imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ImagesSelectionActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        File file = null;
                        try {
                            file = new File(getFilePath(ImagesSelectionActivity.this, selectedUri));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        uploadFile(file);
                    }
                });*/
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)) {//DocumentsContract.isDocumentUri(context.getApplicationContext(), uri))
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    private void updateFile(File file, int num , int id) {

        Log.d(TAG, "updateFile: ###$$$ "+num);
        String fileName = new StringBuilder(Common.currentUser.getPhone())
                .append("_")
                .append(id)
                .append("_")
                .append(num)
                .append(".")
                .append("jpg")
                .toString();
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(file, this);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, progressRequestBody);
        final MultipartBody.Part userPhone = MultipartBody.Part.createFormData("phone", Common.currentUser.getPhone());
        final MultipartBody.Part adds_id = MultipartBody.Part.createFormData("adds_id", String.valueOf(id));
        final MultipartBody.Part uploaded_on = MultipartBody.Part.createFormData("uploaded_on", String.valueOf(System.currentTimeMillis()));

        alertDialog1.show();

        mServices.updateFile(userPhone, body, adds_id, uploaded_on)
                .enqueue(new Callback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        Toast.makeText(ImagesSelectionActivity.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < urislists.size(); i++) {
                            responseList.add(i, response.body());
                        }
                        Log.d(TAG, "onResponse: 5555 " + responseList.size() + " ghghgg  " + urislists.size() * urislists.size());

                        if (responseList.size() == urislists.size() * urislists.size()) {
                            alertDialog1.dismiss();
                            showEndDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        alertDialog1.dismiss();
                        Toast.makeText(ImagesSelectionActivity.this, "error " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }

    private void uploadFile(File file, int i) {
        if (Common.lastUploadedAdd == null) {
            Adds add = Paper.book().read(Prevalent.adds, null);
            add_id = add.getAdds_id();
        } else {
            add_id = Common.lastUploadedAdd.getAdds_id();
        }
        String fileName = new StringBuilder(Common.currentUser.getPhone())
                .append("_")
                .append(add_id)
                .append("_")
                .append(i)
                .append(".")
                .append("jpg")
                .toString();
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(file, this);
        Log.d(TAG, "uploadFile: WWW### " + file.toString());
        final MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, progressRequestBody);
        final MultipartBody.Part userPhone = MultipartBody.Part.createFormData("phone", Common.currentUser.getPhone());
        final MultipartBody.Part adds_id = MultipartBody.Part.createFormData("adds_id", String.valueOf(add_id));
        final MultipartBody.Part uploaded_on = MultipartBody.Part.createFormData("uploaded_on", String.valueOf(System.currentTimeMillis()));

        alertDialog1.show();

        mServices.uploadFile(userPhone, body, adds_id, uploaded_on)
                .enqueue(new Callback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        Toast.makeText(ImagesSelectionActivity.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < urislists.size(); i++) {
                            responseList.add(i, response.body());
                        }
                        Log.d(TAG, "onResponse: 5555 " + responseList.size() + " ghghgg  " + urislists.size() * urislists.size());

                        if (responseList.size() == urislists.size() * urislists.size()) {
                            alertDialog1.dismiss();
                            showEndDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        alertDialog1.dismiss();
                        Toast.makeText(ImagesSelectionActivity.this, "error " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void loadImage(File imageFile, ImageView ivImage) {
        Glide.with(ImagesSelectionActivity.this).load(imageFile).into(ivImage);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

        galleryAdapter = new GalleryAdapter(getApplicationContext(), uriList);
        rvGallery.setAdapter(galleryAdapter);
        galleryAdapter.notifyDataSetChanged();
        /*for (int i = 0; i < uriList.size(); i++) {
            Log.d(TAG, "onMultiImageSelected: WWW222 " + i + " " + uriList.get(i).toString());


            try {


                Log.d(TAG, "onMultiImageSelected: WWW222FILE " + file.toString());;
                //uploadFile(file);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }*/

        urislists = uriList;

    }

    private void check(int i, int size) {

        if (i == size - 1) {
            alertDialog1.dismiss();
        } else {
            alertDialog1.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showEndDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("REVIEWING ADDS");
        alertDialogBuilder
                .setMessage("Please wait we are checking you add you will shortly recieve mail when your add published")
                .setCancelable(false)
                .setView(R.layout.alert_layout).setPositiveButton("I'll wait", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Paper.book().delete(Prevalent.categoty);
                Paper.book().delete(Prevalent.address);
                Paper.book().delete(Prevalent.adds);
                gotoMain();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void gotoMain() {
        Intent intent = new Intent(ImagesSelectionActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void onProgressUpdate(int percentage) {
    }


}

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context ctx;
    private int pos;
    List<Uri> mArrayUri;

    public GalleryAdapter(Context ctx, List<Uri> mArrayUri) {

        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.image_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        pos = position;


        holder.ivGallery.setImageURI(mArrayUri.get(position));
        holder.dltImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mArrayUri.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayUri.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivGallery;
        ImageView dltImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGallery = (ImageView) itemView.findViewById(R.id.ivGallery);
            dltImg = itemView.findViewById(R.id.dlt_img);
        }
    }
}
