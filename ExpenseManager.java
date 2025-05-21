import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
//import java.nio.file.Path;
//import java.nio.file.Files;

public class ExpenseManager {
    private static final String FILE_NAME = "data.csv";
    private static List<Transactions> transactions = new ArrayList<>();

    public static void main(String[] args){
        //transactions = loadTransactions();
        boolean isRunning = true;
        Scanner sc = new Scanner(System.in);
        while(isRunning){
            System.out.println("\n----------------------------------------");
            System.out.println("Expense Tracker - Select an option : ");
            System.out.println("1) Add Transaction ");
            System.out.println("2) View Monthly Summary");
            System.out.println("3) load data from given file");
            System.out.println("4) save and exit");
            System.out.println("\n----------------------------------------");
            int choice = sc.nextInt();
            sc.nextLine();
            switch(choice){
                case 1 -> addTransactions(sc);
                case 2 -> viewMonthlySummaries(sc);
                case 3 -> loadTransactions(sc);
                case 4 -> {
                    saveTransactions();
                    System.out.println("Transactions saved. Exiting...");
                    isRunning = false;
                }
                default -> System.out.println("Invalid Choice, please select from available ones and try again.");
            }
        }
        sc.close();
    }

    private static void saveTransactions() {
        try{
            PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME));
            transactions.forEach(transaction -> writer.println(transaction));
            writer.flush();
            writer.close();
        }catch(IOException e){
            System.out.println("Error while writing transactions into file: " + e.getMessage());
        }
        
    }


    private static void viewMonthlySummaries(Scanner sc) {
        System.out.println("Enter the month and year in (MM-YYYY) : ");
        String month = sc.nextLine().trim();

        double totalIncome = 0, totalExpenses = 0;
        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expensesByCategory = new HashMap<>();

        for (Transactions transaction : transactions) {
            if (transaction.getDate().equals(month)) {
                if (transaction.getTypeDetail().equalsIgnoreCase("income")) {
                    totalIncome += transaction.getAmount();
                    incomeByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
                } else {
                    totalExpenses += transaction.getAmount();
                    expensesByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
                }
            }
        }

        // Display Summary
        System.out.println("\n Summary for: " + month);
        System.out.println("----------------------------------------");

        System.out.println("\n Income Breakdown:");
        if (incomeByCategory.isEmpty()) {
            System.out.println("No income recorded.");
        } else {
            incomeByCategory.forEach((category, amount) ->
                    System.out.printf("   %s: $%.2f%n", category, amount)
            );
        }

        System.out.println("\n Expense Breakdown:");
        if (expensesByCategory.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            expensesByCategory.forEach((category, amount) ->
                    System.out.printf("    %s: $%.2f%n", category, amount)
            );
        }

        System.out.println("\n----------------------------------------");
        System.out.printf(" Total Income: $%.2f%n", totalIncome);
        System.out.printf(" Total Expenses: $%.2f%n", totalExpenses);

        double netBalance = totalIncome - totalExpenses;
        System.out.println("\n----------------------------------------");

        if (netBalance > 0) {
            System.out.printf(" Net Savings: $%.2f%n", netBalance);
        } else {
            System.out.printf(" Net Debt: $%.2f%n", Math.abs(netBalance));
        }

        System.out.println("----------------------------------------\n");
    }


    private static void addTransactions(Scanner sc) {
        System.out.println("Enter the type detail ( 1) for Income OR 2) for Expense) :  ");

        int typeChoice;
        while (!sc.hasNextInt()) {
            System.out.println("Invalid format! Enter a valid choice");
            sc.next();
        }
        typeChoice = sc.nextInt();
        sc.nextLine();

        String type = (typeChoice == 1) ? "income" : (typeChoice == 2) ? "expense" : " unknown";
        if (type.equals("unknown")) {
            System.out.println("Enter a valid choice 1 or 2 : ");
            return;
        }

        System.out.println("Enter the category of the typeDetail : ");
        String category = sc.nextLine().trim().toLowerCase();

        System.out.println("Enter the amount in $: ");
        while (!sc.hasNextDouble()) {
            System.out.println("enter a valid amt");
            sc.next();
        }

        double amt ;
        while(true){
            amt = sc.nextDouble();
            if(amt<0){
                System.out.println("please enter a valid amt!");
            }else{
                break;
            }
        }
        sc.nextLine();

        System.out.println("Enter the date in (MM-YYYY) format valid between 01 and 12 : ");
        String date;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        while (true) {
            date = sc.nextLine().trim();

            // Regex ensures correct format
            if (!date.matches("(0[1-9]|1[0-2])-\\d{4}")) {
                System.out.println(" Invalid format! Please enter date in (MM-YYYY) format (01-12).");
                continue;
            }

            try {
                // Convert date to YearMonth & check if it's in the future
                YearMonth inputDate = YearMonth.parse(date, formatter);
                YearMonth currentDate = YearMonth.now();

                if (inputDate.isAfter(currentDate)) {
                    System.out.println(" Date cannot be in the future! Please enter a valid past or current date.");
                    continue;
                }

                System.out.println(" Date accepted: " + date);
                break;

            } catch (DateTimeParseException e) {
                System.out.println(" Invalid date! Please try again.");
            }
        }

        //sc.close();
        transactions.add(new Transactions(type, category, amt, date));
        System.out.println("Transaction added..");
    }

    private static void loadTransactions(Scanner sc) {
        System.out.println("Enter the file path : ");
        String filePath;

        while(true) {
            filePath = sc.nextLine().trim();
            if (fileValidate(filePath)) {
                System.out.println("loading the transactions from given file");
                break;
            } else {
                System.out.println("Invalid path! Enter a valid file path");
            }
        }
        File file = new File(filePath);

        try{
            Scanner fileScanner = new Scanner(file);
            while(fileScanner.hasNext()){
                String dataStatement = fileScanner.nextLine();
                String[] data = dataStatement.split(",");
                if(data.length == 4 ){
                    String type = (data[0] != null) && (!data[0].isEmpty()) ? data[0] : "other";// if left empty , taken as other
                    String category = (data[1] != null && !data[1].isEmpty()) ? data[1] : "misc"; // if left empty , taken as misc

                    double enteredAmt = 0.0;
                    try{
                        enteredAmt =  Double.parseDouble(data[2]);
                    }catch(NumberFormatException e) {
                        System.out.println("Invalid amt, setting it to default 0 ");
                    }

                    // if month and year is not present, taking it as of the current month
                    LocalDate currDate = LocalDate.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-yyyy");

                    String date = (data[3] != null && !data[3].isEmpty())? data[3] : currDate.format(dateTimeFormatter);
                    transactions.add(new Transactions(type, category, enteredAmt, date));
                }else{
                    System.out.println("skipping current entry as number of paramters don't match");
                }
            }
            fileScanner.close();
            System.out.println("Data added successfully!");    
        }catch(IOException | NumberFormatException e){
            System.out.println("Invalid data : " + e.getMessage());
        }


    }


    private static boolean fileValidate(String filePath) {
        File file =  new File(filePath);

        if(!file.exists()){
            System.out.println("file doesn't exist! ");
            return false;
        }

        if(!file.canRead()){
            System.out.println("file is not readable! ");
            return false;
        }

        if(!filePath.toLowerCase().endsWith(".csv")){
            System.out.println("Invalid format! please give file with a .csv format");
            return false;
        }

//        try {
//            long fileSize = Files.size(Path.of(filePath));
//            if (fileSize > 5 * 1024 * 1024) { // 5MB limit
//                System.out.println(" File size exceeds the allowed limit (5MB)");
//                return false;
//            }
//        } catch (IOException e) {
//            System.out.println(" Error reading file size: " + e.getMessage());
//            return false;
//        }

        System.out.println("File is valid!");
        return  true;
    }

}



