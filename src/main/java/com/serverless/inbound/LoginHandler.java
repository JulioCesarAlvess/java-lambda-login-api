package com.serverless.inbound;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.serverless.entity.ApiGatewayResponse;
import com.serverless.entity.User;
import com.serverless.inbound.dto.TokenDTO;
import com.serverless.outbound.AuthRepository;
import com.serverless.outbound.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class LoginHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger log = LogManager.getLogger(LoginHandler.class);

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
        AuthRepository authRepository = new AuthRepository();
        String token = authRepository.createAuth();
        if (token == null) {
            log.info("erro durante a criação da autenticação");
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
        log.info("senha correta do usuario: {}", receivedUser.getUsuario());
        TokenDTO tokenDto = new TokenDTO(token);
        Gson gson = new Gson();
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setRawBody(gson.toJson(tokenDto))
                .build();
    }
}
