package FernandoDiaz.calendar;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthmanager.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

/**
 * Diálogo para la creación o edición de un evento individual.
 * Permite al usuario introducir un título y una descripción para una fecha específica.
 */
public class EventDialogFragment extends DialogFragment {

    private static final String ARG_DATE = "arg_date";
    private static final String ARG_ID = "arg_id";
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_DESC = "arg_desc";

    private CalendarDay selectedDate;
    private String eventId;
    private String initialTitle;
    private String initialDescription;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private OnEventSavedListener listener;

    public interface OnEventSavedListener {
        void onEventSaved(CalendarDay date, String title, String description, @Nullable String eventId);
    }

    public static EventDialogFragment newInstance(CalendarDay date) {
        EventDialogFragment fragment = new EventDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    public static EventDialogFragment newInstance(CalendarDay date, String eventId, String title, String description) {
        EventDialogFragment fragment = new EventDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATE, date);
        args.putString(ARG_ID, eventId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, description);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnEventSavedListener(OnEventSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDate = getArguments().getParcelable(ARG_DATE);
            eventId = getArguments().getString(ARG_ID);
            initialTitle = getArguments().getString(ARG_TITLE);
            initialDescription = getArguments().getString(ARG_DESC);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_event, container, false);

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        editTextTitle = view.findViewById(R.id.editTextEventTitle);
        editTextDescription = view.findViewById(R.id.editTextEventDescription);
        Button buttonSave = view.findViewById(R.id.buttonSave);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        if (initialTitle != null) {
            textViewTitle.setText("Editar Evento");
            editTextTitle.setText(initialTitle);
            editTextDescription.setText(initialDescription);
        } else {
            textViewTitle.setText("Nuevo Evento para " + selectedDate.getDay() + "/" + (selectedDate.getMonth() + 1));
        }

        buttonCancel.setOnClickListener(v -> dismiss());

        buttonSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (title.isEmpty()) {
                editTextTitle.setError("El título es obligatorio");
                return;
            }

            if (listener != null) {
                listener.onEventSaved(selectedDate, title, description, eventId);
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
