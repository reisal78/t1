package ru.simagin.testwork.view;


import com.vaadin.addon.pagination.Pagination;
import com.vaadin.addon.pagination.PaginationResource;
import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.simagin.testwork.data.dto.CustomerDTO;
import ru.simagin.testwork.data.dto.FiltersForm;
import ru.simagin.testwork.data.dto.TrafficDTO;
import ru.simagin.testwork.presenter.MainPresenter;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI implements MainView {

    private static int DEFAULT_PAGE_NUMBER = 1;
    private static int LIMIT_ROWS = 10;


    private TextField customerFilterField;
    private Button customerFilterButton;
    private Grid<TrafficDTO> trafficGrid;
    private Grid<CustomerDTO> customerGrid;
    private final MainPresenter presenter;
    private DateTimeField startDateTimeField;
    private DateTimeField finishDateTimeField;
    private RadioButtonGroup<Boolean> group;
    private Button searchButton;
    private Pagination customersGridPagination;
    private Label trafficLabel;
    private Label channelCapacityLabel;
    private Component resultLayout;

    public VaadinUI(MainPresenter presenter) {
        this.presenter = presenter;
        this.presenter.setView(this);
    }

    @Override
    protected void init(VaadinRequest request) {
        Component customerPanel = constructCustomerPanel();
        Component trafficPanel = constructTrafficPanel();

        initialListeners();

        HorizontalLayout mainLayout = new HorizontalLayout(customerPanel, trafficPanel);

        mainLayout.setExpandRatio(trafficPanel, 1F);
        mainLayout.setSizeFull();
        setContent(mainLayout);
        this.presenter.onStart(new PageRequest(0, LIMIT_ROWS));
    }

    private Component constructCustomerPanel() {

        this.customerFilterField = new TextField();
        this.customerFilterField.setPlaceholder("Filter by last name");
        this.customerFilterField.setValueChangeMode(ValueChangeMode.LAZY);

        this.customerFilterButton = new Button(VaadinIcons.CLOSE);

        HorizontalLayout filterBar = new HorizontalLayout(customerFilterField, customerFilterButton);

        this.customerGrid = new Grid<>(CustomerDTO.class);
        this.customerGrid.setHeight(300, Unit.PIXELS);
        this.customerGrid.setColumns("fullName");
        this.customerGrid.setHeight(100, Unit.PERCENTAGE);
        this.customerGrid.setSizeFull();

        this.customersGridPagination = createPagination(0, DEFAULT_PAGE_NUMBER, LIMIT_ROWS);


        VerticalLayout layout = new VerticalLayout(filterBar, customerGrid, customersGridPagination);
        layout.setSizeUndefined();
        return layout;
    }

    private Component constructTrafficPanel() {

        this.startDateTimeField = new DateTimeField("From");
        this.startDateTimeField.setDateFormat("dd.MM.yyyy HH:mm:ss");
        this.startDateTimeField.setResolution(DateTimeResolution.SECOND);
        this.finishDateTimeField = new DateTimeField("To");
        this.finishDateTimeField.setDateFormat("dd.MM.yyyy HH:mm:ss");
        this.finishDateTimeField.setResolution(DateTimeResolution.SECOND);

        this.group = new RadioButtonGroup<>();
        this.group.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        this.group.setItems(Boolean.TRUE, Boolean.FALSE);
        this.group.setItemCaptionGenerator(item -> item ? "Uplink" : "Downlink");
        this.group.setSelectedItem(true);

        this.searchButton = new Button("Search", VaadinIcons.SEARCH);
        this.searchButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        this.searchButton.addStyleName("primary");


        this.trafficGrid = new Grid<>(TrafficDTO.class);
        this.trafficGrid.setSizeFull();
        this.trafficGrid.setColumns("uplink", "downlink", "customer");
        this.trafficGrid.addColumn("date", new DateRenderer(new DateFormat() {
            @Override
            public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
                return new StringBuffer(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date));
            }

            @Override
            public Date parse(String source, ParsePosition pos) {
                return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(source, pos);
            }
        }));
        this.trafficGrid.setColumnOrder("date", "customer", "uplink", "downlink");
        this.trafficGrid.setVisible(false);

        this.trafficLabel = new Label();
        this.channelCapacityLabel = new Label();

        this.resultLayout = new VerticalLayout(new HorizontalLayout(
                new Label("Traffic: "), trafficLabel, new Label(" byte")),
                new HorizontalLayout(new Label("Capacity: "), channelCapacityLabel, new Label(" bps"))
        );
        this.resultLayout.setVisible(false);

        return new VerticalLayout(new HorizontalLayout(startDateTimeField, finishDateTimeField),
                new HorizontalLayout(group, searchButton),
                trafficGrid,
                resultLayout
        );
    }

    private void initialListeners() {
        this.customersGridPagination.addPageChangeListener(event -> presenter.customersFilterEvent(this.customerFilterField.getValue(),
                new PageRequest(event.pageIndex(), LIMIT_ROWS)));
        this.customerFilterField.addValueChangeListener(e -> presenter.customersFilterEvent(this.customerFilterField.getValue(),
                new PageRequest(0, LIMIT_ROWS)));
        this.customerFilterButton.addClickListener(e -> presenter.clearCustomerFilterEvent());
        this.searchButton.addClickListener(e -> presenter.searchEvent(getFiltersForm()));
    }

    private Pagination createPagination(long total, int page, int limit) {
        final PaginationResource paginationResource = PaginationResource.newBuilder()
                .setTotal(total)
                .setPage(page)
                .setLimit(limit)
                .build();
        final Pagination pagination = new Pagination(paginationResource);
        pagination.setItemsPerPageEnabled(false);
        pagination.setItemsPerPageVisible(false);
        return pagination;
    }

    private FiltersForm getFiltersForm() {
        Optional selected = ((SingleSelectionModel) customerGrid.getSelectionModel()).getSelectedItem();
        Long id = selected.isPresent() ? ((CustomerDTO) selected.get()).getId() : null;
        Date start = null;
        Date finish = null;
        if (startDateTimeField.getValue() != null) {
            start = Date.from(startDateTimeField.getValue().atZone(ZoneId.systemDefault()).toInstant());
        }
        if (finishDateTimeField.getValue() != null) {
            finish = Date.from(finishDateTimeField.getValue().atZone(ZoneId.systemDefault()).toInstant());
        }
        return new FiltersForm(id, start, finish, this.group.getValue(), !this.group.getValue());
    }


    @Override
    public void showCustomersList(Page<CustomerDTO> customers) {
        this.customerGrid.setItems(customers.getContent());
        this.customersGridPagination.setTotalCount(customers.getTotalElements());
        this.customersGridPagination.setCurrentPage(customers.getNumber() + 1);
    }

    @Override
    public void clearCustomerFilter() {
        this.customerFilterField.clear();
        this.customersGridPagination.setCurrentPage(1);
    }

    @Override
    public void showTrafficList(Collection<TrafficDTO> traffics) {
        this.trafficGrid.setItems(traffics);
        this.trafficGrid.setVisible(true);
    }

    @Override
    public void showTraffic(Long traffic) {
        resultLayout.setVisible(true);
        trafficLabel.setValue(String.valueOf(traffic));
    }

    @Override
    public void showChanelCapacity(Long capacity) {
        resultLayout.setVisible(true);
        channelCapacityLabel.setValue(String.valueOf(capacity));
    }
}