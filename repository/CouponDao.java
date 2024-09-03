package com.eCommerce.repository;

import com.eCommerce.dto.AddCouponDto;
import com.eCommerce.model.Category;
import com.eCommerce.model.Coupon;
import com.eCommerce.model.Product;
import com.eCommerce.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CouponDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveCoupon(Coupon coupon) {
        hibernateTemplate.save(coupon);
    }

    @Transactional
    public void updateCoupon(Coupon coupon) {
        hibernateTemplate.update(coupon);
    }

    public List<Coupon> getAllCoupons(String search,int startIndex,int endIndex) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Coupon> cr = cb.createQuery(Coupon.class);
        Root<Coupon> root = cr.from(Coupon.class);

        if (search != "") {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            predicates[1] = cb.like(root.get("code"), "%" + search + "%");
            cr.select(root).where(predicates);
        } else {
            Predicate[] predicates = new Predicate[1];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            cr.select(root).where(predicates);
        }

        Query<Coupon> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<Coupon> list = query.getResultList();

        return list;

    }

    public int getAllCouponsCount(String search) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Coupon> cr = cb.createQuery(Coupon.class);
        Root<Coupon> root = cr.from(Coupon.class);

        if (search != "") {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            predicates[1] = cb.like(root.get("code"), "%" + search + "%");
            cr.select(root).where(predicates);
        } else {
            Predicate[] predicates = new Predicate[1];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            cr.select(root).where(predicates);
        }

        Query<Coupon> query = session.createQuery(cr);
        int size = query.getResultList().size();

        return size;

    }

    public boolean ifCodeExists(String code) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Coupon> cr = cb.createQuery(Coupon.class);
        Root<Coupon> root = cr.from(Coupon.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("isDeleted"), 0);
        predicates[1] = cb.equal(root.get("code"), code);
        cr.select(root).where(predicates);

        Query<Coupon> query = session.createQuery(cr);
        List<Coupon> list = query.getResultList();

        return !list.isEmpty();

    }

    public Coupon getCoupon(String code) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Coupon> cr = cb.createQuery(Coupon.class);
        Root<Coupon> root = cr.from(Coupon.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("isDeleted"), 0);
        predicates[1] = cb.equal(root.get("code"), code);
        cr.select(root).where(predicates);

        Query<Coupon> query = session.createQuery(cr);
        Coupon coupon = null;

        try {
            coupon = query.getSingleResult();
        } catch (NoResultException e) {

        }

        return coupon;

    }

    public Coupon getCouponFromId(int id) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Coupon> cr = cb.createQuery(Coupon.class);
        Root<Coupon> root = cr.from(Coupon.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("isDeleted"), 0);
        predicates[1] = cb.equal(root.get("id"), id);
        cr.select(root).where(predicates);

        Query<Coupon> query = session.createQuery(cr);
        Coupon coupon = null;

        try {
            coupon = query.getSingleResult();
        } catch (NoResultException e) {

        }

        return coupon;

    }

    public void deleteCoupon(int id, User user) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<Coupon> criteriaUpdate = cb.createCriteriaUpdate(Coupon.class);
        Root<Coupon> root = criteriaUpdate.from(Coupon.class);
        criteriaUpdate.set("isDeleted", 1);
        criteriaUpdate.set("modifiedBy", user);
        criteriaUpdate.where(cb.equal(root.get("id"), id));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public List<Tuple> getCouponFromMinPrice(Double minimumPrice) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createQuery(Tuple.class);
        Root<Coupon> root = cr.from(Coupon.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(root.get("isDeleted"), 0);
        predicates[1] = cb.lessThanOrEqualTo(root.get("minimumPrice"), minimumPrice);
        predicates[2] = cb.greaterThanOrEqualTo(root.get("endDate"), LocalDateTime.now());
        cr.multiselect(root.get("code"), root.get("type"), root.get("discount"), root.get("id")).where(predicates);

        Query<Tuple> query = session.createQuery(cr);
        List<Tuple> resultList = query.getResultList();
        return resultList;

    }

}
