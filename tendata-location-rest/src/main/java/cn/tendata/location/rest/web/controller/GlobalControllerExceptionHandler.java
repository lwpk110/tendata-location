package cn.tendata.location.rest.web.controller;

import cn.tendata.location.core.BasicErrorCodeException;
import cn.tendata.location.core.DefaultMessageSource;
import cn.tendata.location.core.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.hibernate.validator.internal.engine.path.PathImpl;
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
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice(basePackageClasses = IpLocationController.class)
@SuppressWarnings("unused")
public class GlobalControllerExceptionHandler implements MessageSourceAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    private MessageSourceAccessor messages = DefaultMessageSource.getAccessor();


    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<?> handleValidationException(Exception ex) {
        BindingResult result = null;
        if (ex instanceof MethodArgumentNotValidException) {
            result = ((MethodArgumentNotValidException) ex).getBindingResult();
        }
        if (ex instanceof BindException) {
            result = ((BindException) ex).getBindingResult();
        }
        List<Error> errors = new ArrayList<>(6);
        HttpStatus status = HttpStatus.BAD_REQUEST;
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

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> handleConstraintViolationException(Exception ex) {
        List<Error> errors = new ArrayList<>(6);
        if(ex instanceof ConstraintViolationException){
            final Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) ex)
                    .getConstraintViolations();
            for (ConstraintViolation<?> constraintViolation : constraintViolations) {
                final String param = ((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().toString();
                final String message = constraintViolation.getMessage();
                final String invalidValue = constraintViolation.getInvalidValue() == null?"":constraintViolation.getInvalidValue().toString();
                Error error = new Error();
                error.field = param;
                error.rejected = invalidValue;
                error.message = messages.getMessage(message,new Object[]{invalidValue}, message);
                errors.add(error);
            }
        }
        HttpStatus status = HttpStatus.BAD_REQUEST;
        final String message = messages.getMessage("error.VALIDATION_ERROR", "Validation failed. Error count: " + errors.size());
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
        String message = messages.getMessage("error." + ex.getErrorCode(), ex.getErrorCode());
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
