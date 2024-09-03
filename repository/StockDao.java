package com.eCommerce.repository;

import com.eCommerce.model.Product;
import com.eCommerce.model.Ratings;
import com.eCommerce.model.Seller;
import com.eCommerce.model.Stock;
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

@Repository
public class StockDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveStock(Stock stock) {
        hibernateTemplate.save(stock);
    }

    @Transactional
    public void updateStock(Stock stock) {
        hibernateTemplate.update(stock);
    }

    public Stock getStockOfProduct(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Stock> cr = cb.createQuery(Stock.class);
        Root<Stock> root = cr.from(Stock.class);
        Predicate name1 = cb.equal(root.get("productId").get("id"), productId);
        cr.select(root).where(name1);

        Query<Stock> query = session.createQuery(cr);
        Stock stock = query.getSingleResult();

        return stock;

    }

    public Stock getStockOfProductFromColor(int productId, String color) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Stock> cr = cb.createQuery(Stock.class);
        Root<Stock> root = cr.from(Stock.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("productId").get("id"), productId);
        predicates[1] = cb.equal(root.get("color"), color);
        cr.select(root).where(predicates);

        Query<Stock> query = session.createQuery(cr);
        Stock stock = query.getSingleResult();

        return stock;

    }

    public Integer getTotalStockCountOfProduct(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
        Root<Stock> root = cr.from(Stock.class);
        Predicate name1 = cb.equal(root.get("productId").get("id"), productId);
        cr.select(cb.sum(root.get("stock"))).where(name1);

        Query<Integer> query = session.createQuery(cr);
        Integer stock = query.getSingleResult();

        return stock;

    }

    public Integer getStockCountOfProductFromColor(int productId, String color) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
        Root<Stock> root = cr.from(Stock.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("productId").get("id"), productId);
        predicates[1] = cb.equal(root.get("color"), color);
        cr.select(root.get("stock")).where(predicates);

        Query<Integer> query = session.createQuery(cr);
        Integer stock = query.getSingleResult();

        return stock;

    }

//    public Stock updateStockOfProduct(Integer stock, int productId) {
//
//        Session session = sessionFactory.openSession();
//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaUpdate<Stock> criteriaUpdate = cb.createCriteriaUpdate(Stock.class);
//        Root<Stock> root = criteriaUpdate.from(Stock.class);
//        criteriaUpdate.set("stock", stock);
//        criteriaUpdate.where((cb.equal(root.get("productId").get("id"), productId)));
//
//        Transaction transaction = session.beginTransaction();
//        session.createQuery(criteriaUpdate).executeUpdate();
//        transaction.commit();
//
//    }

}
