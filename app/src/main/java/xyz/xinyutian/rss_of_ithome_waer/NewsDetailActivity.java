package xyz.xinyutian.rss_of_ithome_waer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView title;
    private TextView content;

    private final static Logger logger = Logger.getLogger("NewsDetailActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        getNews(id);
    }


    Handler handler=new Handler(new Handler.Callback() {
        int position;
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Map<String,String> info=JSON.parseObject(msg.obj.toString(),Map.class);
                    title=findViewById(R.id.con_title);
                    content=findViewById(R.id.con_detail);
                    String con=RemoveImg.removeit(info.get("content"));
                    Spanned text = Html.fromHtml(con, imgGetter, null);
                    title.setText(info.get("title"));
                    content.setText(text);
                    break;
            }
            return true;
        }
    });

    public void getNews(final String id){
        new Thread(() -> {
            String rss="http://it.androideveloper.club/api/item?id="+id;
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(rss)
                        .get()
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        logger.log(Level.WARNING,e.getCause().getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message=new Message();
                        message.obj=response.body().string();
                        message.what=1;
                        handler.sendMessage(message);
                    }
                });
            } catch (Exception e) {
                logger.log(Level.WARNING,e.getMessage());
            }
        }).start();
    }

    Html.ImageGetter imgGetter = source -> {
        Drawable drawable;
        URL url;
        try {
            url = new URL(source);
            drawable = Drawable.createFromStream(url.openStream(), "");
        } catch (Exception e) {
            return null;
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                .getIntrinsicHeight());
        return drawable;
    };

}
