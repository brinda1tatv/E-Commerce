package com.eCommerce.repository;

import com.eCommerce.model.Cart;
import com.eCommerce.model.Category;
import com.eCommerce.model.Product;
import com.eCommerce.model.Seller;
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
import java.util.List;

@Repository
public class CartDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveCart(Cart cart) {
        hibernateTemplate.save(cart);
    }

    @Transactional
    public void updateCart(Cart cart) {
        hibernateTemplate.update(cart);
    }

    public Cart ifProductExistsInCart(int userId, int pId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Cart> cr = cb.createQuery(Cart.class);
        Root<Cart> root = cr.from(Cart.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("productId").get("id"), pId);
        cr.select(root).where(predicates);

        Query<Cart> query = session.createQuery(cr);

        Cart cart = null;
        try {
            cart = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return cart;

    }

    public int totalProductsInCart(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Cart> cr = cb.createQuery(Cart.class);
        Root<Cart> root = cr.from(Cart.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("isRemoved"), 0);
        cr.select(root).where(predicates);

        Query<Cart> query = session.createQuery(cr);
        int totalSize = query.getResultList().size();

        return totalSize;

    }

    public List<Product> getProductId(int userId, String searchText, String yourFilters) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Cart> root = cr.from(Cart.class);

        Predicate equal1 = cb.equal(root.get("userId").get("id"), userId);
        Predicate isRemoved = cb.equal(root.get("isRemoved"), 0);

        if (!searchText.equals("all")) {
            Predicate equal = cb.like(root.get("productId").get("name"), "%" + searchText + "%");
            cr.select(root.get("productId")).where(cb.and(equal1, isRemoved, equal));
        }
        else {
            cr.select(root.get("productId")).where(equal1, isRemoved);
        }

        if (!yourFilters.equals("0")) {

            if(yourFilters.startsWith("price")) {
                String price = yourFilters.substring(5);

                if (price.equals("htol")) {
                    cr.orderBy(cb.desc(root.get("productId").get("cost")));
                } else {
                    cr.orderBy(cb.asc(root.get("productId").get("cost")));
                }

            }

            else {

                if (yourFilters.equals("ntoo")) {
                    cr.orderBy(cb.desc(root.get("addedDate")));
                } else {
                    cr.orderBy(cb.asc(root.get("addedDate")));
                }

            }

        }

        Query<Product> query = session.createQuery(cr);
        List<Product> list = query.getResultList();

        return list;

    }

    public List<Cart> getCart(int userId, String searchText, String yourFilters) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Cart> cr = cb.createQuery(Cart.class);
        Root<Cart> root = cr.from(Cart.class);

        Predicate equal1 = cb.equal(root.get("userId").get("id"), userId);
        Predicate isRemoved = cb.equal(root.get("isRemoved"), 0);

        if (!searchText.equals("all")) {
            Predicate equal = cb.like(root.get("productId").get("name"), "%" + searchText + "%");
            cr.select(root).where(cb.and(equal1, isRemoved, equal));
        }
        else {
            cr.select(root).where(equal1, isRemoved);
        }

        if (!yourFilters.equals("0")) {

            if(yourFilters.startsWith("price")) {
                String price = yourFilters.substring(5);

                if (price.equals("htol")) {
                    cr.orderBy(cb.desc(root.get("productId").get("cost")));
                } else {
                    cr.orderBy(cb.asc(root.get("productId").get("cost")));
                }

            }

            else {

                if (yourFilters.equals("ntoo")) {
                    cr.orderBy(cb.desc(root.get("addedDate")));
                } else {
                    cr.orderBy(cb.asc(root.get("addedDate")));
                }

            }

        }

        Query<Cart> query = session.createQuery(cr);
        List<Cart> list = query.getResultList();

        return list;

    }

    public void deleteProductFromCart(int productId, int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<Cart> criteriaUpdate = cb.createCriteriaUpdate(Cart.class);
        Root<Cart> root = criteriaUpdate.from(Cart.class);
        criteriaUpdate.set("isRemoved", 1);
        criteriaUpdate.where(cb.and((cb.equal(root.get("productId"), productId)), (cb.equal(root.get("userId"), userId))));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

}
