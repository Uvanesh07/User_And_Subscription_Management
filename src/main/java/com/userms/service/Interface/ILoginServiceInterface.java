package com.userms.service.Interface;

import com.userms.DTO.LoginDTO;
import org.springframework.stereotype.Component;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public interface ILoginServiceInterface {

    void createLogin(LoginDTO loginDTO);

    boolean isUsernameExists(String username);

    boolean updateLoginTime(LoginDTO loginDTO);

    void logout(String username);

    String resetPassword(String username, String password) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
}