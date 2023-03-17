package com.wsti.expensemanager.data;

public class Result<T> {
    private Result() {
    }

    @Override
    public String toString() {
        if (this instanceof Success) {
            Success<T> success = (Success<T>) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Error) {
            Error error = (Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }

        return "";
    }

    public final static class Success<T> extends Result<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    public final static class Error extends Result {
        private final Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}