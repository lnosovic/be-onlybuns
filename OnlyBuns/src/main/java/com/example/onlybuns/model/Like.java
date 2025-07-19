package com.example.onlybuns.model;

import javax.persistence.*; // Potrebni JPA importi
import java.io.Serializable; // Obavezno za klase sa kompozitnim ključem
import java.time.LocalDateTime;
import java.util.Objects; // Za equals i hashCode metode

@Entity // Označava da je ovo JPA entitet
@Table(name = "post_user_likes") // Ime tabele u tvojoj bazi podataka
@IdClass(LikeId.class) // Obavezno: Ovde povezujemo Like entitet sa klasom kompozitnog ključa
public class Like implements Serializable { // Entitet mora implementirati Serializable

    @Id // Označava da je ovo polje deo primarnog ključa
    @ManyToOne(fetch = FetchType.LAZY) // Relacija: Mnogi lajkovi pripadaju jednom postu
    @JoinColumn(name = "post_id", referencedColumnName = "id") // Kolona u 'post_user_likes' tabeli koja se odnosi na ID posta
    private Post post; // Referenca na Post entitet

    @Id // Označava da je ovo polje takođe deo primarnog ključa
    @ManyToOne(fetch = FetchType.LAZY) // Relacija: Mnogi lajkovi pripadaju jednom korisniku
    @JoinColumn(name = "user_id", referencedColumnName = "id") // Kolona u 'post_user_likes' tabeli koja se odnosi na ID korisnika
    private User user; // Referenca na User entitet
    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;
    // Defaultni konstruktor (obavezan za JPA)
    public Like() {
    }

    // Konstruktor za lako kreiranje objekata lajka
    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
    }
    
    @PrePersist
    protected void onLike() {
        likedAt = LocalDateTime.now();
    }
    // Getteri i Setteri za 'post' i 'user' polja
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(LocalDateTime likedAt) {
        this.likedAt = likedAt;
    }

    // ##### KRITIČNO VAŽNO: equals() i hashCode() metode #####
    // Ove metode su apsolutno neophodne kada koristite kompozitni ključ sa @IdClass.
    // One omogućavaju Hibernateu da ispravno identifikuje jedinstvene lajkove u setovima
    // i prilikom manipulacije entitetima.
    // Uvek ih generišite automatski za sva polja koja čine kompozitni ključ ('post' i 'user' u ovom slučaju).
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return Objects.equals(post, like.post) && Objects.equals(user, like.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, user);
    }
}