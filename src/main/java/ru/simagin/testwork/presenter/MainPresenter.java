package ru.simagin.testwork.presenter;

import org.springframework.data.domain.Pageable;
import ru.simagin.testwork.data.dto.FiltersForm;
import ru.simagin.testwork.view.MainView;

public interface MainPresenter extends BasePresenter<MainView> {

    void onStart(Pageable pageable);

    void customersFilterEvent(String value, Pageable pageable);

    void clearCustomerFilterEvent();


    void searchEvent(FiltersForm filtersForm);
}
