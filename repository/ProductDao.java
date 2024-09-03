package com.eCommerce.repository;

import com.eCommerce.dto.ShowProductDto;
import com.eCommerce.model.*;
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
import java.util.*;

@Repository
public class ProductDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveProduct(Product product) {
        hibernateTemplate.save(product);
    }

    @Transactional
    public void updateProduct(Product product) {
        hibernateTemplate.update(product);
    }

    @Transactional
    public void saveProductAttributes(ProductAttributes productAttributes) {
        hibernateTemplate.save(productAttributes);
    }

    @Transactional
    public void updateProductAttributes(ProductAttributes productAttributes) {
        hibernateTemplate.update(productAttributes);
    }

    @Transactional
    public void saveProductDocs(ProductDocuments productDocuments) {
        hibernateTemplate.save(productDocuments);
    }

    @Transactional
    public void updateProductDocs(ProductDocuments productDocuments) {
        hibernateTemplate.update(productDocuments);
    }

    public Product getProductFromId(int id) {

        Product product = hibernateTemplate.get(Product.class, id);
        return product;

    }

    public ProductAttributes getProductAttrs(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ProductAttributes> cr = cb.createQuery(ProductAttributes.class);
        Root<ProductAttributes> root = cr.from(ProductAttributes.class);

        Predicate pr1 = cb.equal(root.get("productId").get("id"), productId);
        cr.select(root).where(pr1);

        Query<ProductAttributes> query = session.createQuery(cr);
        ProductAttributes productAttributes = query.getSingleResult();

        return productAttributes;

    }

    public ProductDocuments getProductDocsFromProductId(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ProductDocuments> cr = cb.createQuery(ProductDocuments.class);
        Root<ProductDocuments> root = cr.from(ProductDocuments.class);

        Predicate pr1 = cb.equal(root.get("productId").get("id"), productId);
        cr.select(root).where(pr1);

        Query<ProductDocuments> query = session.createQuery(cr);
        ProductDocuments productDocuments = query.getSingleResult();

        return productDocuments;

    }

    public String getProductImageFromProductId(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> cr = cb.createQuery(String.class);
        Root<ProductDocuments> root = cr.from(ProductDocuments.class);

        Predicate pr1 = cb.equal(root.get("productId").get("id"), productId);
        cr.select(root.get("images")).where(pr1);

        Query<String> query = session.createQuery(cr);
        String images = query.getSingleResult();

        return images;

    }

    public List<Product> getProductsFromCatId(String catName, int startIndex, int endIndex, List<String> filters, String isSorting) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);
        Join<Product, ProductAttributes> productAttr = root.join("productAttributes", JoinType.INNER);

        if (filters.size()>1) {

            Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
            Predicate categoryName = cb.equal(root.get("categoryId").get("name"), catName);
            Predicate orPredicate = cb.conjunction();
            Predicate tempCol = cb.conjunction();
            Predicate tempSeller = cb.conjunction();
            boolean brandTest = true;
            boolean colorTest = true;
            boolean sellerTest = true;

            for (int i = 1; i < filters.size(); i++) {
                if (filters.get(i).startsWith("br")) {

                    Predicate brand = cb.equal(root.get("brand"), filters.get(i).substring(2));

                    if (brandTest) {
                        orPredicate = cb.and(orPredicate, brand);
                        brandTest = false;
                    } else {
                        Predicate temp3 = cb.and(tempSeller, brand);
                        orPredicate = cb.or(orPredicate, temp3);
                    }

                } else if (filters.get(i).startsWith("col")) {

                    Predicate color = cb.like(productAttr.get("color"), "%" + filters.get(i).substring(3) + "%");

                    if (colorTest) {
                        tempCol = orPredicate;
                        orPredicate = cb.and(orPredicate, color);
                        colorTest = false;

                    } else {
                        Predicate temp1 = cb.and(tempCol, color);
                        orPredicate = cb.or(orPredicate, temp1);
                    }

                } else if (filters.get(i).startsWith("sel")) {
                    Predicate seller = cb.equal(root.get("sellerId").get("id"), filters.get(i).substring(3));

                    if (sellerTest) {
                        tempSeller = orPredicate;
                        orPredicate = cb.and(orPredicate, seller);
                        sellerTest = false;
                    } else {
                        Predicate temp2 = cb.and(tempCol, seller);
                        orPredicate = cb.or(orPredicate, temp2);
                    }

                } else if (filters.get(i).startsWith("last")) {

                    Predicate arrivals;

                    if (filters.get(i).substring(4).equals("30")) {
                        arrivals = cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(1));
                    } else if (filters.get(i).substring(4).equals("60")) {
                        arrivals = cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(2));
                    } else {
                        arrivals = cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(3));
                    }

                    orPredicate = cb.and(orPredicate, arrivals);

                } else if (filters.get(i).startsWith("dis")) {
                    Predicate discount = cb.greaterThanOrEqualTo(productAttr.get("discount"), filters.get(i).substring(3));
                    orPredicate = cb.and(orPredicate, discount);
                } else {
                    Predicate cost = cb.lessThanOrEqualTo(root.get("cost"), filters.get(i));
                    orPredicate = cb.and(orPredicate, cost);
                }

            }

            Predicate and = cb.and(isDeleted, categoryName);
            cr.select(root).where(cb.and(and, orPredicate));

        }

        else {

            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.equal(root.get("isDeleted"), 0);
            predicates[1] = cb.equal(root.get("categoryId").get("name"), catName);
            cr.select(root).where(predicates);

        }

        if (!isSorting.equals("")) {

            if (isSorting.equals("htol")) {
                cr.orderBy(cb.desc(root.get("cost")));
            }
            else {
                cr.orderBy(cb.asc(root.get("cost")));
            }

        }

        Query<Product> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<Product> product = query.getResultList();

        return product;

    }

    public int getCountForProductsFromCatId(String catName) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("isDeleted"), 0);
        predicates[1] = cb.equal(root.get("categoryId").get("name"), catName);
        cr.select(root).where(predicates);

        Query<Product> query = session.createQuery(cr);
        int count = query.getResultList().size();

        return count;

    }

    public List<Product> getProductsFromCatIdForSearch(String search, int startIndex, int endIndex, List<String> filters, String isSorting) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);

        String[] searchWords = search.split(" ");
        Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);

        Predicate orPredicate = cb.disjunction();
        int j=0;
        int x=0;

        for (int i = 0; i < searchWords.length; i++) {

            Predicate likePredicate1 = cb.like(root.get("tags"), "%" + searchWords[i] + "%");
            Predicate likePredicate2 = cb.like(root.get("name"), "%" + searchWords[i] + "%");
            Predicate likePredicate3 = cb.like(root.get("categoryId").get("name"), "%" + searchWords[i] + "%");
            Predicate likePredicate4 = cb.like(root.get("subCategoryId").get("name"), "%" + searchWords[i] + "%");
            Predicate likePredicate5 = cb.equal(root.get("cost").as(String.class), searchWords[i]);
            Predicate likePredicate6 = cb.like(root.get("brand"), "%" + searchWords[i] + "%");
            orPredicate = cb.or(orPredicate, likePredicate1, likePredicate2, likePredicate3, likePredicate4, likePredicate5, likePredicate6);

            if (j==2) {
                Predicate likePredicate7 = cb.lessThanOrEqualTo(root.get("cost"), Integer.parseInt(searchWords[i]));
                orPredicate = cb.and(orPredicate, likePredicate7);
            }
            if (x==2) {
                Predicate likePredicate7 = cb.greaterThanOrEqualTo(root.get("cost"), Integer.parseInt(searchWords[i]));
                orPredicate = cb.and(orPredicate, likePredicate7);
            }

            if (searchWords[i].equals("less") || searchWords[i].equals("than")) {
                j++;
            } else {
                j=0;
            }

            if (searchWords[i].equals("more") || searchWords[i].equals("greater") || searchWords[i].equals("than")) {
                x++;
            } else {
                x=0;
            }

        }

        Join<Product, ProductAttributes> productAttr = root.join("productAttributes", JoinType.INNER);

        if (filters.size()>1) {

            Predicate orPredicate1 = cb.conjunction();
            Predicate tempCol = cb.conjunction();
            Predicate tempSeller = cb.conjunction();
            boolean brandTest = true;
            boolean colorTest = true;
            boolean sellerTest = true;

            for (int i = 1; i < filters.size(); i++) {
                if (filters.get(i).startsWith("br")) {

                    Predicate brand = cb.equal(root.get("brand"), filters.get(i).substring(2));

                    if (brandTest) {
                        orPredicate1 = cb.and(orPredicate1, brand);
                        brandTest = false;
                    } else {
                        Predicate temp3 = cb.and(tempSeller, brand);
                        orPredicate1 = cb.or(orPredicate1, temp3);
                    }

                } else if (filters.get(i).startsWith("col")) {

                    Predicate color = cb.like(productAttr.get("color"), "%" + filters.get(i).substring(3) + "%");

                    if (colorTest) {
                        tempCol = orPredicate1;
                        orPredicate1 = cb.and(orPredicate1, color);
                        colorTest = false;

                    } else {
                        Predicate temp1 = cb.and(tempCol, color);
                        orPredicate1 = cb.or(orPredicate1, temp1);
                    }

                } else if (filters.get(i).startsWith("sel")) {
                    Predicate seller = cb.equal(root.get("sellerId").get("id"), filters.get(i).substring(3));

                    if (sellerTest) {
                        tempSeller = orPredicate1;
                        orPredicate1 = cb.and(orPredicate1, seller);
                        sellerTest = false;
                    } else {
                        Predicate temp2 = cb.and(tempCol, seller);
                        orPredicate1 = cb.or(orPredicate1, temp2);
                    }

                } else if (filters.get(i).startsWith("last")) {

                    Predicate arrivals;

                    if (filters.get(i).substring(4).equals("30")) {
                        arrivals = cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(1));
                    } else if (filters.get(i).substring(4).equals("60")) {
                        arrivals = cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(2));
                    } else {
                        arrivals = cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(3));
                    }

                    orPredicate1 = cb.and(orPredicate1, arrivals);

                } else if (filters.get(i).startsWith("dis")) {
                    Predicate discount = cb.greaterThanOrEqualTo(productAttr.get("discount"), filters.get(i).substring(3));
                    orPredicate1 = cb.and(orPredicate1, discount);
                } else {
                    Predicate cost = cb.lessThanOrEqualTo(root.get("cost"), filters.get(i));
                    orPredicate1 = cb.and(orPredicate1, cost);
                }

            }

            Predicate maninAnd = cb.and(isDeleted, orPredicate);
            cr.select(root).where(cb.and(maninAnd, orPredicate1));

        }

        else {

            cr.select(root).where(cb.and(isDeleted, orPredicate));

        }

        if (!isSorting.equals("")) {

            if (isSorting.equals("htol")) {
                cr.orderBy(cb.desc(root.get("cost")));
            }
            else {
                cr.orderBy(cb.asc(root.get("cost")));
            }

        }

        Query<Product> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<Product> product = query.getResultList();

        return product;

    }

    public int getCountForProductsFromCatIdForSearch(String search) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);

        String[] searchWords = search.split(" ");
        Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);

        Predicate orPredicate = cb.disjunction();
        int j=0;
        int x=0;

        for (int i = 0; i < searchWords.length; i++) {

            System.out.println(searchWords[i]+"==========================");

            Predicate likePredicate1 = cb.like(root.get("tags"), "%" + searchWords[i] + "%");
            Predicate likePredicate2 = cb.like(root.get("name"), "%" + searchWords[i] + "%");
            Predicate likePredicate3 = cb.like(root.get("categoryId").get("name"), "%" + searchWords[i] + "%");
            Predicate likePredicate4 = cb.like(root.get("subCategoryId").get("name"), "%" + searchWords[i] + "%");
            Predicate likePredicate5 = cb.equal(root.get("cost").as(String.class), searchWords[i]);
            Predicate likePredicate6 = cb.like(root.get("brand"), "%" + searchWords[i] + "%");
            orPredicate = cb.or(orPredicate, likePredicate1, likePredicate2, likePredicate3, likePredicate4, likePredicate5, likePredicate6);

            if (j==2) {
                Predicate likePredicate7 = cb.lessThanOrEqualTo(root.get("cost"), Integer.parseInt(searchWords[i]));
                orPredicate = cb.and(orPredicate, likePredicate7);
            }
            if (x==2) {
                Predicate likePredicate7 = cb.greaterThanOrEqualTo(root.get("cost"), Integer.parseInt(searchWords[i]));
                orPredicate = cb.and(orPredicate, likePredicate7);
            }

            if (searchWords[i].equals("less") || searchWords[i].equals("than")) {
                j++;
            } else {
                j=0;
            }

            if (searchWords[i].equals("more") || searchWords[i].equals("greater") || searchWords[i].equals("than")) {
                x++;
            } else {
                x=0;
            }

        }

        cr.select(root).where(cb.and(isDeleted, orPredicate));

        Query<Product> query = session.createQuery(cr);
        int count = query.getResultList().size();

        return count;

    }


    public int doProductExist(String name) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);
        Predicate name1 = cb.equal(root.get("name"), name);
        cr.select(root).where(name1);

        Query<Product> query = session.createQuery(cr);
        int test = query.getResultList().size();

        return test;

    }

    public List<Product> getAllProductList(String search, int startIndex, int endIndex) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);

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

        Query<Product> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<Product> list = query.getResultList();

        return list;

    }

    public List<Product> getAllProductsWhereIsDeletedIsZero(int startIndex, int endIndex, List<String> filters, String isSorting) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);
        Join<Product, ProductAttributes> productAttr = root.join("productAttributes", JoinType.INNER);

        if (filters.size()>1) {

            Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
            Predicate orPredicate = cb.conjunction();
            Predicate tempCol = cb.conjunction();
            Predicate tempSeller = cb.conjunction();
            boolean brandTest = true;
            boolean colorTest = true;
            boolean sellerTest = true;

            for (int i=1; i<filters.size(); i++) {
                if (filters.get(i).startsWith("br")){

                    Predicate brand = cb.equal(root.get("brand"), filters.get(i).substring(2));

                    if (brandTest) {
                        orPredicate = cb.and(orPredicate, brand);
                        brandTest=false;
                    }
                    else {
                        Predicate temp3 = cb.and(tempSeller, brand);
                        orPredicate = cb.or(orPredicate, temp3);
                    }

                }
                else if(filters.get(i).startsWith("col")) {

                    Predicate color = cb.like(productAttr.get("color"), "%" + filters.get(i).substring(3) + "%");

                    if (colorTest) {
                        tempCol = orPredicate;
                        orPredicate = cb.and(orPredicate, color);
                        colorTest=false;

                    }
                    else {
                        Predicate temp1 = cb.and(tempCol, color);
                        orPredicate = cb.or(orPredicate, temp1);
                    }

                }

                else if(filters.get(i).startsWith("sel")) {
                    Predicate seller = cb.equal(root.get("sellerId").get("id"), filters.get(i).substring(3));

                    Predicate orPredicate1 = cb.conjunction();

                    if (sellerTest) {
                        tempSeller = orPredicate;
                        orPredicate = cb.and(orPredicate, seller);
                        sellerTest=false;
                    }
                    else {
                        Predicate temp2 = cb.and(tempCol, seller);
                        orPredicate = cb.or(orPredicate, temp2);
                    }

                }

                else if(filters.get(i).startsWith("last")) {

                    Predicate arrivals;

                    if (filters.get(i).substring(4).equals("30")) {
                        arrivals = cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(1));
                    }
                    else if (filters.get(i).substring(4).equals("60")) {
                        arrivals = cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(2));
                    }
                    else {
                        arrivals = cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.now().minusMonths(3));
                    }

                    orPredicate = cb.and(orPredicate, arrivals);

                }

                else if(filters.get(i).startsWith("dis")) {
                    Predicate discount = cb.greaterThanOrEqualTo(productAttr.get("discount"), filters.get(i).substring(3));
                    orPredicate = cb.and(orPredicate, discount);
                }

                else {
                    Predicate cost = cb.lessThanOrEqualTo(root.get("cost"), filters.get(i));
                    orPredicate = cb.and(orPredicate, cost);
                }

            }

            cr.select(root).where(cb.and(isDeleted, orPredicate));

        }

        else {

            Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
            cr.select(root).where(isDeleted);

        }

        if (!isSorting.equals("")) {

            if (isSorting.equals("htol")) {
                cr.orderBy(cb.desc(root.get("cost")));
            }
            else {
                cr.orderBy(cb.asc(root.get("cost")));
            }

        }

        Query<Product> query = session.createQuery(cr);
        query.setFirstResult(startIndex);
        query.setMaxResults(endIndex);
        List<Product> list = query.getResultList();

        return list;

    }

    public int getCountOfAllProductsWhereIsDeletedIsZero() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);

        Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
        cr.select(root).where(isDeleted);

        Query<Product> query = session.createQuery(cr);
        int total = query.getResultList().size();

        return total;

    }

    public int getTotalCount(String search) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);

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

        Query<Product> query = session.createQuery(cr);
        int count = query.getResultList().size();

        return count;

    }

    public void deleteProduct(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<Product> criteriaUpdate = cb.createCriteriaUpdate(Product.class);
        Root<Product> root = criteriaUpdate.from(Product.class);
        criteriaUpdate.set("isDeleted", 1);
        criteriaUpdate.where(cb.equal(root.get("id"), productId));

        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

    }

    public Tuple getMaxAndMinCost() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createQuery(Tuple.class);
        Root<Product> root = cr.from(Product.class);

        Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
        cr.multiselect(cb.max(root.get("cost")), cb.min(root.get("cost"))).where(isDeleted);

        Query<Tuple> query = session.createQuery(cr);
        Tuple max = query.getSingleResult();

        return max;

    }

    public List<String> getBrandsName() {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> cr = cb.createQuery(String.class);
        Root<Product> root = cr.from(Product.class);

        Predicate isDeleted = cb.equal(root.get("isDeleted"), 0);
        cr.select(root.get("brand")).distinct(true).where(isDeleted);

        Query<String> query = session.createQuery(cr);
        List<String> brands = query.getResultList();

        return brands;

    }

    public String getProductImgNameProductId(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> cr = cb.createQuery(String.class);
        Root<ProductDocuments> root = cr.from(ProductDocuments.class);

        Predicate pr1 = cb.equal(root.get("productId").get("id"), productId);
        cr.select(root.get("images")).where(pr1);

        Query<String> query = session.createQuery(cr);

        String[] splitImgs = query.getSingleResult().split(", ");
        List<String> imgs = new ArrayList<>(Arrays.asList(splitImgs));
        Collections.sort(imgs);
        String imageName = imgs.get(0);

        return imageName;

    }

    public String getProductNameProductId(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> cr = cb.createQuery(String.class);
        Root<Product> root = cr.from(Product.class);

        Predicate pr1 = cb.equal(root.get("id"), productId);
        cr.select(root.get("name")).where(pr1);

        Query<String> query = session.createQuery(cr);
        String name = query.getSingleResult();

        return name;

    }

    public Double getProductCostProductId(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Double> cr = cb.createQuery(Double.class);
        Root<Product> root = cr.from(Product.class);

        Predicate pr1 = cb.equal(root.get("id"), productId);
        cr.select(root.get("cost")).where(pr1);

        Query<Double> query = session.createQuery(cr);
        Double cost = query.getSingleResult();

        return cost;

    }

    public Tuple getProductNameAndCostProductId(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createQuery(Tuple.class);
        Root<Product> root = cr.from(Product.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("id"), productId);
        predicates[1] = cb.equal(root.get("isDeleted"), 0);
        cr.multiselect(root.get("name"), root.get("cost")).where(predicates);

        Query<Tuple> query = session.createQuery(cr);
        Tuple tuple = query.getSingleResult();

        return tuple;

    }

    public Seller getSellerByProductId(int productId) {

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Seller> cr = cb.createQuery(Seller.class);
        Root<Product> root = cr.from(Product.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("id"), productId);
        predicates[1] = cb.equal(root.get("isDeleted"), 0);
        cr.select(root.get("sellerId")).where(predicates);

        Query<Seller> query = session.createQuery(cr);
        Seller seller = null;

        try {
            seller = query.getSingleResult();
        }
        catch (NoResultException e) {

        }

        return seller;

    }


}
