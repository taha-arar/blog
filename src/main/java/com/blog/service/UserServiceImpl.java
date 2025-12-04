package com.blog.service;

import com.blog.dto.UserSaveDTO;
import com.blog.exception.UserDuplicateEmailException;
import com.blog.exception.UserPasswordRequiredException;
import com.blog.exception.UserNotFoundException;
import com.blog.exception.UserValidEmailException;
import com.blog.model.User;
import com.blog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
 @RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long save(UserSaveDTO userTO) {
        log.info("Attempting to save user with email {}", userTO.getEmail());

        if(userTO.getEmail() == null || userTO.getEmail().isEmpty()) {
            log.warn("Email is required to save user");
            throw new UserValidEmailException();
        }

        if(userRepository.existsByEmail(userTO.getEmail())) {
            log.warn("User with email {} already exists", userTO.getEmail());
            throw new UserDuplicateEmailException(userTO.getEmail());
        }

        if(userTO.getPassword() == null || userTO.getPassword().isEmpty()) {
            log.warn("Password is required to save user");
            throw new UserPasswordRequiredException();
        }

        User user = User.builder()
                .firstName(userTO.getFirstName())
                .lastName(userTO.getLastName())
                .email(userTO.getEmail())
                .password(passwordEncoder.encode(userTO.getPassword()))
                .isActive(userTO.getActive() != null ? userTO.getActive() : Boolean.TRUE)
                .role(userTO.getRole())
                .biography(userTO.getBiography())
                .domain(userTO.getDomain())
                .build();

        Long id = userRepository.save(user).getId();
        log.info("User {} saved successfully with id {}", userTO.getEmail(), id);
        return id;
    }

    @Override
    public void delete(Long id) {
        log.info("Attempting to delete user {}", id);
        if(!userRepository.existsById(id)) {
            log.warn("User {} not found for deletion", id);
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("User {} deleted successfully", id);
    }

    @Transactional
    @Override
    public UserSaveDTO update(Long id, UserSaveDTO userDTO) {
        log.info("Attempting to update user {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (userDTO.getEmail() != null && !userDTO.getEmail().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("Duplicate email {} detected during update for user {}", userDTO.getEmail(), id);
            throw new UserDuplicateEmailException(userDTO.getEmail());
        }

        if(userDTO.getFirstName() != null){
            user.setFirstName(userDTO.getFirstName());
        }
        if(userDTO.getLastName() != null){
            user.setLastName(userDTO.getLastName());
        }
        if(userDTO.getEmail() != null){
            user.setEmail(userDTO.getEmail());
        }
        if(userDTO.getPassword() != null && !userDTO.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if(userDTO.getActive() != null){
            user.setIsActive(userDTO.getActive());
        }
        if(userDTO.getBiography() != null){
            user.setBiography(userDTO.getBiography());
        }
        if(userDTO.getDomain() != null){
            user.setDomain(userDTO.getDomain());
        }
        if(userDTO.getRole() != null){
            user.setRole(userDTO.getRole());
        }

        User savedUser = userRepository.save(user);
        log.info("User {} updated successfully", id);
        return toDTO(savedUser);
    }

    @Override
    public List<UserSaveDTO> findAll() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Page<UserSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable) {
        log.info("Fetching paginated users with search criteria={} page={} size={}", criteria, pageable.getPageNumber(), pageable.getPageSize());
        Page<User> users = userRepository.findAll(pageable);
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

    private UserSaveDTO toDTO(User user) {
        UserSaveDTO dto = new UserSaveDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setActive(user.getIsActive());
        dto.setBiography(user.getBiography());
        dto.setDomain(user.getDomain());
        dto.setRole(user.getRole());
        return dto;
    }
}
