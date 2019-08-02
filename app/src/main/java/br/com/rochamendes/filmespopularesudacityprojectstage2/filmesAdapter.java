package br.com.rochamendes.filmespopularesudacityprojectstage2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class filmesAdapter extends ListAdapter<filmes, filmesAdapter.filmesViewHolder> {
    private filmesListener FilmesListener;

    public filmesAdapter() {
        super(DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<filmes> DIFF_CALLBACK = new DiffUtil.ItemCallback<filmes>() {
        @Override
        public boolean areItemsTheSame(@NonNull filmes oldItem, @NonNull filmes newItem) {
            return oldItem.getIdFilme() == newItem.getIdFilme();
        }

        @Override
        public boolean areContentsTheSame(@NonNull filmes oldItem, @NonNull filmes newItem) {
            return oldItem.isFavorito() == newItem.isFavorito();
        }
    };

    @NonNull
    @Override
    public filmesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filme_item_recyclerview, parent, false);
        return new filmesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull filmesAdapter.filmesViewHolder holder, int position) {
        try {
            filmes filme = getItem(position);
            Bitmap imagemCapa = BitmapFactory.decodeByteArray(filme.getCapaFilme(),0,filme.getCapaFilme().length);
            holder.mCapaFilme.setImageBitmap(imagemCapa);
            holder.mTituloFilme.setText(filme.getNomeFilme());
            holder.mPop.setText(String.valueOf(filme.getPopularidade()));
            holder.mNot.setText(String.valueOf(filme.getAvaliacaoMedia()));
            holder.mCont.setText(String.valueOf(filme.getContagemAvaliacao()));
            if (filme.isFavorito()) {
                holder.mEstrelaLigada.setVisibility(View.VISIBLE);
                holder.mEstrela.setVisibility(View.GONE);
            } else {
                holder.mEstrelaLigada.setVisibility(View.GONE);
                holder.mEstrela.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Mensagem de Erro", "Deu pau aqui no onBindViewHolder");
        }
    }

    class filmesViewHolder extends RecyclerView.ViewHolder {
        ImageView mCapaFilme;
        TextView mTituloFilme;
        ImageView mEstrela;
        ImageView mEstrelaLigada;
        TextView mPop;
        TextView mNot;
        TextView mCont;

        public filmesViewHolder(View itemView) {
            super(itemView);
            mCapaFilme = itemView.findViewById(R.id.miniaturaCapaFilme_mainActivity);
            mTituloFilme = itemView.findViewById(R.id.tituloFilme_mainActivity);
            mEstrela = itemView.findViewById(R.id.estrela_botao_favorito);
            mEstrelaLigada = itemView.findViewById(R.id.estrela_botao_favorito_checked);
            mPop = itemView.findViewById(R.id.layout_popularidade);
            mNot = itemView.findViewById(R.id.layout_nota);
            mCont = itemView.findViewById(R.id.layout_cont_votos);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (FilmesListener != null && pos != RecyclerView.NO_POSITION){
                    FilmesListener.filmesClick(getItem(pos).getIdFilme());
                }
            });
            mEstrela.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (FilmesListener != null && pos != RecyclerView.NO_POSITION){
                    FilmesListener.estrelaClickFavoritar(getItem(pos));
                }
            });
            mEstrelaLigada.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (FilmesListener != null && pos != RecyclerView.NO_POSITION){
                    FilmesListener.estrelaClickDesmarcar(getItem(pos));
                }
            });
        }
    }

    public interface filmesListener {
        void filmesClick(int idFilme);
        void estrelaClickFavoritar(filmes filmes);
        void estrelaClickDesmarcar(filmes filmes);
    }

    public void setOnItemClickListener(filmesListener FilmesListener){
        this.FilmesListener = FilmesListener;
    }
}