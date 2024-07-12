package com.rabbani.chatapp.v1.entity;

import io.hypersistence.utils.hibernate.type.array.LongArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Entity(name = "role")
public class RoleEntity extends AuditEntity{

    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Type(LongArrayType.class)
    @Column(name = "qualifier")
    private long[] qualifier;
}
