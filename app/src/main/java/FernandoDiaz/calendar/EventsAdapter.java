package FernandoDiaz.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanager.R;

import java.util.List;

/**
 * Adaptador para el RecyclerView que muestra la lista de eventos.
 * Vincula los datos de la clase {@link Event} con los elementos de la interfaz de usuario.
 * @author Fernando diaz
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<Event> eventList;
    private OnEventClickListener listener;

    /**
     * Interfaz para capturar clics en los elementos de la lista.
     */
    public interface OnEventClickListener {
        /**
         * Se llama cuando se hace clic en un evento.
         *
         * @param event El evento seleccionado.
         */
        void onEventClick(Event event);
    }

    /**
     * Constructor del adaptador.
     *
     * @param eventList Lista de eventos a mostrar.
     * @param listener  Listener para eventos de clic.
     */
    public EventsAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    /**
     * Crea una nueva instancia de {@link EventViewHolder} inflando el diseño del ítem.
     *
     * @param parent   El ViewGroup padre.
     * @param viewType El tipo de vista (no utilizado en este caso).
     * @return Un nuevo ViewHolder.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Vincula los datos de un evento en una posición específica con las vistas del ViewHolder.
     *
     * @param holder   El ViewHolder que debe ser actualizado.
     * @param position La posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewTitle.setText(event.getTitle());
        holder.textViewDescription.setText(event.getDescription());
        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    /**
     * Devuelve el tamaño total de la lista de datos.
     *
     * @return Número de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * Clase interna que contiene las referencias a las vistas de cada elemento de la lista.
     */
    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista raíz del elemento de la lista.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewEventTitle);
            textViewDescription = itemView.findViewById(R.id.textViewEventDescription);
        }
    }
}
