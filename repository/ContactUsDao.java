package com.eCommerce.repository;

import com.eCommerce.model.ContactUs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public class ContactUsDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Transactional
    public void saveContactUs(ContactUs contactUs) {
        hibernateTemplate.save(contactUs);
    }

}
