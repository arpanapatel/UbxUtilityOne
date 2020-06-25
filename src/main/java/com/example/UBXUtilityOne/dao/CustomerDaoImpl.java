package com.example.UBXUtilityOne.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDaoImpl {

    private static Logger logger = Logger.getLogger(CustomerDaoImpl.class);

    @Autowired
    @Qualifier("jdbcTemplate1")
    private JdbcTemplate jdbcTemplate1;

    public String getPartyType(String partyId) {
        logger.info("Inside getPartyType method with partyId "+partyId);
        String partyType = jdbcTemplate1.queryForObject("select party_type from party where party_id=? and deleted_on IS null",
                    new Object[]{Integer.parseInt(partyId)}, String.class);
        logger.info("Exiting getPartyType method with partyType "+partyType);
        return partyType;
    }

    public String getPersonContactEmail(String personId) {
        logger.info("Inside getPersonContactEmail method with personId "+personId);
        String email = jdbcTemplate1.queryForObject("select person_contact_email from person_contact pc where person_id = ?", new Object[]{Integer.parseInt(personId)}, String.class);
        logger.info("Exiting getPersonContactEmail method with email "+email);
        return email;
    }
}
