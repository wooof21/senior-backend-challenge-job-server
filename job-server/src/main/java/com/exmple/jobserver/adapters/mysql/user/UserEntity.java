package com.exmple.jobserver.adapters.mysql.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Data
@Builder
public class UserEntity {

    @Id
    @Column("user_id")
    private String userId;

    @Column("user_name")
    private String userName;
}
