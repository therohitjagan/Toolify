package com.therohitjagan.toolify;

public class Tool {
    private final String name;
    private final int imageResource;
    private final String category;
    private final Class<?> activityClass;

    public Tool(String name, int imageResource, String category, Class<?> activityClass) {
        this.name = name;
        this.imageResource = imageResource;
        this.category = category;
        this.activityClass = activityClass;
    }

    // Getters
    public String getName() { return name; }
    public int getImageResource() { return imageResource; }
    public String getCategory() { return category; }
    public Class<?> getActivityClass() { return activityClass; }
}