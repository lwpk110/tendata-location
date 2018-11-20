package cn.tendata.location.rest.web.controller;

import cn.tendata.location.core.BasicErrorCodeException;
import cn.tendata.location.core.DefaultMessageSource;
import cn.tendata.location.core.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice(basePackageClasses = IpLocationController.class)
public class GlobalControllerExceptionHandler implements MessageSourceAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    private MessageSourceAccessor messages = DefaultMessageSource.getAccessor();

    @ExceptionHandler({BasicErrorCodeException.class})
    public ResponseEntity<?> handleUploadException(BasicErrorCodeException ex) throws IOException {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        final String errMessage = messages.getMessage("error." + ex.getErrorCode(), ex.getMessage());
        Response response = Response.fail(status,status.getReasonPhrase(),errMessage );
        return new ResponseEntity<>(response,status);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<?> handleValidationException(Exception ex) {
        BindingResult result = null;
        if (ex instanceof MethodArgumentNotValidException) {
            result = ((MethodArgumentNotValidException) ex).getBindingResult();
        }
        if (ex instanceof BindException) {
            result = ((BindException) ex).getBindingResult();
        }
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<Error> errors = new ArrayList<>(6);
        for (ObjectError err : result.getGlobalErrors()) {
            Error error = new Error();
            error.message = messages.getMessage(err.getCode(), err.getDefaultMessage());
            errors.add(error);
        }
        for (FieldError err : result.getFieldErrors()) {
            Error error = new Error();
            error.field = err.getField();
            error.rejected = err.getRejectedValue();
            error.message = messages.getMessage(err.getCode(), err.getDefaultMessage());
            errors.add(error);
        }
        final String message = messages.getMessage("error.VALIDATION_ERROR", "Validation failed for object='"
                + result.getObjectName() + "'. Error count: " + result.getErrorCount());
        Response response = Response.fail(status,status.getReasonPhrase(),message);
        response.setPayload(errors);
        return new ResponseEntity<>(response, status);
    }

    @JsonInclude(Include.NON_EMPTY)
    static class Error {
        public String field;
        public Object rejected;
        public String message;
    }

    @ExceptionHandler(BasicErrorCodeException.class)
    public ResponseEntity<?> handleErrorCodeException(BasicErrorCodeException ex, HttpServletRequest request) throws IOException {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        logException(ex, request, status);
        String message = messages.getMessage("error." + ex.getErrorCode(), ex.getMessage());
        Response response = Response.fail(status,ex.getErrorCode(),message);
        return new ResponseEntity<>(response,status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUncaughtException(Exception ex, HttpServletRequest request) throws IOException {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        logException(ex, request, status);
        String message = messages.getMessage("error.INTERNAL_SERVER_ERROR", "Server error");
        Response response = Response.fail(status,status.getReasonPhrase(),message);
        return new ResponseEntity<>(response,status);
    }

    @ExceptionHandler({SocketTimeoutException.class,ResourceAccessException.class})
    public ResponseEntity<?> handleTimeoutException(Exception ex, HttpServletRequest request) throws IOException {
        HttpStatus status = HttpStatus.REQUEST_TIMEOUT;
        logException(ex, request, status);
        final Response response = Response.fail(status);
        return new ResponseEntity<>(response,HttpStatus.REQUEST_TIMEOUT);
    }

    private void logException(Exception ex, HttpServletRequest request, HttpStatus status) {
        if (LOGGER.isErrorEnabled() && status.value() >= 500 || LOGGER.isInfoEnabled()) {
            Marker marker = MarkerFactory.getMarker(ex.getClass().getName());
            String uri = request.getRequestURI();
            if (request.getQueryString() != null) {
                uri += '?' + request.getQueryString();
            }
            String msg = String.format("%s %s ~> %s", request.getMethod(), uri, status);
            if (status.value() >= 500) {
                LOGGER.error(marker, msg, ex);
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(marker, msg, ex);
            } else {
                LOGGER.info(marker, msg);
            }
        }
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
