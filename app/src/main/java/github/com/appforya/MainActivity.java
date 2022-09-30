package github.com.appforya;

import static github.com.appforya.R.drawable.image_background;
import static github.com.appforya.R.drawable.mainlogo_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //интерфейс
    private LinearLayout startMenu, buttonsLayout, settingsLayout;
    private ConstraintLayout addButtonLayout, mainScreenLayout, subjectLayout;
    private EditText inputName, inputDescription;
    private TextView nameSubject, descriptionSubject;


    //листы информации
    private ArrayList<Button> mainButtonList = new ArrayList<Button>();
    private ArrayList<String> descriptionsList = new ArrayList<>();
    private ArrayList<String> namesButtonsList = new ArrayList<>();
    private ArrayList<Button> deleteButtonList = new ArrayList<Button>();

    //another points
    private String name;
    private String description;

    //images
    private ImageView logo;

    //переменный БД
    private final String LOG_TAG = "myLogs";
    private DBHelper dbHelper;
    private ContentValues contentValues;
    private SQLiteDatabase database;


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//layouts
        mainScreenLayout = findViewById(R.id.mainScreen);

        startMenu = findViewById(R.id.startMenu);
        startMenu.setVisibility(LinearLayout.VISIBLE);
        buttonsLayout = findViewById(R.id.buttonsLayout);
        buttonsLayout.setVisibility(LinearLayout.GONE);
        settingsLayout = findViewById(R.id.settingsLayout);
        addButtonLayout = findViewById(R.id.addButtonLayout);
        subjectLayout = findViewById(R.id.subjectLayout);
//images
        logo=findViewById(R.id.imageLogo);
        logo.setImageResource(mainlogo_2);

//text attributes
        inputName = findViewById(R.id.inputNameOfSubject);
        inputDescription = findViewById(R.id.inputDescriptionOfSubject);
        nameSubject = findViewById(R.id.subjectNameView);
        descriptionSubject = findViewById(R.id.subjectDescriptionView);

//data base operation
        dbHelper = new DBHelper(this);
        contentValues = new ContentValues();
        database = dbHelper.getWritableDatabase();
//load info
        loadButtons();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void startAction(View view) {
        buttonsLayout.setVisibility(LinearLayout.VISIBLE);
        startMenu.setVisibility(LinearLayout.GONE);
    }

    public void settingsAction(View view) {
        settingsLayout.setVisibility(LinearLayout.VISIBLE);
        startMenu.setVisibility(LinearLayout.GONE);
    }

   public void deleteButtonAction(View view) {
        buttonsLayout.removeAllViews();
        settingsLayout.setVisibility(LinearLayout.GONE);

       for(int i=0;i<mainButtonList.size();i++){
           Button button = new Button(this);
           button.setText(mainButtonList.get(i).getText());
           deleteButtonList.add(button);

           button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  mainButtonList.remove(mainButtonList.get(deleteButtonList.indexOf(button)));
                  descriptionsList.remove(descriptionsList.get(deleteButtonList.indexOf(button)));
                  namesButtonsList.remove(descriptionsList.get(deleteButtonList.indexOf(button)));
                  deleteButtonList.remove(button);
                   buttonsLayout.removeView(button);

               }
           });

       }
       for(int i=0;i<deleteButtonList.size();i++){
           buttonsLayout.addView(deleteButtonList.get(i));
       }

       Button saveDellButton=new Button(this);
       saveDellButton.setText("Save this");
       saveDellButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               database.delete(DBHelper.TABLE_BUTTONS,null,null);
               for(int i=0;i<mainButtonList.size();i++){
                   contentValues.put(DBHelper.KEY_NAME, namesButtonsList.get(i));
                   contentValues.put(DBHelper.KEY_DESCRIPTION, descriptionsList.get(i));
                   database.insert(DBHelper.TABLE_BUTTONS, null, contentValues);
                   buttonsLayout.addView(mainButtonList.get(i));

               }
               buttonsLayout.removeView(saveDellButton);
               deleteButtonList.clear();
               buttonsLayout.setVisibility(LinearLayout.GONE);
               settingsLayout.setVisibility(LinearLayout.VISIBLE);
           }
       });
       buttonsLayout.addView(saveDellButton);
       buttonsLayout.setVisibility(LinearLayout.VISIBLE);




    }

    public void exitAction(View view) {
        dbHelper.close();
        System.exit(0);
    }

    public void backAction(View view) {
        buttonsLayout.setVisibility(LinearLayout.GONE);
        subjectLayout.setVisibility(ConstraintLayout.GONE);
        startMenu.setVisibility(LinearLayout.VISIBLE);
    }

    public void addButtonAction(View view) {
        addButtonLayout.setVisibility(ConstraintLayout.VISIBLE);
    }

    public void saveAllSettingsAction(View view) {
        buttonsLayout.removeAllViews();
        for (int i = 0; i < mainButtonList.size(); i++) {
            buttonsLayout.addView(mainButtonList.get(i));


        }
        settingsLayout.setVisibility(LinearLayout.GONE);
        addButtonLayout.setVisibility(ConstraintLayout.GONE);
        startMenu.setVisibility(LinearLayout.VISIBLE);
    }

    public void saveThisButtonAction(View view) {
        //input new data
        name = inputName.getText().toString();
        description = inputDescription.getText().toString();
        namesButtonsList.add(name);
        descriptionsList.add(description);

        //input new data in database
        contentValues.put(DBHelper.KEY_NAME, name);
        contentValues.put(DBHelper.KEY_DESCRIPTION, description);

        database.insert(DBHelper.TABLE_BUTTONS, null, contentValues);

        createMainButton();
        inputDescription.setText("Input new description");
        inputName.setText("Input new name");
        addButtonLayout.setVisibility(ConstraintLayout.GONE);

    }

    private void showSubject(View view) {
        addButtonLayout.setVisibility(ConstraintLayout.VISIBLE);
    }

    private void createMainButton() {
        //настройка кнопок
        Button button = new Button(this);
        button.setText(name);
        mainButtonList.add(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameSubject.setText(button.getText());
                nameSubject.setTextColor(Color.BLACK);
                descriptionSubject.setText(descriptionsList.get(mainButtonList.indexOf(button)));
                descriptionSubject.setTextColor(Color.BLACK);
                subjectLayout.setVisibility(ConstraintLayout.VISIBLE);

            }
        });

    }

    private void loadButtons() {
        Cursor cursor = database.query(DBHelper.TABLE_BUTTONS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION);
            do {
                name = cursor.getString(nameIndex);
                description = cursor.getString(descriptionIndex);
                if (!namesButtonsList.contains(name) && !descriptionsList.contains(description)) {
                    namesButtonsList.add(name);
                    descriptionsList.add(description);
                    createMainButton();
                }

            } while (cursor.moveToNext());

        }
        cursor.close();
        for (int i = 0; i < mainButtonList.size(); i++) {
            buttonsLayout.addView(mainButtonList.get(i));
        }

    }



}