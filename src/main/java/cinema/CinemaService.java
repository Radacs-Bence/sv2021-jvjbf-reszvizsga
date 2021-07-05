package cinema;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class CinemaService {

    private ModelMapper modelMapper;
    private AtomicLong idGenerator = new AtomicLong();
    private List<Movie> movies = new ArrayList<>();

    public CinemaService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<MovieDTO> listMovies(Optional<String> title) {
        List<Movie> filtered = movies.stream()
                .filter(instrument -> title.isEmpty() || instrument.getTitle().equalsIgnoreCase(title.get()))
                .collect(Collectors.toList());

        Type targetListType = new TypeToken<List<MovieDTO>>() {}.getType();
        return modelMapper.map(filtered, targetListType);
    }

    public MovieDTO movieById(long id) {
        return modelMapper.map(searchById(id), MovieDTO.class);
    }

    private Movie searchById(long id) {
        return movies.stream()
                .filter(instrument -> instrument.getId() == id)
                .findAny()
                .orElseThrow(() ->new IllegalArgumentException("Movie not found: " + id));
    }

    public MovieDTO createMovie(CreateMovieCommand command) {
        Movie created = new Movie(idGenerator.incrementAndGet(), command.getTitle(), command.getDate(), command.getMaxReservation());
        movies.add(created);
        return modelMapper.map(created, MovieDTO.class);

    }

    public MovieDTO reserveSpaces(long id, CreateReservationCommand command) {
        Movie reservationMovie = searchById(id);
        reservationMovie.reserveSpaces(command.getReservedSpaces());
        return modelMapper.map(reservationMovie, MovieDTO.class);
    }

    public MovieDTO updateDate(long id, UpdateDateCommand command) {
        Movie updateMovie = searchById(id);
        updateMovie.setDate(command.getDate());
        return modelMapper.map(updateMovie, MovieDTO.class);
    }

    public void deleteAllMovies() {
        movies.clear();
        idGenerator = new AtomicLong();
    }
}
