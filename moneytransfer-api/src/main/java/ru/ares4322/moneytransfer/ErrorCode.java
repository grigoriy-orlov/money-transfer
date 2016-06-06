package ru.ares4322.moneytransfer;

public enum ErrorCode {

    OK(0),
    SAME_ACCOUNT(1),
    CONTENTION_ERROR(2),
    NOT_ENOUGH_MONEY(3),
    ACCOUNT_NOT_EXISTS(4),
    WRONG_AMOUNT(5);

    public final int code;

    ErrorCode(int code) {
        this.code = code;
    }
}
