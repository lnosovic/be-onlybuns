package com.example.onlybuns.repository;

import com.example.onlybuns.model.Like; // Import Like entiteta
import com.example.onlybuns.model.LikeId; // Import LikeId (kompozitnog ključa)
import org.springframework.data.jpa.repository.JpaRepository; // Potreban import za JpaRepository
import org.springframework.stereotype.Repository; // Označava da je ovo Spring Data JPA repozitorijum
import java.util.Optional; // Import za Optional

@Repository // Označava da je ovo komponenta Spring repozitorijuma
public interface LikeRepository extends JpaRepository<Like, LikeId> {
    // JpaRepository<Like, LikeId> znači da će ovaj repozitorijum raditi sa 'Like' entitetima
    // i da je njihov primarni ključ definisan klasom 'LikeId'.

    // Opciono, ali korisno: metoda za pronalaženje specifičnog lajka
    // Proverava da li određeni korisnik već lajkuje određeni post.
    // Spring Data JPA automatski generiše implementaciju na osnovu imena metode.
    Optional<Like> findByPostIdAndUserId(Integer postId, Integer userId);

    // Opciono, ali korisno: metoda za brisanje specifičnog lajka
    // Omogućava ti da ukloniš lajk na osnovu ID-a posta i korisnika.
    void deleteByPostIdAndUserId(Integer postId, Integer userId);
}