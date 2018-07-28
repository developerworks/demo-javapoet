package com.example.entity;

import java.lang.String;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Config {
    private String id;

    private String name;

    private String description;

    private String createdAt;
}
