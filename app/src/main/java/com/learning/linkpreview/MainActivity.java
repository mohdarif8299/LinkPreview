package com.learning.linkpreview;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Button upload;
    TextView uploadText;
    Button submit;
    ConstraintLayout preview;
    LinearLayout preview_view;
    ArrayList<String> links;
    private Bitmap[] currentImageSet;
    TextCrawler textCrawler;
    ImageView close, linkClose;
    TextView title, linkTitle, linkDesc;
    String previewImageURL;
    String previewTitle;
    String previewDesc;
    String previewUrl;
    ImageView linkImg;
    boolean isNextEnabled = true;
    RichLinkView richLinkView;
    boolean previewPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.desc);
        upload = findViewById(R.id.upload);
        richLinkView = (RichLinkView) findViewById(R.id.richLinkView);
        uploadText = findViewById(R.id.textView8);
        submit = findViewById(R.id.button);
        preview_view = findViewById(R.id.preview_view);
        preview = findViewById(R.id.preview);
        //linkImg = findViewById(R.id.contentImg);
        linkClose = findViewById(R.id.close);
        linkTitle = findViewById(R.id.title);
        //linkDesc = findViewById(R.id.linkDesc);
        links = new ArrayList<>();
        textCrawler = new TextCrawler();
        linkClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linkImg.setImageResource(android.R.color.transparent);
                linkTitle.setText("");
                linkDesc.setText("");
                previewImageURL = "";
                previewDesc = "";
                previewTitle = "";
                previewUrl = "";
                previewPresent = false;
                linkClose.setVisibility(View.GONE);
                preview.setVisibility(View.GONE);
                upload.setVisibility(View.VISIBLE);
                uploadText.setVisibility(View.VISIBLE);

            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.equals("")) {
                        preview_view.setVisibility(View.GONE);
                        preview.setVisibility(View.GONE);
                        upload.setVisibility(View.VISIBLE);
                        uploadText.setVisibility(View.VISIBLE);
                        isNextEnabled = true;
                        submit.setBackground(getDrawable(R.drawable.revidlybutton1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    preview_view.setVisibility(View.GONE);
                    preview.setVisibility(View.GONE);
                    richLinkView.setVisibility(View.GONE);
                    upload.setVisibility(View.VISIBLE);
                    uploadText.setVisibility(View.VISIBLE);
                    isNextEnabled = true;
                    submit.setBackground(getDrawable(R.drawable.revidlybutton1));
                } else {
                    if (upload.isClickable()) {
                        pullLinks(s.toString());
                        if (links.size() > 0) {
                            try {
                                preview_view.setVisibility(View.VISIBLE);
                                isNextEnabled = false;
                                upload.setVisibility(View.GONE);
                                uploadText.setVisibility(View.GONE);
                                submit.setBackground(getDrawable(R.drawable.revidlybutton1_disabled));
                                richLinkView.setLink(links.get(0), new ViewListener() {
                                    @Override
                                    public void onSuccess(boolean status) {
                                        Log.d("Preview Response", status + "");
                                        if (status) {
                                            preview.setVisibility(View.VISIBLE);
                                            preview_view.setVisibility(View.GONE);
                                            isNextEnabled = true;
                                            uploadText.setVisibility(View.GONE);
                                            upload.setVisibility(View.GONE);
                                            submit.setBackground(getDrawable(R.drawable.revidlybutton1));
                                        }
                                        else{
                                            preview_view.setVisibility(View.GONE);
                                            isNextEnabled = true;
                                            richLinkView.setVisibility(View.GONE);
                                            upload.setVisibility(View.VISIBLE);
                                            uploadText.setVisibility(View.VISIBLE);
                                            submit.setBackground(getDrawable(R.drawable.revidlybutton1));
                                            preview.setVisibility(View.GONE);
                                        }
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        try {
                                            preview_view.setVisibility(View.GONE);
                                            isNextEnabled = true;
                                            richLinkView.setVisibility(View.GONE);
                                            upload.setVisibility(View.VISIBLE);
                                            uploadText.setVisibility(View.VISIBLE);
                                            submit.setBackground(getDrawable(R.drawable.revidlybutton1));
                                            preview.setVisibility(View.GONE);
                                            e.printStackTrace();
                                        } catch (Exception e1) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    preview_view.setVisibility(View.GONE);
                                                    isNextEnabled = true;
                                                    richLinkView.setVisibility(View.GONE);
                                                    upload.setVisibility(View.VISIBLE);
                                                    uploadText.setVisibility(View.VISIBLE);
                                                    submit.setBackground(getDrawable(R.drawable.revidlybutton1));
                                                    preview.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {

                        }
                    }
                }
            }
        });
    }

    private ArrayList pullLinks(String text) {
        Pattern p = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(text);
        links.clear();
        while (m.find()) {
            String urlStr = m.group();
            Log.d("Add_answer", urlStr);
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(0, urlStr);
            Log.d("links", links + "");
        }
        return links;
    }
}
