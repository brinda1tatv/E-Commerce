package com.eCommerce.repository;

import com.eCommerce.model.VerificationToken;
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
import java.time.LocalDate;
import java.util.List;

@Repository
public class VerificationTokenDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveVerificationToken(VerificationToken verificationToken) {
        hibernateTemplate.save(verificationToken);
    }

    @Transactional
    public void updateVerificationToken(VerificationToken verificationToken) {
        hibernateTemplate.update(verificationToken);
    }

    public List<VerificationToken> checkvalidation(String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<VerificationToken> cr = cb.createQuery(VerificationToken.class);
        Root<VerificationToken> root = cr.from(VerificationToken.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("email"), email);
        predicates[1] = cb.equal(root.get("createdDate").as(LocalDate.class), LocalDate.now());
        cr.select(root).where(predicates);

        Query<VerificationToken> query = session.createQuery(cr);
        List<VerificationToken> list = query.getResultList();

        return list;

    }

    public List<VerificationToken> checkToken(String token) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<VerificationToken> cr = cb.createQuery(VerificationToken.class);
        Root<VerificationToken> root = cr.from(VerificationToken.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("token"), token);
        predicates[1] = cb.equal(root.get("validation"), 1);
        cr.select(root).where(predicates);

        Query<VerificationToken> query = session.createQuery(cr);
        List<VerificationToken> list = query.getResultList();

        return list;

    }

    public VerificationToken checkTokenFromId(int tokenId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<VerificationToken> cr = cb.createQuery(VerificationToken.class);
        Root<VerificationToken> root = cr.from(VerificationToken.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("id"), tokenId);
        predicates[1] = cb.equal(root.get("validation"), 1);
        cr.select(root).where(predicates);

        Query<VerificationToken> query = session.createQuery(cr);
        VerificationToken verificationToken = query.getSingleResult();

        return verificationToken;

    }

}
