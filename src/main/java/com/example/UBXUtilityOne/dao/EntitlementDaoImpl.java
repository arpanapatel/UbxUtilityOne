package com.example.UBXUtilityOne.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EntitlementDaoImpl {

    private static Logger logger = Logger.getLogger(EntitlementDaoImpl.class);

    @Autowired
    @Qualifier("jdbcTemplate2")
    private JdbcTemplate jdbcTemplate2;

    public void deleteEntitlements(String customerId){
        logger.info("inside deleteEntitlements method with customerId "+ customerId);
        int result = jdbcTemplate2.update("UPDATE entitlement_v2 SET DELETED_ON=now() WHERE customer_id=?", new Object[]{customerId});
        if (result > 0) {
            logger.info(result +"rows deleted from entitlement_v2 table for party_Id " + customerId);
        } else {
            logger.info("No rows found in entitlement_v2 table for party_Id " + customerId);
        }
    }
}
