package com.example.UBXUtilityOne.service;

import com.example.UBXUtilityOne.dao.CustomerDaoImpl;
import com.example.UBXUtilityOne.dao.EntitlementDaoImpl;
import com.example.UBXUtilityOne.dao.IAMDaoImpl;
import com.example.UBXUtilityOne.dao.NotificationDaoImpl;
import com.example.UBXUtilityOne.model.CustomerResponse;
import com.example.UBXUtilityOne.model.EncryptionRequest;
import com.example.UBXUtilityOne.model.EncryptionResponse;
import com.example.UBXUtilityOne.model.RestResponseEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Transactional
@Service
public class CustomerServiceImpl {

    private static Logger logger = Logger.getLogger(CustomerServiceImpl.class);

    @Value("${encryptionUrl}")
    private String encryptionUrl;

    @Value("${customerApiUrl}")
    private String customerApiUrl;

    @Value("${accessApiUrl}")
    private String accessApiUrl;

    @Value("${ipApiUrl}")
    private String ipApiUrl;

    @Autowired
    private CustomerDaoImpl customerDaoImpl;
    @Autowired
    private EntitlementDaoImpl entitlementDaoImpl;
    @Autowired
    private NotificationDaoImpl notificationDaoImpl;
    @Autowired
    private IAMDaoImpl iamDaoImpl;

    public void deleteCustomer(String filePath,String partyId , String Token) throws Exception {
        logger.info("Inside deleteCustomer method.");
        List<String> fileEntries = null;
        if (partyId == null){
            fileEntries = getCustomerListFromCsv(filePath);
            if(fileEntries.size()==0)
                throw new Exception("No file entries found.");
            }
            else{
            fileEntries = new ArrayList<String>(){{add(partyId);}};
          }
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", Token);
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        for (String fileEntry : fileEntries) {
            logger.info("Processing for customerId " + fileEntry);
            String partyType = customerDaoImpl.getPartyType(fileEntry);
            String UrlFormed = customerApiUrl + fileEntry;
            CustomerResponse customerResponse = null;
            customerResponse = rt.exchange(
                    UrlFormed, HttpMethod.DELETE, entity, CustomerResponse.class).getBody();
            logger.info("customerResponse for customerId:"+ fileEntry+" "+customerResponse.toString());
            if (partyType.equalsIgnoreCase("person")) {
                if (((String) customerResponse.getMetadata().get("status")).equalsIgnoreCase("success")) {
                    String AccessApiUrlFormed = accessApiUrl + fileEntry;
                    Object object = rt.exchange(
                            AccessApiUrlFormed, HttpMethod.DELETE, entity, Object.class).getBody();
                    logger.info("AccessApiResponse for customerId:"+ fileEntry+" "+object.toString());
                    String email = customerDaoImpl.getPersonContactEmail(fileEntry);
                    String encryptedEmail = getEncryptedEmail(email);
                    if (encryptedEmail != null) {
                        iamDaoImpl.deleteIAMUserDetails(encryptedEmail);
                    }
                    notificationDaoImpl.deleteNotifications(fileEntry);
                    logger.info("Successfully deleted person db details for customerId " + fileEntry);
                }
            } else if (partyType.equalsIgnoreCase("organization")) {
                entitlementDaoImpl.deleteEntitlements(fileEntry);

                String ipApiUrlFormed = ipApiUrl + fileEntry +"/ips";
                Object object = rt.exchange(
                        ipApiUrlFormed, HttpMethod.DELETE, entity, Object.class).getBody();
                logger.info("IpApiResponse for customerId:"+ fileEntry+" "+object.toString());

                notificationDaoImpl.deleteNotifications(fileEntry);

                logger.info("Successfully deleted organization db details for customerId " + fileEntry);
            }
        }
        logger.info("Exiting deleteCustomer method.");
    }

    private static List<String> getCustomerListFromCsv(String filePath) throws IOException {
        logger.info("Inside getCustomerListFromCsv method ");
        //String filePath = System.getProperty("user.dir") + "/src/main/resources/PartyList.csv";
        filePath = filePath.replace("\\","/");
        ArrayList<String> fileEntries = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null)
                fileEntries.add(line);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        for (String entry : fileEntries) {
            System.out.println(entry);
        }
        logger.info("Exiting getCustomerListFromCsv method " + fileEntries.toString());
        return fileEntries;
    }

    private String getEncryptedEmail(String email) throws Exception {
        logger.info("Inside getEncryptedEmail method " + email);
        EncryptionResponse encryptionResponse = null;
        EncryptionRequest encryptionRequest = new EncryptionRequest();
        encryptionRequest.setText(email);
        RestTemplate rt2 = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
        messageConverters.add(converter);
        rt2.setMessageConverters(messageConverters);

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<EncryptionRequest> entity2 = new HttpEntity<EncryptionRequest>(encryptionRequest, null);
        encryptionResponse = rt2.exchange(
                encryptionUrl, HttpMethod.POST, entity2, EncryptionResponse.class).getBody();
        logger.info("Exiting getEncryptedEmail method. EncryptedEmail value " + encryptionResponse.getData());
        return encryptionResponse.getData();
    }
}
