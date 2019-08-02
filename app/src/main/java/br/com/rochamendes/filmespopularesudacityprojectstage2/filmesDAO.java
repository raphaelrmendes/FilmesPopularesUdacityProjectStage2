package br.com.rochamendes.filmespopularesudacityprojectstage2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface filmesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFilmes(filmes[] Filmes);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideos(videos[] Videos);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReviews(reviews[] Reviews);

    @Update
    void updateFavorito(filmes Filme);

    @Query("DELETE FROM tabela_filmes")
    void apagarDadosOffline();
    @Query("DELETE FROM tabela_videos")
    void apagarVideos();
    @Query("DELETE FROM tabela_videos WHERE idFilme = :idFilme")
    void apagarVideo(int idFilme);
    @Query("DELETE FROM tabela_reviews")
    void apagarReviews();
    @Query("DELETE FROM tabela_reviews WHERE idFilme = :idFilme")
    void apagarReview(int idFilme);

    @Query("SELECT * FROM tabela_videos WHERE idFilme = :idFilme")
    LiveData<List<videos>> listarVideos(int idFilme);
    @Query("SELECT * FROM tabela_reviews WHERE idFilme = :idFilme")
    LiveData<List<reviews>> listarReviews(int idFilme);

    @Query("SELECT * FROM tabela_filmes WHERE idFilme = :idFilme")
    LiveData<List<filmes>> listarFilme(int idFilme);
    @Query("SELECT * FROM tabela_filmes WHERE popular ORDER BY popularidade DESC")
    LiveData<List<filmes>> listarFilmesPopulares();
    @Query("SELECT * FROM tabela_filmes WHERE bemAvaliado ORDER BY avaliacaoMedia DESC")
    LiveData<List<filmes>> listarFilmesBemAvaliados();
    @Query("SELECT * FROM tabela_filmes WHERE favorito ORDER BY popularidade DESC")
    LiveData<List<filmes>> listarFilmesFavoritos();
}
