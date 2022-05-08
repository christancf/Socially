package com.example.socially.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAO3l7h7U:APA91bHU6Tzm5_DoiVg7RfmPtFaZHv3WdZKLSM9vN51_CHXNsggMgchFBCPID3H3PFusNlt-71XokyoU_ReQzN4uGhr6_XNKzCvOl-O6tnMFp9dWTpqAT2AzrJMYm3b1IeS0Gjq8DIWP"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
