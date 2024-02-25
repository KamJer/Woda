    package com.kamjer.woda.model;

    import androidx.annotation.NonNull;
    import androidx.room.ColumnInfo;
    import androidx.room.Entity;
    import androidx.room.ForeignKey;
    import androidx.room.Insert;
    import androidx.room.OnConflictStrategy;
    import androidx.room.PrimaryKey;

    import org.jetbrains.annotations.NotNull;

    import java.io.Serializable;
    import java.time.LocalDate;

    @Entity(tableName = "water",
            foreignKeys = {
            @ForeignKey(
                    entity = Type.class,
                    parentColumns = "id",
                    childColumns = "typeId",
                    onDelete = ForeignKey.CASCADE)
            ,@ForeignKey(
                    entity = WaterDay.class,
                    parentColumns = "id",
                    childColumns = "waterDayId",
                    onDelete = ForeignKey.CASCADE
            )})
    public class Water implements Serializable {
        @PrimaryKey(autoGenerate = true)
        private long id;
        private Integer waterDrank;
        private long typeId;

        private long waterDayId;

        public Water() {
        }

        public Water(int waterDrank, Type type, WaterDay waterDay) {
            this.waterDrank = waterDrank;
            this.typeId = type.getId();
            this.waterDayId = waterDay.getId();
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public Integer getWaterDrank() {
            return waterDrank;
        }

        public void setWaterDrank(Integer waterDrank) {
            this.waterDrank = waterDrank;
        }

        public long getTypeId() {
            return typeId;
        }

        public void setTypeId(long typeId) {
            this.typeId = typeId;
        }

        public long getWaterDayId() {
            return waterDayId;
        }

        public void setWaterDayId(long waterDayId) {
            this.waterDayId = waterDayId;
        }
    }
