package com.eCommerce.repository;

import com.eCommerce.model.Category;
import com.eCommerce.model.SubCategory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class SubCategoryDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveSubCategory(SubCategory subCategory) {
        hibernateTemplate.save(subCategory);
    }

    @Transactional
    public void saveListOfSubCategory(List<SubCategory> subCategoryList) {

        for (SubCategory subCategory : subCategoryList) {
            hibernateTemplate.save(subCategory);
        }

    }

    @Transactional
    public void updateSubCategory(SubCategory subCategory) {
        hibernateTemplate.update(subCategory);
    }

    public List<SubCategory> getAllSubCategories(String search, int startIndex, int endIndex, Category category) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SubCategory> cr = cb.createQuery(SubCategory.class);
        Root<SubCategory> root = cr.from(SubCategory.class);

        if (search!=""){

            Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
            Predicate categoryId = cb.equal(root.get("categoryId"), category);
            Predicate subCatname = cb.like(root.get("name"), "%" + search + "%");
            Predicate catName = cb.like(root.get("categoryId").get("name"), "%" + search + "%");

            cr.select(root).where(cb.and(isDeleted, categoryId, cb.or(subCatname, catName)));

        } else {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            predicates[1] = cb.equal(root.get("categoryId"), category);
            cr.select(root).where(predicates);
        }

        Query<SubCategory> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<SubCategory> list = query.getResultList();

        return list;

    }

    public Page<SubCategory> getAllSubCategoriesDummy(String search, Pageable pageable, Category category) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SubCategory> cr = cb.createQuery(SubCategory.class);
        Root<SubCategory> root = cr.from(SubCategory.class);

        if (search!=""){

            Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
            Predicate categoryId = cb.equal(root.get("categoryId"), category);
            Predicate subCatname = cb.like(root.get("name"), "%" + search + "%");
            Predicate catName = cb.like(root.get("categoryId").get("name"), "%" + search + "%");

            cr.select(root).where(cb.and(isDeleted, categoryId, cb.or(subCatname, catName)));

        } else {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            predicates[1] = cb.equal(root.get("categoryId"), category);
            cr.select(root).where(predicates);
        }

        Query<SubCategory> query = session.createQuery(cr);
        int count = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<SubCategory> list = query.getResultList();

        return new PageImpl<>(list, pageable, count);

    }

    public int getTotalCount(String search, Category category) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SubCategory> cr = cb.createQuery(SubCategory.class);
        Root<SubCategory> root = cr.from(SubCategory.class);

        if (search!=""){
            Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
            Predicate categoryId = cb.equal(root.get("categoryId"), category);
            Predicate subCatname = cb.like(root.get("name"), "%" + search + "%");
            Predicate catName = cb.like(root.get("categoryId").get("name"), "%" + search + "%");
            cr.select(root).where(cb.and(isDeleted, categoryId, cb.or(subCatname, catName)));
        } else {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            predicates[1] = cb.equal(root.get("categoryId"), category);
            cr.select(root).where(predicates);
        }

        Query<SubCategory> query = session.createQuery(cr);
        int count = query.getResultList().size();

        return count;

    }

    public SubCategory getSubCategoryFromId(int id) {

        SubCategory subCategory = hibernateTemplate.load(SubCategory.class,id);
        return subCategory;

    }

    public int ifElementExistsInSubCat(String name) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SubCategory> cr = cb.createQuery(SubCategory.class);
        Root<SubCategory> root = cr.from(SubCategory.class);
        Predicate name1 = cb.equal(root.get("name"), name);
        cr.select(root).where(name1);

        Query<SubCategory> query = session.createQuery(cr);
        int size = query.getResultList().size();

        return size;

    }

    public List<SubCategory> getSubCatFromCatId(Category category) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SubCategory> cr = cb.createQuery(SubCategory.class);
        Root<SubCategory> root = cr.from(SubCategory.class);
        Predicate name1 = cb.equal(root.get("categoryId"), category);
        cr.select(root).where(name1);

        Query<SubCategory> query = session.createQuery(cr);
        List<SubCategory> list = query.getResultList();

        return list;

    }

}
