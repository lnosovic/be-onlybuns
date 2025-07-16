package com.example.onlybuns.controller;

import com.example.onlybuns.dto.ChatMessageDTO;
import com.example.onlybuns.dto.ChatMessageResponseDTO;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.model.ChatMessage;
import com.example.onlybuns.model.ChatRoom;
import com.example.onlybuns.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController // Označava da je ovo RESTful kontroler
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService; // Injektujemo ChatService
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    // --- ENDPOINTI ZA SLANJE PORUKA I ISTORIJU ---

    /**
     * Endpoint za slanje nove poruke.
     * HTTP POST na /api/chat/messages/send
     *
     * @param messageDTO DTO objekat sa sadržajem poruke i ID-jem sobe.
     * @param principal Principal objekat ulogovanog korisnika.
     * @return ResponseEntity sa sačuvanom ChatMessageResponseDTO ili greškom.
     */
    @PostMapping("/messages/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageDTO messageDTO, Principal principal) {
        try {
            ChatMessage savedMessage = chatService.saveMessage(messageDTO, principal);
            ChatMessageResponseDTO responseDTO = chatService.mapToResponseDTO(savedMessage);

            // 👇 Ovo dodaj za live slanje poruke u chat sobu
            messagingTemplate.convertAndSend("/topic/chatroom/" + messageDTO.getRoomId(), responseDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    /**
     * Endpoint za dohvatanje istorije poruka za određenu sobu.
     * HTTP GET na /api/chat/messages/history/{roomId}
     *
     * @param roomId ID sobe za koju se traži istorija.
     * @return ResponseEntity sa listom ChatMessageResponseDTO objekata ili greškom.
     */
    @GetMapping("/messages/history/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId) {
        try {
            List<ChatMessageResponseDTO> history = chatService.getChatHistory(roomId);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- ENDPOINTI ZA UPRAVLJANJE GRUPNIM ČET SOBAMA ---

    /**
     * Endpoint za kreiranje nove grupne čet sobe.
     * HTTP POST na /api/chat/rooms/group/create
     *
     * @param requestBody Map sa ključem "name" za naziv grupe.
     * @param principal Principal objekat ulogovanog korisnika (kreatora/admina).
     * @return ResponseEntity sa kreiranom ChatRoom ili greškom.
     */
    @PostMapping("/rooms/group/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createGroupChat(@RequestBody Map<String, String> requestBody, Principal principal) {
        String groupName = requestBody.get("name");
        if (groupName == null || groupName.trim().isEmpty()) {
            groupName = "New Group Chat";
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Group name is required.");
        }
        try {
            ChatRoom newGroup = chatService.createGroupChat(groupName, principal);

            return ResponseEntity.status(HttpStatus.CREATED).body(newGroup);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint za dodavanje korisnika u grupnu čet sobu.
     * HTTP POST na /api/chat/rooms/group/{roomId}/add
     *
     * @param roomId ID grupne sobe.
     * @param requestBody Map sa ključem "userId" za ID korisnika koji se dodaje.
     * @param principal Principal objekat admina koji vrši operaciju.
     * @return ResponseEntity sa ažuriranom ChatRoom ili greškom.
     */
    @PostMapping("/rooms/group/{roomId}/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addUserToGroup(@PathVariable Long roomId, @RequestBody Map<String, Long> requestBody, Principal principal) {
        Long userIdToAdd = requestBody.get("userId");
        if (userIdToAdd == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID to add is required.");
        }
        try {
            ChatRoom updatedRoom = chatService.addUserToGroup(roomId, userIdToAdd, principal);
            messagingTemplate.convertAndSend("/topic/newChatRoom/" + userIdToAdd, updatedRoom.getId());

            return ResponseEntity.ok(updatedRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint za uklanjanje korisnika iz grupne čet sobe.
     * HTTP POST na /api/chat/rooms/group/{roomId}/remove
     *
     * @param roomId ID grupne sobe.
     * @param requestBody Map sa ključem "userId" za ID korisnika koji se uklanja.
     * @param principal Principal objekat admina koji vrši operaciju.
     * @return ResponseEntity sa ažuriranom ChatRoom ili greškom.
     */
    @PostMapping("/rooms/group/{roomId}/remove")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeUserFromGroup(@PathVariable Long roomId, @RequestBody Map<String, Long> requestBody, Principal principal) {
        Long userIdToRemove = requestBody.get("userId");
        if (userIdToRemove == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID to remove is required.");
        }
        try {
            ChatRoom updatedRoom = chatService.removeUserFromGroup(roomId, userIdToRemove, principal);
            return ResponseEntity.ok(updatedRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // --- ENDPOINTI ZA UPRAVLJANJE PERSONALNIM ČET SOBAMA ---

    /**
     * Endpoint za kreiranje nove personalne (1-na-1) čet sobe.
     * HTTP POST na /api/chat/rooms/personal/create
     *
     * @param requestBody Map sa ključem "otherUserId" za ID drugog korisnika.
     * @param principal Principal objekat ulogovanog korisnika (kreatora).
     * @return ResponseEntity sa kreiranom ChatRoom ili greškom.
     */
    @PostMapping("/rooms/personal/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPersonalChat(@RequestBody Map<String, Long> requestBody, Principal principal) {
        Long otherUserId = requestBody.get("otherUserId");
        if (otherUserId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Other user ID is required.");
        }
        try {
            ChatRoom newPersonalChat = chatService.createPersonalChat(otherUserId, principal);

            messagingTemplate.convertAndSend("/topic/newChatRoom/" + otherUserId, newPersonalChat.getId());


            return ResponseEntity.status(HttpStatus.CREATED).body(newPersonalChat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint za dohvatanje svih čet soba kojima ulogovani korisnik pripada.
     * HTTP GET na /api/chat/rooms/my-chatrooms
     *
     * @param principal Principal objekat ulogovanog korisnika.
     * @return ResponseEntity sa listom ChatRoom objekata ili greškom.
     */
    @GetMapping("/rooms/my-chatrooms")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserChatRooms(Principal principal) {
        try {
            List<ChatRoom> userChatRooms = chatService.getUserChatRooms(principal);
            return ResponseEntity.ok(userChatRooms);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //...xd
    @GetMapping("/{chatRoomId}/participants")
    public ResponseEntity<List<UserViewDTO>> getParticipants(@PathVariable Long chatRoomId) {
        return ResponseEntity.ok(chatService.getParticipants(chatRoomId));
    }


    @GetMapping("/{chatRoomId}/adminId")
    //@PreAuthorize("isAuthenticated()")
    public Integer getAdminId(@PathVariable Long chatRoomId) {
        return chatService.getAdminIdForChatRoom(chatRoomId);
    }


}