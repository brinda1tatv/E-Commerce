package com.eCommerce.repository;

import com.eCommerce.model.CancelledOrders;
import com.eCommerce.model.Cart;
import com.eCommerce.model.Orders;
import com.eCommerce.model.Revenue;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

@Repository
public class CancelledOrdersDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveCancelledOrders(CancelledOrders cancelledOrders) {
        hibernateTemplate.save(cancelledOrders);
    }

    @Transactional
    public void updateCancelledOrders(CancelledOrders cancelledOrders) {
        hibernateTemplate.update(cancelledOrders);
    }

    public Long getTotalCancelledOrders() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<CancelledOrders> root = cr.from(CancelledOrders.class);

        cr.select(cb.count(root));

        Query<Long> query = session.createQuery(cr);
        Long count = query.getSingleResult();

        return count;

    }

}
