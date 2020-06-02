package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.fragments.AddsFragment;
import com.example.petmania.fragments.HomeFragment;
import com.example.petmania.model.Addresses;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Category;
import com.example.petmania.model.CheckUserResponse;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DescriptionActivity extends AppCompatActivity {

    private TextInputEditText title, discription, price;
    int pricetxt;
    private Button nextBtn;
    IPetManiaAPI mServices;
    private String titletxt, detailtxt;
    private int cat_id, add_id;
    private String ad_City ="";
    private Switch showMyNum;
    int shownum =0;
    private TextView showMyNumTxt;

    @Override
    protected void onStart() {
        super.onStart();
        Adds adds = Paper.book().read(Prevalent.adds, null);
        if (adds != null) {
            gotoImagesActivity();
        }
        checkIsEdit();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Paper.book().delete(Prevalent.address);
        Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        mServices = Common.getAPI();
        showMyNumTxt = findViewById(R.id.show_my_number_to_ad);
        //cat_id = getIntent().getIntExtra("category_id",0);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.show_my_number_to_ad), Common.currentUser.getPhone());
        showMyNumTxt.setText(text);
        title = findViewById(R.id.title_et);
        discription = findViewById(R.id.details_et);
        price = findViewById(R.id.price_et);
        nextBtn = findViewById(R.id.next_btn);
        showMyNum = findViewById(R.id.switch1);
        showMyNum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    shownum = 1;
                }else{
                    shownum =0;
                }
            }
        });

    }

    private void checkIsEdit() {
        String isEdit = Paper.book().read(Prevalent.isEdit,null);
        if (isEdit.equals("true") && isEdit!=null){
            Adds adds = Paper.book().read(Prevalent.editAd,null);
            if (adds!=null){
                String editTitle = adds.getAdd_title();
                String editDesd = adds.getAdd_detail();
                String editPrice = adds.getAdd_price();
                title.setText(editTitle);
                discription.setText(editDesd);
                price.setText(editPrice);
                int editShowNo = adds.getShow_no();
                if (editShowNo == 1){
                    showMyNum.setChecked(true);
                }else {
                    showMyNum.setChecked(false);
                }
            }
        }
    }

    private boolean validTitle() {

        titletxt = title.getText().toString().trim();
        if (titletxt.isEmpty()) {
            title.setError("Field can't be empty");
            return false;
        } else if (titletxt.length() > 15) {
            title.setError("Title too long");
            return false;
        } else {
            title.setError(null);
            return true;
        }
    }

    private boolean validDetails() {

        detailtxt = discription.getText().toString().trim();
        if (detailtxt.isEmpty()) {
            discription.setError("Field can't be empty");
            return false;
        } else if (detailtxt.length() > 5000) {
            discription.setError("Details too long");
            return false;
        } else {
            discription.setError(null);
            return true;
        }
    }

    private boolean validPrice() {

        pricetxt = Integer.parseInt(price.getText().toString());
        if (price.getText().toString().trim().isEmpty()) {
            price.setError("Field can't be empty");
            return false;
        } else if (pricetxt < 300) {
            price.setError("Min Rs. 300");
            return false;
        } else {
            price.setError(null);
            return true;
        }
    }

    public void checkandNext(View view) {
        String isEdit = Paper.book().read(Prevalent.isEdit,null);
        if (isEdit.equals("true") && isEdit!=null){
            Adds adds = Paper.book().read(Prevalent.editAd,null);
            if (adds!=null){
                if (!validTitle() | !validDetails() | !validPrice()) {
                    return;
                }

                AlertDialog alertDialog = new SpotsDialog.Builder()
                        .setContext(this)
                        .setTheme(R.style.Custom)
                        .build();
                alertDialog.show();

                if (Common.currentCategory==null) {
                    Category category = Paper.book().read(Prevalent.categoty, null);
                    cat_id = category.getCategory_id();
                } else {
                    cat_id = Common.currentCategory.getCategory_id();
                }

                if (Common.currentAddres == null) {
                    Addresses addresses = Paper.book().read(Prevalent.address, null);
                    add_id = addresses.getAddress_id();
                    ad_City = addresses.getCity();
                } else {
                    add_id = Common.currentAddres.getAddress_id();
                    ad_City =Common.currentAddres.getCity();
                }

                mServices.updateAd(adds.getUser_id(),cat_id,titletxt,detailtxt,String.valueOf(pricetxt),
                        adds.getAdd_on(),adds.getExpire_on(),adds.getPublished(),
                        adds.getViews(),shownum,add_id,ad_City,adds.getAdds_id()).enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                        CheckUserResponse response1 = response.body();
                        if (response1.isExists()){
                            alertDialog.dismiss();
                            Toast.makeText(DescriptionActivity.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                            askForpic();
                        }else {
                            Toast.makeText(DescriptionActivity.this, "ERROR "+response1.getError_msg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                        alertDialog.dismiss();
                        Toast.makeText(DescriptionActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else {
            if (!validTitle() | !validDetails() | !validPrice()) {
                return;
            }

            AlertDialog alertDialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setTheme(R.style.Custom)
                    .build();
            alertDialog.show();


            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String date = df.format(calendar.getTime());

            calendar.add(Calendar.MONTH, 1);
            SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
            String expire = df1.format(calendar.getTime());
            if (Common.currentCategory == null) {
                Category category = Paper.book().read(Prevalent.categoty, null);
                cat_id = category.getCategory_id();
            } else {
                cat_id = Common.currentCategory.getCategory_id();
            }

            if (Common.currentAddres == null) {
                Addresses addresses = Paper.book().read(Prevalent.address, null);
                add_id = addresses.getAddress_id();
                ad_City = addresses.getCity();
            } else {
                add_id = Common.currentAddres.getAddress_id();
                ad_City = Common.currentAddres.getCity();
            }

            Log.d("DescriptionActivity", "checkandNext: WWW@@@ " + Common.currentUser.getUser_id() +
                    " catid " + cat_id + " title" + titletxt + " detail " + detailtxt + " price " + pricetxt + " time " + date + " expire " + expire
                    + " email " + Common.currentUser.getEmail() + " addid " + add_id);

            mServices.uploadAdd(Common.currentUser.getUser_id(),
                    cat_id, titletxt, detailtxt,
                    String.valueOf(pricetxt), date, expire,
                    Common.currentUser.getEmail(), shownum,
                    add_id,ad_City)
                    .enqueue(new Callback<Adds>() {
                        @Override
                        public void onResponse(Call<Adds> call, Response<Adds> response) {
                            alertDialog.dismiss();
                            Adds adds = response.body();
                            Paper.book().write(Prevalent.adds, adds);
                            if (TextUtils.isEmpty(adds.getError_msg())) {
                                Common.lastUploadedAdd = adds;
                                Toast.makeText(DescriptionActivity.this, "added ", Toast.LENGTH_SHORT).show();
                                gotoImagesActivity();
                            } else {
                                Toast.makeText(DescriptionActivity.this, "Error Publishing" + adds.getError_msg(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Adds> call, Throwable t) {
                            alertDialog.dismiss();
                            Toast.makeText(DescriptionActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void askForpic() {
        AlertDialog alertDialog = new AlertDialog.Builder(DescriptionActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Change Pictures ?")
                .setMessage("you want keep previous pictures ? or  You want to update pictures ?")
                .setPositiveButton("Keep Previous", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        Paper.book().delete(Prevalent.adds);
                        Paper.book().delete(Prevalent.isEdit);
                        Paper.book().delete(Prevalent.editAd);
                        Paper.book().delete(Prevalent.categoty);
                        Paper.book().delete(Prevalent.address);
                        gotoHome();
                    }
                }).setNegativeButton("Change Pictures", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        gotoImagesActivity();
                    }
                }).show();
    }

    private void gotoHome() {
        Intent intent = new Intent(DescriptionActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoImagesActivity() {
        Intent addressIntent = new Intent(DescriptionActivity.this, ImagesSelectionActivity.class);
        startActivity(addressIntent);
    }
}
