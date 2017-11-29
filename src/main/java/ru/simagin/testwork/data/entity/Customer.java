package ru.simagin.testwork.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer extends BaseEntity {

    private String lastName;
    private String firstName;

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }
}
