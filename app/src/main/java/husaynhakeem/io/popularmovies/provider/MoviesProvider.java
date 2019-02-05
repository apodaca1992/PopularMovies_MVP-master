package husaynhakeem.io.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import husaynhakeem.io.popularmovies.database.FavoriteMovieTable;
import husaynhakeem.io.popularmovies.database.MovieDatabase;

import static android.R.attr.id;
import static husaynhakeem.io.popularmovies.provider.MoviesContract.CONTENT_AUTHORITY;
import static husaynhakeem.io.popularmovies.provider.MoviesContract.FAVORITE_MOVIES_PATH;

/**
 * Created by husaynhakeem on 6/27/17.
 */

public class MoviesProvider extends ContentProvider {


    private static final int CODE_FAVORITE_MOVIES = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDatabase movieDb;


    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, FAVORITE_MOVIES_PATH, CODE_FAVORITE_MOVIES);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {

        movieDb = new MovieDatabase(getContext());
        return true;
    }


    @Nullable
    @Override
    public synchronized Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int match = sUriMatcher.match(uri);
        Cursor cursor = null;

        switch (match) {

            case CODE_FAVORITE_MOVIES:
                cursor = query(FavoriteMovieTable.TABLE_NAME, projection, selection, selectionArgs, sortOrder);
                break;

            default:
                throw new RuntimeException("Query: Undefined query uri" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private synchronized Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = movieDb.getReadableDatabase();
        return db.query(tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case CODE_FAVORITE_MOVIES:
                returnUri = insert(uri, FavoriteMovieTable.TABLE_NAME, values);
                break;

            default:
                throw new RuntimeException("Insert: Undefined query uri" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private Uri insert(Uri uri, String tableName, ContentValues values) {

        SQLiteDatabase db = movieDb.getWritableDatabase();
        long popularId = db.insert(tableName, null, values);

        if (popularId >= 0)
            return ContentUris.withAppendedId(uri, id);
        else
            throw new SQLException("Failed to insert row into " + uri);
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        int numberOfDeletedRows;

        switch (match) {

            case CODE_FAVORITE_MOVIES:
                numberOfDeletedRows = delete(FavoriteMovieTable.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new RuntimeException("Delete: Undefined query uri " + uri);
        }

        if (numberOfDeletedRows > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return numberOfDeletedRows;
    }

    private int delete(String tableName, String selection, String[] selectionArgs) {
        SQLiteDatabase db = movieDb.getWritableDatabase();
        return db.delete(tableName, selection, selectionArgs);
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
