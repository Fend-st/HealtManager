package FernandoDiaz.calendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanager.GestorBD;
import com.example.healthmanager.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Diálogo que muestra una lista de eventos para un día seleccionado.
 * Carga los datos reales de la base de datos GestorBD mediante una consulta JOIN manual.
 */
public class EventListDialogFragment extends DialogFragment {

    private static final String ARG_DATE = "arg_date";
    private CalendarDay selectedDate;
    private OnEventActionListener listener;

    public interface OnEventActionListener {
        void onEditEvent(Event event);
    }

    public static EventListDialogFragment newInstance(CalendarDay date) {
        EventListDialogFragment fragment = new EventListDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnEventActionListener(OnEventActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDate = getArguments().getParcelable(ARG_DATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_event_list, container, false);

        TextView textViewTitle = view.findViewById(R.id.textViewListTitle);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents);
        Button buttonClose = view.findViewById(R.id.buttonCloseList);

        textViewTitle.setText(String.format(Locale.getDefault(), "Eventos del %02d/%02d/%04d", 
                selectedDate.getDay(), selectedDate.getMonth() + 1, selectedDate.getYear()));

        // Carga los eventos reales de la base de datos
        List<Event> events = loadEventsFromDatabase();

        EventsAdapter adapter = new EventsAdapter(events, event -> {
            if (listener != null) {
                listener.onEditEvent(event);
            }
            dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        buttonClose.setOnClickListener(v -> dismiss());

        return view;
    }

    /**
     * Consulta la base de datos para obtener los eventos asociados a la fecha seleccionada.
     * Utiliza una consulta manual para evitar modificar GestorBD.
     */
    private List<Event> loadEventsFromDatabase() {
        List<Event> events = new ArrayList<>();
        GestorBD gbd = new GestorBD(getContext());
        SQLiteDatabase db = gbd.getReadableDatabase();

        // Formato YYYY-MM-DD para coincidir con la lógica de guardado
        String dateFormatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", 
                selectedDate.getYear(), selectedDate.getMonth() + 1, selectedDate.getDay());

        // Consulta manual con JOIN entre TABLA_EVENTO y TABLA_DIA
        String query = "SELECT e.* FROM " + GestorBD.TABLA_EVENTO + " e " +
                "INNER JOIN " + GestorBD.TABLA_DIA + " d ON e." + GestorBD.EVENTO_ID + " = d." + GestorBD.DIA_ID_EVENTO + " " +
                "WHERE d." + GestorBD.DIA_FECHA + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{dateFormatted});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.EVENTO_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.EVENTO_NOMBRE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.EVENTO_DESCRIPCION));
                    events.add(new Event(id, title, description, dateFormatted));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return events;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
