package com.example.UBXUtilityOne.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDaoImpl {
    private static Logger logger = Logger.getLogger(NotificationDaoImpl.class);

    @Autowired
    @Qualifier("jdbcTemplate3")
    private JdbcTemplate jdbcTemplate3;

    public void deleteNotifications(String customerId) {
        logger.info("inside deleteNotifications method with customerId " + customerId);
        int result = jdbcTemplate3.update("Delete from notification WHERE party_id=?", new Object[]{customerId});
        if (result > 0) {
            logger.info("deleted from notifications table for party_Id " + customerId);
        } else {
            logger.info("No rows found in notification table for party_Id " + customerId);
        }

        int result2 = jdbcTemplate3.update("Delete from user_notification_status WHERE party_id=?", new Object[]{customerId});
        if (result2 > 0) {
            logger.info("deleted from user_notification_status table for party_Id " + customerId);
        } else {
            logger.info("No rows found in user_notification_status table for party_Id " + customerId);
        }
    }
}
