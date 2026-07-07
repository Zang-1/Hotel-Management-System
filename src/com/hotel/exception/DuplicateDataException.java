package com.hotel.exception;

/**
 * Custom Checked Exception (Theo bài giảng Chương 3 - Ngoại lệ do người lập trình định nghĩa)
 * Bắt buộc phải xử lý (try-catch) khi ném ra lỗi này.
 */
public class DuplicateDataException extends Exception {
    public DuplicateDataException(String message) {
        super(message);
    }
}
