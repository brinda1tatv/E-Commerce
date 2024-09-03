package com.eCommerce.repository;

import com.eCommerce.dto.GetAllWishLists;
import com.eCommerce.dto.GetAllWishlistIdAndName;
import com.eCommerce.model.*;
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
import java.util.List;

@Repository
public class WishListDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveInWishList(WishList wishList) {
        hibernateTemplate.save(wishList);
    }

    @Transactional
    public void updateInWishList(WishList wishList) {
        hibernateTemplate.update(wishList);
    }

    @Transactional
    public void saveInWishListItems(WishListItems wishListItems) {
        hibernateTemplate.save(wishListItems);
    }

    @Transactional
    public void updateInWishListItems(WishListItems wishListItems) {
        hibernateTemplate.update(wishListItems);
    }

    @Transactional
    public void saveInWishListTokens(WishListTokens wishListTokens) {
        hibernateTemplate.save(wishListTokens);
    }

    @Transactional
    public void updateInWishListTokens(WishListTokens wishListTokens) {
        hibernateTemplate.update(wishListTokens);
    }

    @Transactional
    public void saveInSharedWishList(SharedWishList sharedWishList) {
        hibernateTemplate.save(sharedWishList);
    }

    @Transactional
    public void updateInSharedWishList(SharedWishList sharedWishList) {
        hibernateTemplate.update(sharedWishList);
    }

    public WishList getwishListFromId(int id) {

        WishList wishList = hibernateTemplate.get(WishList.class, id);
        return wishList;

    }

    public List<SharedWishList> getSharedwwshListFromUserId(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SharedWishList> cr = cb.createQuery(SharedWishList.class);
        Root<SharedWishList> root = cr.from(SharedWishList.class);

        Predicate equal = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root).where(equal);

        Query<SharedWishList> query = session.createQuery(cr);
        List<SharedWishList> list = query.getResultList();

        return list;

    }

    public int ifNameExistsInWishList(int userId, String name) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<WishList> cr = cb.createQuery(WishList.class);
        Root<WishList> root = cr.from(WishList.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("userId").get("id"), userId);
        predicates[1] = cb.equal(root.get("name"), name);
        cr.select(root).where(predicates);

        Query<WishList> query = session.createQuery(cr);
        int size = query.getResultList().size();

        if (size>0) {
            return 1;
        }
        return 0;

    }

    public List<GetAllWishLists> getAllWishlistsDataForWishListPage(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GetAllWishLists> cr = cb.createQuery(GetAllWishLists.class);
        Root<WishList> root = cr.from(WishList.class);

        Predicate equal = cb.equal(root.get("userId").get("id"), userId);
        cr.multiselect(root.get("id"), root.get("name"), root.get("description")).where(equal);

        Query<GetAllWishLists> query = session.createQuery(cr);
        List<GetAllWishLists> list = query.getResultList();

        return list;

    }

    public List<GetAllWishlistIdAndName> getAllWishlists(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GetAllWishlistIdAndName> cr = cb.createQuery(GetAllWishlistIdAndName.class);
        Root<WishList> root = cr.from(WishList.class);

        Predicate equal = cb.equal(root.get("userId").get("id"), userId);
        cr.multiselect(root.get("id"), root.get("name")).where(equal);

        Query<GetAllWishlistIdAndName> query = session.createQuery(cr);
        List<GetAllWishlistIdAndName> list = query.getResultList();

        return list;

    }

    public WishListItems ifProductExistsInWishListItems(int wishlistId, int pId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<WishListItems> cr = cb.createQuery(WishListItems.class);
        Root<WishListItems> root = cr.from(WishListItems.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(root.get("wishListId").get("id"), wishlistId);
        predicates[1] = cb.equal(root.get("productId").get("id"), pId);
        predicates[2] = cb.equal(root.get("isRemoved"), 0);
        cr.select(root).where(predicates);

        Query<WishListItems> query = session.createQuery(cr);

        WishListItems wishListItems = null;
        try {
            wishListItems = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return wishListItems;

    }

    public List<Integer> ifWishListExistsForUser(int userId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
        Root<WishList> root = cr.from(WishList.class);

        Predicate equal = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root.get("id")).where(equal);

        Query<Integer> query = session.createQuery(cr);
        List<Integer> list = query.getResultList();

        return list;

    }

    public List<Object[]> listOfProductsFromWlId(int wishListId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> cr = cb.createQuery(Object[].class);
        Root<WishListItems> root = cr.from(WishListItems.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("wishListId").get("id"), wishListId);
        predicates[1] = cb.equal(root.get("isRemoved"), 0);
        cr.multiselect(root.get("productId").get("id"), root.get("wishListId").get("id")).where(predicates);

        Query<Object[]> query = session.createQuery(cr);
        List<Object[]> list = query.getResultList();

        return list;

    }

    public List<Product> getProductIdFromWlId(int wishListId, String searchText, String yourFilters) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<WishListItems> root = cr.from(WishListItems.class);

        Predicate equal1 = cb.equal(root.get("wishListId").get("id"), wishListId);
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
                    cr.orderBy(cb.desc(root.get("createdDate")));
                } else {
                    cr.orderBy(cb.asc(root.get("createdDate")));
                }

            }

        }

        Query<Product> query = session.createQuery(cr);
        List<Product> list = query.getResultList();

        return list;

    }

    public void deleteProductFromWishlist(int productId, int wishlistId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<WishListItems> criteriaUpdate = cb.createCriteriaUpdate(WishListItems.class);
        Root<WishListItems> root = criteriaUpdate.from(WishListItems.class);
        criteriaUpdate.set("isRemoved", 1);
        criteriaUpdate.where(cb.and((cb.equal(root.get("productId").get("id"), productId)), (cb.equal(root.get("wishListId").get("id"), wishlistId))));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public WishListTokens checkIfMailAndWishListExists(String email, int wishId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<WishListTokens> cr = cb.createQuery(WishListTokens.class);
        Root<WishListTokens> root = cr.from(WishListTokens.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("recipientEmail"), email);
        predicates[1] = cb.equal(root.get("wishListId").get("id"), wishId);
        cr.select(root).where(predicates);

        Query<WishListTokens> query = session.createQuery(cr);
        WishListTokens wishListTokens = null;

        try {
            wishListTokens = query.getSingleResult();
        } catch (NoResultException e){

        }

        return wishListTokens;

    }

    public boolean checkToken(String token, int wishlistId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<WishListTokens> cr = cb.createQuery(WishListTokens.class);
        Root<WishListTokens> root = cr.from(WishListTokens.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("token"), token);
        predicates[1] = cb.equal(root.get("wishListId").get("id"), wishlistId);
        cr.select(root).where(predicates);

        Query<WishListTokens> query = session.createQuery(cr);
        List<WishListTokens> list = query.getResultList();

        return !list.isEmpty();

    }

    public boolean validateUser(String mail, int wishlistId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<WishListTokens> cr = cb.createQuery(WishListTokens.class);
        Root<WishListTokens> root = cr.from(WishListTokens.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("recipientEmail"), mail);
        predicates[1] = cb.equal(root.get("wishListId").get("id"), wishlistId);
        cr.select(root).where(predicates);

        Query<WishListTokens> query = session.createQuery(cr);
        List<WishListTokens> list = query.getResultList();

        return !list.isEmpty();

    }

    public boolean addInSharedList(int ownerUserId, int userId, int wishlistId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SharedWishList> cr = cb.createQuery(SharedWishList.class);
        Root<SharedWishList> root = cr.from(SharedWishList.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = cb.equal(root.get("ownerUserId").get("id"), ownerUserId);
        predicates[1] = cb.equal(root.get("wishListId").get("id"), wishlistId);
        predicates[2] = cb.equal(root.get("userId").get("id"), userId);
        cr.select(root).where(predicates);

        Query<SharedWishList> query = session.createQuery(cr);
        List<SharedWishList> list = query.getResultList();

        return !list.isEmpty();

    }

}
