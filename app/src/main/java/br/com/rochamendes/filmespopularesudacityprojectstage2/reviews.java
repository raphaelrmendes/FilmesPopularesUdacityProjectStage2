package br.com.rochamendes.filmespopularesudacityprojectstage2;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_reviews")
public class reviews {
    @PrimaryKey
    @NonNull
    private String idReview;
    private int idFilme;
    private String autor;
    private String conteudo;
    private String url;

    public reviews(String idReview,
                   int idFilme,
                   String autor,
                   String conteudo,
                   String url) {
        this.idReview = idReview;
        this.idFilme = idFilme;
        this.autor = autor;
        this.conteudo = conteudo;
        this.url = url;
    }

    String getIdReview(){ return idReview; }
    int getIdFilme(){ return idFilme; }
    String getAutor(){ return autor; }
    String getConteudo(){ return conteudo; }
    String getUrl(){ return url; }

    void setIdReview(String idReview){ this.idReview = idReview; }
    void setIdFilme(int idFilme){ this.idFilme = idFilme; }
    void setAutor(String autor){ this.autor = autor; }
    void setConteudo(String conteudo){ this.conteudo = conteudo; }
    void setUrl(String url){ this.url = url; }
}
