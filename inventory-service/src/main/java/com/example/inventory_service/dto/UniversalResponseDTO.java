package com.example.inventory_service.dto;

public class UniversalResponseDTO<T> {

    private String status;
    private String message;
    private T data;

    public UniversalResponseDTO() {
    }

    public UniversalResponseDTO(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // ---------- Getters & Setters ----------
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // ---------- STATIC BUILDER ----------
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private String status;
        private String message;
        private T data;

        public Builder<T> status(String status) {
            this.status = status;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public UniversalResponseDTO<T> build() {
            return new UniversalResponseDTO<>(status, message, data);
        }
    }
}
