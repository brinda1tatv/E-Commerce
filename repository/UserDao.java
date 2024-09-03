package com.eCommerce.repository;

import com.eCommerce.dto.GetRoleAndUserIdDto;
import com.eCommerce.model.*;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
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
import java.time.LocalDate;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveUser(User user) {
        hibernateTemplate.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        hibernateTemplate.update(user);
    }

    @Transactional
    public void saveUserMain(UserMain userMain) {
        hibernateTemplate.save(userMain);
    }

    @Transactional
    public void updateUserMain(UserMain userMain) {
        hibernateTemplate.update(userMain);
    }

    @Transactional
    public void saveUserSessions(UserSessions userSessions) {
        hibernateTemplate.save(userSessions);
    }

    @Transactional
    public void updateUserSessions(UserSessions userSessions) {
        hibernateTemplate.update(userSessions);
    }

    public User getUser(int id) {

        User user = hibernateTemplate.get(User.class,id);
        return user;

    }

    public UserSessions ifUserSessionExists(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<UserSessions> cr = cb.createQuery(UserSessions.class);
        Root<UserSessions> root = cr.from(UserSessions.class);
        Predicate name1 = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root).where(name1);

        Query<UserSessions> query = session.createQuery(cr);

        UserSessions userSessions = null;
        try {
            userSessions = query.getSingleResult();
        } catch (NoResultException e) {

        }

        return userSessions;

    }

    public int ifUserExists(String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        Predicate name1 = cb.equal(root.get("email"), email);
        cr.select(root).where(name1);

        Query<User> query = session.createQuery(cr);
        int size = query.getResultList().size();

        return size;

    }

    public void blockUser(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<User> criteriaUpdate = cb.createCriteriaUpdate(User.class);
        Root<User> root = criteriaUpdate.from(User.class);
        criteriaUpdate.set("isBlocked", 1);
        criteriaUpdate.where(cb.equal(root.get("id"), userId));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public void unBlockUser(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<User> criteriaUpdate = cb.createCriteriaUpdate(User.class);
        Root<User> root = criteriaUpdate.from(User.class);
        criteriaUpdate.set("isBlocked", 0);
        criteriaUpdate.where(cb.equal(root.get("id"), userId));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public void deleteUser(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<User> criteriaUpdate = cb.createCriteriaUpdate(User.class);
        Root<User> root = criteriaUpdate.from(User.class);
        criteriaUpdate.set("isDeleted", 1);
        criteriaUpdate.where(cb.equal(root.get("id"), userId));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public User getUserFromUserMain(int userMainId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        Predicate name1 = cb.equal(root.get("userMainId").get("id"), userMainId);
        cr.select(root).where(name1);

        Query<User> query = session.createQuery(cr);
        User user = query.getSingleResult();

        return user;

    }

    public List<User> getUserFromUserMail(String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        Predicate name1 = cb.equal(root.get("email"), email);
        cr.select(root).where(name1);

        Query<User> query = session.createQuery(cr);
        List<User> list = query.getResultList();

        return list;

    }

    public List<UserMain> getUserMain(String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<UserMain> cr = cb.createQuery(UserMain.class);
        Root<User> root = cr.from(User.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(root.get("email"), email);
        predicates[1] = cb.equal(root.get("isBlocked"), 0);
        predicates[2] = cb.equal(root.get("isDeleted"), 0);
        cr.select(root.get("userMainId")).where(predicates);

        Query<UserMain> query = session.createQuery(cr);
        List<UserMain> list = query.getResultList();

        return list;

    }

    public GetRoleAndUserIdDto getRoleIdFromEmail(String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GetRoleAndUserIdDto> cr = cb.createQuery(GetRoleAndUserIdDto.class);
        Root<User> root = cr.from(User.class);
        Predicate mail = cb.equal(root.get("email"), email);
        cr.multiselect(root.get("roleId").get("role"), root.get("id"), root.get("isBlocked"), root.get("isDeleted"), root.get("firstName")).where(mail);

        Query<GetRoleAndUserIdDto> query = session.createQuery(cr);
        GetRoleAndUserIdDto getRoleAndUserIdDto = query.getSingleResult();

        return getRoleAndUserIdDto;

    }

    public void editPersonalInfo(String fName, String lName, String phone, String gender, int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<User> criteriaUpdate = cb.createCriteriaUpdate(User.class);
        Root<User> root = criteriaUpdate.from(User.class);
        criteriaUpdate.set("firstName", fName);
        criteriaUpdate.set("lastName", lName);
        criteriaUpdate.set("phone", phone);
        criteriaUpdate.set("gender", gender);
        criteriaUpdate.where((cb.equal(root.get("id"), userId)));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public List<User> getAllUsers(String searchText, int startIndex, int endIndex, String filters, int type) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);

        if (searchText != "") {
            if (filters!="") {
                Predicate[] predicates = new Predicate[4];
                predicates[0] = cb.equal(root.get("isDeleted"), 0);
                predicates[1] = cb.like(root.get("firstName"), "%" + searchText + "%");

                if (type==0){
                    predicates[2] = cb.equal(root.get("roleId"), 2);
                    if (filters.equals("0")){
                        predicates[3] = cb.equal(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now());
                    } else if (filters.equals("1")) {
                        predicates[3] = cb.between(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now().minusDays(7), LocalDate.now());
                    } else if (filters.equals("2")) {
                        predicates[3] = cb.between(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(2), LocalDate.now());
                    }
                }
                else if (type==1) {
                    predicates[2] = cb.and(cb.notEqual(root.get("roleId"), 2), cb.notEqual(root.get("roleId"), 5));
                    if (filters.equals("1")){
                        predicates[3] = cb.equal(root.get("roleId").get("role"), 1);
                    } else if (filters.equals("3")) {
                        predicates[3] = cb.equal(root.get("roleId").get("role"), 3);
                    } else if (filters.equals("4")) {
                        predicates[3] = cb.equal(root.get("roleId").get("role"), 4);
                    }
                }

                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("isDeleted"), 0);

                if (type==0){
                    predicates[1] = cb.equal(root.get("roleId"), 2);
                } else if (type==1) {
                    predicates[1] = cb.and(cb.notEqual(root.get("roleId"), 2), cb.notEqual(root.get("roleId"), 5));
                }

                predicates[2] = cb.like(root.get("firstName"), "%" + searchText + "%");
                cr.select(root).where(predicates);
            }

        } else {
            if (filters!="") {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("isDeleted"), 0);

                if (type==0){
                    predicates[1] = cb.equal(root.get("roleId"), 2);
                    if (filters.equals("0")){
                        predicates[2] = cb.equal(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now());
                    } else if (filters.equals("1")) {
                        predicates[2] = cb.between(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now().minusDays(7), LocalDate.now());
                    } else if (filters.equals("2")) {
                        predicates[2] = cb.between(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(2), LocalDate.now());
                    }
                }
                else if (type==1) {
                    predicates[1] = cb.and(cb.notEqual(root.get("roleId"), 2), cb.notEqual(root.get("roleId"), 5));
                    if (filters.equals("1")){
                        predicates[2] = cb.equal(root.get("roleId").get("role"), 1);
                    } else if (filters.equals("3")) {
                        predicates[2] = cb.equal(root.get("roleId").get("role"), 3);
                    } else if (filters.equals("4")) {
                        predicates[2] = cb.equal(root.get("roleId").get("role"), 4);
                    }
                }

                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[2];
                predicates[0] = cb.equal(root.get("isDeleted"), 0);

                if (type==0){
                    predicates[1] = cb.equal(root.get("roleId"), 2);
                } else if (type==1) {
                    predicates[1] = cb.and(cb.notEqual(root.get("roleId"), 2), cb.notEqual(root.get("roleId"), 5));
                }

                cr.select(root).where(predicates);
            }
        }

        Query<User> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<User> list = query.getResultList();

        return list;

    }

    public int getAllUsersCount(String searchText, String filters, int type) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);

        if (searchText != "") {
            if (filters!="") {
                Predicate[] predicates = new Predicate[4];
                predicates[0] = cb.equal(root.get("isDeleted"), 0);
                predicates[1] = cb.like(root.get("firstName"), "%" + searchText + "%");

                if (type==0){
                    predicates[2] = cb.equal(root.get("roleId"), 2);
                    if (filters.equals("0")){
                        predicates[3] = cb.equal(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now());
                    } else if (filters.equals("1")) {
                        predicates[3] = cb.between(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now().minusDays(7), LocalDate.now());
                    } else if (filters.equals("2")) {
                        predicates[3] = cb.between(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(2), LocalDate.now());
                    }
                }
                else if (type==1) {
                    predicates[2] = cb.and(cb.notEqual(root.get("roleId"), 2), cb.notEqual(root.get("roleId"), 5));
                    if (filters.equals("1")){
                        predicates[3] = cb.equal(root.get("roleId").get("role"), 1);
                    } else if (filters.equals("3")) {
                        predicates[3] = cb.equal(root.get("roleId").get("role"), 3);
                    } else if (filters.equals("4")) {
                        predicates[3] = cb.equal(root.get("roleId").get("role"), 4);
                    }
                }

                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("isDeleted"), 0);

                if (type==0){
                    predicates[1] = cb.equal(root.get("roleId"), 2);
                } else if (type==1) {
                    predicates[1] = cb.and(cb.notEqual(root.get("roleId"), 2), cb.notEqual(root.get("roleId"), 5));
                }

                predicates[2] = cb.like(root.get("firstName"), "%" + searchText + "%");
                cr.select(root).where(predicates);
            }

        } else {
            if (filters!="") {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("isDeleted"), 0);

                if (type==0){
                    predicates[1] = cb.equal(root.get("roleId"), 2);
                    if (filters.equals("0")){
                        predicates[2] = cb.equal(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now());
                    } else if (filters.equals("1")) {
                        predicates[2] = cb.between(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now().minusDays(7), LocalDate.now());
                    } else if (filters.equals("2")) {
                        predicates[2] = cb.between(root.get("userMainId").get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(2), LocalDate.now());
                    }
                }
                else if (type==1) {
                    predicates[1] = cb.and(cb.notEqual(root.get("roleId"), 2), cb.notEqual(root.get("roleId"), 5));
                    if (filters.equals("1")){
                        predicates[2] = cb.equal(root.get("roleId").get("role"), 1);
                    } else if (filters.equals("3")) {
                        predicates[2] = cb.equal(root.get("roleId").get("role"), 3);
                    } else if (filters.equals("4")) {
                        predicates[2] = cb.equal(root.get("roleId").get("role"), 4);
                    }
                }

                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[2];
                predicates[0] = cb.equal(root.get("isDeleted"), 0);

                if (type==0){
                    predicates[1] = cb.equal(root.get("roleId"), 2);
                } else if (type==1) {
                    predicates[1] = cb.and(cb.notEqual(root.get("roleId"), 2), cb.notEqual(root.get("roleId"), 5));
                }

                cr.select(root).where(predicates);
            }
        }

        Query<User> query = session.createQuery(cr);
        int count = query.getResultList().size();

        return count;

    }

    public Long getTotalUsersCount() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<User> root = cr.from(User.class);

        cr.select(cb.count(root));

        Query<Long> query = session.createQuery(cr);
        Long count = query.getSingleResult();

        return count;

    }

}
