package com.eCommerce.repository;

import com.eCommerce.model.Cart;
import com.eCommerce.model.Role;
import com.eCommerce.model.User;
import com.eCommerce.model.UserMain;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class RoleDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    public Role getRole(int id) {

        Role role = hibernateTemplate.load(Role.class,id);
        return role;

    }

    public List<Role> getRoles() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Role> cr = cb.createQuery(Role.class);
        Root<Role> root = cr.from(Role.class);
        cr.select(root).where(cb.notEqual(root.get("role"),5));

        Query<Role> query = session.createQuery(cr);
        List<Role> resultList = query.getResultList();

        return resultList;

    }

}
