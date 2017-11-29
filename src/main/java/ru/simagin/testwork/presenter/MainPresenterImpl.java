package ru.simagin.testwork.presenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.simagin.testwork.data.dto.FiltersForm;
import ru.simagin.testwork.data.dto.TrafficDTO;
import ru.simagin.testwork.service.CustomerService;
import ru.simagin.testwork.service.TrafficService;
import ru.simagin.testwork.view.MainView;

import java.util.Collection;

@Component
public class MainPresenterImpl implements MainPresenter {

    private MainView view;
    private final TrafficService trafficService;
    private final CustomerService customerService;

    @Autowired
    public MainPresenterImpl(TrafficService trafficService, CustomerService customerService) {
        this.trafficService = trafficService;
        this.customerService = customerService;
    }

    @Override
    public void setView(MainView mainView) {
        this.view = mainView;
    }

    @Override
    public void onStart(Pageable pageable) {
        view.showCustomersList(customerService.findByCriteria("", pageable));

    }

    @Override
    public void customersFilterEvent(String value, Pageable pageable) {
        if (!StringUtils.isEmpty(value)) {
            view.showCustomersList(customerService.findByCriteria(value, pageable));
        } else {
            view.showCustomersList(customerService.findAll(pageable));
        }
    }

    @Override
    public void clearCustomerFilterEvent() {
        view.clearCustomerFilter();
    }

    @Override
    public void searchEvent(FiltersForm filtersForm) {
        Collection<TrafficDTO> traffics = this.trafficService.findByCriteria(filtersForm);
        view.showTrafficList(traffics);

        Long traffic = null;
        if (filtersForm.isUplink()) {
            traffic = this.trafficService.calculateUplinkTraffic(traffics);
        }
        if (filtersForm.isDownlink()) {
            traffic = this.trafficService.calculateDownlinkTraffic(traffics);
        }

        view.showTraffic(traffic);

        Long capacity = this.trafficService.calculateCapacity(
                filtersForm.getStartDate(), filtersForm.getFinishDate(), traffic);
        view.showChanelCapacity(capacity);
    }
}
