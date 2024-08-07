import java.io.*;
import java.nio.file.*;
import java.util.*;

class CustomerBilling {
    static class Product {
        String name;
        float price;
        int qty;

        Product(String name, float price, int qty) {
            this.name = name;
            this.price = price;
            this.qty = qty;
        }
    }

    static class Order {
        String customer;
        String phone;
        String email;
        String date;
        Product[] products;

        Order(String customer, String phone, String email, String date, int numOfProducts) {
            this.customer = customer;
            this.phone = phone;
            this.email = email;
            this.date = date;
            this.products = new Product[numOfProducts];
        }
    }

    static void generateBillHeader(PrintWriter writer, String name, String phone, String email, String date) {
        writer.println("\n\n\t   VD Software Solutions ");
        writer.println("\t   -----------------------");
        writer.println("Date: " + date);
        writer.println("Invoice To: " + name);
        writer.println("Phone: " + phone);
        writer.println("Email: " + email);
        writer.println("---------------------------------------");
        writer.printf("Product\t\tQty\t\tTotal\t\t\n");
        writer.println("---------------------------------------");
    }

    static void generateBillBody(PrintWriter writer, Product product) {
        writer.printf("%s\t\t%d\t\t%.2f\t\t\n", product.name, product.qty, product.qty * product.price);
    }

    static void generateBillFooter(PrintWriter writer, float total) {
        writer.println();
        float gst = 0.18f * total;
        writer.println("---------------------------------------");
        writer.printf("Sub Total\t\t\t%.2f\n", total);
        writer.printf("GST @18%%\t\t\t%.2f\n", gst);
        writer.println("---------------------------------------");
        writer.printf("Grand Total\t\t\t%.2f\n", total + gst);
        writer.println("---------------------------------------");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        char contFlag = 'y';

        while (contFlag == 'y') {
            System.out.println("\t-------- VD Software Solutions -----------");
            System.out.println("1. Generate bills");
            System.out.println("2. Show all bills");
            System.out.println("3. Search");
            System.out.println("4. Exit");
            System.out.print("Your choice:\t");
            int opt = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (opt) {
                case 1:
                    generateBill(sc);
                    break;
                case 2:
                    showAllBills();
                    break;
                case 3:
                    searchBill(sc);
                    break;
                case 4:
                    System.out.println("\n\t\t Thank You ! :)\n\n");
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Sorry, invalid option");
                    break;
            }
            System.out.print("Do you want to perform another operation? [y/n]:\t");
            contFlag = sc.next().charAt(0);
        }
        System.out.println("\n\t\tThank You ! :)\n\n");
        sc.close();
    }

    private static void generateBill(Scanner sc) {
        System.out.print("Customer name:\t");
        String customerName = sc.nextLine();
        System.out.print("Phone number:\t");
        String phoneNumber = sc.nextLine();
        System.out.print("Email:\t");
        String email = sc.nextLine();
        System.out.print("Date (yyyy-mm-dd):\t");
        String date = sc.nextLine();
        System.out.print("Number of products:\t");
        int n = sc.nextInt();
        sc.nextLine(); // Consume newline

        Order order = new Order(customerName, phoneNumber, email, date, n);
        float total = 0;

        for (int i = 0; i < n; i++) {
            System.out.print("Product " + (i + 1) + ":\t");
            String productName = sc.nextLine();
            System.out.print("Quantity:\t");
            int qty = sc.nextInt();
            System.out.print("Unit price:\t");
            float price = sc.nextFloat();
            sc.nextLine(); // Consume newline

            order.products[i] = new Product(productName, price, qty);
            total += qty * price;
        }

        saveBill(order, total);
    }

    private static void saveBill(Order order, float total) {
        Path billsDir = Paths.get("bills");
        if (!Files.exists(billsDir)) {
            try {
                Files.createDirectory(billsDir);
            } catch (IOException e) {
                System.out.println("Error creating directory: " + e.getMessage());
                return;
            }
        }

        String fileName = "bills/" + order.customer.replaceAll("\\s", "_") + "_" + order.date.replaceAll("-", "_") + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            generateBillHeader(writer, order.customer, order.phone, order.email, order.date);
            for (Product product : order.products) {
                generateBillBody(writer, product);
            }
            generateBillFooter(writer, total);
            System.out.println("Bill saved as " + fileName);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showAllBills() {
        Path billsDir = Paths.get("bills");
        if (Files.exists(billsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(billsDir)) {
                System.out.println("\nAvailable bills:");
                for (Path file : stream) {
                    System.out.println(file.getFileName());
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("No bills directory found.");
        }
    }

    private static void searchBill(Scanner sc) {
        System.out.print("Enter customer full name to search:\t");
        String searchName = sc.nextLine().replaceAll("\\s", "_");

        Path billsDir = Paths.get("bills");
        if (Files.exists(billsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(billsDir)) {
                boolean found = false;
                for (Path file : stream) {
                    if (file.getFileName().toString().contains(searchName)) {
                        System.out.println("Found: " + file.getFileName());
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("No bills found for " + searchName.replaceAll("_", " "));
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("No bills directory found.");
        }
    }
}
