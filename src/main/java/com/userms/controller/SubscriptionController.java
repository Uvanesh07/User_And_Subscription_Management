package com.userms.controller;

import com.userms.DTO.*;
import com.userms.service.Interface.SubscriptionInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/api/subscription")
@Api(value = "Subscription Related operations", tags = "Subscription Management")
public class SubscriptionController {
    private final Logger LOGGER = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    SubscriptionInterface subscriptionInterface;

    @PostMapping
    @ApiOperation(value = "Create a new Subscription", notes = "This endpoint allows admin to create subscription with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Subscription Created successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO createSubscription(@RequestBody SubscriptionDTO subscriptionDTO) {
        if (subscriptionInterface.isSubscriptionNameExists(subscriptionDTO.getSubscriptionName())) {
            return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(), null, subscriptionDTO.getSubscriptionName() + " " + StatusConstant.EXISTS);
        }else {
            try {
                LOGGER.info("Entering CreateSubscription Successfully");
                SubscriptionDTO createdSubscription = subscriptionInterface.createSubscription(subscriptionDTO);
                LOGGER.info("Existing CreateSubscription successfully");

                return new ResponseBO(org.apache.http.HttpStatus.SC_CREATED,HttpStatus.CREATED.getReasonPhrase(), createdSubscription, StatusConstant.CREATED);
            }catch (Exception ex) {
                LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
                return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/servicesMapping")
    @ApiOperation(value = "Add service mapping", notes = "This endpoint allows admins to add service mapping within the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service mapping added successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Subscription or Service Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO servicesMapping(@RequestBody SubscriptionDTO subscriptionDTO) {
        try {

            if (subscriptionInterface.isSubscriptionIdExists(subscriptionDTO.getSubscriptionId())) {
                LOGGER.info("Subscription Id {} exists", subscriptionDTO.getSubscriptionId());
                LOGGER.info("Entering Update Feature Successfully");
                for (ServiceDTO serviceDTO : subscriptionDTO.getService()) {
                    if (!subscriptionInterface.isServiceIdExists(serviceDTO.getServiceId())) {
                        return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Service Id" + " " + serviceDTO.getServiceId() + " " + StatusConstant.NOT_FOUND);
                    }
                }
                for (ServiceDTO serviceDTO : subscriptionDTO.getService()) {
                    if(subscriptionInterface.isServicesMappingExists(subscriptionDTO.getSubscriptionId(), serviceDTO.getServiceId())){
                        return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(), null,  serviceDTO.getServiceName()+ " "+StatusConstant.EXISTS);
                    }
                }
                SubscriptionDTO updatedSubscription = subscriptionInterface.servicesMapping(subscriptionDTO);
                LOGGER.info("Exiting Update Feature Successfully");
                if (updatedSubscription != null) {
                    return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), updatedSubscription,  "Service Added Successfully");
                }
            } else {
                LOGGER.warn("Subscription Id {} Not Found in servicesMapping", subscriptionDTO.getSubscriptionId());
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Subscription Id" + " " + subscriptionDTO.getSubscriptionId() + " " + StatusConstant.NOT_FOUND);
            }

        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
        return new ResponseBO(org.apache.http.HttpStatus.SC_BAD_REQUEST,HttpStatus.BAD_REQUEST.getReasonPhrase(), null, StatusConstant.BAD_REQUEST);
    }

