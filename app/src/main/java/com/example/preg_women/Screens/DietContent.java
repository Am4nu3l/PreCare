package com.example.preg_women.Screens;

public class DietContent {
    private String title;
    private String link;
    private String description;

    public DietContent(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }
}
