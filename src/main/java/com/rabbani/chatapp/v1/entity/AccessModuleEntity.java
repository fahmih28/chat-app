package com.rabbani.chatapp.v1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "access_module")
public class AccessModuleEntity extends AuditEntity{

    @Id
    private String id;

    @Column(name = "code")
    private String code;

    @Column(name = "qualifier")
    private long qualifier;

}
