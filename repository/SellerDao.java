package com.eCommerce.repository;

import com.eCommerce.model.Category;
import com.eCommerce.model.Product;
import com.eCommerce.model.Seller;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class SellerDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveSeller(Seller seller) {
        hibernateTemplate.save(seller);
    }

    @Transactional
    public void updateSeller(Seller seller) {
        hibernateTemplate.update(seller);
    }

    public Seller getSellerById(int id) {

        Seller seller = hibernateTemplate.load(Seller.class, id);
        return seller;

    }

    public List<Seller> doSellerExist(String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Seller> cr = cb.createQuery(Seller.class);
        Root<Seller> root = cr.from(Seller.class);
        Predicate name1 = cb.equal(root.get("email"), email);
        cr.select(root).where(name1);

        Query<Seller> query = session.createQuery(cr);
        List<Seller> list = query.getResultList();

        return list;

    }

    public List<Seller> getAllSellerName() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Seller> cr = cb.createQuery(Seller.class);
        Root<Seller> root = cr.from(Seller.class);
        cr.select(root);

        Query<Seller> query = session.createQuery(cr);
        List<Seller> list = query.getResultList();

        return list;

    }

}
