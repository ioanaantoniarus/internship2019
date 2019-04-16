package entity;

import java.time.LocalDate;
import java.util.List;

public class Employee {
    private LocalDate employmentStartDate;
    private LocalDate employmentEndDate;
    private List<SuspensionPeriod> suspensionPeriodList;

    public Employee(LocalDate employmentStartDate, LocalDate employmentEndDate, List<SuspensionPeriod> suspensionPeriodList) {
        this.employmentStartDate = employmentStartDate;
        this.employmentEndDate = employmentEndDate;
        this.suspensionPeriodList = suspensionPeriodList;
    }

    public Employee(){
    }

    public LocalDate getEmploymentStartDate() {
        return employmentStartDate;
    }

    public void setEmploymentStartDate(LocalDate employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }

    public LocalDate getEmploymentEndDate() {
        return employmentEndDate;
    }

    public void setEmploymentEndDate(LocalDate employmentEndDate) {
        this.employmentEndDate = employmentEndDate;
    }

    public List<SuspensionPeriod> getSuspensionPeriodList() {
        return suspensionPeriodList;
    }

    public void setSuspensionPeriodList(List<SuspensionPeriod> suspensionPeriodList) {
        this.suspensionPeriodList = suspensionPeriodList;
    }
}
