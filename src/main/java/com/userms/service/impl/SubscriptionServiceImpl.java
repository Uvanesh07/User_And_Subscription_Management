package com.userms.service.impl;

import com.userms.DTO.FeatureDTO;
import com.userms.DTO.ServiceDTO;
import com.userms.DTO.SubscriptionDTO;
import com.userms.entity.*;
import com.userms.repository.*;
import com.userms.service.Interface.SubscriptionInterface;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    @Autowired
    IFeatureRepo iFeatureRepo;

    @Autowired
    IServiceRepo iServiceRepo;

    @Autowired
    ISubscriptionRepo iSubscriptionRepo;

    @Autowired
    IUserRepo iUserRepo;

    @Autowired
    ISubscriptionServicesMappingRepo subscriptionServicesMappingRepo;

    private final ModelMapper modelMapper;

    @Autowired
    public SubscriptionServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean isSubscriptionIdExists(Long subscriptionId) {
        return iSubscriptionRepo.existsBySubscriptionId(subscriptionId);
    }

    @Override
    public boolean isServiceIdExists(Long serviceId) {
        return iServiceRepo.existsByServiceId(serviceId);
    }

    @Override
    public boolean isFeatureIdExists(Long featureId) {
        return iFeatureRepo.existsByFeatureId(featureId);
    }

    @Override
    public boolean isSubscriptionNameExists(String subscriptionName){
        return iSubscriptionRepo.existsBySubscriptionName(subscriptionName);
    }

    @Override
    public boolean isServiceNameExists(String serviceName){
        return iServiceRepo.existsByServiceName(serviceName);
    }

    @Override
    public boolean isFeatureNameExists(String featureName){
        return iFeatureRepo.existsByFeatureName(featureName);
    }

    @Override
    public boolean isServicesMappingExists(Long subscriptionId, Long serviceId) {
        return subscriptionServicesMappingRepo.existsBySubscriptionIdAndServiceId(subscriptionId, serviceId);
    }

    @Value("${default.activeStatus}")
    private Boolean defaultActiveStatus;

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        LOGGER.info("Entering into the createSubscription method in SubscriptionServiceImpl");
        SubscriptionEntity subscriptionEntity = dtoToSubscriptionEntity(subscriptionDTO);

        if (subscriptionDTO.getActiveStatus() == null){
            subscriptionEntity.setActiveStatus(defaultActiveStatus);
        }

        subscriptionEntity = iSubscriptionRepo.save(subscriptionEntity);
        LOGGER.info("Existing into the createSubscription method in SubscriptionServiceImpl");
        return entityToSubscriptionDto(subscriptionEntity);
    }

    @Override
    public SubscriptionDTO servicesMapping(SubscriptionDTO subscriptionDTO) {
        LOGGER.info("Entering into the servicesMapping method in SubscriptionServiceImpl");
        SubscriptionEntity subscriptionEntity = iSubscriptionRepo.findById(subscriptionDTO.getSubscriptionId()).orElse(null);

        if (subscriptionEntity != null) {
            for (ServiceDTO service : subscriptionDTO.getService()) {
                ServiceEntity serviceEntity = iServiceRepo.findById(service.getServiceId()).orElse(null);
                if (serviceEntity != null) {
                    SubscriptionServicesMapping subscriptionServicesMapping =new SubscriptionServicesMapping();
                    subscriptionServicesMapping.setSubscriptionEntity(subscriptionEntity);
                    subscriptionServicesMapping.setServiceEntity(serviceEntity);
                    subscriptionServicesMappingRepo.save(subscriptionServicesMapping);
                }
            }
        }

        Optional<SubscriptionEntity> subscriptionOptional = iSubscriptionRepo.findById(subscriptionDTO.getSubscriptionId());
        LOGGER.info("Existing into the servicesMapping method in SubscriptionServiceImpl");
        return subscriptionOptional.map(this::entityToSubscriptionDto).orElse(null);
    }

    @Override
    public SubscriptionDTO updateSubscription(SubscriptionDTO subscriptionDTO, Long subscriptionId) {
        LOGGER.info("Entering into the updateSubscription method in SubscriptionServiceImpl");
        Optional<SubscriptionEntity> optionalSubscriptionEntity = iSubscriptionRepo.findById(subscriptionId);
        if (optionalSubscriptionEntity.isEmpty()) {
            return null;
        }

        SubscriptionEntity subscriptionEntity = optionalSubscriptionEntity.get();
        if (!subscriptionEntity.getSubscriptionName().equals(subscriptionDTO.getSubscriptionName())) {
            if (isSubscriptionNameExists(subscriptionDTO.getSubscriptionName())) {
                subscriptionDTO.setSubscriptionName("true");
                return subscriptionDTO;
            }

            subscriptionEntity.setSubscriptionName(subscriptionDTO.getSubscriptionName());

        }
        subscriptionEntity.setSubscriptionType(subscriptionDTO.getSubscriptionType());
        subscriptionEntity.setValidity(subscriptionDTO.getValidity());
        subscriptionEntity.setCost(subscriptionDTO.getCost());
        subscriptionEntity.setActiveStatus(subscriptionDTO.getActiveStatus());
        subscriptionEntity = iSubscriptionRepo.save(subscriptionEntity);

        LOGGER.info("Exiting from the updateSubscription method in SubscriptionServiceImpl");
        return entityToSubscriptionDto(subscriptionEntity);
    }

    @Override
    public SubscriptionDTO updateSubscriptionStatus(SubscriptionDTO subscriptionDTO, Long subscriptionId) {

        Boolean activeStatus = subscriptionDTO.getActiveStatus();

        Optional<SubscriptionEntity> subscriptionEntityOptional = iSubscriptionRepo.findById(subscriptionId);

        if (subscriptionEntityOptional.isPresent()) {
            SubscriptionEntity subscriptionEntity = subscriptionEntityOptional.get();
            subscriptionEntity.setActiveStatus(activeStatus);
            SubscriptionEntity updatedSubscriptionEntity = iSubscriptionRepo.save(subscriptionEntity);
            return entityToSubscriptionDto(updatedSubscriptionEntity);
        }
        return null;
    }


    @Override
    public SubscriptionDTO getBySubscriptionId(Long subscriptionId) {
        LOGGER.info("Entering into the getBySubscriptionId method in SubscriptionServiceImpl.");
        Optional<SubscriptionEntity> subscriptionOptional = iSubscriptionRepo.findById(subscriptionId);
        LOGGER.info("Existing into the getBySubscriptionId method in SubscriptionServiceImpl");
        return subscriptionOptional.map(this::entityToSubscriptionDto).orElse(null);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscription() {
        LOGGER.info("Entering into the getAllSubscription method in SubscriptionServiceImpl");
        List<SubscriptionEntity> subscribe = iSubscriptionRepo.findAll();
        LOGGER.info("Existing into the getAllSubscription method in SubscriptionServiceImpl");
        return subscribe.stream().map(this::entityToSubscriptionDto).collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionDTO> getAllActiveSubscription() {
        LOGGER.info("Entering into the getAllActiveSubscription method in SubscriptionServiceImpl");
        List<SubscriptionEntity> subscribe = iSubscriptionRepo.findActiveSubscriptions(PageRequest.of(0, 3));
        LOGGER.info("Existing into the getAllActiveSubscription method in SubscriptionServiceImpl");
        return subscribe.stream().map(this::entityToSubscriptionDto).collect(Collectors.toList());
    }

    @Override
    public boolean deleteSubscriptionById(Long subscriptionId) {
        LOGGER.info("Deleting subscription with ID: {}", subscriptionId);

        SubscriptionEntity subscription = iSubscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with ID: " + subscriptionId));

        List<UserEntity> users = iUserRepo.findByOrganizationOrganizationSubscriptionSubscriptionSubscriptionId(subscriptionId);

        if (users != null && !users.isEmpty()) {
            for (UserEntity user : users) {
                if (Objects.equals(user.getOrganization().getOrganizationSubscription().getSubscription().getSubscriptionId(), subscriptionId)) {
                    LOGGER.warn("Cannot delete subscription ID {} because it is associated with one or more users.", subscriptionId);
                    return true;
                }
            }
        }

        for (ServiceEntity service : subscription.getServiceEntity()) {
            if (subscriptionServicesMappingRepo.existsDuplicateServiceId(service.getServiceId())) {
                List<SubscriptionServicesMapping> mappings = subscriptionServicesMappingRepo.findBySubscriptionEntitySubscriptionIdAndServiceEntityServiceId(subscriptionId, service.getServiceId());
                for (SubscriptionServicesMapping mapping : mappings) {
                    mapping.setSubscriptionEntity(null);
                    mapping.setServiceEntity(null);
                    subscriptionServicesMappingRepo.delete(mapping);
                }
            } else {
                for (FeatureEntity feature : service.getFeatureEntity()) {
                    feature.setServiceEntity(null);
                    iFeatureRepo.delete(feature);
                }
                service.setSubscriptionEntity(null);
                iServiceRepo.delete(service);
            }
        }

        iSubscriptionRepo.delete(subscription);
        LOGGER.info("Deleted subscription with ID: {}", subscriptionId);

        return false;
    }

    @Override
    public SubscriptionEntity dtoToSubscriptionEntity(SubscriptionDTO subscriptionDTO) {
        LOGGER.info("Entering into the dtoToSubscriptionEntity method in SubscriptionServiceImpl");
        SubscriptionEntity subscriptionEntity = modelMapper.map(subscriptionDTO, SubscriptionEntity.class);

        List<ServiceEntity> serviceEntities = new ArrayList<>();
        if (subscriptionDTO.getService() != null) {
            for (ServiceDTO serviceDTO : subscriptionDTO.getService()) {
                ServiceEntity serviceEntity = modelMapper.map(serviceDTO, ServiceEntity.class);

                List<SubscriptionEntity> subscriptionList = new ArrayList<>();
                subscriptionList.add(subscriptionEntity);
                serviceEntity.setSubscriptionEntity(subscriptionList);

                List<FeatureEntity> featureEntities = new ArrayList<>();
                if (serviceDTO.getFeature() != null) {
                    for (FeatureDTO featureDTO : serviceDTO.getFeature()) {
                        FeatureEntity featureEntity = modelMapper.map(featureDTO, FeatureEntity.class);
                        featureEntity.setServiceEntity(serviceEntity);
                        featureEntities.add(featureEntity);
                    }
                }

                serviceEntity.setFeatureEntity(featureEntities);
                serviceEntities.add(serviceEntity);
            }
        }

        subscriptionEntity.setServiceEntity(serviceEntities);
        LOGGER.info("Exiting the dtoToSubscriptionEntity method in SubscriptionServiceImpl");
        return subscriptionEntity;
    }

    @Override
    public SubscriptionDTO entityToSubscriptionDto(SubscriptionEntity subscriptionEntity) {
        LOGGER.info("Entering into the entityToSubscriptionDto method in SubscriptionServiceImpl");
        SubscriptionDTO subscriptionDTO = modelMapper.map(subscriptionEntity, SubscriptionDTO.class);

        List<ServiceDTO> serviceDTOs = new ArrayList<>();
        for (ServiceEntity serviceEntity : subscriptionEntity.getServiceEntity()) {
            ServiceDTO serviceDTO = modelMapper.map(serviceEntity, ServiceDTO.class);

            serviceDTO.setSubscriptionId(subscriptionDTO.getSubscriptionId());

            List<FeatureDTO> featureDTOs = new ArrayList<>();
            for (FeatureEntity featureEntity : serviceEntity.getFeatureEntity()) {
                FeatureDTO featureDTO = modelMapper.map(featureEntity, FeatureDTO.class);
                featureDTO.setServiceId(serviceDTO.getServiceId());
                featureDTOs.add(featureDTO);
            }

            serviceDTO.setFeature(featureDTOs);

            serviceDTOs.add(serviceDTO);
        }

        subscriptionDTO.setService(serviceDTOs);
        LOGGER.info("Exiting from the entityToSubscriptionDto method in SubscriptionServiceImpl");
        return subscriptionDTO;
    }

    @Override
    public ServiceDTO createService(ServiceDTO serviceDTO) {
        LOGGER.info("Entering into the createService method in SubscriptionServiceImpl");

        SubscriptionEntity optionalSubscription = iSubscriptionRepo.findById(serviceDTO.getSubscriptionId()).orElse(null);

        ServiceEntity serviceEntity = dtoToServiceEntity(serviceDTO);

        if (serviceDTO.getActiveStatus() == null){
            serviceEntity.setActiveStatus(defaultActiveStatus);
        }

        serviceEntity = iServiceRepo.save(serviceEntity);

        SubscriptionServicesMapping subscriptionServicesMapping = new SubscriptionServicesMapping();
        subscriptionServicesMapping.setSubscriptionEntity(optionalSubscription);
        subscriptionServicesMapping.setServiceEntity(serviceEntity);

        subscriptionServicesMapping = subscriptionServicesMappingRepo.save(subscriptionServicesMapping);

        ServiceEntity service = iServiceRepo.findById(subscriptionServicesMapping.getServiceEntity().getServiceId()).orElse(null);

        assert service != null;
        ServiceDTO serviceDto = entityToServiceDTO(service);
        serviceDto.setSubscriptionId(serviceDTO.getSubscriptionId());
        LOGGER.info("Exiting from the createService method in SubscriptionServiceImpl");
        return serviceDto;
    }



    @Override
    public ServiceDTO updateService(ServiceDTO serviceDTO, Long serviceId) {
        LOGGER.info("Entering into the updateService method in SubscriptionServiceImpl");
        Optional<ServiceEntity> optionalService = iServiceRepo.findById(serviceId);
        if (optionalService.isEmpty()) {
            return null;
        }

        ServiceEntity serviceEntity = optionalService.get();
        if (!serviceEntity.getServiceName().equals(serviceDTO.getServiceName())) {
            if (isServiceNameExists(serviceDTO.getServiceName())) {
                serviceDTO.setServiceName("true");
                return serviceDTO;
            }

            serviceEntity.setServiceName(serviceDTO.getServiceName());

        }
        serviceEntity.setDescription(serviceDTO.getDescription());
        serviceEntity.setActiveStatus(serviceDTO.getActiveStatus());
        serviceEntity = iServiceRepo.save(serviceEntity);

        LOGGER.info("Exiting from the updateService method in SubscriptionServiceImpl");
        return entityToServiceDTO(serviceEntity);
    }

    @Override
    public ServiceDTO updateServiceStatus(ServiceDTO serviceDTO, Long serviceId) {
        LOGGER.info("Entering into the updateServiceStatus method in SubscriptionServiceImpl");

        Boolean serviceStatus = serviceDTO.getActiveStatus();

        Optional<ServiceEntity> serviceEntityOptional = iServiceRepo.findById(serviceId);
        LOGGER.info("Exiting from the updateServiceStatus method in SubscriptionServiceImpl");

        if (serviceEntityOptional.isPresent()) {
            ServiceEntity serviceEntity = serviceEntityOptional.get();
            serviceEntity.setActiveStatus(serviceStatus);
            ServiceEntity updatedServiceEntity = iServiceRepo.save(serviceEntity);
            return entityToServiceDTO(updatedServiceEntity);
        }
        return null;
    }


    @Override
    public ServiceDTO getByServiceId(Long serviceId) {
        LOGGER.info("Entering into the getByServiceId method in SubscriptionServiceImpl.");
        Optional<ServiceEntity> serviceOptional = iServiceRepo.findById(serviceId);

        if (serviceOptional.isPresent()) {
            ServiceEntity serviceEntity = serviceOptional.get();
            ServiceDTO serviceDTO = entityToServiceDTO(serviceEntity);
            LOGGER.info("Successfully found service by ID");
            return serviceDTO;
        } else {
            LOGGER.error("Service not found for ID ");
            throw new RuntimeException("Service not found for ID: " + serviceId);
        }
    }

    @Override
    public List<ServiceDTO> getAllService() {
        LOGGER.info("Entering into the getAllService method in SubscriptionServiceImpl");
        List<ServiceEntity> serviceList = iServiceRepo.findAll();
        List<ServiceDTO> serviceDTOList = new ArrayList<>();

        for (ServiceEntity serviceEntity : serviceList) {
            ServiceDTO serviceDTO = entityToServiceDTO(serviceEntity);
            serviceDTOList.add(serviceDTO);
        }
        LOGGER.info("Existing into the getAllService method in SubscriptionServiceImpl");
        return serviceDTOList;
    }

    @Override
    public List<ServiceDTO> getServiceBySubscriptionId(Long subscriptionId) {
        LOGGER.info("Entering into the getBySubscriptionId method in SubscriptionServiceImpl");
        Optional<SubscriptionEntity> subscriptionOptional = iSubscriptionRepo.findById(subscriptionId);

        if (subscriptionOptional.isPresent()) {
            SubscriptionEntity subscriptionEntity = subscriptionOptional.get();
            List<ServiceEntity> serviceEntities = subscriptionEntity.getServiceEntity();
            List<ServiceDTO> serviceDTOList = new ArrayList<>();

            for (ServiceEntity serviceEntity : serviceEntities) {
                ServiceDTO serviceDTO = entityToServiceDTO(serviceEntity);
                serviceDTOList.add(serviceDTO);
            }

            LOGGER.info("Successfully found services by Subscription ID");
            return serviceDTOList;
        } else {
            LOGGER.error("Subscription not found for ID ");
            throw new RuntimeException("Subscription not found for ID: " + subscriptionId);
        }
    }

    @Override
    public void deleteServiceById(Long serviceId, Long subscriptionId) {
        LOGGER.info("Deleting service with ID: {}", serviceId);

        ServiceEntity service = iServiceRepo.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + serviceId));

        SubscriptionEntity subscription = iSubscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with ID: " + subscriptionId));

        if (subscriptionServicesMappingRepo.existsDuplicateServiceId(service.getServiceId())) {
            List<SubscriptionServicesMapping> mappings = subscriptionServicesMappingRepo.findBySubscriptionEntitySubscriptionIdAndServiceEntityServiceId(subscription.getSubscriptionId(), service.getServiceId());
            for (SubscriptionServicesMapping mapping : mappings) {
                mapping.setSubscriptionEntity(null);
                mapping.setServiceEntity(null);
                subscriptionServicesMappingRepo.delete(mapping);
            }
        } else {
            for (FeatureEntity feature : service.getFeatureEntity()) {
                feature.setServiceEntity(null);
                iFeatureRepo.delete(feature);
            }
            service.setSubscriptionEntity(null);
            iServiceRepo.delete(service);
        }
    }


    public ServiceDTO entityToServiceDTO(ServiceEntity service) {
        LOGGER.info("Entering into the entityToServiceDTO method in SubscriptionServiceImpl");
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setServiceId(service.getServiceId());
        serviceDTO.setServiceName(service.getServiceName());
        serviceDTO.setDescription(service.getDescription());
        serviceDTO.setActiveStatus(service.getActiveStatus());

        if (!service.getSubscriptionEntity().isEmpty()) {
            serviceDTO.setSubscriptionId(service.getSubscriptionEntity().get(0).getSubscriptionId());
        }

        List<FeatureDTO> featureDTOList = new ArrayList<>();
        for (FeatureEntity featureEntity : service.getFeatureEntity()) {
            featureDTOList.add(entityToFeatureDTO(featureEntity));
        }
        serviceDTO.setFeature(featureDTOList);

        LOGGER.info("Exiting from the entityToServiceDTO method in SubscriptionServiceImpl");
        return serviceDTO;
    }


    public ServiceEntity dtoToServiceEntity(ServiceDTO serviceDTO) {
        LOGGER.info("Entering into the dtoToServiceEntity method in SubscriptionServiceImpl");
        ServiceEntity service = new ServiceEntity();
        service.setServiceId(serviceDTO.getServiceId());
        service.setServiceName(serviceDTO.getServiceName());
        service.setActiveStatus(serviceDTO.getActiveStatus());
        service.setDescription(serviceDTO.getDescription());

        List<FeatureEntity> featureEntityList = new ArrayList<>();
        if (serviceDTO.getFeature() != null) {
            for (FeatureDTO featureDTO : serviceDTO.getFeature()) {
                featureEntityList.add(dtoToFeatureEntity(featureDTO));
            }
        }
        service.setFeatureEntity(featureEntityList);
        LOGGER.info("Existing into the dtoToServiceEntity method in SubscriptionServiceImpl");
        return service;
    }

    @Override
    public FeatureDTO createFeature(FeatureDTO featureDTO) {
        LOGGER.info("Entering into the createFeature method in SubscriptionServiceImpl");
        Optional<ServiceEntity> optionalService = iServiceRepo.findById(featureDTO.getServiceId());
        if (optionalService.isEmpty()) {

            return null;
        }

        FeatureEntity featureEntity = dtoToFeatureEntity(featureDTO);
        featureEntity.setServiceEntity(optionalService.get());

        if (featureDTO.getActiveStatus() == null){
            featureEntity.setActiveStatus(defaultActiveStatus);
        }

        featureEntity = iFeatureRepo.save(featureEntity);

        LOGGER.info("Existing into the createFeature method in SubscriptionServiceImpl");
        return entityToFeatureDTO(featureEntity);
    }

    @Override
    public FeatureDTO updateFeature(FeatureDTO featureDTO, Long featureId) {
        LOGGER.info("Entering into the updateFeature method in SubscriptionServiceImpl");
        Optional<FeatureEntity> optionalFeature = iFeatureRepo.findById(featureId);
        if (optionalFeature.isEmpty()) {
            return null;
        }

        FeatureEntity featureEntity = optionalFeature.get();

        if (!featureEntity.getFeatureName().equals(featureDTO.getFeatureName())) {
            if (isFeatureNameExists(featureDTO.getFeatureName())) {
                featureDTO.setFeatureName("true");
                return featureDTO;
            }

            featureEntity.setFeatureName(featureDTO.getFeatureName());

        }
        featureEntity.setDescription(featureDTO.getDescription());
        featureEntity.setActiveStatus(featureDTO.getActiveStatus());
        featureEntity = iFeatureRepo.save(featureEntity);

        LOGGER.info("Existing into the updateFeature method in SubscriptionServiceImpl");
        return entityToFeatureDTO(featureEntity);
    }

    @Override
    public FeatureDTO updateFeatureStatus(FeatureDTO featureDTO, Long featureId) {
        LOGGER.info("Entering into the updateFeatureStatus method in SubscriptionServiceImpl");
        Boolean featureStatus = featureDTO.getActiveStatus();

        Optional<FeatureEntity> featureEntityOptional = iFeatureRepo.findById(featureId);
        LOGGER.info("Existing into the updateFeatureStatus method in SubscriptionServiceImpl");

        if (featureEntityOptional.isPresent()) {
            FeatureEntity featureEntity = featureEntityOptional.get();
            featureEntity.setActiveStatus(featureStatus);
            FeatureEntity updatedFeatureEntity = iFeatureRepo.save(featureEntity);
            return entityToFeatureDTO(updatedFeatureEntity);
        }
        return null;
    }

    @Override
    public FeatureDTO getByFeatureId(Long featureId) {
        LOGGER.info("Entering into the getByFeatureId method in SubscriptionServiceImpl");
        Optional<FeatureEntity> featureOptional = iFeatureRepo.findById(featureId);

        if (featureOptional.isPresent()) {
            FeatureEntity featureEntity = featureOptional.get();
            FeatureDTO featureDTO = entityToFeatureDTO(featureEntity);
            LOGGER.info("Successfully found feature by ID");
            return featureDTO;
        } else {
            LOGGER.error("Feature not found for ID");
            throw new RuntimeException("Feature not found for ID: " + featureId);
        }
    }

    @Override
    public List<FeatureDTO> getAllFeature() {
        LOGGER.info("Entering into the getAllFeature method in SubscriptionServiceImpl");
        List<FeatureEntity> featureEntities = iFeatureRepo.findAll();
        List<FeatureDTO> featureDTOs = new ArrayList<>();

        for (FeatureEntity featureEntity : featureEntities) {
            FeatureDTO featureDTO = entityToFeatureDTO(featureEntity);
            featureDTOs.add(featureDTO);
        }

        LOGGER.info("Existing into the getAllFeature method in SubscriptionServiceImpl");
        return featureDTOs;
    }

    @Override
    public List<FeatureDTO> getFeatureByServiceId(Long serviceId) {
        LOGGER.info("Entering into the getByServiceId method in SubscriptionServiceImpl");
        Optional<ServiceEntity> serviceOptional = iServiceRepo.findById(serviceId);

        if (serviceOptional.isPresent()) {
            ServiceEntity serviceEntity = serviceOptional.get();
            List<FeatureEntity> featureEntities = serviceEntity.getFeatureEntity();
            List<FeatureDTO> featureDTOList = new ArrayList<>();

            for (FeatureEntity featureEntity : featureEntities) {
                FeatureDTO featureDTO = entityToFeatureDTO(featureEntity);
                featureDTOList.add(featureDTO);
            }

            LOGGER.info("Successfully found services by Service ID");
            return featureDTOList;
        } else {
            LOGGER.error("Service not found for ID");
            throw new RuntimeException("Service not found for ID: " + serviceId);
        }
    }

    @Override
    public void deleteFeatureById(Long featureId) {
        LOGGER.info("Deleting feature with ID: {}", featureId);

        FeatureEntity feature = iFeatureRepo.findById(featureId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId));

        feature.setServiceEntity(null);
        iFeatureRepo.delete(feature);
        LOGGER.info("Deleted feature with ID: {}", featureId);
    }



    @Override
    public FeatureDTO entityToFeatureDTO(FeatureEntity feature) {
        LOGGER.info("Entering into the entityToFeatureDTO method in SubscriptionServiceImpl");
        FeatureDTO featureDTO = new FeatureDTO();
        featureDTO.setFeatureId(feature.getFeatureId());
        featureDTO.setFeatureName(feature.getFeatureName());
        featureDTO.setDescription(feature.getDescription());
        featureDTO.setActiveStatus(feature.getActiveStatus());
        featureDTO.setServiceId(feature.getServiceEntity().getServiceId());
        LOGGER.info("Existing into the entityToFeatureDTO method in SubscriptionServiceImpl");
        return featureDTO;
    }

    @Override
    public FeatureEntity dtoToFeatureEntity(FeatureDTO featureDTO) {
        LOGGER.info("Entering into the dtoToFeatureEntity method in SubscriptionServiceImpl");
        FeatureEntity feature = new FeatureEntity();
        feature.setFeatureId(featureDTO.getFeatureId());
        feature.setFeatureName(featureDTO.getFeatureName());
        feature.setDescription(featureDTO.getDescription());
        feature.setActiveStatus(featureDTO.getActiveStatus());
        LOGGER.info("Existing into the dtoToFeatureEntity method in SubscriptionServiceImpl");
        return feature;
    }

}
