package cinema;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Movie {

    private long id;
    private String title;
    private LocalDateTime date;
    private int allSpaces;
    private int freeSpaces;

    public Movie(long id, String title, LocalDateTime date, int allSpaces) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.allSpaces = allSpaces;
        this.freeSpaces = allSpaces;
    }

    public void reserveSpaces(int reservations){
        int result = freeSpaces - reservations;
        if (result >= 0){
            freeSpaces = result;
        }
        else throw new IllegalStateException("Not enough free spaces!");
    }
}
