package com.userms.service.Interface;

import com.userms.DTO.PageableResponse;
import com.userms.DTO.UserDTO;
import com.userms.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface UserServiceInterface {

        boolean isUserIdExists(Long userId);

        boolean isEmailIdExists(String emailId);

        boolean isGstinExists(String gstin);

        boolean isPanExists(String pan);

        boolean isTanExists(String tan);

        boolean isCinExists(String cin);

        boolean isMobileNoExists(String mobileNo);

    UserDTO createuser(UserDTO userDTO);

    String generateStrongPassword();

    UserDTO findByUsername(String username);

    UserDTO updateUser(UserDTO userDTO, Long userId);

    UserDTO getUserByUserId(Long userId);

    PageableResponse<List<UserDTO>> getAllUsers(int page, int size, String searchKey);

    List<UserDTO> getUsersByRoleId(Long roleId);

    void deleteUser(Long userId);

            UserEntity dtotoentity(UserDTO userDTO);

            UserDTO entitytodto(UserEntity user);
}
