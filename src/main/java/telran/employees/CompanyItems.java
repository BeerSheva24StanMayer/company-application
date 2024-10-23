package telran.employees;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import org.json.JSONObject;

import telran.view.*;

public class CompanyItems {
    private static Company company;
    private static Employee employee;
    final static int MIN_SALARY = 5000;
    final static int MAX_SALARY = 30000;
    final static String[] DEPARTMENTS = { "QA", "Audit", "Development", "Management" };
    final static long MIN_ID = 100000;
    final static long MAX_ID = 999999;

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
        Item[] employeeType = {
                Item.of("Linear employee", CompanyItems::addEmployeeLine),
                Item.of("Wage employee", CompanyItems::addEmployeeWage),
                Item.of("Manager", CompanyItems::addEmployeeManager),
                Item.of("Sales person", CompanyItems::addEmployeeSales),
                Item.ofExit()
        };
        Menu subMenu = new Menu("Choose type of employee", employeeType);
        subMenu.perform(io);
    }

    static JSONObject addEmployeeBase(InputOutput io) {
        long id = io.readNumberRange("Enter employee ID in range between " + MIN_ID + " and " + MAX_ID + ":",
                "Invalid ID, must be a number between " + MIN_ID + " and " + MAX_ID, MIN_ID, MAX_ID).longValue();

        String department = io.readStringOptions("Enter company department",
                "Department doesn't exist in company", new HashSet<>(Arrays.asList(DEPARTMENTS)));

        int basicSalary = io.readNumberRange("Enter the employee salary:",
                "The salary is out of range of company salary limit", MIN_SALARY, MAX_SALARY).intValue();

        JSONObject jsonBase = new JSONObject(String.format("{\"id\":%d, \"basicSalary\":%d, \"department\":%s}", id, basicSalary, department));
        return jsonBase;

    }

    static JSONObject makeWageEmployee(InputOutput io){
        JSONObject jsonWage = addEmployeeBase(io);
        int hours = io.readInt("Enter the extra hours of employee:", "");
        int wage = io.readInt("Enter the employee wage:", "");
        jsonWage.put("hours", hours);
        jsonWage.put("wage", wage);
        return jsonWage;
    }

    static void addEmployeeLine(InputOutput io) {
        JSONObject jsonEmpl = addEmployeeBase(io).put("className", "telran.employees.Employee");
        company.addEmployee(Employee.getEmployeeFromJSON(jsonEmpl.toString()));
        io.writeLine("Employee added successfully");
    }

    static void addEmployeeWage(InputOutput io) {
        JSONObject jsonWage = makeWageEmployee(io).put("className", "telran.employees.WageEmployee");
        company.addEmployee(WageEmployee.getEmployeeFromJSON(jsonWage.toString()));
        io.writeLine("Wage employee added successfully");
    }

    static void addEmployeeManager(InputOutput io) {
        JSONObject jsonEmpl = addEmployeeBase(io).put("className", "telran.employees.Manager");
        float factor = io.readInt("Enter the factor of the manager:", "");
        jsonEmpl.put("factor", factor);
        company.addEmployee(Manager.getEmployeeFromJSON(jsonEmpl.toString()));
        io.writeLine("Manager added successfully");
    }

    static void addEmployeeSales(InputOutput io) {
        JSONObject jsonSales = makeWageEmployee(io).put("className", "telran.employees.SalesPerson");
        float percent = io.readInt("Enter percent of salesman:", "").floatValue();
        long sales = io.readInt("Enter the sales:", "").longValue();
        jsonSales.put("percent", percent);
        jsonSales.put("sales", sales);
        company.addEmployee(SalesPerson.getEmployeeFromJSON(jsonSales.toString()));
        io.writeLine("Salesman added successfully");
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
        for (Manager manger : managers)
            io.writeLine(manger.toString());
        if (managers.length == 0)
            io.writeLine("No managers found");
    }

    static void saveToFile(InputOutput io) {
        ((CompanyImpl) company).saveToFile("employees.data");
        io.writeLine("Company has been updated to file");
    }

    static void restoreFromFile(InputOutput io) {
        String fileName = io.readString("Enter file to read from: ");
        CompanyImpl companyImpl = (CompanyImpl) company;
        companyImpl.restoreFromFile(fileName);
        companyImpl.saveToFile("employees.data");
        io.writeLine("Company data restored from " + fileName);
    }

}
