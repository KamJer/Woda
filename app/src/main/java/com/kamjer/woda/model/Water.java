    package com.kamjer.woda.model;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.room.Entity;
    import androidx.room.ForeignKey;
    import androidx.room.PrimaryKey;

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
                    parentColumns = "date",
                    childColumns = "waterDayId",
                    onDelete = ForeignKey.CASCADE
            )})
    public class Water implements Serializable {
        @PrimaryKey(autoGenerate = true)
        private long id;
        private Integer waterDrank;
        private long typeId;

        private LocalDate waterDayId;

        public Water() {
        }

        public Water(int waterDrank, Type type, WaterDay waterDay) {
            this.waterDrank = waterDrank;
            this.typeId = type.getId();
            this.waterDayId = waterDay.getDate();
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

        public LocalDate getWaterDayId() {
            return waterDayId;
        }

        public void setWaterDayId(LocalDate waterDayId) {
            this.waterDayId = waterDayId;
        }

        @NonNull
        @Override
        public String toString() {
            return getId() + ", " + getWaterDrank() + ", " + getTypeId() + ", " + getWaterDayId();
        }

        @Override
        public int hashCode() {
            return (int) id;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof Water) {
                return false;
            }
            if (obj == null) {
                return false;
            }
            return obj.hashCode() == this.hashCode();
        }
    }
