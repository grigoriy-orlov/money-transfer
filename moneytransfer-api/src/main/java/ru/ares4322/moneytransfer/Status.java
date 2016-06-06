package ru.ares4322.moneytransfer;

public enum Status {

    CREATED(0),
    PROCESSED(1),
    FINISHED_OK(2),
    FINISHED_ERROR(3);

    public final int status;

    Status(int status) {
        this.status = status;
    }
}
