package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/cinema")
public class CinemaController {

    private CinemaService cinemaService;

    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @GetMapping
    public List<MovieDTO> listMovies(@RequestParam Optional<String> title){
        return cinemaService.listMovies(title);
    }

    @GetMapping("/{id}")
    public MovieDTO movieById(@PathVariable("id") long id){
        return cinemaService.movieById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO createMovie(@Valid @RequestBody CreateMovieCommand command){
        return cinemaService.createMovie(command);
    }

    @PostMapping("/{id}/reserve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MovieDTO reserveSpaces(@PathVariable("id") long id, @Valid @RequestBody CreateReservationCommand command){
        return cinemaService.reserveSpaces(id, command);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MovieDTO updateDate(@PathVariable("id") long id, @Valid @RequestBody UpdateDateCommand command){
        return cinemaService.updateDate(id, command);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllMovies(){
        cinemaService.deleteAllMovies();
    }


    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Problem> idNotFound(IllegalArgumentException e) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-found"))
                .withTitle("Id not found")
                .withStatus(Status.NOT_FOUND)
                .withDetail(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Problem> validationException(MethodArgumentNotValidException exception) {

        List<Violation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new Violation(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-valid"))
                .withTitle("Validation error(s)")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .with("violations", violations)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler({IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Problem> notEnoughSpaces(IllegalStateException e){
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/bad-reservation"))
                .withTitle("Not enough spaces for reservation")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }




}
