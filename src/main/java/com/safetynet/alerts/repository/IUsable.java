package com.safetynet.alerts.repository;

public interface IUsable<T> {
    boolean isNotValid(T object);
}
