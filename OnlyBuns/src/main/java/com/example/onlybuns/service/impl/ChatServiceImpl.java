package com.example.onlybuns.service.impl;

import com.example.onlybuns.dto.ChatMessageDTO;
import com.example.onlybuns.dto.ChatMessageResponseDTO;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.mapper.UserDTOMapper;
import com.example.onlybuns.model.ChatMessage;
import com.example.onlybuns.model.ChatRoom;
import com.example.onlybuns.model.User;
import com.example.onlybuns.service.ChatService;
import com.example.onlybuns.repository.ChatMessageRepository;
import com.example.onlybuns.repository.ChatRoomRepository;
import com.example.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service // Označava da je ova klasa Spring Service komponenta
public class ChatServiceImpl implements ChatService { // Implementira ChatService interfejs

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository; // Injektuješ svoj UserRepository

    @Autowired
    private  UserDTOMapper userMapper;


    @Autowired
    public ChatServiceImpl(ChatRoomRepository chatRoomRepository, UserDTOMapper userMapper) {
        this.roomRepository = chatRoomRepository;
        this.userMapper = userMapper;
    }


    /**
     * Čuva novu poruku u bazu podataka.
     *
     * @param messageDTO DTO objekat koji sadrži sadržaj poruke i ID sobe.
     * @param principal Principal objekat koji sadrža informacije o ulogovanom korisniku (pošiljaocu).
     * @return Sačuvani ChatMessage entitet.
     * @throws RuntimeException Ako korisnik ili soba nisu pronađeni.
     */
    @Override
    @Transactional
    public ChatMessage saveMessage(ChatMessageDTO messageDTO, Principal principal) {
        // 1. Pronađi korisnika koji šalje poruku.
        Optional<User> senderOptional = Optional.ofNullable(userRepository.findByUsername(principal.getName()));
        if (senderOptional.isEmpty()) { // Ili !senderOptional.isPresent()
            throw new RuntimeException("Sender user not found: " + principal.getName());
        }
        User sender = senderOptional.get();

        // 2. Pronađi čet sobu.
        Optional<ChatRoom> roomOptional = roomRepository.findById(messageDTO.getRoomId());
        if (roomOptional.isEmpty()) { // Ili !roomOptional.isPresent()
            throw new RuntimeException("Chat room not found with ID: " + messageDTO.getRoomId());
        }
        ChatRoom room = roomOptional.get();

        // 3. Proveri da li je pošiljalac učesnik u toj sobi.
        if (!room.getParticipants().contains(sender)) {
            throw new RuntimeException("User " + principal.getName() + " is not a participant of chat room " + room.getId());
        }

        // 4. Kreiraj novi ChatMessage entitet.
        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setChatRoom(room);
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());

