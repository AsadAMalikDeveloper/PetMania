package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petmania.R;
import com.example.petmania.adapter.SelectedPetAdapter;
import com.example.petmania.model.SelectedCat;
import com.example.petmania.model.SelectedDog;
import com.example.petmania.model.SelectedPetHelper;
import com.example.petmania.utils.Common;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewFeedsSelectedPetActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView petNameTv,petNameCard;
    private String petName;
    private String petId;
    private String isPet = "";
    private RecyclerView recyclerView;
    private RelativeLayout ratingLayout;
    private SelectedPetAdapter adapter ;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ArrayList<SelectedPetHelper> selectedPetHelpers;
    RatingBar indoorR,lap,adaptability,affection_level,child_friendly,dog_friendly,energy_level,
            grooming,health_issues,intelligence,shedding_level,social_needs,stranger_friendly
            ,vocalisation,experimental,hairless,natural,rare,rex,suppressed_tail,short_legs,hypoallergenic;
    ImageView pet_img;
    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feeds_selected_pet);

        backBtn = findViewById(R.id.back_btn_selected_pet);
        petNameTv = findViewById(R.id.selected_pet_nameTv);
        pet_img = findViewById(R.id.selected_pet_image);
        ratingLayout = findViewById(R.id.ratingRl);
        petNameCard = findViewById(R.id.selected_pet_nameTv_card);
        indoorR = findViewById(R.id.indoorRb);
        lap = findViewById(R.id.lapRb);
        adaptability = findViewById(R.id.adaptabilityRb);
        affection_level = findViewById(R.id.affection_levelRb);
        child_friendly = findViewById(R.id.child_friendlyRb);
        dog_friendly = findViewById(R.id.dog_friendlyRb);
        energy_level = findViewById(R.id.energy_levelRb);
        grooming = findViewById(R.id.groomingRb);
        health_issues = findViewById(R.id.health_issuesRb);
        intelligence = findViewById(R.id.intelligenceRb);
        shedding_level = findViewById(R.id.shedding_levelRb);
        social_needs= findViewById(R.id.social_needsRb);
        stranger_friendly = findViewById(R.id.stranger_friendlyRb);
        vocalisation= findViewById(R.id.vocalisationRb);
        experimental = findViewById(R.id.experimentalRb);
        hairless = findViewById(R.id.hairlessRb);
        rare = findViewById(R.id.rareRb);
        rex = findViewById(R.id.rexRb);
        natural = findViewById(R.id.naturalRb);
        suppressed_tail = findViewById(R.id.suppressed_tailRb);
        short_legs = findViewById(R.id.short_legsRb);
        hypoallergenic = findViewById(R.id.hypoallergenicRb);

        recyclerView = findViewById(R.id.selected_pet_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        isPet = getIntent().getStringExtra("is_pet");
        if (isPet.equals("DOG")){
            ratingLayout.setVisibility(View.GONE);
            int id = getIntent().getIntExtra("pet_id",-1);
            petName = getIntent().getStringExtra("pet_name");

            petNameTv.setText(petName);
            petNameCard.setText(petName);

            showDog(id);
        }else if (isPet.equals("CAT")){
            ratingLayout.setVisibility(View.VISIBLE);
            petId = getIntent().getStringExtra("pet_id");
            petName = getIntent().getStringExtra("pet_name");

            petNameTv.setText(petName);
            petNameCard.setText(petName);

            showCat(petId);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showCat(String id) {
        Common.getAPI().getCat(id)
                .enqueue(new Callback<List<SelectedCat>>() {
                    @Override
                    public void onResponse(Call<List<SelectedCat>> call, Response<List<SelectedCat>> response) {
                        List<SelectedCat> selectedPetList = response.body();
                        for (SelectedCat selectedCats:selectedPetList){

                            Glide.with(NewFeedsSelectedPetActivity.this).load(selectedCats.getUrl()).placeholder(R.drawable.img_default).into(pet_img);
                            for (SelectedCat.Breeds breeds: selectedCats.breeds){
                                GradientDrawable gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeff200,0xffaff600});
                                GradientDrawable gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeff400,0xffaff600});
                                GradientDrawable gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeff600,0xffaff600});
                                GradientDrawable gradient4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeff800,0xffaff600});
                                GradientDrawable gradient5 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeaa400,0xffaff600});
                                selectedPetHelpers = new ArrayList<>();

                                selectedPetHelpers.add(new SelectedPetHelper("Description",breeds.getDescription(),gradient1));
                                selectedPetHelpers.add(new SelectedPetHelper("life_span",breeds.getLife_span(),gradient2));
                                selectedPetHelpers.add(new SelectedPetHelper("Temperament",breeds.getTemperament(),gradient2));
                                selectedPetHelpers.add(new SelectedPetHelper("Origin",breeds.getOrigin(),gradient3));
                                selectedPetHelpers.add(new SelectedPetHelper("Country Code",breeds.getCountry_code(),gradient4));
                                selectedPetHelpers.add(new SelectedPetHelper("Alternative names",breeds.getAlt_names(),gradient5));
                                selectedPetHelpers.add(new SelectedPetHelper("CFA Url",breeds.getCfa_url(),gradient1));
                                selectedPetHelpers.add(new SelectedPetHelper("Vetstreet Url",breeds.getVetstreet_url(),gradient2));
                                selectedPetHelpers.add(new SelectedPetHelper("Vcahospitals Url",breeds.getVcahospitals_url(),gradient3));
                                selectedPetHelpers.add(new SelectedPetHelper("Wikipedia Url",breeds.getWikipedia_url(),gradient4));

                                indoorR.setRating(breeds.getIndoor());
                                lap.setRating(breeds.getLap());
                                adaptability.setRating(breeds.getAdaptability());
                                affection_level.setRating(breeds.getAffection_level());
                                child_friendly.setRating(breeds.getChild_friendly());
                                dog_friendly.setRating(breeds.getDog_friendly());
                                energy_level.setRating(breeds.getEnergy_level());
                                grooming.setRating(breeds.getGrooming());
                                health_issues.setRating(breeds.getHealth_issues());
                                intelligence.setRating(breeds.getIntelligence());
                                shedding_level.setRating(breeds.getShedding_level());
                                social_needs.setRating(breeds.getSocial_needs());
                                stranger_friendly.setRating(breeds.getStranger_friendly());
                                vocalisation.setRating(breeds.getVocalisation());
                                experimental.setRating(breeds.getExperimental());
                                hairless.setRating(breeds.getHairless());
                                natural.setRating(breeds.getNatural());
                                rare.setRating(breeds.getRare());
                                rex.setRating(breeds.getRex());
                                suppressed_tail.setRating(breeds.getSuppressed_tail());
                                short_legs.setRating(breeds.getShort_legs());
                                hypoallergenic.setRating(breeds.getHypoallergenic());


                            }
                        }
                        adapter = new SelectedPetAdapter(NewFeedsSelectedPetActivity.this,selectedPetHelpers);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onFailure(Call<List<SelectedCat>> call, Throwable t) {
                        Toast.makeText(NewFeedsSelectedPetActivity.this, "T "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDog(int id) {

        Common.getAPI().getDog(id)
                .enqueue(new Callback<List<SelectedDog>>() {
                    @Override
                    public void onResponse(Call<List<SelectedDog>> call, Response<List<SelectedDog>> response) {
                        List<SelectedDog> selectedPetList = response.body();
                        for (SelectedDog selectedPets:selectedPetList){

                            Glide.with(NewFeedsSelectedPetActivity.this).load(selectedPets.getUrl()).placeholder(R.drawable.img_default).into(pet_img);
                            for (SelectedDog.Breeds breeds: selectedPets.breeds){
                                GradientDrawable gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeff200,0xffaff600});
                                GradientDrawable gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeff400,0xffaff600});
                                GradientDrawable gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeff600,0xffaff600});
                                GradientDrawable gradient4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeff800,0xffaff600});
                                GradientDrawable gradient5 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeaa400,0xffaff600});
                                selectedPetHelpers = new ArrayList<>();
                                selectedPetHelpers.add(new SelectedPetHelper("Bred For",breeds.getBred_for(),gradient1));
                                selectedPetHelpers.add(new SelectedPetHelper("Breed Group",breeds.getBreed_group(),gradient2));
                                selectedPetHelpers.add(new SelectedPetHelper("Life Span",breeds.getLife_span(),gradient3));
                                selectedPetHelpers.add(new SelectedPetHelper("Temperament",breeds.getTemperament(),gradient4));
                                selectedPetHelpers.add(new SelectedPetHelper("Origin",breeds.getOrigin(),gradient5));
                                selectedPetHelpers.add(new SelectedPetHelper("Weight",breeds.getWeight().getImperial(),gradient4));
                                selectedPetHelpers.add(new SelectedPetHelper("Height",breeds.getHeight().getImperial(),gradient5));
                            }
                        }
                        adapter = new SelectedPetAdapter(NewFeedsSelectedPetActivity.this,selectedPetHelpers);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onFailure(Call<List<SelectedDog>> call, Throwable t) {
                        Toast.makeText(NewFeedsSelectedPetActivity.this, "T "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}