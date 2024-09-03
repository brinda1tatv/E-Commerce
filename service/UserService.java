package com.eCommerce.service;

import com.eCommerce.dto.*;
import com.eCommerce.helper.*;
import com.eCommerce.model.*;
import com.eCommerce.repository.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ContactUsDao contactUsDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private RatingsDao ratingsDao;

    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private MailService mailService;

    @Autowired
    private VerificationTokenDao verificationTokenDao;

    @Autowired
    private EmailLogDao emailLogDao;

    @Autowired
    private CartDao cartDao;

    @Autowired
    private WishListDao wishListDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private WalletTransactionReasonDao walletTransactionReasonDao;

    @Autowired
    private TempOrdersDao tempOrdersDao;

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private StockDao stockDao;

    @Autowired
    private CancelledOrdersDao cancelledOrdersDao;

    @Autowired
    private NotificationsDao notificationsDao;

    @Autowired
    private SalesDao salesDao;

    public void contactUs(ContactUsDto contactUsDto) {

        ContactUs contactUs = new ContactUs();
        contactUs.setName(contactUsDto.getName());
        contactUs.setEmail(contactUsDto.getEmail());
        contactUs.setPhone(contactUsDto.getPhone());
        contactUs.setMessage(contactUsDto.getMessage());

        contactUsDao.saveContactUs(contactUs);

    }

    public List<ToShowCategoryInLandingPageDto> ShowCategoriesInLandingPage() {

        List<ToShowCategoryInLandingPageDto> list = new ArrayList<>();

        List<Category> listOfCategory = categoryDao.getAllCategoryNames();

        for (Category category : listOfCategory) {

            ToShowCategoryInLandingPageDto toShowCategoryInLandingPageDto = new ToShowCategoryInLandingPageDto();
            toShowCategoryInLandingPageDto.setCatId(category.getId());
            toShowCategoryInLandingPageDto.setCatName(category.getName());

            list.add(toShowCategoryInLandingPageDto);

        }

        return list;

    }

    public List<MustHavesDto> getMustHaves() {

        List<Product> orderdetailsList = ordersDao.getOrderdetailsForMustHaves();

        List<MustHavesDto> list = new ArrayList<>();

        for (Product product : orderdetailsList) {

            String images = productDao.getProductImageFromProductId(product.getId());
            String[] splitImgs = images.split(", ");

            MustHavesDto mustHavesDto = new MustHavesDto();
            mustHavesDto.setId(product.getId());
            mustHavesDto.setProductName(product.getName());
            mustHavesDto.setImage(splitImgs[splitImgs.length - 1]);
            mustHavesDto.setBrand(product.getBrand().replaceAll("\\s", ""));

            list.add(mustHavesDto);

        }

        return list;

    }

    public List<ShowProductInCardsDto> ShowProductsInCards(String search, int curPage, List<String> filters, String isSorting, Integer useId) {

        int startIndex = ((curPage - 1) * 5);
        int endIndex = 5;

        List<ShowProductInCardsDto> list = new ArrayList<>();

        String catName = search;
        List<Product> listOfProducts = new ArrayList<>();
        List<Integer> listOfWlIds = new ArrayList<>();
        List<Object[]> listOfProductIds = new ArrayList<>();
        int count;

        if (useId != null) {

            listOfWlIds = wishListDao.ifWishListExistsForUser(useId);

            if (listOfWlIds.size() != 0) {

                for (int i = 0; i < listOfWlIds.size(); i++) {

                    listOfProductIds.addAll(wishListDao.listOfProductsFromWlId(listOfWlIds.get(i)));

                }

            }

        }

        if (search.equals("all")) {
            listOfProducts = productDao.getAllProductsWhereIsDeletedIsZero(startIndex, endIndex, filters, isSorting);
            count = productDao.getCountOfAllProductsWhereIsDeletedIsZero();
        } else if (search.startsWith("cate10")) {
            catName = search.substring(6);
            listOfProducts = productDao.getProductsFromCatId(catName, startIndex, endIndex, filters, isSorting);
            count = productDao.getCountForProductsFromCatId(catName);
        } else {

            Analyzer analyzer = new StandardAnalyzer(StopAnalyzer.ENGLISH_STOP_WORDS_SET);

            String processedText = null;
            try {
                processedText = ProcessText.processText(analyzer, search);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            analyzer.close();

            listOfProducts = productDao.getProductsFromCatIdForSearch(processedText, startIndex, endIndex, filters, isSorting);
            count = productDao.getCountForProductsFromCatIdForSearch(processedText);

        }

        for (Product product : listOfProducts) {

            ShowProductInCardsDto showProductInCardsDto = new ShowProductInCardsDto();
            showProductInCardsDto.setProductId(product.getId());
            showProductInCardsDto.setName(product.getName());
            showProductInCardsDto.setCost(String.valueOf(product.getCost().intValue()));
            showProductInCardsDto.setCount(count);

            ProductAttributes productAttributes = productDao.getProductAttrs(product.getId());
            if (productAttributes.getDiscount() != null) {
                showProductInCardsDto.setDiscount(productAttributes.getDiscount().toString());
            } else {
                showProductInCardsDto.setDiscount("");
            }
            if (productAttributes.getActualCost() != null) {
                showProductInCardsDto.setActualPrice(String.valueOf(productAttributes.getActualCost().intValue()));
            } else {
                showProductInCardsDto.setActualPrice("");
            }

            ProductDocuments productDocuments = productDao.getProductDocsFromProductId(product.getId());
            String[] splitImgs = productDocuments.getImages().split(", ");
            List<String> imgs = new ArrayList<>(Arrays.asList(splitImgs));
            Collections.sort(imgs);
            showProductInCardsDto.setImgName(imgs.get(0));

            Double ratings = ratingsDao.getRatingsFromProductId(product.getId());

            if (ratings != null) {
                int rating = (int) Math.ceil(ratings);
                showProductInCardsDto.setRating(String.valueOf(rating));
            } else {
                showProductInCardsDto.setRating("");
            }

            showProductInCardsDto.setBrand(product.getBrand());
            showProductInCardsDto.setSellerName(product.getSellerId().getBusinessName());
            showProductInCardsDto.setWishListId(0);

            if (!listOfProductIds.isEmpty()) {

                for (Object[] obj : listOfProductIds) {

                    Integer productId = (Integer) obj[0];
                    Integer wishListId = (Integer) obj[1];

                    if (product.getId() == productId) {
                        showProductInCardsDto.setWishListId(wishListId);
                    }

                }

            }

            list.add(showProductInCardsDto);

        }

        return list;

    }

    public List<GetSellerNameDto> getSellerNameForFilters() {

        List<Seller> list = sellerDao.getAllSellerName();

        List<GetSellerNameDto> getSellerNameDtoList = new ArrayList<>();

        for (Seller seller : list) {
            GetSellerNameDto getSellerNameDto = new GetSellerNameDto();
            getSellerNameDto.setId(seller.getId());
            getSellerNameDto.setName(seller.getBusinessName());

            getSellerNameDtoList.add(getSellerNameDto);
        }

        return getSellerNameDtoList;

    }

    public List<String> getBrandsNameForFilter() {

        List<String> brandsName = productDao.getBrandsName();
        return brandsName;

    }

    public ShowMaxAndMinPrice getMaxAndMinPrice() {

        Tuple result = productDao.getMaxAndMinCost();
        Double maxCost = (Double) result.get(0);
        Double minCost = (Double) result.get(1);

        if (maxCost % 100 != 0) {
            maxCost += (maxCost % 100);
        }

        ShowMaxAndMinPrice showMaxAndMinPrice = new ShowMaxAndMinPrice();
        showMaxAndMinPrice.setMin(minCost.intValue());
        showMaxAndMinPrice.setMax(maxCost.intValue());

        return showMaxAndMinPrice;

    }

    @Transactional
    public String submitForgetPassword(ForgetPswdDto forgetPswdDto, HttpServletRequest request) {

        List<User> userList = userDao.getUserFromUserMail(forgetPswdDto.getEmail());

        if (userList.isEmpty()) {
            return "This mail is not registered!";
        }

        List<VerificationToken> list = verificationTokenDao.checkvalidation(forgetPswdDto.getEmail());

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setEmail(forgetPswdDto.getEmail());
        verificationToken.setCreatedDate(LocalDateTime.now());

        String token = new TokenGenerator().tokengenerator();
        verificationToken.setToken(token);

        if (list.size() > 0) {

            if (list.size() >= 3) {
                return "You have exceeded maximun number of attempts. Please try again after 24 hours.";
            }

            VerificationToken lasVerificationToken = list.get(list.size() - 1);
            lasVerificationToken.setValidation(false);
            verificationToken.setValidation(true);
            verificationTokenDao.updateVerificationToken(lasVerificationToken);
            verificationTokenDao.saveVerificationToken(verificationToken);

        } else {
            verificationToken.setValidation(true);
            verificationTokenDao.saveVerificationToken(verificationToken);
        }

        String baseURL = new GetContextPath().getProjectBaseURL(request);

        String sendTo = forgetPswdDto.getEmail();
        String subject = "Verify Your Email For Reset Password";
        String body = "\n" +
                "Dear User,\n" +
                "\n" +
                "Trouble signing in?\n" +
                "\n" +
                "Resetting your password is easy.\n" +
                "\n" +
                "Just click on the link below and follow the instructions. We’ll have you up and running in no time.\n" +
                "\n" +
                baseURL + "/resetpswd/" + token + "\n" +
                "\n" +
                "Thank you,\n" +
                "The Clucth&Carry Team";

        System.out.println(baseURL + "/resetpswd/" + token + "   =============================================");

//        mailService.send(sendTo, subject, body);

        EmailLogs emailLogs = new EmailLogs();
        emailLogs.setEmail(forgetPswdDto.getEmail());
        emailLogs.setRecipient(userList.get(0).getFirstName() + " " + userList.get(0).getLastName());
        emailLogs.setSentDateTime(LocalDateTime.now());
        emailLogs.setAction(subject);

        emailLogDao.saveEmailLog(emailLogs);

        return "true";

    }

    public List<VerificationToken> checkTokenOfResetLink(String token) {

        List<VerificationToken> list = verificationTokenDao.checkToken(token);

        return list;

    }

    @Transactional
    public String submitResetPassword(ResetPasswordDto resetPasswordDto) {

        VerificationToken verificationToken = verificationTokenDao.checkTokenFromId(resetPasswordDto.getTokenId());

        if (verificationToken == null) {
            return "invalid token!";
        } else if (!resetPasswordDto.getPswd().equals(resetPasswordDto.getConfpswd())) {
            return "Password don't match!";
        } else {
            List<UserMain> list = userDao.getUserMain(verificationToken.getEmail());
            UserMain userMain = list.get(0);

            String salt = PasswordHash.generateSalt();
            String hashedPassword = PasswordHash.hashPassword(resetPasswordDto.getPswd(), salt);

            userMain.setPasswordHash(hashedPassword);
            userMain.setSalt(salt);
            userMain.setModifiedDate(LocalDateTime.now());

            userDao.updateUserMain(userMain);
        }

        return "true";

    }

    public ProductDetailsDto showProductDetails(int productId) {

        Product product = productDao.getProductFromId(productId);
        ProductAttributes productAttrs = productDao.getProductAttrs(productId);
        ProductDocuments productDocuments = productDao.getProductDocsFromProductId(productId);
        Double avgRatings = ratingsDao.getRatingsFromProductId(productId);
        List<Ratings> ratingsList = ratingsDao.getFullRatingsFromProductId(productId);

        ProductDetailsDto productDetailsDto = new ProductDetailsDto();

        productDetailsDto.setProductId(productId);

        String[] mainImg = productDocuments.getImages().split(", ");
        List<String> mainImgs = new ArrayList<>(Arrays.asList(mainImg));
        Collections.sort(mainImgs);
        productDetailsDto.setMainImg(mainImgs);

        productDetailsDto.setName(product.getName());

        if (avgRatings != null) {

            productDetailsDto.setRatings(String.valueOf(avgRatings.intValue()));

            List<String> reviewerName = new ArrayList<>();
            List<String> reviewStars = new ArrayList<>();
            List<String> reviewHeadings = new ArrayList<>();
            List<String> reviewsText = new ArrayList<>();

            for (Ratings ratings : ratingsList) {

                reviewerName.add(ratings.getUserId().getFirstName() + " " + ratings.getUserId().getLastName());
                reviewStars.add(String.valueOf(ratings.getRating()));
                reviewHeadings.add(ratings.getHeading());
                reviewsText.add(ratings.getReviewText());

            }

            productDetailsDto.setReviewerName(reviewerName);
            productDetailsDto.setReviewStars(reviewStars);
            productDetailsDto.setReviewHeadings(reviewHeadings);
            productDetailsDto.setReviewsText(reviewsText);

        } else {
            productDetailsDto.setRatings("");
        }

        productDetailsDto.setCost(String.valueOf(product.getCost().intValue()));

        if (productAttrs.getActualCost() != null && productAttrs.getDiscount() != null) {

            productDetailsDto.setActualCost(String.valueOf(productAttrs.getActualCost().intValue()));
            productDetailsDto.setDiscount(String.valueOf(productAttrs.getDiscount()));

        }

        String[] colors = productAttrs.getColor().split(", ");

        List<String> colorNames = new ArrayList<>(Arrays.asList(colors));
        Collections.sort(colorNames);

        String[] colorImgs = productDocuments.getImages().split(", ");

        List<String> colorImg = new ArrayList<>();

        for (int j = 1; j < colorImgs.length; j = j + 2) {

            for (int i = 0; i < colors.length; i++) {

                if (colorImgs[j].toLowerCase().startsWith(colors[i].toLowerCase())) {
                    colorImg.add(colorImgs[j]);
                }
            }

        }

        Collections.sort(colorImg);
        productDetailsDto.setColorImgs(colorImg);
        productDetailsDto.setColors(colorNames);

        productDetailsDto.setDesc(product.getProdDescription());
        productDetailsDto.setWeight(String.valueOf(product.getWeight().intValue()));
        productDetailsDto.setCreatedDate(String.valueOf(product.getCreatedDate().toLocalDate()));
        productDetailsDto.setSeller(product.getSellerId().getBusinessName());
        productDetailsDto.setBrand(product.getBrand());
        productDetailsDto.setCategory(product.getCategoryId().getName());
        productDetailsDto.setSubCategory(product.getSubCategoryId().getName());

        return productDetailsDto;

    }

    public GetRoleAndUserIdDto getRoleId(String email) {
        return userDao.getRoleIdFromEmail(email);
    }

    @Transactional
    public int addToCart(int userId, int pId, String color, int qty) {

        Cart cart1 = cartDao.ifProductExistsInCart(userId, pId);

        if (cart1 != null) {

            if (!cart1.isRemoved() && cart1.getColor().equals(color)) {
                return 1;
            } else {
                cart1.setRemoved(false);
                cart1.setColor(color);
                cart1.setQuantity(qty);
                cartDao.updateCart(cart1);

                return 0;
            }

        } else {
            Cart cart = new Cart();
            cart.setUserId(userDao.getUser(userId));
            cart.setProductId(productDao.getProductFromId(pId));
            cart.setColor(color);
            cart.setQuantity(qty);
            cart.setRemoved(false);

            cartDao.saveCart(cart);

            return 0;
        }

    }

    public int noOfItemsInCart(int userId) {

        int total = cartDao.totalProductsInCart(userId);
        return total;

    }

    public List<ShowProductsInCartDto> showProductsInCart(int userId, String searchText, String yourFilters, HttpServletRequest request) {

        List<Cart> cartsItems = cartDao.getCart(userId, searchText, yourFilters);

        List<ShowProductsInCartDto> list = new ArrayList<>();

        for (Cart cart : cartsItems) {

            Product product = cart.getProductId();

            Integer stock = stockDao.getStockCountOfProductFromColor(product.getId(), cart.getColor());

            ShowProductsInCartDto showProductsInCartDto = new ShowProductsInCartDto();
            showProductsInCartDto.setId(product.getId());
            showProductsInCartDto.setName(product.getName());
            showProductsInCartDto.setCost(String.valueOf(product.getCost()));
            showProductsInCartDto.setColor(cart.getColor());
            showProductsInCartDto.setQty(String.valueOf(cart.getQuantity()));

            if (stock < 10) {
                int ifOutOfStock = checkStock(product.getId(), cart.getColor(), request);
                showProductsInCartDto.setOutOfStock(ifOutOfStock);
            }

            if (product.getProductAttributes().getDiscount() != null) {

                showProductsInCartDto.setDiscount(String.valueOf(product.getProductAttributes().getDiscount()));
                showProductsInCartDto.setActualCost(String.valueOf(product.getProductAttributes().getActualCost()));

            }

            Double ratings = ratingsDao.getRatingsFromProductId(product.getId());

            if (ratings != null) {
                showProductsInCartDto.setRatings(String.valueOf(ratings.intValue()));
            } else {
                showProductsInCartDto.setRatings("0");
            }

            String image = productDao.getProductImgNameProductId(product.getId());
            showProductsInCartDto.setImage(image);

            list.add(showProductsInCartDto);

        }

        return list;

    }

    public void deleteItemFromCart(int userId, int productId) {

        cartDao.deleteProductFromCart(productId, userId);

    }

    @Transactional
    public String addNewWishList(int userId, AddNewWishListDto addNewWishListDto) {

        int ifExists = wishListDao.ifNameExistsInWishList(userId, addNewWishListDto.getWishlistName());

        if (ifExists == 1) {

            return "This Name already Exists!";

        } else {
            WishList wishList1 = new WishList();
            wishList1.setUserId(userDao.getUser(userId));
            wishList1.setName(addNewWishListDto.getWishlistName());
            wishList1.setDescription(addNewWishListDto.getWishlistDescription());

            wishListDao.saveInWishList(wishList1);

            return "added";
        }

    }

    @Transactional
    public List<GetAllWishlistIdAndName> getAllWishList(int userId) {

        List<GetAllWishlistIdAndName> allWishlists = wishListDao.getAllWishlists(userId);
        return allWishlists;

    }

    @Transactional
    public List<GetAllWishLists> getAllWishListDataForWishListPage(int userId) {

        List<GetAllWishLists> allWishlists = wishListDao.getAllWishlistsDataForWishListPage(userId);
        return allWishlists;

    }

    @Transactional
    public List<GetAllWishListsOfFriends> getAllWishListDataForFriendsForWishListPage(int userId) {

        List<SharedWishList> allWishlists = wishListDao.getSharedwwshListFromUserId(userId);
        List<GetAllWishListsOfFriends> list = new ArrayList<>();

        for (SharedWishList sharedWishList : allWishlists) {

            GetAllWishListsOfFriends getAllWishLists = new GetAllWishListsOfFriends();
            getAllWishLists.setId(sharedWishList.getWishListId().getId());
            getAllWishLists.setOwnerName(sharedWishList.getOwnerUserId().getFirstName() + " " + sharedWishList.getOwnerUserId().getLastName());
            getAllWishLists.setName(sharedWishList.getWishListId().getName());
            getAllWishLists.setDesc(sharedWishList.getWishListId().getDescription());

            list.add(getAllWishLists);

        }

        return list;

    }

    @Transactional
    public void addToWishList(int pId, int wishlistId, int add) {

        WishListItems wishListItems = wishListDao.ifProductExistsInWishListItems(wishlistId, pId);

        if (wishListItems != null) {

            if (add == 1) {
                wishListItems.setRemoved(false);
                wishListDao.updateInWishListItems(wishListItems);
            } else {
                wishListItems.setRemoved(true);
                wishListDao.updateInWishListItems(wishListItems);
            }

        } else {
            WishListItems wishListItems1 = new WishListItems();
            wishListItems1.setProductId(productDao.getProductFromId(pId));
            wishListItems1.setWishListId(wishListDao.getwishListFromId(wishlistId));
            wishListItems1.setRemoved(false);

            wishListDao.saveInWishListItems(wishListItems1);
        }

    }

    public List<ShowProductsInCartDto> showProductsInWishlist(int wishListId, String searchText, String yourFilters) {

        List<Product> products = wishListDao.getProductIdFromWlId(wishListId, searchText, yourFilters);

        List<ShowProductsInCartDto> list = new ArrayList<>();

        for (Product product : products) {

            ShowProductsInCartDto showProductsInCartDto = new ShowProductsInCartDto();
            showProductsInCartDto.setId(product.getId());
            showProductsInCartDto.setName(product.getName());
            showProductsInCartDto.setCost(String.valueOf(product.getCost()));

            Double ratings = ratingsDao.getRatingsFromProductId(product.getId());

            if (ratings != null) {
                showProductsInCartDto.setRatings(String.valueOf(ratings.intValue()));
            } else {
                showProductsInCartDto.setRatings("0");
            }

            String image = productDao.getProductImgNameProductId(product.getId());
            showProductsInCartDto.setImage(image);

            list.add(showProductsInCartDto);

        }

        return list;

    }

    public void deleteItemFromWishlist(int productId, int wishlistId) {

        wishListDao.deleteProductFromWishlist(productId, wishlistId);

    }

    @Transactional
    public void shareWishList(ShareWishListDto shareWishListDto, int userId, HttpServletRequest request) {

        WishListTokens wishListTokens = wishListDao.checkIfMailAndWishListExists(shareWishListDto.getEmail(), shareWishListDto.getWishId());
        User user = userDao.getUser(userId);

        String token;

        if (wishListTokens != null) {

            token = new TokenGenerator().tokengenerator();
            wishListTokens.setToken(token);
            wishListDao.updateInWishListTokens(wishListTokens);

        } else {

            WishList wishList = wishListDao.getwishListFromId(shareWishListDto.getWishId());

            WishListTokens wishListTokens1 = new WishListTokens();
            wishListTokens1.setRecipientEmail(shareWishListDto.getEmail());
            wishListTokens1.setWishListId(wishList);

            token = new TokenGenerator().tokengenerator();
            wishListTokens1.setToken(token);

            wishListDao.saveInWishListTokens(wishListTokens1);

        }

        String baseURL = new GetContextPath().getProjectBaseURL(request);

        String sendTo = shareWishListDto.getEmail();
        String subject = "Your Friend " + user.getFirstName() + " " + user.getLastName() + " Just Shared Their Wishlist with You!";

        String body = "\n" +
                "Hello User,\n" +
                "\n" +
                "Your friend " + user.getFirstName() + " " + user.getLastName() + ", has just shared their wishlist with you! 🎉\n" +
                "\n" +
                "Here’s what they have in mind:\n" +
                "\n" +
                shareWishListDto.getMessage() + "\n" +
                "\n" +
                "Click the link below to view the full wishlist and explore the items they are excited about:\n" +
                "\n" +
                baseURL + "/c&c/sharedwishlist/" + user.getId() + "/" + shareWishListDto.getWishId() + "/" + token + "\n" +
                "\n" +
                "We hope you enjoy browsing through the wishlist and maybe find something you love too!\n" +
                "\n" +
                "Happy Shopping! 🛍️\n" +
                "\n" +
                "Best regards,\n" +
                "The Clucth&Carry Team";

//        mailService.send(sendTo, subject, body);

        System.out.println(baseURL + "/c&c/sharedwishlist/" + user.getId() + "/" + shareWishListDto.getWishId() + "/" + token);

        EmailLogs emailLogs = new EmailLogs();
        emailLogs.setEmail(shareWishListDto.getEmail());

        List<User> userFromUserMail = userDao.getUserFromUserMail(shareWishListDto.getEmail());

        if (!userFromUserMail.isEmpty()) {
            User newUser = userFromUserMail.get(0);
            emailLogs.setRecipient(newUser.getFirstName() + " " + newUser.getLastName());
        } else {
            emailLogs.setRecipient("-");
        }

        emailLogs.setSentDateTime(LocalDateTime.now());
        emailLogs.setAction("Shared wishlist with friend");

        emailLogDao.saveEmailLog(emailLogs);

    }

    @Transactional
    public boolean checkTokenForSharedList(String token, int wishlistId) {

        return wishListDao.checkToken(token, wishlistId);

    }

    public boolean validateUserForAccessingWishList(int userId, int wishListId) {

        User user = userDao.getUser(userId);
        return wishListDao.validateUser(user.getEmail(), wishListId);

    }

    @Transactional
    public void accessSharedWishList(int oldUserId, int currentuserId, int wishlistId) {

        boolean ifALreayExists = wishListDao.addInSharedList(oldUserId, currentuserId, wishlistId);

        if (!ifALreayExists) {
            SharedWishList sharedWishList = new SharedWishList();
            sharedWishList.setWishListId(wishListDao.getwishListFromId(wishlistId));
            sharedWishList.setUserId(userDao.getUser(currentuserId));
            sharedWishList.setOwnerUserId(userDao.getUser(oldUserId));

            wishListDao.saveInSharedWishList(sharedWishList);
        }

    }

    @Transactional
    public EditPersonalInfoDto getDataOfUser(int userId) {

        User user = userDao.getUser(userId);

        EditPersonalInfoDto editPersonalInfoDto = new EditPersonalInfoDto();
        editPersonalInfoDto.setFirstName(user.getFirstName());
        editPersonalInfoDto.setLastName(user.getLastName());
        editPersonalInfoDto.setPhone(user.getPhone());
        editPersonalInfoDto.setEmail(user.getEmail());
        editPersonalInfoDto.setRole(user.getRoleId().getRoleName());

        String genD = user.getGender();
        int gender = 0;

        switch (genD) {

            case "male":
                gender = 0;
                break;

            case "female":
                gender = 1;
                break;

            case "other":
                gender = 2;
                break;

        }

        editPersonalInfoDto.setGender(gender);
        return editPersonalInfoDto;

    }

    @Transactional
    public String editPersonalInfo(EditPersonalInfoDto editPersonalInfoDto, int userId, HttpServletRequest request) {

        Set<Integer> set = new HashSet<>(Arrays.asList(0, 1, 2));

        int genD = editPersonalInfoDto.getGender();
        String gender = "";

        if (set.contains(genD)) {

            switch (genD) {

                case 0:
                    gender = "male";
                    break;

                case 1:
                    gender = "female";
                    break;

                case 2:
                    gender = "other";
                    break;

            }

            User user = userDao.getUser(userId);
            user.setFirstName(editPersonalInfoDto.getFirstName());
            user.setLastName(editPersonalInfoDto.getLastName());
            user.setPhone(editPersonalInfoDto.getPhone());
            user.setGender(gender);
            user.getUserMainId().setModifiedDate(LocalDateTime.now());
            user.getUserMainId().setModifiedBy(userDao.getUser(userId));
            user.getUserMainId().setModifiedDate(LocalDateTime.now());
            userDao.updateUser(user);

            String baseURL = new GetContextPath().getProjectBaseURL(request);

            String sendTo = user.getEmail();
            String subject = "Your Profile Information Has Been Updated";
            String body = "\n" +
                    "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n" +
                    "\n" +
                    "We wanted to let you know that your personal information has been successfully updated.\n" +
                    "\n" +
                    "If you did not make this change, please contact our support team immediately.\n" +
                    "\n" +
                    "Here are the details of your updated profile:\n" +
                    "First Name: " + editPersonalInfoDto.getFirstName() + "\n" +
                    "Last Name: " + editPersonalInfoDto.getLastName() + "\n" +
                    "Phone: " + editPersonalInfoDto.getPhone() + "\n" +
                    "Gender: " + gender + "\n" +
                    "\n" +
                    "You can review and update your profile anytime by logging into your account.\n" +
                    "\n" +
                    baseURL + "/login" + "\n" +
                    "\n" +
                    "If you have any questions or need further assistance, please feel free to reach out to our support team.\n" +
                    "\n" +
                    "Thank you for being a valued member of our community.\n" +
                    "\n" +
                    "Best regards,\n" +
                    "The Clutch&Carry Team";

//            mailService.send(sendTo, subject, body);

            EmailLogs emailLogs = new EmailLogs();
            emailLogs.setEmail(user.getEmail());
            emailLogs.setRecipient(user.getFirstName() + " " + user.getLastName());
            emailLogs.setSentDateTime(LocalDateTime.now());
            emailLogs.setAction("profile update of customer");

            emailLogDao.saveEmailLog(emailLogs);

            return "success";

        } else {

            return "Enter a valid gender";

        }

    }

    public boolean getAddressType(int userId) {

        return customerAddressDao.ifBillingExists(userId);

    }

    public List<AddAddressDto> getAllAddresses(int userId) {

        List<AddAddressDto> allAddresses = customerAddressDao.getAllAddresses(userId);
        return allAddresses;

    }

    @Transactional
    public String addAddress(AddAddressDto addAddressDto, int userId) {

        CustomerAddress customerAddress = new CustomerAddress();
        customerAddress.setUserId(userDao.getUser(userId));
        customerAddress.setFirstName(addAddressDto.getFirstName());
        customerAddress.setLastName(addAddressDto.getLastName());
        customerAddress.setPhone(addAddressDto.getPhone());
        customerAddress.setAddress(addAddressDto.getAddress());
        customerAddress.setCity(addAddressDto.getCity());
        customerAddress.setState(addAddressDto.getState());
        customerAddress.setZipCode((addAddressDto.getZipCode()));
        customerAddress.setDeleted(false);

        String type = addAddressDto.getType();
        Set<String> set = new HashSet<>(Arrays.asList("shipping", "billing"));

        boolean ifExists = customerAddressDao.ifBillingExists(userId);

        if (set.contains(type)) {

            if (type.equals("billing")) {

                if (ifExists) {
                    return "please select a valid type of address";
                } else {
                    customerAddress.setType(type);
//                    customerAddressDao.saveCustomerAddress(customerAddress);

                    return "success";
                }

            } else {
                customerAddress.setType(type);
//                customerAddressDao.saveCustomerAddress(customerAddress);

                return "success";
            }

        } else {
            return "please select a valid type of address";
        }

    }

    @Transactional
    public String editAddress(AddAddressDto addAddressDto, int userId) {

        CustomerAddress customerAddress = customerAddressDao.getCustomerAddressFromId(addAddressDto.getId());

        customerAddress.setFirstName(addAddressDto.getFirstName());
        customerAddress.setLastName(addAddressDto.getLastName());
        customerAddress.setPhone(addAddressDto.getPhone());
        customerAddress.setAddress(addAddressDto.getAddress());
        customerAddress.setCity(addAddressDto.getCity());
        customerAddress.setState(addAddressDto.getState());
        customerAddress.setZipCode((addAddressDto.getZipCode()));

        String type = addAddressDto.getType();
        Set<String> set = new HashSet<>(Arrays.asList("shipping", "billing"));

        if (set.contains(type)) {

            customerAddress.setType(type);
            customerAddressDao.updateCustomerAddress(customerAddress);

            return "success";
        } else {
            return "please select a valid type of address";
        }


    }

    public void deleteAddress(int id) {

        customerAddressDao.deleteAddress(id);

    }

    @Transactional
    public List<ShowReviewDto> showReview(int userId) {

        List<Ratings> fullRatingsFromUserId = ratingsDao.getFullRatingsFromUserId(userId);

        List<ShowReviewDto> list = new ArrayList<>();

        for (Ratings ratings : fullRatingsFromUserId) {

            ShowReviewDto showReviewDto = new ShowReviewDto();
            showReviewDto.setId(ratings.getId());
            showReviewDto.setProductId(ratings.getProductId().getId());
            showReviewDto.setProductName(ratings.getProductId().getName());
            showReviewDto.setHeading(ratings.getHeading());
            showReviewDto.setRating(String.valueOf(ratings.getRating()));
            showReviewDto.setReviewText(ratings.getReviewText());

            String dateInFormate = new DateHelper().getDateInFormate(ratings.getCreatedDate().toLocalDate());
            showReviewDto.setReviewedDate(dateInFormate);

            list.add(showReviewDto);

        }

        return list;

    }

    @Transactional
    public void editReview(ShowReviewDto showReviewDto) {

        ratingsDao.editReview(showReviewDto.getId(), showReviewDto.getHeading(), showReviewDto.getReviewText(), Integer.parseInt(showReviewDto.getRating()));

    }

    public void deleteReview(int id) {

        ratingsDao.deleteReview(id);

    }

    public String checkForUserReview(int productId, int userId) {

        int ifEligible = ordersDao.ifUserEligible(productId, userId);

        Long ratingsCount = ratingsDao.getRatingsCount(userId);

        if (ifEligible > 0) {

            int ifReviewd = ratingsDao.ifUserHasReviewdOnce(userId, productId);
            if (ifReviewd > 0) {
                return "Already Given";
            } else {
                return "success";
            }

        } else {
            return "Not Purchased";
        }

    }

    @Transactional
    public void addReview(AddReviewDto addReviewDto, int userId) {

        User user = userDao.getUser(userId);

        Ratings ratings = new Ratings();
        ratings.setUserId(user);
        ratings.setHeading(addReviewDto.getHeading());
        ratings.setReviewText(addReviewDto.getReviewText());
        ratings.setRating(addReviewDto.getRating());
        ratings.setDeleted(false);
        ratings.setProductId(productDao.getProductFromId(addReviewDto.getPId()));

        ratingsDao.saveRatings(ratings);

        Long ratingsCount = ratingsDao.getRatingsCount(userId);

        if (ratingsCount % 50 == 0) {

            double cashbackAmount = ((double) ratingsCount / 50) * 10;

            Double oldBalance = walletDao.getWalletDetails(userId);

            Wallet wallet = new Wallet();
            wallet.setUserId(user);
            wallet.setTransactionType(Wallet.TransactionType.CREDIT);
            WalletTransactionReason walletTransactionReason = walletTransactionReasonDao.getWalletTrReasonId(2);
            wallet.setWalletTransactionReasonId(walletTransactionReason);
            wallet.setBalance(oldBalance + cashbackAmount);
            wallet.setTransactionAmount(cashbackAmount);
            walletDao.saveWallet(wallet);

        }

    }

    @Transactional
    public List<ShowWalletDto> showWallet(int userId) {

        List<Wallet> list = walletDao.getWallet(userId);

        List<ShowWalletDto> listOfWalletDto = new ArrayList<>();

        double totalExpense = 0;
        double todaysExpense = 0;
        double cuurentBalance = 0;

        DecimalFormat df = new DecimalFormat("0.00");

        for (int i = list.size() - 1; i >= 0; i--) {

            Wallet wallet = list.get(i);

            if (wallet.getTransactionType().toString().equals("DEBIT")) {

                totalExpense += wallet.getTransactionAmount();

                if (Objects.equals(wallet.getTransactionaDate().toLocalDate(), LocalDate.now())) {

                    todaysExpense += wallet.getTransactionAmount();

                }

            }

            if (i == list.size() - 1) {
                cuurentBalance += wallet.getBalance();
            }

        }

        ShowWalletDto showWalletDto = new ShowWalletDto();
        showWalletDto.setTotalExpense(df.format(totalExpense));
        showWalletDto.setTodayExpense(df.format(todaysExpense));
        showWalletDto.setCurrentBalance(df.format(cuurentBalance));

        listOfWalletDto.add(showWalletDto);

        return listOfWalletDto;

    }

    @Transactional
    public List<ShowPaymentHistoryDto> showPaymentHistory(int userId, int currentPage, int pageSize) {

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Wallet> listOfWallet = walletDao.getWalletPaymentHistory(userId, pageable);
        List<Wallet> list = listOfWallet.getContent();
        int totalPages = listOfWallet.getTotalPages();

        List<ShowPaymentHistoryDto> listOfWalletDto = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("0.00");

        for (int i = 0; i < list.size(); i++) {

            Wallet wallet = list.get(i);

            ShowPaymentHistoryDto showPaymentHistoryDto = new ShowPaymentHistoryDto();
            showPaymentHistoryDto.setDate(new DateHelper().getDateInFormate(wallet.getTransactionaDate().toLocalDate()));
            showPaymentHistoryDto.setDesc(wallet.getWalletTransactionReasonId().getReason());
            showPaymentHistoryDto.setAmount(df.format(wallet.getTransactionAmount()));
            showPaymentHistoryDto.setType(String.valueOf(wallet.getTransactionType()).toLowerCase());
            showPaymentHistoryDto.setCount(totalPages);

            listOfWalletDto.add(showPaymentHistoryDto);

        }

        return listOfWalletDto;

    }


    @Transactional
    public String addIntoWallet(Double amount, int userId) {

        if (amount.isNaN()) {
            return "Please Enter a Number";
        } else if (amount > 50000 || amount < 10) {
            return "Amount must be between 10 and 50,000 INR";
        } else {
            Double oldBalance = walletDao.getWalletDetails(userId);

            Wallet wallet = new Wallet();
            wallet.setUserId(userDao.getUser(userId));
            wallet.setTransactionType(Wallet.TransactionType.CREDIT);
            WalletTransactionReason walletTransactionReason = walletTransactionReasonDao.getWalletTrReasonId(5);
            wallet.setWalletTransactionReasonId(walletTransactionReason);
            wallet.setBalance(oldBalance + amount);
            wallet.setTransactionAmount(amount);
            walletDao.saveWallet(wallet);

            return "success";
        }

    }

    public String addToTempOrders(ProceedToBuyDto proceedToBuyDto, int userId, HttpServletRequest request) {

        if (proceedToBuyDto.getProducts().isEmpty() || proceedToBuyDto.getTotalPrice() == 0 || proceedToBuyDto.getTotalItems() == 0) {
            return "empty";
        } else {

            TempOrders tempOrders1 = tempOrdersDao.ifOrderWithOutPaymentExists(userId);

            StringBuilder pIds = new StringBuilder();
            StringBuilder qty = new StringBuilder();
            StringBuilder colors = new StringBuilder();

            Double totalCost = 0.0;
            Double totalDiscount = 0.0;

            int j = 0;

            for (int i = 0; i < proceedToBuyDto.getProducts().size() - 1; i = i + 2) {

                pIds.append(proceedToBuyDto.getProducts().get(i)).append(", ");
                qty.append(proceedToBuyDto.getProducts().get(i + 1)).append(", ");
                colors.append(proceedToBuyDto.getColors().get(j)).append(", ");

                if (proceedToBuyDto.getProducts().get(i + 1) < 1 || proceedToBuyDto.getProducts().get(i + 1) > 100) {
                    return "qty";
                }

                int ifOutOfStock = checkStock(proceedToBuyDto.getProducts().get(i), proceedToBuyDto.getColors().get(j), request);
                if (ifOutOfStock == 1) {
                    return "stock";
                }
                j++;

                ProductAttributes productAttrs = productDao.getProductAttrs(proceedToBuyDto.getProducts().get(i));

                Double productActualCost = productAttrs.getActualCost();
                Integer discount = productAttrs.getDiscount();
                if (productActualCost != null && discount != null) {
                    totalDiscount += productActualCost * (discount * (0.01)) * proceedToBuyDto.getProducts().get(i + 1);
                }

                Double productCost = productDao.getProductCostProductId(proceedToBuyDto.getProducts().get(i));
                totalCost += productCost * proceedToBuyDto.getProducts().get(i + 1);

            }

            if (totalCost != proceedToBuyDto.getTotalPrice() || totalDiscount != proceedToBuyDto.getTotalDiscount()) {
                return "cost";
            }

            if (tempOrders1 == null) {

                TempOrders tempOrders = new TempOrders();
                tempOrders.setUserId(userDao.getUser(userId));
                tempOrders.setTotalAmount(proceedToBuyDto.getTotalPrice());
                tempOrders.setDiscount(proceedToBuyDto.getTotalDiscount());
                tempOrders.setTotalItems(proceedToBuyDto.getTotalItems());
                tempOrders.setPaymentDone(false);

                tempOrdersDao.saveTempOrders(tempOrders);

                TempOrderDetails tempOrderDetails = new TempOrderDetails();
                tempOrderDetails.setOrderId(tempOrders);
                tempOrderDetails.setProductId(String.valueOf(pIds));
                tempOrderDetails.setQuantity(String.valueOf(qty));
                tempOrderDetails.setColors(String.valueOf(colors));

                tempOrdersDao.saveTempOrderDetails(tempOrderDetails);

            } else {

                tempOrders1.setUserId(userDao.getUser(userId));
                tempOrders1.setTotalAmount(proceedToBuyDto.getTotalPrice());
                tempOrders1.setDiscount(proceedToBuyDto.getTotalDiscount());
                tempOrders1.setTotalItems(proceedToBuyDto.getTotalItems());
                tempOrders1.setPaymentDone(false);

                tempOrdersDao.updateTempOrders(tempOrders1);

                TempOrderDetails tempOrderDetails = tempOrdersDao.tempOrderDetails(tempOrders1.getId(), userId);
                tempOrderDetails.setOrderId(tempOrders1);
                tempOrderDetails.setProductId(String.valueOf(pIds));
                tempOrderDetails.setQuantity(String.valueOf(qty));
                tempOrderDetails.setColors(String.valueOf(colors));

                tempOrdersDao.updateTempOrderDetails(tempOrderDetails);

            }

            return "success";

        }

    }

    public List<CheckOutDto> getDataForCheckOut(int userId) {

        List<CustomerAddress> listOfCustomerAddresses = customerAddressDao.checkIfAddrExists(userId);

        List<CheckOutDto> list = new ArrayList<>();

        TempOrders tempOrders = tempOrdersDao.ifOrderWithOutPaymentExists(userId);

        if (tempOrders != null) {
            CheckOutDto checkOutDto = new CheckOutDto();
            checkOutDto.setTotalItems(String.valueOf(tempOrders.getTotalItems()));
            checkOutDto.setTotalPrice(String.valueOf(tempOrders.getTotalAmount()));
            checkOutDto.setTotalDiscount(String.valueOf(tempOrders.getDiscount()));

            List<String> listOfItems = new ArrayList<String>();
            TempOrderDetails tempOrderDetails = tempOrdersDao.tempOrderDetails(tempOrders.getId(), userId);

            String productId = tempOrderDetails.getProductId();

            String[] productIds = productId.split(", ");

            for (String id : productIds) {

                Tuple productNameAndCostProductId = productDao.getProductNameAndCostProductId(Integer.parseInt(id));
                listOfItems.add(productNameAndCostProductId.get(0).toString());
                listOfItems.add(productNameAndCostProductId.get(1).toString());

            }

            checkOutDto.setItems(listOfItems);
            list.add(checkOutDto);
        }

        if (!list.isEmpty()) {
            for (CustomerAddress customerAddress : listOfCustomerAddresses) {

                CheckOutDto checkOutDto = new CheckOutDto();
                checkOutDto.setFirstName(customerAddress.getFirstName());
                checkOutDto.setLastName(customerAddress.getLastName());
                checkOutDto.setPhone(customerAddress.getPhone());
                checkOutDto.setAddress(customerAddress.getAddress());
                checkOutDto.setCity(customerAddress.getCity());
                checkOutDto.setState(customerAddress.getState());
                checkOutDto.setZipCode(customerAddress.getZipCode());
                checkOutDto.setAddressId(customerAddress.getId());

                list.add(checkOutDto);

            }
        }

        return list;

    }

    public List<GetCouponsDto> getCouponsFromMinPrice(Double total) {

        List<Tuple> couponList = couponDao.getCouponFromMinPrice(total);
        List<GetCouponsDto> list = new ArrayList<>();

        for (Tuple tuple : couponList) {

            String code = (String) tuple.get(0);

            String type = (String) tuple.get(1);

            Double discount = (Double) tuple.get(2);

            int id = (int) tuple.get(3);

            GetCouponsDto getCouponsDto = new GetCouponsDto();
            getCouponsDto.setCode(code);
            getCouponsDto.setType(type);
            getCouponsDto.setId(id);

            if (!type.equals("Flat")) {
                getCouponsDto.setDiscount(String.valueOf(discount.intValue()));
            } else {
                getCouponsDto.setDiscount(discount.toString());
            }

            list.add(getCouponsDto);
        }

        return list;


    }

    public boolean checkWalletAmounts(Double total, int userId) {

        Double walletOfUser = walletDao.getWalletDetails(userId);

        if (walletOfUser <= 0 || walletOfUser < total) {

            return false;

        } else {

            return true;

        }

    }

    public List<ShowProductsInCartDto> getProductsForReviewOrder(int userId) {

        TempOrders tempOrders = tempOrdersDao.ifOrderWithOutPaymentExists(userId);
        TempOrderDetails tempOrderDetails = tempOrdersDao.tempOrderDetails(tempOrders.getId(), userId);

        List<ShowProductsInCartDto> list = new ArrayList<>();

        String[] pIds = tempOrderDetails.getProductId().split(", ");
        String[] qty = tempOrderDetails.getQuantity().split(", ");

        for (int i = 0; i < pIds.length; i++) {

            String pId = pIds[i];

            Product product = productDao.getProductFromId(Integer.parseInt(pId));
            ShowProductsInCartDto showProductsInCartDto = new ShowProductsInCartDto();
            showProductsInCartDto.setId(product.getId());
            showProductsInCartDto.setName(product.getName());
            showProductsInCartDto.setCost(String.valueOf(product.getCost()));
            showProductsInCartDto.setImage(productDao.getProductImgNameProductId(Integer.parseInt(pId)));
            showProductsInCartDto.setQty(qty[i]);

            if (product.getProductAttributes().getDiscount() != null) {
                showProductsInCartDto.setDiscount(String.valueOf(product.getProductAttributes().getDiscount()));
            } else {
                showProductsInCartDto.setDiscount("0");
            }

            if (product.getProductAttributes().getActualCost() != null) {
                showProductsInCartDto.setActualCost(String.valueOf(product.getProductAttributes().getActualCost()));
            } else {
                showProductsInCartDto.setActualCost("0");
            }

            list.add(showProductsInCartDto);

        }

        if (tempOrders.getOrderDate().toLocalDate().isBefore(LocalDate.now())) {
            tempOrders.setOrderDate(LocalDateTime.now());
            tempOrdersDao.updateTempOrders(tempOrders);
        }

        ShowProductsInCartDto showProductsInCartDto = new ShowProductsInCartDto();
        showProductsInCartDto.setArrivalDate(new DateHelper().getDateInFormate(tempOrders.getOrderDate().toLocalDate().plusDays(7)));
        list.add(showProductsInCartDto);

        return list;

    }

    public boolean placeOrder(PlaceOrderDto placeOrderDto, int userId) {

        TempOrders tempOrders = tempOrdersDao.ifOrderWithOutPaymentExists(userId);
        TempOrderDetails tempOrderDetails = tempOrdersDao.tempOrderDetails(tempOrders.getId(), userId);

        double sum = Double.sum(tempOrders.getTotalAmount(), tempOrders.getTotalAmount() * (0.28));

        if ((placeOrderDto.getTotal() != sum && placeOrderDto.getCouponId() == 0)) {

            return false;

        } else {

            User user = userDao.getUser(userId);

            Double finalTotal = tempOrders.getTotalAmount() + tempOrders.getTotalAmount() * (0.28);

            Integer methodTemp = placeOrderDto.getMethod();
            String method;

            switch (methodTemp) {

                case 1:
                    method = "credit or debit card";
                    break;

                case 2:
                    method = "net banking";
                    break;

                case 3:
                    method = "other UPI apps";
                    break;

                case 4:
                    method = "COD";
                    break;

                default:
                    return false;

            }

            Orders orders = new Orders();
            orders.setUserId(user);
            orders.setCancelled(false);
            orders.setSingleItemCancelled(false);
            orders.setEstimatedDate(LocalDateTime.now().plusDays(7));
            orders.setCompleted(false);
            orders.setAssigned(false);
            orders.setTotalAmount(placeOrderDto.getTotal());
            orders.setPaymentMethod(method);

            double check;

            if (placeOrderDto.getCouponId() != 0) {
                Coupon coupon = couponDao.getCouponFromId(placeOrderDto.getCouponId());
                if (coupon != null) {

                    if (coupon.getType().equals("Discount")) {
                        check = finalTotal - finalTotal * (coupon.getDiscount() / 100);
                    } else {
                        check = finalTotal - coupon.getDiscount();
                    }

                    int tempCheck = (int) check;

                    if (tempCheck == placeOrderDto.getTotal().intValue()) {
                        coupon.setAppliedCount(coupon.getAppliedCount() + 1);
                        couponDao.updateCoupon(coupon);
                        orders.setCouponId(coupon);
                    } else {
                        return false;
                    }


                } else {
                    return false;
                }
            }

            orders.setCustomerAddressId(customerAddressDao.getCustomerAddressFromId(placeOrderDto.getAddrId()));
            ordersDao.saveOrders(orders);

            String[] pIds = tempOrderDetails.getProductId().split(", ");
            String[] qty = tempOrderDetails.getQuantity().split(", ");
            String[] colors = tempOrderDetails.getColors().split(", ");

            for (int i = 0; i < pIds.length; i++) {

                String pId = pIds[i];

                Product product = productDao.getProductFromId(Integer.parseInt(pId));

                Stock stock = stockDao.getStockOfProductFromColor(product.getId(), colors[i]);
                stock.setStock(stock.getStock() - Integer.parseInt(qty[i]));
                stockDao.updateStock(stock);

                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setOrderId(orders);
                orderDetails.setProductId(product);
                orderDetails.setQuantity(Integer.parseInt(qty[i]));
                orderDetails.setPrice(product.getCost());
                orderDetails.setColor(colors[i]);

                ordersDao.saveOrderDetails(orderDetails);

                cartDao.deleteProductFromCart(Integer.parseInt(pId), userId);

            }

            tempOrders.setPaymentDone(true);
            tempOrdersDao.updateTempOrders(tempOrders);

            Sales sales = new Sales();
            sales.setTotalAmount(placeOrderDto.getTotal());
            sales.setOrderId(orders);
            salesDao.saveSales(sales);

            Double oldWallet = walletDao.getWalletDetails(userId);
            Double balance = oldWallet - placeOrderDto.getTotal();
            Wallet wallet = new Wallet();
            wallet.setUserId(user);
            wallet.setTransactionAmount(placeOrderDto.getTotal());
            wallet.setBalance(balance);
            wallet.setTransactionType(Wallet.TransactionType.DEBIT);
            wallet.setWalletTransactionReasonId(walletTransactionReasonDao.getWalletTrReasonId(6));
            wallet.setTransactionaDate(LocalDateTime.now());
            walletDao.saveWallet(wallet);

            return true;

        }

    }

    public String downloadInvoice(int userId, int orderId, HttpServletRequest request) throws FileNotFoundException, MalformedURLException {

        User user = userDao.getUser(userId);
        Orders order = null;

        if (orderId == 0) {
            order = ordersDao.getOrderFromUserId(userId);
            orderId = order.getId();
        } else {
            order = ordersDao.getOrderFromId(orderId);
        }

        HttpSession session = request.getSession();
        String destFolder = session.getServletContext().getRealPath("/") + "WEB-INF" + File.separator + "resources"
                + File.separator + "Invoices" + File.separator + userId + File.separator;

        File directory = new File(destFolder);

        if (!directory.exists()) {
            boolean mkdir = directory.mkdirs();
            if (!mkdir) {
                System.out.println("Failed to create directory: " + destFolder);
            }
        }

        String dest = session.getServletContext().getRealPath("/") + "WEB-INF" + File.separator + "resources"
                + File.separator + "Invoices" + File.separator + userId + File.separator + "Invoice_" + orderId + ".pdf";

        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.addNewPage();
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        float width = pdf.getDefaultPageSize().getWidth();
        float height = pdf.getDefaultPageSize().getHeight();

        PdfCanvas canvas = new PdfCanvas(pdf.getFirstPage());
        canvas.rectangle(20, 20, width - 40, height - 40);
        canvas.stroke();

        ImageData data = ImageDataFactory.create(
                "D:/eCommerce/eCommerce/src/main/webapp/WEB-INF/resources/images/user/MainLogo.png");
        Image img = new Image(data);
        img.setFixedPosition(20, PageSize.A4.getHeight() - 100);
        img.scaleToFit(80, 80);
        img.setMarginLeft(10);
        img.setMarginTop(-3);
        document.add(img);

        Paragraph heading = new Paragraph("Invoice")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(heading);

        CustomerAddress customerAddress = order.getCustomerAddressId();

        Paragraph customerAddrDetails = new Paragraph("Shipping Address:\n" +
                customerAddress.getFirstName() + " " + customerAddress.getLastName() + "\n" +
                customerAddress.getAddress() + ",\n" +
                customerAddress.getCity() + ", " + customerAddress.getState() + "\n" +
                customerAddress.getZipCode())
                .setMarginTop(70)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginLeft(5);
        document.add(customerAddrDetails);

        Table detailsTable = new Table(2);
        detailsTable.setWidth(UnitValue.createPercentValue(100));

        Paragraph orderDetails = new Paragraph("Order Details:\n" +
                "Order ID: " + orderId + "\n" +
                "Payment Method: " + order.getPaymentMethod() + "\n" +
                "Order Date: " + order.getOrderDate())
                .setMarginTop(20)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginLeft(5);
        detailsTable.addCell(new Cell().add(orderDetails).setBorder(Border.NO_BORDER));

        Paragraph customerDetails = new Paragraph("Customer Details:\n" +
                "Name: " + customerAddress.getFirstName() + " " + customerAddress.getLastName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Phone: " + user.getPhone())
                .setMarginTop(20)
                .setMarginRight(5);
        detailsTable.addCell(new Cell().add(customerDetails).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));

        document.add(detailsTable);

        document.add(new Paragraph("\n\n"));

        float[] columnWidths = {1, 6, 1, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Cell().add(new Paragraph("S/N")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Product")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Quantity")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Price")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        List<OrderDetails> orderDetailsList = ordersDao.getOrderDetailsFromOrderIdForInvoice(orderId);
        Double totalCost = 0.0;
        Double totalDiscount = 0.0;

        for (int i = 0; i < orderDetailsList.size(); i++) {

            OrderDetails orderDetails1 = orderDetailsList.get(i);
            ProductAttributes productAttrs = productDao.getProductAttrs(orderDetails1.getProductId().getId());
            Double price = orderDetails1.getPrice() * orderDetails1.getQuantity();

            Double productCost = productDao.getProductCostProductId(orderDetails1.getProductId().getId());
            totalCost += productCost * orderDetails1.getQuantity();

            Double productActualCost = productAttrs.getActualCost();
            Integer discount = productAttrs.getDiscount();
            if (productActualCost != null && discount != null) {
                totalDiscount += productActualCost * (discount * (0.01)) * orderDetails1.getQuantity();
                price = productActualCost * orderDetails1.getQuantity();
            }

            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
            table.addCell(new Cell().add(new Paragraph("Product " + orderDetails1.getProductId().getName())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(orderDetails1.getQuantity()))));
            table.addCell(new Cell().add(new Paragraph("INR " + price)));

        }

        if (order.getCouponId() != null) {
            Coupon coupon = couponDao.getCouponFromId(order.getCouponId().getId());

            if (coupon.getType().equals("Discount")) {
                totalDiscount += (totalCost * (coupon.getDiscount() * (0.01)));
            } else {
                totalDiscount += coupon.getDiscount();
            }

        }

        Double GST = totalCost * (0.28);

        table.addCell(new Cell(1, 3).add(new Paragraph("GST (28%) "))).setTextAlignment(TextAlignment.RIGHT);
        table.addCell(new Cell().add(new Paragraph("INR " + String.format("%.1f", GST))));

        table.addCell(new Cell(1, 3).add(new Paragraph("Discount"))).setTextAlignment(TextAlignment.RIGHT);
        table.addCell(new Cell().add(new Paragraph("INR " + totalDiscount)));

        table.addCell(new Cell(1, 3).add(new Paragraph("Total"))).setTextAlignment(TextAlignment.RIGHT);
        table.addCell(new Cell().add(new Paragraph("INR " + order.getTotalAmount())));

        document.add(table);

        String baseURL = new GetContextPath().getProjectBaseURL(request);
        String connect = baseURL + "/user/clutchandcarry";
        Paragraph footer = new Paragraph("Thank you for Shopping With Us!" + "\n" +
                "Stay connected : " + connect)
                .setMarginTop(80)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(footer);

        document.close();

        String pathTemp = String.format("/%s/%s/%s/", "resources", "Invoices", userId);
        String path = request.getContextPath() + pathTemp + "Invoice_" + orderId + ".pdf";

        return path;

    }

    public List<GetDataForOrdersDto> getDataForOrders(int userId, int typeId) {

        List<Orders> ordersList = ordersDao.getOrderListFromUserId(userId, typeId);

        List<GetDataForOrdersDto> list = new ArrayList<>();

        for (int i = 0; i < ordersList.size(); i++) {

            Orders orders = ordersList.get(i);
            List<OrderDetails> orderDetailsFromOrderId = ordersDao.getOrderDetailsFromOrderId(orders.getId());

            for (int j = 0; j < orderDetailsFromOrderId.size(); j++) {

                OrderDetails orderDetails = orderDetailsFromOrderId.get(j);

                if (((typeId == 1 || typeId == 2) && orderDetails.isCancelled()) || (typeId == 3 && !orderDetails.isCancelled())) {
                } else {

                    Product product = orderDetails.getProductId();
                    String productImg = productDao.getProductImgNameProductId(product.getId());
                    CustomerAddress customerAddress = orders.getCustomerAddressId();

                    GetDataForOrdersDto getDataForOrdersDto = new GetDataForOrdersDto();
                    getDataForOrdersDto.setOrderId(orders.getId());
                    getDataForOrdersDto.setOrderTotal(String.valueOf(orders.getTotalAmount()));
                    getDataForOrdersDto.setFName(customerAddress.getFirstName());
                    getDataForOrdersDto.setLName(customerAddress.getLastName());
                    getDataForOrdersDto.setProductId(product.getId());
                    getDataForOrdersDto.setPName(product.getName());
                    getDataForOrdersDto.setPImage(productImg);
                    getDataForOrdersDto.setPCost(String.valueOf(product.getCost()));
                    getDataForOrdersDto.setPaymentMethod(orders.getPaymentMethod());
                    getDataForOrdersDto.setAddress(customerAddress.getAddress());
                    getDataForOrdersDto.setCity(customerAddress.getCity());
                    getDataForOrdersDto.setState(customerAddress.getState());
                    getDataForOrdersDto.setZipCode(Integer.parseInt(customerAddress.getZipCode()));
                    getDataForOrdersDto.setOrderedDate(new DateHelper().getDateInFormate(orders.getOrderDate().toLocalDate()));

                    if (orders.isCancelled() || orderDetails.isCancelled()) {
                        getDataForOrdersDto.setCancelledDate(new DateHelper().getDateInFormate(orders.getCancelledDate().toLocalDate()));
                    } else {
                        getDataForOrdersDto.setCancelledDate("NA");
                    }

                    list.add(getDataForOrdersDto);

                }

            }

        }

        return list;

    }

    public String cancelOrder(CancelOrderDto cancelOrderDto, int userId) {

        if ((cancelOrderDto.getCancellationReason().equals("Other") && (cancelOrderDto.getReason().isBlank() || cancelOrderDto.getReason().isEmpty()))) {
            return "Please Enter a valid reason for cancellation.";
        } else {

            Orders order = ordersDao.getOrderFromId(cancelOrderDto.getOrdId());

            if (!order.isAssigned()) {

                if (cancelOrderDto.getCancellationType().equals("1")) {

                    OrderDetails orderDetails1 = ordersDao.getOrderDetailsFromIdAndPId(cancelOrderDto.getProdId(), cancelOrderDto.getOrdId());
                    orderDetails1.setCancelled(true);
                    orderDetails1.setCancelledDate(LocalDateTime.now());
                    ordersDao.updateOrderDetails(orderDetails1);

                    Stock stock = stockDao.getStockOfProduct(cancelOrderDto.getProdId());
                    Integer oldStock = stock.getStock();
                    stock.setStock(oldStock + orderDetails1.getQuantity());
                    stockDao.updateStock(stock);

                    double refundTemp = (orderDetails1.getPrice() * orderDetails1.getQuantity());
                    double orderTotal = 0;

                    List<OrderDetails> orderDetailsFromOrderId = ordersDao.getOrderDetailsFromOrderId(cancelOrderDto.getOrdId());

                    for (OrderDetails orderDetails : orderDetailsFromOrderId) {

                        orderTotal += (orderDetails.getQuantity() * orderDetails.getPrice());

                    }

                    refundTemp += refundTemp * (0.28);
                    orderTotal += (orderTotal * (0.28));
                    Double refund;
                    Double newOrderTotal;

                    Coupon coupon = order.getCouponId();

                    if (coupon != null) {

                        Double percentageInOrderTotal = refundTemp * (100) / orderTotal;
                        Double couponValue;
                        Double couponDeduction;

                        if (coupon.getType().equals("Discount")) {
                            couponValue = orderTotal * coupon.getDiscount() / (100);
                        } else {
                            couponValue = coupon.getDiscount();
                        }

                        couponDeduction = couponValue * percentageInOrderTotal / (100);
                        refund = (refundTemp - couponDeduction) - ((refundTemp - couponDeduction) * (0.05));
                        newOrderTotal = order.getTotalAmount() - refund;

                    } else {

                        refund = refundTemp - refundTemp * (0.05);
                        newOrderTotal = order.getTotalAmount() - refund;

                    }

                    order.setSingleItemCancelled(true);
                    order.setCancelledDate(LocalDateTime.now());
                    order.setTotalAmount(newOrderTotal);
                    ordersDao.updateOrders(order);

                    CancelledOrders cancelledOrders = new CancelledOrders();
                    cancelledOrders.setUserId(order.getUserId());
                    cancelledOrders.setOrderId(ordersDao.getOrderFromId(cancelOrderDto.getOrdId()));
                    cancelledOrders.setProductId(productDao.getProductFromId(cancelOrderDto.getProdId()));
                    cancelledOrders.setReason(cancelOrderDto.getCancellationReason());
                    cancelledOrders.setReason(cancelOrderDto.getCancellationReason());
                    cancelledOrders.setReasonText(cancelOrderDto.getReason());
                    cancelledOrdersDao.saveCancelledOrders(cancelledOrders);

                    Wallet wallet = new Wallet();
                    Double oldWalletBalance = walletDao.getWalletDetails(order.getUserId().getId());
                    double newWallet = oldWalletBalance + refund;
                    wallet.setUserId(order.getUserId());
                    wallet.setTransactionType(Wallet.TransactionType.CREDIT);
                    wallet.setTransactionAmount(refund);
                    wallet.setWalletTransactionReasonId(walletTransactionReasonDao.getWalletTrReasonId(4));
                    wallet.setTransactionaDate(LocalDateTime.now());
                    wallet.setBalance(newWallet);
                    walletDao.saveWallet(wallet);

                    return "success";

                } else if (cancelOrderDto.getCancellationType().equals("2")) {

                    ordersDao.cancelOrder(cancelOrderDto.getOrdId());
                    ordersDao.cancelAllroducts(cancelOrderDto.getOrdId());

                    List<Product> productIds = ordersDao.getAllProductIdsFromOrderId(cancelOrderDto.getOrdId());

                    for (int i = 0; i < productIds.size(); i++) {

                        CancelledOrders cancelledOrders = new CancelledOrders();
                        cancelledOrders.setUserId(order.getUserId());
                        cancelledOrders.setOrderId(ordersDao.getOrderFromId(cancelOrderDto.getOrdId()));
                        cancelledOrders.setReason(cancelOrderDto.getCancellationReason());
                        cancelledOrders.setReasonText(cancelOrderDto.getReason());
                        cancelledOrders.setProductId(productIds.get(i));

                        cancelledOrdersDao.saveCancelledOrders(cancelledOrders);

                        Stock stock = stockDao.getStockOfProduct(productIds.get(i).getId());
                        Integer oldStock = stock.getStock();
                        OrderDetails orderDetails = ordersDao.getOrderDetailsFromIdAndPId(productIds.get(i).getId(), cancelOrderDto.getOrdId());
                        stock.setStock(oldStock + orderDetails.getQuantity());
                        stockDao.updateStock(stock);

                    }

                    Wallet wallet = new Wallet();
                    Double oldWalletBalance = walletDao.getWalletDetails(order.getUserId().getId());
                    Double newWalletAmount = order.getTotalAmount() - (order.getTotalAmount() * (0.05));
                    double newWallet1 = oldWalletBalance + newWalletAmount;
                    wallet.setUserId(order.getUserId());
                    wallet.setTransactionType(Wallet.TransactionType.CREDIT);
                    wallet.setTransactionAmount(newWalletAmount);
                    wallet.setWalletTransactionReasonId(walletTransactionReasonDao.getWalletTrReasonId(4));
                    wallet.setTransactionaDate(LocalDateTime.now());
                    wallet.setBalance(newWallet1);
                    walletDao.saveWallet(wallet);

                    return "success";

                }

            } else {
                return "The order is already shipped. You can not cancel any item now!";
            }

        }

        return "Something went wrong!";
    }

    public boolean isNotificationsOn(int userId) {
        return notificationsDao.ifUserHasNotiOn(userId);
    }

    public List<GetNotificationsDto> getNotifications(int userId, boolean isSeen) {

        UserNotifications ifUserHasNotiOn = notificationsDao.getIfUserHasNotiOn(userId);

        List<GetNotificationsDto> list = new ArrayList<>();

        if (ifUserHasNotiOn.isNotiOn()) {

            List<Integer> seenNotificationIds = notificationsDao.getSeenNotificationIds(userId);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("d MMM, yyyy h:mm a");

            if (!isSeen) {

                List<Notifications> notificationsList = notificationsDao.ifNewNotificationAdded();

                for (Notifications notifications : notificationsList) {

                    if (!seenNotificationIds.contains(notifications.getId()) && notifications.getType() == 1 && notifications.getUserId().getId() == userId) {
                        GetNotificationsDto getNotificationsDto = new GetNotificationsDto();
                        getNotificationsDto.setId(notifications.getId());
                        getNotificationsDto.setSubject(notifications.getSubject());
                        getNotificationsDto.setDescription(notifications.getDescription());
                        getNotificationsDto.setSeen(false);
                        getNotificationsDto.setDateTime(notifications.getCreatedDate().format(outputFormatter));

                        list.add(getNotificationsDto);
                    } else if (!seenNotificationIds.contains(notifications.getId()) && notifications.getType() == 0) {
                        GetNotificationsDto getNotificationsDto = new GetNotificationsDto();
                        getNotificationsDto.setId(notifications.getId());
                        getNotificationsDto.setSubject(notifications.getSubject());
                        getNotificationsDto.setDescription(notifications.getDescription());
                        getNotificationsDto.setSeen(false);
                        getNotificationsDto.setDateTime(notifications.getCreatedDate().format(outputFormatter));

                        list.add(getNotificationsDto);
                    }

                }
            } else {

                for (Integer notificationId : seenNotificationIds) {

                    Notifications notifications = notificationsDao.getNotificationsFromId(notificationId);

                    GetNotificationsDto getNotificationsDto = new GetNotificationsDto();
                    getNotificationsDto.setId(notifications.getId());
                    getNotificationsDto.setSubject(notifications.getSubject());
                    getNotificationsDto.setDescription(notifications.getDescription());
                    getNotificationsDto.setSeen(true);
                    getNotificationsDto.setDateTime(notifications.getCreatedDate().format(outputFormatter));

                    list.add(getNotificationsDto);

                }

            }

        }

        return list;

    }

    public int getCountForNotifications(int userId) {

        List<Notifications> notificationsList = notificationsDao.ifNewNotificationAdded();
        UserNotifications ifUserHasNotiOn = notificationsDao.getIfUserHasNotiOn(userId);

        int count = 0;

        if (ifUserHasNotiOn.isNotiOn()) {

            for (Notifications notifications : notificationsList) {

                if (notifications.getType() == 1 && notifications.getUserId().getId() == userId) {
                    count++;
                } else if (notifications.getType() == 0) {
                    count++;
                }

            }

        }

        return count;

    }

    public String addToSeenNotifications(List<Integer> notificationIds, int userId) {

        if (notificationIds == null || notificationIds.isEmpty()) {
            return "oops! something went wrong!";
        }

        for (Integer notificationId : notificationIds) {

            SeenNotifications seenNotifications = new SeenNotifications();

            Notifications notifications = notificationsDao.getNotificationsFromId(notificationId);
            if (notifications == null) {
                return "oops! something went wrong!";
            }

            seenNotifications.setNotificationsId(notifications);
            seenNotifications.setUserId(userDao.getUser(userId));

            System.out.println(notificationId + "/////////////////////////////////////////");

            notificationsDao.saveSeenNotifications(seenNotifications);

        }

        return "success";

    }

    public void stopNotifications(int userId) {

        notificationsDao.stopNotifications(userId);

    }

    public void onNotifications(int userId) {

        notificationsDao.onNotifications(userId);

    }

    @Transactional
    public int checkStock(int pId, String color, HttpServletRequest request) {

        Stock stock = stockDao.getStockOfProductFromColor(pId, color);
        Integer stock1 = stock.getStock();

        if (stock1 < 10) {
            Tuple productNameAndCostProductId = productDao.getProductNameAndCostProductId(pId);
            String productName = (String) productNameAndCostProductId.get(0);
            Seller seller = productDao.getSellerByProductId(pId);
            String action = emailLogDao.checkForBusinessMailForStock(seller.getEmail());

            if (action == null || !action.startsWith("Please Refill The Product")) {

                String baseURL = new GetContextPath().getProjectBaseURL(request);

                String sendTo = seller.getEmail();
                String subject = "Please Refill The Product : " + productName;

                String body = "\n" +
                        "Hello Seller,\n" +
                        "\n" +
                        "Your product : " + productName + "is getting out of stock. \n" +
                        "\n" +
                        "Please contact admin for refilling your product ASAP." + "\n" +
                        "\n" +
                        "We hope you will reach out to the admin and get exciting profits on your way!\n" +
                        "\n" +
                        "Best regards,\n" +
                        "The Clucth&Carry Team";

//                mailService.send(sendTo, subject, body);

                EmailLogs emailLogs = new EmailLogs();
                emailLogs.setEmail(seller.getEmail());
                emailLogs.setRecipient(seller.getBusinessName());
                emailLogs.setSentDateTime(LocalDateTime.now());
                emailLogs.setAction(subject);

                emailLogDao.saveEmailLog(emailLogs);

            }

            return 1;

        }

        return 0;

    }

}
