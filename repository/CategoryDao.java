package com.eCommerce.repository;

import com.eCommerce.dto.ToShowCategoryInLandingPageDto;
import com.eCommerce.model.Category;
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
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CategoryDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveCategory(Category category) {
        hibernateTemplate.save(category);
    }

    @Transactional
    public void updateCategory(Category category) {
        hibernateTemplate.update(category);
    }

    public Page<Category> getAllCategories(String search, Pageable pageable) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Category> cr = cb.createQuery(Category.class);
        Root<Category> root = cr.from(Category.class);

        if (search != "") {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            predicates[1] = cb.like(root.get("name"), "%" + search + "%");
            cr.select(root).where(predicates);
        } else {
            Predicate[] predicates = new Predicate[1];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            cr.select(root).where(predicates);
        }

        Query<Category> query = session.createQuery(cr);

        int count = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Category> list = query.getResultList();

        return new PageImpl<>(list, pageable, count);

    }

    public Category getCategoryFromId(int id) {

        Category category = hibernateTemplate.load(Category.class, id);
        return category;

    }

    public int ifElementExists(String name) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Category> cr = cb.createQuery(Category.class);
        Root<Category> root = cr.from(Category.class);
        Predicate name1 = cb.equal(root.get("name"), name);
        cr.select(root).where(name1);

        Query<Category> query = session.createQuery(cr);
        int size = query.getResultList().size();

        return size;

    }

    public List<Category> getAllCategoryNames() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Category> cr = cb.createQuery(Category.class);
        Root<Category> root = cr.from(Category.class);

        cr.multiselect(root.get("id"),root.get("name"));

        Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
        cr.select(root).where(isDeleted);

        Query<Category> query = session.createQuery(cr);
        List<Category> list = query.getResultList();

        return list;

    }

}
