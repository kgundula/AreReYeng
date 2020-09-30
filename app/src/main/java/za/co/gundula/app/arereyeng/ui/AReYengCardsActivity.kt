package za.co.gundula.app.arereyeng.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import za.co.gundula.app.arereyeng.R
import za.co.gundula.app.arereyeng.model.Agency
import za.co.gundula.app.arereyeng.utils.Constants

class AReYengCardsActivity : AppCompatActivity() {

    @BindView(R.id.toolbar)

    var toolbar: Toolbar? = null
    var agency: Agency? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_are_yeng_cards)
        ButterKnife.bind(this)
        val bundle = intent.extras
        if (bundle != null) {
            agency = bundle.getParcelable(Constants.agency_key)
        }
        toolbar!!.title = resources.getString(R.string.areyeng_cards)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}