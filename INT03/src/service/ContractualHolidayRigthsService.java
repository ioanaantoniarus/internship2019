package service;

import entity.ContractualHolidayRights;
import entity.Employee;
import entity.SuspensionPeriod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

public class ContractualHolidayRigthsService {
    private final int holidayDays=20;
    public ContractualHolidayRigthsService() {
    }

    public Employee readEmployeeData(String path){
        JSONParser parser = new JSONParser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Employee employee=new Employee();
        try {
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject =  (JSONObject) obj;

            String employmentStartDate = (String) jsonObject.get("employmentStartDate");
            String employmentEndDate = (String) jsonObject.get("employmentEndDate");

            employee.setEmploymentStartDate(LocalDate.parse(employmentStartDate, formatter));
            employee.setEmploymentEndDate(LocalDate.parse(employmentEndDate, formatter));

            JSONArray suspensionList = (JSONArray) jsonObject.get("suspensionPeriodList");
            List<SuspensionPeriod> suspensionPeriodList=new ArrayList<SuspensionPeriod>();
            for (Object o : suspensionList)
            {
                JSONObject suspension = (JSONObject) o;
                String start=(String) suspension.get("startDate");
                String end=(String) suspension.get("endDate");
                LocalDate startDate= LocalDate.parse(start, formatter);
                LocalDate endDate= LocalDate.parse(end, formatter);
                suspensionPeriodList.add(new SuspensionPeriod(startDate,endDate));
            }
            employee.setSuspensionPeriodList(suspensionPeriodList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return employee;
    }

    public List<ContractualHolidayRights> calculateHolidayRights(Employee employee){
        int startEmploymentYear=employee.getEmploymentStartDate().getYear();
        int endEmploymentYear=employee.getEmploymentEndDate().getYear();
        int bonus=0;
        List<ContractualHolidayRights> holidayList=new ArrayList<ContractualHolidayRights>();
        if(startEmploymentYear==endEmploymentYear){
            int notWorkedDays=0;
            notWorkedDays+=DAYS.between(employee.getEmploymentStartDate().with(firstDayOfYear()),employee.getEmploymentStartDate());
            notWorkedDays+=DAYS.between(employee.getEmploymentEndDate(),employee.getEmploymentEndDate().with(lastDayOfYear()));
            int suspendedDays=computeSuspendedDays(employee.getSuspensionPeriodList(),startEmploymentYear,notWorkedDays);
            int daysOfTheYear=0;
            daysOfTheYear+=DAYS.between(employee.getEmploymentStartDate().with(lastDayOfYear()),employee.getEmploymentStartDate().with(firstDayOfYear()))+1;
            int rights=computeHolidayRightsOnYear(daysOfTheYear,daysOfTheYear-suspendedDays,bonus);
            holidayList.add(new ContractualHolidayRights(startEmploymentYear,rights));
        }
        else {
            for (int i = startEmploymentYear; i <= endEmploymentYear; i++) {
                if(i==startEmploymentYear){
                    int notWorkedDays=0;
                    notWorkedDays+=DAYS.between(employee.getEmploymentStartDate().with(firstDayOfYear()),employee.getEmploymentStartDate());
                    int suspendedDays=computeSuspendedDays(employee.getSuspensionPeriodList(),i,notWorkedDays);
                    int daysOfTheYear=0;
                    daysOfTheYear+=1+DAYS.between(employee.getEmploymentStartDate().withYear(i).with(firstDayOfYear()),employee.getEmploymentStartDate().withYear(i).with(lastDayOfYear()));
                    int rights=computeHolidayRightsOnYear(daysOfTheYear,daysOfTheYear-suspendedDays,bonus);
                    holidayList.add(new ContractualHolidayRights(i,rights));
                }else{
                    if(i==endEmploymentYear) {
                        int notWorkedDays = 0;
                        notWorkedDays += DAYS.between(employee.getEmploymentEndDate(), employee.getEmploymentEndDate().with(lastDayOfYear()));
                        int suspendedDays = computeSuspendedDays(employee.getSuspensionPeriodList(), i, notWorkedDays);
                        int daysOfTheYear=0;
                        daysOfTheYear+=1+DAYS.between(employee.getEmploymentStartDate().withYear(i).with(firstDayOfYear()),employee.getEmploymentStartDate().withYear(i).with(lastDayOfYear()));
                        int rights = computeHolidayRightsOnYear(daysOfTheYear, daysOfTheYear - suspendedDays, bonus);
                        holidayList.add(new ContractualHolidayRights(i,rights));
                    }else{
                        int notWorkedDays=0;
                        int suspendedDays = computeSuspendedDays(employee.getSuspensionPeriodList(), i, notWorkedDays);
                        int daysOfTheYear=0;
                        daysOfTheYear+=1+DAYS.between(employee.getEmploymentStartDate().withYear(i).with(firstDayOfYear()),employee.getEmploymentStartDate().withYear(i).with(lastDayOfYear()));
                        int rights = computeHolidayRightsOnYear(daysOfTheYear, daysOfTheYear - suspendedDays, bonus);
                        holidayList.add(new ContractualHolidayRights(i,rights));
                    }
                }
                if(i<4){
                    bonus++;
                }
            }
        }
        return holidayList;
    }

    private int computeSuspendedDays(List<SuspensionPeriod> list, int year, int totalSuspendedDays){
        for(SuspensionPeriod suspensionPeriod: list){
            if(suspensionPeriod.getStartDate().getYear()==year){
                if(suspensionPeriod.getEndDate().getYear()==year){
                    totalSuspendedDays+=DAYS.between(suspensionPeriod.getStartDate(),suspensionPeriod.getEndDate());
                }else{
                    totalSuspendedDays+=DAYS.between(suspensionPeriod.getStartDate(),suspensionPeriod.getStartDate().with(lastDayOfYear()));
                }
            }else if(suspensionPeriod.getEndDate().getYear()==year){
                totalSuspendedDays+=DAYS.between(suspensionPeriod.getEndDate().with(firstDayOfYear()),suspensionPeriod.getEndDate());
            }
        }
        return totalSuspendedDays;
    }

    private int computeHolidayRightsOnYear(int daysOfYear, int workedDays, int increase){
        int initialHolidayRights=holidayDays+increase;
        return (workedDays*initialHolidayRights)/daysOfYear;
    }

    public void writeOutput(List<ContractualHolidayRights> list){
        JSONObject outputDetails = new JSONObject();

        JSONArray holidayList = new JSONArray();

        for(ContractualHolidayRights holidays:list){
            JSONObject year = new JSONObject();
            year.put("year",holidays.getYear());
            year.put("holidayDays",holidays.getHolidayRights());
            holidayList.add(year);
        }

        outputDetails.put("holidayRightsPerYearList",holidayList);

        JSONObject output = new JSONObject();
        output.put("output",outputDetails);

        //Write JSON file
        try (FileWriter file = new FileWriter("output.json")) {

            file.write(output.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
