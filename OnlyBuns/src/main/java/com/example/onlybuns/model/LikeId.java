package com.example.onlybuns.model;

import java.io.Serializable; // Obavezno: Kompozitni ključ mora biti serijalizabilan
import java.util.Objects; // Za equals i hashCode metode

// Ova klasa predstavlja kompozitni primarni ključ za Like entitet.
// Ona obaveštava Hibernate kako da jedinstveno identifikuje jedan "lajk" u bazi.
public class LikeId implements Serializable {

    // Važno: Imena ovih polja (post i user) moraju se POKLAPATI
    // sa imenima @Id polja u Like.java entitetu (post i user).
    // Takođe, tipovi (Integer) moraju odgovarati tipovima primarnih ključeva
    // Post i User entiteta.
    private Integer post; // Odnosi se na ID Post entiteta
    private Integer user; // Odnosi se na ID User entiteta

    // Defaultni konstruktor (obavezan za JPA)
    public LikeId() {
    }

    // Konstruktor sa svim poljima (korisno za inicijalizaciju)
    public LikeId(Integer post, Integer user) {
        this.post = post;
        this.user = user;
    }

    // ##### KRITIČNO VAŽNO: Getteri i Setteri #####
    // Ove metode su apsolutno neophodne da bi Hibernate mogao da pristupi vrednostima ključa.
    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    // ##### KRITIČNO VAŽNO: equals() i hashCode() metode #####
    // Ove metode su apsolutno neophodne za ispravan rad sa kompozitnim ključevima.
    // One osiguravaju da Hibernate (i Java kolekcije poput Set-a)
    // ispravno prepoznaju i upoređuju objekte kompozitnog ključa.
    // Uvek ih generišite automatski za sva polja koja čine ključ (post i user).
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeId likeId = (LikeId) o;
        return Objects.equals(post, likeId.post) && Objects.equals(user, likeId.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, user);
    }
}