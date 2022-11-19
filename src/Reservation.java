import java.time.Instant;
import java.time.LocalDateTime;

public class Reservation {

    Table table;
    int numOfPeople;
    int customerId;
    LocalDateTime startTime;
    LocalDateTime endTime;

    public Reservation(Table t, int numOfPeople, int customerId, LocalDateTime startTime, LocalDateTime endTime) {
        this.table = t;
        this.numOfPeople = numOfPeople;
        this.customerId = customerId;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public Table getTable() {
        return table;
    }

    public int getCustomerId() {
        return customerId;
    }
}
