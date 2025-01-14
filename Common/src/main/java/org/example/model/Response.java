package org.example.model;

public class Response<T> {
    private final String status;
    private final T data;


    public Response(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }



    @Override
    public String toString() {
        return "Response{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}

