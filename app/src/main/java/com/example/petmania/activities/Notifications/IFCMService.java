package com.example.petmania.activities.Notifications;


import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAF3X22co:APA91bEyxIiZF3K1AsmFPm3XeQAYIX81rT1V62izzk9_Xrf-jiHwT4vSyeYcC8XiJhIhwZroTW8GtT9snN8SmRlUbZ1j4T1Rpugg3mXFAyzS9U4GolTwkNHd1BJdvgvBAKD4qBx7BWId"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
