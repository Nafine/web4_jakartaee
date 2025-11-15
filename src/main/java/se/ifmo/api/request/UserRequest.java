package se.ifmo.api.request;

import lombok.Data;

@Data
public class UserRequest {
    private String login;
    private String password;
}
