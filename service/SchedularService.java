package com.eCommerce.service;

import com.eCommerce.model.Category;
import com.eCommerce.model.Notifications;
import com.eCommerce.repository.CategoryDao;
import com.eCommerce.repository.NotificationsDao;
import com.eCommerce.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class SchedularService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private NotificationsDao notificationsDao;

//    @Scheduled(cron = "0 0/30 * * * *")
//    public void checkForAddingCategory() {
//        System.out.println("yes");
//
//        List<Notifications> notificationsList = notificationsDao.ifNewNotificationAdded();
//
//        for (Notifications notifications : notificationsList) {
//
//            if (notifications.getType()==0) {
//
//            }
//
//        }
//
//    }

}
