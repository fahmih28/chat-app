package com.rabbani.chatapp.v1.entity.query;

public interface UserAuthQuery {

    String getId();

    String getEmail();

    String getPassword();

    String getFirstName();

    String getLastName();

    String getRoleName();

    Long[] getRoleQualifier();

}
