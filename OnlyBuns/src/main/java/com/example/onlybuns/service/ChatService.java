package com.example.onlybuns.service;

import com.example.onlybuns.dto.ChatMessageDTO;
import com.example.onlybuns.dto.ChatMessageResponseDTO;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.model.ChatMessage;
import com.example.onlybuns.model.ChatRoom; // Pretpostavljam da ćeš vratiti ChatRoom ili DTO za nju
import com.example.onlybuns.model.User;    // Možda će ti trebati User objekat ili njegov ID

import java.security.Principal;
import java.util.List;

/**
 * Interfejs koji definiše ugovore za operacije vezane za čet.
 * Odgovornosti uključuju upravljanje porukama, čet sobama i korisnicima unutar tih soba.
 */
public interface ChatService {

    /**
     * Čuva novu poruku u bazu podataka.
     *
     * @param messageDTO DTO objekat koji sadrži sadržaj poruke i ID sobe.
     * @param principal Principal objekat koji sadrža informacije o ulogovanom korisniku (pošiljaocu).
     * @return Sačuvani ChatMessage entitet.
     */
    ChatMessage saveMessage(ChatMessageDTO messageDTO, Principal principal);
    ChatMessage saveMessage(ChatMessageDTO messageDTO);

    /**
     * Dohvata poslednjih 10 poruka za određenu čet sobu.
     *
     * @param roomId ID čet sobe za koju se traži istorija poruka.
     * @return Lista ChatMessageResponseDTO objekata, sortiranih od najstarije ka najnovijoj.
     */
    List<ChatMessageResponseDTO> getChatHistory(Long roomId);

    /**
     * Pomoćna metoda za mapiranje ChatMessage entiteta u ChatMessageResponseDTO.
     *
     * @param message ChatMessage entitet koji treba mapirati.
     * @return ChatMessageResponseDTO objekat spreman za slanje klijentu.
     */
    ChatMessageResponseDTO mapToResponseDTO(ChatMessage message);

    /**
     * Kreira novu grupnu čet sobu.
     * Kreira se prazna soba, korisnici se dodaju naknadno.
     *
     * @param name Naziv grupne čet sobe.
     * @param creator Principal objekat ulogovanog korisnika koji kreira sobu (postaje admin).
     * @return Kreirani ChatRoom objekat.
     */
    ChatRoom createGroupChat(String name, Principal creator);

    /**
     * Dodaje korisnika u postojeću grupnu čet sobu.
     * Samo admin sobe može dodavati korisnike.
     *
     * @param roomId ID grupne sobe u koju se dodaje korisnik.
     * @param userIdToAdd ID korisnika kojeg treba dodati.
     * @param admin Principal objekat admina koji vrši operaciju.
     * @return Ažurirani ChatRoom objekat.
     */
    ChatRoom addUserToGroup(Long roomId, Long userIdToAdd, Principal admin);

    /**
     * Uklanja korisnika iz postojeće grupne čet sobe.
     * Samo admin sobe može uklanjati korisnike.
     *
     * @param roomId ID grupne sobe iz koje se uklanja korisnik.
     * @param userIdToRemove ID korisnika kojeg treba ukloniti.
     * @param admin Principal objekat admina koji vrši operaciju.
     * @return Ažurirani ChatRoom objekat.
     */
    ChatRoom removeUserFromGroup(Long roomId, Long userIdToRemove, Principal admin);

    /**
     * Kreira personalnu (1-na-1) čet sobu između dva korisnika.
     * Ako soba već postoji, vraća postojeću.
     *
     * @param otherUserId ID drugog korisnika sa kojim se kreira čet.
     * @param creator Principal objekat ulogovanog korisnika koji inicira čet.
     * @return Kreirani ili pronađeni ChatRoom objekat.
     */
    ChatRoom createPersonalChat(Long otherUserId, Principal creator);

    /**
     * Dohvata sve čet sobe u kojima je ulogovani korisnik učesnik.
     *
     * @param principal Principal objekat ulogovanog korisnika.
     * @return Lista ChatRoom objekata.
     */
    List<ChatRoom> getUserChatRooms(Principal principal);
    List<UserViewDTO> getParticipants(Long chatRoomId);

    //
    Integer getAdminIdForChatRoom(Long chatRoomId);
    String getParticipantsMeta(Long roomId);
}