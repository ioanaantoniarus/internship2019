package entity;

public class ContractualHolidayRights {
    private int year;
    private int holidayRights;

    public ContractualHolidayRights(int year, int holidayRights) {
        this.year = year;
        this.holidayRights = holidayRights;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHolidayRights() {
        return holidayRights;
    }

    public void setHolidayRights(int holidayRights) {
        this.holidayRights = holidayRights;
    }
}
