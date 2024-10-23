package telran.employees;

import java.util.HashMap;

import telran.io.Persistable;
import telran.view.*;

public class Main {
    public static void main(String[] args) {
        Company company = new CompanyImpl();
        if (company instanceof Persistable persistable) {
            try {
                persistable.restoreFromFile("employees.data");
            } catch (Exception e) {
                persistable.saveToFile("employees.data");
            }
        }
        Item[] items = CompanyItems.getItems(company);
        Menu menu = new Menu("Company Application", items);
        menu.perform(new StandardInputOutput());
    }
}