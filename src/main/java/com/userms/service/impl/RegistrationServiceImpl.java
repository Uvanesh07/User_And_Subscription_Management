package com.userms.service.impl;

import com.userms.DTO.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userms.entity.*;
import com.userms.repository.*;
import com.userms.service.Interface.UserServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.security.SecureRandom;


@Service
public class RegistrationServiceImpl implements UserServiceInterface {

    private final Logger LOGGER = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    IUserRepo userRepository;

    @Autowired
    IAddressRepo addressRepo;

    @Autowired
    IOrganizationRepo organizationRepo;

    @Autowired
    IOrganizationSubscriptionRepo organizationSubscriptionRepo;

    @Autowired
    ISubscriptionRepo iSubscriptionRepo;

    @Autowired
    ILoginRepo iLoginRepo;

    @Autowired
    IRoleRepo iRoleRepo;

    @Autowired
    IPermissionRepo iPermissionRepo;

    @Value("${role.default.name}")
    private String defaultRoleName;

    @Value("${superAdmin.role}")
    private String superAdminRole;

    @Value("${superAdmin.email}")
    private String superAdminEmail;


    @Override
    public boolean isUserIdExists(Long userId) { return userRepository.existsByUserId(userId); }

    @Override
    public boolean isEmailIdExists(String emailId) {
        return userRepository.existsByEmailId(emailId);
    }

    @Override
    public boolean isGstinExists(String gstin) {
        return organizationRepo.existsByGstin(gstin);
    }

    @Override
    public boolean isPanExists(String pan) {
        return organizationRepo.existsByPan(pan);
    }

    @Override
    public boolean isTanExists(String tan) {
        return organizationRepo.existsByTan(tan);
    }

    @Override
    public boolean isCinExists(String cin) {
        return organizationRepo.existsByCin(cin);
    }

    @Override
    public boolean isMobileNoExists(String mobileNo) {
        return userRepository.existsByMobileNo(mobileNo);
    }

    @Override
    @Transactional
    public UserDTO createuser(UserDTO userDTO) {
        LOGGER.info("Entering into the createUser method in RegistrationServiceImpl");
        UserEntity userEntity = dtotoentity(userDTO);

        if (userEntity == null) {
            LOGGER.error("User conversion from DTO failed.");
            return null;
        }

        userEntity.getAddress().setReferenceId(userEntity.getUserId());

        if (!Objects.equals(userDTO.getEmailId(), superAdminEmail)) {
            PermissionEntity permissionEntity = createDefaultPermission(userEntity);
            userEntity.setPermission(permissionEntity);
        }

        userEntity = userRepository.save(userEntity);

        LOGGER.info("Exiting the createUser method in RegistrationServiceImpl");
        return entitytodto(userEntity);
    }

