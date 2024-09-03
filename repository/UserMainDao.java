package com.eCommerce.repository;

import com.eCommerce.model.UserMain;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository
public class UserMainDao {

    @Autowired
    private SessionFactory sessionFactory;

    public UserMain getPswdAndSalt(String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<UserMain> cr = cb.createQuery(UserMain.class);
        Root<UserMain> root = cr.from(UserMain.class);
        Predicate name1 = cb.equal(root.get("email"), email);
        cr.select(root).where(name1);

        Query<UserMain> query = session.createQuery(cr);
        UserMain userMain = query.getSingleResult();

        return userMain;

    }

}
