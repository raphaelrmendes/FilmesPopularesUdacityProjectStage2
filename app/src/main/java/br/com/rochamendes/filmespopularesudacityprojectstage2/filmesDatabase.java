package br.com.rochamendes.filmespopularesudacityprojectstage2;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {filmes.class, videos.class, reviews.class}, version=1, exportSchema = false)
public abstract class filmesDatabase extends RoomDatabase {
    private static filmesDatabase instance;
    public abstract filmesDAO FilmesDAO();
    public static synchronized filmesDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    filmesDatabase.class, "filmes_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private filmesDAO FilmesDAO;
        private PopulateDbAsyncTask(filmesDatabase db){
            FilmesDAO = db.FilmesDAO();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
