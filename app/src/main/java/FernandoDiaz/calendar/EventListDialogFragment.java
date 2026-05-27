package FernandoDiaz.calendar;

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

import com.example.healthmanager.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;

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

        textViewTitle.setText("Eventos del " + selectedDate.getDay() + "/" + (selectedDate.getMonth() + 1));

        // PLACEHOLDER: Aquí se debería  cargar los eventos reales de la base de datos para este día.
        List<Event> dummyEvents = new ArrayList<>();
        dummyEvents.add(new Event("1", "Evento Ejemplo 1", "Descripción larga del primer evento", selectedDate.toString()));
        dummyEvents.add(new Event("2", "Evento Ejemplo 2", "Descripción del segundo evento", selectedDate.toString()));

        EventsAdapter adapter = new EventsAdapter(dummyEvents, event -> {
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

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
