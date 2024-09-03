package com.eCommerce.service;

import com.eCommerce.dto.*;
import com.eCommerce.helper.*;
import com.eCommerce.model.*;
import com.eCommerce.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminService {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private StockDao stockDao;

    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private OtpDao otpDao;

    @Autowired
    private WalletTransactionReasonDao walletTransactionReasonDao;

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private UserMainDao userMainDao;

    @Autowired
    private NotificationsDao notificationsDao;

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private MailService mailService;

    @Autowired
    private EmailLogDao emailLogDao;

    @Autowired
    private CarrierDao carrierDao;

    @Autowired
    private SalesDao salesDao;

    @Autowired
    private CancelledOrdersDao cancelledOrdersDao;

    public int ifNameExists(String name) {

        int size = categoryDao.ifElementExists(name);
        return size;

    }

    public int ifSubNameExists(String name) {

        int size = subCategoryDao.ifElementExistsInSubCat(name);
        return size;

    }

    @Transactional
    public List<ToShowCategoryDto> showCategory(String search, int currentPage, int pageSize) {

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Category> allCategoriesDummy = categoryDao.getAllCategories(search.trim(), pageable);

        List<Category> listOfCategory = allCategoriesDummy.getContent();
        int totalPages = allCategoriesDummy.getTotalPages();

        List<ToShowCategoryDto> list = new ArrayList<>();

        for (Category category : listOfCategory) {

            ToShowCategoryDto toShowCategoryDto = new ToShowCategoryDto();
            toShowCategoryDto.setId(category.getId());
            toShowCategoryDto.setName(category.getName());
            toShowCategoryDto.setDescription(category.getDescription());
            toShowCategoryDto.setCount(totalPages);

            list.add(toShowCategoryDto);

        }

        return list;

    }

    @Transactional
    public String AddCategory(ToAddCategoryDto toAddCategoryDto, int userId) {

        int size = ifNameExists(toAddCategoryDto.getCatName());

        if (size > 0) {
            return "catName";
        } else {
            Category category = new Category();
            category.setName(toAddCategoryDto.getCatName());
            category.setDescription(toAddCategoryDto.getCatDesc());

            category.setDeleted(false);
            category.setCreatedDate(LocalDateTime.now());

            User user = userDao.getUser(userId);
            category.setCreatedBy(user);

            List<String> subCats = toAddCategoryDto.getSubCats();

            List<SubCategory> listOfSubCategories = new ArrayList<>();

            int j = 1;

            if (toAddCategoryDto.getNoOfSubCat() > 0 && subCats.size() == (toAddCategoryDto.getNoOfSubCat() * 2)) {

                for (int i = 0; i < toAddCategoryDto.getSubCats().size() - 1; i = i + 2) {

                    SubCategory subCategory = new SubCategory();
                    subCategory.setCategoryId(category);

                    boolean isNameContainsCharsOnly = StringUtils.isAlpha(subCats.get(i));

                    if (!isNameContainsCharsOnly) {
                        return "NotNumberSubCategory" + j;
                    }

                    if (subCats.get(i) != "") {

                        int sizeOfSubCatName = ifSubNameExists(subCats.get(i));

                        if (sizeOfSubCatName > 0) {
                            return "SubCategory" + j;
                        }

                        subCategory.setName(subCats.get(i));

                        if (subCats.get(i + 1) == "") {
                            return "Description" + j;
                        }

                        subCategory.setDescription(subCats.get(i + 1));

                        subCategory.setDeleted(false);

                        subCategory.setCreatedDate(LocalDateTime.now());
                        subCategory.setCreatedBy(user);

                        listOfSubCategories.add(subCategory);

                    } else {
                        return "NullSubCategory" + j;

                    }

                    j++;

                }

            }

            category.setTotalSubCategories(toAddCategoryDto.getNoOfSubCat());
            categoryDao.saveCategory(category);

            subCategoryDao.saveListOfSubCategory(listOfSubCategories);

            return "success";
        }

    }

    @Transactional
    public boolean editCategory(EditCategoryDto editCategoryDto, int userId) {

        Category category = categoryDao.getCategoryFromId(editCategoryDto.getCatId());

        if (!editCategoryDto.getName().equals(category.getName())) {
            int size = ifNameExists(editCategoryDto.getName());
            if (size > 0) {
                return false;
            }
        }

        category.setName(editCategoryDto.getName());
        category.setDescription(editCategoryDto.getDesc());
        category.setModifiedDate(LocalDateTime.now());

        User user = userDao.getUser(userId);

        category.setModifiedBy(user);

        categoryDao.updateCategory(category);

        return true;

    }

    @Transactional
    public void deleteCategory(int categoryId) {

        Category category = categoryDao.getCategoryFromId(categoryId);
        category.setDeleted(true);
        category.setModifiedDate(LocalDateTime.now());

        User user = userDao.getUser(1);

        category.setModifiedBy(user);

        categoryDao.updateCategory(category);

    }

    @Transactional
    public List<ToShowSubCategoryDto> showAllSubCategory(int catId, String search, int currentPage, int pageSize) {

        Category category = categoryDao.getCategoryFromId(catId);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<SubCategory> allSubCategoriesDummy = subCategoryDao.getAllSubCategoriesDummy(search.trim(), pageable, category);

        List<SubCategory> listOfCategory = allSubCategoriesDummy.getContent();
        int totalPages = allSubCategoriesDummy.getTotalPages();

        List<ToShowSubCategoryDto> list = new ArrayList<>();

        for (SubCategory subCategory : listOfCategory) {

            ToShowSubCategoryDto toShowCategoryDto = new ToShowSubCategoryDto();
            toShowCategoryDto.setId(subCategory.getId());
            toShowCategoryDto.setCatName(category.getName());
            toShowCategoryDto.setSubCatName(subCategory.getName());
            toShowCategoryDto.setDescription(subCategory.getDescription());
            toShowCategoryDto.setCount(totalPages);

            list.add(toShowCategoryDto);

        }

        return list;

    }

    @Transactional
    public boolean AddSubCategory(ToAddSubCategoryDto toAddSubCategoryDto) {

        int size = ifSubNameExists(toAddSubCategoryDto.getCatName());

        if (size > 0) {
            return false;
        } else {
            User user = userDao.getUser(1);

            Category category = categoryDao.getCategoryFromId(toAddSubCategoryDto.getCatId());
            category.setTotalSubCategories(category.getTotalSubCategories()+1);
            category.setModifiedDate(LocalDateTime.now());
            category.setModifiedBy(user);
            categoryDao.updateCategory(category);

            SubCategory subCategory = new SubCategory();
            subCategory.setCategoryId(category);
            subCategory.setName(toAddSubCategoryDto.getCatName());
            subCategory.setDescription(toAddSubCategoryDto.getCatDesc());
            subCategory.setDeleted(false);
            subCategory.setCreatedDate(LocalDateTime.now());
            subCategory.setCreatedBy(user);

            subCategoryDao.saveSubCategory(subCategory);

            return true;
        }

    }

    @Transactional
    public boolean editSubCategory(EditSubCategoryDto editSubCategoryDto) {

        SubCategory subCategory = subCategoryDao.getSubCategoryFromId(editSubCategoryDto.getSubcateId());

        if (!editSubCategoryDto.getName().equals(subCategory.getName())) {
            int sizeForSubcatName = ifNameExists(editSubCategoryDto.getName());
            int sizeForCatName = ifSubNameExists(editSubCategoryDto.getName());

            if (sizeForCatName > 0 || sizeForSubcatName > 0) {
                return false;
            }
        }

        User user = userDao.getUser(1);

        Category category = categoryDao.getCategoryFromId(editSubCategoryDto.getCatId());
        category.setModifiedDate(LocalDateTime.now());
        category.setModifiedBy(user);
        categoryDao.updateCategory(category);

        subCategory.setName(editSubCategoryDto.getName());
        subCategory.setDescription(editSubCategoryDto.getDesc());
        subCategory.setModifiedDate(LocalDateTime.now());
        subCategory.setModifiedBy(user);
        subCategoryDao.updateSubCategory(subCategory);

        return true;

    }

    @Transactional
    public void deleteSubCategory(int subcateId, int catId) {

        User user = userDao.getUser(1);

        Category category = categoryDao.getCategoryFromId(catId);
        category.setModifiedDate(LocalDateTime.now());
        category.setModifiedBy(user);
        categoryDao.updateCategory(category);

        SubCategory subCategory = subCategoryDao.getSubCategoryFromId(subcateId);
        subCategory.setDeleted(true);
        subCategory.setModifiedDate(LocalDateTime.now());
        subCategory.setModifiedBy(user);
        subCategoryDao.updateSubCategory(subCategory);

    }

    @Transactional
    public List<ShowSubCatNameDto> showSubCatNames(int catId) {

        Category category = categoryDao.getCategoryFromId(catId);

        List<SubCategory> listOfCategory = subCategoryDao.getSubCatFromCatId(category);

        List<ShowSubCatNameDto> list = new ArrayList<>();

        for (SubCategory subCategory : listOfCategory) {

            ShowSubCatNameDto showSubCatNameDto = new ShowSubCatNameDto();
            showSubCatNameDto.setId(subCategory.getId());
            showSubCatNameDto.setName(subCategory.getName());

            list.add(showSubCatNameDto);

        }

        return list;

    }

    @Transactional
    public List<ShowProductDto> showProducts(String search, int curPage) {

        int startIndex = ((curPage - 1) * 5);
        int endIndex = 5;

        List<Product> list = productDao.getAllProductList(search.trim(), startIndex, endIndex);
        int count = productDao.getTotalCount(search.trim());

        List<ShowProductDto> listOfDto = new ArrayList<>();

        for (Product product : list) {

            Integer stock = stockDao.getTotalStockCountOfProduct(product.getId());

            ShowProductDto showProductDto = new ShowProductDto();
            showProductDto.setId(product.getId());
            showProductDto.setName(product.getName());
            showProductDto.setCategoryName(product.getCategoryId().getName());
            showProductDto.setPrice(product.getCost().toString());
            showProductDto.setStock(stock.toString());
            showProductDto.setCreatedDate(product.getCreatedDate().toLocalDate().toString());
            showProductDto.setSellerName(product.getSellerId().getBusinessName());
            showProductDto.setCount(count);

            listOfDto.add(showProductDto);

        }

        return listOfDto;

    }

    @Transactional
    public void deleteProduct(int productId) {

        productDao.deleteProduct(productId);

    }

    @Transactional
    public String addProduct(AddProductDto addProductDto, HttpSession session, int userId) {

        User user = userDao.getUser(userId);

        List<MultipartFile> files = addProductDto.getFormFileMultiple();

        Category category = categoryDao.getCategoryFromId(Integer.parseInt(addProductDto.getCatName()));
        SubCategory subCategory = subCategoryDao.getSubCategoryFromId(Integer.parseInt(addProductDto.getSubCatName()));

        int isProduct = productDao.doProductExist(addProductDto.getName());

        if (isProduct > 0) {
            return "Product Name Already Exists!";
        }

        if (addProductDto.getFormFileMultiple().size() != (addProductDto.getColor().size() * 2)) {
            return "Enter enough number of files";
        }

        int y = 0;
        for (int i = 0; i < files.size(); i++) {
            MultipartFile tempFile = files.get(i);
            String filename = tempFile.getOriginalFilename().toLowerCase();

            for (int j = 0; j < addProductDto.getColor().size(); j++) {

                if (filename.startsWith(addProductDto.getColor().get(j).toLowerCase())) {
                    y++;
                }

            }

        }

        if (y != files.size()) {
            return "Enter proper fileName in proper order as per the given instructions";
        }

        if (addProductDto.getActualCost() != null) {
            double discount = addProductDto.getCost() * (100) / addProductDto.getActualCost();
            if ((addProductDto.getDiscount() + Math.round(discount)) != 100) {
                return "Enter proper discount!";
            }
        }

        Product product = new Product();
        product.setName(addProductDto.getName());
        product.setCategoryId(category);
        product.setSubCategoryId(subCategory);
        product.setBrand(addProductDto.getBrand());
        product.setCost(addProductDto.getCost());
        product.setWeight(addProductDto.getWeight());
        product.setProdDescription(addProductDto.getProdDesc());

        String temp = "";
        for (int i = 0; i < addProductDto.getTags().size(); i++) {
            temp += (addProductDto.getTags().get(i) + ", ");
        }
        String test = temp.substring(0, temp.length() - 2);
        product.setTags(test);

        product.setCreatedBy(user);
        product.setCreatedDate(LocalDateTime.now());
        product.setDeleted(false);

        List<Seller> doSellerExist = sellerDao.doSellerExist(addProductDto.getBEmail());

        if (doSellerExist.size() == 0) {

            Seller seller = new Seller();
            seller.setBusinessName(addProductDto.getBName());
            seller.setWebsite(addProductDto.getBWebsite());
            seller.setEmail(addProductDto.getBEmail());
            seller.setContact(addProductDto.getBPhone());
            seller.setCreatedBy(user);
            seller.setCreatedDate(LocalDateTime.now());
            sellerDao.saveSeller(seller);

            product.setSellerId(seller);

        } else {

            Seller seller = doSellerExist.get(0);
            seller.setBusinessName(addProductDto.getBName());
            seller.setWebsite(addProductDto.getBWebsite());
            seller.setContact(addProductDto.getBPhone());
            seller.setModifiedBy(user);
            seller.setModifiedDate(LocalDateTime.now());
            sellerDao.updateSeller(seller);

            product.setSellerId(seller);

        }

        ProductAttributes productAttributes = new ProductAttributes();

        String temp2 = "";
        for (int i = 0; i < addProductDto.getColor().size(); i++) {
            temp2 += (addProductDto.getColor().get(i) + ", ");
        }
        String test2 = temp2.substring(0, temp2.length() - 2);
        productAttributes.setColor(test2);

        productAttributes.setProductId(product);
        productAttributes.setActualCost(addProductDto.getActualCost());
        productAttributes.setDiscount(addProductDto.getDiscount());

        product.setProductAttributes(productAttributes);
        productDao.saveProduct(product);

        ProductDocuments productDocuments = new ProductDocuments();
        productDocuments.setProductId(product);

        String temp3 = "";
        for (int i = 0; i < files.size(); i++) {
            MultipartFile tempFile = files.get(i);
            String filename = tempFile.getOriginalFilename();
            temp3 += filename + ", ";

            String uploadDir = session.getServletContext().getRealPath("/") + "WEB-INF" + File.separator + "resources"
                    + File.separator + "ProductDocs" + File.separator + product.getId() + File.separator + "Images" + File.separator;

            File directory = new File(uploadDir);

            if (!directory.exists()) {
                boolean mkdir = directory.mkdirs();
                if (!mkdir) {
                    System.out.println("Failed to create directory: " + uploadDir);
                }
            }

            Path filePath = Paths.get(uploadDir, filename);
            try {
                Files.copy(tempFile.getInputStream(), filePath);
            } catch (IOException e) {
                System.out.println("Error transferring file: " + e.getMessage());
            }
        }

        String test3 = temp3.substring(0, temp3.length() - 2);
        productDocuments.setImages(test3);

        MultipartFile tempFile = addProductDto.getFormFile();
        String filename = tempFile.getOriginalFilename();

        String uploadDir = session.getServletContext().getRealPath("/") + "WEB-INF" + File.separator + "resources"
                + File.separator + "ProductDocs" + File.separator + product.getId() + File.separator + "WarrantyDoc" + File.separator;

        File directory = new File(uploadDir);

        if (!directory.exists()) {
            boolean mkdir = directory.mkdirs();
            if (!mkdir) {
                System.out.println("Failed to create directory: " + uploadDir);
            }
        }

        Path filePath = Paths.get(uploadDir, filename);
        try {
            Files.copy(tempFile.getInputStream(), filePath);
        } catch (IOException e) {
            System.out.println("Error transferring file: " + e.getMessage());
        }

        productDocuments.setWarrantyDoc(addProductDto.getFormFile().getOriginalFilename());

        productDao.saveProductDocs(productDocuments);

        for (int i = 0; i < addProductDto.getColor().size(); i++) {

            int stockk = addProductDto.getStock() / addProductDto.getColor().size();

            Stock stock = new Stock();
            stock.setProductId(product);
            stock.setStock((int) Math.floor(stockk));
            stock.setColor(addProductDto.getColor().get(i));

            stockDao.saveStock(stock);

        }

        return "success";
    }

    @Transactional
    public String editProduct(AddProductDto addProductDto, HttpSession session, int userId) {

        User user = userDao.getUser(userId);

        List<MultipartFile> files = addProductDto.getFormFileMultiple();

        Category category = categoryDao.getCategoryFromId(Integer.parseInt(addProductDto.getCatName()));
        SubCategory subCategory = subCategoryDao.getSubCategoryFromId(Integer.parseInt(addProductDto.getSubCatName()));

        if (addProductDto.getFormFileMultiple().size() != (addProductDto.getColor().size() * 2)) {
            return "Enter enough number of files";
        }

        int j = 0;
        for (int i = 0; i < files.size(); i++) {
            MultipartFile tempFile = files.get(i);
            String filename = tempFile.getOriginalFilename().toLowerCase();

            if (!filename.startsWith(addProductDto.getColor().get(j).toLowerCase())) {
                return "Enter proper fileName in proper order as per the given instructions";
            }

            if (i % 2 != 0) {
                j++;
            }

        }

        if (addProductDto.getActualCost() != null) {
            double discount = addProductDto.getCost() * (100) / addProductDto.getActualCost();
            if (addProductDto.getDiscount() != discount) {
                return "Enter proper discount!";
            }
        }

        Product product = productDao.getProductFromId(addProductDto.getPId());
        product.setName(addProductDto.getName());
        product.setCategoryId(category);
        product.setSubCategoryId(subCategory);
        product.setBrand(addProductDto.getBrand());
        product.setCost(addProductDto.getCost());
        product.setWeight(addProductDto.getWeight());
        product.setProdDescription(addProductDto.getProdDesc());

        String temp = "";
        for (int i = 0; i < addProductDto.getTags().size(); i++) {
            temp += (addProductDto.getTags().get(i) + ", ");
        }
        String test = temp.substring(0, temp.length() - 2);
        product.setTags(test);

        product.setModifiedBy(user);
        product.setModifiedDate(LocalDateTime.now());
        product.setDeleted(false);

        List<Seller> doSellerExist = sellerDao.doSellerExist(addProductDto.getBEmail());

        if (doSellerExist.size() == 0) {

            Seller seller = new Seller();
            seller.setBusinessName(addProductDto.getBName());
            seller.setWebsite(addProductDto.getBWebsite());
            seller.setEmail(addProductDto.getBEmail());
            seller.setContact(addProductDto.getBPhone());
            seller.setCreatedBy(user);
            seller.setCreatedDate(LocalDateTime.now());
            sellerDao.saveSeller(seller);

            product.setSellerId(seller);

        } else {

            Seller seller = product.getSellerId();
            seller.setBusinessName(addProductDto.getBName());
            seller.setWebsite(addProductDto.getBWebsite());
            seller.setContact(addProductDto.getBPhone());
            seller.setModifiedBy(user);
            seller.setModifiedDate(LocalDateTime.now());
            sellerDao.updateSeller(seller);

            product.setSellerId(seller);

        }

        productDao.updateProduct(product);

        ProductAttributes productAttributes = product.getProductAttributes();

        String temp2 = "";
        for (int i = 0; i < addProductDto.getColor().size(); i++) {
            temp2 += (addProductDto.getColor().get(i) + ", ");
        }
        String test2 = temp2.substring(0, temp2.length() - 2);
        productAttributes.setColor(test2);

        productAttributes.setProductId(product);
        productAttributes.setActualCost(addProductDto.getActualCost());
        productAttributes.setDiscount(addProductDto.getDiscount());

        productDao.updateProductAttributes(productAttributes);

        ProductDocuments productDocuments = productDao.getProductDocsFromProductId(addProductDto.getPId());
        productDocuments.setProductId(product);

        boolean dummy = true;
        String temp3 = "";
        for (int i = 0; i < files.size(); i++) {
            MultipartFile tempFile = files.get(i);
            String filename = tempFile.getOriginalFilename();
            temp3 += filename + ", ";

            String uploadDir = session.getServletContext().getRealPath("/") + "WEB-INF" + File.separator + "resources"
                    + File.separator + "ProductDocs" + File.separator + product.getId() + File.separator + "Images" + File.separator;

            File directory = new File(uploadDir);

            if (!directory.exists()) {
                boolean mkdir = directory.mkdirs();
                if (!mkdir) {
                    System.out.println("Failed to create directory: " + uploadDir);
                }
            }

            File[] files1 = directory.listFiles();
            if (files1 != null && dummy) {
                for (File file : files1) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
                dummy = false;
            }


            Path filePath = Paths.get(uploadDir, filename);
            try {
                Files.copy(tempFile.getInputStream(), filePath);
            } catch (IOException e) {
                System.out.println("Error transferring file: " + e.getMessage());
            }
        }

        String test3 = temp3.substring(0, temp3.length() - 2);
        productDocuments.setImages(test3);

        MultipartFile tempFile = addProductDto.getFormFile();
        String filename = tempFile.getOriginalFilename();

        String uploadDir = session.getServletContext().getRealPath("/") + "WEB-INF" + File.separator + "resources"
                + File.separator + "ProductDocs" + File.separator + product.getId() + File.separator + "WarrantyDoc" + File.separator;

        File directory = new File(uploadDir);

        if (!directory.exists()) {
            boolean mkdir = directory.mkdirs();
            if (!mkdir) {
                System.out.println("Failed to create directory: " + uploadDir);
            }
        }

        File[] files1 = directory.listFiles();
        if (files1 != null) {
            for (File file : files1) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }

        Path filePath = Paths.get(uploadDir, filename);
        try {
            Files.copy(tempFile.getInputStream(), filePath);
        } catch (IOException e) {
            System.out.println("Error transferring file: " + e.getMessage());
        }

        productDocuments.setWarrantyDoc(addProductDto.getFormFile().getOriginalFilename());

        productDao.updateProductDocs(productDocuments);

        Stock stock = stockDao.getStockOfProduct(addProductDto.getPId());
        stock.setProductId(product);
        stock.setStock(addProductDto.getStock());

        stockDao.updateStock(stock);

        return "success";
    }

    @Transactional
    public EditProductDto showProductDetailsForEdit(int productId) {

        Product product = productDao.getProductFromId(productId);
        ProductAttributes productAttributes = productDao.getProductAttrs(productId);
        Stock stock = stockDao.getStockOfProduct(productId);
        ProductDocuments productDocuments = productDao.getProductDocsFromProductId(productId);

        EditProductDto editProductDto = new EditProductDto();
        editProductDto.setName(product.getName());
        editProductDto.setCatId(product.getCategoryId().getId());
        editProductDto.setSubCatId(product.getSubCategoryId().getId());
        editProductDto.setBrand(product.getBrand());
        editProductDto.setWeight(product.getWeight());
        editProductDto.setColor(productAttributes.getColor());
        editProductDto.setCost(product.getCost());

        if (productAttributes.getActualCost() != null) {
            editProductDto.setActualCost(productAttributes.getActualCost());
        } else {
            editProductDto.setActualCost(0.0);
        }

        if (productAttributes.getDiscount() != null) {
            editProductDto.setDiscount(productAttributes.getDiscount());
        } else {
            editProductDto.setDiscount(0);
        }

        editProductDto.setFormFileMultiple(productDocuments.getImages());
        editProductDto.setFormFile(productDocuments.getWarrantyDoc());
        editProductDto.setStock(stock.getStock());
        editProductDto.setProdDesc(product.getProdDescription());
        editProductDto.setTags(product.getTags());
        editProductDto.setBusiName(product.getSellerId().getBusinessName());
        editProductDto.setBusiEmail(product.getSellerId().getEmail());
        editProductDto.setBusiWebsite(product.getSellerId().getWebsite());
        editProductDto.setBusiPhone(product.getSellerId().getContact());

        return editProductDto;

    }

    public boolean ifUserExists(String email) {
        int size = userDao.ifUserExists(email);

        if (size > 0) {
            return true;
        } else {
            return false;
        }

    }

    @Transactional
    public String generateOtp(String email, HttpServletRequest request) {

        int isValid = otpDao.isEmailValid(email);

        if (isValid >= 3) {

            return "You have exceeded the maximum number of attempts. Please try again after 24 hrs.";

        }

        String otp = OtpGenerator.generateOTP();

        OTPData otpData = new OTPData();
        otpData.setEmail(email);
        otpData.setOtp(otp);

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(1);

        otpData.setStartTime(startTime);
        otpData.setEndTime(endTime);

        System.out.println(otp + "=============================" + endTime);

        otpDao.saveOtp(otpData);

        String baseURL = new GetContextPath().getProjectBaseURL(request);

        String sendTo = email;
        String subject = "Your OTP For Creating New Account";
        String body = "\n" +
                "Dear User," + ",\n" +
                "\n" +
                "Welcome to our community!\n" +
                "\n" +
                "We are thrilled to have you on board. To proceed with your registration, please use the One-Time Password (OTP) provided below:\n" +
                "\n" +
                "Your OTP: " + otp + "\n" +
                "\n" +
                "Please enter this code on our website to complete your verification process. The OTP is valid for a limited time, so please use it as soon as possible.\n" +
                "\n" +
                "If you did not request this OTP, please disregard this email.\n" +
                "\n" +
                "If you have any questions or need further assistance, feel free to contact our support team.\n" +
                "\n" +
                "Thank you for choosing us.\n" +
                "\n" +
                "Best regards,\n" +
                "The Clutch&Carry Team";

//                mailService.send(sendTo, subject, body);

        EmailLogs emailLogs = new EmailLogs();
        emailLogs.setEmail(email);
        emailLogs.setRecipient("User");
        emailLogs.setSentDateTime(LocalDateTime.now());
        emailLogs.setAction(subject);

        emailLogDao.saveEmailLog(emailLogs);

        return "true";

    }

    @Transactional
    public String submitCreateAccount(OtpValidationDto OtpValidationDto) {

        int isValid = otpDao.isOtpValid(OtpValidationDto.getOtp(), LocalDateTime.now(), OtpValidationDto.getEmail());

        if (isValid == 0) {

            return "Invalid OTP!!";

        }

        UserMain userMain = new UserMain();

        String salt = PasswordHash.generateSalt();
        String hashedPassword = PasswordHash.hashPassword(OtpValidationDto.getPswd(), salt);

        userMain.setPasswordHash(hashedPassword);
        userMain.setSalt(salt);
        userMain.setCreatedDate(LocalDateTime.now());
        userMain.setCreatedBy(userDao.getUser(1));

        System.out.println(OtpValidationDto.getPswd()+"==================================");

        userDao.saveUserMain(userMain);

        User user = new User();

        String name = OtpValidationDto.getName();
        String fName = name.substring(0, name.indexOf(' '));
        String lName = name.substring(name.indexOf(' ') + 1);

        user.setFirstName(fName);
        user.setLastName(lName);
        user.setEmail(OtpValidationDto.getEmail());
        user.setGender(OtpValidationDto.getGender());
        user.setPhone(OtpValidationDto.getPhone());
        user.setDeleted(false);
        user.setBlocked(false);

        Role role = roleDao.getRole(2);

        user.setRoleId(role);
        user.setUserMainId(userMain);

        userDao.saveUser(user);

        Wallet wallet = new Wallet();
        wallet.setUserId(user);
        wallet.setTransactionType(Wallet.TransactionType.CREDIT);

        WalletTransactionReason walletTransactionReason = walletTransactionReasonDao.getWalletTrReasonId(1);

        double amount = Math.random() * (walletTransactionReason.getMaxPrice() - walletTransactionReason.getMinPrice()) + walletTransactionReason.getMinPrice();

        wallet.setWalletTransactionReasonId(walletTransactionReason);
        wallet.setBalance(amount);
        wallet.setTransactionAmount(amount);
        walletDao.saveWallet(wallet);

        UserNotifications userNotifications = new UserNotifications();
        userNotifications.setUserId(user);
        userNotifications.setNotiOn(true);

        notificationsDao.saveUserNotifications(userNotifications);

        userMain.setCreatedBy(user);
        userDao.updateUserMain(userMain);

        return "true";

    }

    @Transactional
    public String submitLogIn(LogInDto logInDto) {

        List<UserMain> userMainList = userDao.getUserMain(logInDto.getEmail());

        if (userMainList.isEmpty()) {
            return "This mail is not registered or Your account may have blocked!";
        }

        boolean isPswdTrue = PasswordHash.checkPassword(logInDto.getPswd(), userMainList.get(0).getSalt(), userMainList.get(0).getPasswordHash());

        if (!isPswdTrue) {
            return "Wrong Password!! If you don't remember then please click on Forgot Password.";
        }

        return "true";

    }

    public void setSessionId(int userId, String sessionId) {
        UserSessions userSessions = userDao.ifUserSessionExists(userId);

        if (userSessions != null) {
            userSessions.setSessionId(sessionId);
            userDao.updateUserSessions(userSessions);
        } else {
            UserSessions userSessions1 = new UserSessions();
            userSessions1.setUserId(userDao.getUser(userId));
            userSessions1.setSessionId(sessionId);
            userDao.saveUserSessions(userSessions1);
        }

    }

    @Transactional
    public List<AddCouponDto> showCoupons(String search, int curPage) {

        int startIndex = ((curPage - 1) * 5);
        int endIndex = 5;

        List<Coupon> allCoupons = couponDao.getAllCoupons(search.trim(), startIndex, endIndex);
        int allCouponsCount = couponDao.getAllCouponsCount(search.trim());

        List<AddCouponDto> list = new ArrayList<>();

        for (Coupon coupon : allCoupons) {

            AddCouponDto addCouponDto = new AddCouponDto();
            addCouponDto.setId(coupon.getId());
            addCouponDto.setCode(coupon.getCode());
            addCouponDto.setStartDate(coupon.getStartDate().toLocalDate().toString());
            addCouponDto.setEndDate(coupon.getEndDate().toLocalDate().toString());
            addCouponDto.setType(coupon.getType());
            addCouponDto.setMinAmount(String.valueOf(coupon.getMinimumPrice()));
            addCouponDto.setDiscount(String.valueOf(coupon.getDiscount()));
            addCouponDto.setStartDateTime(coupon.getStartDate().toString());
            addCouponDto.setEndDateTime(coupon.getEndDate().toString());
            addCouponDto.setCount(allCouponsCount);

            list.add(addCouponDto);

        }

        return list;

    }

    @Transactional
    public String addCoupon(AddCouponDto addCouponDto, int userId) {

        boolean ifExists = couponDao.ifCodeExists(addCouponDto.getCode());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDate = LocalDateTime.parse(addCouponDto.getStartDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(addCouponDto.getEndDate(), formatter);

        if (ifExists) {
            return "This code is already taken! Please enter unique coupon code.";
        } else if (endDate.toLocalDate().isBefore(LocalDate.now()) || endDate.toLocalDate().isBefore(startDate.toLocalDate())) {
            return "Please enter a valid end date (It must be of today or after today)";
        } else if (Integer.parseInt(addCouponDto.getMinAmount()) < 10) {
            return "Please enter a valid amount";
        } else if (addCouponDto.getType().equals("Discount") && Integer.parseInt(addCouponDto.getDiscount()) <= 0) {
            return "please enter a valid discount";
        } else if (addCouponDto.getType().equals("Flat") && Integer.parseInt(addCouponDto.getDiscount()) < 10) {
            return "please enter a valid discount";
        } else {

            Coupon coupon = new Coupon();
            coupon.setCode(addCouponDto.getCode());
            coupon.setStartDate(startDate);
            coupon.setEndDate(endDate);
            coupon.setType(addCouponDto.getType());
            coupon.setMinimumPrice(Double.parseDouble(addCouponDto.getMinAmount()));
            coupon.setDiscount(Double.parseDouble(addCouponDto.getDiscount()));
            coupon.setDeleted(false);
            coupon.setCreatedBy(userDao.getUser(userId));
            coupon.setAppliedCount(0);

            couponDao.saveCoupon(coupon);

            return "success";

        }

    }

    @Transactional
    public String editCoupon(AddCouponDto addCouponDto, int userId) {

        boolean ifExists = couponDao.ifCodeExists(addCouponDto.getCode());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDate = LocalDateTime.parse(addCouponDto.getStartDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(addCouponDto.getEndDate(), formatter);

        if (!ifExists) {
            return "This code doesn't exists!";
        } else if (endDate.toLocalDate().isBefore(LocalDate.now()) || endDate.toLocalDate().isBefore(startDate.toLocalDate())) {
            return "Please enter a valid end date (It must be of today or after today)";
        } else if (Integer.parseInt(addCouponDto.getMinAmount()) < 10) {
            return "Please enter a valid amount";
        } else if (addCouponDto.getType().equals("Discount") && Integer.parseInt(addCouponDto.getDiscount()) < 0) {
            return "please enter a valid discount";
        } else if (!addCouponDto.getType().equals("Flat") && Integer.parseInt(addCouponDto.getDiscount()) < 10) {
            return "please enter a valid discount";
        }

        Coupon coupon = couponDao.getCoupon(addCouponDto.getCode());
        coupon.setCode(addCouponDto.getCode());
        coupon.setStartDate(startDate);
        coupon.setEndDate(endDate);
        coupon.setType(addCouponDto.getType());
        coupon.setMinimumPrice(Double.parseDouble(addCouponDto.getMinAmount()));
        coupon.setDiscount(Double.parseDouble(addCouponDto.getDiscount()));
        coupon.setDeleted(false);
        coupon.setModifiedBy(userDao.getUser(userId));

        couponDao.updateCoupon(coupon);

        return "success";


    }

    @Transactional
    public void deleteCoupon(int id, int userId) {

        couponDao.deleteCoupon(id, userDao.getUser(userId));

    }

    @Transactional
    public List<GetAllUsersDto> showUsers(String searchText, int curPage, String filters, int type) {

        int startIndex = ((curPage - 1) * 5);
        int endIndex = 5;

        List<User> allUsers = userDao.getAllUsers(searchText.trim(), startIndex, endIndex, filters, type);
        int count = userDao.getAllUsersCount(searchText.trim(), filters, type);
        List<GetAllUsersDto> list = new ArrayList<>();

        for (User user : allUsers) {

            GetAllUsersDto getAllUsersDto = new GetAllUsersDto();
            getAllUsersDto.setId(user.getId());
            getAllUsersDto.setFirstName(user.getFirstName());
            getAllUsersDto.setLastName(user.getLastName());
            getAllUsersDto.setEmail(user.getEmail());
            getAllUsersDto.setContact(user.getPhone());
            getAllUsersDto.setRole(user.getRoleId().getRole());
            getAllUsersDto.setRoleName(user.getRoleId().getRoleName());
            getAllUsersDto.setBlocked(user.isBlocked());
            getAllUsersDto.setCreatedDate(new DateHelper().getDateInFormate(user.getUserMainId().getCreatedDate().toLocalDate()));
            getAllUsersDto.setCount(count);

            list.add(getAllUsersDto);

        }

        return list;

    }

    public List<Role> getRoles() {

        List<Role> roles = roleDao.getRoles();
        return roles;

    }

    public void blockUser(int userId) {

        userDao.blockUser(userId);
        UserSessions userSessions = userDao.ifUserSessionExists(userId);

        if (userSessions != null) {
            HttpSession session = SessionRegistry.getSession(userSessions.getSessionId());
            if (session != null) {
                session.invalidate();
                SessionRegistry.removeSession(userSessions.getSessionId());
            }
        }

    }

    public void unBlockUser(int userId) {
        userDao.unBlockUser(userId);
    }

    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
        UserSessions userSessions = userDao.ifUserSessionExists(userId);

        if (userSessions != null) {
            HttpSession session = SessionRegistry.getSession(userSessions.getSessionId());
            if (session != null) {
                session.invalidate();
                SessionRegistry.removeSession(userSessions.getSessionId());
            }
        }

    }

    @Transactional
    public String editUser(EditUserDto editUserDto, int userId) {

        List<Role> roles = roleDao.getRoles();
        Set<Integer> set = new HashSet<>();

        for (Role role : roles) {
            set.add(role.getRole());
        }

        int roleId = editUserDto.getRole();

        if (set.contains(roleId)) {

            User user = userDao.getUser(editUserDto.getId());

            if (user == null) {
                return "Enter a valid user";
            }

            if (user.getRoleId().getRole() != editUserDto.getRole()) {

                UserSessions userSessions = userDao.ifUserSessionExists(user.getId());

                if (userSessions != null) {
                    HttpSession session = SessionRegistry.getSession(userSessions.getSessionId());
                    if (session != null) {
                        session.invalidate();
                        SessionRegistry.removeSession(userSessions.getSessionId());
                    }
                }
            }

            user.setFirstName(editUserDto.getFirstName());
            user.setLastName(editUserDto.getLastName());
            user.setPhone(editUserDto.getPhone());
            user.setRoleId(roleDao.getRole(editUserDto.getRole()));
            user.getUserMainId().setModifiedDate(LocalDateTime.now());
            user.getUserMainId().setModifiedBy(userDao.getUser(userId));
            user.getUserMainId().setModifiedDate(LocalDateTime.now());
            userDao.updateUser(user);

            return "success";

        } else {

            return "Enter a valid role";

        }

    }

    public List<GetAllOrdersDto> showOrders(String searchText, int curPage, String filters) {

        int startIndex = ((curPage - 1) * 5);
        int endIndex = 5;

        List<Orders> orders = ordersDao.getAllOrders(searchText.trim(), startIndex, endIndex, filters);
        int count = ordersDao.getAllOrdersCount(searchText.trim(), startIndex, endIndex, filters);
        List<GetAllOrdersDto> list = new ArrayList<>();

        for (Orders orders1 : orders) {

            GetAllOrdersDto getAllOrdersDto = new GetAllOrdersDto();
            getAllOrdersDto.setId(orders1.getId());
            getAllOrdersDto.setOrderDate(new DateHelper().getDateInFormate(orders1.getOrderDate().toLocalDate()));
            getAllOrdersDto.setTotal(String.valueOf(orders1.getTotalAmount()));
            getAllOrdersDto.setOrderBy(orders1.getUserId().getFirstName() + " " + orders1.getUserId().getLastName());
            getAllOrdersDto.setIsCompleted(orders1.isCompleted());
            getAllOrdersDto.setPaymentMethod(orders1.getPaymentMethod());
            getAllOrdersDto.setCount(count);

            list.add(getAllOrdersDto);

        }

        return list;

    }

    public List<GetOrderDetailsDto> showOrderDetails(String searchText, int curPage, int oId) {

        int startIndex = ((curPage - 1) * 5);
        int endIndex = 5;

        List<OrderDetails> orderDetailsList = ordersDao.getAllOrderDetails(searchText.trim(), startIndex, endIndex, oId);
        int count = ordersDao.getAllOrderDetailsCount(searchText.trim(), startIndex, endIndex, oId);
        List<GetOrderDetailsDto> list = new ArrayList<>();

        for (OrderDetails orderDetails : orderDetailsList) {

            GetOrderDetailsDto getOrderDetailsDto = new GetOrderDetailsDto();
            getOrderDetailsDto.setId(orderDetails.getId());
            getOrderDetailsDto.setProduct(orderDetails.getProductId().getName());
            getOrderDetailsDto.setQty(String.valueOf(orderDetails.getQuantity()));
            getOrderDetailsDto.setPrice(String.valueOf(orderDetails.getPrice()));
            getOrderDetailsDto.setCount(count);

            list.add(getOrderDetailsDto);

        }

        return list;

    }

    public List<GetCancelledItems> showCancelledItems(String searchText, int curPage) {

        int startIndex = ((curPage - 1) * 5);
        int endIndex = 5;

        List<CancelledOrders> cancelledOrdersList = ordersDao.getCancelledOrderDetails(searchText.trim(), startIndex, endIndex);
        int count = ordersDao.getCancelledOrderDetailsCount(searchText.trim());
        List<GetCancelledItems> list = new ArrayList<>();

        for (CancelledOrders cancelledOrders : cancelledOrdersList) {

            GetCancelledItems getCancelledItems = new GetCancelledItems();
            getCancelledItems.setId(cancelledOrders.getId());
            getCancelledItems.setOrderId(cancelledOrders.getOrderId().getId());
            getCancelledItems.setProduct(cancelledOrders.getProductId().getName());
            getCancelledItems.setCancelledBy(cancelledOrders.getUserId().getFirstName() + " " + cancelledOrders.getUserId().getLastName());
            getCancelledItems.setReason(cancelledOrders.getReason());

            if (cancelledOrders.getReasonText() != null) {
                getCancelledItems.setNotes(cancelledOrders.getReasonText());
            } else {
                getCancelledItems.setNotes("-");
            }

            getCancelledItems.setCount(count);

            list.add(getCancelledItems);

        }

        return list;

    }

    @Transactional
    public String addAdmin(AddAdminDto addAdminDto, int userId, HttpServletRequest request) {

        List<Role> roles = roleDao.getRoles();
        Set<Integer> roleSet = new HashSet<>();
        Set<String> genderSet = new HashSet<>(Arrays.asList("male", "female", "other"));

        for (Role role : roles) {
            roleSet.add(role.getRole());
        }

        int roleId = Integer.parseInt(addAdminDto.getRole());
        String gender = addAdminDto.getGender();

        if (roleSet.contains(roleId)) {

            if (genderSet.contains(gender)) {

                UserMain userMain = new UserMain();

                String salt = PasswordHash.generateSalt();

                int fNameLength = addAdminDto.getFirstName().length();
                String tempPassword = "";

                if (fNameLength < 4) {
                    String fName = addAdminDto.getFirstName();
                    String lName = addAdminDto.getLastName();
                    String temp1 = fName.toUpperCase().charAt(0) + fName.toLowerCase().substring(1, fNameLength);
                    String temp2 = lName.toLowerCase().substring(0, temp1.length());
                    tempPassword = temp1 + temp2 + "@" + addAdminDto.getPhone().charAt(11);
                } else {
                    tempPassword = addAdminDto.getFirstName().toUpperCase().charAt(0) + addAdminDto.getFirstName().toLowerCase().substring(1, 4) + "@" + addAdminDto.getPhone().charAt(11);
                }

                String hashedPassword = PasswordHash.hashPassword(tempPassword, salt);

                userMain.setPasswordHash(hashedPassword);
                userMain.setSalt(salt);
                userMain.setCreatedDate(LocalDateTime.now());

                User user = new User();

                userMain.setCreatedBy(userDao.getUser(userId));

                userDao.saveUserMain(userMain);

                user.setFirstName(addAdminDto.getFirstName());
                user.setLastName(addAdminDto.getLastName());
                user.setEmail(addAdminDto.getEmail());
                user.setGender(addAdminDto.getGender());
                user.setPhone(addAdminDto.getPhone());
                user.setDeleted(false);
                user.setBlocked(false);

                Role role = roleDao.getRole(roleId);

                user.setRoleId(role);
                user.setUserMainId(userMain);

                userDao.saveUser(user);

                String baseURL = new GetContextPath().getProjectBaseURL(request);

                String sendTo = addAdminDto.getEmail();
                String subject = "Your Admin account created on Clutch And Carry!";
                String body = "\n" +
                        "Dear " + addAdminDto.getFirstName() + " " + addAdminDto.getLastName() + ",\n" +
                        "\n" +
                        "Welcome to our community!\n" +
                        "\n" +
                        "Your admin account just created on our website.\n" +
                        "\n" +
                        "Here are your credentials for the same.\n" +
                        "\n" +
                        "UserName(Email) : " + addAdminDto.getEmail() + "\n" +
                        "Password : " + tempPassword + "\n" +
                        "\n" +
                        "You can change your password any time.\n" +
                        "\n" +
                        "Just click on the link below to login to our website.\n" +
                        "\n" +
                        baseURL + "/login" + "\n" +
                        "\n" +
                        "If you have any questions or need further assistance, please feel free to reach out to our support team.\n" +
                        "\n" +
                        "Thank you for being a valued member of our community.\n" +
                        "\n" +
                        "Best regards,\n" +
                        "The Clutch&Carry Team";

//                mailService.send(sendTo, subject, body);

                EmailLogs emailLogs = new EmailLogs();
                emailLogs.setEmail(addAdminDto.getEmail());
                emailLogs.setRecipient(addAdminDto.getFirstName() + " " + addAdminDto.getLastName());
                emailLogs.setSentDateTime(LocalDateTime.now());
                emailLogs.setAction(subject);

                emailLogDao.saveEmailLog(emailLogs);

                return "success";

            } else {
                return "Enter a valid gender";
            }

        } else {

            return "Enter a valid role";

        }

    }

    @Transactional
    public String addCarrier(AddCarrierDto addCarrierDto, int userId, HttpServletRequest request) {

        Set<String> genderSet = new HashSet<>(Arrays.asList("male", "female", "other"));

        String gender = addCarrierDto.getGender();

        if (genderSet.contains(gender)) {

            UserMain userMain = new UserMain();

            String salt = PasswordHash.generateSalt();

            int fNameLength = addCarrierDto.getFirstName().length();
            String tempPassword = "";

            if (fNameLength < 4) {
                String fName = addCarrierDto.getFirstName();
                String lName = addCarrierDto.getLastName();
                String temp1 = fName.toUpperCase().charAt(0) + fName.toLowerCase().substring(1, fNameLength);
                String temp2 = lName.toLowerCase().substring(0, temp1.length());
                tempPassword = temp1 + temp2 + "@" + addCarrierDto.getPhone().charAt(11);
            } else {
                tempPassword = addCarrierDto.getFirstName().toUpperCase().charAt(0) + addCarrierDto.getFirstName().toLowerCase().substring(1, 4) + "@" + addCarrierDto.getPhone().charAt(11);
            }

            String hashedPassword = PasswordHash.hashPassword(tempPassword, salt);

            userMain.setPasswordHash(hashedPassword);
            userMain.setSalt(salt);
            userMain.setCreatedDate(LocalDateTime.now());

            User user = new User();

            userMain.setCreatedBy(userDao.getUser(userId));

            userDao.saveUserMain(userMain);

            user.setFirstName(addCarrierDto.getFirstName());
            user.setLastName(addCarrierDto.getLastName());
            user.setEmail(addCarrierDto.getEmail());
            user.setGender(addCarrierDto.getGender());
            user.setPhone(addCarrierDto.getPhone());
            user.setDeleted(false);
            user.setBlocked(false);

            Role role = roleDao.getRole(5);

            user.setRoleId(role);
            user.setUserMainId(userMain);

            userDao.saveUser(user);

            Carrier carrier = new Carrier();
            carrier.setUserId(user);
            carrier.setCity(addCarrierDto.getCity());
            carrier.setState(addCarrierDto.getState());
            carrier.setZipCode(addCarrierDto.getZipCode());
            carrier.setCompletedOrdersCount(0);

            carrierDao.saveCarrier(carrier);

            String baseURL = new GetContextPath().getProjectBaseURL(request);

            String sendTo = addCarrierDto.getEmail();
            String subject = "Your Carrier account created on Clutch And Carry!";
            String body = "\n" +
                    "Dear " + addCarrierDto.getFirstName() + " " + addCarrierDto.getLastName() + ",\n" +
                    "\n" +
                    "Welcome to our community!\n" +
                    "\n" +
                    "Your carrier account just created on our website.\n" +
                    "\n" +
                    "Here are your credentials for the same.\n" +
                    "\n" +
                    "UserName(Email) : " + addCarrierDto.getEmail() + "\n" +
                    "Password : " + tempPassword + "\n" +
                    "\n" +
                    "You can change your password any time.\n" +
                    "\n" +
                    "Just click on the link below to login to our website.\n" +
                    "\n" +
                    baseURL + "/login" + "\n" +
                    "\n" +
                    "If you have any questions or need further assistance, please feel free to reach out to our support team.\n" +
                    "\n" +
                    "Thank you for being a valued member of our community.\n" +
                    "\n" +
                    "Best regards,\n" +
                    "The Clutch&Carry Team";

//                mailService.send(sendTo, subject, body);

            EmailLogs emailLogs = new EmailLogs();
            emailLogs.setEmail(addCarrierDto.getEmail());
            emailLogs.setRecipient(addCarrierDto.getFirstName() + " " + addCarrierDto.getLastName());
            emailLogs.setSentDateTime(LocalDateTime.now());
            emailLogs.setAction(subject);

            emailLogDao.saveEmailLog(emailLogs);

            return "success";

        } else {
            return "Enter a valid gender";
        }

    }

    @Transactional
    public List<AddCarrierDto> showCarriers(String searchText, int curPage) {

        int startIndex = ((curPage - 1) * 5);
        int endIndex = 5;

        List<Carrier> carrierList = carrierDao.getAllCarriers(searchText.trim(), startIndex, endIndex);
        int count = carrierDao.getAllCarriersCount(searchText.trim());

        List<AddCarrierDto> list = new ArrayList<>();

        for (Carrier carrier : carrierList) {

            User user = carrier.getUserId();

            AddCarrierDto addCarrierDto = getAddCarrierDto(carrier, user, count);

            list.add(addCarrierDto);

        }

        return list;

    }

    private static @NotNull AddCarrierDto getAddCarrierDto(Carrier carrier, User user, int count) {
        AddCarrierDto addCarrierDto = new AddCarrierDto();
        addCarrierDto.setCarrierId(carrier.getId());
        addCarrierDto.setUserId(user.getId());
        addCarrierDto.setFirstName(user.getFirstName());
        addCarrierDto.setLastName(user.getLastName());
        addCarrierDto.setEmail(user.getEmail());
        addCarrierDto.setPhone(user.getPhone());
        addCarrierDto.setCity(carrier.getCity());
        addCarrierDto.setState(carrier.getState());
        addCarrierDto.setZipCode(String.valueOf(carrier.getZipCode()));
        addCarrierDto.setCompletedOrders(carrier.getCompletedOrdersCount());
        addCarrierDto.setCount(count);
        addCarrierDto.setBlocked(user.isBlocked());
        return addCarrierDto;
    }

    public List<Integer> getReleventOrders(int carrierId) {

        int zipCodeOfCarrier = Integer.parseInt(carrierDao.getZipCodeOfCarrier(carrierId));



//        List<Integer> ordersFromZipCode = ordersDao.getOrdersFromZipCode(zipCodeOfCarrier);
        List<Orders> ordersList = ordersDao.getAllOrdersWhichAreNotAssignedAndNotCancelled();

        List<Integer> relevantOrderIds = ordersList.stream()
                .filter(order -> {
                    int orderZipCode = Integer.parseInt(order.getCustomerAddressId().getZipCode());
                    return Math.abs(orderZipCode - zipCodeOfCarrier) <= 10;
                })
                .map(Orders::getId)
                .collect(Collectors.toList());


        return relevantOrderIds;

    }

    public void assignOrdersToCarrier(List<Integer> listOfOrederIds, int carrierId) {

        Carrier carrier = carrierDao.getCarrierFromId(carrierId);

        for (int oId : listOfOrederIds) {

            Orders order = ordersDao.getOrderFromId(oId);
            order.setAssigned(true);
            ordersDao.updateOrders(order);

            CarrierOrders carrierOrders = new CarrierOrders();
            carrierOrders.setCarrierId(carrier);
            carrierOrders.setOrderId(order);
            carrierOrders.setDelivered(false);
            carrierOrders.setNotAbleToDeliver(false);

            carrierDao.saveCarrierOrders(carrierOrders);

        }

    }

    public AdminDashboardDataDto getAdminDashData() {

        Long totalOrdersCount = ordersDao.getTotalOrdersCount();
        Long totalUsersCount = userDao.getTotalUsersCount();
        Double totalSales = salesDao.getTotalSales();
        Double totalRevenue = salesDao.getTotalRevenue();

        List<Tuple> totalSalesMonthlyTemp = salesDao.getTotalSalesMonthly();
        List<Tuple> totalRevenueMonthlyTemp = salesDao.getTotalRevenueMonthly();

        List<Double> totalSalesMonthly = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        List<Double> totalRevenueMonthly = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));

        for (Tuple tuple : totalSalesMonthlyTemp) {
            int month = (int) tuple.get(0);
            Double totalSalesAmountTemp = (Double) tuple.get(1);

            totalSalesMonthly.set(month - 1, totalSalesAmountTemp);
        }

        for (Tuple tuple : totalRevenueMonthlyTemp) {
            int month = (int) tuple.get(0);
            Double totalRevenueAmountTemp = (Double) tuple.get(1);

            totalRevenueMonthly.set(month - 1, totalRevenueAmountTemp);
        }

        Long totalOrderedItemsCount = ordersDao.getTotalOrderedItemsCount();
        Long totalCancelledItems = cancelledOrdersDao.getTotalCancelledOrders();

        int rate = (int) (totalCancelledItems * (100) / totalOrderedItemsCount);

        AdminDashboardDataDto adminDashboardDataDto = new AdminDashboardDataDto();
        adminDashboardDataDto.setTotalOrders(totalOrdersCount);
        adminDashboardDataDto.setTotalUsers(totalUsersCount);
        adminDashboardDataDto.setTotalSales(totalSales);
        adminDashboardDataDto.setTotalRevenue(totalRevenue);
        adminDashboardDataDto.setMonthlySales(totalSalesMonthly);
        adminDashboardDataDto.setMonthlyRevenue(totalRevenueMonthly);
        adminDashboardDataDto.setTotalOrderedItems(totalOrderedItemsCount);
        adminDashboardDataDto.setCancelledItems(totalCancelledItems);
        adminDashboardDataDto.setRateOfCancellation(rate);

        return adminDashboardDataDto;

    }

}
