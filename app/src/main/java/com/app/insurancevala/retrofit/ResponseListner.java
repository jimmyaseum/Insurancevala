package com.app.insurancevala.retrofit;

public interface ResponseListner<T> {

    void onResponse(ApiResponse<T> it);
}
