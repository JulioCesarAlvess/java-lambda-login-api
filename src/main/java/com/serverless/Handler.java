package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.serverless.entity.ApiGatewayResponse;
import com.serverless.entity.User;
import com.serverless.outbound.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger log = LogManager.getLogger(Handler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        String body = (String) input.get("body");
        User receivedUser = new Gson().fromJson(body, User.class);
        log.info("realizando login do usuario: {}", receivedUser.getUsuario());
        UserRepository userRepository = new UserRepository();
        User user = userRepository.findByUsuario(receivedUser.getUsuario());
        log.info("usuario localizado: {}", receivedUser.getUsuario());
        if (user == null) {
            log.info("usuario nao localizado: {}", receivedUser.getUsuario());
            return ApiGatewayResponse.builder()
                    .setStatusCode(404)
                    .build();
        }
        if (!user.getSenha().equals(receivedUser.getSenha())) {
            log.info("senha incorreta do usuario: {}", receivedUser.getUsuario());
            return ApiGatewayResponse.builder()
                    .setStatusCode(401)
                    .build();
        }
        log.info("senha correta do usuario: {}", receivedUser.getUsuario());
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .build();
    }
}
