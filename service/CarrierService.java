package com.eCommerce.service;

import com.eCommerce.dto.EditPersonalInfoDto;
import com.eCommerce.dto.GetAllCarrierOrders;
import com.eCommerce.dto.GetAllUsersDto;
import com.eCommerce.dto.GetCarrierMyProfileDto;
import com.eCommerce.helper.DateHelper;
import com.eCommerce.helper.GetContextPath;
import com.eCommerce.model.*;
import com.eCommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CarrierService {

    @Autowired
    private CarrierDao carrierDao;

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailLogDao emailLogDao;

    @Autowired
    private SalesDao salesDao;

    @Transactional
    public List<GetAllCarrierOrders> showCarrierOrders(String searchText, int curPage, String filters,int userId) {

        int startIndex = ((curPage - 1) * 5);
        int endIndex = 5;

        Carrier carrier = carrierDao.getCarrierFromUser(userId);
        List<CarrierOrders> carrierOrders = carrierDao.getAllCarrierOrders(searchText.trim(), startIndex, endIndex, filters, carrier.getId());
        Integer count = carrierDao.getAllCarrierOrdersCount(searchText.trim(), filters, carrier.getId());

        List<GetAllCarrierOrders> list = new ArrayList<>();

        for (CarrierOrders carrierOrders1 : carrierOrders) {

            GetAllCarrierOrders getAllCarrierOrders = new GetAllCarrierOrders();
            getAllCarrierOrders.setCarrierId(carrier.getId());
            getAllCarrierOrders.setOrderId(carrierOrders1.getOrderId().getId());
            getAllCarrierOrders.setCarrierOrderId(carrierOrders1.getId());
            getAllCarrierOrders.setTotalPrice(String.valueOf(carrierOrders1.getOrderId().getTotalAmount()));
            getAllCarrierOrders.setCustomer(carrierOrders1.getOrderId().getCustomerAddressId().getFirstName()+" "+carrierOrders1.getOrderId().getCustomerAddressId().getLastName());
            getAllCarrierOrders.setCity(carrier.getCity());
            getAllCarrierOrders.setState(carrier.getState());
            getAllCarrierOrders.setZipCode(carrier.getZipCode());
            getAllCarrierOrders.setCompleted(carrierOrders1.isDelivered());
            getAllCarrierOrders.setCanNotDeliver(carrierOrders1.isNotAbleToDeliver());
            getAllCarrierOrders.setCount(count);

            list.add(getAllCarrierOrders);

        }

        return list;

    }

    public void completeOrder(int carrierOrderId) {

        CarrierOrders carrierOrder = carrierDao.getCarrierOrderFromId(carrierOrderId);
        carrierOrder.setDelivered(true);
        carrierDao.updateCarrierOrders(carrierOrder);

        Carrier carrier = carrierOrder.getCarrierId();
        int count = carrier.getCompletedOrdersCount();
        carrier.setCompletedOrdersCount(count+1);
        carrierDao.updateCarrier(carrier);

        Orders orders = carrierDao.getOrderIdFromCarrierOrderId(carrierOrderId);
        orders.setCompleted(true);
        ordersDao.updateOrders(orders);

        Revenue revenue = new Revenue();
        double value = orders.getTotalAmount() * (0.20);
        revenue.setTotalAmount(value);
        revenue.setOrderId(orders);
        salesDao.saveRevenue(revenue);

    }

    public String notBeDeliveredOrder(Integer carrierOrderId, String reason) {

        if (!carrierDao.doCarrierOrderIdExists(carrierOrderId)) {
            return "Something went wrong!!";
        }
        else if (reason==null || reason.isBlank() || reason.length()>300) {
            return "Please add valid reason!";
        }
        CarrierOrders carrierOrders = carrierDao.getCarrierOrderFromId(carrierOrderId);
        carrierOrders.setNotAbleToDeliver(true);
        carrierOrders.setReason(reason);
        carrierDao.updateCarrierOrders(carrierOrders);

        Orders orders = carrierOrders.getOrderId();
        orders.setAssigned(false);
        ordersDao.updateOrders(orders);

        return "success";

    }

    @Transactional
    public GetCarrierMyProfileDto getDataOfCarrier(int userId) {

        User user = userDao.getUser(userId);
        Carrier carrier = carrierDao.getCarrierFromUser(userId);

        GetCarrierMyProfileDto getCarrierMyProfileDto = new GetCarrierMyProfileDto();
        getCarrierMyProfileDto.setFirstName(user.getFirstName());
        getCarrierMyProfileDto.setLastName(user.getLastName());
        getCarrierMyProfileDto.setPhone(user.getPhone());
        getCarrierMyProfileDto.setEmail(user.getEmail());
        getCarrierMyProfileDto.setRole(user.getRoleId().getRoleName());

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

        getCarrierMyProfileDto.setGender(gender);
        getCarrierMyProfileDto.setCity(carrier.getCity());
        getCarrierMyProfileDto.setState(carrier.getState());
        getCarrierMyProfileDto.setZipCode(carrier.getZipCode());

        return getCarrierMyProfileDto;

    }

    @Transactional
    public String editPersonalInfo(GetCarrierMyProfileDto getCarrierMyProfileDto, int userId, HttpServletRequest request) {

        Set<Integer> set = new HashSet<>(Arrays.asList(0, 1, 2));

        int genD = getCarrierMyProfileDto.getGender();
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
            user.setFirstName(getCarrierMyProfileDto.getFirstName());
            user.setLastName(getCarrierMyProfileDto.getLastName());
            user.setGender(gender);
            user.getUserMainId().setModifiedDate(LocalDateTime.now());
            user.getUserMainId().setModifiedBy(userDao.getUser(userId));
            user.getUserMainId().setModifiedDate(LocalDateTime.now());
            userDao.updateUser(user);

            Carrier carrier = carrierDao.getCarrierFromUser(userId);
            carrier.setCity(getCarrierMyProfileDto.getCity());
            carrier.setState(getCarrierMyProfileDto.getState());
            carrier.setZipCode(getCarrierMyProfileDto.getZipCode());
            carrierDao.updateCarrier(carrier);

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
                    "First Name: " + getCarrierMyProfileDto.getFirstName() + "\n" +
                    "Last Name: " + getCarrierMyProfileDto.getLastName() + "\n" +
                    "City: " + getCarrierMyProfileDto.getCity() + "\n" +
                    "State: " + getCarrierMyProfileDto.getState() + "\n" +
                    "Zip Code: " + getCarrierMyProfileDto.getZipCode() + "\n" +
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
            emailLogs.setAction("profile update of carrier");

            emailLogDao.saveEmailLog(emailLogs);

            return "success";

        } else {

            return "Enter a valid gender";

        }

    }

}
