package com.backflip270bb.android.tobuylist4ics.model;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class ToBuyListItemsDBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "tobuylist.db";
    private static final int DATABASE_VERSION = 1;

    public ToBuyListItemsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            // エンティティを指定してcreate tableします
            TableUtils.createTable(connectionSource, ToBuyItem.class);
            TableUtils.createTable(connectionSource, PlaceItem.class);
        } catch (SQLException e) {
            Log.e(ToBuyListItemsDBHelper.class.getName(), "データベースを作成できませんでした", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        // DBのアップグレード処理（今回は割愛）
    }
}
