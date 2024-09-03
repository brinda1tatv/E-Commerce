package com.eCommerce.repository;

import com.eCommerce.model.Revenue;
import com.eCommerce.model.Sales;
import com.eCommerce.model.Seller;
import com.eCommerce.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public class SalesDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveSales(Sales sales) {
        hibernateTemplate.save(sales);
    }

    @Transactional
    public void updateSales(Sales sales) {
        hibernateTemplate.update(sales);
    }

    @Transactional
    public void saveRevenue(Revenue revenue) {
        hibernateTemplate.save(revenue);
    }

    @Transactional
    public void updateRevenue(Revenue revenue) {
        hibernateTemplate.update(revenue);
    }

    public Double getTotalSales() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Double> cr = cb.createQuery(Double.class);
        Root<Sales> root = cr.from(Sales.class);

        cr.select(cb.sum(root.get("totalAmount")));

        Query<Double> query = session.createQuery(cr);
        Double count = query.getSingleResult();

        return count;

    }

    public List<Tuple> getTotalSalesMonthly() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createQuery(Tuple.class);
        Root<Sales> root = cr.from(Sales.class);

        Expression<Integer> monthExp = cb.function("month", Integer.class, root.get("addedDateTime"));

        cr.multiselect(
                        monthExp,
                        cb.sum(root.get("totalAmount"))
                ).groupBy(monthExp);

        Query<Tuple> query = session.createQuery(cr);
        List<Tuple> list = query.getResultList();

        return list;

    }

    public Double getTotalRevenue() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Double> cr = cb.createQuery(Double.class);
        Root<Revenue> root = cr.from(Revenue.class);

        cr.select(cb.sum(root.get("totalAmount")));

        Query<Double> query = session.createQuery(cr);
        Double count = query.getSingleResult();

        return count;

    }

    public List<Tuple> getTotalRevenueMonthly() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createQuery(Tuple.class);
        Root<Revenue> root = cr.from(Revenue.class);

        Expression<Integer> monthExp = cb.function("month", Integer.class, root.get("addedDateTime"));

        cr.multiselect(
                monthExp,
                cb.sum(root.get("totalAmount"))
        ).groupBy(monthExp);

        Query<Tuple> query = session.createQuery(cr);
        List<Tuple> list = query.getResultList();

        return list;

    }

}
