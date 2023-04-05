package edu.uestc.carelink.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "data_record")
public class DatabaseEntity {

    @PrimaryKey
    @NonNull
    public String date;
    @ColumnInfo
    public String jsonText;
}
