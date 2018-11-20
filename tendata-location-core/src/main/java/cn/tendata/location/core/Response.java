package cn.tendata.location.core;

import cn.tendata.location.core.jackson.DataView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Response {
    @JsonView(DataView.Basic.class)
    private int status;
    @JsonView(DataView.Basic.class)
    private Object payload;
    @JsonView(DataView.Basic.class)
    private String errorCode;
    @JsonView(DataView.Basic.class)
    private String errorMsg;


    public Response(HttpStatus status, Object payload) {
        this.status = status.value();
        this.payload = payload;
    }

    public Response(HttpStatus status, String errorCode, String errorMsg) {
        this.status = status.value();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public static Response success() {
        return success(null);
    }

    public static Response success(Object payload) {
        return new Response(HttpStatus.OK, payload);
    }

    public static Response fail(HttpStatus status) {
        return new Response(status, null);
    }

    public static Response fail(HttpStatus status, String errorCode, String errorMsg) {
        return new Response(status, errorCode, errorMsg);
    }

}
