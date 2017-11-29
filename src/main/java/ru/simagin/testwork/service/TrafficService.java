package ru.simagin.testwork.service;

import ru.simagin.testwork.data.dto.FiltersForm;
import ru.simagin.testwork.data.dto.TrafficDTO;

import java.util.Collection;
import java.util.Date;

public interface TrafficService {

    Collection<TrafficDTO> findByCriteria(FiltersForm filtersForm);

    Long calculateUplinkTraffic(Collection<TrafficDTO> traffics);

    Long calculateDownlinkTraffic(Collection<TrafficDTO> traffics);

    Long calculateCapacity(Date start, Date finish, Long traffic);
}
