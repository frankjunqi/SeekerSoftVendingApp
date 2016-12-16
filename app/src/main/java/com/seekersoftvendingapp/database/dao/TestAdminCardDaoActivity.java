package com.seekersoftvendingapp.database.dao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.seekersoftvendingapp.BaseActivity;
import com.seekersoftvendingapp.R;
import com.seekersoftvendingapp.SeekersoftApp;
import com.seekersoftvendingapp.database.AdminCardsAdapter;
import com.seekersoftvendingapp.database.table.AdminCard;
import com.seekersoftvendingapp.database.table.AdminCardDao;
import com.seekersoftvendingapp.database.table.DaoSession;

import org.greenrobot.greendao.query.Query;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kjh08490 on 2016/11/18.
 */

public class TestAdminCardDaoActivity extends BaseActivity {

    private EditText editText;
    private View addNoteButton;

    private AdminCardDao adminCardDao;
    private Query<AdminCard> adminCardQuery;
    private AdminCardsAdapter adminCardsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_dbadmincard);

        setUpViews();

        // get the note DAO
        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();

        // get the admincard DAO
        adminCardDao = daoSession.getAdminCardDao();

        // query all admincards
        adminCardQuery = adminCardDao.queryBuilder().build();
        updateAdminCards();
    }

    private void updateAdminCards() {
        List<AdminCard> adminCards = adminCardQuery.list();
        adminCardsAdapter.setAdminCards(adminCards);
    }

    protected void setUpViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNotes);
        //noinspection ConstantConditions
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adminCardsAdapter = new AdminCardsAdapter(noteClickListener);
        recyclerView.setAdapter(adminCardsAdapter);

        addNoteButton = findViewById(R.id.buttonAdd);
        //noinspection ConstantConditions
        addNoteButton.setEnabled(false);

        editText = (EditText) findViewById(R.id.editTextNote);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addAdminCard();
                    return true;
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                addNoteButton.setEnabled(enable);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void onAddButtonClick(View view) {
        addAdminCard();
    }

    private void addAdminCard() {
        String noteText = editText.getText().toString();
        editText.setText("");

        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());

        AdminCard adminCard = new AdminCard( false, noteText.toString(), "1212", new Date(), new Date());
        adminCardDao.insert(adminCard);
        Log.d("DaoExample", "Inserted new note, ID: " + adminCard.getObjectId());

        updateAdminCards();
    }

    AdminCardsAdapter.AdminCardClickListener noteClickListener = new AdminCardsAdapter.AdminCardClickListener() {
        @Override
        public void onAdminCardClick(int position) {
            AdminCard note = adminCardsAdapter.getAdminCard(position);
            String objectId = note.getObjectId();

            adminCardDao.deleteByKey(objectId);
            Log.d("DaoExample", "Deleted note, ID: " + objectId);

            updateAdminCards();
        }
    };

}
