package com.example.onlybuns.dto;

public class UserActivityBreakdownDTO {
    private Double percentWithPosts;
    private Double percentWithOnlyComments;
    private Double percentInactive;

    // getteri i setteri
    public Double getPercentWithPosts() { return percentWithPosts; }
    public void setPercentWithPosts(Double percentWithPosts) { this.percentWithPosts = percentWithPosts; }

    public Double getPercentWithOnlyComments() { return percentWithOnlyComments; }
    public void setPercentWithOnlyComments(Double percentWithOnlyComments) { this.percentWithOnlyComments = percentWithOnlyComments; }

    public Double getPercentInactive() { return percentInactive; }
    public void setPercentInactive(Double percentInactive) { this.percentInactive = percentInactive; }
}