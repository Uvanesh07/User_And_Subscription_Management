package com.userms.service.impl;

import com.userms.DTO.LoginDTO;
import com.userms.entity.LoginEntity;
import com.userms.entity.UserEntity;
import com.userms.repository.ILoginRepo;
import com.userms.repository.IUserRepo;
import com.userms.service.Interface.ILoginServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
public class LoginServiceImpl implements ILoginServiceInterface {

    private final Logger LOGGER= LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    ILoginRepo loginRepo;

    @Autowired
    IUserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public boolean isUsernameExists(String username) {
        return loginRepo.existsByUsername(username);
    }


    @Override
    public void createLogin(LoginDTO loginDTO) {
//        loginDTO.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
        loginDTO.setPassword(passwordEncoder.encode("123"));
        LoginEntity login = dtoToLoginEntity(loginDTO);
        login = loginRepo.save(login);
        UserEntity user = userRepo.findByEmailId(loginDTO.getUsername());
        user.setLogin(login);
        userRepo.save(user);
    }

    @Override
    public boolean updateLoginTime(LoginDTO loginDTO) {
        LoginEntity uname= loginRepo.findByUsername(loginDTO.getUsername());
        LOGGER.info("ENTERING into UpdateLOGIN Time");
        if (uname.isAccountActive()) {
            uname.setLoginTime(LocalDateTime.now());
            uname.setAccountInactiveReason(null);
            loginRepo.save(uname);
            return true;
        }
        else {
            uname.setAccountInactiveReason("UNAUTHORIZED");
            loginRepo.save(uname);
            return false;
        }
    }


    @Override
    public String resetPassword(String username, String password) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        LOGGER.info("Entering Into the resetPassword");
        LoginEntity login = loginRepo.findByUsername(username);
        if (login == null) {
            return null;
        }
        if (!login.isAccountActive()) {
            return "inActive";
        }
        login.setPassword(passwordEncoder.encode(password));
        loginRepo.save(login);
        LOGGER.info("EXIT Into the resetPassword");
        return "active";
    }


    @Override
    public void logout(String username){
        LOGGER.info("Entering Into the logout");
        LoginEntity login = loginRepo.findByUsername(username);
        if (login.isAccountActive()) {
            login.setLogoutTime(LocalDateTime.now());
            loginRepo.save(login);
        }
        LOGGER.info("EXIT Into the logout");
    }

    public LoginEntity dtoToLoginEntity(LoginDTO dto) {
        LOGGER.info("Entering Into the toEntity");
        LoginEntity entity = new LoginEntity();
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setAccountActive(dto.isAccountActive());
        entity.setAccountInactiveReason(dto.getAccountInactiveReason());
        entity.setLoginTime(dto.getLoginTime());
        entity.setLogoutTime(dto.getLogoutTime());
        LOGGER.info("EXIT Into the toEntity");
        return entity;
    }


}