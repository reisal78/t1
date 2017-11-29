package ru.simagin.testwork.view;

import org.springframework.data.domain.Page;
import ru.simagin.testwork.data.dto.CustomerDTO;
import ru.simagin.testwork.data.dto.TrafficDTO;

import java.util.Collection;

public interface MainView {

    void showCustomersList(Page<CustomerDTO> customers);

    void clearCustomerFilter();

    void showTrafficList(Collection<TrafficDTO> traffics);

    void showTraffic(Long traffic);

    void showChanelCapacity(Long throughput);
}
