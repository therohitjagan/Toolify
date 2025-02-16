package com.therohitjagan.toolify.alltools.barcodetool.models;

public class HistoryItem {
    private final String data;
    private final String format;
    private final String timestamp;

    public HistoryItem(String data, String format, String timestamp) {
        this.data = data;
        this.format = format;
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public String getFormat() {
        return format;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
