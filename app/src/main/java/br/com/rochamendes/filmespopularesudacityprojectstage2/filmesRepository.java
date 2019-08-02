package br.com.rochamendes.filmespopularesudacityprojectstage2;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class filmesRepository {
    private filmesDAO FilmesDAO;
    private LiveData<List<filmes>> listaFilmesPopulares;
    private LiveData<List<filmes>> listaFilmesBemAvaliados;
    private LiveData<List<filmes>> listaFilmesFavoritos;

    public  filmesRepository(Application application){
        filmesDatabase database = filmesDatabase.getInstance(application);
        FilmesDAO = database.FilmesDAO();

        listaFilmesPopulares = FilmesDAO.listarFilmesPopulares();
        listaFilmesBemAvaliados = FilmesDAO.listarFilmesBemAvaliados();
        listaFilmesFavoritos = FilmesDAO.listarFilmesFavoritos();
    }

    public LiveData<List<filmes>> getListaFilmesPopulares(){
        return listaFilmesPopulares;
    }
    public LiveData<List<filmes>> getListaFilmesBemAvaliados() {
        return listaFilmesBemAvaliados;
    }
    public LiveData<List<filmes>> getListaFilmesFavoritos() {
        return listaFilmesFavoritos;
    }

    public LiveData<List<filmes>> getListaFilme(int id) { return FilmesDAO.listarFilme(id); }
    public LiveData<List<videos>> getListaVideos(int id) { return FilmesDAO.listarVideos(id); }
    public LiveData<List<reviews>> getListaReviews(int id) { return FilmesDAO.listarReviews(id); }

    public void updateFavorito(filmes Filmes){
        new UpdateFavoritoAsyncTask(FilmesDAO).execute(Filmes);
    }

    public void insertFilmes(filmes[] Filmes){
        new InsertFilmesAsyncTask(FilmesDAO).execute(Filmes);
    }

    public void apagaDadosOffline(){
        new apagarDadosOfflineAsyncTask(FilmesDAO).execute();
    }

    public void insertVideos(videos[] videos){
        new InsertVideosAsyncTask(FilmesDAO).execute(videos);
    }

    public void apagaVideos(){
        new apagarVideosAsyncTask(FilmesDAO).execute();
    }

    public void apagaVideo(int i){
        new apagarVideoAsyncTask(FilmesDAO).execute(i);
    }

    public void insertReviews(reviews[] reviews){
        new InsertReviewsAsyncTask(FilmesDAO).execute(reviews);
    }

    public void apagaReviews(){
        new apagarReviewsAsyncTask(FilmesDAO).execute();
    }

    public void apagaReview(int i){
        new apagarReviewAsyncTask(FilmesDAO).execute(i);
    }

    private static class UpdateFavoritoAsyncTask extends AsyncTask<filmes, Void, Void>{
        private filmesDAO FilmesDAO;
        private UpdateFavoritoAsyncTask(filmesDAO FilmesDAO){
            this.FilmesDAO = FilmesDAO;
        }
        @Override
        protected Void doInBackground(filmes... filmes) {
            FilmesDAO.updateFavorito(filmes[0]);
            return null;
        }
    }

    private static class InsertFilmesAsyncTask extends AsyncTask<filmes, Void, Void>{
        private filmesDAO FilmesDAO;
        private InsertFilmesAsyncTask(filmesDAO FilmesDAO){
            this.FilmesDAO = FilmesDAO;
        }
        @Override
        protected Void doInBackground(filmes... filmes) {
            FilmesDAO.insertFilmes(filmes);
            return null;
        }
    }

    private static class apagarDadosOfflineAsyncTask extends AsyncTask<Void, Void, Void>{
        private filmesDAO FilmesDAO;
        private apagarDadosOfflineAsyncTask(filmesDAO FilmesDAO){
            this.FilmesDAO = FilmesDAO;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            FilmesDAO.apagarDadosOffline();
            return null;
        }
    }

    private static class InsertVideosAsyncTask extends AsyncTask<videos, Void, Void>{
        private filmesDAO FilmesDAO;
        private InsertVideosAsyncTask(filmesDAO FilmesDAO){
            this.FilmesDAO = FilmesDAO;
        }
        @Override
        protected Void doInBackground(videos... videos) {
            FilmesDAO.insertVideos(videos);
            return null;
        }
    }

    private static class apagarVideosAsyncTask extends AsyncTask<Void, Void, Void>{
        private filmesDAO FilmesDAO;
        private apagarVideosAsyncTask(filmesDAO FilmesDAO){
            this.FilmesDAO = FilmesDAO;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            FilmesDAO.apagarVideos();
            return null;
        }
    }

    private static class apagarVideoAsyncTask extends AsyncTask<Integer, Void, Void>{
        private filmesDAO FilmesDAO;
        private apagarVideoAsyncTask(filmesDAO FilmesDAO){
            this.FilmesDAO = FilmesDAO;
        }
        @Override
        protected Void doInBackground(Integer... integers) {
            FilmesDAO.apagarVideo(integers[0]);
            return null;
        }
    }

    private static class InsertReviewsAsyncTask extends AsyncTask<reviews, Void, Void>{
        private filmesDAO FilmesDAO;
        private InsertReviewsAsyncTask(filmesDAO FilmesDAO){
            this.FilmesDAO = FilmesDAO;
        }
        @Override
        protected Void doInBackground(reviews... reviews) {
            FilmesDAO.insertReviews(reviews);
            return null;
        }
    }

    private static class apagarReviewsAsyncTask extends AsyncTask<Void, Void, Void>{
        private filmesDAO FilmesDAO;
        private apagarReviewsAsyncTask(filmesDAO FilmesDAO){
            this.FilmesDAO = FilmesDAO;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            FilmesDAO.apagarReviews();
            return null;
        }
    }

    private static class apagarReviewAsyncTask extends AsyncTask<Integer, Void, Void>{
        private filmesDAO FilmesDAO;
        private apagarReviewAsyncTask(filmesDAO FilmesDAO){
            this.FilmesDAO = FilmesDAO;
        }
        @Override
        protected Void doInBackground(Integer... integers) {
            FilmesDAO.apagarReview(integers[0]);
            return null;
        }
    }
}
