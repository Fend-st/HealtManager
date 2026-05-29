package FernandoDiaz.calendar;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Modelo de datos que representa un evento en el calendario.
 * Esta clase implementa Parcelable para permitir que los objetos de tipo Event 
 * se pasen entre componentes de la aplicación (como Fragments o Activities).
 */
public class Event implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String date; // Puede ser un String o CalendarDay.toString()

    public Event(String id, String title, String description, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    protected Event(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        date = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
    }
}
