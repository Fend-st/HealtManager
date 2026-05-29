package FernandoDiaz.calendar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modelo de datos que representa un evento en el calendario.
 * Esta clase implementa Parcelable para permitir que los objetos de tipo Event 
 * se pasen entre componentes de la aplicación (como Fragments o Activities).
 * @author Fernando diaz
 */
public class Event implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String date; // Puede ser un String o CalendarDay.toString()

    /**
     * Constructor para crear un nuevo evento.
     *
     * @param id          Identificador único del evento.
     * @param title       Título del evento.
     * @param description Descripción detallada del evento.
     * @param date        Fecha del evento en formato String (ej: YYYY-MM-DD).
     */
    public Event(String id, String title, String description, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    /**
     * Constructor utilizado por el sistema de parcelación para reconstruir el objeto.
     *
     * @param in El objeto Parcel que contiene los datos serializados.
     */
    protected Event(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        date = in.readString();
    }

    /**
     * Creador estático para la interfaz Parcelable.
     */
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

    /**
     * @return El identificador del evento.
     */
    public String getId() { return id; }

    /**
     * @return El título del evento.
     */
    public String getTitle() { return title; }

    /**
     * @return La descripción del evento.
     */
    public String getDescription() { return description; }

    /**
     * @return La fecha del evento como cadena de texto.
     */
    public String getDate() { return date; }

    /**
     * Describe el tipo de objetos especiales contenidos en la representación parcelada.
     *
     * @return 0 para valores estándar.
     */
    @Override
    public int describeContents() { return 0; }

    /**
     * Escribe los datos del objeto en un Parcel.
     *
     * @param dest  El Parcel donde se debe escribir el objeto.
     * @param flags Indicadores adicionales sobre cómo se debe escribir el objeto.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
    }
}
