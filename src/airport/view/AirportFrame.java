/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package airport.view;

import airport.utils.observer.Observer;

import airport.controller.FlightController;
import airport.controller.LocationController;
import airport.controller.PassengerController;
import airport.controller.PlaneController;
import airport.controller.factory.service.FlightFactoryService;
import airport.controller.factory.service.LocationFactoryService;
import airport.controller.factory.service.PassengerFactoryService;
import airport.controller.factory.service.PlaneFactoryService;
import airport.controller.interfaces.IFlightFactory;
import airport.controller.interfaces.IFlightRepository;
import airport.controller.interfaces.IFlightValidator;
import airport.controller.interfaces.ILocationFactory;
import airport.controller.interfaces.ILocationRepository;
import airport.controller.interfaces.ILocationValidator;
import airport.controller.interfaces.IPassengerFactory;
import airport.controller.interfaces.IPassengerRepository;
import airport.controller.interfaces.IPassengerValidator;
import airport.controller.interfaces.IPlaneFactory;
import airport.controller.interfaces.IPlaneRepository;
import airport.controller.interfaces.IPlaneValidator;
import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.controller.validation.service.FlightValidatorService;
import airport.controller.validation.service.LocationValidatorService;
import airport.controller.validation.service.PassengerValidatorService;
import airport.controller.validation.service.PlaneValidatorService;
import airport.model.Location;
import airport.model.Plane;
import airport.model.Flight;
import airport.model.Passenger;
import airport.model.storage.StorageFlight;
import airport.model.storage.StorageLocation;
import airport.model.storage.StoragePassenger;
import airport.model.storage.StoragePlane;
import airport.utils.observer.Subject;
import java.awt.Color;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author edangulo
 */
public class AirportFrame extends javax.swing.JFrame implements Observer {

    /**
     * Creates new form AirportFrame
     */
    private int x, y;
    private final LocationController locationController;
    private final PassengerController passengerController;
    private final PlaneController planeController;
    private final FlightController flightController;

    // Repositories are also subjects now
    private final ILocationRepository locationRepository;
    private final IPassengerRepository passengerRepository;
    private final IPlaneRepository planeRepository;
    private final IFlightRepository flightRepository;

