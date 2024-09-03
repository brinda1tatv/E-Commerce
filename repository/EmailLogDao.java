package com.eCommerce.repository;

import com.eCommerce.model.EmailLogs;
import com.eCommerce.model.Product;
import com.eCommerce.model.Seller;
import com.eCommerce.model.Stock;
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
import java.time.LocalDate;

@Repository
public class EmailLogDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveEmailLog(EmailLogs emailLogs) {
        hibernateTemplate.save(emailLogs);
    }

    @Transactional
    public void updateEmailLog(EmailLogs emailLogs) {
        hibernateTemplate.update(emailLogs);
    }

    public String checkForBusinessMailForStock(String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> cr = cb.createQuery(String.class);
        Root<EmailLogs> root = cr.from(EmailLogs.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("email"), email);
        predicates[1] = cb.equal(root.get("sentDateTime").as(LocalDate.class), LocalDate.now());
        cr.select(root.get("action")).where(predicates);

        Query<String> query = session.createQuery(cr);
        String result = null;

        try {
            result = query.getSingleResult();
        }
        catch (NoResultException e) {

        }

        return result;

    }

}
