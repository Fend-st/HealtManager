package FernandoDiaz;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmanager.MainActivity;
import com.example.healthmanager.R;
import com.example.healthmanager.ResumenActividadActivity;
import FernandoDiaz.crono.Cronometro;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_calendar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_calendar) {
                return true;
            } else if (itemId == R.id.nav_timer) {
                startActivity(new Intent(this, Cronometro.class));
                return true;
            } else if (itemId == R.id.nav_summary) {
                startActivity(new Intent(this, ResumenActividadActivity.class));
                return true;
            }
            return false;
        });
    }
}