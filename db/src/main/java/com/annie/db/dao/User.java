package com.annie.db.dao;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name="user")
public class User implements Serializable {

    private static final long serialVersionUID = 1l;

    private long id;
    private String username;
    private String password;
}