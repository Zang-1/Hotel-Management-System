package com.hotel.exception;

/**
 * Custom Unchecked Exception (Theo bài giảng Chương 3)
 * Kế thừa RuntimeException, dùng để kiểm tra lỗi logic nghiệp vụ.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
