package com.example.preg_women.Screens;

public class Car {
    private String driverName;
    private String plateNumber;
    private String driverPhoneNumber1;
    private String driverPhoneNumber2;

    public Car(String driverName, String plateNumber, String driverPhoneNumber1, String driverPhoneNumber2) {
        this.driverName = driverName;
        this.plateNumber = plateNumber;
        this.driverPhoneNumber1 = driverPhoneNumber1;
        this.driverPhoneNumber2 = driverPhoneNumber2;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getDriverPhoneNumber1() {
        return driverPhoneNumber1;
    }

    public void setDriverPhoneNumber1(String driverPhoneNumber1) {
        this.driverPhoneNumber1 = driverPhoneNumber1;
    }

    public String getDriverPhoneNumber2() {
        return driverPhoneNumber2;
    }

    public void setDriverPhoneNumber2(String driverPhoneNumber2) {
        this.driverPhoneNumber2 = driverPhoneNumber2;
    }
}
