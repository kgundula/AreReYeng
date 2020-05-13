package za.co.gundula.app.arereyeng.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.gundula.app.arereyeng.R;
import za.co.gundula.app.arereyeng.model.Agency;

import static za.co.gundula.app.arereyeng.utils.Constants.agency_key;

public class AReYengCardsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Agency agency = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_are_yeng_cards);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            agency = bundle.getParcelable(agency_key);
        }

        toolbar.setTitle(getResources().getString(R.string.areyeng_cards));
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
