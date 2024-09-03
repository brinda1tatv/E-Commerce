package com.eCommerce.repository;

import com.eCommerce.model.Category;
import com.eCommerce.model.OTPData;
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
import javax.validation.constraints.Email;
import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static java.util.Date.*;

@Repository
public class OtpDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveOtp(OTPData otpData) {
        hibernateTemplate.save(otpData);
    }

    public int isOtpValid(String otp, LocalDateTime endTime, String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OTPData> cr = cb.createQuery(OTPData.class);
        Root<OTPData> root = cr.from(OTPData.class);
        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(root.get("otp"), otp);
        predicates[1] = cb.greaterThanOrEqualTo(root.get("endTime"), endTime);
        predicates[2] = cb.equal(root.get("email"), email);
        cr.select(root).where(predicates);

        Query<OTPData> query = session.createQuery(cr);
        int size = query.getResultList().size();

        return size;

    }

    public int isEmailValid(String email) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OTPData> cr = cb.createQuery(OTPData.class);
        Root<OTPData> root = cr.from(OTPData.class);
        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("startTime").as(LocalDate.class), LocalDate.now());
        predicates[1] = cb.equal(root.get("email"), email);
        cr.select(root).where(predicates);

        Query<OTPData> query = session.createQuery(cr);
        int size = query.getResultList().size();

        return size;

    }

}
