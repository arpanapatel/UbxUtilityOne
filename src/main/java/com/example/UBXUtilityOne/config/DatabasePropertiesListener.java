package com.example.UBXUtilityOne.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.example.UBXUtilityOne.controller.CustomerController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Properties;

public class DatabasePropertiesListener implements ApplicationListener<ApplicationPreparedEvent> {

    private static Logger logger = Logger.getLogger(DatabasePropertiesListener.class);

    private final static String CUSTOMER_DATASOURCE_USERNAME = "spring.customer-db.username";
    private final static String CUSTOMER_DATASOURCE_PASSWORD = "spring.customer-db.password";
    private final static String CUSTOMER_DATASOURCE_URL = "spring.customer-db.jdbcUrl";

    private final static String ENTITLEMENT_DATASOURCE_USERNAME = "spring.entitlement-db.username";
    private final static String ENTITLEMENT_DATASOURCE_PASSWORD = "spring.entitlement-db.password";
    private final static String ENTITLEMENT_DATASOURCE_URL = "spring.entitlement-db.jdbcUrl";

    private final static String NOTIFICATION_DATASOURCE_USERNAME = "spring.notification-db.username";
    private final static String NOTIFICATION_DATASOURCE_PASSWORD = "spring.notification-db.password";
    private final static String NOTIFICATION_DATASOURCE_URL = "spring.notification-db.jdbcUrl";


    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        Properties props = new Properties();

        String customerSecretName = "sm/appe/use1/ubx/customer/"+environment.getActiveProfiles()[0]+"/postgres" ;
        String ubxSecretName = "sm/appe/use1/ubx/"+environment.getActiveProfiles()[0] ;
        String region = "us-east-1" ;

        String secretJsonCustomer = getSecret(customerSecretName ,region);
        String dbUserCustomer = getString(secretJsonCustomer, "username");
        String dbPasswordCustomer = getString(secretJsonCustomer, "password");
        String dbUrlCustomer = getString(secretJsonCustomer, "host");
        String dbNameCustomer = getString(secretJsonCustomer, "dbname");

        String secretJsonUbx = getSecret(ubxSecretName ,region);
        String dbUserEntitlement = getString(secretJsonUbx, "entitlement.write.datasource.username");
        String dbPasswordEntitlement = getString(secretJsonUbx, "entitlement.write.datasource.password");
        String dbUrlEntitlement = getString(secretJsonUbx, "entitlement.write.datasource.url");
        String dbUserNotification = getString(secretJsonUbx, "notification.datasource.username");
        String dbPasswordNotification = getString(secretJsonUbx, "notification.datasource.password");
        String dbUrlNotification = getString(secretJsonUbx, "notification.datasource.url");


        props.put(CUSTOMER_DATASOURCE_USERNAME, dbUserCustomer);
        props.put(CUSTOMER_DATASOURCE_PASSWORD, dbPasswordCustomer);
        props.put(CUSTOMER_DATASOURCE_URL, "jdbc:postgresql://"+dbUrlCustomer+":5432/"+dbNameCustomer);
        props.put(ENTITLEMENT_DATASOURCE_USERNAME, dbUserEntitlement);
        props.put(ENTITLEMENT_DATASOURCE_PASSWORD, dbPasswordEntitlement);
        props.put(ENTITLEMENT_DATASOURCE_URL, dbUrlEntitlement);
        props.put(NOTIFICATION_DATASOURCE_USERNAME, dbUserNotification);
        props.put(NOTIFICATION_DATASOURCE_PASSWORD, dbPasswordNotification);
        props.put(NOTIFICATION_DATASOURCE_URL, dbUrlNotification);
        environment.getPropertySources().addFirst(new PropertiesPropertySource("aws.secret.manager", props));
        System.out.println("Successfully retrieved all db properties.");

    }

    private String getSecret(String secretName, String region ) {
        // Create a Secrets Manager client
        AWSSecretsManager client  = AWSSecretsManagerClientBuilder. standard ()
                . withRegion (region)
                . build ();
        // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
        // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
        // We rethrow the exception by default.
        String secret = null, decodedBinarySecret = null;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                . withSecretId (secretName);
        GetSecretValueResult getSecretValueResult = null ;
        try {
            getSecretValueResult = client. getSecretValue (getSecretValueRequest);
        } catch (DecryptionFailureException e) {
            // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
            // Deal with the exception here, and/or rethrow at your discretion.
            logger.error(e);
        } catch (InternalServiceErrorException e) {
            // An error occurred on the server side.
            // Deal with the exception here, and/or rethrow at your discretion.
            logger.error(e);
        } catch (InvalidParameterException e) {
            // You provided an invalid value for a parameter.
            // Deal with the exception here, and/or rethrow at your discretion.
            logger.error(e);
        } catch (InvalidRequestException e) {
            // You provided a parameter value that is not valid for the current state of the resource.
            // Deal with the exception here, and/or rethrow at your discretion.
            logger.error(e);
        } catch (ResourceNotFoundException e) {
            // We can't find the resource that you asked for.
            // Deal with the exception here, and/or rethrow at your discretion.
            logger.error(e);
        }
        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        if (getSecretValueResult. getSecretString () != null ) {
            secret = getSecretValueResult. getSecretString ();
        }
        else {
            decodedBinarySecret = new String(Base64. getDecoder (). decode (getSecretValueResult. getSecretBinary ()). array ());
        }
        return secret != null ? secret : decodedBinarySecret;
    }

    private String getString(String json, String path) {
        try {
            JsonNode root = mapper.readTree(json);
            return root.path(path).asText();
        } catch (IOException e) {
            logger.error("Can't get "+path+" from json "+json);
            return null;
        }
    }
}
