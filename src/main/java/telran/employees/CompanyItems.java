package telran.employees;

import telran.view.*;

public class CompanyItems {
    private static Company company;
    public static Item[] getItems(Company company) {
        CompanyItems.company = company;
        Item[] res = {
            Item.of("Add Employee", CompanyItems::addEmployee),
            Item.of("Remove Employee", CompanyItems::removeEmployee),
            Item.of("Get Employee by ID", CompanyItems::getEmployeeById),
            Item.of("Get Department Budget", CompanyItems::getDepartmentBudget),
            Item.of("Get Departments", CompanyItems::getDepartments),
            Item.of("Get Managers with Highest Factor", CompanyItems::getManagersWithMostFactor),
            Item.of("Save", CompanyItems::saveToFile),
            Item.of("Restore", CompanyItems::restoreFromFile),
            Item.ofExit()
        };
        return res;
    }

    static void addEmployee(InputOutput io) {
        Employee employee = io.readObject(
            "Enter employee details in JSON format: ", 
            "Invalid employee data", 
            Employee::getEmployeeFromJSON
        );
        company.addEmployee(employee);
        io.writeLine("Employee added successfully");
    }

    static void removeEmployee(InputOutput io) {
        Long id = io.readLong("Enter employee ID: ", "Invalid ID, please repeat: ");
        Employee removedEmployee = company.removeEmployee(id);
        io.writeLine(String.format("Employee %s successfully removed", removedEmployee));
    }

    static void getEmployeeById(InputOutput io) {
        Long id = io.readLong("Enter employee ID: ", "Invalid ID, please repeat: ");
        Employee employee = company.getEmployee(id);
        io.writeLine(employee == null ? "No employee found" : employee);
    }

    static void getDepartmentBudget(InputOutput io) {
        String department = io.readString("Enter department: ");
        int budget;
        try {
            budget = company.getDepartmentBudget(department);
            io.writeLine("The budget is " + budget);
        } catch (Exception e) {
            io.writeLine("No such department in company");
        }
        
    }

    static void getDepartments(InputOutput io) {
        String[] departments = company.getDepartments();
        io.writeLine("The departments of company are: " + String.join(", ", departments));
    }

    static void getManagersWithMostFactor(InputOutput io) {
        Manager[] managers = company.getManagersWithMostFactor();
        io.writeLine("The managers with most factor: \n");
        for(Manager manger:managers) io.writeLine(manger.toString());
        if(managers.length == 0) io.writeLine("No managers found");
    }

    static void saveToFile(InputOutput io) {
        ((CompanyImpl)company).saveToFile("company.data");
        io.writeLine("Company has been updated to file");
    }

    static void restoreFromFile(InputOutput io) {
        String fileName = io.readString("Enter file to read from: ");
        CompanyImpl companyImpl = (CompanyImpl) company;
        companyImpl.restoreFromFile(fileName);
        companyImpl.saveToFile("company.data");
        io.writeLine("Company data restored from " + fileName);
    }

}
