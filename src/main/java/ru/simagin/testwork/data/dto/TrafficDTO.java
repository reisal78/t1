package ru.simagin.testwork.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficDTO {

    private Date date;
    private String customer;
    private Long uplink;
    private Long downlink;
}
