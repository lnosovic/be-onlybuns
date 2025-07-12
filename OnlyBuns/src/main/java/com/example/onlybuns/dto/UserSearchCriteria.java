package com.example.onlybuns.dto;

public class UserSearchCriteria {
    private String username;
    private String name;
    private String surname;
    private String email;
    private String address;

    private Integer minPostCount;
    private Integer maxPostCount;

    private String sortBy;  // npr. "followingCount" ili "email"
    private String sortDirection; // "asc" ili "desc"

    private Integer page = 0;  // podrazumevano prva stranica
    private Integer size = 5;  // podrazumevano 5 korisnika po strani

    // Getteri i setteri

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getMinPostCount() { return minPostCount; }
    public void setMinPostCount(Integer minPostCount) { this.minPostCount = minPostCount; }

    public Integer getMaxPostCount() { return maxPostCount; }
    public void setMaxPostCount(Integer maxPostCount) { this.maxPostCount = maxPostCount; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}