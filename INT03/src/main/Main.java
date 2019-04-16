package main;

import entity.ContractualHolidayRights;
import entity.Employee;
import service.ContractualHolidayRigthsService;

import java.util.List;


public class Main {

    public static void main(String[] args) {
        ContractualHolidayRigthsService calculate=new ContractualHolidayRigthsService();
        Employee employee=calculate.readEmployeeData("input.json");
        List<ContractualHolidayRights> list=calculate.calculateHolidayRights(employee);
        calculate.writeOutput(list);
    }
}
