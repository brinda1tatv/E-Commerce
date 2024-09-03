package com.eCommerce.repository;

import com.eCommerce.model.CancelledOrders;
import com.eCommerce.model.OrderDetails;
import com.eCommerce.model.Orders;
import com.eCommerce.model.Product;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrdersDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveOrders(Orders orders) {
        hibernateTemplate.save(orders);
    }

    @Transactional
    public void updateOrders(Orders orders) {
        hibernateTemplate.update(orders);
    }

    @Transactional
    public void saveOrderDetails(OrderDetails orderDetails) {
        hibernateTemplate.save(orderDetails);
    }

    @Transactional
    public void updateOrderDetails(OrderDetails orderDetails) {
        hibernateTemplate.update(orderDetails);
    }

    public Orders getOrderFromId(int id) {
        return hibernateTemplate.get(Orders.class, id);
    }

    public int ifUserEligible(int productId, int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OrderDetails> cr = cb.createQuery(OrderDetails.class);
        Root<OrderDetails> root = cr.from(OrderDetails.class);

        Predicate[] predicates = new Predicate[4];
        predicates[0] = cb.equal(root.get("orderId").get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("orderId").get("isCompleted"), 1);
        predicates[2] = cb.equal(root.get("orderId").get("isCancelled"), 0);
        predicates[3] = cb.equal(root.get("productId").get("id"), productId);
        cr.select(root).where(predicates);

        Query<OrderDetails> query = session.createQuery(cr);
        int size = query.getResultList().size();

        return size;

    }

    public Orders getOrderFromUserId(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Orders> cr = cb.createQuery(Orders.class);
        Root<Orders> root = cr.from(Orders.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("isCancelled"), 0);
        cr.select(root).where(predicates);

        Query<Orders> query = session.createQuery(cr);
        List<Orders> resultList = query.getResultList();

        return resultList.get(resultList.size() - 1);

    }

    public List<Product> getOrderdetailsForMustHaves() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<OrderDetails> root = cr.from(OrderDetails.class);

        Predicate isCancelled = cb.equal(root.get("isCancelled"), 0);
        cr.select(root.get("productId")).where(isCancelled);
        cr.groupBy(root.get("productId").get("id"));

        Query<Product> query = session.createQuery(cr).setMaxResults(4);
        List<Product> resultList = query.getResultList();

        return resultList;

    }

    public List<OrderDetails> getOrderDetailsFromOrderId(int orderId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OrderDetails> cr = cb.createQuery(OrderDetails.class);
        Root<OrderDetails> root = cr.from(OrderDetails.class);

        Predicate equal = cb.equal(root.get("orderId").get("id"), orderId);
        cr.select(root).where(equal);

        Query<OrderDetails> query = session.createQuery(cr);
        List<OrderDetails> resultList = query.getResultList();

        return resultList;

    }

    public List<OrderDetails> getOrderDetailsFromOrderIdForInvoice(int orderId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OrderDetails> cr = cb.createQuery(OrderDetails.class);
        Root<OrderDetails> root = cr.from(OrderDetails.class);

        Predicate equal = cb.equal(root.get("orderId").get("id"), orderId);
        Predicate isCancelled = cb.equal(root.get("isCancelled"), 0);
        cr.select(root).where(cb.and(equal, isCancelled));

        Query<OrderDetails> query = session.createQuery(cr);
        List<OrderDetails> resultList = query.getResultList();

        return resultList;

    }

    public List<Product> getAllProductIdsFromOrderId(int orderId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<OrderDetails> root = cr.from(OrderDetails.class);

        Predicate equal = cb.equal(root.get("orderId").get("id"), orderId);
        cr.select(root.get("productId")).where(equal);

        Query<Product> query = session.createQuery(cr);
        List<Product> resultList = query.getResultList();

        return resultList;

    }

    public List<Orders> getOrderListFromUserId(int userId, int typeId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Orders> cr = cb.createQuery(Orders.class);
        Root<Orders> root = cr.from(Orders.class);

        if (typeId == 1) {
            Predicate[] predicates = new Predicate[3];
            predicates[0] = cb.equal(root.get("userId").get("id"), userId);
            predicates[1] = cb.equal(root.get("isCancelled"), 0);
            predicates[2] = cb.equal(root.get("isCompleted"), 1);
            cr.select(root).where(predicates);
        } else if (typeId == 2) {
            Predicate[] predicates = new Predicate[3];
            predicates[0] = cb.equal(root.get("userId").get("id"), userId);
            predicates[1] = cb.equal(root.get("isCancelled"), 0);
            predicates[2] = cb.equal(root.get("isCompleted"), 0);
            cr.select(root).where(predicates);
        } else if (typeId == 3) {
            Predicate user = cb.equal(root.get("userId").get("id"), userId);
            Predicate isCancelled = cb.equal(root.get("isCancelled"), 1);
            Predicate isSingleItemCancelled = cb.equal(root.get("isSingleItemCancelled"), 1);
            cr.select(root).where(cb.and(cb.or(isCancelled, isSingleItemCancelled), user));
        }

        Query<Orders> query = session.createQuery(cr);
        List<Orders> resultList = query.getResultList();

        return resultList;

    }

    public List<CancelledOrders> getCancelledOrdersList(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CancelledOrders> cr = cb.createQuery(CancelledOrders.class);
        Root<CancelledOrders> root = cr.from(CancelledOrders.class);

        Predicate user = cb.equal(root.get("userId").get("id"), userId);
        Predicate isCancelled = cb.equal(root.get("isCancelled"), 1);
        Predicate isSingleItemCancelled = cb.equal(root.get("isSingleItemCancelled"), 1);
        cr.select(root).where(cb.and(cb.or(isCancelled, isSingleItemCancelled), user));

        Query<CancelledOrders> query = session.createQuery(cr);
        List<CancelledOrders> resultList = query.getResultList();

        return resultList;

    }

    public void cancelSingleProduct(int pId, int oId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<OrderDetails> criteriaUpdate = cb.createCriteriaUpdate(OrderDetails.class);
        Root<OrderDetails> root = criteriaUpdate.from(OrderDetails.class);
        criteriaUpdate.set("isCancelled", 1);
        criteriaUpdate.set("cancelledDate", LocalDateTime.now());

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("productId").get("id"), pId);
        predicates[1] = cb.equal(root.get("orderId").get("id"), oId);
        criteriaUpdate.where(predicates);

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public OrderDetails getOrderDetailsFromIdAndPId(int pId, int oId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OrderDetails> cr = cb.createQuery(OrderDetails.class);
        Root<OrderDetails> root = cr.from(OrderDetails.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("productId").get("id"), pId);
        predicates[1] = cb.equal(root.get("orderId").get("id"), oId);
        cr.where(predicates);

        Query<OrderDetails> query = session.createQuery(cr);
        OrderDetails orderDetails = null;

        try {
            orderDetails = query.getSingleResult();
        } catch (NoResultException e) {

        }

        return orderDetails;

    }

    public void cancelSingleOrderInOrders(int oId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<Orders> criteriaUpdate = cb.createCriteriaUpdate(Orders.class);
        Root<Orders> root = criteriaUpdate.from(Orders.class);
        criteriaUpdate.set("isSingleItemCancelled", 1);
        criteriaUpdate.set("cancelledDate", LocalDateTime.now());

        Predicate[] predicates = new Predicate[1];
        predicates[0] = cb.equal(root.get("id"), oId);
        criteriaUpdate.where(predicates);

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public void cancelOrder(int oId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<Orders> criteriaUpdate = cb.createCriteriaUpdate(Orders.class);
        Root<Orders> root = criteriaUpdate.from(Orders.class);
        criteriaUpdate.set("isCancelled", 1);
        criteriaUpdate.set("cancelledDate", LocalDateTime.now());

        Predicate[] predicates = new Predicate[1];
        predicates[0] = cb.equal(root.get("id"), oId);
        criteriaUpdate.where(predicates);

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public void cancelAllroducts(int oId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<OrderDetails> criteriaUpdate = cb.createCriteriaUpdate(OrderDetails.class);
        Root<OrderDetails> root = criteriaUpdate.from(OrderDetails.class);
        criteriaUpdate.set("isCancelled", 1);
        criteriaUpdate.set("cancelledDate", LocalDateTime.now());

        Predicate[] predicates = new Predicate[1];
        predicates[0] = cb.equal(root.get("orderId").get("id"), oId);
        criteriaUpdate.where(predicates);

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public List<Orders> getAllOrders(String searchText, int startIndex, int endIndex, String filters) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Orders> cr = cb.createQuery(Orders.class);
        Root<Orders> root = cr.from(Orders.class);

        if (searchText != "") {
            if (filters != "") {

                Predicate[] predicates = new Predicate[4];
                predicates[0] = cb.equal(root.get("isSingleItemCancelled"), 0);
                predicates[1] = cb.equal(root.get("isCancelled"), 0);
                predicates[2] = cb.equal(root.get("id"), searchText);

                if (filters.equals("0")) {
                    predicates[3] = cb.equal(root.get("orderDate").as(LocalDate.class), LocalDate.now());
                } else if (filters.equals("1")) {
                    predicates[3] = cb.between(root.get("orderDate").as(LocalDate.class), LocalDate.now().minusDays(7), LocalDate.now());
                } else if (filters.equals("2")) {
                    predicates[3] = cb.between(root.get("orderDate").as(LocalDate.class), LocalDate.now().minusMonths(1), LocalDate.now());
                }
                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("isSingleItemCancelled"), 0);
                predicates[1] = cb.equal(root.get("isCancelled"), 0);
                predicates[2] = cb.equal(root.get("id"), searchText);
                cr.select(root).where(predicates);
            }

        } else {
            if (filters != "") {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("isSingleItemCancelled"), 0);
                predicates[1] = cb.equal(root.get("isCancelled"), 0);

                if (filters.equals("0")) {
                    predicates[2] = cb.equal(root.get("orderDate").as(LocalDate.class), LocalDate.now());
                } else if (filters.equals("1")) {
                    predicates[2] = cb.between(root.get("orderDate").as(LocalDate.class), LocalDate.now().minusDays(7), LocalDate.now());
                } else if (filters.equals("2")) {
                    predicates[2] = cb.between(root.get("orderDate").as(LocalDate.class), LocalDate.now().minusMonths(1), LocalDate.now());
                }
                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[2];
                predicates[0] = cb.equal(root.get("isSingleItemCancelled"), 0);
                predicates[1] = cb.equal(root.get("isCancelled"), 0);
                cr.select(root).where(predicates);
            }
        }

        Query<Orders> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<Orders> resultList = query.getResultList();

        return resultList;

    }

    public List<Orders> getAllOrdersWhichAreNotAssignedAndNotCancelled() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Orders> cr = cb.createQuery(Orders.class);
        Root<Orders> root = cr.from(Orders.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(root.get("isCancelled"), 0);
        predicates[1] = cb.equal(root.get("isAssigned"), 0);
        predicates[2] = cb.equal(root.get("isCompleted"), 0);
        cr.select(root).where(predicates);

        Query<Orders> query = session.createQuery(cr);
        List<Orders> resultList = query.getResultList();

        return resultList;

    }

    public int getAllOrdersCount(String searchText, int startIndex, int endIndex, String filters) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Orders> cr = cb.createQuery(Orders.class);
        Root<Orders> root = cr.from(Orders.class);

        if (searchText != "") {
            if (filters != "") {

                Predicate[] predicates = new Predicate[4];
                predicates[0] = cb.equal(root.get("isSingleItemCancelled"), 0);
                predicates[1] = cb.equal(root.get("isCancelled"), 0);
                predicates[2] = cb.equal(root.get("id"), searchText);

                if (filters.equals("0")) {
                    predicates[3] = cb.equal(root.get("orderDate").as(LocalDate.class), LocalDate.now());
                } else if (filters.equals("1")) {
                    predicates[3] = cb.between(root.get("orderDate").as(LocalDate.class), LocalDate.now().minusDays(7), LocalDate.now());
                } else if (filters.equals("2")) {
                    predicates[3] = cb.between(root.get("orderDate").as(LocalDate.class), LocalDate.now().minusMonths(1), LocalDate.now());
                }
                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("isSingleItemCancelled"), 0);
                predicates[1] = cb.equal(root.get("isCancelled"), 0);
                predicates[2] = cb.equal(root.get("id"), searchText);
                cr.select(root).where(predicates);
            }

        } else {
            if (filters != "") {
                Predicate[] predicates = new Predicate[3];
                predicates[0] = cb.equal(root.get("isSingleItemCancelled"), 0);
                predicates[1] = cb.equal(root.get("isCancelled"), 0);

                if (filters.equals("0")) {
                    predicates[2] = cb.equal(root.get("orderDate").as(LocalDate.class), LocalDate.now());
                } else if (filters.equals("1")) {
                    predicates[2] = cb.between(root.get("orderDate").as(LocalDate.class), LocalDate.now().minusDays(7), LocalDate.now());
                } else if (filters.equals("2")) {
                    predicates[2] = cb.between(root.get("orderDate").as(LocalDate.class), LocalDate.now().minusMonths(1), LocalDate.now());
                }
                cr.select(root).where(predicates);

            } else {
                Predicate[] predicates = new Predicate[2];
                predicates[0] = cb.equal(root.get("isSingleItemCancelled"), 0);
                predicates[1] = cb.equal(root.get("isCancelled"), 0);
                cr.select(root).where(predicates);
            }
        }

        Query<Orders> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        int count = query.getResultList().size();

        return count;

    }

    public List<OrderDetails> getAllOrderDetails(String searchText, int startIndex, int endIndex, int oId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OrderDetails> cr = cb.createQuery(OrderDetails.class);
        Root<OrderDetails> root = cr.from(OrderDetails.class);

        if (searchText != "") {
            Predicate[] predicates = new Predicate[3];
            predicates[0] = cb.equal(root.get("isCancelled"), 0);
            predicates[1] = cb.equal(root.get("orderId").get("id"), oId);
            predicates[2] = cb.like(root.get("productId").get("name"), "%" + searchText + "%");
            cr.select(root).where(predicates);

        } else {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("isCancelled"), 0);
            predicates[1] = cb.equal(root.get("orderId").get("id"), oId);
            cr.select(root).where(predicates);
        }

        Query<OrderDetails> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<OrderDetails> resultList = query.getResultList();

        return resultList;

    }

    public int getAllOrderDetailsCount(String searchText, int startIndex, int endIndex, int oId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<OrderDetails> cr = cb.createQuery(OrderDetails.class);
        Root<OrderDetails> root = cr.from(OrderDetails.class);

        if (searchText != "") {
            Predicate[] predicates = new Predicate[3];
            predicates[0] = cb.equal(root.get("isCancelled"), 0);
            predicates[1] = cb.equal(root.get("orderId").get("id"), oId);
            predicates[2] = cb.like(root.get("productId").get("name"), "%" + searchText + "%");
            cr.select(root).where(predicates);

        } else {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("isCancelled"), 0);
            predicates[1] = cb.equal(root.get("orderId").get("id"), oId);
            cr.select(root).where(predicates);
        }

        Query<OrderDetails> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        int count = query.getResultList().size();

        return count;

    }

    public List<CancelledOrders> getCancelledOrderDetails(String searchText, int startIndex, int endIndex) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CancelledOrders> cr = cb.createQuery(CancelledOrders.class);
        Root<CancelledOrders> root = cr.from(CancelledOrders.class);

        if (searchText != "") {
            Predicate like = cb.like(root.get("productId").get("name"), "%" + searchText + "%");
            cr.select(root).where(like);

        } else {
            cr.select(root);
        }

        Query<CancelledOrders> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<CancelledOrders> resultList = query.getResultList();

        return resultList;

    }

    public int getCancelledOrderDetailsCount(String searchText) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CancelledOrders> cr = cb.createQuery(CancelledOrders.class);
        Root<CancelledOrders> root = cr.from(CancelledOrders.class);

        if (searchText != "") {
            Predicate like = cb.like(root.get("productId").get("name"), "%" + searchText + "%");
            cr.select(root).where(like);

        } else {
            cr.select(root);
        }

        Query<CancelledOrders> query = session.createQuery(cr);
        int count = query.getResultList().size();

        return count;

    }

    public List<Integer> getOrdersFromZipCode(String zipCode) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
        Root<Orders> root = cr.from(Orders.class);

        Predicate[] predicates = new Predicate[4];
        predicates[0] = cb.equal(root.get("isCompleted"), 0);
        predicates[1] = cb.equal(root.get("isCancelled"), 0);
        predicates[2] = cb.equal(root.get("isAssigned"), 0);
        predicates[3] = cb.equal(root.get("customerAddressId").get("zipCode"), zipCode);
        cr.select(root.get("id")).where(predicates);

        Query<Integer> query = session.createQuery(cr);
        List<Integer> resultList = query.getResultList();

        return resultList;

    }

    public Long getTotalOrdersCount() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Orders> root = cr.from(Orders.class);

        cr.select(cb.count(root));

        Query<Long> query = session.createQuery(cr);
        Long count = query.getSingleResult();

        return count;

    }

    public Long getTotalOrderedItemsCount() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<OrderDetails> root = cr.from(OrderDetails.class);

        cr.select(cb.count(root));

        Query<Long> query = session.createQuery(cr);
        Long count = query.getSingleResult();

        return count;

    }

}
