package com.eCommerce.repository;

import com.eCommerce.model.*;
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
public class TempOrdersDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveTempOrders(TempOrders tempOrders) {
        hibernateTemplate.save(tempOrders);
    }

    @Transactional
    public void updateTempOrders(TempOrders tempOrders) {
        hibernateTemplate.update(tempOrders);
    }

    @Transactional
    public void saveTempOrderDetails(TempOrderDetails tempOrderDetails) {
        hibernateTemplate.save(tempOrderDetails);
    }

    @Transactional
    public void updateTempOrderDetails(TempOrderDetails tempOrderDetails) {
        hibernateTemplate.update(tempOrderDetails);
    }

    public TempOrders ifOrderWithOutPaymentExists(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<TempOrders> cr = cb.createQuery(TempOrders.class);
        Root<TempOrders> root = cr.from(TempOrders.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("isPaymentDone"), 0);
        cr.select(root).where(predicates);

        Query<TempOrders> query = session.createQuery(cr);

        TempOrders tempOrders = null;
        try {
            tempOrders = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return tempOrders;

    }

    public TempOrderDetails tempOrderDetails(int orderId, int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<TempOrderDetails> cr = cb.createQuery(TempOrderDetails.class);
        Root<TempOrderDetails> root = cr.from(TempOrderDetails.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("orderId").get("id"), orderId);
        predicates[1] = cb.equal(root.get("orderId").get("userId").get("id"), userId);
        cr.select(root).where(predicates);

        Query<TempOrderDetails> query = session.createQuery(cr);

        TempOrderDetails tempOrderDetails = null;
        try {
            tempOrderDetails = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return tempOrderDetails;

    }

}
