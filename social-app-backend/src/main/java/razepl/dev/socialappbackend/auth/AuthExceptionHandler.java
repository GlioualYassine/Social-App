package razepl.dev.socialappbackend.auth;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import razepl.dev.socialappbackend.auth.apicalls.ExceptionResponse;
import razepl.dev.socialappbackend.auth.interfaces.AuthExceptionInterface;
import razepl.dev.socialappbackend.exceptions.AuthManagerInstanceException;
import razepl.dev.socialappbackend.exceptions.InvalidTokenException;
import razepl.dev.socialappbackend.exceptions.PasswordValidationException;
import razepl.dev.socialappbackend.exceptions.TokenDoesNotExistException;

import java.util.stream.Collectors;

import static razepl.dev.socialappbackend.auth.constants.AuthMessages.ERROR_DELIMITER;
import static razepl.dev.socialappbackend.auth.constants.AuthMessages.ERROR_FORMAT;

@Slf4j
@ControllerAdvice
public class AuthExceptionHandler implements AuthExceptionInterface {
    @Override
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ExceptionResponse> handleConstraintValidationExceptions(ConstraintViolationException exception) {
        String className = exception.getClass().getName();
        String errorMessage = exception.getConstraintViolations()
                .stream()
                .map(error -> String.format(ERROR_FORMAT, error.getPropertyPath(), error.getMessage()))
                .collect(Collectors.joining(ERROR_DELIMITER));

        log.error(errorMessage);

        return new ResponseEntity<>(buildResponse(errorMessage, className), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ExceptionResponse> handleMethodArgValidExceptions(MethodArgumentNotValidException exception) {
        String className = exception.getClass().getName();
        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format(ERROR_FORMAT, error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(ERROR_DELIMITER));

        log.error(errorMessage);

        return new ResponseEntity<>(buildResponse(errorMessage, className), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    @ExceptionHandler(PasswordValidationException.class)
    public final ResponseEntity<ExceptionResponse> handlePasswordValidationException(PasswordValidationException exception) {
        String errorMessage = exception.getMessage();
        String className = exception.getClass().getName();

        log.error(errorMessage);

        return new ResponseEntity<>(buildResponse(errorMessage, className), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleUserNotFoundException(UsernameNotFoundException exception) {
        String errorMessage = exception.getMessage();
        String className = exception.getClass().getName();

        log.error(errorMessage);

        return new ResponseEntity<>(buildResponse(errorMessage, className), HttpStatus.NOT_FOUND);
    }

    @Override
    @ExceptionHandler(AuthManagerInstanceException.class)
    public final ResponseEntity<ExceptionResponse> handleAuthManagerInstanceException(AuthManagerInstanceException exception) {
        String errorMessage = exception.getMessage();
        String className = exception.getClass().getName();

        log.error(errorMessage);

        return new ResponseEntity<>(buildResponse(errorMessage, className), HttpStatus.FAILED_DEPENDENCY);
    }

    @Override
    @ExceptionHandler({InvalidTokenException.class, TokenDoesNotExistException.class})
    public ResponseEntity<ExceptionResponse> handleTokenExceptions(IllegalArgumentException exception) {
        String errorMessage = exception.getMessage();
        String className = exception.getClass().getName();

        log.error(errorMessage);

        return new ResponseEntity<>(buildResponse(errorMessage, className), HttpStatus.UNAUTHORIZED);
    }

    private ExceptionResponse buildResponse(String errorMessage, String exceptionClassName) {
        return ExceptionResponse.builder()
                .errorMessage(errorMessage)
                .exceptionClassName(exceptionClassName)
                .build();
    }
}
