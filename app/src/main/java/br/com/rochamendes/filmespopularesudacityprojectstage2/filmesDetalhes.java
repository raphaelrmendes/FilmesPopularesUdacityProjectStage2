package br.com.rochamendes.filmespopularesudacityprojectstage2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class filmesDetalhes extends AppCompatActivity {

    SharedPreferences preferencias;
    filmesViewModel FilmesViewModel;
    RecyclerView videosRecycler;
    RecyclerView reviewsRecycler;
    RecyclerView.LayoutManager videosLayout;
    RecyclerView.LayoutManager reviewsLayout;
    videosAdapter VideosAdapter;
    reviewsAdapter ReviewsAdapter;
    videos[] videoslist;
    reviews[] reviewslist;
    int idFilme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filmes_detalhes);

        TextView tituloOriginal = findViewById(R.id.tituloOriginalFilme);
        ImageView capa = findViewById(R.id.capaFilmeDetalhes);
        TextView idioma = findViewById(R.id.idioma);
        TextView sinopse = findViewById(R.id.sinopse);
        TextView data = findViewById(R.id.data);
        TextView nota = findViewById(R.id.nota);
        Intent intent = getIntent();

        idFilme = intent.getIntExtra("idFilme", 0);
        FilmesViewModel = ViewModelProviders.of(this).get(filmesViewModel.class);
        try {
            FilmesViewModel.getListaFilme(idFilme).observe(this, new Observer<List<filmes>>() {
                @Override
                public void onChanged(List<filmes> filmes) {
                    Log.i("Mensagem de Busca: ", filmes.size() + " itens encontrados com id: " + idFilme);
                    filmesDetalhes.this.setTitle(filmes.get(0).getNomeFilme());
                    tituloOriginal.setText(filmes.get(0).getNomeOriginal());
                    idioma.setText(filmes.get(0).getIdiomaFilme());
                    sinopse.setText(filmes.get(0).getSinopseFilme());
                    data.setText(filmes.get(0).getDataFilme());
                    nota.setText(String.valueOf(filmes.get(0).getAvaliacaoMedia()));
                    Bitmap imagemCapa = BitmapFactory.decodeByteArray(filmes.get(0).getCapaFilme(), 0, filmes.get(0).getCapaFilme().length);
                    capa.setImageBitmap(imagemCapa);
                    capa.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        preferencias = getSharedPreferences("ordenamento", MODE_PRIVATE);
        videosRecycler = findViewById(R.id.recyclerViewVideos);
        videosRecycler.setHasFixedSize(true);
        videosLayout = new LinearLayoutManager(this);
        videosRecycler.setLayoutManager(videosLayout);
        reviewsRecycler = findViewById(R.id.recyclerViewReviews);
        reviewsRecycler.setHasFixedSize(false);
        reviewsLayout = new LinearLayoutManager(this);
        reviewsRecycler.setLayoutManager(reviewsLayout);

        long updateIntervalo = preferencias.getLong("intervaloAtualizacao", 3600);
        long ultAtVideos = Long.parseLong(preferencias.getString("ultimaAtualizacao" + idFilme, "0"));
        if (ultAtVideos + updateIntervalo <= System.currentTimeMillis() / 1000) {
            FilmesViewModel.apagaVideo(idFilme);
            FilmesViewModel.apagaReview(idFilme);
            new buscaVideos().execute(idFilme);
            new buscaReviews().execute(idFilme);
        } else {
            FilmesViewModel.getListaVideos(idFilme).observe(this, new Observer<List<videos>>() {
                @Override
                public void onChanged(List<videos> videos) {
                    VideosAdapter = new videosAdapter(videos.toArray(new videos[videos.size()]));
                    videosRecycler.setAdapter(VideosAdapter);
                    videoslistener();
                }
            });
            FilmesViewModel.getListaReviews(idFilme).observe(this, new Observer<List<reviews>>() {
                @Override
                public void onChanged(List<reviews> reviews) {
                    ReviewsAdapter = new reviewsAdapter(reviews.toArray(new reviews[reviews.size()]));
                    reviewsRecycler.setAdapter(ReviewsAdapter);
                    reviewsListener();
                }
            });
            Toast.makeText(filmesDetalhes.this, "Dados em cache...", Toast.LENGTH_SHORT).show();
        }
    }

    public void videoslistener() {
        VideosAdapter.setOnItemClickListener(new videosAdapter.videosListener() {
            @Override
            public void videosClick(String videoKey) {
                if (!videoKey.equals("notYoutube")) {
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + videoKey));
                    try {
                        filmesDetalhes.this.startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        filmesDetalhes.this.startActivity(webIntent);
                    }
                } else {
                    Toast.makeText(filmesDetalhes.this,
                            "No momento só é possível abrir vídeos do YouTube...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void reviewsListener() {
        ReviewsAdapter.setOnItemClickListener(new reviewsAdapter.reviewsListener() {
            @Override
            public void reviewsClick(String urlReview) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlReview));
                filmesDetalhes.this.startActivity(intent);
            }
        });
    }

    public class buscaVideos extends AsyncTask<Integer, Void, videos[]> {
        @Override
        protected videos[] doInBackground(Integer... ints) {
            try {
                // https://api.themoviedb.org/3/movie/idFilme/videos?api_key=[api_key that you get on The Movies DB website]
                Uri.Builder construtorUri = new Uri.Builder();
                construtorUri.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(String.valueOf(ints[0]))
                        .appendPath("videos")
                        .appendQueryParameter("api_key", BuildConfig.API_key);
                String UrlPesquisa = construtorUri.build().toString();

                URL url = new URL(UrlPesquisa);
                URLConnection requisicao = url.openConnection();
                requisicao.connect();

                JsonParser dadosJson = new JsonParser();
                JsonElement raizJson = dadosJson.parse(new InputStreamReader((InputStream) requisicao.getContent()));
                JsonObject pagPesqJSON = raizJson.getAsJsonObject();
                JsonArray resultadoPesqJSON = pagPesqJSON.getAsJsonArray("results");

                videoslist = new videos[resultadoPesqJSON.size()];
                for (int i = 0; i < resultadoPesqJSON.size(); i++) {

                    JsonObject FilmeJson = resultadoPesqJSON.get(i).getAsJsonObject();

                    JsonPrimitive key = FilmeJson.getAsJsonPrimitive("key");
                    JsonPrimitive titulo = FilmeJson.getAsJsonPrimitive("name");
                    JsonPrimitive site = FilmeJson.getAsJsonPrimitive("site");
                    JsonPrimitive tipo = FilmeJson.getAsJsonPrimitive("type");
                    JsonPrimitive idioma = FilmeJson.getAsJsonPrimitive("iso_639_1");
                    JsonPrimitive pais = FilmeJson.getAsJsonPrimitive("iso_3166_1");

                    String hora = String.valueOf(System.currentTimeMillis() / 1000);
                    preferencias.edit().putString("ultimaAtualizacao" + ints[0], hora).apply();

                    videoslist[i] = new videos(
                            key.getAsString(),
                            ints[0],
                            titulo.getAsString(),
                            tipo.getAsString(),
                            site.getAsString(),
                            idioma.getAsString(),
                            pais.getAsString());
                }
                return videoslist;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(videos[] Videos) {
            if (Videos != null) {
                FilmesViewModel.insertVideos(Videos);
                VideosAdapter = new videosAdapter(Videos);
                videosRecycler.setAdapter(VideosAdapter);
                videoslistener();
            } else {
                Toast.makeText(filmesDetalhes.this, "Erro...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class buscaReviews extends AsyncTask<Integer, Void, reviews[]> {
        @Override
        protected reviews[] doInBackground(Integer... ints) {
            try {
                // https://api.themoviedb.org/3/movie/idFilme/reviews?api_key=[api_key that you get on The Movies DB website]
                Uri.Builder construtorUri = new Uri.Builder();
                construtorUri.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(String.valueOf(ints[0]))
                        .appendPath("reviews")
                        .appendQueryParameter("api_key", BuildConfig.API_key);
                String UrlPesquisa = construtorUri.build().toString();

                URL url = new URL(UrlPesquisa);
                URLConnection requisicao = url.openConnection();
                requisicao.connect();

                JsonParser dadosJson = new JsonParser();
                JsonElement raizJson = dadosJson.parse(new InputStreamReader((InputStream) requisicao.getContent()));
                JsonObject pagPesqJSON = raizJson.getAsJsonObject();
                JsonArray resultadoPesqJSON = pagPesqJSON.getAsJsonArray("results");

                reviewslist = new reviews[resultadoPesqJSON.size()];
                for (int i = 0; i < resultadoPesqJSON.size(); i++) {

                    JsonObject FilmeJson = resultadoPesqJSON.get(i).getAsJsonObject();

                    JsonPrimitive idReview = FilmeJson.getAsJsonPrimitive("id");
                    JsonPrimitive autor = FilmeJson.getAsJsonPrimitive("author");
                    JsonPrimitive conteudo = FilmeJson.getAsJsonPrimitive("content");
                    JsonPrimitive urlReview = FilmeJson.getAsJsonPrimitive("url");

                    reviewslist[i] = new reviews(
                            idReview.getAsString(),
                            ints[0],
                            autor.getAsString(),
                            conteudo.getAsString(),
                            urlReview.getAsString());
                }
                return reviewslist;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(reviews[] Reviews) {
            if (Reviews != null) {
                FilmesViewModel.insertReviews(Reviews);
                ReviewsAdapter = new reviewsAdapter(Reviews);
                reviewsRecycler.setAdapter(ReviewsAdapter);
                reviewsListener();
            } else {
                Toast.makeText(filmesDetalhes.this, "Erro...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
