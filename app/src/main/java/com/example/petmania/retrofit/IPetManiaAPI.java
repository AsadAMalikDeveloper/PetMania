package com.example.petmania.retrofit;


import com.example.petmania.classes.ServerResponse;
import com.example.petmania.model.Addresses;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Adds_images;
import com.example.petmania.model.Branches;
import com.example.petmania.model.Category;
import com.example.petmania.model.CheckUserResponse;
import com.example.petmania.model.Clinic;
import com.example.petmania.model.Doctors;
import com.example.petmania.model.Review;
import com.example.petmania.model.User;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface IPetManiaAPI {
    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkUserExists(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("register.php")
    Call<User> registerNewUser(
                                @Field("email") String email,
                                @Field("password") String password,
                                @Field("name") String name,
                                @Field("phone") String phone,
                                @Field("about") String about,
                                @Field("date_of_join") String doj);

    @FormUrlEncoded
    @POST("updateLocation.php")
    Call<Addresses> updateLocation(
            @Field("user_id") int user_id,
            @Field("country") String country,
            @Field("state") String state,
            @Field("city") String city,
            @Field("postal_code") String postal_code,
            @Field("address") String address);
    @FormUrlEncoded
    @POST("addReview.php")
    Call<Review> addReview(
            @Field("user_id") int user_id,
            @Field("dr_id") int dr_id,
            @Field("review") String review,
            @Field("rating") String rating,
            @Field("timestamp") String timestamp);

    @FormUrlEncoded
    @POST("updateReview.php")
    Call<Review> updateReview(
            @Field("user_id") int user_id,
            @Field("dr_id") int dr_id,
            @Field("rating") String rating,
            @Field("review") String review,
            @Field("timestamp") String timestamp);

    @FormUrlEncoded
    @POST("upload_add.php")
    Call<Adds> uploadAdd(
            @Field("user_id") int user_id,
            @Field("category_id") int category_id,
            @Field("add_title") String add_title,
            @Field("add_detail") String add_detail,
            @Field("add_price") String add_price,
            @Field("add_on") String add_on,
            @Field("expire_on")String expire_on,
            @Field("email") String email,
            @Field("show_no") int show_no,
            @Field("address_id") int address_id,
            @Field("ad_city") String ad_city);

    @FormUrlEncoded
    @POST("updateAd.php")
    Call<CheckUserResponse> updateAd(@Field("user_id") int user_id,
                                     @Field("category_id") int category_id,
                                     @Field("add_title") String add_title,
                                     @Field("add_detail") String add_detail,
                                     @Field("add_price") String add_price,
                                     @Field("add_on") String add_on,
                                     @Field("expire_on")String expire_on,
                                     @Field("published")int published,
                                     @Field("views") int views,
                                     @Field("show_no") int show_no,
                                     @Field("address_id") int address_id,
                                     @Field("ad_city") String ad_city,
                                     @Field("ad_id") int ad_id);

    //https://petsmaniapk.000webhostapp.com/images/uploads/


    @FormUrlEncoded
    @POST("rp.php")
    Call<CheckUserResponse> forgetPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("signInWithEmail.php")
    Call<User> signinInWithEmail(@Field("email") String email);

    @FormUrlEncoded
    @POST("signInWithEmailDr.php")
    Call<Doctors> signinInWithEmailDr(@Field("email") String email);

    @FormUrlEncoded
    @POST("getUserByUserId.php")
    Call<User> getUserByUserId(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("getReviewByUserId.php")
    Call<Review> getReviewByUserId(@Field("user_id") int user_id,
                                   @Field("dr_id") int dr_id);

    @FormUrlEncoded
    @POST("getAllReviewDrCheck.php")
    Call<CheckUserResponse> checkDrReview(@Field("dr_id") int dr_id);
    @FormUrlEncoded
    @POST("getAllReviewDr.php")
    Observable<ArrayList<Review>> getAllReviewDr(@Field("dr_id") int dr_id);

    @Multipart
    @POST("upload1.php")
    Call<String> uploadFile(@Part MultipartBody.Part phone,
                            @Part MultipartBody.Part file,
                            @Part MultipartBody.Part adds_id,
                            @Part MultipartBody.Part uploaded_on);
    @Multipart
    @POST("updateAdImages.php")
    Call<String> updateFile(@Part MultipartBody.Part phone,
                            @Part MultipartBody.Part adds_id,
                            @Part MultipartBody.Part uploaded_on,
                            @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("updateInfo.php")
    Call<User> updateInfo(@Field("email") String email,
                          @Field("phone") String phone);

    @FormUrlEncoded
    @POST("updateVkey.php")
    Call<User> updateVkey(@Field("email") String email,
                          @Field("value") int value);


    @GET("getCategories.php")
    Observable<ArrayList<Category>> getCategories();

    @GET("getAllClinic.php")
    Observable<List<Clinic>> getAllClinics();

    @FormUrlEncoded
    @POST("getImages.php")
    Observable<ArrayList<Adds_images>> getImages(@Field("add_id") int add_id);

    @FormUrlEncoded
    @POST("loadAddress.php")
    Observable<List<Addresses>> getAddresses(@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("loadAddressWithAddId.php")
    Observable<List<Addresses>> loadAddressWithAddId(@Field("address_id") int address_id);

    @FormUrlEncoded
    @POST("deletead.php")
    Call<CheckUserResponse> deleteAd(@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("loadAllAds.php")
    Observable<List<Adds>> getAllAds(@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("getAllBranches.php")
    Observable<ArrayList<Branches>> getAllBranches(@Field("id") int id);
    @FormUrlEncoded
    @POST("getAllDoctors.php")
    Observable<ArrayList<Doctors>> getAllDoctors(@Field("id") int id);
    @FormUrlEncoded
    @POST("getAllDoctorsById.php")
    Observable<ArrayList<Doctors>> getAllDoctorsById(@Field("id") int id);


    @GET("getAllAds.php")
    Observable<List<Adds>> loadAds();

    @FormUrlEncoded
    @POST("checkAllAds.php")
    Call<CheckUserResponse> checkAllAds(@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("loadAdds.php")
    Observable<List<Adds>> getAdds(@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("loadAdById.php")
    Call<Adds> getAdsByAdId(@Field("ad_id") int ad_id);

    @Multipart
    @POST("images/check.php")
    Call<ServerResponse> upload(
            @Header("Authorization") String authorization,
            @PartMap Map<String, RequestBody> map
    );

    @FormUrlEncoded
    @POST("checkAddress.php")
    Call<CheckUserResponse> checkExistAddress(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("checkAdds.php")
    Call<CheckUserResponse> checkExistAdds(@Field("user_id") String user_id);

}
