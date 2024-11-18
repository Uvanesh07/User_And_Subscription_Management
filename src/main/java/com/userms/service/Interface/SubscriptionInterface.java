package com.userms.service.Interface;

import com.userms.DTO.FeatureDTO;
import com.userms.DTO.ServiceDTO;
import com.userms.DTO.SubscriptionDTO;
import com.userms.entity.FeatureEntity;
import com.userms.entity.ServiceEntity;
import com.userms.entity.SubscriptionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SubscriptionInterface {

            boolean isSubscriptionIdExists(Long subscriptionId);

            boolean isServiceIdExists(Long serviceId);

            boolean isFeatureIdExists(Long featureId);

            boolean isSubscriptionNameExists(String subscriptionName);

            boolean isServiceNameExists(String serviceName);

            boolean isFeatureNameExists(String featureName);

            boolean isServicesMappingExists(Long subscriptionId, Long serviceId);

    SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO servicesMapping(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO updateSubscription(SubscriptionDTO subscriptionDTO, Long subscriptionId);

    SubscriptionDTO updateSubscriptionStatus(SubscriptionDTO subscriptionDTO, Long subscriptionId);

    SubscriptionDTO getBySubscriptionId(Long subscriptionId);

    List<SubscriptionDTO> getAllSubscription();

    List<SubscriptionDTO> getAllActiveSubscription();

    boolean deleteSubscriptionById(Long subscriptionId);

        SubscriptionEntity dtoToSubscriptionEntity(SubscriptionDTO subscriptionDTO);

        SubscriptionDTO entityToSubscriptionDto(SubscriptionEntity subscriptionEntity);

    List<ServiceDTO> getServiceBySubscriptionId(Long subscriptionId);

    ServiceDTO createService(ServiceDTO serviceDTO);

    ServiceDTO updateService(ServiceDTO serviceDTO, Long serviceId);

    ServiceDTO updateServiceStatus(ServiceDTO serviceDTO, Long serviceId);

    ServiceDTO getByServiceId(Long serviceId);

    List<ServiceDTO> getAllService();

    void deleteServiceById(Long serviceId, Long subscriptionId);

        ServiceEntity dtoToServiceEntity(ServiceDTO serviceDTO);

        ServiceDTO entityToServiceDTO(ServiceEntity serviceEntity);

    List<FeatureDTO> getAllFeature();

    FeatureDTO createFeature(FeatureDTO featureDTO);

    FeatureDTO updateFeature(FeatureDTO featureDTO, Long featureId);

    FeatureDTO updateFeatureStatus(FeatureDTO featureDTO, Long featureId);

    FeatureDTO getByFeatureId(Long featureId);

    List<FeatureDTO> getFeatureByServiceId(Long serviceId);

    void deleteFeatureById(Long featureId);

        FeatureEntity dtoToFeatureEntity(FeatureDTO featureDTO);

        FeatureDTO entityToFeatureDTO(FeatureEntity featureEntity);
}
