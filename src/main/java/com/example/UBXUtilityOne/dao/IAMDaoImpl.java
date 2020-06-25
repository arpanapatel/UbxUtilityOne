package com.example.UBXUtilityOne.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


@Repository
public class IAMDaoImpl {

    private static Logger logger = Logger.getLogger(EntitlementDaoImpl.class);

    @Value("${mongoClientUriName}")
    private String mongoClientUriName;

    @Value("${databaseName}")
    private String databaseName;

    @Value("${collectionName}")
    private String collectionName;

    @Bean
    public MongoCollection<Document>  collection() {
        MongoClientURI mongoClientURI = new MongoClientURI(mongoClientUriName);

        MongoClient mongoClient = new MongoClient(mongoClientURI);

        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        return collection;
    }

    @Autowired
    MongoCollection<Document>  collection;

    public void deleteIAMUserDetails(String encryptedEmail) {
        logger.info("inside deleteIAMUserDetails method with encryptedEmail "+ encryptedEmail);

        BasicDBObject document = new BasicDBObject();
        document.put("email", encryptedEmail);
        Object object = collection.findOneAndDelete(document);
        if(object==null) {
            logger.info("No records found in IAM DB for encryptedMail.");
        }
        else{
            logger.info("record found and deleted for encryptedMail");
        }
        logger.info("Exiting deleteIAMUserDetails after execution.");

    }

}