    private PermissionEntity createDefaultPermission(UserEntity userEntity) {
        Long lastPermissionId = iPermissionRepo.findLastPermissionId();
        long newPermissionId = lastPermissionId != null ? lastPermissionId + 1 : 1L;

        String permissionName = "Default Permission " + newPermissionId;

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setPermissionName(permissionName);
        permissionEntity.setPermission(userEntity.getRole().getDefaultPermissions().getPermission());
        permissionEntity.setUser(userEntity);

        return iPermissionRepo.save(permissionEntity);
    }


    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+={}[]|:;<>,.?/~`";
    private static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
    private static final int PASSWORD_LENGTH = 8;

    @Override
    public String generateStrongPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        return password.toString();
    }


    @Override
    public UserDTO updateUser(UserDTO userDTO, Long userId) {
        LOGGER.info("Entering into the updateUser method in RegistrationServiceImpl");
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity existingUser = userOptional.get();

            existingUser.setFirstName(userDTO.getFirstName());
            existingUser.setLastName(userDTO.getLastName());
            existingUser.setEmailId(userDTO.getEmailId());
            existingUser.setMobileNo(userDTO.getMobileNo());

            if(userDTO.getRoleId()!= null){
                RoleEntity roleEntity = iRoleRepo.findById(userDTO.getRoleId()).orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + userDTO.getRoleId()));
                existingUser.setRole(roleEntity);
                PermissionEntity permissionEntity = existingUser.getPermission();
                permissionEntity.setPermission(roleEntity.getDefaultPermissions().getPermission());
                existingUser.setPermission(permissionEntity);
            }

            AddressEntity addressEntity = existingUser.getAddress();
            addressEntity.setAddressLine1(userDTO.getAddress().getAddressLine1());
            addressEntity.setAddressLine2(userDTO.getAddress().getAddressLine2());
            addressEntity.setCity(userDTO.getAddress().getCity());
            addressEntity.setState(userDTO.getAddress().getState());
            addressEntity.setCountry(userDTO.getAddress().getCountry());
            addressEntity.setPincode(userDTO.getAddress().getPincode());

            OrganizationEntity organizationEntity = existingUser.getOrganization();
            organizationEntity.setOrganizationName(userDTO.getOrganization().getOrganizationName());
            organizationEntity.setDisplayName(userDTO.getOrganization().getDisplayName());
            organizationEntity.setGstin(userDTO.getOrganization().getGstin());
            organizationEntity.setPan(userDTO.getOrganization().getPan());
            organizationEntity.setTan(userDTO.getOrganization().getTan());
            organizationEntity.setOrganizationType(userDTO.getOrganization().getOrganizationType());
            organizationEntity.setIncorporationDate(userDTO.getOrganization().getIncorporationDate());
            organizationEntity.setCin(userDTO.getOrganization().getCin());

            OrganizationSubscriptionEntity orgSubscriptionEntity = organizationEntity.getOrganizationSubscription();

            if(userDTO.getSubscriptionId()!= null){
                SubscriptionEntity subscriptionEntity = iSubscriptionRepo.findById(userDTO.getSubscriptionId()).orElseThrow(() -> new IllegalArgumentException("Subscription not found with ID: " + userDTO.getSubscriptionId()));
                orgSubscriptionEntity.setSubscription(subscriptionEntity);
                orgSubscriptionEntity.setSubscriptionDate(LocalDate.now());}

            existingUser = userRepository.save(existingUser);
            LOGGER.info("Exiting the updateUser method in RegistrationServiceImpl");
            return entitytodto(existingUser);
        }
        return null;
    }


    @Override
    public UserDTO getUserByUserId(Long userId) {
        LOGGER.info("Entering into the getUserByUserId method in RegistrationServiceImpl");
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        LOGGER.info("Exiting the getUserByUserId method in RegistrationServiceImpl");
        return userEntityOptional.map(this::entitytodto).orElse(null);
    }

    @Override
    public PageableResponse<List<UserDTO>> getAllUsers(int page, int size, String searchKey) {
        LOGGER.info("Fetching users with search key: {}", searchKey);

        Pageable pageable = PageRequest.of(page, size).withSort(Sort.by("userId").ascending());
        Page<UserEntity> userEntitiesPage;

        if (searchKey == null || searchKey.isEmpty()) {
            userEntitiesPage = userRepository.findAll(pageable);
        } else {
            userEntitiesPage = userRepository.searchUsers(searchKey, pageable);
        }

        List<UserDTO> userDTOs = userEntitiesPage.getContent().stream()
                .map(this::entitytodto)
                .collect(Collectors.toList());

        LOGGER.info("Fetched {} users", userDTOs.size());

        return new PageableResponse<>(
                0, null, userEntitiesPage.getNumber(), userEntitiesPage.getSize(), userDTOs,
                null, userEntitiesPage.getTotalPages(), userEntitiesPage.getTotalElements());
    }


    public UserDTO findByUsername(String username) {
        LOGGER.info("Entering into the FindByUserName in RegistrationServiceImpl Successful");

        UserEntity userEntity = userRepository.findByEmailId(username);

        if (userEntity == null) {
            LOGGER.info("User with username {} not found", username);
            return null;
        }

        UserDTO userdto = entitytodto(userEntity);

        LOGGER.info("Existing into the FindByUserName in RegistrationServiceImpl Successful");
        return userdto;
    }


    @Override
    public List<UserDTO> getUsersByRoleId(Long roleId) {
        LOGGER.info("Entering getUsersByRoleId method with roleId: {}", roleId);

        List<UserEntity> userEntities = userRepository.findAllByRoleRoleId(roleId);
        List<UserDTO> userDTOs = userEntities.stream()
                .map(this::entitytodto)
                .collect(Collectors.toList());

        LOGGER.info("Exiting getUsersByRoleId method with {} users found", userDTOs.size());
        return userDTOs;
    }


    @Override
    public void deleteUser(Long userId) {
        LOGGER.info("Entering into the deleteUser method in RoleServiceImpl");

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (user != null) {
            LoginEntity login = user.getLogin();
            AddressEntity address = user.getAddress();
            OrganizationEntity org = user.getOrganization();
            PermissionEntity permission = user.getPermission();

            if (login != null) {
                user.setLogin(null);
                userRepository.save(user);
                iLoginRepo.delete(login);
            }
            if (address != null) {
                user.setAddress(null);
                userRepository.save(user);
                addressRepo.delete(address);
            }
            if (org != null) {
                OrganizationSubscriptionEntity organizationSubscription = org.getOrganizationSubscription();
                org.setOrganizationSubscription(null);
                user.setOrganization(null);
                userRepository.save(user);
                organizationRepo.delete(org);
                if (organizationSubscription != null) {
                    organizationSubscription.setSubscription(null);
                    organizationSubscriptionRepo.delete(organizationSubscription);
                }
            }
            if (permission != null) {
                user.setPermission(null);
                userRepository.save(user);
                iPermissionRepo.delete(permission);
            }

            user.setRole(null);

            userRepository.delete(user);
        }

        LOGGER.info("Exiting the deleteUser method in RoleServiceImpl");
    }

    @Override
    public UserEntity dtotoentity(UserDTO userDTO) {
        LOGGER.info("Entering into the dtoToEntity method in RoleServiceImpl");

        UserEntity userEntity = new UserEntity();

        userEntity.setCustomerId(generateCustomerId());
        userEntity.setFirstName(userDTO.getFirstName());
        userEntity.setLastName(userDTO.getLastName());
        userEntity.setEmailId(userDTO.getEmailId());
        userEntity.setMobileNo(userDTO.getMobileNo());

        RoleEntity roleEntity = getRoleForUser(userDTO);
        if (roleEntity == null) {
            LOGGER.error("Role not found for the user.");
            return null;
        }
        userEntity.setRole(roleEntity);

        AddressEntity addressEntity = convertToAddressEntity(userDTO.getAddress());
        userEntity.setAddress(addressEntity);

        OrganizationEntity organizationEntity = convertToOrganizationEntity(userDTO);
        userEntity.setOrganization(organizationEntity);

        assert organizationEntity != null;
        organizationRepo.save(organizationEntity);
        addressRepo.save(addressEntity);
        userEntity = userRepository.save(userEntity);

        LOGGER.info("Exiting the dtoToEntity method in RoleServiceImpl");
        return userEntity;
    }

    private String generateCustomerId() {
        Long lastCustomerId = userRepository.findLastCustomerId();
        Long newCustomerId = lastCustomerId != null ? lastCustomerId + 1 : 1L;
        return String.format("%06d", newCustomerId);
    }

    private RoleEntity getRoleForUser(UserDTO userDTO) {
        String roleName = Objects.equals(userDTO.getEmailId(), superAdminEmail) ? superAdminRole : defaultRoleName;
        return iRoleRepo.findByRole(roleName);
    }

    private AddressEntity convertToAddressEntity(AddressDTO addressDTO) {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddressLine1(addressDTO.getAddressLine1());
        addressEntity.setAddressLine2(addressDTO.getAddressLine2());
        addressEntity.setCity(addressDTO.getCity());
        addressEntity.setState(addressDTO.getState());
        addressEntity.setCountry(addressDTO.getCountry());
        addressEntity.setPincode(addressDTO.getPincode());
        return addressEntity;
    }

    private OrganizationEntity convertToOrganizationEntity(UserDTO userDTO) {
        OrganizationEntity organizationEntity = new OrganizationEntity();
        OrganizationDTO organizationDTO = userDTO.getOrganization();

        organizationEntity.setOrganizationName(organizationDTO.getOrganizationName());
        organizationEntity.setDisplayName(organizationDTO.getDisplayName());
        organizationEntity.setGstin(organizationDTO.getGstin());
        organizationEntity.setPan(organizationDTO.getPan());
        organizationEntity.setTan(organizationDTO.getTan());
        organizationEntity.setOrganizationType(organizationDTO.getOrganizationType());
        organizationEntity.setIncorporationDate(organizationDTO.getIncorporationDate());
        organizationEntity.setCin(organizationDTO.getCin());

        if (!Objects.equals(userDTO.getEmailId(), superAdminEmail)) {
            OrganizationSubscriptionEntity organizationSubscriptionEntity = new OrganizationSubscriptionEntity();
            SubscriptionEntity subscriptionEntity = iSubscriptionRepo.findById(userDTO.getSubscriptionId()).orElse(null);

            if (subscriptionEntity == null) {
                LOGGER.error("Subscription not found.");
                return null;
            }

            organizationSubscriptionEntity.setSubscription(subscriptionEntity);
            organizationSubscriptionEntity.setSubscriptionDate(LocalDate.now());
            organizationEntity.setOrganizationSubscription(organizationSubscriptionEntity);
        }

        return organizationEntity;
    }




    @Override
    public UserDTO entitytodto(UserEntity userEntity) {
        LOGGER.info("Entering into the entityToDto method in RoleServiceImpl");

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userEntity.getUserId());
        userDTO.setCustomerId(userEntity.getCustomerId());
        userDTO.setFirstName(userEntity.getFirstName());
        userDTO.setLastName(userEntity.getLastName());
        userDTO.setEmailId(userEntity.getEmailId());
        userDTO.setMobileNo(userEntity.getMobileNo());

        RoleEntity roleEntity = userEntity.getRole();
        if (roleEntity != null) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setRoleId(roleEntity.getRoleId());
            roleDTO.setRole(roleEntity.getRole());
            roleDTO.setDescription(roleEntity.getDescription());

            RoleDefaultPermissionEntity roleDefaultPermissionEntity = roleEntity.getDefaultPermissions();
            if (roleDefaultPermissionEntity != null) {
                RoleDefaultPermissionDTO roleDefaultPermissionDTO = new RoleDefaultPermissionDTO();
                roleDefaultPermissionDTO.setId(roleDefaultPermissionEntity.getId());
                roleDefaultPermissionDTO.setRoleId(roleEntity.getRoleId());
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Object jsonDataAsObject = objectMapper.readValue(roleDefaultPermissionEntity.getPermission(), Object.class);
                    roleDefaultPermissionDTO.setPermission(jsonDataAsObject);
                } catch (Exception e) {
                    return null;
                }
                roleDTO.setDefaultPermission(roleDefaultPermissionDTO);
            }

            userDTO.setRole(roleDTO);
            userDTO.setRoleId(roleEntity.getRoleId());
        }


        PermissionEntity permissionEntity = userEntity.getPermission();
        if (permissionEntity != null) {
            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setPermissionId(permissionEntity.getPermissionId());
            permissionDTO.setPermissionName(permissionEntity.getPermissionName());
            permissionDTO.setUserId(userEntity.getUserId());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Object jsonDataAsObject = objectMapper.readValue(permissionEntity.getPermission(), Object.class);
                permissionDTO.setPermission(jsonDataAsObject);
            } catch (Exception e) {
                return null;
            }
            userDTO.setPermission(permissionDTO);
        }

        AddressEntity addressEntity = userEntity.getAddress();
        if (addressEntity != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setAddressId(addressEntity.getAddressId());
            addressDTO.setAddressLine1(addressEntity.getAddressLine1());
            addressDTO.setAddressLine2(addressEntity.getAddressLine2());
            addressDTO.setCity(addressEntity.getCity());
            addressDTO.setState(addressEntity.getState());
            addressDTO.setCountry(addressEntity.getCountry());
            addressDTO.setPincode(addressEntity.getPincode());
            addressDTO.setReferenceId(addressEntity.getReferenceId());
            userDTO.setAddress(addressDTO);
        }

        OrganizationEntity organizationEntity = userEntity.getOrganization();
        if (organizationEntity != null) {
            OrganizationDTO organizationDTO = new OrganizationDTO();
            organizationDTO.setOrgId(organizationEntity.getOrgId());
            organizationDTO.setOrganizationName(organizationEntity.getOrganizationName());
            organizationDTO.setDisplayName(organizationEntity.getDisplayName());
            organizationDTO.setGstin(organizationEntity.getGstin());
            organizationDTO.setPan(organizationEntity.getPan());
            organizationDTO.setTan(organizationEntity.getTan());
            organizationDTO.setOrganizationType(organizationEntity.getOrganizationType());
            organizationDTO.setIncorporationDate(organizationEntity.getIncorporationDate());
            organizationDTO.setCin(organizationEntity.getCin());

            OrganizationSubscriptionEntity organizationSubscriptionEntity = organizationEntity.getOrganizationSubscription();
            if (organizationSubscriptionEntity != null) {
                OrganizationSubscriptionDTO organizationSubscriptionDTO = new OrganizationSubscriptionDTO();
                organizationSubscriptionDTO.setOrgSubscriptionId(organizationSubscriptionEntity.getOrgSubscriptionId());

                organizationSubscriptionDTO.setSubscriptionDate(organizationSubscriptionEntity.getSubscriptionDate());

                SubscriptionEntity subscriptionEntity = organizationSubscriptionEntity.getSubscription();
                if (subscriptionEntity != null) {
                    SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
                    subscriptionDTO.setSubscriptionId(subscriptionEntity.getSubscriptionId());
                    subscriptionDTO.setSubscriptionName(subscriptionEntity.getSubscriptionName());
                    subscriptionDTO.setValidity(subscriptionEntity.getValidity());
                    subscriptionDTO.setCost(subscriptionEntity.getCost());
                    subscriptionDTO.setSubscriptionType(subscriptionEntity.getSubscriptionType());
                    subscriptionDTO.setActiveStatus(subscriptionEntity.getActiveStatus());


                    List<ServiceDTO> serviceDTOList = new ArrayList<>();
                    List<ServiceEntity> serviceEntities = subscriptionEntity.getServiceEntity();
                    for (ServiceEntity serviceEntity : serviceEntities) {
                        ServiceDTO serviceDTO = new ServiceDTO();
                        serviceDTO.setServiceId(serviceEntity.getServiceId());
                        serviceDTO.setServiceName(serviceEntity.getServiceName());
                        serviceDTO.setDescription(serviceEntity.getDescription());
                        serviceDTO.setActiveStatus(serviceEntity.getActiveStatus());

                        List<FeatureDTO> featureDTOList = new ArrayList<>();
                        List<FeatureEntity> featureEntities = serviceEntity.getFeatureEntity();
                        for (FeatureEntity featureEntity : featureEntities) {
                            FeatureDTO featureDTO = new FeatureDTO();
                            featureDTO.setFeatureId(featureEntity.getFeatureId());
                            featureDTO.setFeatureName(featureEntity.getFeatureName());
                            featureDTO.setDescription(featureEntity.getDescription());
                            featureDTO.setActiveStatus(featureEntity.getActiveStatus());
                            featureDTO.setServiceId(serviceEntity.getServiceId());
                            featureDTOList.add(featureDTO);
                        }
                        serviceDTO.setFeature(featureDTOList);
                        serviceDTO.setSubscriptionId(subscriptionEntity.getSubscriptionId());
                        serviceDTOList.add(serviceDTO);
                    }
                    subscriptionDTO.setService(serviceDTOList);
                    organizationSubscriptionDTO.setSubscription(subscriptionDTO);
                }

                organizationDTO.setOrganizationSubscription(organizationSubscriptionDTO);
                userDTO.setSubscriptionId(organizationSubscriptionEntity.getSubscription().getSubscriptionId());
            }

            userDTO.setOrganization(organizationDTO);
        }

        LOGGER.info("Exiting the entityToDto method in RoleServiceImpl");
        return userDTO;
    }


}