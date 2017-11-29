package ru.simagin.testwork.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simagin.testwork.data.entity.Traffic;
import ru.simagin.testwork.data.dto.FiltersForm;
import ru.simagin.testwork.data.dto.TrafficDTO;
import ru.simagin.testwork.data.repository.TrafficRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specifications.where;

@Service
@Slf4j
public class TrafficServiceImpl implements TrafficService {

    private final TrafficRepository trafficRepository;

    @Autowired
    public TrafficServiceImpl(TrafficRepository trafficRepository) {
        this.trafficRepository = trafficRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Collection<TrafficDTO> findByCriteria(FiltersForm filtersForm) {
        Specification<Traffic> specification = null;
        if (filtersForm.isUplink()) {
            specification = where(specification).and(((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("uplink"), 0)));
        } else {
            specification = where(specification).and(((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("downlink"), 0)));
        }

        if (filtersForm.getCustomerId() != null) {
            specification = where(specification).and(((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("customer"), filtersForm.getCustomerId())));
        }

        if (filtersForm.getStartDate() != null) {
            specification = where(specification).and(((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("date"), filtersForm.getStartDate())));
        }
        if (filtersForm.getFinishDate() != null) {
            specification = where(specification).and(((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("date"), filtersForm.getFinishDate())));
        }

        return convertTrafficListToDto(trafficRepository.findAll(specification, new Sort(Sort.Direction.ASC, "date")));
    }

    private Collection<TrafficDTO> convertTrafficListToDto(List<Traffic> all) {
        return all.stream().map((Traffic traffic) -> new TrafficDTO(
                traffic.getDate(),
                traffic.getCustomer().getLastName(),
                traffic.getUplink(),
                traffic.getDownlink())).collect(Collectors.toList());
    }

    @Override
    public Long calculateUplinkTraffic(Collection<TrafficDTO> traffics) {
        return traffics.stream().map(TrafficDTO::getUplink).mapToLong(u -> u).sum();
    }

    @Override
    public Long calculateDownlinkTraffic(Collection<TrafficDTO> traffics) {
        return traffics.stream().map(TrafficDTO::getDownlink).mapToLong(u -> u).sum();
    }

    @Override
    public Long calculateCapacity(Date start, Date finish, Long traffic) {
        if (start == null || finish == null || traffic == null) {
            return -1L;
        }
        Long time = (finish.getTime() - start.getTime())/1000;
        if (time == 0) {
            return 0L;
        }
        return (traffic * 8) / time;
    }
}
