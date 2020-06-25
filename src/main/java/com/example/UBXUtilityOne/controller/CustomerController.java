package com.example.UBXUtilityOne.controller;

import com.example.UBXUtilityOne.dao.CustomerDaoImpl;
import com.example.UBXUtilityOne.dao.EntitlementDaoImpl;
import com.example.UBXUtilityOne.dao.IAMDaoImpl;
import com.example.UBXUtilityOne.dao.NotificationDaoImpl;
import com.example.UBXUtilityOne.model.CustomerResponse;
import com.example.UBXUtilityOne.model.EncryptionResponse;
import com.example.UBXUtilityOne.model.EncryptionRequest;
import com.example.UBXUtilityOne.model.RestResponseEntity;
import com.example.UBXUtilityOne.service.CustomerServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
public class CustomerController {

    private static Logger logger = Logger.getLogger(CustomerController.class);

    @Autowired
    CustomerServiceImpl customerServiceImpl;

    private RestResponseEntity restResponseEntity;

    @RequestMapping(method = RequestMethod.DELETE,value ="/deleteMultipleCustomer")
    public ResponseEntity<RestResponseEntity> deleteCustomerFromCsvFile(HttpServletRequest request ,
                                                             @RequestParam(value = "filePath") String filePath) {
        logger.info("inside deleteCustomer method");
        restResponseEntity = new RestResponseEntity();

        if(request.getHeader("Authorization")==null){
            restResponseEntity.add("message", "Error");
            restResponseEntity.add("error_message", "No authentication token provided.");
            restResponseEntity.setResult(null);
            return new ResponseEntity<RestResponseEntity>(restResponseEntity,HttpStatus.FORBIDDEN);
        }
        try {
            customerServiceImpl.deleteCustomer(filePath,null,request.getHeader("Authorization"));
        }catch(Exception e){
            logger.error(e);
            restResponseEntity.add("message", "Error");
            restResponseEntity.add("error_message", e.getMessage());
            restResponseEntity.setResult(null);
            return new ResponseEntity<RestResponseEntity>(restResponseEntity,HttpStatus.NOT_FOUND);
        }
        logger.info("All customers deleted successfully.");
        restResponseEntity.add("message", "All customers deleted successfully.");
        restResponseEntity.setResult(null);
        return new ResponseEntity<RestResponseEntity>(restResponseEntity, HttpStatus.OK);
    }



    @RequestMapping(method = RequestMethod.DELETE,value ="/deleteSingleCustomer")
    public ResponseEntity<RestResponseEntity> deleteSingleCustomer(HttpServletRequest request ,
                                                             @RequestParam(value = "partyId") String partyId) {
        logger.info("inside deleteCustomer method");        restResponseEntity = new RestResponseEntity();
        restResponseEntity = new RestResponseEntity();

        if(request.getHeader("Authorization")==null){
            restResponseEntity.add("message", "Error");
            restResponseEntity.add("error_message", "No authentication token provided.");
            restResponseEntity.setResult(null);
            return new ResponseEntity<RestResponseEntity>(restResponseEntity,HttpStatus.FORBIDDEN);
        }
        try {
            customerServiceImpl.deleteCustomer(null,partyId,request.getHeader("Authorization"));
        }catch(Exception e){
            logger.error(e);
            restResponseEntity.add("message", "Error");
            restResponseEntity.add("error_message", e.getMessage());
            restResponseEntity.setResult(null);
            return new ResponseEntity<RestResponseEntity>(restResponseEntity,HttpStatus.NOT_FOUND);
        }
        logger.info("All customers deleted successfully.");
        restResponseEntity.add("message", "All customers deleted successfully.");
        restResponseEntity.setResult(null);
        return new ResponseEntity<RestResponseEntity>(restResponseEntity, HttpStatus.OK);
    }

}