        // 5. Sačuvaj poruku u bazu podataka.
        return messageRepository.save(message);
    }
    @Override
    @Transactional
    public ChatMessage saveMessage(ChatMessageDTO messageDTO) {
        // 1. Pronađi korisnika koji šalje poruku.
//        System.out.print("MessageDTO SA FRONTA\nSender: " + messageDTO.getSenderUsername());
//        Optional<User> senderOptional = Optional.ofNullable(userRepository.findByUsername(messageDTO.getSenderUsername()));
//        if (senderOptional.isEmpty()) { // Ili !senderOptional.isPresent()
//            throw new RuntimeException("Sender user not found: " + messageDTO.getSenderUsername());
//        }
//        User sender = senderOptional.get();
//
//        // 2. Pronađi čet sobu.
//        Optional<ChatRoom> roomOptional = roomRepository.findById(messageDTO.getRoomId());
//        if (roomOptional.isEmpty()) { // Ili !roomOptional.isPresent()
//            throw new RuntimeException("Chat room not found with ID: " + messageDTO.getRoomId());
//        }
//        ChatRoom room = roomOptional.get();
//
//        // 3. Proveri da li je pošiljalac učesnik u toj sobi.
//        if (!room.getParticipants().contains(sender)) {
//            throw new RuntimeException("User " + messageDTO.getSenderUsername() + " is not a participant of chat room " + room.getId());
//        }

        // 4. Kreiraj novi ChatMessage entitet.
        ChatMessage message = new ChatMessage();
        //message.setSender(sender);
        //message.setChatRoom(1);
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());

        // 5. Sačuvaj poruku u bazu podataka.
        return messageRepository.save(message);
    }
    @Override
    @Transactional(readOnly = true) // Ova metoda samo čita podatke, pa koristimo readOnly = true za optimizaciju
    public List<ChatMessageResponseDTO> getChatHistory(Long roomId) {
        // 1. Proveri da li čet soba uopšte postoji.
        // Iako se poruke dohvataju preko ID-a sobe, dobra je praksa proveriti postojanje sobe
        // pre nego što pokušamo da dohvatimo njene poruke.
        Optional<ChatRoom> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            throw new RuntimeException("Chat room not found with ID: " + roomId);
        }
        // Nije nam potreban sam objekat sobe, ali provera je dobra zbog rukovanja greškama.

        // 2. Dohvati poslednjih 10 poruka za datu sobu, sortirane opadajuće po vremenu.
        // Ovo koristi magiju Spring Data JPA i metodu koju si definisao u ChatMessageRepository.
        //List<ChatMessage> messages = messageRepository.findTop10ByChatRoom_IdOrderByTimestampDesc(roomId);
        List<ChatMessage> messages = messageRepository.findByChatRoom_IdOrderByTimestampDesc(roomId);

        // 3. Obrni redosled poruka.
        // Spring Data JPA metoda `findTop10...OrderByTimestampDesc` vraća poruke od najnovije ka najstarijoj.
        // Za prikaz istorije, obično želimo da najstarije poruke budu prve, a najnovije poslednje.
        Collections.reverse(messages);

        // 4. Mapiraj ChatMessage entitete u ChatMessageResponseDTO objekte.
        // Koristimo Stream API i pomoćnu metodu 'mapToResponseDTO' za transformaciju.
        return messages.stream()
                .map(this::mapToResponseDTO) // 'this::mapToResponseDTO' je skraćeni način za pozivanje metode na instanci 'this'
                .collect(Collectors.toList()); // Sakupi rezultat u Listu
    }
    @Override // I dalje je @Override jer je u ChatService interfejsu (ponovo)
    public ChatMessageResponseDTO mapToResponseDTO(ChatMessage message) {
        if (message == null) {
            return null;
        }

        ChatMessageResponseDTO dto = new ChatMessageResponseDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());

        // Sigurne provere za povezane objekte
        if (message.getChatRoom() != null) {
            dto.setRoomId(message.getChatRoom().getId());
        } else {
            dto.setRoomId(null);
        }

        if (message.getSender() != null) {
            dto.setSenderId((long) message.getSender().getId());
            dto.setSenderUsername(message.getSender().getUsername());
        } else {
            dto.setSenderId(null);
            dto.setSenderUsername("Unknown");
        }

        return dto;
    }

    @Override
    @Transactional // Ovo je operacija pisanja u bazu, pa nam treba transakcija
    public ChatRoom createGroupChat(String name, Principal creator) {
        // 1. Pronađi korisnika koji kreira sobu na osnovu Principal objekta.
        Optional<User> creatorUserOptional = Optional.ofNullable(userRepository.findByUsername(creator.getName()));
        if (creatorUserOptional.isEmpty()) {
            throw new RuntimeException("Creator user not found: " + creator.getName());
        }
        User creatorUser = creatorUserOptional.get();

        // 2. Kreiraj novi ChatRoom entitet.
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name); // Postavi naziv grupe
        chatRoom.setGroupChat(true); // Postavi tip sobe na "GROUP"
        chatRoom.setAdmin(creatorUser); // Postavi kreatora kao admina grupe

        // 3. Dodaj kreatora kao prvog učesnika grupe.
        // Bitno: Koristi HashSet ili ArrayList za učesnike u tvom ChatRoom modelu.
        chatRoom.setParticipants(new HashSet<>()); // Inicijalizuj set ako nije već
        chatRoom.getParticipants().add(creatorUser);

        // 4. Sačuvaj novu čet sobu u bazu podataka.
        return roomRepository.save(chatRoom);
    }

    @Override
    @Transactional // Ovo je operacija pisanja/ažuriranja u bazu, pa nam treba transakcija
    public ChatRoom addUserToGroup(Long roomId, Long userIdToAdd, Principal admin) {
        // 1. Pronađi čet sobu po ID-u.
        Optional<ChatRoom> chatRoomOptional = roomRepository.findById(roomId);
        if (chatRoomOptional.isEmpty()) {
            throw new RuntimeException("Chat room not found with ID: " + roomId);
        }
        ChatRoom chatRoom = chatRoomOptional.get();

        // 2. Proveri da li je soba grupnog tipa.
        // Koristimo tvoje ispravno polje isGroupChat.
        if (!chatRoom.isGroupChat()) {
            throw new RuntimeException("Cannot add users to a non-group chat room. Room ID: " + roomId);
        }

        // 3. Pronađi korisnika koji je admin i vrši operaciju.
        Optional<User> adminUserOptional = Optional.ofNullable(userRepository.findByUsername(admin.getName()));
        if (adminUserOptional.isEmpty()) {
            throw new RuntimeException("Admin user not found: " + admin.getName());
        }
        User adminUser = adminUserOptional.get();

        // 4. Proveri da li je pozivač (trenutni ulogovani korisnik) stvarno admin te sobe.
        // Pretpostavljam da ChatRoom ima polje 'admin' tipa User.
        if (chatRoom.getAdmin() == null || !chatRoom.getAdmin().getId().equals(adminUser.getId())) {
            throw new RuntimeException("User " + admin.getName() + " is not an admin of chat room " + roomId);
        }

        // 5. Pronađi korisnika kojeg treba dodati.
        Optional<User> userToAddOptional = userRepository.findById( userIdToAdd.intValue());
        if (userToAddOptional.isEmpty()) {
            throw new RuntimeException("User to add not found with ID: " + userIdToAdd);
        }
        User userToAdd = userToAddOptional.get();

        // 6. Proveri da li je korisnik već učesnik u sobi.
        if (chatRoom.getParticipants().contains(userToAdd)) {
            throw new RuntimeException("User " + userToAdd.getUsername() + " is already in chat room " + roomId);
        }

        // 7. Dodaj korisnika u set učesnika čet sobe.
        // Pretpostavljam da chatRoom.getParticipants() vraća modifikabilnu kolekciju (npr. Set ili List).
        chatRoom.getParticipants().add(userToAdd);


        //7.5!!!
        String meta = chatRoom.getParticipantsMeta();
        if (meta == null) {
            meta = "";
        }
        String newEntry = userIdToAdd + "," + LocalDateTime.now().toString() + ";";
        chatRoom.setParticipantsMeta(meta + newEntry);


        // 8. Sačuvaj (ažuriraj) čet sobu u bazi podataka.
        return roomRepository.save(chatRoom);
    }
    @Override
    @Transactional // Operacija pisanja/ažuriranja u bazu, pa nam treba transakcija
    public ChatRoom removeUserFromGroup(Long roomId, Long userIdToRemove, Principal admin) {
        // 1. Pronađi čet sobu po ID-u.
        Optional<ChatRoom> chatRoomOptional = roomRepository.findById(roomId);
        if (chatRoomOptional.isEmpty()) {
            throw new RuntimeException("Chat room not found with ID: " + roomId);
        }
        ChatRoom chatRoom = chatRoomOptional.get();

        // 2. Proveri da li je soba grupnog tipa.
        if (!chatRoom.isGroupChat()) {
            throw new RuntimeException("Cannot remove users from a non-group chat room. Room ID: " + roomId);
        }

        // 3. Pronađi korisnika koji je admin i vrši operaciju.
        Optional<User> adminUserOptional = Optional.ofNullable(userRepository.findByUsername(admin.getName()));
        if (adminUserOptional.isEmpty()) {
            throw new RuntimeException("Admin user not found: " + admin.getName());
        }
        User adminUser = adminUserOptional.get();

        // 4. Proveri da li je pozivač (trenutni ulogovani korisnik) stvarno admin te sobe.
        if (chatRoom.getAdmin() == null || !chatRoom.getAdmin().getId().equals(adminUser.getId())) {
            throw new RuntimeException("User " + admin.getName() + " is not an admin of chat room " + roomId);
        }

        // 5. Pronađi korisnika kojeg treba ukloniti.
        Optional<User> userToRemoveOptional = userRepository.findById((Integer) userIdToRemove.intValue());
        if (userToRemoveOptional.isEmpty()) {
            throw new RuntimeException("User to remove not found with ID: " + userIdToRemove);
        }
        User userToRemove = userToRemoveOptional.get();

        // 6. Proveri da li je korisnik kojeg treba ukloniti zapravo učesnik u sobi.
        if (!chatRoom.getParticipants().contains(userToRemove)) {
            throw new RuntimeException("User " + userToRemove.getUsername() + " is not a participant of chat room " + roomId);
        }

        // 7. (Opciono, ali preporučeno): Sprečiti admina da ukloni sam sebe kao poslednjeg člana.
        //    Ako admin ukloni sam sebe, a nema drugih admina (što je pretpostavka), soba bi mogla ostati bez kontrole.
        //    Ovo zavisi od poslovne logike. Ako postoji više admina, ili ako soba može ostati bez admina.
        if (userToRemove.getId().equals(adminUser.getId()) && chatRoom.getParticipants().size() == 1) {
            throw new RuntimeException("Admin cannot remove themselves as the last participant of the group.");
        }


        // 8. Ukloni korisnika iz seta učesnika čet sobe.
        chatRoom.getParticipants().remove(userToRemove);

        // 9. Sačuvaj (ažuriraj) čet sobu u bazi podataka.
        return roomRepository.save(chatRoom);
    }

    @Override
    @Transactional // Ovo je operacija pisanja u bazu, pa nam treba transakcija
    public ChatRoom createPersonalChat(Long otherUserId, Principal creator) {
        // 1. Pronađi korisnika koji kreira čet (Creator).
        Optional<User> creatorUserOptional = Optional.ofNullable(userRepository.findByUsername(creator.getName()));
        if (creatorUserOptional.isEmpty()) {
            throw new RuntimeException("Creator user not found: " + creator.getName());
        }
        User creatorUser = creatorUserOptional.get();

        // 2. Pronađi drugog korisnika (Other User) s kojim se kreira čet.
        // Pretpostavljam da je ID korisnika tipa Long, prema tvojim poslednjim informacijama.
        Optional<User> otherUserOptional = userRepository.findById(otherUserId.intValue());
        if (otherUserOptional.isEmpty()) {
            throw new RuntimeException("Other user not found with ID: " + otherUserId);
        }
        User otherUser = otherUserOptional.get();

        // 3. Proveri da li korisnik pokušava da kreira čet sa samim sobom.
        if (creatorUser.getId().equals(otherUser.getId())) {
            throw new RuntimeException("Cannot create a personal chat with yourself.");
        }

        // 4. Proveri da li personalna soba između ova dva korisnika već postoji.
        // Ovo je ključno za 1-na-1 četove kako se ne bi duplirale sobe.
        // Moraš implementirati metodu u ChatRoomRepository koja traži sobu po dva učesnika
        // i po tipu sobe.
        // Primer: findByParticipantsContainsAndParticipantsContainsAndIsGroupChatFalse
        // S obzirom da redosled u setu nije bitan, treba da proverimo oba smera
        // (tj. da li su U1 i U2 u sobi, ili U2 i U1).
        // Bolje je dohvatiti sve sobe oba korisnika i onda proveriti da li postoji soba sa samo ta dva učesnika
        // i da je isGroupChat = false.

        // Mogući query: List<ChatRoom> findByParticipantsContainingAndParticipantsContaining(User user1, User user2);
        // Zatim iterirati kroz rezultate i proveriti isGroupChat i da li ima TAČNO 2 učesnika.
        List<ChatRoom> existingPersonalChats = roomRepository.findByParticipantsContainsAndParticipantsContains(creatorUser, otherUser);

        for (ChatRoom room : existingPersonalChats) {
            if (!room.isGroupChat() && room.getParticipants().size() == 2) {
                // Pronađena postojeća personalna soba između ovih korisnika
//                throw new RuntimeException("Personal chat room already exists between "
//                        + creatorUser.getUsername() + " and " + otherUser.getUsername());
                return room;
            }
        }


        // 5. Kreiraj novi ChatRoom entitet za personalni čet.
        ChatRoom personalChatRoom = new ChatRoom();
        // Naziv za personalne četove često se ne postavlja ili se automatski generiše
        // npr. "John & Jane" ili id od oba korisnika. Za sada, možemo ostaviti null ili generisati.
        // Npr. personalChatRoom.setName(creatorUser.getUsername() + " & " + otherUser.getUsername());
        personalChatRoom.setName(null); // Ostavimo null ako se ne prikazuje naziv za 1-na-1
        personalChatRoom.setGroupChat(false); // Postavi tip sobe na false za personalni čet

        // 6. Dodaj oba korisnika kao učesnike.
        Set<User> participants = new HashSet<>();
        participants.add(creatorUser);
        participants.add(otherUser);
        personalChatRoom.setParticipants(participants);

        // 7. Admin polje za personalni čet često može biti null ili se postavlja na kreatora.
        // Ako nema admina za 1-na-1 četove, ostavi ga null ili odluči šta model sugeriše.
        // U zavisnosti od tvog ChatRoom modela, admin bi mogao biti null za personalne chatove.
        personalChatRoom.setAdmin(null); // Nema admina za 1-na-1 čet, obično

        // 8. Sačuvaj novu personalnu čet sobu u bazu podataka.
        return roomRepository.save(personalChatRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoom> getUserChatRooms(Principal principal) {
        // 1. Pronađi korisnika na osnovu Principal objekta.
        Optional<User> currentUserOptional = Optional.ofNullable(userRepository.findByUsername(principal.getName()));
        if (currentUserOptional.isEmpty()) {
            throw new RuntimeException("Logged-in user not found: " + principal.getName());
        }
        User currentUser = currentUserOptional.get();

        // 2. Dohvati sve čet sobe u kojima je ovaj korisnik učesnik.
        List<ChatRoom> userChatRooms = roomRepository.findByParticipantsContains(currentUser);

        return userChatRooms;
    }

    @Override
    public List<UserViewDTO> getParticipants(Long chatRoomId) {
        return roomRepository.findParticipantsByChatRoomId(chatRoomId).stream()
                .map(userMapper::toDTO) // bez user u zagradi
                .collect(Collectors.toList());
    }
    //XD
    @Override
    public Integer getAdminIdForChatRoom(Long chatRoomId) {
        Integer adminId = roomRepository.findAdminIdByChatRoomId(chatRoomId);
        if (adminId == null) {
            //throw new IllegalStateException("Admin ID not found for ChatRoom ID: " + chatRoomId);
            adminId = 0;
        }
        return adminId;
    }
    @Override
    @Transactional(readOnly = true)
    public String getParticipantsMeta(Long roomId) {
        Optional<ChatRoom> chatRoomOptional = roomRepository.findById(roomId);
        if (chatRoomOptional.isEmpty()) {
            throw new RuntimeException("Chat room not found with ID: " + roomId);
        }
        ChatRoom chatRoom = chatRoomOptional.get();

        return chatRoom.getParticipantsMeta(); // assuming getter exists
    }
}