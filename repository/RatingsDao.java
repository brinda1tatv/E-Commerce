package com.eCommerce.repository;

import com.eCommerce.model.Cart;
import com.eCommerce.model.Product;
import com.eCommerce.model.ProductAttributes;
import com.eCommerce.model.Ratings;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RatingsDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveRatings(Ratings ratings) {
        hibernateTemplate.save(ratings);
    }

    @Transactional
    public void updateRatings(Ratings ratings) {
        hibernateTemplate.update(ratings);
    }

    public Double getRatingsFromProductId(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Double> cr = cb.createQuery(Double.class);
        Root<Ratings> root = cr.from(Ratings.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("productId").get("id"), productId);
        predicates[1] = cb.equal(root.get("isDeleted"), 0);
        cr.select(cb.avg(root.get("rating"))).where(predicates);

        Query<Double> query = session.createQuery(cr);
        Double ratings = query.getSingleResult();

        return ratings;

    }

    public List<Ratings> getFullRatingsFromProductId(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ratings> cr = cb.createQuery(Ratings.class);
        Root<Ratings> root = cr.from(Ratings.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("productId").get("id"), productId);
        predicates[1] = cb.equal(root.get("isDeleted"), 0);
        cr.select(root).where(predicates);

        Query<Ratings> query = session.createQuery(cr);
        List<Ratings> list = query.getResultList();

        return list;

    }

    public List<Ratings> getFullRatingsFromUserId(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ratings> cr = cb.createQuery(Ratings.class);
        Root<Ratings> root = cr.from(Ratings.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("isDeleted"), 0);
        cr.select(root).where(predicates);

        Query<Ratings> query = session.createQuery(cr);
        List<Ratings> list = query.getResultList();

        return list;

    }

    public void editReview(int id, String heading, String reviewText, int rating) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<Ratings> criteriaUpdate = cb.createCriteriaUpdate(Ratings.class);
        Root<Ratings> root = criteriaUpdate.from(Ratings.class);
        criteriaUpdate.set("heading", heading);
        criteriaUpdate.set("reviewText", reviewText);
        criteriaUpdate.set("rating", rating);
        criteriaUpdate.set("modifiedDate", LocalDateTime.now());
        criteriaUpdate.where((cb.equal(root.get("id"), id)));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public void deleteReview(int id) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<Ratings> criteriaUpdate = cb.createCriteriaUpdate(Ratings.class);
        Root<Ratings> root = criteriaUpdate.from(Ratings.class);
        criteriaUpdate.set("isDeleted", 1);
        criteriaUpdate.where((cb.equal(root.get("id"), id)));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public int ifUserHasReviewdOnce(int userId, int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ratings> cr = cb.createQuery(Ratings.class);
        Root<Ratings> root = cr.from(Ratings.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("productId").get("id"), productId);
        predicates[2] = cb.equal(root.get("isDeleted"), 0);
        cr.select(root).where(predicates);

        Query<Ratings> query = session.createQuery(cr);
        int size = query.getResultList().size();

        return size;

    }

    public Long getRatingsCount(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Ratings> root = cr.from(Ratings.class);

        Predicate[] predicates = new Predicate[1];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        cr.select(cb.count(root)).where(predicates);

        Query<Long> query = session.createQuery(cr);
        Long count = query.getSingleResult();

        return count;

    }

}
