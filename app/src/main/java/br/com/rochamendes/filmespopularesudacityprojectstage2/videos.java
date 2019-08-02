package br.com.rochamendes.filmespopularesudacityprojectstage2;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_videos")
public class videos {
    @PrimaryKey
    @NonNull
    private String videoKey;
    private int idFilme;
    private String videoNome;
    private String videoTipo;
    private String videoSite;
    private String idiomaVideo;
    private String paisVideo;

    public videos(String videoKey,
                  int idFilme,
                  String videoNome,
                  String videoTipo,
                  String videoSite,
                  String idiomaVideo,
                  String paisVideo) {
        this.videoKey = videoKey;
        this.idFilme = idFilme;
        this.videoNome = videoNome;
        this.videoTipo = videoTipo;
        this.videoSite = videoSite;
        this.idiomaVideo = idiomaVideo;
        this.paisVideo = paisVideo;
    }

    String getVideoKey(){ return videoKey; }
    int getIdFilme(){ return idFilme; }
    String getVideoNome(){ return videoNome; }
    String getVideoTipo(){ return videoTipo; }
    String getVideoSite(){ return videoSite; }
    String getIdiomaVideo() { return idiomaVideo; }
    String getPaisVideo(){ return paisVideo; }

    void setVideoKey(String videoKey){ this.videoKey = videoKey; }
    void setIdFilme(int idFilme){ this.idFilme = idFilme; }
    void setVideoNome(String videoNome){ this.videoNome = videoNome; }
    void setVideoTipo(String videoTipo){ this.videoTipo = videoTipo; }
    void setVideoSite(String videoSite){ this.videoSite = videoSite; }
    void setIdiomaVideo(String idiomaVideo) { this.idiomaVideo = idiomaVideo; }
    void setPaisVideo(String paisVideo){ this.paisVideo = paisVideo; }
}
