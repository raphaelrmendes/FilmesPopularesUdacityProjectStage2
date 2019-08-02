package br.com.rochamendes.filmespopularesudacityprojectstage2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class videosAdapter extends RecyclerView.Adapter<videosAdapter.videosViewHolder> {

    private videosListener VideosListener;
    private videos[] videosList;

    videosAdapter(videos[] Videos) {
        videosList = Videos;
    }

    @NonNull
    @Override
    public videosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.videos_item_recyclerview, parent, false);
        return new videosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final videosViewHolder holder, int position) {
        try {
            videos video = videosList[position];
            holder.mTitulo.setText(video.getVideoNome());
            holder.mSite.setText(video.getVideoSite());
            holder.mTipo.setText(video.getVideoTipo());
            holder.mIdioma.setText(video.getIdiomaVideo());
            holder.mPais.setText(video.getPaisVideo());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Mensagem de Erro", "Deu pau aqui no onBindViewHolder");
        }
    }

    @Override
    public int getItemCount() {
        if (videosList == null) return 0;
        return videosList.length;
    }

    class videosViewHolder extends RecyclerView.ViewHolder {
        TextView mTitulo;
        TextView mSite;
        TextView mTipo;
        TextView mIdioma;
        TextView mPais;

        public videosViewHolder(View itemView) {
            super(itemView);
            mTitulo = itemView.findViewById(R.id.recycler_videos_etiqueta_nome);
            mSite = itemView.findViewById(R.id.recycler_videos_etiqueta_site);
            mTipo = itemView.findViewById(R.id.recycler_videos_etiqueta_tipo);
            mIdioma = itemView.findViewById(R.id.recycler_videos_etiqueta_idioma);
            mPais = itemView.findViewById(R.id.recycler_videos_etiqueta_pais);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (VideosListener != null && pos != RecyclerView.NO_POSITION &&
                    videosList[pos].getVideoSite().equals("YouTube")) {
                    VideosListener.videosClick(videosList[pos].getVideoKey());
                } else {
                    VideosListener.videosClick("notYoutube");
                }
            });
        }
    }

    public interface videosListener {
        void videosClick(String videoKey);
    }

    public void setOnItemClickListener(videosAdapter.videosListener VideosListener) {
        this.VideosListener = VideosListener;
    }
}