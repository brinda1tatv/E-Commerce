package com.eCommerce.repository;

import com.eCommerce.model.WalletTransactionReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WalletTransactionReasonDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public WalletTransactionReason getWalletTrReasonId(int id) {
        return hibernateTemplate.get(WalletTransactionReason.class, id);
    }

}
