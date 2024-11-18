package com.userms.service;

import com.userms.entity.SubscriptionEntity;
import com.userms.entity.SubscriptionType;
import com.userms.entity.UserEntity;
import com.userms.repository.IUserRepo;
import com.userms.service.impl.RegistrationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class SubscriptionSchedulerService {

    @Autowired
    private IUserRepo userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RegistrationServiceImpl registrationService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkSubscriptions() {
        List<UserEntity> allUsers = userRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        for (UserEntity user : allUsers) {
            SubscriptionEntity subscription = user.getOrganization().getOrganizationSubscription().getSubscription();
            if (subscription != null && subscription.getActiveStatus()) {
                LocalDate subscriptionDate = user.getOrganization().getOrganizationSubscription().getSubscriptionDate();
                long validity = subscription.getValidity();

                LocalDate expiryDate = null;
                switch (subscription.getSubscriptionType()) {
                    case DAYS:
                        expiryDate = subscriptionDate.plusDays(validity);
                        break;
                    case MONTH:
                        expiryDate = subscriptionDate.plusMonths(validity);
                        break;
                    case YEAR:
                        expiryDate = subscriptionDate.plusYears(validity);
                        break;
                }

                if (expiryDate != null) {

                    if (subscription.getSubscriptionType() == SubscriptionType.DAYS) {
                        if (currentDate.plusDays(1).isEqual(expiryDate)) {
                            emailService.sendSubscriptionExpiryAlert(user.getEmailId(), user.getEmailId(), expiryDate);
                        }
                        if (currentDate.isEqual(expiryDate)) {
                            emailService.sendSubscriptionExpiredNotice(user.getEmailId(), user.getEmailId());
                        }
                    }

                    if (subscription.getSubscriptionType() == SubscriptionType.MONTH) {
                        if (currentDate.plusDays(7).isEqual(expiryDate)) {
                            emailService.sendSubscriptionExpiryAlert(user.getEmailId(), user.getEmailId(), expiryDate);
                        }
                        if (currentDate.plusDays(1).isEqual(expiryDate)) {
                            emailService.sendSubscriptionExpiryAlert(user.getEmailId(), user.getEmailId(), expiryDate);
                        }
                        if (currentDate.isEqual(expiryDate)) {
                            emailService.sendSubscriptionExpiredNotice(user.getEmailId(), user.getEmailId());
                        }
                        if (currentDate.isEqual(expiryDate.plusDays(3))) {
                            emailService.sendSubscriptionRenewalReminder(user.getEmailId(), user.getEmailId());
                        }
                    }

                    if (subscription.getSubscriptionType() == SubscriptionType.YEAR) {
                        if (currentDate.plusDays(30).isEqual(expiryDate)) {
                            emailService.sendSubscriptionExpiryAlert(user.getEmailId(), user.getEmailId(), expiryDate);
                        }
                        if (currentDate.plusDays(7).isEqual(expiryDate)) {
                            emailService.sendSubscriptionExpiryAlert(user.getEmailId(), user.getEmailId(), expiryDate);
                        }
                        if (currentDate.plusDays(1).isEqual(expiryDate)) {
                            emailService.sendSubscriptionExpiryAlert(user.getEmailId(), user.getEmailId(), expiryDate);
                        }
                        if (currentDate.isEqual(expiryDate)) {
                            emailService.sendSubscriptionExpiredNotice(user.getEmailId(), user.getEmailId());
                        }
                        if (currentDate.isEqual(expiryDate.plusDays(7))) {
                            emailService.sendSubscriptionRenewalReminder(user.getEmailId(), user.getEmailId());
                        }
                    }


                    if (currentDate.isAfter(expiryDate)) {
                        user.getOrganization().getOrganizationSubscription().setSubscription(null);
                        userRepository.save(user);
                    }
                }
            }
        }
    }
}

