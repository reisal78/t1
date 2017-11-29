package ru.simagin.testwork.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class FiltersForm {

    private final Long customerId;
    private final Date startDate;
    private final Date finishDate;
    private final boolean uplink;
    private final boolean downlink;

}
