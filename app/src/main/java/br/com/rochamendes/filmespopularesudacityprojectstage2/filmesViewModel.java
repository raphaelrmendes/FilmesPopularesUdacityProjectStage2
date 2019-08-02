package br.com.rochamendes.filmespopularesudacityprojectstage2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class filmesViewModel extends AndroidViewModel {
    private filmesRepository repository;
    private LiveData<List<filmes>> listarFilmesPopulares;
    private LiveData<List<filmes>> listarFilmesBemAvaliados;
    private LiveData<List<filmes>> listarFilmesFavoritos;

    public  filmesViewModel(@NonNull Application application){
        super(application);
        repository = new filmesRepository(application);

        listarFilmesPopulares = repository.getListaFilmesPopulares();
        listarFilmesBemAvaliados = repository.getListaFilmesBemAvaliados();
        listarFilmesFavoritos = repository.getListaFilmesFavoritos();
    }

    public LiveData<List<filmes>> getListarFilmesPopulares(){
        return listarFilmesPopulares;
    }
    public LiveData<List<filmes>> getListarFilmesBemAvaliados() { return listarFilmesBemAvaliados; }
    public LiveData<List<filmes>> getListarFilmesFavoritos() { return listarFilmesFavoritos; }

    public LiveData<List<filmes>> getListaFilme(int id) { return repository.getListaFilme(id); }
    public LiveData<List<videos>> getListaVideos(int id) { return repository.getListaVideos(id); }
    public LiveData<List<reviews>> getListaReviews(int id) { return repository.getListaReviews(id); }

    public void updateFavorito(filmes Filmes) { repository.updateFavorito(Filmes); }
    public void insertFilmes(filmes[] Filmes){
        repository.insertFilmes(Filmes);
    }
    public void apagaDadosOffline() { repository.apagaDadosOffline(); }

    public void insertVideos(videos[] videos){
        repository.insertVideos(videos);
    }
    public void apagaVideos() { repository.apagaVideos(); }
    public void apagaVideo(int i) { repository.apagaVideo(i); }

    public void insertReviews(reviews[] reviews){
        repository.insertReviews(reviews);
    }
    public void apagaReviews() { repository.apagaReviews(); }
    public void apagaReview(int i) { repository.apagaReview(i); }

}
