package edu.uestc.carelink.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DatabaseEntity {

    @PrimaryKey
    public String date;
    @ColumnInfo
    public String jsonText;
}
