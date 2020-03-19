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
    boolean previewPresent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.desc);
        upload = findViewById(R.id.upload);
        uploadText = findViewById(R.id.textView8);
        submit = findViewById(R.id.button);
        preview_view = findViewById(R.id.preview_view);
        preview = findViewById(R.id.preview);
        linkImg = findViewById(R.id.contentImg);
        linkClose = findViewById(R.id.close);
        linkTitle = findViewById(R.id.title);
        linkDesc = findViewById(R.id.linkDesc);
        links= new ArrayList<>();
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
                if (s.equals("")) {
                    preview_view.setVisibility(View.GONE);
                    preview.setVisibility(View.GONE);
                    upload.setVisibility(View.VISIBLE);
                    uploadText.setVisibility(View.VISIBLE);
                    submit.setBackground(getDrawable(R.drawable.revidlybutton1));
                    return;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    preview_view.setVisibility(View.GONE);
                    preview.setVisibility(View.GONE);
                    upload.setVisibility(View.VISIBLE);
                    uploadText.setVisibility(View.VISIBLE);
                    submit.setBackground(getDrawable(R.drawable.revidlybutton1));
                }
                else {
                    if (upload.isClickable()) {
                        pullLinks(s.toString());
                        if (links.size() > 0) {
                            try {
                                textCrawler.makePreview(callback, links.get(0).toString());
                                new CountDownTimer(10000,1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        preview_view.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Unable to Fetch", Toast.LENGTH_SHORT).show();
                                    }
                                }.start();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }
    private ArrayList pullLinks(String text) {
        String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        //  Pattern p = Pattern.compile(regex);
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
        }
        return links;
    }
    private LinkPreviewCallback callback = new LinkPreviewCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPre() {
            //   if (!isPreviewLoading) {
            preview_view.setVisibility(View.VISIBLE);
            upload.setVisibility(View.GONE);
            uploadText.setVisibility(View.GONE);
            submit.setBackground(getDrawable(R.drawable.revidlybutton1_disabled));
            isNextEnabled = false;
//                isPreviewLoading = true;
//            }
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPos(final SourceContent sourceContent, boolean b) {
            Log.d("PreviewResult",b+"");
            if (b) {
                Log.d("LinksList",links+"");
                //  links.clear();
                Toast.makeText(MainActivity.this, "Unable to Fetch", Toast.LENGTH_SHORT).show();
                preview_view.setVisibility(View.GONE);
                preview.setVisibility(View.GONE);
                upload.setVisibility(View.VISIBLE);
                submit.setBackground(getDrawable(R.drawable.revidlybutton1));
                uploadText.setVisibility(View.VISIBLE);
            }
            else {
                Log.d("LinksList",links+"");
                if (sourceContent.getFinalUrl() == null) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    currentImageSet = new Bitmap[sourceContent.getImages().size()];
                    if (sourceContent.getTitle().equals("")) {
                        sourceContent.setTitle("No Title");
                    } else {
                        UrlImageViewHelper.setUrlDrawable(linkImg, sourceContent.getImages().get(0), new UrlImageViewCallback() {
                            @Override
                            public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                                if (loadedBitmap != null) {
                                  //  currentImage = loadedBitmap;
                                    currentImageSet[0] = loadedBitmap;
                                    Log.d("Add_answer", "inside if");
                                    Log.d("Add_answer", "ANS: " + currentImageSet);
                                    Log.d("Add_answer", "URL: " + url);
                                    previewImageURL = url;
                                    Log.d("Add_Answer", "link Image = " + linkImg + "Current Image = " + linkImg);
//                            imageView.setImageURI(Uri.parse(url));
//                            imageView.setImageBitmap(getBitmapFromURL(url));
                                    try {
                                        Glide.with(getApplicationContext())
                                                .load(new URL(url))
                                                .into(linkImg);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.d("Add_answer", "Outside if");
                                }
                            }
                        });
                        linkTitle.setText(sourceContent.getTitle());
                        Log.d("Add_answer", "linkTitle : sourceContent Title = " + sourceContent.getTitle());
              //          url = sourceContent.getUrl();
                        Log.d("Add_answer", "url = sourceContent.getUrl() = " + sourceContent.getUrl());
                        linkDesc.setText(sourceContent.getDescription());
                        Log.d("Add_answer", "linkDesc : sourceContent Desc= " + sourceContent.getDescription());
                        preview.setVisibility(View.VISIBLE);
                        linkClose.setVisibility(View.VISIBLE);
                        upload.setVisibility(View.GONE);
                        uploadText.setVisibility(View.GONE);
                        previewDesc = sourceContent.getDescription();
                        previewTitle = sourceContent.getTitle();
                        previewUrl = sourceContent.getUrl();
                        previewPresent = true;
                        preview_view.setVisibility(View.GONE);
                        submit.setBackground(getDrawable(R.drawable.revidlybutton1));

                        preview_view.setVisibility(View.GONE);
                        links.clear();
                    }
                }
            }
        }
    };
}
