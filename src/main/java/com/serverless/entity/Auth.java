package com.serverless.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Auth {
    private String id;
    private String token;
    private String idUsuario;
}
