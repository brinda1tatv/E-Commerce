package com.eCommerce.repository;

import com.eCommerce.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class NotificationsDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveNotifications(Notifications notifications) {
        hibernateTemplate.save(notifications);
    }

    @Transactional
    public void updateNotifications(Notifications notifications) {
        hibernateTemplate.update(notifications);
    }

    @Transactional
    public void saveUserNotifications(UserNotifications userNotifications) {
        hibernateTemplate.save(userNotifications);
    }

    @Transactional
    public void updateUserNotifications(UserNotifications userNotifications) {
        hibernateTemplate.update(userNotifications);
    }

    @Transactional
    public void saveSeenNotifications(SeenNotifications seenNotifications) {
        hibernateTemplate.save(seenNotifications);
    }

    @Transactional
    public void updateSeenNotifications(SeenNotifications seenNotifications) {
        hibernateTemplate.update(seenNotifications);
    }

    public Notifications getNotificationsFromId(int id) {
        return hibernateTemplate.get(Notifications.class, id);
    }

    public List<Notifications> ifNewNotificationAdded() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Notifications> cr = cb.createQuery(Notifications.class);
        Root<Notifications> root = cr.from(Notifications.class);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = LocalDateTime.now().minusMinutes(15);

        Predicate isNewCategory = cb.between(root.get("createdDate"), before, now);
        cr.select(root).where(isNewCategory);
        cr.orderBy(cb.desc(root.get("createdDate")));

        Query<Notifications> query = session.createQuery(cr);
        List<Notifications> list = query.getResultList();

        return list;

    }

    public UserNotifications getIfUserHasNotiOn(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<UserNotifications> cr = cb.createQuery(UserNotifications.class);
        Root<UserNotifications> root = cr.from(UserNotifications.class);

        Predicate user = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root).where(user);

        Query<UserNotifications> query = session.createQuery(cr);

        UserNotifications userNotifications = null;
        try {
            userNotifications = query.getSingleResult();
        }catch (NoResultException e) {

        }

        return userNotifications;

    }

    public boolean ifUserHasNotiOn(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Boolean> cr = cb.createQuery(Boolean.class);
        Root<UserNotifications> root = cr.from(UserNotifications.class);

        Predicate user = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root.get("isNotiOn")).where(user);

        Query<Boolean> query = session.createQuery(cr);
        Boolean isNotiOn = query.getSingleResult();

        return isNotiOn;

    }

    public void stopNotifications(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<UserNotifications> criteriaUpdate = cb.createCriteriaUpdate(UserNotifications.class);
        Root<UserNotifications> root = criteriaUpdate.from(UserNotifications.class);
        criteriaUpdate.set("isNotiOn", false);
        criteriaUpdate.where((cb.equal(root.get("userId").get("id"), userId)));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public void onNotifications(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<UserNotifications> criteriaUpdate = cb.createCriteriaUpdate(UserNotifications.class);
        Root<UserNotifications> root = criteriaUpdate.from(UserNotifications.class);
        criteriaUpdate.set("isNotiOn", true);
        criteriaUpdate.where((cb.equal(root.get("userId").get("id"), userId)));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public List<Integer> getSeenNotificationIds(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
        Root<SeenNotifications> root = cr.from(SeenNotifications.class);

        Predicate user = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root.get("notificationsId").get("id")).where(user);
        cr.orderBy(cb.desc(root.get("notificationsId").get("createdDate")));

        Query<Integer> query = session.createQuery(cr);
        List<Integer> resultList = query.getResultList();

        return resultList;

    }

}
