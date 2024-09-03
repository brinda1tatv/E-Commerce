package com.eCommerce.repository;

import com.eCommerce.dto.AddAddressDto;
import com.eCommerce.model.Category;
import com.eCommerce.model.CustomerAddress;
import com.eCommerce.model.WishListItems;
import com.eCommerce.model.WishListTokens;
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
public class CustomerAddressDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveCustomerAddress(CustomerAddress customerAddress) {
        hibernateTemplate.save(customerAddress);
    }

    @Transactional
    public void updateCustomerAddress(CustomerAddress customerAddress) {
        hibernateTemplate.update(customerAddress);
    }

    public CustomerAddress getCustomerAddressFromId(int id) {
        return hibernateTemplate.load(CustomerAddress.class, id);
    }

    public boolean ifBillingExists(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CustomerAddress> cr = cb.createQuery(CustomerAddress.class);
        Root<CustomerAddress> root = cr.from(CustomerAddress.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(root.get("type"), "billing");
        predicates[1] = cb.equal(root.get("userId").get("id"), userId);
        predicates[2] = cb.equal(root.get("isDeleted"), 0);
        cr.select(root).where(predicates);

        Query<CustomerAddress> query = session.createQuery(cr);
        List<CustomerAddress> resultList = query.getResultList();

        return !resultList.isEmpty();

    }

    public List<AddAddressDto> getAllAddresses(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AddAddressDto> cr = cb.createQuery(AddAddressDto.class);
        Root<CustomerAddress> root = cr.from(CustomerAddress.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("isDeleted"), 0);
        cr.select(cb.construct(AddAddressDto.class,
                root.get("id"),
                root.get("firstName"),
                root.get("lastName"),
                root.get("phone"),
                root.get("type"),
                root.get("address"),
                root.get("city"),
                root.get("state"),
                root.get("zipCode")
        )).where(predicates);

        Query<AddAddressDto> query = session.createQuery(cr);
        List<AddAddressDto> resultList = query.getResultList();

        return resultList;

    }

    public void deleteAddress(int id) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<CustomerAddress> criteriaUpdate = cb.createCriteriaUpdate(CustomerAddress.class);
        Root<CustomerAddress> root = criteriaUpdate.from(CustomerAddress.class);
        criteriaUpdate.set("isDeleted", 1);
        criteriaUpdate.where((cb.equal(root.get("id"), id)));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public List<CustomerAddress> checkIfAddrExists(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CustomerAddress> cr = cb.createQuery(CustomerAddress.class);
        Root<CustomerAddress> root = cr.from(CustomerAddress.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("isDeleted"), 0);
        predicates[2] = cb.equal(root.get("type"), "shipping");
        cr.select(root).where(predicates);

        Query<CustomerAddress> query = session.createQuery(cr);
        List<CustomerAddress> resultList = query.getResultList();

        return resultList;

    }

}
