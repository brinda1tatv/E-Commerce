package com.eCommerce.repository;

import com.eCommerce.model.*;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public class CarrierDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveCarrier(Carrier carrier) {
        hibernateTemplate.save(carrier);
    }

    @Transactional
    public void updateCarrier(Carrier carrier) {
        hibernateTemplate.update(carrier);
    }

    @Transactional
    public void saveCarrierOrders(CarrierOrders carrierOrders) {
        hibernateTemplate.save(carrierOrders);
    }

    @Transactional
    public void updateCarrierOrders(CarrierOrders carrierOrders) {
        hibernateTemplate.update(carrierOrders);
    }

    public Carrier getCarrierFromId(int id) {
        return hibernateTemplate.get(Carrier.class, id);
    }

    public List<Carrier> getAllCarriers(String searchText, int startIndex, int endIndex) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Carrier> cr = cb.createQuery(Carrier.class);
        Root<Carrier> root = cr.from(Carrier.class);

        if (searchText != "") {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("userId").get("isDeleted"), 0);
            predicates[1] = cb.like(root.get("userId").get("firstName"), "%" + searchText + "%");
            cr.select(root).where(predicates);
        } else {
            Predicate[] predicates = new Predicate[1];
            predicates[0] = cb.equal(root.get("userId").get("isDeleted"), 0);
            cr.select(root).where(predicates);
        }

        Query<Carrier> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<Carrier> list = query.getResultList();

        return list;

    }

    public int getAllCarriersCount(String searchText) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Carrier> cr = cb.createQuery(Carrier.class);
        Root<Carrier> root = cr.from(Carrier.class);

        if (searchText != "") {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("userId").get("isDeleted"), 0);
            predicates[1] = cb.like(root.get("userId").get("firstName"), "%" + searchText + "%");
            cr.select(root).where(predicates);
        } else {
            Predicate[] predicates = new Predicate[1];
            predicates[0] = cb.equal(root.get("userId").get("isDeleted"), 0);
            cr.select(root).where(predicates);
        }

        Query<Carrier> query = session.createQuery(cr);
        int size = query.getResultList().size();

        return size;

    }

    public String getZipCodeOfCarrier(int carrierId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> cr = cb.createQuery(String.class);
        Root<Carrier> root = cr.from(Carrier.class);

        Predicate id = cb.equal(root.get("id"), carrierId);
        cr.select(root.get("zipCode")).where(id);

        Query<String> query = session.createQuery(cr);
        String zipCode = query.getSingleResult();

        return zipCode;

    }

    public List<CarrierOrders> getAllCarrierOrders(String searchText, int startIndex, int endIndex, String filters, int carrierId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CarrierOrders> cr = cb.createQuery(CarrierOrders.class);
        Root<CarrierOrders> root = cr.from(CarrierOrders.class);

        if (searchText != "") {
            if (filters != "") {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("carrierId").get("id"), carrierId);
                predicates[1] = cb.equal(root.get("orderId").get("id"), searchText);

                if (filters.equals("0")) {
                    predicates[2] = cb.equal(root.get("isDelivered"), 1);
                } else if (filters.equals("1")) {
                    predicates[2] = cb.equal(root.get("isDelivered"), 0);
                }

                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[2];
                predicates[0] = cb.equal(root.get("carrierId").get("id"), carrierId);
                predicates[1] = cb.equal(root.get("orderId").get("id"), searchText);

                cr.select(root).where(predicates);
            }

        } else {
            if (filters != "") {
                Predicate[] predicates = new Predicate[2];
                predicates[0] = cb.equal(root.get("carrierId").get("id"), carrierId);

                if (filters.equals("0")) {
                    predicates[1] = cb.equal(root.get("isDelivered"), 1);
                } else if (filters.equals("1")) {
                    predicates[1] = cb.equal(root.get("isDelivered"), 0);
                }

                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[1];
                predicates[0] = cb.equal(root.get("carrierId").get("id"), carrierId);

                cr.select(root).where(predicates);
            }
        }

        Query<CarrierOrders> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<CarrierOrders> list = query.getResultList();

        return list;

    }

    public Integer getAllCarrierOrdersCount(String searchText, String filters, int carrierId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CarrierOrders> cr = cb.createQuery(CarrierOrders.class);
        Root<CarrierOrders> root = cr.from(CarrierOrders.class);

        if (searchText != "") {
            if (filters != "") {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("carrierId").get("id"), carrierId);
                predicates[1] = cb.equal(root.get("orderId").get("id"), searchText);

                if (filters.equals("0")) {
                    predicates[2] = cb.equal(root.get("isDelivered"), 1);
                } else if (filters.equals("1")) {
                    predicates[2] = cb.equal(root.get("isDelivered"), 0);
                }

                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[2];
                predicates[0] = cb.equal(root.get("carrierId").get("id"), carrierId);
                predicates[1] = cb.equal(root.get("orderId").get("id"), searchText);

                cr.select(root).where(predicates);
            }

        } else {
            if (filters != "") {
                Predicate[] predicates = new Predicate[2];
                predicates[0] = cb.equal(root.get("carrierId").get("id"), carrierId);

                if (filters.equals("0")) {
                    predicates[1] = cb.equal(root.get("isDelivered"), 1);
                } else if (filters.equals("1")) {
                    predicates[1] = cb.equal(root.get("isDelivered"), 0);
                }

                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[1];
                predicates[0] = cb.equal(root.get("carrierId").get("id"), carrierId);

                cr.select(root).where(predicates);
            }
        }

        Query<CarrierOrders> query = session.createQuery(cr);
        Integer count = query.getResultList().size();

        return count;

    }

    public Carrier getCarrierFromUser(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Carrier> cr = cb.createQuery(Carrier.class);
        Root<Carrier> root = cr.from(Carrier.class);

        Predicate id = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root).where(id);

        Query<Carrier> query = session.createQuery(cr);
        Carrier carrierId = query.getSingleResult();

        return carrierId;

    }

    public Orders getOrderIdFromCarrierOrderId(int carrierOrderId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Orders> cr = cb.createQuery(Orders.class);
        Root<CarrierOrders> root = cr.from(CarrierOrders.class);

        Predicate id = cb.equal(root.get("id"), carrierOrderId);
        cr.select(root.get("orderId")).where(id);

        Query<Orders> query = session.createQuery(cr);

        Orders Orders = null;
        try {
            Orders = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Orders;

    }

    public void completeOrder(int carrierOrderId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<CarrierOrders> criteriaUpdate = cb.createCriteriaUpdate(CarrierOrders.class);
        Root<CarrierOrders> root = criteriaUpdate.from(CarrierOrders.class);
        criteriaUpdate.set("isDelivered", 1);
        criteriaUpdate.where(cb.equal(root.get("id"), carrierOrderId));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public void notBeDeliveredOrder(int carrierOrderId, String reason) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<CarrierOrders> criteriaUpdate = cb.createCriteriaUpdate(CarrierOrders.class);
        Root<CarrierOrders> root = criteriaUpdate.from(CarrierOrders.class);
        criteriaUpdate.set("notAbleToDeliver", 1);
        criteriaUpdate.set("reason", reason);
        criteriaUpdate.where(cb.equal(root.get("id"), carrierOrderId));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public boolean doCarrierOrderIdExists(int carrierOrderId) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CarrierOrders> cr = cb.createQuery(CarrierOrders.class);
        Root<CarrierOrders> root = cr.from(CarrierOrders.class);

        Predicate id = cb.equal(root.get("id"), carrierOrderId);
        cr.select(root).where(id);

        Query<CarrierOrders> query = session.createQuery(cr);
        CarrierOrders carrierId = query.getSingleResult();

        return carrierId != null;
    }

    public CarrierOrders getCarrierOrderFromId(int carrierOrderId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CarrierOrders> cr = cb.createQuery(CarrierOrders.class);
        Root<CarrierOrders> root = cr.from(CarrierOrders.class);

        Predicate id = cb.equal(root.get("id"), carrierOrderId);
        cr.select(root).where(id);

        Query<CarrierOrders> query = session.createQuery(cr);
        CarrierOrders carrier = null;

        try {
            carrier = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return carrier;
    }

}
