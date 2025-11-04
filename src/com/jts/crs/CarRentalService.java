package com.jts.crs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CarRentalService {
    private List<Car> cars;
    private List<Customer> customers;
    private List<BookedCarInformation> bookedCarInformation;

    public CarRentalService() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        bookedCarInformation = new ArrayList<>();
    }

    public void bookCar(Car car, Customer customer, int days) {
        if (car.getNoOfAvailableCar() > 0) {
            car.setNoOfAvailableCar(car.getNoOfAvailableCar() - 1);
            bookedCarInformation.add(new BookedCarInformation(car, customer, days));
        } else {
            System.out.println("Car is not available for rent.");
        }
    }

    public void returnCar(Car car, BookedCarInformation booking) {
        car.setNoOfAvailableCar(car.getNoOfAvailableCar() + 1);
        bookedCarInformation.remove(booking);
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void addCustomer(Customer cust) {
        customers.add(cust);
    }

    public void options() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n======= WELCOME TO OUR CAR RENTAL SYSTEM =======");
            System.out.println("1. Rent a Car");
            System.out.println("2. Return a Car");
            System.out.println("3. Available Cars");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            if (choice == 1) { // Rent a Car
                System.out.print("Enter your name: ");
                String custName = sc.nextLine();

                System.out.print("Enter the Car ID you want to rent: ");
                String carId = sc.nextLine();

                int days;
                System.out.print("Enter the number of days for rental: ");
                try {
                    days = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number of days.");
                    continue;
                }

                Customer customer = new Customer("CUSTOMER-" + (customers.size() + 1), custName);
                addCustomer(customer);

                Optional<Car> optionalCar = cars.stream()
                        .filter(c -> c.getCarId().equalsIgnoreCase(carId) && c.getNoOfAvailableCar() > 0)
                        .findAny();

                if (!optionalCar.isPresent()) {
                    System.out.println("Car is not available. Please try another car.");
                    continue;
                }

                Car selectedCar = optionalCar.get();
                System.out.println("\n=== Bill Receipt ===");
                System.out.println("Customer ID: " + customer.getId());
                System.out.println("Customer Name: " + customer.getName());
                System.out.println("Car Brand: " + selectedCar.getBrand() + " Model: " + selectedCar.getModel());
                System.out.println("Rental Days: " + days);
                System.out.println("Total Price: " + selectedCar.calculatePrice(days));

                System.out.print("Confirm rental (Y/N): ");
                String confirmation = sc.nextLine();
                if (confirmation.equalsIgnoreCase("Y")) {
                    bookCar(selectedCar, customer, days);
                    System.out.println("Car booking is done successfully.");
                } else {
                    System.out.println("Car booking is cancelled.");
                }

            } else if (choice == 2) { // Return a Car
                System.out.print("Enter the Car ID you want to return: ");
                String carId = sc.nextLine();

                Optional<Car> optionalCar = cars.stream()
                        .filter(c -> c.getCarId().equalsIgnoreCase(carId))
                        .findAny();

                if (!optionalCar.isPresent()) {
                    System.out.println("Please provide valid car details.");
                    continue;
                }

                Car carToReturn = optionalCar.get();
                BookedCarInformation booking = bookedCarInformation.stream()
                        .filter(b -> b.getCar().getCarId().equals(carToReturn.getCarId()))
                        .findFirst()
                        .orElse(null);

                if (booking == null) {
                    System.out.println("Car booking information not available. Please provide valid details.");
                    continue;
                }

                Customer cust = booking.getCustomer();
                returnCar(carToReturn, booking);
                System.out.println("Car returned successfully by " + cust.getName());

            } else if (choice == 3) { // Available Cars
                System.out.println("\n== Available Cars ==");
                cars.stream()
                        .filter(c -> c.getNoOfAvailableCar() > 0)
                        .forEach(car -> System.out.println(
                                car.getCarId() + " - " + car.getBrand() + " " + car.getModel() + " | Available: " + car.getNoOfAvailableCar()
                        ));

            } else if (choice == 4) { // Exit
                System.out.println("Thank you for choosing us.");
                sc.close();
                break;

            } else {
                System.out.println("Please provide a valid option.");
            }
        }
    }
}