    public AirportFrame() {
        initComponents();

        this.locationRepository = new StorageLocation(); //
        this.passengerRepository = new StoragePassenger(); //
        this.planeRepository = new StoragePlane(); //
        this.flightRepository = new StorageFlight(this.planeRepository, this.locationRepository); //

        ILocationValidator locationVal = new LocationValidatorService();
        IPassengerValidator passengerVal = new PassengerValidatorService();
        IPlaneValidator planeVal = new PlaneValidatorService();
        IFlightValidator flightVal = new FlightValidatorService();

        ILocationFactory locationFac = new LocationFactoryService();
        IPassengerFactory passengerFac = new PassengerFactoryService();
        IPlaneFactory planeFac = new PlaneFactoryService();
        IFlightFactory flightFac = new FlightFactoryService();

        this.locationController = new LocationController(this.locationRepository, locationVal, locationFac);
        this.passengerController = new PassengerController(this.passengerRepository, passengerVal, passengerFac);
        this.planeController = new PlaneController(this.planeRepository, planeVal, planeFac);
        this.flightController = new FlightController(
                this.flightRepository,
                flightVal,
                flightFac,
                this.planeRepository,
                this.locationRepository,
                this.passengerRepository
        );

        if (this.locationRepository instanceof Subject) {
            ((Subject) this.locationRepository).registerObserver(this);
        }
        if (this.passengerRepository instanceof Subject) {
            ((Subject) this.passengerRepository).registerObserver(this);
        }
        if (this.planeRepository instanceof Subject) {
            ((Subject) this.planeRepository).registerObserver(this);
        }
        if (this.flightRepository instanceof Subject) {
            ((Subject) this.flightRepository).registerObserver(this);
        }

        populateUserSelectComboBox();
        populateFlightPlaneComboBox();
        populateLocationComboBoxes();
        populateFlightSelectionComboBoxes();

        refreshAllTables();

        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);
        this.generateMonths();
        this.generateDays();
        this.generateHours();
        this.generateMinutes();
        this.blockPanels();
    }

    private void populateUserSelectComboBox() {
        UserSelectComboBox.removeAllItems();
        UserSelectComboBox.addItem("Select User");
        Response response = this.passengerController.getAllPassengersForTable();
        if (response.getStatus() == Status.OK) { //
            List<Passenger> passengerList = (List<Passenger>) response.getObject(); //
            if (passengerList != null) {
                for (Passenger passenger : passengerList) {
                    UserSelectComboBox.addItem(String.valueOf(passenger.getId()));
                }
            }
        }
    }

    private void populateFlightPlaneComboBox() {
        FlightPlaneComboBox.removeAllItems();
        FlightPlaneComboBox.addItem("Plane");
        Response response = this.planeController.getAllPlanesForTable();
        if (response.getStatus() == Status.OK) { //
            List<Plane> planeList = (List<Plane>) response.getObject(); //
            if (planeList != null) {
                for (Plane plane : planeList) {
                    FlightPlaneComboBox.addItem(plane.getId());
                }
            }
        }
    }

    private void populateLocationComboBoxes() {
        FlightDepartureLocationComboBox.removeAllItems();
        FlightArrivalLocationComboBox.removeAllItems();
        FlightScaleLocationComboBox.removeAllItems();

        FlightDepartureLocationComboBox.addItem("Location");
        FlightArrivalLocationComboBox.addItem("Location");
        FlightScaleLocationComboBox.addItem("Location");

        Response response = this.locationController.getAllLocationsForTable();
        if (response.getStatus() == Status.OK) { //
            List<Location> locationList = (List<Location>) response.getObject(); //
            if (locationList != null) {
                for (Location location : locationList) {
                    FlightDepartureLocationComboBox.addItem(location.getAirportId());
                    FlightArrivalLocationComboBox.addItem(location.getAirportId());
                    FlightScaleLocationComboBox.addItem(location.getAirportId());
                }
            }
        }
    }

    private void populateFlightSelectionComboBoxes() {
        AddToFlightFlightComboBox.removeAllItems();
        DelayFlightIDComboBox.removeAllItems();

        AddToFlightFlightComboBox.addItem("Flight");
        DelayFlightIDComboBox.addItem("ID");

        Response response = this.flightController.getAllFlightsForTable();
        if (response.getStatus() == Status.OK) { //
            List<Flight> flightList = (List<Flight>) response.getObject(); //
            if (flightList != null) {
                for (Flight flight : flightList) {
                    AddToFlightFlightComboBox.addItem(flight.getId());
                    DelayFlightIDComboBox.addItem(flight.getId());
                }
            }
        }
    }

    @Override
    public void update() {
        refreshAllTables();
    }

    private void refreshAllTables() {
        ShowAllPassengersRefreshButtonActionPerformed(null);
        ShowAllPlanesRefreshButtonActionPerformed(null);
        ShowAllLocationsRefreshButtonActionPerformed(null);
        ShowAllFlightsRefreshButtonActionPerformed(null);
        if (UserSelectComboBox.getSelectedIndex() > 0 && UserSelectComboBox.getSelectedItem() != null && !UserSelectComboBox.getSelectedItem().toString().equals("Select User")) {
            ShowMyFlightsRefreshButtonActionPerformed(null);
        } else {
            DefaultTableModel myFlightsModel = (DefaultTableModel) ShowMyFlightsTable.getModel();
            myFlightsModel.setRowCount(0);
        }

    }

    private void blockPanels() {
        //9, 11
        for (int i = 1; i < MenuTabbedPane.getTabCount(); i++) {
            if (i != 9 && i != 11) {
                MenuTabbedPane.setEnabledAt(i, false);
            }
        }
    }

    private void generateMonths() {
        for (int i = 1; i < 13; i++) {
            PassengerMonthComboBox.addItem("" + i);
            FlightDepartureDateMonthComboBox.addItem("" + i);
            UpdateInfoBirthdateMonthComboBox.addItem("" + i);
        }
    }

    private void generateDays() {
        for (int i = 1; i < 32; i++) {
            PassengerDayComboBox.addItem("" + i);
            FlightDepartureDateDayComboBox.addItem("" + i);
            UpdateInfoBirthdateDayComboBox.addItem("" + i);
        }
    }

    private void generateHours() {
        for (int i = 0; i < 24; i++) {
            FlightDepartureDateHourComboBox.addItem("" + i);
            FlightArrivalHourComboBox.addItem("" + i);
            FlightScaleHourComboBox.addItem("" + i);
            DelayFlightHourComboBox.addItem("" + i);
        }
    }

    private void generateMinutes() {
        for (int i = 0; i < 60; i++) {
            FlightDepartureDateMinuteComboBox.addItem("" + i);
            FlightArrivalMinuteComboBox.addItem("" + i);
            FlightScaleMinuteComboBox.addItem("" + i);
            DelayFlightMinuteComboBox.addItem("" + i);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainPanelRound = new airport.view.utils.PanelRound();
        MenuPanelRound = new airport.view.utils.PanelRound();
        ExitButton = new javax.swing.JButton();
        MenuTabbedPane = new javax.swing.JTabbedPane();
        AdminPanel = new javax.swing.JPanel();
        UserRadioButton = new javax.swing.JRadioButton();
        AdministratorRadioButton = new javax.swing.JRadioButton();
        UserSelectComboBox = new javax.swing.JComboBox<>();
        PassangerRegPanel = new javax.swing.JPanel();
        PassengerCountryLabel = new javax.swing.JLabel();
        PassengerIDLabel = new javax.swing.JLabel();
        PassengerFirstNameLabel = new javax.swing.JLabel();
        PassengerLastNameLabel = new javax.swing.JLabel();
        PassengerBirthdateLabel = new javax.swing.JLabel();
        PassengerPhonePrefixPlusLabel = new javax.swing.JLabel();
        PassengerPhonePrefixTextField = new javax.swing.JTextField();
        PassengerIDTextField = new javax.swing.JTextField();
        PassengerBirthdateYearTextField = new javax.swing.JTextField();
        PassengerCountryTextField = new javax.swing.JTextField();
        PassengerPhoneTextField = new javax.swing.JTextField();
        PassengerPhoneLabel = new javax.swing.JLabel();
        PassengerBirthdateSeparator1Label = new javax.swing.JLabel();
        PassengerLastNameTextField = new javax.swing.JTextField();
        PassengerPhoneSeparatorLabel = new javax.swing.JLabel();
        PassengerMonthComboBox = new javax.swing.JComboBox<>();
        PassengerFirstNameTextField = new javax.swing.JTextField();
        PassengerBirthdateSeparator2Label = new javax.swing.JLabel();
        PassengerDayComboBox = new javax.swing.JComboBox<>();
        PassangerRegisterButton = new javax.swing.JButton();
        AirplaneRegPanel = new javax.swing.JPanel();
        AirplaneIDLabel = new javax.swing.JLabel();
        AirplaneIDTextField = new javax.swing.JTextField();
        AirplaneBrandLabel = new javax.swing.JLabel();
        AirplaneBrandTextField = new javax.swing.JTextField();
        AirplaneModelTextField = new javax.swing.JTextField();
        AirplaneModelLabel = new javax.swing.JLabel();
        AirplaneMaxCapacityTextField = new javax.swing.JTextField();
        AirplaneMaxCapacityLabel = new javax.swing.JLabel();
        AirplaneAirlineTextField = new javax.swing.JTextField();
        AirplaneAirlineLabel = new javax.swing.JLabel();
        AirplaneCreateButton = new javax.swing.JButton();
        LocationRegPanel = new javax.swing.JPanel();
        LocationAirportIDLabel = new javax.swing.JLabel();
        LocationAirportIDTextField = new javax.swing.JTextField();
        LocationAirportNameLabel = new javax.swing.JLabel();
        LocationAirportNameTextField = new javax.swing.JTextField();
        LocationAirportCityTextField = new javax.swing.JTextField();
        LocationAirportCityLabel = new javax.swing.JLabel();
        LocationAirportCountryLabel = new javax.swing.JLabel();
        LocationAirportCountryTextField = new javax.swing.JTextField();
        LocationAirportLatitudeTextField = new javax.swing.JTextField();
        LocationAirportLatitudeLabel = new javax.swing.JLabel();
        LocationAirportLongitudeLabel = new javax.swing.JLabel();
        LocationAirportLongitudeTextField = new javax.swing.JTextField();
        LocationAirportCreateButton = new javax.swing.JButton();
        FlightRegPanel = new javax.swing.JPanel();
        FlightIDLabel = new javax.swing.JLabel();
        FlightIDTextField = new javax.swing.JTextField();
        FlightPlaneLabel = new javax.swing.JLabel();
        FlightPlaneComboBox = new javax.swing.JComboBox<>();
        FlightDepartureLocationComboBox = new javax.swing.JComboBox<>();
        FlightDepartureLocationLabel = new javax.swing.JLabel();
        FlightArrivalLocationComboBox = new javax.swing.JComboBox<>();
        FlightArrivalLocationLabel = new javax.swing.JLabel();
        FlightScaleLocationLabel = new javax.swing.JLabel();
        FlightScaleLocationComboBox = new javax.swing.JComboBox<>();
        FlightScaleDurationLabel = new javax.swing.JLabel();
        FlightArrivalDurationLabel = new javax.swing.JLabel();
        FlightDepartureDateLabel = new javax.swing.JLabel();
        FlightDepartureDateYearTextField = new javax.swing.JTextField();
        FlightDepartureDateSeparator1Label = new javax.swing.JLabel();
        FlightDepartureDateMonthComboBox = new javax.swing.JComboBox<>();
        FlightDepartureDateSeparator2Label = new javax.swing.JLabel();
        FlightDepartureDateDayComboBox = new javax.swing.JComboBox<>();
        FlightDepartureDateSeparator3Label = new javax.swing.JLabel();
        FlightDepartureDateHourComboBox = new javax.swing.JComboBox<>();
        FlightDepartureDateSeparator4Label = new javax.swing.JLabel();
        FlightDepartureDateMinuteComboBox = new javax.swing.JComboBox<>();
        FlightArrivalHourComboBox = new javax.swing.JComboBox<>();
        FlightArrivalSeparatorLabel = new javax.swing.JLabel();
        FlightArrivalMinuteComboBox = new javax.swing.JComboBox<>();
        FlightScaleSeparatorLabel = new javax.swing.JLabel();
        FlightScaleHourComboBox = new javax.swing.JComboBox<>();
        FlightScaleMinuteComboBox = new javax.swing.JComboBox<>();
        FlightCreateButton = new javax.swing.JButton();
        UpdateInfoPanel = new javax.swing.JPanel();
        UpdateInfoIDLabel = new javax.swing.JLabel();
        UpdateInfoIDTextField = new javax.swing.JTextField();
        UpdateInfoFirstNameLabel = new javax.swing.JLabel();
        UpdateInfoFirstNameTextField = new javax.swing.JTextField();
        UpdateInfoLastNameLabel = new javax.swing.JLabel();
        UpdateInfoLastNameTextField = new javax.swing.JTextField();
        UpdateInfoBirthdateLabel = new javax.swing.JLabel();
        UpdateInfoBirthdateYearTextField = new javax.swing.JTextField();
        UpdateInfoBirthdateMonthComboBox = new javax.swing.JComboBox<>();
        UpdateInfoBirthdateDayComboBox = new javax.swing.JComboBox<>();
        UpdateInfoPhoneTextField = new javax.swing.JTextField();
        UpdateInfoPhoneSeparatorLabel = new javax.swing.JLabel();
        UpdateInfoPhonePrefixTextField = new javax.swing.JTextField();
        UpdateInfoPhonePrefixLabel = new javax.swing.JLabel();
        UpdateInfoPhoneLabel = new javax.swing.JLabel();
        UpdateInfoCountryLabel = new javax.swing.JLabel();
        UpdateInfoCountryTextField = new javax.swing.JTextField();
        UpdateInfoUpdateButton = new javax.swing.JButton();
        AddToFlightPanel = new javax.swing.JPanel();
        AddToFlightIDTextField = new javax.swing.JTextField();
        AddToFlightIDLabel = new javax.swing.JLabel();
        AddToFlightFlightLabel = new javax.swing.JLabel();
        AddToFlightFlightComboBox = new javax.swing.JComboBox<>();
        AddToFlightAddButton = new javax.swing.JButton();
        ShowMyFlightsPanel = new javax.swing.JPanel();
        ShowMyFlightsScrollPane = new javax.swing.JScrollPane();
        ShowMyFlightsTable = new javax.swing.JTable();
        ShowMyFlightsRefreshButton = new javax.swing.JButton();
        ShowAllPassengersPanel = new javax.swing.JPanel();
        ShowAllPassengersScrollPane = new javax.swing.JScrollPane();
        ShowAllPassengersTable = new javax.swing.JTable();
        ShowAllPassengersRefreshButton = new javax.swing.JButton();
        ShowAllFlightsPanel = new javax.swing.JPanel();
        ShowAllFlightsScrollPane = new javax.swing.JScrollPane();
        ShowAllFlightsTable = new javax.swing.JTable();
        ShowAllFlightsRefreshButton = new javax.swing.JButton();
        ShowAllPlanesPanel = new javax.swing.JPanel();
        ShowAllPlanesRefreshButton = new javax.swing.JButton();
        ShowAllPlanesScrollPane = new javax.swing.JScrollPane();
        ShowAllPlanesTable = new javax.swing.JTable();
        ShowAllLocationsPanel = new javax.swing.JPanel();
        ShowAllLocationsScrollPane = new javax.swing.JScrollPane();
        ShowAllLocationsTable = new javax.swing.JTable();
        ShowAllLocationsRefreshButton = new javax.swing.JButton();
        DelayFlightPanel = new javax.swing.JPanel();
        DelayFlightHourComboBox = new javax.swing.JComboBox<>();
        DelayFlightHourLabel = new javax.swing.JLabel();
        DelayFlightIDLabel = new javax.swing.JLabel();
        DelayFlightIDComboBox = new javax.swing.JComboBox<>();
        DelayFlightMinuteLabel = new javax.swing.JLabel();
        DelayFlightMinuteComboBox = new javax.swing.JComboBox<>();
        DelayFlightDelayButton = new javax.swing.JButton();
        BottomPanelRound = new airport.view.utils.PanelRound();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        MainPanelRound.setRadius(40);
        MainPanelRound.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        MenuPanelRound.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                MenuPanelRoundMouseDragged(evt);
            }
        });
        MenuPanelRound.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MenuPanelRoundMousePressed(evt);
            }
        });

        ExitButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ExitButton.setText("X");
        ExitButton.setBorderPainted(false);
        ExitButton.setContentAreaFilled(false);
        ExitButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuPanelRoundLayout = new javax.swing.GroupLayout(MenuPanelRound);
        MenuPanelRound.setLayout(MenuPanelRoundLayout);
        MenuPanelRoundLayout.setHorizontalGroup(
            MenuPanelRoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MenuPanelRoundLayout.createSequentialGroup()
                .addContainerGap(1083, Short.MAX_VALUE)
                .addComponent(ExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        MenuPanelRoundLayout.setVerticalGroup(
            MenuPanelRoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuPanelRoundLayout.createSequentialGroup()
                .addComponent(ExitButton)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        MainPanelRound.add(MenuPanelRound, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1150, -1));

        MenuTabbedPane.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N

        AdminPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        UserRadioButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UserRadioButton.setText("User");
        UserRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserRadioButtonActionPerformed(evt);
            }
        });
        AdminPanel.add(UserRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 230, -1, -1));

        AdministratorRadioButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AdministratorRadioButton.setText("Administrator");
        AdministratorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdministratorRadioButtonActionPerformed(evt);
            }
        });
        AdminPanel.add(AdministratorRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 164, -1, -1));

        UserSelectComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UserSelectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select User" }));
        UserSelectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserSelectComboBoxActionPerformed(evt);
            }
        });
        AdminPanel.add(UserSelectComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 300, 130, -1));

        MenuTabbedPane.addTab("Administration", AdminPanel);

        PassangerRegPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PassengerCountryLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerCountryLabel.setText("Country:");
        PassangerRegPanel.add(PassengerCountryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, -1, -1));

        PassengerIDLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerIDLabel.setText("ID:");
        PassangerRegPanel.add(PassengerIDLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, -1, -1));

        PassengerFirstNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerFirstNameLabel.setText("First Name:");
        PassangerRegPanel.add(PassengerFirstNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, -1, -1));

        PassengerLastNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerLastNameLabel.setText("Last Name:");
        PassangerRegPanel.add(PassengerLastNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 220, -1, -1));

        PassengerBirthdateLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerBirthdateLabel.setText("Birthdate:");
        PassangerRegPanel.add(PassengerBirthdateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, -1, -1));

        PassengerPhonePrefixPlusLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerPhonePrefixPlusLabel.setText("+");
        PassangerRegPanel.add(PassengerPhonePrefixPlusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 340, 20, -1));

        PassengerPhonePrefixTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassangerRegPanel.add(PassengerPhonePrefixTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 340, 50, -1));

        PassengerIDTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassangerRegPanel.add(PassengerIDTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 130, -1));

        PassengerBirthdateYearTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassangerRegPanel.add(PassengerBirthdateYearTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 280, 90, -1));

        PassengerCountryTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassangerRegPanel.add(PassengerCountryTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 400, 130, -1));

        PassengerPhoneTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassangerRegPanel.add(PassengerPhoneTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 340, 130, -1));

        PassengerPhoneLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerPhoneLabel.setText("Phone:");
        PassangerRegPanel.add(PassengerPhoneLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, -1, -1));

        PassengerBirthdateSeparator1Label.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerBirthdateSeparator1Label.setText("-");
        PassangerRegPanel.add(PassengerBirthdateSeparator1Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 280, 30, -1));

        PassengerLastNameTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassangerRegPanel.add(PassengerLastNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 130, -1));

        PassengerPhoneSeparatorLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerPhoneSeparatorLabel.setText("-");
        PassangerRegPanel.add(PassengerPhoneSeparatorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 340, 30, -1));

        PassengerMonthComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerMonthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month" }));
        PassangerRegPanel.add(PassengerMonthComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 280, -1, -1));

        PassengerFirstNameTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassangerRegPanel.add(PassengerFirstNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 160, 130, -1));

        PassengerBirthdateSeparator2Label.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerBirthdateSeparator2Label.setText("-");
        PassangerRegPanel.add(PassengerBirthdateSeparator2Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 280, 30, -1));

        PassengerDayComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassengerDayComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day" }));
        PassangerRegPanel.add(PassengerDayComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 280, -1, -1));

        PassangerRegisterButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        PassangerRegisterButton.setText("Register");
        PassangerRegisterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PassangerRegisterButtonActionPerformed(evt);
            }
        });
        PassangerRegPanel.add(PassangerRegisterButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 480, -1, -1));

        MenuTabbedPane.addTab("Passenger registration", PassangerRegPanel);

        AirplaneRegPanel.setLayout(null);

        AirplaneIDLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneIDLabel.setText("ID:");
        AirplaneRegPanel.add(AirplaneIDLabel);
        AirplaneIDLabel.setBounds(53, 96, 22, 25);

        AirplaneIDTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneRegPanel.add(AirplaneIDTextField);
        AirplaneIDTextField.setBounds(180, 93, 130, 31);

        AirplaneBrandLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneBrandLabel.setText("Brand:");
        AirplaneRegPanel.add(AirplaneBrandLabel);
        AirplaneBrandLabel.setBounds(53, 157, 50, 25);

        AirplaneBrandTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneRegPanel.add(AirplaneBrandTextField);
        AirplaneBrandTextField.setBounds(180, 154, 130, 31);

        AirplaneModelTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneRegPanel.add(AirplaneModelTextField);
        AirplaneModelTextField.setBounds(180, 213, 130, 31);

        AirplaneModelLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneModelLabel.setText("Model:");
        AirplaneRegPanel.add(AirplaneModelLabel);
        AirplaneModelLabel.setBounds(53, 216, 55, 25);

        AirplaneMaxCapacityTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneRegPanel.add(AirplaneMaxCapacityTextField);
        AirplaneMaxCapacityTextField.setBounds(180, 273, 130, 31);

        AirplaneMaxCapacityLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneMaxCapacityLabel.setText("Max Capacity:");
        AirplaneRegPanel.add(AirplaneMaxCapacityLabel);
        AirplaneMaxCapacityLabel.setBounds(53, 276, 109, 25);

        AirplaneAirlineTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneRegPanel.add(AirplaneAirlineTextField);
        AirplaneAirlineTextField.setBounds(180, 333, 130, 31);

        AirplaneAirlineLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneAirlineLabel.setText("Airline:");
        AirplaneRegPanel.add(AirplaneAirlineLabel);
        AirplaneAirlineLabel.setBounds(53, 336, 70, 25);

        AirplaneCreateButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AirplaneCreateButton.setText("Create");
        AirplaneCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AirplaneCreateButtonActionPerformed(evt);
            }
        });
        AirplaneRegPanel.add(AirplaneCreateButton);
        AirplaneCreateButton.setBounds(490, 480, 120, 40);

        MenuTabbedPane.addTab("Airplane registration", AirplaneRegPanel);

        LocationAirportIDLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        LocationAirportIDLabel.setText("Airport ID:");

        LocationAirportIDTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        LocationAirportNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        LocationAirportNameLabel.setText("Airport name:");

        LocationAirportNameTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        LocationAirportCityTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        LocationAirportCityLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        LocationAirportCityLabel.setText("Airport city:");

        LocationAirportCountryLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        LocationAirportCountryLabel.setText("Airport country:");

        LocationAirportCountryTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        LocationAirportLatitudeTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        LocationAirportLatitudeLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        LocationAirportLatitudeLabel.setText("Airport latitude:");

        LocationAirportLongitudeLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        LocationAirportLongitudeLabel.setText("Airport longitude:");

        LocationAirportLongitudeTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        LocationAirportCreateButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        LocationAirportCreateButton.setText("Create");
        LocationAirportCreateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LocationAirportCreateButtonMouseClicked(evt);
            }
        });
        LocationAirportCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LocationAirportCreateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout LocationRegPanelLayout = new javax.swing.GroupLayout(LocationRegPanel);
        LocationRegPanel.setLayout(LocationRegPanelLayout);
        LocationRegPanelLayout.setHorizontalGroup(
            LocationRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LocationRegPanelLayout.createSequentialGroup()
                .addGroup(LocationRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LocationRegPanelLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(LocationRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LocationAirportIDLabel)
                            .addComponent(LocationAirportNameLabel)
                            .addComponent(LocationAirportCityLabel)
                            .addComponent(LocationAirportCountryLabel)
                            .addComponent(LocationAirportLatitudeLabel)
                            .addComponent(LocationAirportLongitudeLabel))
                        .addGap(80, 80, 80)
                        .addGroup(LocationRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LocationAirportLongitudeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LocationAirportIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LocationAirportNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LocationAirportCityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LocationAirportCountryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LocationAirportLatitudeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(LocationRegPanelLayout.createSequentialGroup()
                        .addGap(515, 515, 515)
                        .addComponent(LocationAirportCreateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(515, 515, 515))
        );
        LocationRegPanelLayout.setVerticalGroup(
            LocationRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LocationRegPanelLayout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(LocationRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(LocationRegPanelLayout.createSequentialGroup()
                        .addComponent(LocationAirportIDLabel)
                        .addGap(36, 36, 36)
                        .addComponent(LocationAirportNameLabel)
                        .addGap(34, 34, 34)
                        .addComponent(LocationAirportCityLabel)
                        .addGap(35, 35, 35)
                        .addComponent(LocationAirportCountryLabel)
                        .addGap(35, 35, 35)
                        .addComponent(LocationAirportLatitudeLabel))
                    .addGroup(LocationRegPanelLayout.createSequentialGroup()
                        .addComponent(LocationAirportIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(LocationAirportNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(LocationAirportCityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(LocationAirportCountryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(LocationAirportLatitudeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(44, 44, 44)
                .addGroup(LocationRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LocationAirportLongitudeLabel)
                    .addComponent(LocationAirportLongitudeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(LocationAirportCreateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        MenuTabbedPane.addTab("Location registration", LocationRegPanel);

        FlightIDLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightIDLabel.setText("ID:");

        FlightIDTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        FlightPlaneLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightPlaneLabel.setText("Plane:");

        FlightPlaneComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightPlaneComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Plane" }));

        FlightDepartureLocationComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureLocationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Location" }));

        FlightDepartureLocationLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureLocationLabel.setText("Departure location:");

        FlightArrivalLocationComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightArrivalLocationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Location" }));

        FlightArrivalLocationLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightArrivalLocationLabel.setText("Arrival location:");

        FlightScaleLocationLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightScaleLocationLabel.setText("Scale location:");

        FlightScaleLocationComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightScaleLocationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Location" }));

        FlightScaleDurationLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightScaleDurationLabel.setText("Duration:");

        FlightArrivalDurationLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightArrivalDurationLabel.setText("Duration:");

        FlightDepartureDateLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureDateLabel.setText("Departure date:");

        FlightDepartureDateYearTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        FlightDepartureDateSeparator1Label.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureDateSeparator1Label.setText("-");

        FlightDepartureDateMonthComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureDateMonthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month" }));

        FlightDepartureDateSeparator2Label.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureDateSeparator2Label.setText("-");

        FlightDepartureDateDayComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureDateDayComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day" }));

        FlightDepartureDateSeparator3Label.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureDateSeparator3Label.setText("-");

        FlightDepartureDateHourComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureDateHourComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        FlightDepartureDateSeparator4Label.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureDateSeparator4Label.setText("-");

        FlightDepartureDateMinuteComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightDepartureDateMinuteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        FlightArrivalHourComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightArrivalHourComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        FlightArrivalSeparatorLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightArrivalSeparatorLabel.setText("-");

        FlightArrivalMinuteComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightArrivalMinuteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        FlightScaleSeparatorLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightScaleSeparatorLabel.setText("-");

        FlightScaleHourComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightScaleHourComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        FlightScaleMinuteComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightScaleMinuteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        FlightCreateButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        FlightCreateButton.setText("Create");
        FlightCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FlightCreateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout FlightRegPanelLayout = new javax.swing.GroupLayout(FlightRegPanel);
        FlightRegPanel.setLayout(FlightRegPanelLayout);
        FlightRegPanelLayout.setHorizontalGroup(
            FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FlightRegPanelLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FlightRegPanelLayout.createSequentialGroup()
                        .addComponent(FlightScaleLocationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(FlightScaleLocationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FlightRegPanelLayout.createSequentialGroup()
                        .addComponent(FlightArrivalLocationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(FlightArrivalLocationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FlightRegPanelLayout.createSequentialGroup()
                        .addComponent(FlightDepartureLocationLabel)
                        .addGap(46, 46, 46)
                        .addComponent(FlightDepartureLocationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FlightRegPanelLayout.createSequentialGroup()
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FlightIDLabel)
                            .addComponent(FlightPlaneLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(FlightIDTextField)
                            .addComponent(FlightPlaneComboBox, 0, 130, Short.MAX_VALUE))))
                .addGap(45, 45, 45)
                .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(FlightScaleDurationLabel)
                    .addComponent(FlightArrivalDurationLabel)
                    .addComponent(FlightDepartureDateLabel))
                .addGap(18, 18, 18)
                .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FlightRegPanelLayout.createSequentialGroup()
                        .addComponent(FlightDepartureDateYearTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FlightRegPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(FlightDepartureDateMonthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(FlightDepartureDateSeparator1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FlightDepartureDateSeparator2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(FlightRegPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(FlightDepartureDateDayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FlightRegPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(FlightDepartureDateHourComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(FlightDepartureDateSeparator3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FlightDepartureDateSeparator4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(FlightRegPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(FlightDepartureDateMinuteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(30, 30, 30))
                    .addGroup(FlightRegPanelLayout.createSequentialGroup()
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FlightRegPanelLayout.createSequentialGroup()
                                .addComponent(FlightArrivalHourComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(FlightArrivalSeparatorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(FlightRegPanelLayout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(FlightArrivalMinuteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(FlightRegPanelLayout.createSequentialGroup()
                                .addComponent(FlightScaleHourComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(FlightScaleSeparatorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(FlightRegPanelLayout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(FlightScaleMinuteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FlightRegPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(FlightCreateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(530, 530, 530))
        );
        FlightRegPanelLayout.setVerticalGroup(
            FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FlightRegPanelLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FlightRegPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(FlightIDLabel))
                    .addComponent(FlightIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FlightPlaneLabel)
                    .addComponent(FlightPlaneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(FlightDepartureDateHourComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FlightDepartureDateSeparator3Label)
                    .addComponent(FlightDepartureDateSeparator4Label)
                    .addComponent(FlightDepartureDateMinuteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(FlightRegPanelLayout.createSequentialGroup()
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(FlightDepartureLocationLabel)
                                .addComponent(FlightDepartureLocationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(FlightDepartureDateLabel))
                            .addComponent(FlightDepartureDateYearTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FlightDepartureDateMonthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FlightDepartureDateSeparator1Label)
                            .addComponent(FlightDepartureDateSeparator2Label)
                            .addComponent(FlightDepartureDateDayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(FlightArrivalLocationLabel)
                                .addComponent(FlightArrivalLocationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(FlightArrivalDurationLabel))
                            .addComponent(FlightArrivalHourComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FlightArrivalSeparatorLabel)
                            .addComponent(FlightArrivalMinuteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FlightScaleHourComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FlightScaleSeparatorLabel)
                            .addComponent(FlightScaleMinuteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(FlightRegPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(FlightScaleLocationLabel)
                                .addComponent(FlightScaleLocationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(FlightScaleDurationLabel)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
                .addComponent(FlightCreateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        MenuTabbedPane.addTab("Flight registration", FlightRegPanel);

        UpdateInfoIDLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoIDLabel.setText("ID:");

        UpdateInfoIDTextField.setEditable(false);
        UpdateInfoIDTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoIDTextField.setEnabled(false);

        UpdateInfoFirstNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoFirstNameLabel.setText("First Name:");

        UpdateInfoFirstNameTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        UpdateInfoLastNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoLastNameLabel.setText("Last Name:");

        UpdateInfoLastNameTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        UpdateInfoBirthdateLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoBirthdateLabel.setText("Birthdate:");

        UpdateInfoBirthdateYearTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        UpdateInfoBirthdateMonthComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoBirthdateMonthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month" }));

        UpdateInfoBirthdateDayComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoBirthdateDayComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day" }));

        UpdateInfoPhoneTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        UpdateInfoPhoneSeparatorLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoPhoneSeparatorLabel.setText("-");

        UpdateInfoPhonePrefixTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        UpdateInfoPhonePrefixLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoPhonePrefixLabel.setText("+");

        UpdateInfoPhoneLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoPhoneLabel.setText("Phone:");

        UpdateInfoCountryLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoCountryLabel.setText("Country:");

        UpdateInfoCountryTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        UpdateInfoUpdateButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        UpdateInfoUpdateButton.setText("Update");
        UpdateInfoUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateInfoUpdateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout UpdateInfoPanelLayout = new javax.swing.GroupLayout(UpdateInfoPanel);
        UpdateInfoPanel.setLayout(UpdateInfoPanelLayout);
        UpdateInfoPanelLayout.setHorizontalGroup(
            UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                .addGroup(UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addGroup(UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                                .addComponent(UpdateInfoIDLabel)
                                .addGap(108, 108, 108)
                                .addComponent(UpdateInfoIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                                .addComponent(UpdateInfoFirstNameLabel)
                                .addGap(41, 41, 41)
                                .addComponent(UpdateInfoFirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                                .addComponent(UpdateInfoLastNameLabel)
                                .addGap(43, 43, 43)
                                .addComponent(UpdateInfoLastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                                .addComponent(UpdateInfoBirthdateLabel)
                                .addGap(55, 55, 55)
                                .addComponent(UpdateInfoBirthdateYearTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(UpdateInfoBirthdateMonthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(UpdateInfoBirthdateDayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                                .addComponent(UpdateInfoPhoneLabel)
                                .addGap(56, 56, 56)
                                .addComponent(UpdateInfoPhonePrefixLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(UpdateInfoPhonePrefixTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(UpdateInfoPhoneSeparatorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(UpdateInfoPhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                                .addComponent(UpdateInfoCountryLabel)
                                .addGap(63, 63, 63)
                                .addComponent(UpdateInfoCountryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                        .addGap(507, 507, 507)
                        .addComponent(UpdateInfoUpdateButton)))
                .addContainerGap(598, Short.MAX_VALUE))
        );
        UpdateInfoPanelLayout.setVerticalGroup(
            UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateInfoPanelLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UpdateInfoIDLabel)
                    .addComponent(UpdateInfoIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UpdateInfoFirstNameLabel)
                    .addComponent(UpdateInfoFirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UpdateInfoLastNameLabel)
                    .addComponent(UpdateInfoLastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UpdateInfoBirthdateLabel)
                    .addComponent(UpdateInfoBirthdateYearTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UpdateInfoBirthdateMonthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UpdateInfoBirthdateDayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UpdateInfoPhoneLabel)
                    .addComponent(UpdateInfoPhonePrefixLabel)
                    .addComponent(UpdateInfoPhonePrefixTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UpdateInfoPhoneSeparatorLabel)
                    .addComponent(UpdateInfoPhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(UpdateInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UpdateInfoCountryLabel)
                    .addComponent(UpdateInfoCountryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(UpdateInfoUpdateButton)
                .addGap(113, 113, 113))
        );

        MenuTabbedPane.addTab("Update info", UpdateInfoPanel);

        AddToFlightIDTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AddToFlightIDTextField.setEnabled(false);

        AddToFlightIDLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AddToFlightIDLabel.setText("ID:");

        AddToFlightFlightLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AddToFlightFlightLabel.setText("Flight:");

        AddToFlightFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AddToFlightFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Flight" }));

        AddToFlightAddButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        AddToFlightAddButton.setText("Add");
        AddToFlightAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddToFlightAddButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AddToFlightPanelLayout = new javax.swing.GroupLayout(AddToFlightPanel);
        AddToFlightPanel.setLayout(AddToFlightPanelLayout);
        AddToFlightPanelLayout.setHorizontalGroup(
            AddToFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddToFlightPanelLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(AddToFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AddToFlightIDLabel)
                    .addComponent(AddToFlightFlightLabel))
                .addGap(79, 79, 79)
                .addGroup(AddToFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AddToFlightFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddToFlightIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(873, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddToFlightPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(AddToFlightAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(509, 509, 509))
        );
        AddToFlightPanelLayout.setVerticalGroup(
            AddToFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddToFlightPanelLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(AddToFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddToFlightPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(AddToFlightIDLabel))
                    .addComponent(AddToFlightIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(AddToFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddToFlightFlightLabel)
                    .addComponent(AddToFlightFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 288, Short.MAX_VALUE)
                .addComponent(AddToFlightAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
        );

        MenuTabbedPane.addTab("Add to flight", AddToFlightPanel);

        ShowMyFlightsTable.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ShowMyFlightsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Departure Date", "Arrival Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ShowMyFlightsScrollPane.setViewportView(ShowMyFlightsTable);

        ShowMyFlightsRefreshButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ShowMyFlightsRefreshButton.setText("Refresh");
        ShowMyFlightsRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowMyFlightsRefreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ShowMyFlightsPanelLayout = new javax.swing.GroupLayout(ShowMyFlightsPanel);
        ShowMyFlightsPanel.setLayout(ShowMyFlightsPanelLayout);
        ShowMyFlightsPanelLayout.setHorizontalGroup(
            ShowMyFlightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ShowMyFlightsPanelLayout.createSequentialGroup()
                .addGap(269, 269, 269)
                .addComponent(ShowMyFlightsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(337, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ShowMyFlightsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ShowMyFlightsRefreshButton)
                .addGap(527, 527, 527))
        );
        ShowMyFlightsPanelLayout.setVerticalGroup(
            ShowMyFlightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ShowMyFlightsPanelLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(ShowMyFlightsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(ShowMyFlightsRefreshButton)
                .addContainerGap())
        );

        MenuTabbedPane.addTab("Show my flights", ShowMyFlightsPanel);

        ShowAllPassengersTable.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ShowAllPassengersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Birthdate", "Age", "Phone", "Country", "Num Flight"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ShowAllPassengersScrollPane.setViewportView(ShowAllPassengersTable);

        ShowAllPassengersRefreshButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ShowAllPassengersRefreshButton.setText("Refresh");
        ShowAllPassengersRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowAllPassengersRefreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ShowAllPassengersPanelLayout = new javax.swing.GroupLayout(ShowAllPassengersPanel);
        ShowAllPassengersPanel.setLayout(ShowAllPassengersPanelLayout);
        ShowAllPassengersPanelLayout.setHorizontalGroup(
            ShowAllPassengersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ShowAllPassengersPanelLayout.createSequentialGroup()
                .addGroup(ShowAllPassengersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ShowAllPassengersPanelLayout.createSequentialGroup()
                        .addGap(489, 489, 489)
                        .addComponent(ShowAllPassengersRefreshButton))
                    .addGroup(ShowAllPassengersPanelLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(ShowAllPassengersScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1078, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        ShowAllPassengersPanelLayout.setVerticalGroup(
            ShowAllPassengersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ShowAllPassengersPanelLayout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(ShowAllPassengersScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ShowAllPassengersRefreshButton)
                .addContainerGap())
        );

        MenuTabbedPane.addTab("Show all passengers", ShowAllPassengersPanel);

        ShowAllFlightsTable.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ShowAllFlightsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Departure Airport ID", "Arrival Airport ID", "Scale Airport ID", "Departure Date", "Arrival Date", "Plane ID", "Number Passengers"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ShowAllFlightsScrollPane.setViewportView(ShowAllFlightsTable);

        ShowAllFlightsRefreshButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ShowAllFlightsRefreshButton.setText("Refresh");
        ShowAllFlightsRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowAllFlightsRefreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ShowAllFlightsPanelLayout = new javax.swing.GroupLayout(ShowAllFlightsPanel);
        ShowAllFlightsPanel.setLayout(ShowAllFlightsPanelLayout);
        ShowAllFlightsPanelLayout.setHorizontalGroup(
            ShowAllFlightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ShowAllFlightsPanelLayout.createSequentialGroup()
                .addGroup(ShowAllFlightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ShowAllFlightsPanelLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(ShowAllFlightsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ShowAllFlightsPanelLayout.createSequentialGroup()
                        .addGap(521, 521, 521)
                        .addComponent(ShowAllFlightsRefreshButton)))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        ShowAllFlightsPanelLayout.setVerticalGroup(
            ShowAllFlightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ShowAllFlightsPanelLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(ShowAllFlightsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ShowAllFlightsRefreshButton)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        MenuTabbedPane.addTab("Show all flights", ShowAllFlightsPanel);

        ShowAllPlanesRefreshButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ShowAllPlanesRefreshButton.setText("Refresh");
        ShowAllPlanesRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowAllPlanesRefreshButtonActionPerformed(evt);
            }
        });

        ShowAllPlanesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Brand", "Model", "Max Capacity", "Airline", "Number Flights"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ShowAllPlanesScrollPane.setViewportView(ShowAllPlanesTable);

        javax.swing.GroupLayout ShowAllPlanesPanelLayout = new javax.swing.GroupLayout(ShowAllPlanesPanel);
        ShowAllPlanesPanel.setLayout(ShowAllPlanesPanelLayout);
        ShowAllPlanesPanelLayout.setHorizontalGroup(
            ShowAllPlanesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ShowAllPlanesPanelLayout.createSequentialGroup()
                .addGroup(ShowAllPlanesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ShowAllPlanesPanelLayout.createSequentialGroup()
                        .addGap(508, 508, 508)
                        .addComponent(ShowAllPlanesRefreshButton))
                    .addGroup(ShowAllPlanesPanelLayout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(ShowAllPlanesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 816, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(235, Short.MAX_VALUE))
        );
        ShowAllPlanesPanelLayout.setVerticalGroup(
            ShowAllPlanesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ShowAllPlanesPanelLayout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addComponent(ShowAllPlanesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(ShowAllPlanesRefreshButton)
                .addGap(17, 17, 17))
        );

        MenuTabbedPane.addTab("Show all planes", ShowAllPlanesPanel);

        ShowAllLocationsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Airport ID", "Airport Name", "City", "Country"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ShowAllLocationsScrollPane.setViewportView(ShowAllLocationsTable);

        ShowAllLocationsRefreshButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ShowAllLocationsRefreshButton.setText("Refresh");
        ShowAllLocationsRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowAllLocationsRefreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ShowAllLocationsPanelLayout = new javax.swing.GroupLayout(ShowAllLocationsPanel);
        ShowAllLocationsPanel.setLayout(ShowAllLocationsPanelLayout);
        ShowAllLocationsPanelLayout.setHorizontalGroup(
            ShowAllLocationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ShowAllLocationsPanelLayout.createSequentialGroup()
                .addGroup(ShowAllLocationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ShowAllLocationsPanelLayout.createSequentialGroup()
                        .addGap(508, 508, 508)
                        .addComponent(ShowAllLocationsRefreshButton))
                    .addGroup(ShowAllLocationsPanelLayout.createSequentialGroup()
                        .addGap(226, 226, 226)
                        .addComponent(ShowAllLocationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 652, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(318, Short.MAX_VALUE))
        );
        ShowAllLocationsPanelLayout.setVerticalGroup(
            ShowAllLocationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ShowAllLocationsPanelLayout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addComponent(ShowAllLocationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(ShowAllLocationsRefreshButton)
                .addGap(17, 17, 17))
        );

        MenuTabbedPane.addTab("Show all locations", ShowAllLocationsPanel);

        DelayFlightHourComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        DelayFlightHourComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        DelayFlightHourLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        DelayFlightHourLabel.setText("Hours:");

        DelayFlightIDLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        DelayFlightIDLabel.setText("ID:");

        DelayFlightIDComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        DelayFlightIDComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID" }));

        DelayFlightMinuteLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        DelayFlightMinuteLabel.setText("Minutes:");

        DelayFlightMinuteComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        DelayFlightMinuteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        DelayFlightDelayButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        DelayFlightDelayButton.setText("Delay");
        DelayFlightDelayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DelayFlightDelayButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DelayFlightPanelLayout = new javax.swing.GroupLayout(DelayFlightPanel);
        DelayFlightPanel.setLayout(DelayFlightPanelLayout);
        DelayFlightPanelLayout.setHorizontalGroup(
            DelayFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DelayFlightPanelLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(DelayFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DelayFlightPanelLayout.createSequentialGroup()
                        .addComponent(DelayFlightMinuteLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DelayFlightMinuteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DelayFlightPanelLayout.createSequentialGroup()
                        .addGroup(DelayFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(DelayFlightIDLabel)
                            .addComponent(DelayFlightHourLabel))
                        .addGap(79, 79, 79)
                        .addGroup(DelayFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(DelayFlightHourComboBox, 0, 151, Short.MAX_VALUE)
                            .addComponent(DelayFlightIDComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(820, 820, 820))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DelayFlightPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(DelayFlightDelayButton)
                .addGap(531, 531, 531))
        );
        DelayFlightPanelLayout.setVerticalGroup(
            DelayFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DelayFlightPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(DelayFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DelayFlightIDLabel)
                    .addComponent(DelayFlightIDComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(DelayFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DelayFlightHourLabel)
                    .addComponent(DelayFlightHourComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(DelayFlightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DelayFlightMinuteLabel)
                    .addComponent(DelayFlightMinuteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 307, Short.MAX_VALUE)
                .addComponent(DelayFlightDelayButton)
                .addGap(33, 33, 33))
        );

        MenuTabbedPane.addTab("Delay flight", DelayFlightPanel);

        MainPanelRound.add(MenuTabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 41, 1150, 620));

        javax.swing.GroupLayout BottomPanelRoundLayout = new javax.swing.GroupLayout(BottomPanelRound);
        BottomPanelRound.setLayout(BottomPanelRoundLayout);
        BottomPanelRoundLayout.setHorizontalGroup(
            BottomPanelRoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1150, Short.MAX_VALUE)
        );
        BottomPanelRoundLayout.setVerticalGroup(
            BottomPanelRoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        MainPanelRound.add(BottomPanelRound, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 660, 1150, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanelRound, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanelRound, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void MenuPanelRoundMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuPanelRoundMousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_MenuPanelRoundMousePressed

    private void MenuPanelRoundMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuPanelRoundMouseDragged
        this.setLocation(this.getLocation().x + evt.getX() - x, this.getLocation().y + evt.getY() - y);
    }//GEN-LAST:event_MenuPanelRoundMouseDragged

    private void AdministratorRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdministratorRadioButtonActionPerformed
        if (UserRadioButton.isSelected()) {
            UserRadioButton.setSelected(false);
            UserSelectComboBox.setSelectedIndex(0);

        }
        for (int i = 1; i < MenuTabbedPane.getTabCount(); i++) {
            MenuTabbedPane.setEnabledAt(i, true);
        }
        MenuTabbedPane.setEnabledAt(5, false);
        MenuTabbedPane.setEnabledAt(6, false);
        MenuTabbedPane.setEnabledAt(7, false);
    }//GEN-LAST:event_AdministratorRadioButtonActionPerformed

    private void UserRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserRadioButtonActionPerformed
        if (AdministratorRadioButton.isSelected()) {
            AdministratorRadioButton.setSelected(false);
        }
        for (int i = 1; i < MenuTabbedPane.getTabCount(); i++) {

            MenuTabbedPane.setEnabledAt(i, false);

        }
        MenuTabbedPane.setEnabledAt(9, true);
        MenuTabbedPane.setEnabledAt(5, true);
        MenuTabbedPane.setEnabledAt(6, true);
        MenuTabbedPane.setEnabledAt(7, true);
        MenuTabbedPane.setEnabledAt(11, true);
    }//GEN-LAST:event_UserRadioButtonActionPerformed

    private void PassangerRegisterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PassangerRegisterButtonActionPerformed
        String id = PassengerIDTextField.getText();
        String firstname = PassengerFirstNameTextField.getText();
        String lastname = PassengerLastNameTextField.getText();
        String year = PassengerBirthdateYearTextField.getText();
        String month = PassengerMonthComboBox.getItemAt(PassengerMonthComboBox.getSelectedIndex());
        String day = PassengerDayComboBox.getItemAt(PassengerDayComboBox.getSelectedIndex());
        String phoneCode = PassengerPhonePrefixTextField.getText();
        String phone = PassengerPhoneTextField.getText();
        String country = PassengerCountryTextField.getText();

        Response response = this.passengerController.createPassenger(id, firstname, lastname, year, month, day, phoneCode, phone, country); //

        if (response.getStatus() >= 500) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        } else if (response.getStatus() >= 400) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE); //
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE); //

            PassengerIDTextField.setText("");
            PassengerFirstNameTextField.setText("");
            PassengerLastNameTextField.setText("");
            PassengerBirthdateYearTextField.setText("");
            PassengerMonthComboBox.setSelectedIndex(0);
            PassengerDayComboBox.setSelectedIndex(0);
            PassengerPhonePrefixTextField.setText("");
            PassengerPhoneTextField.setText("");
            PassengerCountryTextField.setText("");

            if (response.getStatus() == Status.CREATED && response.getObject() != null) { //
                Passenger createdPassenger = (Passenger) response.getObject(); //
                this.UserSelectComboBox.addItem(String.valueOf(createdPassenger.getId()));
            }
        }
    }//GEN-LAST:event_PassangerRegisterButtonActionPerformed

    private void AirplaneCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AirplaneCreateButtonActionPerformed
        String id = AirplaneIDTextField.getText();
        String brand = AirplaneBrandTextField.getText();
        String model = AirplaneModelTextField.getText();
        String maxCapacity = AirplaneMaxCapacityTextField.getText();
        String airline = AirplaneAirlineTextField.getText();

        Response response = this.planeController.createPlane(id, brand, model, maxCapacity, airline); //

        if (response.getStatus() >= 500) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        } else if (response.getStatus() >= 400) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE); //
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE); //

            AirplaneIDTextField.setText("");
            AirplaneBrandTextField.setText("");
            AirplaneModelTextField.setText("");
            AirplaneMaxCapacityTextField.setText("");
            AirplaneAirlineTextField.setText("");

            if (response.getStatus() == Status.CREATED && response.getObject() != null) { //
                Plane createdPlane = (Plane) response.getObject(); //
                this.FlightPlaneComboBox.addItem(createdPlane.getId());
            }
        }
    }//GEN-LAST:event_AirplaneCreateButtonActionPerformed

    private void LocationAirportCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LocationAirportCreateButtonActionPerformed
        String id = LocationAirportIDTextField.getText();
        String name = LocationAirportNameTextField.getText();
        String city = LocationAirportCityTextField.getText();
        String country = LocationAirportCountryTextField.getText();
        String latitude = LocationAirportLatitudeTextField.getText();
        String longitude = LocationAirportLongitudeTextField.getText();

        Response response = this.locationController.createLocation(id, name, city, country, latitude, longitude); //

        if (response.getStatus() >= 500) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        } else if (response.getStatus() >= 400) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE); //
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE); //

            LocationAirportIDTextField.setText("");
            LocationAirportNameTextField.setText("");
            LocationAirportCityTextField.setText("");
            LocationAirportCountryTextField.setText("");
            LocationAirportLatitudeTextField.setText("");
            LocationAirportLongitudeTextField.setText("");

            if (response.getStatus() == Status.CREATED && response.getObject() != null) { //
                Location createdLocation = (Location) response.getObject(); //
                String airportId = createdLocation.getAirportId();
                this.FlightDepartureLocationComboBox.addItem(airportId);
                this.FlightArrivalLocationComboBox.addItem(airportId);
                this.FlightScaleLocationComboBox.addItem(airportId);
            }
        }
    }//GEN-LAST:event_LocationAirportCreateButtonActionPerformed

    private void FlightCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FlightCreateButtonActionPerformed
        String id = FlightIDTextField.getText();
        String planeId = FlightPlaneComboBox.getItemAt(FlightPlaneComboBox.getSelectedIndex());
        String departureLocationId = FlightDepartureLocationComboBox.getItemAt(FlightDepartureLocationComboBox.getSelectedIndex());
        String arrivalLocationId = FlightArrivalLocationComboBox.getItemAt(FlightArrivalLocationComboBox.getSelectedIndex());
        String scaleLocationId = FlightScaleLocationComboBox.getItemAt(FlightScaleLocationComboBox.getSelectedIndex());
        String year = FlightDepartureDateYearTextField.getText();
        String month = FlightDepartureDateMonthComboBox.getItemAt(FlightDepartureDateMonthComboBox.getSelectedIndex());
        String day = FlightDepartureDateDayComboBox.getItemAt(FlightDepartureDateDayComboBox.getSelectedIndex());
        String hour = FlightDepartureDateHourComboBox.getItemAt(FlightDepartureDateHourComboBox.getSelectedIndex());
        String minutes = FlightDepartureDateMinuteComboBox.getItemAt(FlightDepartureDateMinuteComboBox.getSelectedIndex());
        String hoursDurationsArrival = FlightArrivalHourComboBox.getItemAt(FlightArrivalHourComboBox.getSelectedIndex());
        String minutesDurationsArrival = FlightArrivalMinuteComboBox.getItemAt(FlightArrivalMinuteComboBox.getSelectedIndex());
        String hoursDurationsScale = FlightScaleHourComboBox.getItemAt(FlightScaleHourComboBox.getSelectedIndex());
        String minutesDurationsScale = FlightScaleMinuteComboBox.getItemAt(FlightScaleMinuteComboBox.getSelectedIndex());

        Response response = this.flightController.createFlight(id, planeId, departureLocationId, arrivalLocationId, scaleLocationId, year, month, day, hour, minutes, hoursDurationsArrival, minutesDurationsArrival, hoursDurationsScale, minutesDurationsScale); //

        if (response.getStatus() >= 500) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        } else if (response.getStatus() >= 400) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE); //
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE); //

            FlightIDTextField.setText("");
            FlightPlaneComboBox.setSelectedIndex(0);
            FlightDepartureLocationComboBox.setSelectedIndex(0);
            FlightArrivalLocationComboBox.setSelectedIndex(0);
            FlightScaleLocationComboBox.setSelectedIndex(0);
            FlightDepartureDateYearTextField.setText("");
            FlightDepartureDateMonthComboBox.setSelectedIndex(0);
            FlightDepartureDateDayComboBox.setSelectedIndex(0);
            FlightDepartureDateHourComboBox.setSelectedIndex(0);
            FlightDepartureDateMinuteComboBox.setSelectedIndex(0);
            FlightArrivalHourComboBox.setSelectedIndex(0);
            FlightArrivalMinuteComboBox.setSelectedIndex(0);
            FlightScaleHourComboBox.setSelectedIndex(0);
            FlightScaleMinuteComboBox.setSelectedIndex(0);

            if (response.getStatus() == Status.CREATED && response.getObject() != null) { //
                Flight createdFlight = (Flight) response.getObject(); //
                this.AddToFlightFlightComboBox.addItem(createdFlight.getId());
                this.DelayFlightIDComboBox.addItem(createdFlight.getId());
            }
        }
    }//GEN-LAST:event_FlightCreateButtonActionPerformed

    private void UpdateInfoUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateInfoUpdateButtonActionPerformed
        String id = UpdateInfoIDTextField.getText();
        String firstname = UpdateInfoFirstNameTextField.getText();
        String lastname = UpdateInfoLastNameTextField.getText();
        String year = UpdateInfoBirthdateYearTextField.getText();
        String month = UpdateInfoBirthdateMonthComboBox.getItemAt(UpdateInfoBirthdateMonthComboBox.getSelectedIndex());
        String day = UpdateInfoBirthdateDayComboBox.getItemAt(UpdateInfoBirthdateDayComboBox.getSelectedIndex());
        String phoneCode = UpdateInfoPhonePrefixTextField.getText();
        String phone = UpdateInfoPhoneTextField.getText();
        String country = UpdateInfoCountryTextField.getText();

        Response response = this.passengerController.updatePassenger(id, firstname, lastname, year, month, day, phoneCode, phone, country); //

        if (response.getStatus() >= 500) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        } else if (response.getStatus() >= 400) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE); //
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE); //

            UpdateInfoFirstNameTextField.setText("");
            UpdateInfoLastNameTextField.setText("");
            UpdateInfoBirthdateYearTextField.setText("");
            UpdateInfoBirthdateMonthComboBox.setSelectedIndex(0);
            UpdateInfoBirthdateDayComboBox.setSelectedIndex(0);
            UpdateInfoPhonePrefixTextField.setText("");
            UpdateInfoPhoneTextField.setText("");
            UpdateInfoCountryTextField.setText("");
        }
    }//GEN-LAST:event_UpdateInfoUpdateButtonActionPerformed

    private void AddToFlightAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddToFlightAddButtonActionPerformed
        String passengerId = AddToFlightIDTextField.getText();
        String flightId = AddToFlightFlightComboBox.getItemAt(AddToFlightFlightComboBox.getSelectedIndex());

        Response response = this.flightController.addPassengertoFlight(passengerId, flightId); //

        if (response.getStatus() >= 500) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        } else if (response.getStatus() >= 400) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE); //
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE); //

            // AddToFlightIDTextField is usually not cleared here as it's linked to UserSelectComboBox
            AddToFlightFlightComboBox.setSelectedIndex(0);
        }
    }//GEN-LAST:event_AddToFlightAddButtonActionPerformed

    private void DelayFlightDelayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DelayFlightDelayButtonActionPerformed
        String flightId = DelayFlightIDComboBox.getItemAt(DelayFlightIDComboBox.getSelectedIndex());
        String hours = DelayFlightHourComboBox.getItemAt(DelayFlightHourComboBox.getSelectedIndex());
        String minutes = DelayFlightMinuteComboBox.getItemAt(DelayFlightMinuteComboBox.getSelectedIndex());

        Response response = this.flightController.delayFlight(flightId, hours, minutes); //

        if (response.getStatus() >= 500) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        } else if (response.getStatus() >= 400) { //
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE); //
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE); //
            DelayFlightIDComboBox.setSelectedIndex(0);
            DelayFlightHourComboBox.setSelectedIndex(0);
            DelayFlightMinuteComboBox.setSelectedIndex(0);
        }
    }//GEN-LAST:event_DelayFlightDelayButtonActionPerformed

    private void ShowMyFlightsRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowMyFlightsRefreshButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) ShowMyFlightsTable.getModel();
        model.setRowCount(0);

        String selectedPassengerIdStr = UserSelectComboBox.getSelectedItem() != null ? UserSelectComboBox.getSelectedItem().toString() : null;

        if (selectedPassengerIdStr == null || selectedPassengerIdStr.equals("Select User") || selectedPassengerIdStr.trim().isEmpty()) {
            return;
        }

        Response response = this.passengerController.getFlightsForPassengerTable(selectedPassengerIdStr); //

        if (response.getStatus() == Status.OK) { //
            List<Flight> passengerFlights = (List<Flight>) response.getObject(); //
            if (passengerFlights != null) {
                for (Flight flight : passengerFlights) {
                    model.addRow(new Object[]{
                        flight.getId(),
                        flight.getDepartureDate() != null ? flight.getDepartureDate().toString() : "N/A",
                        flight.calculateArrivalDate() != null ? flight.calculateArrivalDate().toString() : "N/A"
                    });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        }
    }//GEN-LAST:event_ShowMyFlightsRefreshButtonActionPerformed

    private void ShowAllPassengersRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowAllPassengersRefreshButtonActionPerformed
        Response response = this.passengerController.getAllPassengersForTable(); //
        DefaultTableModel model = (DefaultTableModel) ShowAllPassengersTable.getModel();
        model.setRowCount(0);

        if (response.getStatus() == Status.OK) { //
            List<Passenger> passengersList = (List<Passenger>) response.getObject(); //
            if (passengersList != null) {
                for (Passenger passenger : passengersList) {
                    model.addRow(new Object[]{
                        passenger.getId(),
                        passenger.getFullname(),
                        passenger.getBirthDate() != null ? passenger.getBirthDate().toString() : "N/A",
                        passenger.calculateAge(),
                        passenger.generateFullPhone(),
                        passenger.getCountry(),
                        passenger.getNumFlights()
                    });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        }
    }//GEN-LAST:event_ShowAllPassengersRefreshButtonActionPerformed

    private void ShowAllFlightsRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowAllFlightsRefreshButtonActionPerformed
        Response response = this.flightController.getAllFlightsForTable(); //
        DefaultTableModel model = (DefaultTableModel) ShowAllFlightsTable.getModel();
        model.setRowCount(0);

        if (response.getStatus() == Status.OK) { //
            List<Flight> flightsList = (List<Flight>) response.getObject(); //
            if (flightsList != null) {
                for (Flight flight : flightsList) {
                    String scaleAirportId = (flight.getScaleLocation() == null) ? "-" : flight.getScaleLocation().getAirportId();
                    model.addRow(new Object[]{
                        flight.getId(),
                        flight.getDepartureLocation() != null ? flight.getDepartureLocation().getAirportId() : "N/A",
                        flight.getArrivalLocation() != null ? flight.getArrivalLocation().getAirportId() : "N/A",
                        scaleAirportId,
                        flight.getDepartureDate() != null ? flight.getDepartureDate().toString() : "N/A",
                        flight.calculateArrivalDate() != null ? flight.calculateArrivalDate().toString() : "N/A",
                        flight.getPlane() != null ? flight.getPlane().getId() : "N/A",
                        flight.getNumPassengers()
                    });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        }
    }//GEN-LAST:event_ShowAllFlightsRefreshButtonActionPerformed

    private void ShowAllPlanesRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowAllPlanesRefreshButtonActionPerformed
        Response response = this.planeController.getAllPlanesForTable(); //
        DefaultTableModel model = (DefaultTableModel) ShowAllPlanesTable.getModel();
        model.setRowCount(0);

        if (response.getStatus() == Status.OK) { //
            List<Plane> planesList = (List<Plane>) response.getObject(); //
            if (planesList != null) {
                for (Plane plane : planesList) {
                    model.addRow(new Object[]{
                        plane.getId(),
                        plane.getBrand(),
                        plane.getModel(),
                        plane.getMaxCapacity(),
                        plane.getAirline(),
                        plane.getNumFlights()
                    });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        }
    }//GEN-LAST:event_ShowAllPlanesRefreshButtonActionPerformed

    private void ShowAllLocationsRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowAllLocationsRefreshButtonActionPerformed
        Response response = this.locationController.getAllLocationsForTable(); //
        DefaultTableModel model = (DefaultTableModel) ShowAllLocationsTable.getModel();
        model.setRowCount(0);

        if (response.getStatus() == Status.OK) { //
            List<Location> locationsList = (List<Location>) response.getObject(); //
            if (locationsList != null) {
                for (Location location : locationsList) {
                    model.addRow(new Object[]{
                        location.getAirportId(),
                        location.getAirportName(),
                        location.getAirportCity(),
                        location.getAirportCountry()
                    });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE); //
        }
    }//GEN-LAST:event_ShowAllLocationsRefreshButtonActionPerformed

    private void ExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_ExitButtonActionPerformed

    private void UserSelectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserSelectComboBoxActionPerformed
        try {
            String id = UserSelectComboBox.getSelectedItem().toString();
            if (!id.equals(UserSelectComboBox.getItemAt(0))) {
                UpdateInfoIDTextField.setText(id);
                AddToFlightIDTextField.setText(id);
            } else {
                UpdateInfoIDTextField.setText("");
                AddToFlightIDTextField.setText("");
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_UserSelectComboBoxActionPerformed

    private void LocationAirportCreateButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LocationAirportCreateButtonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_LocationAirportCreateButtonMouseClicked

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddToFlightAddButton;
    private javax.swing.JComboBox<String> AddToFlightFlightComboBox;
    private javax.swing.JLabel AddToFlightFlightLabel;
    private javax.swing.JLabel AddToFlightIDLabel;
    private javax.swing.JTextField AddToFlightIDTextField;
    private javax.swing.JPanel AddToFlightPanel;
    private javax.swing.JPanel AdminPanel;
    private javax.swing.JRadioButton AdministratorRadioButton;
    private javax.swing.JLabel AirplaneAirlineLabel;
    private javax.swing.JTextField AirplaneAirlineTextField;
    private javax.swing.JLabel AirplaneBrandLabel;
    private javax.swing.JTextField AirplaneBrandTextField;
    private javax.swing.JButton AirplaneCreateButton;
    private javax.swing.JLabel AirplaneIDLabel;
    private javax.swing.JTextField AirplaneIDTextField;
    private javax.swing.JLabel AirplaneMaxCapacityLabel;
    private javax.swing.JTextField AirplaneMaxCapacityTextField;
    private javax.swing.JLabel AirplaneModelLabel;
    private javax.swing.JTextField AirplaneModelTextField;
    private javax.swing.JPanel AirplaneRegPanel;
    private airport.view.utils.PanelRound BottomPanelRound;
    private javax.swing.JButton DelayFlightDelayButton;
    private javax.swing.JComboBox<String> DelayFlightHourComboBox;
    private javax.swing.JLabel DelayFlightHourLabel;
    private javax.swing.JComboBox<String> DelayFlightIDComboBox;
    private javax.swing.JLabel DelayFlightIDLabel;
    private javax.swing.JComboBox<String> DelayFlightMinuteComboBox;
    private javax.swing.JLabel DelayFlightMinuteLabel;
    private javax.swing.JPanel DelayFlightPanel;
    private javax.swing.JButton ExitButton;
    private javax.swing.JLabel FlightArrivalDurationLabel;
    private javax.swing.JComboBox<String> FlightArrivalHourComboBox;
    private javax.swing.JComboBox<String> FlightArrivalLocationComboBox;
    private javax.swing.JLabel FlightArrivalLocationLabel;
    private javax.swing.JComboBox<String> FlightArrivalMinuteComboBox;
    private javax.swing.JLabel FlightArrivalSeparatorLabel;
    private javax.swing.JButton FlightCreateButton;
    private javax.swing.JComboBox<String> FlightDepartureDateDayComboBox;
    private javax.swing.JComboBox<String> FlightDepartureDateHourComboBox;
    private javax.swing.JLabel FlightDepartureDateLabel;
    private javax.swing.JComboBox<String> FlightDepartureDateMinuteComboBox;
    private javax.swing.JComboBox<String> FlightDepartureDateMonthComboBox;
    private javax.swing.JLabel FlightDepartureDateSeparator1Label;
    private javax.swing.JLabel FlightDepartureDateSeparator2Label;
    private javax.swing.JLabel FlightDepartureDateSeparator3Label;
    private javax.swing.JLabel FlightDepartureDateSeparator4Label;
    private javax.swing.JTextField FlightDepartureDateYearTextField;
    private javax.swing.JComboBox<String> FlightDepartureLocationComboBox;
    private javax.swing.JLabel FlightDepartureLocationLabel;
    private javax.swing.JLabel FlightIDLabel;
    private javax.swing.JTextField FlightIDTextField;
    private javax.swing.JComboBox<String> FlightPlaneComboBox;
    private javax.swing.JLabel FlightPlaneLabel;
    private javax.swing.JPanel FlightRegPanel;
    private javax.swing.JLabel FlightScaleDurationLabel;
    private javax.swing.JComboBox<String> FlightScaleHourComboBox;
    private javax.swing.JComboBox<String> FlightScaleLocationComboBox;
    private javax.swing.JLabel FlightScaleLocationLabel;
    private javax.swing.JComboBox<String> FlightScaleMinuteComboBox;
    private javax.swing.JLabel FlightScaleSeparatorLabel;
    private javax.swing.JLabel LocationAirportCityLabel;
    private javax.swing.JTextField LocationAirportCityTextField;
    private javax.swing.JLabel LocationAirportCountryLabel;
    private javax.swing.JTextField LocationAirportCountryTextField;
    private javax.swing.JButton LocationAirportCreateButton;
    private javax.swing.JLabel LocationAirportIDLabel;
    private javax.swing.JTextField LocationAirportIDTextField;
    private javax.swing.JLabel LocationAirportLatitudeLabel;
    private javax.swing.JTextField LocationAirportLatitudeTextField;
    private javax.swing.JLabel LocationAirportLongitudeLabel;
    private javax.swing.JTextField LocationAirportLongitudeTextField;
    private javax.swing.JLabel LocationAirportNameLabel;
    private javax.swing.JTextField LocationAirportNameTextField;
    private javax.swing.JPanel LocationRegPanel;
    private airport.view.utils.PanelRound MainPanelRound;
    private airport.view.utils.PanelRound MenuPanelRound;
    private javax.swing.JTabbedPane MenuTabbedPane;
    private javax.swing.JPanel PassangerRegPanel;
    private javax.swing.JButton PassangerRegisterButton;
    private javax.swing.JLabel PassengerBirthdateLabel;
    private javax.swing.JLabel PassengerBirthdateSeparator1Label;
    private javax.swing.JLabel PassengerBirthdateSeparator2Label;
    private javax.swing.JTextField PassengerBirthdateYearTextField;
    private javax.swing.JLabel PassengerCountryLabel;
    private javax.swing.JTextField PassengerCountryTextField;
    private javax.swing.JComboBox<String> PassengerDayComboBox;
    private javax.swing.JLabel PassengerFirstNameLabel;
    private javax.swing.JTextField PassengerFirstNameTextField;
    private javax.swing.JLabel PassengerIDLabel;
    private javax.swing.JTextField PassengerIDTextField;
    private javax.swing.JLabel PassengerLastNameLabel;
    private javax.swing.JTextField PassengerLastNameTextField;
    private javax.swing.JComboBox<String> PassengerMonthComboBox;
    private javax.swing.JLabel PassengerPhoneLabel;
    private javax.swing.JLabel PassengerPhonePrefixPlusLabel;
    private javax.swing.JTextField PassengerPhonePrefixTextField;
    private javax.swing.JLabel PassengerPhoneSeparatorLabel;
    private javax.swing.JTextField PassengerPhoneTextField;
    private javax.swing.JPanel ShowAllFlightsPanel;
    private javax.swing.JButton ShowAllFlightsRefreshButton;
    private javax.swing.JScrollPane ShowAllFlightsScrollPane;
    private javax.swing.JTable ShowAllFlightsTable;
    private javax.swing.JPanel ShowAllLocationsPanel;
    private javax.swing.JButton ShowAllLocationsRefreshButton;
    private javax.swing.JScrollPane ShowAllLocationsScrollPane;
    private javax.swing.JTable ShowAllLocationsTable;
    private javax.swing.JPanel ShowAllPassengersPanel;
    private javax.swing.JButton ShowAllPassengersRefreshButton;
    private javax.swing.JScrollPane ShowAllPassengersScrollPane;
    private javax.swing.JTable ShowAllPassengersTable;
    private javax.swing.JPanel ShowAllPlanesPanel;
    private javax.swing.JButton ShowAllPlanesRefreshButton;
    private javax.swing.JScrollPane ShowAllPlanesScrollPane;
    private javax.swing.JTable ShowAllPlanesTable;
    private javax.swing.JPanel ShowMyFlightsPanel;
    private javax.swing.JButton ShowMyFlightsRefreshButton;
    private javax.swing.JScrollPane ShowMyFlightsScrollPane;
    private javax.swing.JTable ShowMyFlightsTable;
    private javax.swing.JComboBox<String> UpdateInfoBirthdateDayComboBox;
    private javax.swing.JLabel UpdateInfoBirthdateLabel;
    private javax.swing.JComboBox<String> UpdateInfoBirthdateMonthComboBox;
    private javax.swing.JTextField UpdateInfoBirthdateYearTextField;
    private javax.swing.JLabel UpdateInfoCountryLabel;
    private javax.swing.JTextField UpdateInfoCountryTextField;
    private javax.swing.JLabel UpdateInfoFirstNameLabel;
    private javax.swing.JTextField UpdateInfoFirstNameTextField;
    private javax.swing.JLabel UpdateInfoIDLabel;
    private javax.swing.JTextField UpdateInfoIDTextField;
    private javax.swing.JLabel UpdateInfoLastNameLabel;
    private javax.swing.JTextField UpdateInfoLastNameTextField;
    private javax.swing.JPanel UpdateInfoPanel;
    private javax.swing.JLabel UpdateInfoPhoneLabel;
    private javax.swing.JLabel UpdateInfoPhonePrefixLabel;
    private javax.swing.JTextField UpdateInfoPhonePrefixTextField;
    private javax.swing.JLabel UpdateInfoPhoneSeparatorLabel;
    private javax.swing.JTextField UpdateInfoPhoneTextField;
    private javax.swing.JButton UpdateInfoUpdateButton;
    private javax.swing.JRadioButton UserRadioButton;
    private javax.swing.JComboBox<String> UserSelectComboBox;
    // End of variables declaration//GEN-END:variables
}
