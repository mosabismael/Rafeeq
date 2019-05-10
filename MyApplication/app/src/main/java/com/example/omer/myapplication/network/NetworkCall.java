package com.example.omer.myapplication.network;


import com.example.omer.myapplication.Comments;
import com.example.omer.myapplication.Normal;
import com.example.omer.myapplication.Posts;
import com.example.omer.myapplication.Save;
import com.example.omer.myapplication.model.UploadObject;

import okhttp3.RequestBody;
import retrofit2.Response;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.Call;
import rx.Observable;
/**
 * Created by Shadik on 1/28/2017.
 */

public interface NetworkCall {
//

    @POST("comments")
    Observable<UploadObject> createComment(@Body Comments comment );
    @GET("count")
    Observable<Comments> countComment();

    @POST("authenticate")
    Observable<UploadObject> login();
    @POST("normals")
    Observable<UploadObject> createNormals(@Body Normal normal);
    @GET("normals/{email}")
    Observable<Normal> getProfile(@Path("email") String email);
    @POST("saves")
    Observable<UploadObject> savePost(@Body Save post );

//    @Multipart
    @POST("posts")
    Observable<UploadObject> createPost(@Body Posts post );
    @PUT("posts/{id}")
    Observable<UploadObject> changePost(@Path("id") int id, @Body Posts post);
    @DELETE("posts/{id}")
    Observable<UploadObject> deletePost(@Path("id") String id);
    @POST("fileuploads")
    Call<ResponseBody> uploadMultiFile(@Body RequestBody file);


}
