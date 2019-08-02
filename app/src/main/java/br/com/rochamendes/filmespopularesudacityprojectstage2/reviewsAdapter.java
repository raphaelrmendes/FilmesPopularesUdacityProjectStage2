package br.com.rochamendes.filmespopularesudacityprojectstage2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class reviewsAdapter extends RecyclerView.Adapter<reviewsAdapter.reviewsViewHolder> {

    private reviewsListener ReviewsListener;
    private reviews[] reviewsList;

    reviewsAdapter(reviews[] Reviews) {
        reviewsList = Reviews;
    }

    @NonNull
    @Override
    public reviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reviews_item_recyclerview, parent, false);
        return new reviewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final reviewsViewHolder holder, int position) {
        try {
            reviews review = reviewsList[position];
            holder.mAutor.setText(review.getAutor());
            holder.mConteudo.setText(review.getConteudo());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Mensagem de Erro", "Deu pau aqui no onBindViewHolder");
        }
    }

    @Override
    public int getItemCount() {
        if (reviewsList == null) return 0;
        return reviewsList.length;
    }

    class reviewsViewHolder extends RecyclerView.ViewHolder {
        TextView mAutor;
        TextView mConteudo;

        public reviewsViewHolder(View itemView) {
            super(itemView);
            mAutor = itemView.findViewById(R.id.recycler_reviews_etiqueta_autor);
            mConteudo = itemView.findViewById(R.id.recycler_reviews_etiqueta_conteudo);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (ReviewsListener != null && pos != RecyclerView.NO_POSITION) {
                    ReviewsListener.reviewsClick(reviewsList[pos].getUrl());
                }
            });
        }
    }

    public interface reviewsListener {
        void reviewsClick(String urlReview);
    }

    public void setOnItemClickListener(reviewsAdapter.reviewsListener ReviewsListener) {
        this.ReviewsListener = ReviewsListener;
    }
}