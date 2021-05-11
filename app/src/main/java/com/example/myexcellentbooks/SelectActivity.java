package com.example.myexcellentbooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.kursx.parser.fb2.FictionBook;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<HashMap<String, Object>> myBooks = new ArrayList<HashMap<String, Object>>();
    private HashMap<String, Object> hm;
    private boolean flagDelete = false;

    private ListView listView;
    private ConstraintLayout constraintLayout;
    private FloatingActionButton delete_fab, add_fab, refresh_fub;
    private Context context;
    private SimpleAdapter adapter;
    private ImageView imageView;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor sharedPreferencesEditor;

    /* Программные функции */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // делаем полноэкранное
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.select_activity);
        init();
        startBackground();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (flagDelete) {
                    ArrayList<String> strBook = new ArrayList<>();
                    sharedPreferencesEditor.remove(Integer.toString(position));
                    sharedPreferencesEditor.apply();
                    Map<String, ?> keyMap = sharedPreferences.getAll();
                    for (Map.Entry<String, ?> entry : keyMap.entrySet()) {
                        strBook.add((String) entry.getValue());
                    }
                    sharedPreferencesEditor.clear().apply();
                    for (int i = 0; i < strBook.size(); i++) {
                        sharedPreferencesEditor.putString(Integer.toString(i), strBook.get(i));
                        sharedPreferencesEditor.apply();
                    }
                    flagDelete = false;
                    onResume();
                }
                else {
                    Intent intent = new Intent(context, Convertor.class);
                    intent.putExtra("ArrayNum", Integer.toString(position));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_fub:
                showFileChooser();
                break;
            case R.id.del_fub:
                if (sharedPreferences.getAll().size() > 0) {
                    if (!flagDelete){
                        deleteBackground();
                        flagDelete = true;
                    }
                    else {
                        startBackground();
                        flagDelete = false;
                    }
                }
                else {
                    setMassage("There is empty, you dont have a books");
                }
                break;
            case R.id.refresh_fub:
                sharedPreferencesEditor.clear().apply();
                onResume();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // ссылка в системе на книгу
        assert data != null; // если data не null, то функция работает
        Uri uri = data.getData(); // получает ссылку после выбора
        String path = uri.getSchemeSpecificPart(); // вырезает главную, рабочую часть из ссылки
        uri = Uri.parse(path); // вот это уже ссылка преобразуется в нормальлный вид
        path = uri.getSchemeSpecificPart(); // uri это обработчик ссылок, он вырезает из любых ссылку желаемую тебе часть

        String real_path = Environment.getExternalStorageDirectory() + "/" + path; // конечная ссылка
        System.out.println("------------" + real_path + "------------");

        try { // тут работаешь с ссылками, у меня книга правда, но работает и с image, сто пудов, отвечаю
            FictionBook myBook = new FictionBook(new File(real_path));
            saveObjectToSharedPreference(parseOfBook(myBook));
        } catch (Exception e) {
            setMassage("You choice wrong file or you don't have any permission to open your file!");
            setMassage("To give storage permission for app, please go to setting > apps > 'Excellent Books' > give storage permission", "long");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getAll() != null) {
            myBooks.clear();
            adapter = new SimpleAdapter(context,
                    myBooks,
                    R.layout.list, new String[]{
                    constants.BOOKKEY,         //верхний текст
                    constants.PRICEKEY,        //нижний текст
                    constants.IMGKEY          //наша картинка
            }, new int[]{
                    R.id.text1, //ссылка на объект отображающий текст
                    R.id.text2, //ссылка на объект отображающий текст
                    R.id.img}); //добавили ссылку в чем отображать картинки из list.xml

            for (int i = 0; i < sharedPreferences.getAll().size(); i++) {
                try {
                    final Gson gson = new Gson();
                    ArrayList<String> myBook = gson.fromJson(sharedPreferences.getString(Integer.toString(i), ""), ArrayList.class);
                    Bitmap niceCover = fromBase64(myBook.get(myBook.size() - 1));
                    hm = new HashMap<String, Object>();
                    hm.put(constants.BOOKKEY, myBook.get(0));
                    hm.put(constants.PRICEKEY, '"' + myBook.get(1) + '"');
                    if ((myBook.get(myBook.size() - 1)).equals("fb2")){
                        hm.put(constants.IMGKEY, R.drawable.ic_fb2); //тут мы её добавляем для отображения
                    }
                    myBooks.add(hm);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            adapter = new SimpleAdapter(context,
                    myBooks,
                    R.layout.list, new String[]{
                    constants.BOOKKEY,         //верхний текст
                    constants.PRICEKEY,        //нижний текст
                    constants.IMGKEY          //наша картинка
            }, new int[]{
                    R.id.text1, //ссылка на объект отображающий текст
                    R.id.text2, //ссылка на объект отображающий текст
                    R.id.img}); //добавили ссылку в чем отображать картинки из list.xml

            listView.setAdapter(adapter);
            registerForContextMenu(listView);
            startBackground();
            flagDelete = false;
        }
    }

    /* Мои функции */

    public Bitmap fromBase64(String image) {
        // Декодируем строку Base64 в массив байтов
        byte[] imageAsBytes = Base64.decode(image.getBytes(), 0);

        // Декодируем массив байтов в изображение
        Bitmap decodedByte = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

        return decodedByte;
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // возвращает интент
        intent.setType("*/*"); // задает каталог в котором появляется пользователь
        intent.addCategory(Intent.CATEGORY_OPENABLE); // задается открытие менеджера (файлового ок да)


        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select fb2 file"), // для андроида 10 < при открытии будет надпись в header
                    constants.FILE_SELECT_CODE);  // открывает менеджер
        } catch (android.content.ActivityNotFoundException ex) { // ошибка открытия менеджера
            // Potentially direct the user to the Market with a Dialog
            setMassage("Please install a File Manager"); // функция setMassage() делает Toast
        }
    }

    public void saveObjectToSharedPreference(ArrayList<String> array) {
        boolean flag = false;
        final Gson gson = new Gson();
        String completeArray = gson.toJson(array);
        for (int i = 0; i < sharedPreferences.getAll().size(); i++) {
            String check = sharedPreferences.getString(Integer.toString(i), "");
            if (check == completeArray) {
                flag = true;
            }
        }
        if (!flag) {
            if (sharedPreferences.getAll() == null) {
                sharedPreferencesEditor.putString("0", completeArray);
            } else {
                sharedPreferencesEditor.putString(Integer.toString(sharedPreferences.getAll().size()), completeArray);
            }
            sharedPreferencesEditor.apply();
            setMassage("Book has been added!");
        } else {
            setMassage("This book is already in list!");
        }
    }

    public ArrayList<String> parseOfBook(Object myBook) {
        ArrayList<String> myText = new ArrayList<>();
        if (myBook instanceof FictionBook) {
            myText.add(((FictionBook) myBook).getDescription().getTitleInfo().getAuthors().get((((FictionBook) myBook).getDescription().getTitleInfo().getAuthors().size()) - 1).getFullName());
            myText.add(((FictionBook) myBook).getDescription().getTitleInfo().getBookTitle());

            /* Получаем действующих лиц */
            for (int actors = 0; actors < ((FictionBook) myBook).getBody().getSections().get(1).getSections().get(0).getElements().size(); actors++) {
                myText.add(((FictionBook) myBook).getBody().getSections().get(1).getSections().get(0).getElements().get(actors).getText());
            }

            for (int i = 1; i < ((FictionBook) myBook).getBody().getSections().get(1).getSections().size(); i++) {
                /* Получаю номер действия */
                myText.add(((FictionBook) myBook).getBody().getSections().get(1).getSections().get(i).getTitles().get(0).getParagraphs().get(0).getText());
                /* Получаю явление */
                for (int j = 0; j < ((FictionBook) myBook).getBody().getSections().get(1).getSections().get(i).getSections().size(); j++) {
                    /* Получаю номер явления */
                    try { // если до явления нету описания
                        myText.add(((FictionBook) myBook).getBody().getSections().get(1).getSections().get(i).getSections().get(j).getTitles().get(0).getParagraphs().get(0).getText());
                    } catch (Exception e) { // если есть
                        for (int description = 0; description < ((FictionBook) myBook).getBody().getSections().get(1).getSections().get(i).getSections().get(j).getElements().size(); description++) {
                            myText.add(((FictionBook) myBook).getBody().getSections().get(1).getSections().get(i).getSections().get(j).getElements().get(description).getText());
                        }
                    }
                    /* Основной текст */
                    for (int text = 0; text < ((FictionBook) myBook).getBody().getSections().get(1).getSections().get(i).getSections().get(j).getElements().size(); text++) {
                        myText.add(((FictionBook) myBook).getBody().getSections().get(1).getSections().get(i).getSections().get(j).getElements().get(text).getText());
                    }
                }
            }
            myText.add("fb2");
        }
        else {
            setMassage("Please, chose a normal format!");
        }
        return myText;
    }

    private void init() {
        context = this.getApplicationContext();

        constraintLayout = (ConstraintLayout) findViewById(R.id.layout);


        listView = (ListView) findViewById(R.id.list);
        delete_fab = (FloatingActionButton) findViewById(R.id.del_fub);
        delete_fab.setOnClickListener(this);

        add_fab = (FloatingActionButton) findViewById(R.id.add_fub);
        add_fab.setOnClickListener(this);

        refresh_fub = (FloatingActionButton) findViewById(R.id.refresh_fub);
        refresh_fub.setOnClickListener(this);

        sharedPreferences = context.getSharedPreferences("MyBooks", 0);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    public String deleteCharacters(String str, int from, int to) {
        return str.substring(0, from) + str.substring(to);
    }

    public void setMassage(String reason) {
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }
    public void setMassage(String reason, String how) {
        Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
    }

    private void startBackground() {
        constraintLayout.setBackgroundResource(R.drawable.gradient_list);
        ConstraintLayout constraintLayout_new = findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout_new.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    private void deleteBackground() {
        constraintLayout.setBackgroundResource(R.drawable.gradient_delete);
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

}
