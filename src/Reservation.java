import java.time.Instant;
import java.time.LocalDateTime;

public class Reservation {

    Table table;
    int numOfPeople;
    int customerId;
    LocalDateTime startTime;
    LocalDateTime endTime;

    public Reservation(int numOfPeople, int customerId, LocalDateTime startTime, LocalDateTime endTime) {
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

    public int getCustomerId() {
        return customerId;
    }
}
