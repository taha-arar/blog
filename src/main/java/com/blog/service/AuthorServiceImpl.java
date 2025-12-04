package com.blog.service;

import com.blog.dto.UserSaveDTO;
import com.blog.exception.AuthorActiveFlagRequiredException;
import com.blog.exception.AuthorDuplicateEmailException;
import com.blog.exception.AuthorNotFoundException;
import com.blog.exception.AuthorPasswordRequiredException;
import com.blog.model.User;
import com.blog.model.enums.Role;
import com.blog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long save(UserSaveDTO author) {
        log.info("Attempting to save author with email {}", author.getEmail());
        if(userRepository.existsByEmail(author.getEmail())) {
            log.warn("Author with email {} already exists", author.getEmail());
            throw new AuthorDuplicateEmailException(author.getEmail());
        }

        if(author.getPassword() == null || author.getPassword().isBlank()){
            log.warn("Password missing for author {}", author.getEmail());
            throw new AuthorPasswordRequiredException();
        }

        User user = User.builder()
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .email(author.getEmail())
                .password(passwordEncoder.encode(author.getPassword()))
                .isActive(author.getActive() != null ? author.getActive() : Boolean.TRUE)
                .role(Role.AUTHOR)
                .biography(author.getBiography())
                .domain(author.getDomain())
                .build();
        Long id = userRepository.save(user).getId();
        log.info("Author {} saved successfully with id {}", author.getEmail(), id);
        return id;
    }

    @Override
    public void delete(Long id) {
        log.info("Attempting to delete author {}", id);
        if(!userRepository.existsById(id)) {
            log.warn("Author {} not found for deletion", id);
            throw new AuthorNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("Author {} deleted successfully", id);
    }

    @Transactional
    @Override
    public UserSaveDTO update(Long id, UserSaveDTO authorDTO) {
        log.info("Attempting to update author {}", id);
        User author = userRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));

        if (authorDTO.getEmail() != null && !authorDTO.getEmail().equalsIgnoreCase(author.getEmail())
                && userRepository.existsByEmail(authorDTO.getEmail())) {
            log.warn("Duplicate email {} detected during update for author {}", authorDTO.getEmail(), id);
            throw new AuthorDuplicateEmailException(authorDTO.getEmail());
        }

        if(authorDTO.getFirstName() != null){
            author.setFirstName(authorDTO.getFirstName());
        }
        if(authorDTO.getLastName() != null){
            author.setLastName(authorDTO.getLastName());
        }
        if(authorDTO.getEmail() != null){
            author.setEmail(authorDTO.getEmail());
        }
        if(authorDTO.getPassword() != null && !authorDTO.getPassword().isBlank()){
            author.setPassword(passwordEncoder.encode(authorDTO.getPassword()));
        }
        if(authorDTO.getActive() != null){
            author.setIsActive(authorDTO.getActive());
        }
        if(authorDTO.getBiography() != null){
            author.setBiography(authorDTO.getBiography());
        }
        if(authorDTO.getDomain() != null){
            author.setDomain(authorDTO.getDomain());
        }

        User savedUser = userRepository.save(author);
        log.info("Author {} updated successfully", id);
        return toDTO(savedUser);
    }

    @Override
    public String active(Long id, Boolean active) {
        log.info("Updating active state for author {} to {}", id, active);
        if (active == null) {
            log.warn("Active flag required when updating author {}", id);
            throw new AuthorActiveFlagRequiredException();
        }

        User author = userRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));

        if (active.equals(author.getIsActive())) {
            log.warn("Active state already {} for author {}", active, id);
            return "Author already " + (active ? "active" : "deactivated");
        }

        author.setIsActive(active);
        userRepository.save(author);
        log.info("Author {} active state updated to {}", id, active);
        return "Author " + (active ? "activated" : "deactivated") + " successfully.";
    }

    @Override
    public UserSaveDTO findById(Long id) {
        log.info("Fetching author {}", id);
        User author = userRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
        log.info("Author {} fetched successfully", id);
        return toDTO(author);
    }

    @Override
    public List<UserSaveDTO> findAll() {
        log.info("Fetching all authors");
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Page<UserSaveDTO> findAllPagination(Pageable pageable) {
        log.info("Fetching paginated authors page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findByRole(Role.AUTHOR, pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<UserSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable) {
        log.info("Fetching paginated authors with search criteria={} page={} size={}", criteria, pageable.getPageNumber(), pageable.getPageSize());
        Page<User> users = userRepository.findByRole(Role.AUTHOR, pageable);
        if (criteria == null || criteria.isBlank()) {
            return users.map(this::toDTO);
        }

        String filter = criteria.toLowerCase();
        List<User> filtered = users.stream()
                .filter(user -> userFilter(user, filter))
                .toList();
        List<UserSaveDTO> dtos = filtered.stream().map(this::toDTO).toList();
        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    private boolean userFilter(User user, String filter){
        return (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(filter)
        || user.getLastName() != null && user.getLastName().toLowerCase().contains(filter)
        || user.getEmail() != null && user.getEmail().toLowerCase().contains(filter)
        || user.getBiography() != null && user.getBiography().toLowerCase().contains(filter)
        || user.getDomain() != null && user.getDomain().name().toLowerCase().contains(filter)
        );
    }

    private  UserSaveDTO  toDTO(User author) {
        UserSaveDTO dto = new UserSaveDTO();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        dto.setEmail(author.getEmail());
        dto.setActive(author.getIsActive());
        dto.setBiography(author.getBiography());
        dto.setDomain(author.getDomain());
        return dto;
    }

}
