package com.dyaco.spiritbike.support.event;

public class CommandEvent<T> {

    public CommandEvent(int eventType, T data) {
        this.eventType = eventType;
        this.data = data;
    }

    public T data;
    private int eventType;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

}
