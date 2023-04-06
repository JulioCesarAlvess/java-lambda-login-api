package com.serverless.outbound;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.serverless.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final Logger LOG = LogManager.getLogger(UserRepository.class);
    private AmazonDynamoDB dynamoDB;

    public UserRepository() {
        this.dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    public User findByUsuario(String usuario) {
        Map<String, String> attributesNames = new HashMap<>();
        attributesNames.put("#usuario", "usuario");
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":usuario", new AttributeValue().withS(usuario));
        QueryRequest query = new QueryRequest("UserV2")
                .withIndexName("usuario-index")
                .withKeyConditionExpression("#usuario = :usuario")
                .withExpressionAttributeNames(attributesNames)
                .withExpressionAttributeValues(attributeValues);
        QueryResult result = this.dynamoDB.query(query);
        LOG.info("result: {}", result);
        if (result.getCount() > 0) {
            return new User(result.getItems().get(0).get("id").getS(),
                    result.getItems().get(0).get("usuario").getS(),
                    result.getItems().get(0).get("senha").getS());
        }
        return null;
    }
}
