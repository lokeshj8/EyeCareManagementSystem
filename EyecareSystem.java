import java.util.*;

class Patient {
    String name;
    int age;
    String problem;

    Patient(String name, int age, String problem) {
        this.name = name;
        this.age = age;
        this.problem = problem;
    }
}

public class EyeCareSystem {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<Patient> patients = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Eye Care Management System ---");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patients");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1: addPatient(); break;
                case 2: viewPatients(); break;
                case 3: System.out.println("Thank you!"); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void addPatient() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine(); // clear buffer
        System.out.print("Enter Eye Problem: ");
        String problem = sc.nextLine();

        Patient p = new Patient(name, age, problem);
        patients.add(p);
        System.out.println("Patient added successfully!");
    }

    static void viewPatients() {
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
        } else {
            System.out.println("\nPatient List:");
            for (Patient p : patients) {
                System.out.println(p.name + " | Age: " + p.age + " | Problem: " + p.problem);
            }
        }
    }
        }
