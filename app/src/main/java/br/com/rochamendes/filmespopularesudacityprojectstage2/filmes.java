package br.com.rochamendes.filmespopularesudacityprojectstage2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_filmes")
public class filmes {
    @PrimaryKey
    private int idFilme;
    private String nomeFilme;
    private String nomeOriginal;
    private byte[] capaFilme;
    private String idiomaFilme;
    private String sinopseFilme;
    private String dataFilme;
    private float avaliacaoMedia;
    private int contagemAvaliacao;
    private int popularidade;
    private boolean popular;
    private boolean bemAvaliado;
    private boolean favorito;

    public filmes(int idFilme,
                  String nomeFilme,
                  String nomeOriginal,
                  byte[] capaFilme,
                  String idiomaFilme,
                  String sinopseFilme,
                  String dataFilme,
                  float avaliacaoMedia,
                  int contagemAvaliacao,
                  int popularidade,
                  boolean popular,
                  boolean bemAvaliado,
                  boolean favorito) {
        this.idFilme = idFilme;
        this.nomeFilme = nomeFilme;
        this.nomeOriginal = nomeOriginal;
        this.capaFilme = capaFilme;
        this.idiomaFilme = idiomaFilme;
        this.sinopseFilme = sinopseFilme;
        this.dataFilme = dataFilme;
        this.avaliacaoMedia = avaliacaoMedia;
        this.contagemAvaliacao = contagemAvaliacao;
        this.popularidade = popularidade;
        this.popular = popular;
        this.bemAvaliado = bemAvaliado;
        this.favorito = favorito;
    }

    int getIdFilme() {
        return idFilme;
    }

    String getNomeFilme() {
        return nomeFilme;
    }

    String getNomeOriginal() {
        return nomeOriginal;
    }

    byte[] getCapaFilme() {
        return capaFilme;
    }

    String getIdiomaFilme() {
        return idiomaFilme;
    }

    String getSinopseFilme() {
        return sinopseFilme;
    }

    String getDataFilme() {
        return dataFilme;
    }

    float getAvaliacaoMedia() {
        return avaliacaoMedia;
    }

    int getContagemAvaliacao() {
        return contagemAvaliacao;
    }

    int getPopularidade() {
        return popularidade;
    }

    boolean isPopular() {
        return popular;
    }

    boolean isBemAvaliado() {
        return bemAvaliado;
    }

    boolean isFavorito() {
        return favorito;
    }

    public void setIdFilme(int idFilme) {
        this.idFilme = idFilme;
    }

    public void setNomeFilme(String nomeFilme) {
        this.nomeFilme = nomeFilme;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public void setCapaFilme(byte[] capaFilme) {
        this.capaFilme = capaFilme;
    }

    public void setIdiomaFilme(String idiomaFilme) {
        this.idiomaFilme = idiomaFilme;
    }

    public void setSinopseFilme(String sinopseFilme) {
        this.sinopseFilme = sinopseFilme;
    }

    public void setDataFilme(String dataFilme) {
        this.dataFilme = dataFilme;
    }

    public void setAvaliacaoMedia(float avaliacaoMedia) {
        this.avaliacaoMedia = avaliacaoMedia;
    }

    public void setContagemAvaliacao(int contagemAvaliacao) {
        this.contagemAvaliacao = contagemAvaliacao;
    }

    public void setPopularidade(int popularidade) {
        this.popularidade = popularidade;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public void setBemAvaliado(boolean bemAvaliado) {
        this.bemAvaliado = bemAvaliado;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}