    @PutMapping("/{subscriptionId}")
    @ApiOperation(value = "Update a existing Subscription", notes = "This endpoint allows admin to update subscription with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscription Updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Id does Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updateSubscription(@RequestBody SubscriptionDTO subscriptionDTO, @PathVariable Long subscriptionId) {
        try {
            String name = subscriptionDTO.getSubscriptionName();
            LOGGER.info("Entering updateSubscription successfully");
            SubscriptionDTO updatedSubscription = subscriptionInterface.updateSubscription(subscriptionDTO, subscriptionId);
            LOGGER.info("Exiting updateSubscription successfully");
            if (updatedSubscription == null) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Subscription Id" + " " + subscriptionId + " " + StatusConstant.NOT_FOUND);
            }else if (Objects.equals(updatedSubscription.getSubscriptionName(), "true")) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(), null,  name +" " + StatusConstant.EXISTS);
            }
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), updatedSubscription,  StatusConstant.UPDATED);
        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/changeStatus/{subscriptionId}")
    @ApiOperation(value = "Update the subscription status", notes = "This endpoint allows admin to update the status of a subscription.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscription status updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Subscription Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updateSubscriptionStatus(@RequestBody SubscriptionDTO subscriptionDTO, @PathVariable Long subscriptionId) {
        try {
            LOGGER.info("Entering updateSubscriptionStatus successfully");
            SubscriptionDTO updatedSubscription = subscriptionInterface.updateSubscriptionStatus(subscriptionDTO, subscriptionId);
            LOGGER.info("Exiting updateSubscriptionStatus successfully");
            if (updatedSubscription != null) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), updatedSubscription,  StatusConstant.UPDATED);
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Subscription Id" + " " + subscriptionId + " " + StatusConstant.NOT_FOUND);
            }
        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{subscriptionId}")
    @ApiOperation(value = "Get Service details By Subscription Id", notes = "This endpoint allows admin to get a subscription details by Id with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get a subscription by Id Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "SubscriptionId Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getSubscriptionById(@PathVariable Long subscriptionId) {
        if (subscriptionInterface.isSubscriptionIdExists(subscriptionId)) {
            try {
                LOGGER.info("Entering to the [getSubscriptionById] successfully");
                SubscriptionDTO subscription = subscriptionInterface.getBySubscriptionId(subscriptionId);
                LOGGER.info("Exiting to the [getSubscriptionById] successfully");

                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), subscription,  StatusConstant.GET);
            }  catch (Exception ex) {
                LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
                return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.warn("Subscription Id {} Not Found in getSubscriptionById", subscriptionId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Subscription Id" + " " + subscriptionId + " " + StatusConstant.NOT_FOUND);
        }
    }

    @GetMapping("/getAll")
    @ApiOperation(value = "Get all Subscription details", notes = "This endpoint allows admin to get All subscription details with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get All Subscription successfully"),
            @ApiResponse(code = 204, message = "Subscriptions Not Found"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getAllSubscription() {
        try {
            LOGGER.info("Entering GetAllSubscription Successfully");
            List<SubscriptionDTO> subscribe = subscriptionInterface.getAllSubscription();
            LOGGER.info("Exiting GetAllSubscription Successfully");

            if (!subscribe.isEmpty()) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), subscribe,  subscribe.size() + " " + StatusConstant.GET_LIST);
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NO_CONTENT,HttpStatus.NO_CONTENT.getReasonPhrase(), null, StatusConstant.NO_CONTENT);
            }

        }  catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllActive")
    @ApiOperation(value = "Get all Subscription details", notes = "This endpoint allows admin to get All subscription details with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get All Subscription successfully"),
            @ApiResponse(code = 204, message = "Subscriptions Not Found"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getAllActiveSubscription() {
        try {
            LOGGER.info("Entering getAllActiveSubscription Successfully");
            List<SubscriptionDTO> subscribe = subscriptionInterface.getAllActiveSubscription();
            LOGGER.info("Exiting getAllActiveSubscription Successfully");

            if (!subscribe.isEmpty()) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), subscribe,  subscribe.size() + " " + StatusConstant.GET_LIST);
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NO_CONTENT,HttpStatus.NO_CONTENT.getReasonPhrase(), null, StatusConstant.NO_CONTENT);
            }

        }  catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{subscriptionId}")
    @ApiOperation(value = "Delete Subscription By Id", notes = "This endpoint allows admin to delete subscription by Id with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscription Deleted Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "SubscriptionId Not Found"),
            @ApiResponse(code = 409, message = "Role merge by some users, You can't delete!"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO deleteSubscriptionById(@PathVariable Long subscriptionId) {
        LOGGER.info("Deleting subscription with Id: {}", subscriptionId);
        SubscriptionDTO subscriptionDTO = subscriptionInterface.getBySubscriptionId(subscriptionId);

        try {
            boolean delete = subscriptionInterface.deleteSubscriptionById(subscriptionId);
            if (delete) {
                LOGGER.warn("Subscription merge by some users, You can't delete!");
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(), null,  subscriptionDTO.getSubscriptionName()+" "+"Subscription"+" "+StatusConstant.MERGE);
            }
            LOGGER.info("Deleted subscription with Id: {}", subscriptionId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), null,  StatusConstant.DELETED);

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Subscription Id {} Not Found in deleteSubscriptionById", subscriptionId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Subscription Id" + " " + subscriptionId + " " + StatusConstant.NOT_FOUND);
        } catch (Exception ex) {
            LOGGER.error("Error deleting subscription Id {}: {}", subscriptionId, ex.getMessage());
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }





    @PostMapping("/service")
    @ApiOperation(value = "Create a new service", notes = "This endpoint allows admin to create service with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Service Created successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Subscription Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO createService(@RequestBody ServiceDTO serviceDTO) {
        if (subscriptionInterface.isServiceNameExists(serviceDTO.getServiceName())) {
            return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(), null,  serviceDTO.getServiceName() + " " + StatusConstant.EXISTS);
        }else if (subscriptionInterface.isSubscriptionIdExists(serviceDTO.getSubscriptionId())) {
            try {
                LOGGER.info("CreateService Entering Successfully");
                ServiceDTO createdService = subscriptionInterface.createService(serviceDTO);
                LOGGER.info("CreateService Exiting Successfully");
                return new ResponseBO(org.apache.http.HttpStatus.SC_CREATED,HttpStatus.CREATED.getReasonPhrase(), createdService, StatusConstant.CREATED);
            }catch (Exception ex) {
                LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
                return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Subscription Id" + " " + serviceDTO.getSubscriptionId() + " " + StatusConstant.NOT_FOUND);
        }
    }

    @PutMapping("/service/{serviceId}")
    @ApiOperation(value = "Update a existing service", notes = "This endpoint allows admin to update service with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service Updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Service Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updateService(@RequestBody ServiceDTO serviceDTO, @PathVariable Long serviceId) {
        try {
            LOGGER.info("Entering to the [updateService] Successfully");
            ServiceDTO updatedService = subscriptionInterface.updateService(serviceDTO, serviceId);
            LOGGER.info("Exiting to the [updateService] Successfully");
            if (updatedService == null) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Service Id" + " " + serviceId + " " + StatusConstant.NOT_FOUND);
            } else if (Objects.equals(updatedService.getServiceName(), "true")) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(), null,  serviceDTO.getServiceName() + " " + StatusConstant.EXISTS);
            }
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), updatedService,  StatusConstant.UPDATED);

        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/service/changeStatus/{serviceId}")
    @ApiOperation(value = "Update the service status", notes = "This endpoint allows admin to update the status of a service.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service status updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Service Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updateServiceStatus(@RequestBody ServiceDTO serviceDTO, @PathVariable Long serviceId) {
        try {
            LOGGER.info("Entering updateServiceStatus Successfully");
            ServiceDTO updatedService = subscriptionInterface.updateServiceStatus(serviceDTO, serviceId);
            LOGGER.info("Exiting updateServiceStatus Successfully");
            if (updatedService != null) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), updatedService,  StatusConstant.UPDATED);
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Service Id" + " " + serviceId + " " + StatusConstant.NOT_FOUND);
            }
        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/service/{serviceId}")
    @ApiOperation(value = "Get All details of service by Id", notes = "This endpoint allows admin to get All Service Details by Id with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service Details Get successfully"),
            @ApiResponse(code = 404, message = "Service Id Not Found"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO serviceGetById(@PathVariable Long serviceId) {
        if (subscriptionInterface.isServiceIdExists(serviceId)) {
            try {
                LOGGER.info("Entering ServiceGetById Successfully");
                ServiceDTO serviceDetails = subscriptionInterface.getByServiceId(serviceId);
                LOGGER.info("Exiting ServiceGetById Successfully");
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), serviceDetails, StatusConstant.GET);
            } catch (Exception ex) {
                LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
                return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.warn("Service Id {} Not Found in serviceGetById", serviceId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Service Id" + " " + serviceId + " " + StatusConstant.NOT_FOUND);
        }
    }

    @GetMapping("/service/getAll")
    @ApiOperation(value = "Get all service details", notes = "This endpoint allows admin to get all service details with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get All Services successfully"),
            @ApiResponse(code = 204, message = "Services Not Found"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getAllService() {
        try {
            LOGGER.info("Entering GetAllService Successfully");
            List<ServiceDTO> services = subscriptionInterface.getAllService();
            LOGGER.info("Exiting GetAllService Successfully");

            if (!services.isEmpty()) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), services,  services.size() + " " + StatusConstant.GET_LIST);
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NO_CONTENT,HttpStatus.NO_CONTENT.getReasonPhrase(), null, StatusConstant.NO_CONTENT);
            }
        }  catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/service/subscription/{subscriptionId}")
    @ApiOperation(value = "Get service details by subscription Id", notes = "This endpoint allows admin to get service details by subscription Id with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service Details Get successfully"),
            @ApiResponse(code = 404, message = "Subscription Id Not Found"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO serviceGetBySubscriptionId(@PathVariable Long subscriptionId) {
        if (subscriptionInterface.isSubscriptionIdExists(subscriptionId)) {
            try {
                LOGGER.info("ServiceGetBySubscriptionId Entering Successfully");
                List<ServiceDTO> serviceDetails = subscriptionInterface.getServiceBySubscriptionId(subscriptionId);
                LOGGER.info("ServiceGetBySubscriptionId Exiting Successfully");
                if (serviceDetails.isEmpty()){
                    return new ResponseBO(org.apache.http.HttpStatus.SC_NO_CONTENT,HttpStatus.NO_CONTENT.getReasonPhrase(), null, StatusConstant.NO_CONTENT);
                }
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), serviceDetails,  serviceDetails.size() + " " + StatusConstant.GET_LIST);
            } catch (Exception ex) {
                LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
                return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.warn("Subscription Id {} Not Found", subscriptionId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Subscription Id" + " " + subscriptionId + " " + StatusConstant.NOT_FOUND);
        }
    }

    @DeleteMapping("/service")
    @ApiOperation(value = "Delete Service By Id", notes = "This endpoint allows admin to delete service by Id with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service Deleted Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Service Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO deleteServiceById(@RequestParam Long serviceId, @RequestParam Long subscriptionId) {
        LOGGER.info("Deleting service with Id: {}", serviceId);
        try {
            subscriptionInterface.deleteServiceById(serviceId, subscriptionId);
                LOGGER.info("Deleted service with Id: {}", serviceId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), null,  StatusConstant.DELETED);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Service Id {} Not Found", serviceId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Service Id" + " " + serviceId + " " + StatusConstant.NOT_FOUND);
        } catch (Exception ex) {
            LOGGER.error("Error deleting service Id {}: {}", serviceId, ex.getMessage());
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }



    @PostMapping("/service/feature")
    @ApiOperation(value = "Create a new feature", notes = "This endpoint allows admin to create feature with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Feature Created successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Service Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO createFeature(@RequestBody FeatureDTO featureDTO) {
        if (subscriptionInterface.isFeatureNameExists(featureDTO.getFeatureName())) {
            return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(), null,  featureDTO.getFeatureName() + " " + StatusConstant.EXISTS);
        }else if (subscriptionInterface.isServiceIdExists(featureDTO.getServiceId())) {
            try {
                LOGGER.info("Entering to the [createFeature] Successfully");
                FeatureDTO createdFeature = subscriptionInterface.createFeature(featureDTO);
                LOGGER.info("Exiting to the [createFeature] Successfully");
                return new ResponseBO(org.apache.http.HttpStatus.SC_CREATED,HttpStatus.CREATED.getReasonPhrase(), createdFeature, StatusConstant.CREATED);
            } catch (Exception ex) {
                LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
                return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Service Id" + " " + featureDTO.getServiceId() + " " + StatusConstant.NOT_FOUND);
        }
    }

    @PutMapping("/service/feature/{featureId}")
    @ApiOperation(value = "Update a existing Feature", notes = "This endpoint allows admin to update feature with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Feature Updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Feature Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updateFeature(@RequestBody FeatureDTO featureDTO, @PathVariable Long featureId) {
        try {
            LOGGER.info("Update Feature Entering Successfully");
            FeatureDTO updatedFeature = subscriptionInterface.updateFeature(featureDTO, featureId);
            LOGGER.info("Update Feature Exiting Successfully");
            if (updatedFeature == null) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Feature Id" + " " + featureId + " " + StatusConstant.NOT_FOUND);
            } else if (Objects.equals(updatedFeature.getFeatureName(), "true")) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_CONFLICT,HttpStatus.CONFLICT.getReasonPhrase(), null,  featureDTO.getFeatureName() + " " + StatusConstant.EXISTS);
            }
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), updatedFeature,  StatusConstant.UPDATED);
        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/service/feature/changeStatus/{featureId}")
    @ApiOperation(value = "Update the status of a feature", notes = "This endpoint allows admin to update the status of a feature.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Feature status updated successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Feature Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO updateFeatureStatus(@RequestBody FeatureDTO featureDTO, @PathVariable Long featureId) {
        try {
            LOGGER.info("Entering updateFeatureStatus Successfully");
            FeatureDTO updatedFeature = subscriptionInterface.updateFeatureStatus(featureDTO, featureId);
            LOGGER.info("Exiting updateFeatureStatus Successfully");
            if (updatedFeature != null) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), updatedFeature,  StatusConstant.UPDATED);
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Feature Id" + " " + featureId + " " + StatusConstant.NOT_FOUND);
            }
        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/service/feature/{featureId}")
    @ApiOperation(value = "Get all feature details by Id", notes = "This endpoint allows admin to get all feature details by Id with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Feature Details Get successfully"),
            @ApiResponse(code = 404, message = "Feature Id Not Found"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getFeatureById(@PathVariable Long featureId) {
        if (subscriptionInterface.isFeatureIdExists(featureId)) {
            try {
                LOGGER.info("Entering to the [getFeatureById] Successfully");
                FeatureDTO feature = subscriptionInterface.getByFeatureId(featureId);
                LOGGER.info("Exiting to the [getFeatureById] Successfully");

                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), feature,  StatusConstant.GET);
            }  catch (Exception ex) {
                LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
                return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.warn("Feature Id {} Not Found in getFeatureById", featureId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Feature Id" + " " + featureId + " " + StatusConstant.NOT_FOUND);
        }
    }

    @GetMapping("/service/feature/getAll")
    @ApiOperation(value = "Get all Feature details", notes = "This endpoint get all feature details.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Feature Details Get All successfully"),
            @ApiResponse(code = 204, message = "No features Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO getAllFeatures() {
        try {
            LOGGER.info("FeatureGetAll Entering Successfully");
            List<FeatureDTO> features = subscriptionInterface.getAllFeature();
            LOGGER.info("FeatureGetAll Exiting successfully");

            if (!features.isEmpty()) {
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), features, features.size() + " " + StatusConstant.GET_LIST);
            } else {
                return new ResponseBO(org.apache.http.HttpStatus.SC_NO_CONTENT,HttpStatus.NO_CONTENT.getReasonPhrase(), null, StatusConstant.NO_CONTENT);
            }
        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/service/feature/service/{serviceId}")
    @ApiOperation(value = "Get feature details by service Id", notes = "This endpoint allows admin to get feature details by service Id with the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Feature Details Get successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Service Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO featureGetByServiceId(@PathVariable Long serviceId) {
        if (subscriptionInterface.isServiceIdExists(serviceId)) {
            try {
                LOGGER.info("featureGetBySubscriptionId Entering Successfully");
                List<FeatureDTO> featureDetails = subscriptionInterface.getFeatureByServiceId(serviceId);
                if (featureDetails.isEmpty()){
                    return new ResponseBO(org.apache.http.HttpStatus.SC_NO_CONTENT,HttpStatus.NO_CONTENT.getReasonPhrase(), null, StatusConstant.NO_CONTENT);
                }
                LOGGER.info("featureGetBySubscriptionId Exiting Successfully");
                return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), featureDetails, featureDetails.size() + " " + StatusConstant.GET_LIST);

            } catch (Exception ex) {
                LOGGER.error(ExceptionUtils.getFullStackTrace(ex));
                return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Service Id" + " " + serviceId + " " + StatusConstant.NOT_FOUND);
        }
    }

    @DeleteMapping("/service/feature/{featureId}")
    @ApiOperation(value = "Delete Feature By Id", notes = "This endpoint allows admin to delete a feature by its Id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Feature Deleted Successfully"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 404, message = "Feature Id Not Found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseBO deleteFeatureById(@PathVariable Long featureId) {
        LOGGER.info("Deleting feature with Id: {}", featureId);

        try {
            subscriptionInterface.deleteFeatureById(featureId);
            LOGGER.info("Deleted feature with Id: {}", featureId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_OK,HttpStatus.OK.getReasonPhrase(), null,  StatusConstant.DELETED);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Feature Id {} Not Found", featureId);
            return new ResponseBO(org.apache.http.HttpStatus.SC_NOT_FOUND,HttpStatus.NOT_FOUND.getReasonPhrase(), null, "Feature Id" + " " + featureId + " " + StatusConstant.NOT_FOUND);
        } catch (Exception ex) {
            LOGGER.error("Error deleting feature Id {}: {}", featureId, ex.getMessage());
            return new ResponseBO(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, StatusConstant.INTERNAL_SERVER_ERROR);
        }
    }

}
