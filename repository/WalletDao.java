package com.eCommerce.repository;

import com.eCommerce.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class WalletDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveWallet(Wallet wallet) {
        hibernateTemplate.save(wallet);
    }

    @Transactional
    public void updateWallet(Wallet wallet) {
        hibernateTemplate.update(wallet);
    }

    public Double getWalletDetails(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Double> cr = cb.createQuery(Double.class);
        Root<Wallet> root = cr.from(Wallet.class);

        Predicate equal = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root.get("balance")).where(equal);

        Query<Double> query = session.createQuery(cr);
        List<Double> oldBalance = query.getResultList();

        return oldBalance.get(oldBalance.size()-1);

    }

    public List<Wallet> getWallet(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Wallet> cr = cb.createQuery(Wallet.class);
        Root<Wallet> root = cr.from(Wallet.class);

        Predicate equal = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root).where(equal);

        Query<Wallet> query = session.createQuery(cr);
        List<Wallet> list = query.getResultList();

        return list;

    }

    public Page<Wallet> getWalletPaymentHistory(int userId, Pageable pageable) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Wallet> cr = cb.createQuery(Wallet.class);
        Root<Wallet> root = cr.from(Wallet.class);

        Predicate equal = cb.equal(root.get("userId").get("id"), userId);
        cr.orderBy(cb.desc(root.get("transactionaDate")));
        cr.select(root).where(equal);

        Query<Wallet> query = session.createQuery(cr);
        int count = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Wallet> list = query.getResultList();

        return new PageImpl<>(list, pageable, count);

    }

}
