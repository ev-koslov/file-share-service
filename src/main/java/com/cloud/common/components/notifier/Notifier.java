package com.cloud.common.components.notifier;


public interface Notifier<T extends NotifierPayload> {
    void notify(String destination, T payload);
}
