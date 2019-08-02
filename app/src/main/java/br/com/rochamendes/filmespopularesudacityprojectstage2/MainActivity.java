package br.com.rochamendes.filmespopularesudacityprojectstage2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private filmesViewModel FilmesViewModel;
    RecyclerView filmesRecycler;
    RecyclerView.LayoutManager filmesLayout;
    filmesAdapter FilmesAdapter;
    SharedPreferences preferencias;
    String ordenamento;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Mensagem", "onCreate");
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        FilmesViewModel = ViewModelProviders.of(this).get(filmesViewModel.class);
        preferencias = getSharedPreferences("ordenamento", MODE_PRIVATE);
        preferencias.registerOnSharedPreferenceChangeListener(this);
        ordenamento = preferencias.getString("ordenamento", "popular");
        filmesRecycler = findViewById(R.id.filmesRecyclerView);
        filmesRecycler.setHasFixedSize(false);
        filmesLayout = new LinearLayoutManager(this);
        filmesRecycler.setLayoutManager(filmesLayout);
        FilmesAdapter = new filmesAdapter();
        filmesRecycler.setAdapter(FilmesAdapter);
        info = findViewById(R.id.info);
        onSharedPreferenceChanged(preferencias, "ordenamento");

        FilmesAdapter.setOnItemClickListener(new filmesAdapter.filmesListener() {
            @Override
            public void filmesClick(int idFilme) {
                Intent intent = new Intent(MainActivity.this, filmesDetalhes.class);
                try {
                    intent.putExtra("idFilme", idFilme);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Deu ruim!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void estrelaClickFavoritar(filmes Filme) {
                preferencias.edit().putBoolean(String.valueOf(Filme.getIdFilme()), true).apply();
                Filme.setFavorito(true);
                FilmesViewModel.updateFavorito(Filme);
                FilmesAdapter.notifyDataSetChanged();
            }

            @Override
            public void estrelaClickDesmarcar(filmes Filme) {
                preferencias.edit().putBoolean(String.valueOf(Filme.getIdFilme()), false).apply();
                Filme.setFavorito(false);
                FilmesViewModel.updateFavorito(Filme);
                FilmesAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Mensagem", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Mensagem", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Mensagem", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferencias.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("ordenamento")) {
            ordenamento = preferencias.getString("ordenamento", "popular");
            new atualizaFilmes().execute(ordenamento);
        }
    }

    private void ordem() {
        ordenamento = preferencias.getString("ordenamento", "popular");
        Log.i("Mensagem", "Passou por Ordem...");
        if (ordenamento.equals("popular")) {
            Log.i("Mensagem", "Passou por Popular...");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Mais Populares");
            FilmesViewModel.getListarFilmesFavoritos().removeObservers(this);
            FilmesViewModel.getListarFilmesBemAvaliados().removeObservers(this);
            FilmesViewModel.getListarFilmesPopulares().observe(this, filmes -> FilmesAdapter.submitList(filmes));
            long ultAtPopulares = Long.parseLong(preferencias.getString("ultimaAtualizacaoPopulares", "0"))*1000;
            Date ultAt = new Date(ultAtPopulares);
            String titulo = getString(R.string.ult_atualizacao);
            info.setText(titulo + ultAt.toString());
        }
        if (ordenamento.equals("top_rated")) {
            Log.i("Mensagem", "Passou por Bem Avaliados...");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Maiores Notas");
            FilmesViewModel.getListarFilmesFavoritos().removeObservers(this);
            FilmesViewModel.getListarFilmesPopulares().removeObservers(this);
            FilmesViewModel.getListarFilmesBemAvaliados().observe(this, filmes -> FilmesAdapter.submitList(filmes));
            long ultAtBemAvaliados = Long.parseLong(preferencias.getString("ultimaAtualizacaoBemAvaliados", "0"))*1000;
            Date ultAt = new Date(ultAtBemAvaliados);
            String titulo = getString(R.string.ult_atualizacao);
            info.setText(titulo + ultAt.toString());
        }
        if (ordenamento.equals("favoritos")) {
            Log.i("Mensagem", "Passou por Favoritos...");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Meus Favoritos");
            FilmesViewModel.getListarFilmesPopulares().removeObservers(this);
            FilmesViewModel.getListarFilmesBemAvaliados().removeObservers(this);
            FilmesViewModel.getListarFilmesFavoritos().observe(this, filmes -> FilmesAdapter.submitList(filmes));
        }
    }

    public class atualizaFilmes extends AsyncTask<String, Void, filmes[]> {
        protected void onPreExecute() {
            super.onPreExecute();
            visibilide(true);
        }

        protected filmes[] doInBackground(String... params) {
            long updateIntervalo = preferencias.getLong("intervaloAtualizacao", 3600);
            long ultAtPopulares = Long.parseLong(preferencias.getString("ultimaAtualizacaoPopulares", "0"));
            long ultAtBemAvaliados = Long.parseLong(preferencias.getString("ultimaAtualizacaoBemAvaliados", "0"));
            if (params[0].equals("popular") && ultAtPopulares + updateIntervalo <= System.currentTimeMillis() / 1000
                    || params[0].equals("top_rated") && ultAtBemAvaliados + updateIntervalo <= System.currentTimeMillis() / 1000) {
                try {
                    // https://api.themoviedb.org/3/movie/popular?api_key=[api_key that you get on The Movies DB website]
                    Uri.Builder construtorUri = new Uri.Builder();
                    construtorUri.scheme("https")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("movie")
                            .appendPath(params[0])
                            .appendQueryParameter("api_key", BuildConfig.API_key);
                    String UrlPesquisa = construtorUri.build().toString();

                    URL url = new URL(UrlPesquisa);
                    URLConnection requisicao = url.openConnection();
                    requisicao.connect();

                    JsonParser dadosJson = new JsonParser();
                    JsonElement raizJson = dadosJson.parse(new InputStreamReader((InputStream) requisicao.getContent()));
                    JsonObject pagPesqJSON = raizJson.getAsJsonObject();
                    JsonArray resultadoPesqJSON = pagPesqJSON.getAsJsonArray("results");

                    filmes[] filmesList = new filmes[resultadoPesqJSON.size()];
                    for (int i = 0; i < resultadoPesqJSON.size(); i++) {

                        JsonObject FilmeJson = resultadoPesqJSON.get(i).getAsJsonObject();

                        JsonPrimitive id = FilmeJson.getAsJsonPrimitive("id");
                        JsonPrimitive titulo = FilmeJson.getAsJsonPrimitive("title");
                        JsonPrimitive tituloOriginal = FilmeJson.getAsJsonPrimitive("original_title");
                        JsonPrimitive capa = FilmeJson.getAsJsonPrimitive("poster_path");
                        JsonPrimitive idioma = FilmeJson.getAsJsonPrimitive("original_language");
                        JsonPrimitive sinopse = FilmeJson.getAsJsonPrimitive("overview");
                        JsonPrimitive lancamento = FilmeJson.getAsJsonPrimitive("release_date");
                        JsonPrimitive avaliacao = FilmeJson.getAsJsonPrimitive("vote_average");
                        JsonPrimitive contagem = FilmeJson.getAsJsonPrimitive("vote_count");
                        JsonPrimitive popularidade = FilmeJson.getAsJsonPrimitive("popularity");
                        int convert_popularidade = Integer.valueOf(
                                popularidade.getAsString().replace(".", ""));

                        boolean popular = false;
                        boolean bemAvaliado = false;
                        boolean favorito = false;
                        if (params[0].equals("popular")) {
                            popular = true;
                            String hora = String.valueOf(System.currentTimeMillis() / 1000);
                            preferencias.edit().putString("ultimaAtualizacaoPopulares", hora).apply();
                        }
                        if (params[0].equals("top_rated")) {
                            bemAvaliado = true;
                            String hora = String.valueOf(System.currentTimeMillis() / 1000);
                            preferencias.edit().putString("ultimaAtualizacaoBemAvaliados", hora).apply();
                        }
                        if (preferencias.getBoolean(id.getAsString(), false)) {
                            favorito = true;
                        }
                        // Final webaddress should look like "https://image.tmdb.org/t/p/w185/imageID.jpg"
                        Uri.Builder construtorUriPhoto = new Uri.Builder();
                        construtorUriPhoto.scheme("https")
                                .authority("image.tmdb.org")
                                .appendPath("t")
                                .appendPath("p")
                                .appendPath("w342")
                                .appendPath(capa.getAsString().replace("/", ""));
                        String UrlPesquisaPhoto = construtorUriPhoto.build().toString();
                        URL urlPhoto = new URL(UrlPesquisaPhoto);
                        Bitmap imagemCapaBitmap = BitmapFactory.decodeStream(urlPhoto.openConnection().getInputStream());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imagemCapaBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                        imagemCapaBitmap.recycle();
                        byte[] imagemCapa = stream.toByteArray();

                        filmesList[i] = new filmes(
                                id.getAsInt(),
                                titulo.getAsString(),
                                tituloOriginal.getAsString(),
                                imagemCapa,
                                idioma.getAsString(),
                                sinopse.getAsString(),
                                lancamento.getAsString(),
                                avaliacao.getAsFloat(),
                                contagem.getAsInt(),
                                convert_popularidade,
                                popular,
                                bemAvaliado,
                                favorito);
                    }
                    return filmesList;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        protected void onPostExecute(filmes[] Filmes) {
            if (Filmes != null) {
                FilmesViewModel.insertFilmes(Filmes);
            } else {
                //Toast.makeText(MainActivity.this, "Filmes armazenados em cache...", Toast.LENGTH_SHORT).show();
            }
            visibilide(false);
            ordem();
        }
    }

    private void visibilide(Boolean Visibilidade) {   //referente a barra de progresso
        ProgressBar carregamento = findViewById(R.id.barraProgresso);
        TextView txtCarregamento = findViewById(R.id.textoCarregamento);
        if (Visibilidade) {
            carregamento.setVisibility(View.VISIBLE);
            txtCarregamento.setVisibility(View.VISIBLE);
            filmesRecycler.setVisibility(View.GONE);
        } else {
            carregamento.setVisibility(View.GONE);
            txtCarregamento.setVisibility(View.GONE);
            filmesRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void apagarFavoritos() {
        try {
            FilmesViewModel.getListarFilmesFavoritos().observe(MainActivity.this, new Observer<List<filmes>>() {
                @Override
                public void onChanged(List<filmes> filmes) {
                    if (filmes != null) {
                        for (int i = 0; i < filmes.size(); i++) {
                            preferencias.edit().putBoolean(String.valueOf(filmes.get(i).getIdFilme()), false).apply();
                            filmes.get(i).setFavorito(false);
                            FilmesViewModel.updateFavorito(filmes.get(i));
                        }
                    }
                    FilmesAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Deu certo não...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ordemExibicao) {
            AlertDialog.Builder construtor = new AlertDialog.Builder(this);
            final SharedPreferences.Editor editor = preferencias.edit();
            int escolha;
            ordenamento = preferencias.getString("ordenamento", "popular");
            if (ordenamento.equals("popular")) escolha = 0;
            else if (ordenamento.equals("top_rated")) escolha = 1;
            else if (ordenamento.equals("favoritos")) escolha = 2;
            else escolha = 0;
            construtor.setTitle("Ordem dos resultados");
            construtor.setSingleChoiceItems(new String[]{
                            "Mais populares", "Melhor Classificação", "Favoritos"}, escolha,
                    (dialog, selecionado) -> {
                        if (selecionado == 0) editor.putString("ordenamento", "popular");
                        if (selecionado == 1) editor.putString("ordenamento", "top_rated");
                        if (selecionado == 2) editor.putString("ordenamento", "favoritos");
                    });
            construtor.setPositiveButton("Salvar", (dialog, which) -> editor.apply());
            construtor.setNegativeButton("Cancelar", (dialog, which) -> {
            });
            construtor.setOnDismissListener(dialog -> {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
            });
            AlertDialog dialog = construtor.create();
            dialog.show();
        }

        if (id == R.id.dadosOffline) {
            AlertDialog.Builder construtor = new AlertDialog.Builder(MainActivity.this);
            String[] opcoesApagar = new String[]{"Apagar dados offline", "Apagar todos os favoritos"};
            final boolean[] opcoesApagarSelecionadas = new boolean[]{false, false};
            construtor.setMultiChoiceItems(opcoesApagar, opcoesApagarSelecionadas, (dialog, which, isChecked) -> {
                opcoesApagarSelecionadas[which] = isChecked;
            });
            construtor.setTitle("Dados offline");
            construtor.setPositiveButton("Ok", (dialog, which) -> {
                if (opcoesApagarSelecionadas[0]) {
                    FilmesViewModel.apagaDadosOffline();
                    FilmesViewModel.apagaVideos();
                    FilmesViewModel.apagaReviews();
                }
                if (opcoesApagarSelecionadas[1]) {
                    apagarFavoritos();
                }
            });
            construtor.setNeutralButton("Cancelar", (dialog, which) -> {
            });
            AlertDialog dialog = construtor.create();
            dialog.show();
        }

        if (id == R.id.intervaloAtualizacao) {
            AlertDialog.Builder construtor = new AlertDialog.Builder(this);
            final SharedPreferences.Editor editor = preferencias.edit();
            int escolha;
            long intervalo = preferencias.getLong("intervaloAtualizacao", 3600);
            if (intervalo == 0) escolha = 0;
            else if (intervalo == 60) escolha = 1;
            else if (intervalo == 3600) escolha = 2;
            else if (intervalo == 86400) escolha = 3;
            else escolha = 0;
            construtor.setTitle("Intervalo de Atualização");
            construtor.setSingleChoiceItems(new String[]{
                            "Atualizar sempre", "Mínimo 1 minuto", "Mínimo 1 hora", "Mínimo 1 dia"}, escolha,
                    (dialog, selecionado) -> {
                        if (selecionado == 0) editor.putLong("intervaloAtualizacao", 0);
                        if (selecionado == 1) editor.putLong("intervaloAtualizacao", 60);
                        if (selecionado == 2) editor.putLong("intervaloAtualizacao", 3600);
                        if (selecionado == 3) editor.putLong("intervaloAtualizacao", 86400);
                    });
            construtor.setPositiveButton("Salvar", (dialog, which) -> editor.apply());
            construtor.setNeutralButton("Cancelar", (dialog, which) -> {
            });
            construtor.setOnDismissListener(dialog -> {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
            });
            AlertDialog dialog = construtor.create();
            dialog.show();
        }

        if (id == R.id.atualizar) {
            final SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("ultimaAtualizacaoPopulares", "0");
            editor.putString("ultimaAtualizacaoBemAvaliados", "0");
            editor.apply();
            String ordem = preferencias.getString("ordenamento", "popular");
            new atualizaFilmes().execute(ordem);
        }
        return super.onOptionsItemSelected(item);
    }
}