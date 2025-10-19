package kit.corp.handler;

import kit.corp.controller.api.ApiResponse;
import kit.corp.handler.exception.MarketIsNotSupportException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(errorMessage + "\n, name exception: " + ex.getClass().getName(), false));
    }

    @ExceptionHandler(MarketIsNotSupportException.class)
    public ResponseEntity<ApiResponse> handlerMarketIsNotSupportException(MarketIsNotSupportException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(ex.getMessage() + "\n, name exception: " + ex.getClass().getName(), false));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(
                        "Внутренняя ошибка сервера, описание: " + ex.getMessage() + "\n, name exception: " + ex.getClass().getName(),
                        false));
    }
}
