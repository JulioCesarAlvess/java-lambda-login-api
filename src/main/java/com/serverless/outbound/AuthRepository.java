package com.serverless.outbound;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthRepository {
    private static final Logger log = LogManager.getLogger(AuthRepository.class);
    private AmazonDynamoDB dynamoDB;

    public AuthRepository() {
        this.dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    public String createAuth(String idUsuario) {
        PutItemRequest request = new PutItemRequest();
        request.setTableName("AuthV3");
        request.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("idUsuario", new AttributeValue(idUsuario));
        String token = generateUuid();
        map.put("token", new AttributeValue(token));
        request.setItem(map);
        try {
            PutItemResult result = dynamoDB.putItem(request);
            log.info("Status da criação da autenticação: {}", result.getSdkHttpMetadata().getHttpStatusCode());
            log.info("Token gerado autenticado com sucesso");
            return token;
        } catch (AmazonServiceException e) {
            log.error("Erro durante a criação da autenticação: {}", e.toString());
            return null;
        }
    }
    private String generateUuid() {
        log.info("Gerando token");
        UUID uuid = UUID.randomUUID();
        log.info("Token gerado com sucesso");
        return uuid.toString();
    }
}
